# Data Display Tags

Tags for displaying tabular data.

---

## n:dataGrid

Renders collections as HTML tables with support for sorting, grouping, and custom columns.

```jsp
<n:dataGrid itens="${products}" var="product">
    <n:column header="Name">
        <n:output value="${product.name}"/>
    </n:column>
    <n:column header="Price">
        <n:output value="${product.price}" pattern="$#,##0.00"/>
    </n:column>
</n:dataGrid>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `itens` | Collection | Data source |
| `var` | String | Variable name for current row |
| `varIndex` | String | Variable name for row index |
| `varStatus` | String | Variable name for iteration status |
| `headerStyleClass` | String | CSS class for header row |
| `bodyStyleClasses` | String | Comma-separated body row classes |
| `bodyStyles` | String | Comma-separated body row styles |
| `footerStyleClass` | String | CSS class for footer row |
| `groupProperty` | String | Property for row grouping |
| `indexProperty` | String | Property for row ordering |

### Basic Example

```jsp
<n:dataGrid itens="${orders}" var="order" varIndex="i">
    <n:column header="#">
        <n:output value="${i + 1}"/>
    </n:column>
    <n:column header="Order #">
        <n:output value="${order.number}"/>
    </n:column>
    <n:column header="Customer">
        <n:output value="${order.customer.name}"/>
    </n:column>
    <n:column header="Total">
        <n:output value="${order.total}" pattern="$#,##0.00"/>
    </n:column>
    <n:column header="Status">
        <n:output value="${order.status}"/>
    </n:column>
</n:dataGrid>
```

### Row Styling

#### Alternating Rows

```jsp
<n:dataGrid itens="${items}"
            bodyStyleClasses="oddRow,evenRow">
    ...
</n:dataGrid>
```

#### Conditional Row Styling

```jsp
<n:dataGrid itens="${orders}" var="order">
    <n:column header="Status" bodyStyleClass="${order.overdue ? 'overdue' : ''}">
        <n:output value="${order.status}"/>
    </n:column>
</n:dataGrid>
```

### Grouping

Group rows by a property value:

```jsp
<n:dataGrid itens="${employees}" var="emp" groupProperty="department">
    <n:column header="Name">
        <n:output value="${emp.name}"/>
    </n:column>
    <n:column header="Position">
        <n:output value="${emp.position}"/>
    </n:column>
</n:dataGrid>
```

Renders:
```
┌────────────────────────────────────┐
│ Engineering                        │ (group header)
├──────────────┬─────────────────────┤
│ John Smith   │ Senior Developer    │
│ Jane Doe     │ Tech Lead           │
├────────────────────────────────────┤
│ Sales                              │ (group header)
├──────────────┬─────────────────────┤
│ Bob Wilson   │ Account Manager     │
└──────────────┴─────────────────────┘
```

### With Actions Column

```jsp
<n:dataGrid itens="${products}" var="product">
    <n:column header="Name">
        <n:output value="${product.name}"/>
    </n:column>
    <n:column header="Actions">
        <n:link url="/products?action=edit&id=${product.id}">Edit</n:link>
        <n:link url="/products?action=delete&id=${product.id}"
                confirmationMessage="Delete this product?">Delete</n:link>
    </n:column>
</n:dataGrid>
```

### Empty Data

```jsp
<n:dataGrid itens="${items}" var="item">
    <n:column header="Name">
        <n:output value="${item.name}"/>
    </n:column>
</n:dataGrid>

<c:if test="${empty items}">
    <p>No items found.</p>
</c:if>
```

---

## n:column

Defines a column within a dataGrid.

```jsp
<n:column header="Product Name" order="name">
    <n:output value="${row.name}"/>
</n:column>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `header` | String | Column header text |
| `order` | Integer/String | Column order or sort property |
| `headerStyleClass` | String | CSS class for header cell |
| `headerStyle` | String | Inline style for header |
| `bodyStyleClass` | String | CSS class for body cells |
| `bodyStyle` | String | Inline style for body cells |
| `footerStyleClass` | String | CSS class for footer cell |
| `footerStyle` | String | Inline style for footer |

### Column Sections

Use nested tags for header, body, and footer:

```jsp
<n:column>
    <n:header>
        <strong>Total</strong>
    </n:header>
    <n:body>
        <n:output value="${row.amount}"/>
    </n:body>
    <n:footer>
        <n:output value="${totalAmount}"/>
    </n:footer>
</n:column>
```

### Sortable Column

```jsp
<n:column header="Name" order="name">
    <n:output value="${row.name}"/>
</n:column>
```

The `order` attribute enables sorting by clicking the header.

---

## n:header, n:body, n:footer

Column section tags for custom content.

```jsp
<n:dataGrid itens="${sales}" var="sale">
    <n:column>
        <n:header>
            <span class="sortable">Amount</span>
        </n:header>
        <n:body>
            <n:output value="${sale.amount}" pattern="$#,##0.00"/>
        </n:body>
        <n:footer>
            <strong>Total: <n:output value="${totalSales}" pattern="$#,##0.00"/></strong>
        </n:footer>
    </n:column>
</n:dataGrid>
```

---

## n:dataGridOptionalColumns

Allows users to show/hide columns.

```jsp
<n:dataGrid itens="${items}" var="item">
    <n:dataGridOptionalColumns cacheKey="myGridColumns"/>

    <n:column header="ID">
        <n:output value="${item.id}"/>
    </n:column>
    <n:column header="Name">
        <n:output value="${item.name}"/>
    </n:column>
    <n:column header="Description">
        <n:output value="${item.description}"/>
    </n:column>
</n:dataGrid>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `cacheKey` | String | Key for saving column preferences |

---

## Patterns

### Editable Grid

```jsp
<n:form>
    <n:dataGrid itens="${items}" var="item" varIndex="i">
        <n:bean name="item" propertyPrefix="items" propertyIndex="${i}">
            <n:column header="Name">
                <n:input name="name"/>
            </n:column>
            <n:column header="Quantity">
                <n:input name="quantity" type="integer"/>
            </n:column>
            <n:column header="Price">
                <n:input name="price" type="money"/>
            </n:column>
        </n:bean>
    </n:dataGrid>
    <n:submit action="saveAll">Save All</n:submit>
</n:form>
```

### Grid with Totals

```jsp
<n:dataGrid itens="${orderItems}" var="item">
    <n:column header="Product">
        <n:body><n:output value="${item.product.name}"/></n:body>
    </n:column>
    <n:column header="Quantity">
        <n:body><n:output value="${item.quantity}"/></n:body>
        <n:footer><strong>${totalQuantity}</strong></n:footer>
    </n:column>
    <n:column header="Subtotal">
        <n:body><n:output value="${item.subtotal}" pattern="$#,##0.00"/></n:body>
        <n:footer><strong><n:output value="${orderTotal}" pattern="$#,##0.00"/></strong></n:footer>
    </n:column>
</n:dataGrid>
```

### Master-Detail in Grid

```jsp
<n:dataGrid itens="${orders}" var="order">
    <n:column header="Order #">
        <n:output value="${order.number}"/>
    </n:column>
    <n:column header="Items">
        <n:dataGrid itens="${order.items}" var="item">
            <n:column header="Product">
                <n:output value="${item.product.name}"/>
            </n:column>
            <n:column header="Qty">
                <n:output value="${item.quantity}"/>
            </n:column>
        </n:dataGrid>
    </n:column>
</n:dataGrid>
```

### Conditional Column Content

```jsp
<n:dataGrid itens="${tasks}" var="task">
    <n:column header="Status">
        <c:choose>
            <c:when test="${task.completed}">
                <span class="completed">Done</span>
            </c:when>
            <c:when test="${task.overdue}">
                <span class="overdue">Overdue!</span>
            </c:when>
            <c:otherwise>
                <span class="pending">Pending</span>
            </c:otherwise>
        </c:choose>
    </n:column>
</n:dataGrid>
```
