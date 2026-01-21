# Level 3: Full Dynamic Authorization

Complete role-based access control with granular permissions stored in database. Supports CRUD operations, custom permission types, and permission management UI.

---

## How It Works

1. **Static check first** - authorization.properties (Level 2)
2. **Load permissions** - For each user role, load permissions from database
3. **Create authorization object** - AuthorizationModule parses permissions
4. **Check action** - Module decides based on action (list, create, delete, etc.)

---

## Setup

### 1. Domain Entities

```java
@Entity
public class AppUser implements User {

    @Id @GeneratedValue
    private Integer id;

    private String username;
    private String password;

    @ManyToMany
    private Set<AppRole> roles = new HashSet<>();

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Set<AppRole> getRoles() { return roles; }
}

@Entity
public class AppRole implements Role {

    @Id @GeneratedValue
    private Integer id;

    private String name;

    public String getName() { return name; }

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

@Entity
public class AppPermission extends AbstractPermission {

    @Id @GeneratedValue
    private Integer id;

    @ManyToOne
    private AppRole role;

    private String resource;      // e.g., "/admin/users"

    @Column(length = 1000)
    private String permissions;   // e.g., "create=true;read=true;update=false;delete=false"

    // Permission interface
    public Role getRole() { return role; }
    public String getControlName() { return resource; }
    public String getPermissions() { return permissions; }
    public void setPermissions(String p) { this.permissions = p; }

    // Setters
    public void setRole(AppRole role) { this.role = role; }
    public void setResource(String resource) { this.resource = resource; }
}
```

### 2. Full AuthorizationDAO

```java
@Repository
public class AuthorizationDAOImpl extends AbstractAuthorizationDAO {

    @Autowired
    private AppUserDAO userDAO;

    @Autowired
    private AppRoleDAO roleDAO;

    @Autowired
    private AppPermissionDAO permissionDAO;

    @Override
    public User findUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    @Override
    public Role[] findUserRoles(User user) {
        AppUser u = (AppUser) user;
        return u.getRoles().toArray(new Role[0]);
    }

    @Override
    public Permission findPermission(Role role, String resource) {
        return permissionDAO.findByRoleAndResource((AppRole) role, resource);
    }

    @Override
    public void savePermission(Role role, String resource, String permissions) {
        AppPermission p = permissionDAO.findByRoleAndResource((AppRole) role, resource);
        if (p == null) {
            p = new AppPermission();
            p.setRole((AppRole) role);
            p.setResource(resource);
        }
        p.setPermissions(permissions);
        permissionDAO.saveOrUpdate(p);
    }

    @Override
    public Role[] findAllRoles() {
        return roleDAO.findAll().toArray(new Role[0]);
    }

    @Override
    public long getLastUpdateTime(Role role) {
        // Return timestamp of last permission change for cache invalidation
        return permissionDAO.getLastUpdateTime((AppRole) role);
    }
}
```

### 3. Controller Configuration

```java
// CRUD controller - uses CrudAuthorizationModule by default
@Controller(path = "/admin/users")
public class UserController extends CrudController<AppUser> {
    // Permissions: CREATE, READ, UPDATE, DELETE
}

// Process controller
@Controller(path = "/processes/import",
            authorizationModule = ProcessAuthorizationModule.class)
public class ImportController extends MultiActionController {
    // Permission: EXECUTE
}

// Report controller
@Controller(path = "/reports/sales",
            authorizationModule = ReportAuthorizationModule.class)
public class SalesReportController extends ReportDesignController<...> {
    // Permission: GENERATE
}
```

---

## Built-in Authorization Modules

### CrudAuthorizationModule

Default for `CrudController`. Four permissions:

| Permission | Actions |
|------------|---------|
| READ | `list`, `view` |
| CREATE | `create` |
| UPDATE | `update` |
| DELETE | `delete` |
| CREATE or UPDATE | `save`, `form` |

Permission string format:
```
create=true;read=true;update=true;delete=false
```

### ProcessAuthorizationModule

Single permission: **EXECUTE**

```
execute=true
```

### ReportAuthorizationModule

Single permission: **GENERATE**

```
generate=true
```

---

## Permission Storage

Permissions are stored as key=value pairs separated by semicolons:

```
Database record:
┌─────────────────────────────────────────────────────┐
│ role_id: 1 (ADMIN)                                  │
│ resource: /admin/users                              │
│ permissions: create=true;read=true;update=true;delete=true │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│ role_id: 2 (MANAGER)                                │
│ resource: /admin/users                              │
│ permissions: create=false;read=true;update=false;delete=false │
└─────────────────────────────────────────────────────┘
```

`AbstractPermission.getPermissionMap()` parses this into a Map.

---

## Auto-Creation of Permissions

When a permission doesn't exist in the database, `WebPermissionLocator` auto-creates it with **most restrictive defaults**:

```java
// For CRUD: all permissions default to "false"
create=false;read=false;update=false;delete=false
```

This ensures new resources are locked down until explicitly granted.

---

## Permission Resolution

When user has multiple roles, permissions are **merged with OR logic**:

```
User roles: [ADMIN, MANAGER]

ADMIN permissions for /admin/users:
  create=true, read=true, update=true, delete=true

MANAGER permissions for /admin/users:
  create=false, read=true, update=false, delete=false

Effective permissions (OR):
  create=true, read=true, update=true, delete=true
```

---

## Caching

`WebPermissionLocator` caches in HTTP session:

- **User roles** - cached per user
- **Role permissions** - cached per role+resource combination
- **Cache invalidation** - via `getLastUpdateTime()` in DAO

To invalidate cache after permission change:

```java
// Update the timestamp in your DAO
public void savePermission(...) {
    // save permission
    this.lastUpdateTime = System.currentTimeMillis();
}

public long getLastUpdateTime(Role role) {
    return lastUpdateTime;
}
```

---

## Permission Management UI

Build a UI to manage permissions per role:

```java
@Controller(path = "/admin/permissions")
public class PermissionController extends MultiActionController {

    @Autowired
    private AuthorizationDAO authDAO;

    @Autowired
    private ResourceAuthorizationMapper mapper;

    @DefaultAction
    public ModelAndView index() {
        setAttribute("roles", authDAO.findAllRoles());
        setAttribute("resources", getProtectedResources());
        return new ModelAndView("permissions/list");
    }

    public ModelAndView edit(AppRole role, String resource) {
        Permission permission = authDAO.findPermission(role, resource);
        AuthorizationModule module = mapper.getAuthorizationModule(resource);

        setAttribute("role", role);
        setAttribute("resource", resource);
        setAttribute("permission", permission);
        setAttribute("items", module.getAuthorizationItens());  // Available permissions

        return new ModelAndView("permissions/edit");
    }

    public ModelAndView save(AppRole role, String resource, Map<String, String> permissions) {
        String permissionString = permissions.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining(";"));

        authDAO.savePermission(role, resource, permissionString);

        addMessage("Permissions saved");
        return new ModelAndView("redirect:/admin/permissions");
    }
}
```

---

## Combining with Static Authorization

Level 3 works together with Level 2:

```properties
# authorization.properties
/admin/**=ADMIN,MANAGER
```

```java
@Controller(path = "/admin/users")
public class UserController extends CrudController<AppUser> { }
```

Authorization flow:
1. **Static check**: User must have ADMIN or MANAGER role
2. **Dynamic check**: User's role must have READ permission to list, DELETE permission to delete, etc.

---

## Example: Complete Setup

### Database Schema

```sql
CREATE TABLE app_user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE app_role (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE app_user_roles (
    user_id INT,
    role_id INT,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE app_permission (
    id INT PRIMARY KEY AUTO_INCREMENT,
    role_id INT NOT NULL,
    resource VARCHAR(255) NOT NULL,
    permissions VARCHAR(1000),
    UNIQUE (role_id, resource)
);
```

### Sample Data

```sql
-- Roles
INSERT INTO app_role (id, name) VALUES (1, 'ADMIN'), (2, 'MANAGER'), (3, 'USER');

-- Users
INSERT INTO app_user (id, username, password) VALUES (1, 'admin', '...');
INSERT INTO app_user_roles (user_id, role_id) VALUES (1, 1);

-- Permissions
INSERT INTO app_permission (role_id, resource, permissions) VALUES
    (1, '/admin/users', 'create=true;read=true;update=true;delete=true'),
    (2, '/admin/users', 'create=false;read=true;update=false;delete=false'),
    (1, '/reports/sales', 'generate=true'),
    (2, '/reports/sales', 'generate=true');
```

### authorization.properties

```properties
/admin/**=ADMIN,MANAGER
/reports/**=ADMIN,MANAGER,ANALYST
```

### Controllers

```java
@Controller(path = "/admin/users")
public class UserController extends CrudController<AppUser> {
    // ADMIN: full CRUD
    // MANAGER: read only
}

@Controller(path = "/reports/sales",
            authorizationModule = ReportAuthorizationModule.class)
public class SalesReportController extends ReportDesignController<...> {
    // ADMIN, MANAGER: can generate
}
```
