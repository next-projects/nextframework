# Form Binding and Validation

How request parameters are bound to Java objects and validated.

---

## Automatic Binding

Request parameters automatically bind to command object properties:

```html
<form action="/admin/products?action=save" method="post">
    <input name="name" value="Widget"/>
    <input name="price" value="29.99"/>
    <input name="active" value="true"/>
</form>
```

```java
public class Product {
    private String name;      // "Widget"
    private BigDecimal price; // 29.99
    private Boolean active;   // true
}

public ModelAndView save(WebRequestContext request, Product product) {
    // product is populated from request
}
```

---

## Property Path Syntax

### Simple Properties

```
name=John
age=30
active=true
```

### Nested Objects

```
customer.name=John
customer.address.street=Main St
customer.address.city=Springfield
```

Maps to:

```java
product.getCustomer().getName()           // John
product.getCustomer().getAddress().getStreet()  // Main St
product.getCustomer().getAddress().getCity()    // Springfield
```

### Indexed Properties (Lists/Arrays)

```
items[0].name=Item 1
items[0].quantity=5
items[1].name=Item 2
items[1].quantity=3
```

Maps to:

```java
order.getItems().get(0).getName()      // Item 1
order.getItems().get(0).getQuantity()  // 5
order.getItems().get(1).getName()      // Item 2
```

### Map Properties

```
attributes[color]=Red
attributes[size]=Large
```

Maps to:

```java
product.getAttributes().get("color")  // Red
product.getAttributes().get("size")   // Large
```

---

## Type Conversion

The framework includes property editors for common types:

### Dates and Times

| Type | Format Examples |
|------|-----------------|
| `Date` | `dd/MM/yyyy`, `yyyy-MM-dd` |
| `Calendar` | `dd/MM/yyyy` |
| `Time` | `HH:mm`, `HH:mm:ss` |
| `Timestamp` | `dd/MM/yyyy HH:mm:ss` |

Custom date pattern per field:

```html
<input name="birthDate" value="25/12/1990"/>
<input name="_datePattern_birthDate" value="dd/MM/yyyy"/>
```

### Numbers

| Type | Conversion |
|------|------------|
| `Integer`, `Long` | Automatic |
| `BigDecimal` | Automatic, supports locale formatting |
| `Double`, `Float` | Automatic |

### Boolean

```
active=true
active=on
active=1
active=yes
```

All map to `Boolean.TRUE`.

### Enums

```html
<select name="status">
    <option value="ACTIVE">Active</option>
    <option value="INACTIVE">Inactive</option>
</select>
```

```java
public enum Status { ACTIVE, INACTIVE }
// Automatically converts "ACTIVE" to Status.ACTIVE
```

---

## Entity Binding

Bind entities by ID:

```html
<select name="category">
    <option value="1">Electronics</option>
    <option value="2">Clothing</option>
</select>
```

```java
public class Product {
    private Category category;  // Loaded by ID
}
```

The framework uses `DescriptionPropertyEditor` to load entities by their ID.

### Multiple Entities (Collection)

```html
<select name="tags" multiple>
    <option value="1">Sale</option>
    <option value="2">New</option>
    <option value="3">Featured</option>
</select>
```

---

## Special Parameters

### Null Value

Explicit null:

```html
<input name="description" value="<null>"/>
```

Sets `product.setDescription(null)`.

### Exclude Field

Prevent binding:

```html
<input name="price" value="100"/>
<input name="_excludeField" value="price"/>
```

`price` will not be bound.

### Date Pattern

Custom date format:

```html
<input name="eventDate" value="2024-12-25"/>
<input name="_datePattern_eventDate" value="yyyy-MM-dd"/>
```

---

## File Uploads

### Single File

```html
<form enctype="multipart/form-data">
    <input type="file" name="document"/>
</form>
```

```java
public class ProductForm {
    private File document;  // Uploaded file
}
```

### File with Metadata

```java
public class ProductForm {
    private File photo;
    private String photoContentType;
    private String photoFileName;
}
```

---

## Custom Property Editors

### Register in Controller

```java
@Override
protected void initBinder(WebRequestContext request, ServletRequestDataBinder binder) {
    super.initBinder(request, binder);

    // Custom editor for Money type
    binder.registerCustomEditor(Money.class, new MoneyEditor());

    // Custom date format
    binder.registerCustomEditor(Date.class, "eventDate",
        new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
}
```

### Implement PropertyEditor

```java
public class MoneyEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {
        if (text == null || text.isEmpty()) {
            setValue(null);
        } else {
            // Parse "R$ 1.234,56" to Money object
            setValue(Money.parse(text));
        }
    }

    @Override
    public String getAsText() {
        Money money = (Money) getValue();
        return money != null ? money.format() : "";
    }
}
```

### Global Registration

Implement `BinderConfigurer`:

```java
@Component
public class CustomBinderConfigurer implements BinderConfigurer {

    @Override
    public void configureBinder(ServletRequestDataBinder binder) {
        binder.registerCustomEditor(Money.class, new MoneyEditor());
        binder.registerCustomEditor(PhoneNumber.class, new PhoneNumberEditor());
    }
}
```

---

## Built-in Property Editors

| Editor | Type | Description |
|--------|------|-------------|
| `CalendarEditor` | Calendar | Date to Calendar |
| `TimePropertyEditor` | Time | Time parsing |
| `TimestampPropertyEditor` | Timestamp | Timestamp parsing |
| `CpfPropertyEditor` | String | Brazilian CPF validation |
| `CnpjPropertyEditor` | String | Brazilian CNPJ validation |
| `PhonePropertyEditor` | String | Phone number formatting |
| `MoneyPropertyEditor` | BigDecimal | Currency formatting |
| `CepPropertyEditor` | String | Brazilian postal code |
| `FileEditor` | File | File uploads |
| `DescriptionPropertyEditor` | Entity | Load entity by ID |

---

## Validation

### Automatic Validation

Enable with `@Command(validate = true)` (default):

```java
@Input("form")
@Command(validate = true)
public ModelAndView save(WebRequestContext request, Product product) {
    // Validation runs before this method
}
```

### Bean Validation (JSR-303)

```java
public class Product {

    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;

    @Email
    private String contactEmail;
}
```

### Custom Validation

Override in controller:

```java
@Override
protected void customValidation(WebRequestContext request,
                                Object command,
                                BindException errors) {
    Product product = (Product) command;

    // Field-level error
    if (product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
        errors.rejectValue("price", "invalid", "Price must be positive");
    }

    // Object-level error
    if (product.getStartDate() != null && product.getEndDate() != null) {
        if (product.getStartDate().after(product.getEndDate())) {
            errors.reject("dateRange", "Start date must be before end date");
        }
    }
}
```

### Checking Errors

```java
public ModelAndView save(WebRequestContext request, Product product) {
    BindException errors = request.getBindException();

    if (errors.hasErrors()) {
        // Handle errors manually
        return new ModelAndView("product/form");
    }

    // Proceed with save
    productService.save(product);
    return new ModelAndView("redirect:/admin/products");
}
```

### Suppress Validation

```html
<input type="hidden" name="suppressValidation" value="true"/>
```

Or in controller:

```java
@Command(validate = false)
public ModelAndView preview(WebRequestContext request, Product product) {
    // No validation runs
}
```

---

## Error Messages

### In Properties File

`messages.properties`:

```properties
NotNull.product.name=Product name is required
Size.product.name=Product name must be between {2} and {1} characters
DecimalMin.product.price=Price must be at least {1}
```

### Programmatic

```java
errors.rejectValue("name", "duplicate", "This name already exists");
errors.rejectValue("price", "invalid", new Object[]{0}, "Price must be greater than {0}");
errors.reject("general.error", "An error occurred");
```

### Display in View

```jsp
<%-- Field-specific errors --%>
<t:input name="name"/>  <%-- Shows error next to field --%>

<%-- All errors --%>
<t:validation-messages/>

<%-- Manual error display --%>
<c:forEach items="${errors.allErrors}" var="error">
    <div class="error">${error.defaultMessage}</div>
</c:forEach>
```

---

## Command Lifecycle

Events during binding:

```java
@Component
public class AuditCommandListener implements CommandEventListener {

    @Override
    public void onInstantiateNewCommand(Object command, Class<?> commandClass) {
        // After command object created
    }

    @Override
    public void onCreateBinderForCommand(Object command, ServletRequestDataBinder binder) {
        // After binder created, before binding
    }

    @Override
    public void onCommandBind(Object command, ServletRequestDataBinder binder) {
        // After binding, before validation
    }

    @Override
    public void onCommandValidation(Object command, BindException errors) {
        // After validation
    }
}
```
