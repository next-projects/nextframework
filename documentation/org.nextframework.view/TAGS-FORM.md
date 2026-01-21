# Form and Input Tags

Tags for forms, data binding, and input fields.

---

## n:form

HTML form container with validation support.

```jsp
<n:form>
    <!-- form content -->
</n:form>
```

### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `url` | String | current URL | Form target URL (path) |
| `action` | String | current action | Controller action parameter (e.g., "save") |
| `method` | String | post | HTTP method |
| `enctype` | String | - | Encoding (auto-set for file uploads) |
| `validate` | Boolean | true | Enable client-side validation |
| `forBean` | String | - | Bean name for validation context |
| `name` | String | - | Form name |

### Examples

#### Basic Form

```jsp
<n:form action="save">
    <n:input name="name"/>
    <n:submit>Save</n:submit>
</n:form>
```

#### With Custom URL

```jsp
<n:form url="/admin/products" action="save">
    <n:input name="name"/>
    <n:submit>Save</n:submit>
</n:form>
```

#### File Upload

```jsp
<n:form action="upload">
    <n:input name="document" type="file"/>
    <n:submit>Upload</n:submit>
</n:form>
```

The `enctype="multipart/form-data"` is automatically set when the form contains file inputs.

---

## n:bean

Establishes a bean context for nested property access.

```jsp
<n:bean name="product">
    <!-- Properties accessed relative to product bean -->
    <n:input name="name"/>      <!-- product.name -->
    <n:input name="price"/>     <!-- product.price -->
</n:bean>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `name` | String | Request attribute name containing the bean |
| `valueType` | Class | Bean class (for type introspection) |
| `propertyPrefix` | String | Prefix for property names |
| `propertyIndex` | Integer | Index for collection items |
| `varDisplayName` | String | Variable name for display name |

### Nested Beans

```jsp
<n:bean name="order">
    <n:bean name="customer" propertyPrefix="customer">
        <n:input name="name"/>     <!-- order.customer.name -->
        <n:input name="email"/>    <!-- order.customer.email -->
    </n:bean>
</n:bean>
```

### With Collection Index

```jsp
<c:forEach items="${order.items}" var="item" varStatus="s">
    <n:bean name="item" propertyPrefix="items" propertyIndex="${s.index}">
        <n:input name="product"/>    <!-- order.items[0].product -->
        <n:input name="quantity"/>   <!-- order.items[0].quantity -->
    </n:bean>
</c:forEach>
```

---

## n:property

Exports bean property metadata for use by other tags.

```jsp
<n:bean name="product">
    <n:property name="price">
        <!-- Exports: varValue, varLabel, varName, varType, varAnnotations -->
        Value: ${varValue}
        Label: ${varLabel}
        Type: ${varType}
    </n:property>
</n:bean>
```

### Exported Variables

| Variable | Description |
|----------|-------------|
| `varValue` | Property value |
| `varLabel` | Display label (from @DisplayName or property name) |
| `varName` | Full property path |
| `varType` | Property type (Class) |
| `varAnnotations` | Property annotations array |
| `varParameterizedTypes` | Generic type parameters |

---

## n:input

Input field with 30+ types supported.

```jsp
<n:input name="username" type="text"/>
<n:input name="price" type="money"/>
<n:input name="category" type="select_one" itens="${categories}"/>
```

### Common Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `name` | String | Property name (required) |
| `type` | String | Input type |
| `value` | Object | Override value |
| `pattern` | String | Input mask pattern |
| `itens` | Object | Items for select types |
| `reloadOnChange` | Boolean | Reload form on change |
| Dynamic | - | HTML attributes (class, style, disabled, etc.) |

### Input Types

#### Basic Types

| Type | Description | Example |
|------|-------------|---------|
| `text` | Text input (default) | `<n:input name="name"/>` |
| `password` | Password input | `<n:input name="pwd" type="password"/>` |
| `hidden` | Hidden field | `<n:input name="id" type="hidden"/>` |
| `text_area` | Multi-line text | `<n:input name="desc" type="text_area" rows="5"/>` |
| `checkbox` | Checkbox | `<n:input name="active" type="checkbox"/>` |

#### Numeric Types

| Type | Description | Example |
|------|-------------|---------|
| `integer` | Integer input | `<n:input name="qty" type="integer"/>` |
| `float` | Decimal input | `<n:input name="rate" type="float"/>` |
| `money` | Currency input | `<n:input name="price" type="money"/>` |

#### Date/Time Types

| Type | Description | Example |
|------|-------------|---------|
| `date` | Date picker | `<n:input name="birthDate" type="date"/>` |
| `time` | Time picker | `<n:input name="startTime" type="time"/>` |

#### Selection Types

| Type | Description | Example |
|------|-------------|---------|
| `select_one` | Dropdown | `<n:input name="status" type="select_one" itens="${statuses}"/>` |
| `select_many` | Multi-select | `<n:input name="tags" type="select_many" itens="${tags}"/>` |
| `select_one_radio` | Radio buttons | `<n:input name="gender" type="select_one_radio" itens="${genders}"/>` |
| `select_one_button` | Popup selector | `<n:input name="customer" type="select_one_button" selectOnePath="/customers"/>` |
| `suggest` | Autocomplete | `<n:input name="city" type="suggest"/>` |

#### Brazilian Types

| Type | Description |
|------|-------------|
| `cpf` | Brazilian CPF (individual tax ID) |
| `cnpj` | Brazilian CNPJ (company tax ID) |
| `cep` | Brazilian postal code |
| `phone` | Brazilian phone number |
| `inscricao_estadual` | State registration number |

#### Other Types

| Type | Description |
|------|-------------|
| `file` | File upload |
| `html` | Rich text editor |
| `credit_card` | Credit card number |

### Select Attributes

| Attribute | Description |
|-----------|-------------|
| `itens` | Collection/array of options |
| `includeBlank` | Include empty option (default: true) |
| `blankLabel` | Label for blank option |
| `selectLabelProperty` | Property for option label |
| `useAjax` | Enable AJAX loading |
| `holdValue` | Maintain value after reload |

### Examples

#### Select with Entity Items

```jsp
<n:input name="category"
         type="select_one"
         itens="${categories}"
         selectLabelProperty="name"
         includeBlank="true"
         blankLabel="-- Select --"/>
```

#### File Upload

```jsp
<n:input name="photo"
         type="file"
         transientFile="false"
         showDeleteButton="true"/>
```

#### AJAX Cascade

```jsp
<n:comboReloadGroup useAjax="true">
    <n:input name="country" itens="${countries}" reloadOnChange="true"/>
    <n:input name="state" itens="${states}"/>
    <n:input name="city" itens="${cities}"/>
</n:comboReloadGroup>
```

#### Input with Mask

```jsp
<n:input name="phone" pattern="(00) 00000-0000"/>
<n:input name="zipCode" pattern="00000-000"/>
```

---

## n:output

Displays formatted values.

```jsp
<n:output value="${product.name}"/>
<n:output value="${product.price}" pattern="#,##0.00"/>
<n:output value="${product.active}" trueFalseNullLabels="Yes,No,-"/>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `value` | Object | Value to display |
| `pattern` | String | Format pattern |
| `escapeHTML` | Boolean | Escape HTML (default: true) |
| `replaceMessagesCodes` | Boolean | Replace message codes |
| `trueFalseNullLabels` | String | Labels for boolean (true,false,null) |
| `styleClass` | String | CSS class |
| `style` | String | Inline style |

### Examples

#### Boolean Formatting

```jsp
<n:output value="${user.active}" trueFalseNullLabels="Active,Inactive,Unknown"/>
```

#### Date Formatting

```jsp
<n:output value="${order.date}" pattern="dd/MM/yyyy"/>
<n:output value="${order.time}" pattern="HH:mm"/>
```

#### Number Formatting

```jsp
<n:output value="${product.price}" pattern="$#,##0.00"/>
<n:output value="${item.quantity}" pattern="#,##0"/>
```

---

## n:forEachBean

Iterates over a collection with bean context.

```jsp
<n:bean name="order">
    <n:forEachBean property="items" var="item" varIndex="i">
        <n:input name="product"/>
        <n:input name="quantity"/>
    </n:forEachBean>
</n:bean>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `property` | String | Collection property name |
| `var` | String | Variable name for current item |
| `varIndex` | String | Variable name for index |

---

## n:comboReloadGroup

Groups inputs for cascade reloading.

```jsp
<n:comboReloadGroup useAjax="true">
    <n:input name="parent" reloadOnChange="true" itens="${parents}"/>
    <n:input name="child" itens="${children}"/>
</n:comboReloadGroup>
```

When `parent` changes:
1. Form submits (or AJAX call if `useAjax="true"`)
2. Controller reloads `children` based on selected `parent`
3. Child input is updated with new items

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `useAjax` | Boolean | Use AJAX instead of form submit |

---

## n:validation

Generates client-side validation script.

```jsp
<n:form>
    <!-- inputs -->
    <n:validation functionName="validateForm"/>
</n:form>

<script>
    function submitForm() {
        if (validateForm()) {
            document.forms[0].submit();
        }
    }
</script>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `functionName` | String | Name of validation function |

---

## n:messages

Displays validation and info messages.

```jsp
<n:messages/>
```

Renders messages added via:
```java
request.addMessage("Success!");
request.addError("Validation failed");
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `renderAsHtml` | Boolean | Render as HTML (default: false) |

---

## n:hasMessages

Conditional rendering based on message presence.

```jsp
<n:hasMessages>
    <div class="alert">
        <n:messages/>
    </div>
</n:hasMessages>
```
