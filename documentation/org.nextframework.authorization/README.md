# org.nextframework.authorization

## Overview

Security in Next Framework has two layers:

1. **Authentication** - Is the user logged in?
2. **Authorization** - Does the user have permission?

```
Request arrives
    │
    ▼
┌─────────────────────────────┐
│  AUTHENTICATION             │  ← authentication.properties
│  Is user logged in?         │     (module-level)
└─────────────────────────────┘
    │
    ▼
┌─────────────────────────────┐
│  AUTHORIZATION              │  ← authorization.properties + AuthorizationModule
│  Does user have permission? │     (resource/action-level)
└─────────────────────────────┘
```

---

## Module Authentication

Controllers are organized into **modules** (first path segment). You can require authentication for entire modules via `authentication.properties`.

### Setup

Create `authentication.properties` in your classpath:

```properties
# Modules requiring authentication (user must be logged in)
admin=true
app=true
reports=true

# Public modules (no authentication required)
public=false
```

### How It Works

1. Framework creates a servlet per module at startup
2. If module is marked `true`, `AuthenticationHandlerInterceptor` is added
3. Interceptor checks if user is logged in before any controller action
4. If not logged in → redirects to login page

### Login Controller

You need a login controller implementing `AuthenticationController`:

```java
@Controller(path = "/public/login",
            authorizationModule = HasAccessAuthorizationModule.class)
public class LoginController extends MultiActionController
                             implements AuthenticationController {

    @Override
    public String getPath() {
        return "/public/login";  // Must match @Controller path
    }

    @DefaultAction
    public ModelAndView index() {
        return new ModelAndView("login");
    }

    public ModelAndView doLogin(String username, String password) {
        User user = authDAO.findUserByUsername(username);

        if (user != null && checkPassword(password, user.getPassword())) {
            getSession().setAttribute("USER", user);
            return new ModelAndView("redirect:/app/dashboard");
        }

        addError("Invalid credentials");
        return new ModelAndView("login");
    }

    public ModelAndView logout() {
        getSession().removeAttribute("USER");
        getSession().invalidate();
        return new ModelAndView("redirect:/public/login");
    }
}
```

**Important:** Login controller must be in a **public module** (not marked as `true` in authentication.properties).

### Example Module Organization

```properties
# authentication.properties
public=false    # No authentication
app=true        # Requires login
admin=true      # Requires login
```

```
/public/login      ← Login page (public)
/public/about      ← Public pages

/app/dashboard     ← Requires authentication
/app/profile       ← Requires authentication

/admin/users       ← Requires authentication
/admin/settings    ← Requires authentication
```

---

## Authorization Levels

Once authenticated, **authorization** controls what the user can do. Three levels of complexity:

| Level | Description | Requires DAO? |
|-------|-------------|---------------|
| **Level 1** | Simple access control (public or authenticated) | No |
| **Level 2** | Static role-based rules via properties file | Minimal |
| **Level 3** | Full dynamic RBAC with database permissions | Yes |

---

## Level 1: Simple Access Control

No database, no roles - just control whether a resource is public or requires login.

### HasAccessAuthorizationModule

Allows unrestricted access:

```java
@Controller(path = "/public/info",
            authorizationModule = HasAccessAuthorizationModule.class)
public class PublicInfoController extends MultiActionController {
    // Anyone can access
}
```

### RequiresAuthenticationAuthorizationModule

Only requires user to be logged in:

```java
@Controller(path = "/profile",
            authorizationModule = RequiresAuthenticationAuthorizationModule.class)
public class ProfileController extends MultiActionController {
    // Any authenticated user can access
}
```

**No AuthorizationDAO needed.** The framework uses a fallback implementation.

---

## Level 2: Static Role-Based Access Control

Define which roles can access which resources using `authorization.properties`. The framework checks if the user has any of the required roles.

### Setup

1. Create `authorization.properties` in your classpath:

```properties
# Pattern = role1,role2,role3 (user needs ANY of these roles)
/admin/**=ADMIN
/reports/**=ADMIN,MANAGER
/api/**=USER,ADMIN
```

Supports Ant-style patterns:
- `/admin/*` - matches single path segment
- `/admin/**` - matches multiple segments

2. Implement minimal AuthorizationDAO (only `findUserRoles` needed):

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
}
```

3. Use any authorization module - the static check runs first:

```java
@Controller(path = "/admin/dashboard",
            authorizationModule = RequiresAuthenticationAuthorizationModule.class)
public class AdminDashboardController extends MultiActionController {
    // Must be authenticated AND have ADMIN role (from properties)
}
```

**See [LEVEL2-STATIC.md](LEVEL2-STATIC.md) for complete setup.**

---

## Level 3: Full Dynamic Authorization

Complete RBAC with permissions stored in database. Define granular permissions (create, read, update, delete) per role per resource.

### How It Works

1. **Static check runs first** (authorization.properties)
2. **Then dynamic check** - loads permissions from database for each role
3. **AuthorizationModule decides** based on action and permissions

### Built-in Modules

| Module | Permissions | Use Case |
|--------|-------------|----------|
| `CrudAuthorizationModule` | CREATE, READ, UPDATE, DELETE | Data entry screens |
| `ProcessAuthorizationModule` | EXECUTE | Background processes |
| `ReportAuthorizationModule` | GENERATE | Report generation |

### Setup

1. Implement domain entities:

```java
@Entity
public class AppUser implements User { ... }

@Entity
public class AppRole implements Role { ... }

@Entity
public class AppPermission extends AbstractPermission { ... }
```

2. Implement full AuthorizationDAO:

```java
@Repository
public class AuthorizationDAOImpl extends AbstractAuthorizationDAO {

    @Override
    public User findUserByUsername(String username) { ... }

    @Override
    public Role[] findUserRoles(User user) { ... }

    @Override
    public Permission findPermission(Role role, String resource) { ... }

    @Override
    public void savePermission(Role role, String resource, String permissions) { ... }

    @Override
    public Role[] findAllRoles() { ... }
}
```

3. Use CRUD or other dynamic modules:

```java
@Controller(path = "/admin/users")
public class UserController extends CrudController<AppUser> {
    // CrudAuthorizationModule is the default
    // Checks create/read/update/delete per role
}
```

**See [LEVEL3-DYNAMIC.md](LEVEL3-DYNAMIC.md) for complete setup.**

---

## Authorization Flow

```
Request arrives
    │
    ▼
┌─────────────────────────────────────┐
│  STATIC CHECK (authorization.properties)  │
│  Does user have required role?      │
└─────────────────────────────────────┘
    │
    ├─► No roles defined → Continue
    ├─► User has role → Continue
    └─► User lacks role → DENY
    │
    ▼
┌─────────────────────────────────────┐
│  DYNAMIC CHECK (AuthorizationModule)│
└─────────────────────────────────────┘
    │
    ├─► HasAccessAuthorizationModule → ALLOW
    │
    ├─► RequiresAuthenticationAuthorizationModule
    │       └─► User logged in? → ALLOW/DENY
    │
    └─► CrudAuthorizationModule (or other)
            │
            ├─► Load permissions from DB
            ├─► Check action (list→READ, delete→DELETE, etc.)
            └─► ALLOW/DENY based on permission
```

---

## Core Interfaces

### User

```java
public interface User extends Serializable {
    String getUsername();
    String getPassword();
}
```

### Role

```java
public interface Role extends Serializable {
    // Marker interface - implement equals/hashCode
}
```

### Permission

```java
public interface Permission {
    Role getRole();
    String getControlName();  // Resource path
    Map<String, String> getPermissionMap();
}
```

Use `AbstractPermission` for easy implementation - stores permissions as `key=value;key=value` string.

---

## Services

Access authorization services via `Authorization` utility:

```java
// Get current user
User user = Authorization.getUserLocator().getUser();

// Check authorization programmatically
AuthorizationManager manager = Authorization.getAuthorizationManager();
boolean allowed = manager.isAuthorized("/admin/users", "delete");

// Get DAO
AuthorizationDAO dao = Authorization.getAuthorizationDAO();
```

---

## Web Integration

### User Session

`WebUserLocator` retrieves user from HTTP session:

```java
// Store user on login
session.setAttribute("USER", user);

// Framework retrieves automatically
User user = Authorization.getUserLocator().getUser();
```

### Permission Caching

`WebPermissionLocator` caches permissions in session for performance:
- User roles cached per session
- Role permissions cached per resource
- Auto-invalidates when permissions change

---

## Files

- [LEVEL2-STATIC.md](LEVEL2-STATIC.md) - Static role-based setup
- [LEVEL3-DYNAMIC.md](LEVEL3-DYNAMIC.md) - Full dynamic RBAC setup
- [CUSTOM-MODULE.md](CUSTOM-MODULE.md) - Creating custom authorization modules

---

## Dependencies

- `org.nextframework.core` - ServiceFactory, utilities
- Servlet API (for web integration)
