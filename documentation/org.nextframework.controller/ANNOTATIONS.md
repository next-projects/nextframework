# Controller Annotations

Complete reference for controller annotations.

---

## @Controller

Marks a class as a controller and configures URL mapping.

```java
@Controller(
    path = "/admin/products",
    authorizationModule = CrudAuthorizationModule.class
)
public class ProductController extends MultiActionController {
}
```

| Attribute | Type | Required | Description |
|-----------|------|----------|-------------|
| `path` | String[] | Yes | URL path(s) - must include module as first segment |
| `authorizationModule` | Class | No | Authorization module (default: HasAccessAuthorizationModule) |

### Path Structure

The path **must** follow the pattern `/{module}/{controller}`:

```java
@Controller(path = "/admin/products")   // module=admin, controller=products
@Controller(path = "/reports/sales")    // module=reports, controller=sales
@Controller(path = "/public/info")      // module=public, controller=info
```

### Multiple Paths

Multiple paths must be in the same module:

```java
@Controller(path = {"/admin/product", "/admin/products"})
public class ProductController extends MultiActionController {
    // Both paths in "admin" module
}
```

### Authorization Modules

```java
// No authorization (public)
@Controller(path = "/public/info", authorizationModule = HasAccessAuthorizationModule.class)

// Requires login
@Controller(path = "/app/profile", authorizationModule = RequiresAuthenticationAuthorizationModule.class)

// CRUD permissions (default for CrudController)
@Controller(path = "/admin/users", authorizationModule = CrudAuthorizationModule.class)

// Report permissions
@Controller(path = "/reports/sales", authorizationModule = ReportAuthorizationModule.class)

// Process permissions
@Controller(path = "/admin/jobs", authorizationModule = ProcessAuthorizationModule.class)
```

---

## @Action

Defines the action name for a method.

```java
@Action("save")
public ModelAndView saveProduct(WebRequestContext request, Product product) {
    // Called when action=save
}
```

| Attribute | Type | Required | Description |
|-----------|------|----------|-------------|
| `value` | String | Yes | Action name |

### Usage

```java
@Action("list")
public ModelAndView listProducts() { }

@Action("edit")
public ModelAndView editProduct() { }

@Action("delete")
public ModelAndView deleteProduct() { }
```

URL: `/products?action=edit`

### Method Name Fallback

If no `@Action` annotation, method name is used:

```java
// Matches action=calculate
public ModelAndView calculate() { }
```

---

## @DefaultAction

Marks the method called when no action parameter is provided.

```java
@DefaultAction
public ModelAndView index(WebRequestContext request) {
    // Called for /products (no action parameter)
}
```

Only one method per controller should have `@DefaultAction`.

### Combined with @Action

```java
@DefaultAction
@Action("list")
public ModelAndView list(WebRequestContext request) {
    // Called for /products OR /products?action=list
}
```

---

## @Input

Specifies the action to redirect to when validation fails.

```java
@Input("form")
public ModelAndView save(WebRequestContext request, Product product) {
    // If validation fails, redirects to "form" action
    productService.save(product);
    return new ModelAndView("redirect:/products");
}
```

| Attribute | Type | Required | Description |
|-----------|------|----------|-------------|
| `value` | String | Yes | Target action on validation error |

### How It Works

1. Request parameters bound to command object
2. Validation runs (annotation-based + custom)
3. If errors exist → redirect to `@Input` action
4. If no errors → method executes normally

### Example Flow

```java
@Action("form")
public ModelAndView form(WebRequestContext request, Product product) {
    // Display form (also receives product with errors on redirect)
    return new ModelAndView("product/form");
}

@Action("save")
@Input("form")  // ← On validation error, go back to form
public ModelAndView save(WebRequestContext request, Product product) {
    productService.save(product);
    return new ModelAndView("redirect:/products");
}
```

### Accessing Errors in View

```jsp
<%-- In product/form.jsp --%>
<t:form>
    <t:input name="name"/>  <%-- Shows error if validation failed --%>
    <t:input name="price"/>
</t:form>
```

---

## @OnErrors

Specifies the action to redirect to when an exception occurs.

```java
@OnErrors("list")
public ModelAndView delete(WebRequestContext request, Product product) {
    productService.delete(product);  // May throw exception
    return new ModelAndView("redirect:/products");
}
```

| Attribute | Type | Required | Description |
|-----------|------|----------|-------------|
| `value` | String | Yes | Target action on exception |

### Exception Handling Flow

1. Method executes
2. Exception thrown
3. Framework catches exception
4. Adds error message to context
5. Redirects to `@OnErrors` action

### Example

```java
@Action("delete")
@OnErrors("list")
public ModelAndView delete(WebRequestContext request, Product product) {
    // If delete fails, error message added and redirect to list
    productService.delete(product);
    addMessage("Product deleted");
    return new ModelAndView("redirect:/products");
}

@DefaultAction
@Action("list")
public ModelAndView list(WebRequestContext request) {
    // Error message will be displayed here
    setAttribute("products", productService.findAll());
    return new ModelAndView("product/list");
}
```

---

## @Command

Configures command object binding for an action method.

```java
@Command(session = true, validate = true, name = "productForm")
public ModelAndView form(WebRequestContext request, Product product) {
}
```

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `session` | boolean | false | Store command in session |
| `validate` | boolean | true | Run validation |
| `name` | String | "" | Custom session attribute name |

### Session Storage

Store command between requests (e.g., multi-step forms):

```java
@Command(session = true)
public ModelAndView step1(WebRequestContext request, WizardForm form) {
    return new ModelAndView("wizard/step1");
}

@Command(session = true)
public ModelAndView step2(WebRequestContext request, WizardForm form) {
    // Same form instance from session
    return new ModelAndView("wizard/step2");
}

@Command(session = true)
public ModelAndView finish(WebRequestContext request, WizardForm form) {
    // Complete wizard with all data
    wizardService.complete(form);
    return new ModelAndView("redirect:/wizard/done");
}
```

### Custom Session Name

```java
@Command(session = true, name = "checkoutCart")
public ModelAndView cart(WebRequestContext request, ShoppingCart cart) {
}
```

### Disable Validation

```java
@Command(validate = false)
public ModelAndView preview(WebRequestContext request, Product product) {
    // Binding happens but no validation
}
```

---

## Combining Annotations

Common patterns:

### List Action (Default)

```java
@DefaultAction
@Action("list")
public ModelAndView list(WebRequestContext request, ProductFilter filter) {
    setAttribute("products", productService.find(filter));
    return new ModelAndView("product/list");
}
```

### Form Display

```java
@Action("form")
public ModelAndView form(WebRequestContext request, Product product) {
    if (product.getId() != null) {
        product = productService.load(product.getId());
    }
    return new ModelAndView("product/form");
}
```

### Save with Validation

```java
@Action("save")
@Input("form")
@OnErrors("form")
public ModelAndView save(WebRequestContext request, Product product) {
    productService.save(product);
    addMessage("Product saved successfully");
    return new ModelAndView("redirect:/products");
}
```

### Delete with Error Handling

```java
@Action("delete")
@OnErrors("list")
public ModelAndView delete(WebRequestContext request, Product product) {
    productService.delete(product);
    addMessage("Product deleted");
    return new ModelAndView("redirect:/products");
}
```

### Multi-Step Wizard

```java
@Action("step1")
@Command(session = true)
public ModelAndView step1(WebRequestContext request, OrderForm form) {
    return new ModelAndView("order/step1");
}

@Action("step2")
@Input("step1")
@Command(session = true)
public ModelAndView step2(WebRequestContext request, OrderForm form) {
    return new ModelAndView("order/step2");
}

@Action("step3")
@Input("step2")
@Command(session = true)
public ModelAndView step3(WebRequestContext request, OrderForm form) {
    return new ModelAndView("order/step3");
}

@Action("submit")
@Input("step3")
@OnErrors("step1")
@Command(session = true)
public ModelAndView submit(WebRequestContext request, OrderForm form) {
    orderService.placeOrder(form);
    return new ModelAndView("redirect:/order/confirmation");
}
```
