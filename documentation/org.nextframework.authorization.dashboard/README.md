# org.nextframework.authorization.dashboard

## Overview

Web-based administrative UI for managing authentication and authorization. Provides ready-to-use controllers for:

1. **Login/Logout** - User authentication via `LoginController`
2. **Permission Management** - Visual permission editor via `AuthorizationController`

This module provides the UI layer for Level 3 (Full Dynamic Authorization) setups where permissions are stored in database and managed through a web interface.

```
┌─────────────────────────────────────────────────────┐
│  LOGIN SCREEN                                       │
│  ┌─────────────────────────────────────────────┐    │
│  │ Username: [________________]                │    │
│  │ Password: [________________]                │    │
│  │                              [Login]        │    │
│  └─────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│  PERMISSION MANAGEMENT SCREEN                       │
│  Role: [Manager ▼]                                  │
│  ┌─────────────────────────────────────────────┐    │
│  │ CRUD Operations                             │    │
│  │ Screen          │Create │Read │Update │Delete │  │
│  │ /admin/users    │  ☑   │  ☑  │  ☑   │  ☐  │    │
│  │ /admin/products │  ☑   │  ☑  │  ☐   │  ☐  │    │
│  └─────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────┐    │
│  │ Reports                                     │    │
│  │ Screen          │Generate                   │    │
│  │ /reports/sales  │  ☑                        │    │
│  └─────────────────────────────────────────────┘    │
│                                        [Save]       │
└─────────────────────────────────────────────────────┘
```

---

## Quick Start

### 1. Create Login Controller

```java
@Controller(path = "/public/login",
            authorizationModule = HasAccessAuthorizationModule.class)
public class AppLoginController extends LoginController {

    @Override
    protected String afterLoginRedirectTo() {
        return "/app/dashboard";  // Redirect after successful login
    }

    @Override
    protected boolean validPassword(String persisted, String provided) {
        // Custom password validation (e.g., BCrypt)
        return BCrypt.checkpw(provided, persisted);
    }
}
```

### 2. Create Authorization Controller

```java
@Controller(path = "/admin/authorization")
public class AppAuthorizationController extends AuthorizationController {

    @Override
    protected ModelAndView getModelAndView() {
        return new ModelAndView("admin/authorization");  // Custom JSP path
    }
}
```

### 3. Copy JSP Views

Copy the sample authorization JSP to your application:

```
/WEB-INF/jsp/admin/authorization.jsp
```

The login JSP is auto-copied on first access.

---

## LoginController

Base controller for user authentication. Implements `AuthenticationController` interface.

### Actions

| Action | URL | Description |
|--------|-----|-------------|
| `doPage` (default) | `/public/login` | Display login form |
| `doLogin` | `/public/login?action=doLogin` | Authenticate user |

### How It Works

```
User submits username/password
        │
        ▼
┌─────────────────────────────┐
│  Find user by username      │
│  (AuthorizationDAO)         │
└─────────────────────────────┘
        │
        ▼
┌─────────────────────────────┐
│  Validate password          │
│  (validPassword method)     │
└─────────────────────────────┘
        │
        ├─► Valid → Set session attribute "USER" → Redirect
        │
        └─► Invalid → Show error message → Return to login
```

### Overridable Methods

```java
/**
 * Where to redirect after successful login.
 * Default: "/"
 */
@Override
protected String afterLoginRedirectTo() {
    return "/app/dashboard";
}

/**
 * Password validation logic.
 * Default: plain text comparison
 */
@Override
protected boolean validPassword(String passwordPersisted, String passwordProvided) {
    return BCrypt.checkpw(passwordProvided, passwordPersisted);
}
```

### Auto-Copy Login JSP

On first access, if no `login.jsp` exists in the module's JSP folder, the framework copies a default one from the dashboard resources:

```
Source: org/nextframework/authorization/login.jsp (in JAR)
Target: /WEB-INF/jsp/{module}/login.jsp
```

### Default Login JSP Structure

```jsp
<n:form>
    <n:bean name="user">
        <div class="loginPanel">
            <div class="pageTitle">Login</div>
            <t:property name="username" />
            <t:property name="password" type="password" />
            <n:submit action="doLogin">Login</n:submit>
        </div>
    </n:bean>
</n:form>
```

---

## AuthorizationController

Permission management UI for configuring role-based access to all controllers in the application.

### Actions

| Action | URL | Description |
|--------|-----|-------------|
| `list` (default) | `/admin/authorization` | Display permission editor |
| `salvar` | `/admin/authorization?action=salvar` | Save permission changes |

### How It Works

```
1. Load all @Controller classes
        │
        ▼
2. Group by AuthorizationModule type
   (CrudAuthorizationModule, ReportAuthorizationModule, etc.)
        │
        ▼
3. For each controller path:
   - Get AuthorizationItems (CREATE, READ, UPDATE, DELETE, etc.)
   - Load current Permission from database
   - Build UI filter objects
        │
        ▼
4. Display grouped permission checkboxes
        │
        ▼
5. On save:
   - Save each Permission via AuthorizationDAO
   - Clear permission cache
```

### Overridable Methods

```java
@Controller(path = "/admin/authorization")
public class AppAuthorizationController extends AuthorizationController {

    /**
     * Override to customize JSP path.
     * Default: "process/autorizacao"
     */
    @Override
    protected ModelAndView getModelAndView() {
        return new ModelAndView("admin/authorization");
    }

    /**
     * Override to customize path display in UI.
     */
    @Override
    protected String translatePath(String path) {
        // Example: "/admin/users" → "User Management"
        return pathTranslations.getOrDefault(path, path);
    }
}
```

### Filter Classes

**AuthorizationProcessFilter** - Main form backing object:

| Property | Type | Description |
|----------|------|-------------|
| `role` | Role | Selected role for editing |
| `groupAuthorizationMap` | Map<String, List<...>> | Grouped permissions by module |

**AuthorizationProcessItemFilter** - Single controller permission:

| Property | Type | Description |
|----------|------|-------------|
| `path` | String | Controller path (e.g., `/admin/users`) |
| `description` | String | Human-readable name |
| `authorizationModule` | AuthorizationModule | Module type |
| `permissionMap` | Map<String, String> | Permission values (e.g., `CREATE=true`) |

---

## Authorization JSP View

Create the authorization management view at your configured path (e.g., `/WEB-INF/jsp/admin/authorization.jsp`).

### Sample Structure

```jsp
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>

<t:view title="Authorization">

    <%-- Role selector --%>
    <t:filterPanel showSubmit="false">
        <t:property name="role" itens="${roles}" reloadOnChange="true" />
    </t:filterPanel>

    <%-- Permission groups --%>
    <c:forEach items="${filter.groupAuthorizationMap}" var="group">
        <t:listPanel>
            <h3>${group.key}</h3>
            <n:dataGrid itens="${group.value}">
                <n:bean name="row"
                        propertyPrefix="groupAuthorizationMap[${group.key}][${index}]"
                        valueType="${authorizationProcessItemFilterClass}">

                    <n:column header="Screen">
                        <t:property name="description" mode="output" />
                        <t:property name="path" type="hidden" />
                    </n:column>

                    <%-- Permission checkboxes --%>
                    <c:forEach items="${mapaGroupModule[group.key].authorizationItens}"
                               var="item">
                        <n:column header="${item.nome}">
                            <n:property name="permissionMap[${item.id}]">
                                <n:input type="checkbox" />
                            </n:property>
                        </n:column>
                    </c:forEach>

                </n:bean>
            </n:dataGrid>
        </t:listPanel>
    </c:forEach>

    <%-- Save button --%>
    <c:if test="${!empty filter.role}">
        <n:submit action="salvar">Save</n:submit>
    </c:if>

</t:view>
```

### Available Request Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `roles` | Role[] | All roles for dropdown |
| `filter` | AuthorizationProcessFilter | Form backing object |
| `mapaGroupModule` | Map<String, AuthorizationModule> | Module by group name |
| `authorizationProcessItemFilterClass` | Class | For bean binding |

---

## Complete Setup Example

### 1. Domain Entities

See [org.nextframework.authorization](../org.nextframework.authorization/LEVEL3-DYNAMIC.md) for entity setup.

### 2. AuthorizationDAO Implementation

```java
@Repository
public class AuthorizationDAOImpl extends AbstractAuthorizationDAO {

    @Autowired
    private GenericDAO dao;

    @Override
    public User findUserByUsername(String username) {
        return dao.findBy(AppUser.class, "username", username);
    }

    @Override
    public Role[] findUserRoles(User user) {
        AppUser appUser = (AppUser) user;
        return appUser.getRoles().toArray(new Role[0]);
    }

    @Override
    public Role[] findAllRoles() {
        return dao.findAll(AppRole.class).toArray(new Role[0]);
    }

    @Override
    public Permission findPermission(Role role, String controlName) {
        return dao.findBy(AppPermission.class,
            new String[]{"role", "controlName"},
            new Object[]{role, controlName});
    }

    @Override
    public Permission savePermission(String controlName, Role role,
                                      Map<String, String> permissionMap) {
        AppPermission permission = (AppPermission) findPermission(role, controlName);
        if (permission == null) {
            permission = new AppPermission();
            permission.setRole((AppRole) role);
            permission.setControlName(controlName);
        }
        permission.setPermissionMapFromMap(permissionMap);
        dao.saveOrUpdate(permission);
        return permission;
    }
}
```

### 3. Controllers

```java
// Login controller in public module
@Controller(path = "/public/login",
            authorizationModule = HasAccessAuthorizationModule.class)
public class LoginController extends org.nextframework.authorization.LoginController {

    @Override
    protected String afterLoginRedirectTo() {
        return "/app/dashboard";
    }
}

// Authorization management in admin module
@Controller(path = "/admin/authorization")
public class AuthorizationManagementController extends AuthorizationController {
}
```

### 4. JSP Views

Create authorization JSP at `/WEB-INF/jsp/admin/process/autorizacao.jsp` (or override `getModelAndView()`).

---

## Integration with Authentication

The `LoginController` implements `AuthenticationController`:

```java
public interface AuthenticationController {
    String getPath();  // Returns login URL for redirects
}
```

When a protected module denies access, the framework redirects to this path.

### Session Management

On successful login:
```java
getRequest().setUserAttribute("USER", userByLogin);
```

This sets the user in session, which `WebUserLocator` retrieves:
```java
User user = Authorization.getUserLocator().getUser();
```

---

## Cache Management

After saving permissions, the controller clears the permission cache:

```java
WebPermissionLocator permissionLocator = ...;
permissionLocator.clearCache();
```

This ensures permission changes take effect immediately without requiring users to log out.

---

## Dependencies

- `org.nextframework.authorization` - Core authorization framework
- `org.nextframework.controller` - Controller framework
- `org.nextframework.core` - Core utilities
- `org.nextframework.view` - JSP tag libraries
- Spring Framework (transactions)
