# Level 2: Static Role-Based Access Control

Define role requirements per resource using `authorization.properties`. Simple to set up, no permission management UI needed.

---

## How It Works

1. Framework loads `authorization.properties` from classpath
2. On each request, matches resource path against patterns using `AntPathMatcher`
3. Checks if user has ANY of the required roles
4. If check passes, continues to AuthorizationModule (Level 1 or 3)

---

## Setup

### 1. Create authorization.properties

Place in `src/main/resources/` (or classpath root):

```properties
# Syntax: resource-pattern = role1,role2,role3

# Admin section - only ADMIN role
/admin/**=ADMIN

# Reports - ADMIN or MANAGER
/reports/**=ADMIN,MANAGER

# API - multiple roles
/api/orders/**=SALES,SUPPORT,ADMIN
/api/users/**=ADMIN

# Public areas - no restriction (omit or leave empty)
/public/**=
```

### 2. Pattern Matching

Uses Spring's `AntPathMatcher`:

| Pattern | Matches | Does NOT Match |
|---------|---------|----------------|
| `/admin/*` | `/admin/users`, `/admin/roles` | `/admin/users/edit` |
| `/admin/**` | `/admin/users`, `/admin/users/edit/123` | `/api/admin` |
| `/api/*/list` | `/api/users/list`, `/api/orders/list` | `/api/users/edit` |
| `/**/delete` | `/users/delete`, `/admin/users/delete` | `/users/edit` |

### 3. Implement Minimal AuthorizationDAO

Only `findUserRoles()` is required for Level 2:

```java
@Repository
public class SimpleAuthorizationDAO extends AbstractAuthorizationDAO {

    @Autowired
    private AppUserDAO userDAO;

    @Override
    public User findUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    @Override
    public Role[] findUserRoles(User user) {
        AppUser u = (AppUser) user;
        return u.getRoles().toArray(new Role[0]);
    }

    // Other methods use default empty implementations from AbstractAuthorizationDAO
}
```

### 4. Domain Entities

```java
@Entity
public class AppUser implements User {

    @Id @GeneratedValue
    private Integer id;

    private String username;
    private String password;

    @ManyToMany
    private Set<AppRole> roles = new HashSet<>();

    // User interface
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    // Getters/setters
    public Set<AppRole> getRoles() { return roles; }
}

@Entity
public class AppRole implements Role {

    @Id @GeneratedValue
    private Integer id;

    private String name;  // e.g., "ADMIN", "MANAGER", "USER"

    public String getName() { return name; }

    // Important: implement equals/hashCode based on name
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppRole)) return false;
        return name != null && name.equals(((AppRole) o).name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
```

### 5. Controller Configuration

Use Level 1 modules - the static check runs automatically before them:

```java
// Public area - no auth needed, no role check
@Controller(path = "/public/info",
            authorizationModule = HasAccessAuthorizationModule.class)
public class PublicController extends MultiActionController { }

// Requires login + ADMIN role (from properties)
@Controller(path = "/admin/dashboard",
            authorizationModule = RequiresAuthenticationAuthorizationModule.class)
public class AdminDashboardController extends MultiActionController { }

// Requires login + ADMIN or MANAGER role (from properties)
@Controller(path = "/reports/sales",
            authorizationModule = RequiresAuthenticationAuthorizationModule.class)
public class SalesReportController extends MultiActionController { }
```

---

## Role Matching

Roles are compared **case-insensitively**:

```properties
# In authorization.properties
/admin/**=ADMIN,Admin,admin   # All equivalent
```

```java
// User's role
AppRole role = new AppRole();
role.setName("Admin");  // Matches "ADMIN" in properties
```

---

## Multiple Patterns

A resource can match multiple patterns. All matching roles are combined:

```properties
/admin/**=ADMIN
/admin/reports/**=ADMIN,MANAGER
```

For `/admin/reports/sales`:
- Matches `/admin/**` → requires ADMIN
- Matches `/admin/reports/**` → requires ADMIN or MANAGER
- Combined: user needs ADMIN or MANAGER (OR logic)

---

## No Restriction

Resources without matching patterns are unrestricted (static check passes):

```properties
# Only /admin is restricted
/admin/**=ADMIN

# Everything else passes static check
# (but may still be restricted by AuthorizationModule)
```

---

## Combining with Level 1

Static check runs BEFORE the AuthorizationModule check:

```
1. Static check (authorization.properties)
   └─► Must have required role

2. Module check (HasAccess/RequiresAuth)
   └─► HasAccess: always pass
   └─► RequiresAuth: must be logged in
```

Example:

```properties
/admin/**=ADMIN
```

```java
@Controller(path = "/admin/settings",
            authorizationModule = RequiresAuthenticationAuthorizationModule.class)
```

User must:
1. Have ADMIN role (static check)
2. Be logged in (module check)

---

## Login Implementation

```java
@Controller(path = "/login",
            authorizationModule = HasAccessAuthorizationModule.class)
public class LoginController extends MultiActionController {

    @Autowired
    private AuthorizationDAO authDAO;

    @DefaultAction
    public ModelAndView index() {
        return new ModelAndView("login");
    }

    public ModelAndView doLogin(String username, String password) {
        User user = authDAO.findUserByUsername(username);

        if (user != null && passwordMatches(password, user.getPassword())) {
            // Store in session - WebUserLocator will find it
            getSession().setAttribute("USER", user);
            return new ModelAndView("redirect:/dashboard");
        }

        addError("Invalid username or password");
        return new ModelAndView("login");
    }

    public ModelAndView logout() {
        getSession().removeAttribute("USER");
        getSession().invalidate();
        return new ModelAndView("redirect:/login");
    }
}
```

---

## Troubleshooting

### User always denied

1. Check role names match exactly (case-insensitive)
2. Verify `findUserRoles()` returns the user's roles
3. Check pattern matches the resource path

### Pattern not matching

Use `AntPathMatcher` to test:

```java
AntPathMatcher matcher = new AntPathMatcher();
boolean matches = matcher.match("/admin/**", "/admin/users/edit");
// true
```

### Debugging

Enable logging for authorization:

```properties
# log4j.properties
log4j.logger.org.nextframework.authorization=DEBUG
```
