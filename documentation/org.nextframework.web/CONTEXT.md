# Web Context Management

Thread-safe access to request and application context from any code.

## WebRequestContext

Interface for accessing HTTP request information:

```java
public interface WebRequestContext extends RequestContext {

    // User/Security
    Principal getUserPrincipal();

    // Servlet objects
    HttpServletRequest getServletRequest();
    HttpServletResponse getServletResponse();
    HttpSession getSession();

    // Path info
    String getContextPath();
    String getServletPath();
    String getPathInfo();
    String getRequestQuery();
    String getFirstRequestUrl();

    // Validation
    BindException getBindException();

    // Navigation tracking
    String getLastAction();
    void setLastAction(String action);

    // Application context
    WebApplicationContext getWebApplicationContext();
}
```

### Accessing the Context

```java
// From anywhere in your code
WebRequestContext ctx = NextWeb.getRequestContext();

// Common operations
HttpServletRequest request = ctx.getServletRequest();
HttpSession session = ctx.getSession();
Principal user = ctx.getUserPrincipal();
Locale locale = ctx.getLocale();
TimeZone timeZone = ctx.getTimeZone();
```

### Messages and Errors

```java
WebRequestContext ctx = NextWeb.getRequestContext();

// Add messages (stored in session, shown once)
ctx.addMessage("Operation completed successfully");
ctx.addMessage("warning.key", MessageType.WARNING);

// Get messages for display
List<Message> messages = ctx.getMessages();

// Validation errors
BindException errors = ctx.getBindException();
if (errors.hasErrors()) {
    List<ObjectError> allErrors = errors.getAllErrors();
}
```

### Request Attributes

```java
WebRequestContext ctx = NextWeb.getRequestContext();

// Store/retrieve attributes (request scope)
ctx.setAttribute("myKey", myValue);
Object value = ctx.getAttribute("myKey");

// User persistent attributes (survives sessions via UserPersistentDataProvider)
ctx.setUserAttribute("preference", preferenceValue);
Object pref = ctx.getUserAttribute("preference");
```

### Locale and TimeZone

Resolved via Spring's `LocaleResolver`:

```java
WebRequestContext ctx = NextWeb.getRequestContext();

Locale locale = ctx.getLocale();       // User's locale
TimeZone tz = ctx.getTimeZone();       // User's timezone
```

### Navigation Tracking

Track the last action for redirects and navigation:

```java
WebRequestContext ctx = NextWeb.getRequestContext();

// Set when processing action
ctx.setLastAction("save");

// Check later (e.g., after redirect)
String lastAction = ctx.getLastAction();
if ("save".equals(lastAction)) {
    // Show success message
}
```

---

## WebApplicationContext

Application-wide context interface:

```java
public interface WebApplicationContext extends ApplicationContext {
    ServletContext getServletContext();
}
```

### Accessing Application Context

```java
// From anywhere
WebApplicationContext appCtx = NextWeb.getApplicationContext();
ServletContext servletContext = appCtx.getServletContext();
String appName = appCtx.getApplicationName();
```

---

## NextWeb Static Access

The `NextWeb` class provides static access to contexts via ThreadLocal:

```java
public class NextWeb {

    // Get current request context (throws if not in web request)
    public static WebRequestContext getRequestContext();

    // Get application context
    public static WebApplicationContext getApplicationContext();

    // Create/initialize context (called by framework)
    public static void createRequestContext(HttpServletRequest req, HttpServletResponse resp);

    // Customize request context creation
    public static void setWebRequestFactory(WebRequestFactory factory);
}
```

### Error Handling

When accessed outside a web request:

```java
try {
    WebRequestContext ctx = NextWeb.getRequestContext();
} catch (NotInNextContextException e) {
    // Code is not running in a web request
    // Error message includes class, method, and line number
}
```

---

## WebContext (Low-Level)

Direct ThreadLocal access for simple cases:

```java
// Get servlet objects directly
ServletContext servletContext = WebContext.getServletContext();
HttpServletRequest request = WebContext.getRequest();
```

Used internally by `WebContextFilter` to set up the thread context.

---

## Context Lifecycle

1. **Request arrives** - `WebContextFilter` intercepts
2. **ThreadLocal setup** - `WebContext.setServletRequest(request)`
3. **Context creation** - `NextWeb.createRequestContext(request, response)`
4. **Request processing** - Context available via `NextWeb.getRequestContext()`
5. **Request completes** - ThreadLocal cleared

```
HTTP Request
    │
    ▼
WebContextFilter.doFilter()
    │
    ├─► WebContext.setServletRequest(request)
    │
    ├─► NextWeb.createRequestContext(request, response)
    │       │
    │       └─► WebRequestFactory.createWebRequestContext()
    │               │
    │               └─► new DefaultWebRequestContext()
    │
    ├─► chain.doFilter() ─── Your code runs here
    │
    └─► ThreadLocal cleanup
```

---

## Custom Request Context

Extend `DefaultWebRequestContext` for custom behavior:

```java
public class MyRequestContext extends DefaultWebRequestContext {

    public MyRequestContext(HttpServletRequest request,
                           HttpServletResponse response,
                           WebApplicationContext appContext) {
        super(request, response, appContext);
    }

    @Override
    public Principal getUserPrincipal() {
        // Custom user resolution
        return myUserService.getCurrentUser();
    }

    @Override
    public Locale getLocale() {
        // Custom locale resolution
        return myLocaleService.getLocale();
    }
}
```

Register via factory:

```java
NextWeb.setWebRequestFactory(new WebRequestFactory() {
    @Override
    public WebRequestContext createWebRequestContext(
            HttpServletRequest request,
            HttpServletResponse response,
            WebApplicationContext appContext) {
        return new MyRequestContext(request, response, appContext);
    }
});
```
