# Creating Custom Authorization Modules

Build custom authorization logic beyond CRUD, Process, and Report modules.

---

## When to Create a Custom Module

- You need permissions beyond CREATE/READ/UPDATE/DELETE
- You have application-specific authorization rules
- You need multi-level access (e.g., access levels 1-5)
- You want to combine multiple permission types

---

## Module Structure

```java
public class CustomAuthorizationModule extends AuthorizationModuleSupport {

    // 1. Display name for permission management UI
    @Override
    public String getAuthorizationGroupName() {
        return "My Custom Authorization";
    }

    // 2. Define available permission items
    @Override
    public AuthorizationItem[] getAuthorizationItens() {
        return new AuthorizationItem[] { ... };
    }

    // 3. Parse permissions into authorization object
    @Override
    public UserAuthorization createAuthorization(Permission[] permissions) {
        // Parse and merge permissions from all user roles
        return new CustomAuthorization(...);
    }

    // 4. Check if action is allowed
    @Override
    public boolean isAuthorized(String action, User user, UserAuthorization auth) {
        // Return true/false based on action and permissions
    }
}
```

---

## Example: Document Authorization

A module with VIEW, EDIT, APPROVE, and ARCHIVE permissions:

### 1. Define Authorization Items

```java
public class DocumentAuthorizationModule extends AuthorizationModuleSupport {

    public static final String VIEW = "view";
    public static final String EDIT = "edit";
    public static final String APPROVE = "approve";
    public static final String ARCHIVE = "archive";

    @Override
    public String getAuthorizationGroupName() {
        return "Documents";
    }

    @Override
    public AuthorizationItem[] getAuthorizationItens() {
        return new AuthorizationItem[] {
            new AuthorizationItem(VIEW, "View",
                new String[] {"true", "false"},
                "Can view documents"),
            new AuthorizationItem(EDIT, "Edit",
                new String[] {"true", "false"},
                "Can edit documents"),
            new AuthorizationItem(APPROVE, "Approve",
                new String[] {"true", "false"},
                "Can approve documents"),
            new AuthorizationItem(ARCHIVE, "Archive",
                new String[] {"true", "false"},
                "Can archive documents")
        };
    }
}
```

### 2. Create Authorization Object

```java
public class DocumentAuthorization implements UserAuthorization {

    private boolean canView;
    private boolean canEdit;
    private boolean canApprove;
    private boolean canArchive;

    public DocumentAuthorization(boolean view, boolean edit,
                                  boolean approve, boolean archive) {
        this.canView = view;
        this.canEdit = edit;
        this.canApprove = approve;
        this.canArchive = archive;
    }

    public boolean canView() { return canView; }
    public boolean canEdit() { return canEdit; }
    public boolean canApprove() { return canApprove; }
    public boolean canArchive() { return canArchive; }
}
```

### 3. Parse Permissions

```java
@Override
public UserAuthorization createAuthorization(Permission[] permissions) {
    boolean canView = false;
    boolean canEdit = false;
    boolean canApprove = false;
    boolean canArchive = false;

    // Merge permissions from all roles (OR logic)
    for (Permission p : permissions) {
        if ("true".equals(p.getPermissionValue(VIEW))) canView = true;
        if ("true".equals(p.getPermissionValue(EDIT))) canEdit = true;
        if ("true".equals(p.getPermissionValue(APPROVE))) canApprove = true;
        if ("true".equals(p.getPermissionValue(ARCHIVE))) canArchive = true;
    }

    return new DocumentAuthorization(canView, canEdit, canApprove, canArchive);
}
```

### 4. Check Authorization

```java
@Override
public boolean isAuthorized(String action, User user, UserAuthorization auth) {
    DocumentAuthorization da = (DocumentAuthorization) auth;

    switch (action) {
        case "list":
        case "view":
            return da.canView();
        case "create":
        case "update":
        case "save":
            return da.canEdit();
        case "approve":
            return da.canApprove();
        case "archive":
            return da.canArchive();
        default:
            return da.canView();  // Default to view permission
    }
}
```

### 5. Use in Controller

```java
@Controller(path = "/documents",
            authorizationModule = DocumentAuthorizationModule.class)
public class DocumentController extends MultiActionController {

    public ModelAndView list() { ... }      // Requires VIEW
    public ModelAndView view() { ... }      // Requires VIEW
    public ModelAndView create() { ... }    // Requires EDIT
    public ModelAndView save() { ... }      // Requires EDIT
    public ModelAndView approve() { ... }   // Requires APPROVE
    public ModelAndView archive() { ... }   // Requires ARCHIVE
}
```

---

## Example: Access Level Authorization

A module with numeric access levels (1-5):

```java
public class AccessLevelAuthorizationModule extends AuthorizationModuleSupport {

    public static final String LEVEL = "level";

    @Override
    public String getAuthorizationGroupName() {
        return "Access Levels";
    }

    @Override
    public AuthorizationItem[] getAuthorizationItens() {
        return new AuthorizationItem[] {
            new AuthorizationItem(LEVEL, "Access Level",
                new String[] {"0", "1", "2", "3", "4", "5"},
                "User access level (0=none, 5=full)")
        };
    }

    @Override
    public UserAuthorization createAuthorization(Permission[] permissions) {
        int maxLevel = 0;

        // Take highest level from all roles
        for (Permission p : permissions) {
            String levelStr = p.getPermissionValue(LEVEL);
            if (levelStr != null) {
                int level = Integer.parseInt(levelStr);
                maxLevel = Math.max(maxLevel, level);
            }
        }

        return new AccessLevelAuthorization(maxLevel);
    }

    @Override
    public boolean isAuthorized(String action, User user, UserAuthorization auth) {
        AccessLevelAuthorization ala = (AccessLevelAuthorization) auth;
        int userLevel = ala.getLevel();

        // Define required levels per action
        switch (action) {
            case "list":
            case "view":
                return userLevel >= 1;
            case "create":
            case "update":
                return userLevel >= 3;
            case "delete":
                return userLevel >= 4;
            case "admin":
                return userLevel >= 5;
            default:
                return userLevel >= 1;
        }
    }
}

public class AccessLevelAuthorization implements UserAuthorization {
    private int level;

    public AccessLevelAuthorization(int level) {
        this.level = level;
    }

    public int getLevel() { return level; }
}
```

---

## Example: Combined Permissions

Mix boolean and level-based permissions:

```java
@Override
public AuthorizationItem[] getAuthorizationItens() {
    return new AuthorizationItem[] {
        // Boolean permissions
        new AuthorizationItem("read", "Read",
            new String[] {"true", "false"}, "Can read"),
        new AuthorizationItem("write", "Write",
            new String[] {"true", "false"}, "Can write"),

        // Level-based permission
        new AuthorizationItem("exportLevel", "Export Level",
            new String[] {"none", "csv", "excel", "all"},
            "Export capabilities"),

        // Numeric limit
        new AuthorizationItem("maxRecords", "Max Records",
            new String[] {"100", "1000", "10000", "unlimited"},
            "Maximum records to display")
    };
}
```

---

## Registering the Module

The module is used via `@Controller` annotation:

```java
@Controller(path = "/documents",
            authorizationModule = DocumentAuthorizationModule.class)
public class DocumentController { }
```

For programmatic mapping, implement `ResourceAuthorizationMapper`:

```java
public class CustomResourceAuthorizationMapper implements ResourceAuthorizationMapper {

    @Override
    public AuthorizationModule getAuthorizationModule(String resource) {
        if (resource.startsWith("/documents")) {
            return new DocumentAuthorizationModule();
        }
        if (resource.startsWith("/admin")) {
            return new CrudAuthorizationModule();
        }
        return new RequiresAuthenticationAuthorizationModule();
    }
}
```

---

## Best Practices

1. **Use constants** for permission IDs to avoid typos
2. **Document actions** - map controller actions to permissions clearly
3. **Default to restrictive** - when in doubt, deny access
4. **Consider inheritance** - extend existing modules when possible
5. **Keep it simple** - avoid overly complex permission schemes
