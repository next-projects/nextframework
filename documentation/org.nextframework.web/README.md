# org.nextframework.web

## Overview

Web layer foundation for Next Framework applications. Provides thread-safe context access, Spring integration, and web utilities.

```java
// Access current request context from anywhere
WebRequestContext ctx = NextWeb.getRequestContext();
String clientIp = WebUtils.getClientIpAddress();
Locale locale = ctx.getLocale();

// Access application context
WebApplicationContext appCtx = NextWeb.getApplicationContext();
```

---

## Core Features

### Thread-Local Context Access

Access request and application context from any code without passing parameters:

```java
// Request context - available during HTTP request processing
WebRequestContext request = NextWeb.getRequestContext();
HttpServletRequest servletRequest = request.getServletRequest();
HttpSession session = request.getSession();
Principal user = request.getUserPrincipal();

// Application context - available application-wide
WebApplicationContext app = NextWeb.getApplicationContext();
ServletContext servletContext = app.getServletContext();
```

**See [CONTEXT.md](CONTEXT.md) for detailed context documentation.**

---

### Automatic Initialization

The module auto-configures via `web-fragment.xml`:
- Component scanning of application packages
- Spring context initialization
- Thread-local context setup per request

No `web.xml` configuration required for basic setup.

**See [INITIALIZATION.md](INITIALIZATION.md) for bootstrap details.**

---

### Web Utilities

Common web operations via `WebUtils`:

```java
// Client IP (handles proxies, load balancers)
String ip = WebUtils.getClientIpAddress();

// Server paths
String realPath = WebUtils.getServerRealPath();
String fullUrl = WebUtils.getFullUrl(request, "/path");

// Request info
String module = WebUtils.getRequestModule();
String controller = WebUtils.getRequestController();
String action = WebUtils.getRequestAction();

// URL rewriting (pluggable via UrlRewriter service)
String rewritten = WebUtils.rewriteUrl("/my/path");
```

#### XSS Prevention

```java
// Check for HTML/script injection
if (WebUtils.containsTagsOrCodes(userInput)) {
    // reject input
}

// Sanitize input
String clean = WebUtils.removeTagsAndCodes(userInput);

// Validate request parameters
WebUtils.verificaMapComHTML(request.getParameterMap(), "allowedField1,allowedField2");

// Validate bean attributes
WebUtils.verificaAtributosComHTML(myBean, "htmlContentField");
```

Detects and removes:
- HTML tags (`<script>`, `<div>`, etc.)
- `eval()` expressions
- `expression()` CSS expressions
- `javascript:` protocol

---

## Key Classes

| Class | Description |
|-------|-------------|
| `NextWeb` | Static access to request/application context |
| `WebRequestContext` | HTTP request context interface |
| `WebApplicationContext` | Application context interface |
| `WebContext` | Low-level ThreadLocal holder |
| `WebUtils` | Web utility methods |
| `NextWebApplicationContext` | Spring context with auto-scanning |

---

## Extension Points

### Custom Request Context Factory

```java
public class MyWebRequestFactory extends WebRequestFactory {
    @Override
    public WebRequestContext createWebRequestContext(
            HttpServletRequest request,
            HttpServletResponse response,
            WebApplicationContext appContext) {
        return new MyCustomRequestContext(request, response, appContext);
    }
}

// Register at startup
NextWeb.setWebRequestFactory(new MyWebRequestFactory());
```

### Custom URL Rewriter

Implement `UrlRewriter` and register via SPI:

```java
public class MyUrlRewriter implements UrlRewriter {
    @Override
    public String rewriteUrl(String url) {
        return "/prefix" + url;
    }
}
```

Register in `META-INF/services/org.nextframework.web.service.UrlRewriter`.

### Custom Bean Definition Loading

Implement `BeanDefinitionLoader` to add custom beans during startup:

```java
public class MyBeanLoader implements BeanDefinitionLoader {
    @Override
    public void loadBeanDefinitions(ApplicationContext context,
                                    DefaultListableBeanFactory beanFactory) {
        // Register custom beans
    }
}
```

---

## Dependencies

- `org.nextframework.context` - Core context interfaces
- Spring Framework (web, context)
- Servlet API
