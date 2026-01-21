# Template Tags (t:)

High-level view composition tags for building CRUD screens quickly.

```jsp
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>
```

---

## Overview

Template tags provide pre-built structures for common view patterns:

```
┌─────────────────────────────────────────────────────────┐
│  t:view                                                  │
│  ┌─────────────────────────────────────────────────┐    │
│  │  t:filterPanel / t:simplePanel / t:formPanel    │    │
│  │  ┌─────────────────────────────────────────┐    │    │
│  │  │  t:filterTable / t:formTable             │    │    │
│  │  │  ┌─────────────────────────────────┐    │    │    │
│  │  │  │  t:property  t:property         │    │    │    │
│  │  │  └─────────────────────────────────┘    │    │    │
│  │  └─────────────────────────────────────────┘    │    │
│  └─────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────┐    │
│  │  t:listPanel                                     │    │
│  │  ┌─────────────────────────────────────────┐    │    │
│  │  │  n:dataGrid                              │    │    │
│  │  └─────────────────────────────────────────┘    │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
```

---

## t:view

Base view container. Wraps content in a standard page structure.

```jsp
<t:view title="Product Management">
    <!-- View content -->
</t:view>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `title` | String | Page title |
| `useForm` | Boolean | Wrap in form tag |
| `styleClass` | String | Container CSS class |

### Specialized Views

#### t:listView

View for list screens with "New" button.

```jsp
<t:listView title="Products">
    <t:filterPanel>
        <t:filterTable>
            <t:property name="filter.name"/>
        </t:filterTable>
    </t:filterPanel>

    <t:listPanel>
        <n:dataGrid itens="${products}" var="product">
            <t:property name="name"/>
            <t:property name="price"/>
        </n:dataGrid>
    </t:listPanel>
</t:listView>
```

#### t:formView

View for form screens with navigation links.

```jsp
<t:formView title="Edit Product">
    <t:formPanel>
        <t:formTable>
            <t:property name="name"/>
            <t:property name="price"/>
        </t:formTable>
    </t:formPanel>
</t:formView>
```

#### t:reportView

View for report screens.

```jsp
<t:reportView title="Sales Report">
    <t:reportPanel>
        <t:reportTable>
            <t:property name="filter.startDate"/>
            <t:property name="filter.endDate"/>
        </t:reportTable>
    </t:reportPanel>
</t:reportView>
```

---

## Panel Tags

### t:simplePanel

Basic panel with title and optional action bar.

```jsp
<t:simplePanel title="Customer Details">
    <t:property name="name"/>
    <t:property name="email"/>
</t:simplePanel>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `title` | String | Panel title |
| `sectionTitle` | String | Section header |
| `actionBarStyleClass` | String | Action bar CSS class |
| `panelStyleClass` | String | Panel CSS class |
| `titleStyleClass` | String | Title CSS class |

### t:filterPanel

Panel for filter/search forms with submit button.

```jsp
<t:filterPanel>
    <t:filterTable>
        <t:property name="filter.name"/>
        <t:property name="filter.status"/>
    </t:filterTable>
</t:filterPanel>
```

### t:formPanel

Panel for data entry with save button.

```jsp
<t:formPanel>
    <t:formTable>
        <t:property name="name"/>
        <t:property name="description"/>
        <t:property name="category"/>
    </t:formTable>
</t:formPanel>
```

### t:listPanel

Panel for data listing.

```jsp
<t:listPanel>
    <n:dataGrid itens="${items}" var="item">
        <t:property name="name"/>
        <t:property name="status"/>
    </n:dataGrid>
</t:listPanel>
```

### t:reportPanel

Panel for report filters.

```jsp
<t:reportPanel>
    <t:reportTable>
        <t:property name="filter.dateRange"/>
        <t:property name="filter.department"/>
    </t:reportTable>
</t:reportPanel>
```

### t:actionPanel

Container for action buttons.

```jsp
<t:actionPanel>
    <n:submit action="save">Save</n:submit>
    <n:submit action="cancel" type="link">Cancel</n:submit>
</t:actionPanel>
```

---

## Table Tags

Table tags provide grid layouts for properties.

### t:filterTable

2-column layout for filter forms.

```jsp
<t:filterTable>
    <t:property name="filter.name"/>
    <t:property name="filter.category"/>
    <t:property name="filter.status"/>
    <t:property name="filter.dateRange"/>
</t:filterTable>
```

### t:formTable

Layout for data entry forms.

```jsp
<t:formTable>
    <t:property name="name"/>
    <t:property name="description"/>
    <t:property name="price"/>
    <t:property name="quantity"/>
</t:formTable>
```

### t:listTable

Data listing with automatic CRUD links.

```jsp
<t:listTable>
    <t:property name="name"/>
    <t:property name="status"/>
    <t:property name="createdAt"/>
</t:listTable>
```

### t:reportTable

Layout for report filter forms.

```jsp
<t:reportTable>
    <t:property name="filter.startDate"/>
    <t:property name="filter.endDate"/>
    <t:property name="filter.format"/>
</t:reportTable>
```

---

## t:property

Smart property display/input tag. Automatically determines rendering based on bean metadata and context.

```jsp
<t:property name="username"/>
```

The framework automatically infers:
- **mode** (input/output) from the containing panel type
- **type** from the bean property type (String, Integer, Date, etc.)
- **label** from `@DisplayName` annotation or property name
- **renderAs** from the context (column in dataGrid, double in formTable, etc.)

### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | String | - | Property name (required) |
| `mode` | String | auto | `input` or `output` |
| `renderAs` | String | auto | `single`, `double`, `column`, `invert`, `stacked` |
| `type` | Object | auto | Input type override |
| `label` | String | auto | Custom label |
| `showLabel` | Boolean | varies | Show/hide label |
| `colspan` | Integer | 1 | Columns to span |
| `order` | String | - | Sort order for columns |

### renderAs Options

| Value | Description |
|-------|-------------|
| `single` | Value only, no label |
| `double` | Label and value side by side |
| `column` | As a dataGrid column |
| `invert` | Value then label |
| `stacked` | Label above value |

### Overriding Defaults

Most of the time, just use `name`. Override attributes only when needed:

#### Custom Label

```jsp
<t:property name="birthDate" label="Date of Birth"/>
```

#### Select Items

```jsp
<t:property name="category" itens="${categories}"/>
```

#### Output Formatting

```jsp
<t:property name="salary" pattern="$#,##0.00"/>
```

#### Spanning Columns

```jsp
<t:property name="description" colspan="2"/>
```

#### Column Sorting

```jsp
<n:dataGrid itens="${employees}" var="emp">
    <t:property name="name" order="name"/>
    <t:property name="department" order="department.name"/>
</n:dataGrid>
```

### Input-Specific Attributes

| Attribute | Description |
|-----------|-------------|
| `itens` | Items for select types |
| `includeBlank` | Include blank option |
| `blankLabel` | Label for blank option |
| `selectLabelProperty` | Property for option labels |
| `useAjax` | Enable AJAX loading |
| `reloadOnChange` | Reload form on change |
| `pattern` | Input mask pattern |
| `rows`, `cols` | Text area dimensions |

---

## t:propertyConfig

Configures defaults for nested t:property tags.

```jsp
<t:propertyConfig mode="input" renderAs="double" showLabel="true">
    <t:property name="name"/>
    <t:property name="email"/>
    <t:property name="phone"/>
</t:propertyConfig>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `mode` | String | Default mode (input/output) |
| `renderAs` | String | Default renderAs |
| `showLabel` | Boolean | Default showLabel |

---

## t:propertyLayout

Custom layout wrapper for a property.

```jsp
<t:propertyLayout>
    <div class="custom-field">
        <label>${label}</label>
        <div class="value">${value}</div>
    </div>
</t:propertyLayout>
```

---

## t:detail

Master-detail editor for collections.

```jsp
<t:detail name="items">
    <t:property name="product"/>
    <t:property name="quantity"/>
    <t:property name="unitPrice"/>
</t:detail>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `name` | String | Collection property name |
| `newLineButtonLabel` | String | Label for "add" button (optional) |
| `showDeleteButton` | Boolean | Show delete button per row |

### With Select Items

```jsp
<t:detail name="items">
    <t:property name="product" itens="${products}"/>
    <t:property name="quantity"/>
    <t:property name="unitPrice"/>
</t:detail>
```

### With Custom Columns

For more control over column rendering, use `n:column`:

```jsp
<t:detail name="items">
    <t:property name="product"/>
    <t:property name="quantity"/>
    <n:column header="Subtotal">
        <n:output value="${row.subtotal}" pattern="$#,##0.00"/>
    </n:column>
</t:detail>
```

---

## Complete Examples

### List Screen

```jsp
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>

<t:listView title="Products">

    <t:filterPanel>
        <t:filterTable>
            <t:property name="filter.name"/>
            <t:property name="filter.category"/>
            <t:property name="filter.active"/>
        </t:filterTable>
    </t:filterPanel>

    <t:listPanel>
        <n:dataGrid itens="${products}" var="product">
            <t:property name="name"/>
            <t:property name="category"/>
            <t:property name="price"/>
            <t:property name="active"/>
        </n:dataGrid>
    </t:listPanel>

</t:listView>
```

### Form Screen

```jsp
<t:formView title="Edit Product">

    <t:formPanel>
        <t:formTable>
            <t:property name="name"/>
            <t:property name="description"/>
            <t:property name="category" itens="${categories}"/>
            <t:property name="price"/>
            <t:property name="active"/>
        </t:formTable>
    </t:formPanel>

</t:formView>
```

### Master-Detail Screen

```jsp
<t:formView title="Edit Order">

    <t:formPanel>
        <t:formTable>
            <t:property name="number"/>
            <t:property name="customer" itens="${customers}"/>
            <t:property name="date"/>
            <t:property name="status" itens="${statuses}"/>
        </t:formTable>

        <t:detail name="items">
            <t:property name="product" itens="${products}"/>
            <t:property name="quantity"/>
            <t:property name="unitPrice"/>
        </t:detail>

        <t:simplePanel title="Totals">
            <t:property name="subtotal"/>
            <t:property name="tax"/>
            <t:property name="total"/>
        </t:simplePanel>
    </t:formPanel>

</t:formView>
```
