# Navigation Tags

Tags for links, buttons, menus, and pagination.

---

## n:link

Hyperlink with URL building and confirmation support.

```jsp
<n:link url="/admin/products?action=edit&id=${product.id}">Edit</n:link>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `url` | String | Target URL |
| `action` | String | Action parameter |
| `type` | String | Render type: `link` (default), `button`, `image` |
| `parameters` | String | Additional URL parameters |
| `confirmationMessage` | String | Confirmation dialog message |
| `styleClass` | String | CSS class |
| `style` | String | Inline style |
| Dynamic | - | HTML attributes (target, title, etc.) |

### Types

#### Standard Link

```jsp
<n:link url="/products?action=view&id=${id}">View Details</n:link>
```

#### Button Style

```jsp
<n:link url="/products?action=create" type="button" class="btn btn-primary">
    New Product
</n:link>
```

#### Image Link

```jsp
<n:link url="/products?action=edit&id=${id}" type="image">
    <img src="/images/edit.png" alt="Edit"/>
</n:link>
```

### With Confirmation

```jsp
<n:link url="/products?action=delete&id=${id}"
        confirmationMessage="Are you sure you want to delete this product?">
    Delete
</n:link>
```

### Building URLs

#### With Action Parameter

```jsp
<n:link url="/admin/products" action="edit" parameters="id=${product.id}">
    Edit
</n:link>
<!-- Generates: /admin/products?action=edit&id=123 -->
```

#### With Multiple Parameters

```jsp
<n:link url="/reports/sales"
        parameters="startDate=${startDate}&endDate=${endDate}&format=pdf">
    Download Report
</n:link>
```

---

## n:submit

Form submit button with various render types.

```jsp
<n:submit action="save">Save</n:submit>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `action` | String | Action to submit to |
| `type` | String | Render type: `button`, `submit` (default), `link`, `image` |
| `validate` | Boolean | Run validation before submit |
| `confirmationScript` | String | JavaScript confirmation |
| `styleClass` | String | CSS class |
| `style` | String | Inline style |

### Types

#### Standard Submit Button

```jsp
<n:submit action="save">Save</n:submit>
```

#### Button (non-submit)

```jsp
<n:submit action="preview" type="button" validate="false">Preview</n:submit>
```

#### Link Style

```jsp
<n:submit action="cancel" type="link">Cancel</n:submit>
```

#### Image Button

```jsp
<n:submit action="save" type="image">
    <img src="/images/save.png" alt="Save"/>
</n:submit>
```

### With Validation

```jsp
<n:submit action="save" validate="true">Save</n:submit>
<n:submit action="draft" validate="false">Save as Draft</n:submit>
```

### With Confirmation

```jsp
<n:submit action="delete"
          confirmationScript="return confirm('Delete this record?')">
    Delete
</n:submit>
```

### Multiple Submit Buttons

```jsp
<n:form>
    <!-- Form fields -->

    <n:submit action="save">Save</n:submit>
    <n:submit action="saveAndNew">Save & New</n:submit>
    <n:submit action="cancel" type="link" validate="false">Cancel</n:submit>
</n:form>
```

---

## n:pagging

Pagination controls.

```jsp
<n:pagging currentPage="${currentPage}"
           totalNumberOfPages="${totalPages}"
           parameters="filter.name=${filter.name}"/>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `currentPage` | Integer | Current page number (0-based) |
| `totalNumberOfPages` | Integer | Total number of pages |
| `parameters` | String | Parameters to preserve in page links |

### Example with Filter

```jsp
<t:filterPanel>
    <n:input name="filter.name"/>
    <n:submit>Search</n:submit>
</t:filterPanel>

<n:dataGrid itens="${items}" var="item">
    <!-- columns -->
</n:dataGrid>

<n:pagging currentPage="${page}"
           totalNumberOfPages="${totalPages}"
           parameters="filter.name=${filter.name}&filter.status=${filter.status}"/>
```

---

## n:menu

Renders navigation menus from XML or objects.

```jsp
<n:menu menu="${menu}"/>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `menu` | Menu | Menu object to render |

### Menu Definition (XML)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE menu SYSTEM "menu.dtd">
<menu>
    <item label="Home" url="/home"/>
    <item label="Products">
        <item label="List" url="/products"/>
        <item label="Add New" url="/products?action=create"/>
    </item>
    <item label="Reports">
        <item label="Sales" url="/reports/sales"/>
        <item label="Inventory" url="/reports/inventory"/>
    </item>
    <item label="Settings" url="/settings"/>
</menu>
```

### Menu Builders

The framework provides multiple menu builders:

- `MenuBuilderBootstrap` - Bootstrap-compatible HTML
- `MenuBuilderJS` - JavaScript-based menus

### Programmatic Menu

```java
Menu menu = new Menu();
menu.addItem(new MenuItem("Home", "/home"));

MenuItem products = new MenuItem("Products");
products.addItem(new MenuItem("List", "/products"));
products.addItem(new MenuItem("Add", "/products?action=create"));
menu.addItem(products);

request.setAttribute("menu", menu);
```

---

## n:hasAuthorization

Conditional rendering based on user authorization.

```jsp
<n:hasAuthorization url="/admin/users" action="delete">
    <n:link url="/admin/users?action=delete&id=${user.id}">Delete</n:link>
</n:hasAuthorization>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `url` | String | Controller path to check |
| `action` | String | Action to check (CREATE, READ, UPDATE, DELETE) |

### Examples

#### Show Edit Link Only If Authorized

```jsp
<n:hasAuthorization url="/admin/products" action="update">
    <n:link url="/admin/products?action=edit&id=${product.id}">Edit</n:link>
</n:hasAuthorization>
```

#### Conditional Action Buttons

```jsp
<n:hasAuthorization url="/admin/orders" action="update">
    <n:submit action="approve">Approve</n:submit>
</n:hasAuthorization>

<n:hasAuthorization url="/admin/orders" action="delete">
    <n:submit action="cancel" confirmationScript="return confirm('Cancel order?')">
        Cancel Order
    </n:submit>
</n:hasAuthorization>
```

---

## Patterns

### CRUD Action Bar

```jsp
<div class="action-bar">
    <n:link url="/products?action=create" type="button" class="btn-primary">
        New Product
    </n:link>

    <n:hasAuthorization url="/products" action="delete">
        <n:submit action="deleteSelected"
                  type="button"
                  class="btn-danger"
                  confirmationScript="return confirm('Delete selected items?')">
            Delete Selected
        </n:submit>
    </n:hasAuthorization>
</div>
```

### Breadcrumb Navigation

```jsp
<nav class="breadcrumb">
    <n:link url="/home">Home</n:link> &gt;
    <n:link url="/products">Products</n:link> &gt;
    <span>${product.name}</span>
</nav>
```

### Form with Multiple Actions

```jsp
<n:form>
    <!-- Form content -->

    <div class="form-actions">
        <n:submit action="save" class="btn-primary">Save</n:submit>
        <n:submit action="apply" class="btn-secondary">Apply</n:submit>
        <n:link url="/products" type="button" class="btn-default">Cancel</n:link>
    </div>
</n:form>
```

### Grid Row Actions

```jsp
<n:dataGrid itens="${products}" var="product">
    <n:column header="Name">
        <n:output value="${product.name}"/>
    </n:column>
    <n:column header="Actions" headerStyleClass="actions-col">
        <n:link url="/products?action=view&id=${product.id}" title="View">
            <img src="/icons/view.png"/>
        </n:link>
        <n:hasAuthorization url="/products" action="update">
            <n:link url="/products?action=edit&id=${product.id}" title="Edit">
                <img src="/icons/edit.png"/>
            </n:link>
        </n:hasAuthorization>
        <n:hasAuthorization url="/products" action="delete">
            <n:link url="/products?action=delete&id=${product.id}"
                    title="Delete"
                    confirmationMessage="Delete ${product.name}?">
                <img src="/icons/delete.png"/>
            </n:link>
        </n:hasAuthorization>
    </n:column>
</n:dataGrid>
```
