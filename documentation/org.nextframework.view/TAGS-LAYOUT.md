# Layout Tags

Tags for organizing content in grids, panels, and tabs.

---

## n:panelGrid

Grid layout container. Child elements are automatically arranged in cells.

```jsp
<n:panelGrid columns="2">
    <n:panel>Cell 1</n:panel>
    <n:panel>Cell 2</n:panel>
    <n:panel>Cell 3</n:panel>
    <n:panel>Cell 4</n:panel>
</n:panelGrid>
```

Renders:
```
┌─────────┬─────────┐
│ Cell 1  │ Cell 2  │
├─────────┼─────────┤
│ Cell 3  │ Cell 4  │
└─────────┴─────────┘
```

### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `columns` | Integer | 1 | Number of columns |
| `flatMode` | Boolean | false | Flat rendering (no table) |
| `styleClass` | String | - | CSS class for container |
| `style` | String | - | Inline style |
| `rowStyleClasses` | String | - | Comma-separated row classes |
| `columnStyleClasses` | String | - | Comma-separated column classes |
| `propertyRenderAs` | String | - | Default renderAs for nested t:property |

### Automatic Panel Wrapping

Tags inside `n:panelGrid` are automatically wrapped in panels:

```jsp
<n:panelGrid columns="2">
    <n:input name="firstName"/>  <!-- Auto-wrapped in panel -->
    <n:input name="lastName"/>   <!-- Auto-wrapped in panel -->
</n:panelGrid>
```

Equivalent to:
```jsp
<n:panelGrid columns="2">
    <n:panel><n:input name="firstName"/></n:panel>
    <n:panel><n:input name="lastName"/></n:panel>
</n:panelGrid>
```

### Panel Attributes in Context

When inside a panelGrid, use `panel*` prefix for panel attributes:

```jsp
<n:panelGrid columns="2">
    <n:input name="description" panelColspan="2"/>  <!-- Spans both columns -->
</n:panelGrid>
```

### Row and Column Styling

```jsp
<n:panelGrid columns="3"
             rowStyleClasses="odd,even"
             columnStyleClasses="col1,col2,col3">
    <!-- Alternating row styles, specific column styles -->
</n:panelGrid>
```

---

## n:panel

Single panel/cell within a panelGrid.

```jsp
<n:panel colspan="2" title="Description">
    <n:input name="description" type="text_area"/>
</n:panel>
```

### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `colspan` | Integer | 1 | Number of columns to span |
| `title` | String | - | Panel title/header |
| `styleClass` | String | - | CSS class |
| `style` | String | - | Inline style |
| `propertyRenderAs` | String | - | Default renderAs for nested t:property |

### Examples

#### Spanning Columns

```jsp
<n:panelGrid columns="3">
    <n:panel>Column 1</n:panel>
    <n:panel>Column 2</n:panel>
    <n:panel>Column 3</n:panel>
    <n:panel colspan="3">Full Width Row</n:panel>
</n:panelGrid>
```

#### With Title

```jsp
<n:panelGrid columns="1">
    <n:panel title="Personal Information">
        <n:input name="firstName"/>
        <n:input name="lastName"/>
    </n:panel>
    <n:panel title="Contact">
        <n:input name="email"/>
        <n:input name="phone"/>
    </n:panel>
</n:panelGrid>
```

---

## n:group

Fieldset with legend. Similar to panelGrid but renders as HTML fieldset.

```jsp
<n:group legend="Address" columns="2">
    <n:panel>
        <label>Street:</label>
        <n:input name="street"/>
    </n:panel>
    <n:panel>
        <label>City:</label>
        <n:input name="city"/>
    </n:panel>
</n:group>
```

Renders:
```html
<fieldset>
    <legend>Address</legend>
    <table>
        <tr>
            <td>Street: <input .../></td>
            <td>City: <input .../></td>
        </tr>
    </table>
</fieldset>
```

### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `legend` | String | - | Fieldset legend |
| `columns` | Integer | 1 | Number of columns |
| `flatMode` | Boolean | false | Flat rendering |
| `styleClass` | String | - | CSS class |
| `style` | String | - | Inline style |
| `propertyRenderAs` | String | - | Default renderAs for nested t:property |

### Example

```jsp
<n:group legend="Shipping Address" columns="2">
    <n:input name="shippingStreet" panelTitle="Street"/>
    <n:input name="shippingCity" panelTitle="City"/>
    <n:input name="shippingState" panelTitle="State"/>
    <n:input name="shippingZip" panelTitle="ZIP"/>
</n:group>

<n:group legend="Billing Address" columns="2">
    <n:input name="billingStreet" panelTitle="Street"/>
    <n:input name="billingCity" panelTitle="City"/>
    <n:input name="billingState" panelTitle="State"/>
    <n:input name="billingZip" panelTitle="ZIP"/>
</n:group>
```

---

## n:tabPanel

Tabbed interface. Each nested `n:panel` becomes a tab.

```jsp
<n:tabPanel>
    <n:panel title="General">
        <!-- General tab content -->
    </n:panel>
    <n:panel title="Details">
        <!-- Details tab content -->
    </n:panel>
    <n:panel title="Settings">
        <!-- Settings tab content -->
    </n:panel>
</n:tabPanel>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `renderUniqueTab` | Boolean | Render single tab without tab UI |
| `navPanelClass` | String | CSS class for navigation container |
| `navClass` | String | CSS class for nav items |
| `contentClass` | String | CSS class for content area |

### Example with Styling

```jsp
<n:tabPanel navPanelClass="nav nav-tabs"
            navClass="nav-link"
            contentClass="tab-content">
    <n:panel title="Basic Info">
        <t:property name="name" mode="input"/>
        <t:property name="description" mode="input"/>
    </n:panel>
    <n:panel title="Pricing">
        <t:property name="price" mode="input" type="money"/>
        <t:property name="cost" mode="input" type="money"/>
    </n:panel>
    <n:panel title="Inventory">
        <t:property name="stock" mode="input" type="integer"/>
        <t:property name="reorderPoint" mode="input" type="integer"/>
    </n:panel>
</n:tabPanel>
```

---

## n:modal

Modal dialog overlay.

```jsp
<n:modal id="confirmModal" visible="false">
    <h3>Confirm Action</h3>
    <p>Are you sure you want to proceed?</p>
    <button onclick="closeModal()">Cancel</button>
    <button onclick="confirm()">OK</button>
</n:modal>

<button onclick="showModal('confirmModal')">Delete</button>
```

### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `visible` | Boolean | Initial visibility |
| `overlayStyleClass` | String | CSS class for overlay |
| `panelStyleClass` | String | CSS class for panel |

---

## Layout Patterns

### Form with Sections

```jsp
<n:panelGrid columns="1">
    <n:group legend="Personal" columns="2">
        <n:input name="firstName"/>
        <n:input name="lastName"/>
        <n:input name="email" panelColspan="2"/>
    </n:group>

    <n:group legend="Address" columns="2">
        <n:input name="street" panelColspan="2"/>
        <n:input name="city"/>
        <n:input name="state"/>
        <n:input name="zip"/>
        <n:input name="country"/>
    </n:group>
</n:panelGrid>
```

### Side-by-Side Panels

```jsp
<n:panelGrid columns="2" columnStyleClasses="leftPanel,rightPanel">
    <n:panel>
        <h3>Summary</h3>
        <n:output value="${order.total}" pattern="$#,##0.00"/>
    </n:panel>
    <n:panel>
        <h3>Actions</h3>
        <n:submit action="approve">Approve</n:submit>
        <n:submit action="reject">Reject</n:submit>
    </n:panel>
</n:panelGrid>
```

### Responsive Layout

```jsp
<n:panelGrid columns="3" styleClass="responsive-grid">
    <n:panel styleClass="card">Card 1</n:panel>
    <n:panel styleClass="card">Card 2</n:panel>
    <n:panel styleClass="card">Card 3</n:panel>
</n:panelGrid>
```

With CSS:
```css
@media (max-width: 768px) {
    .responsive-grid td {
        display: block;
        width: 100%;
    }
}
```
