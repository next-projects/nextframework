# org.nextframework.controller

## Overview

Web MVC framework built on Spring MVC. Provides multi-action controllers where a single class handles multiple HTTP requests via action methods, with built-in support for CRUD operations, form binding, validation, and various view types.

```java
@Controller(path = "/admin/products")
public class ProductController extends MultiActionController {

    @DefaultAction
    public ModelAndView list(WebRequestContext request) {
        setAttribute("products", productService.findAll());
        return new ModelAndView("product/list");
    }

    @Input("form")
    public ModelAndView save(WebRequestContext request, Product product) {
        productService.save(product);
        addMessage("Product saved");
        return new ModelAndView("redirect:/admin/products");
    }
}
```

---

## Key Features

- **Module-based organization** - Controllers grouped into modules
- **Multi-action controllers** - One class handles multiple actions
- **Annotation-based routing** - `@Controller`, `@Action`, `@DefaultAction`
- **CRUD base class** - `CrudController` with standard operations
- **Automatic binding** - Request parameters to Java objects
- **Validation** - Annotation-based and custom validation
- **Multiple view types** - JSP, JSON, redirects, resources
- **Error handling** - `@Input`, `@OnErrors` for error flows

---

## Quick Start

### 1. Create a Controller

```java
@Controller(path = "/admin/hello")
public class HelloController extends MultiActionController {

    @DefaultAction
    public ModelAndView index(WebRequestContext request) {
        return new ModelAndView("hello");
    }
}
```

### 2. Access at URL

```
http://localhost:8080/app/admin/hello
```

### 3. Call Specific Action

```
http://localhost:8080/app/admin/hello?action=save
```

---

## Modules

Controllers are organized into **modules**. The module is the **first segment** of the controller path.

### Path Structure

```
@Controller(path = "/{module}/{controller}")
```

| Path | Module | Controller |
|------|--------|------------|
| `/admin/users` | admin | users |
| `/admin/products` | admin | products |
| `/reports/sales` | reports | sales |
| `/public/info` | public | info |

### How It Works

1. Framework scans all `@Controller` classes at startup
2. Extracts module name from first path segment
3. Creates a `NextDispatcherServlet` for each module
4. Servlet maps to `/{module}/*`

```
Controllers found:
  /admin/users     → module: admin
  /admin/products  → module: admin
  /reports/sales   → module: reports

Servlets created:
  admin   → handles /admin/*
  reports → handles /reports/*
```

### Module Security

Secure entire modules via `authentication.properties`:

```properties
# Modules requiring authentication
admin=true
reports=true

# Public modules (no authentication needed)
public=false
```

When a module is secured:
- All controllers in that module require authentication
- Unauthenticated users are redirected to login

### Example Organization

```
/public           ← Public module (no auth)
  /public/home
  /public/about
  /public/contact

/app              ← App module (authenticated users)
  /app/dashboard
  /app/profile
  /app/settings

/admin            ← Admin module (admin role)
  /admin/users
  /admin/products
  /admin/reports
```

---

## Controller Basics

### @Controller Annotation

```java
@Controller(
    path = "/admin/products",                        // Must include module
    authorizationModule = CrudAuthorizationModule.class  // Authorization
)
public class ProductController extends MultiActionController {
}
```

Multiple paths (must be in same module):

```java
@Controller(path = {"/admin/product", "/admin/products"})
```

**Note:** Path must start with `/` and include a module (first segment).

### Action Methods

Methods that handle requests. Can receive:
- `WebRequestContext` - Access to request, session, messages
- Command objects - Automatically bound from request parameters

```java
@DefaultAction
public ModelAndView list(WebRequestContext request) {
    // Handle request
    return new ModelAndView("viewName");
}

public ModelAndView save(WebRequestContext request, Product product) {
    // product is automatically bound from request
    return new ModelAndView("redirect:/products");
}
```

### Action Resolution

1. **No action parameter** → Method with `@DefaultAction`
2. **action=save** → Method with `@Action("save")` or method named `save`

```java
@DefaultAction
@Action("list")
public ModelAndView list() { }

@Action("save")
public ModelAndView save() { }

// Also matches action=delete (by method name)
public ModelAndView delete() { }
```

---

## Annotations

| Annotation | Target | Description |
|------------|--------|-------------|
| `@Controller` | Class | Marks controller, defines path and authorization |
| `@Action` | Method | Action name for routing |
| `@DefaultAction` | Method | Default when no action specified |
| `@Input` | Method | Redirect target on validation errors |
| `@OnErrors` | Method | Redirect target on exceptions |
| `@Command` | Method | Configure command object binding |

**See [ANNOTATIONS.md](ANNOTATIONS.md) for detailed documentation.**

---

## Views and Responses

### ModelAndView

```java
// JSP view
return new ModelAndView("product/list");

// With model data
ModelAndView mv = new ModelAndView("product/list");
mv.addObject("products", productList);
return mv;

// Redirect
return new ModelAndView("redirect:/products");

// Redirect to action
return new ModelAndView("redirect:/products?action=list");
```

### Helper Methods

```java
// Redirect to action in same controller
return sendRedirectToAction("list");

// Continue to another action (internal forward)
return continueOnAction("form", product);

// Set attributes (available in view)
setAttribute("key", value);
```

### JSON Response

```java
public Object getProductsJson(WebRequestContext request) {
    return productService.findAll();  // Automatically serialized to JSON
}
```

### Resource Download

```java
public ModelAndView downloadReport(WebRequestContext request) {
    byte[] pdf = generatePdf();
    Resource resource = new Resource("application/pdf", "report.pdf", pdf);
    return new ResourceModelAndView(resource);
}
```

### Classpath Views

Views bundled in JAR files:

```java
return new ClasspathModelAndView("org.nextframework.mymodule.view");
```

---

## Form Binding

Request parameters automatically bind to command objects:

```html
<form action="/products?action=save" method="post">
    <input name="name" value="Widget"/>
    <input name="price" value="29.99"/>
    <input name="category.id" value="5"/>
</form>
```

```java
public ModelAndView save(WebRequestContext request, Product product) {
    // product.name = "Widget"
    // product.price = 29.99
    // product.category.id = 5
}
```

### Nested Properties

```
customer.name=John
customer.address.street=Main St
customer.address.city=Springfield
items[0].name=Item 1
items[1].name=Item 2
```

### Special Binding Features

| Parameter | Description |
|-----------|-------------|
| `_excludeField` | Exclude field from binding |
| `_datePattern` | Custom date format |
| `<null>` | Explicit null value |

**See [BINDING.md](BINDING.md) for complete binding documentation.**

---

## Validation

### Automatic Validation

```java
@Input("form")  // Redirect here on validation errors
public ModelAndView save(WebRequestContext request, Product product) {
    // If validation fails, redirects to "form" action
    productService.save(product);
    return new ModelAndView("redirect:/products");
}
```

### Check for Errors

```java
public ModelAndView save(WebRequestContext request, Product product) {
    BindException errors = request.getBindException();
    if (errors.hasErrors()) {
        return new ModelAndView("product/form");
    }
    // proceed with save
}
```

### Custom Validation

Override in controller:

```java
@Override
protected void customValidation(WebRequestContext request,
                                Object command,
                                BindException errors) {
    Product p = (Product) command;
    if (p.getPrice() < 0) {
        errors.rejectValue("price", "invalid", "Price must be positive");
    }
}
```

---

## Error Handling

### @Input - Validation Errors

```java
@Input("form")  // Go back to form on validation error
public ModelAndView save(WebRequestContext request, Product product) {
    productService.save(product);
    return new ModelAndView("redirect:/products");
}
```

### @OnErrors - Exceptions

```java
@OnErrors("list")  // Go to list on any exception
public ModelAndView delete(WebRequestContext request, Product product) {
    productService.delete(product);
    return new ModelAndView("redirect:/products");
}
```

### Exception Handler Methods

```java
// Handles DataAccessException in this controller
public ModelAndView handleException(WebRequestContext request,
                                    DataAccessException e) {
    addError("Database error: " + e.getMessage());
    return new ModelAndView("error");
}
```

---

## CRUD Controller

For standard create/read/update/delete operations:

```java
@Controller(path = "/products")
public class ProductController extends CrudController<Product> {

    // Provides: list, form, create, update, view, save, delete
}
```

**See [CRUD.md](CRUD.md) for complete CRUD documentation.**

---

## WebRequestContext

Access request information and utilities:

```java
public ModelAndView example(WebRequestContext request) {
    // Request/Response
    HttpServletRequest servletRequest = request.getServletRequest();
    HttpSession session = request.getSession();

    // User
    Principal user = request.getUserPrincipal();
    Locale locale = request.getLocale();

    // Messages
    request.addMessage("Operation successful");
    request.addMessage("warning.key", MessageType.WARN);
    request.addError("Something went wrong");

    // Attributes
    request.setAttribute("key", value);
    Object val = request.getAttribute("key");

    // Validation errors
    BindException errors = request.getBindException();
}
```

---

## Session Command Objects

Store command objects in session between requests:

```java
@Command(session = true)
public ModelAndView form(WebRequestContext request, Product product) {
    // product retrieved from session if exists, otherwise new instance
    return new ModelAndView("product/form");
}
```

With custom session key:

```java
@Command(session = true, name = "editProduct")
public ModelAndView form(WebRequestContext request, Product product) {
}
```

---

## Files

- [ANNOTATIONS.md](ANNOTATIONS.md) - Complete annotation reference
- [CRUD.md](CRUD.md) - CrudController documentation
- [BINDING.md](BINDING.md) - Form binding and property editors

---

## Dependencies

- `org.nextframework.core` - Core utilities
- `org.nextframework.web` - Web context
- `org.nextframework.authorization` - Authorization integration
- Spring Web MVC
- Jackson (for JSON)
