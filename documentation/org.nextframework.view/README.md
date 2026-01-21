# org.nextframework.view

## Overview

Comprehensive JSP tag library framework providing UI components for web applications. Features automatic template rendering, bean introspection, AJAX support, and high-level view abstractions.

```
┌─────────────────────────────────────────────────────────────────┐
│  Tag Libraries                                                   │
├─────────────────────────────────────────────────────────────────┤
│  n:  (next.tld)      Core tags - forms, inputs, grids, layout   │
│  t:  (template.tld)  High-level views - panels, tables, CRUD    │
│  ajax: (ajax.tld)    AJAX functionality                          │
│  combo: (combo.tld)  Convenience composite tags                  │
│  code: (code.tld)    Reusable template fragments                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Quick Start

### Include Tag Library

```jsp
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>
<%@ taglib prefix="t" uri="http://www.nextframework.org/tag-lib/template"%>
```

### Basic Form

```jsp
<n:form action="save">
    <n:panelGrid columns="2">
        <n:bean name="product">
            <n:panel><n:output value="${product.name}"/></n:panel>
            <n:panel><n:input name="price"/></n:panel>
        </n:bean>
    </n:panelGrid>
    <n:submit action="save">Save</n:submit>
</n:form>
```

### High-Level Template View

```jsp
<t:view title="Products">
    <t:simplePanel title="Edit Product">
        <t:formTable>
            <t:property name="name"/>
            <t:property name="price"/>
            <!-- items inferred from property type if it's an entity or enum -->
            <t:property name="category"/>
        </t:formTable>
    </t:simplePanel>
</t:view>
```

---

## Architecture

### Tag Hierarchy

```
javax.servlet.jsp.tagext.SimpleTagSupport
  └─ BaseTag (framework foundation)
       ├─ Form Tags (FormTag, BeanTag, InputTag, OutputTag)
       ├─ Layout Tags (PanelGridTag, PanelTag, GroupTag, TabPanelTag)
       ├─ Data Tags (DataGridTag, ColumnTag)
       ├─ Navigation Tags (LinkTag, SubmitTag, MenuTag)
       ├─ LogicalTag (interface)
       │    └─ ComboTag
       │         └─ TemplateTag (base for t: tags)
       └─ Utility Tags (HeadTag, MessagesTag, etc.)
```

### Template Engine

Each tag can delegate rendering to a JSP template file:

```
Tag Class                    Template File
─────────────────────────────────────────────────
InputTag (type=text)    →    InputTag-text.jsp
InputTag (type=select)  →    InputTag-select_one.jsp
FormTag                 →    FormTag.jsp
PropertyTag             →    PropertyTag.jsp
```

Templates are:
- Stored in the framework JAR alongside tag classes
- Auto-extracted to `/WEB-INF/classes/` on first use
- Can be overridden by placing custom versions in your webapp

See [TEMPLATE-ENGINE.md](TEMPLATE-ENGINE.md) for details.

---

## Tag Libraries

### Core Tags (n:)

| Tag | Description | Documentation |
|-----|-------------|---------------|
| `n:form` | HTML form with validation | [TAGS-FORM.md](TAGS-FORM.md) |
| `n:bean` | Bean context for property binding | [TAGS-FORM.md](TAGS-FORM.md) |
| `n:input` | Input field (30+ types) | [TAGS-FORM.md](TAGS-FORM.md) |
| `n:output` | Formatted output display | [TAGS-FORM.md](TAGS-FORM.md) |
| `n:panelGrid` | Grid layout container | [TAGS-LAYOUT.md](TAGS-LAYOUT.md) |
| `n:panel` | Single panel/cell | [TAGS-LAYOUT.md](TAGS-LAYOUT.md) |
| `n:group` | Fieldset with legend | [TAGS-LAYOUT.md](TAGS-LAYOUT.md) |
| `n:tabPanel` | Tabbed interface | [TAGS-LAYOUT.md](TAGS-LAYOUT.md) |
| `n:dataGrid` | Data table/grid | [TAGS-DATA.md](TAGS-DATA.md) |
| `n:column` | Table column | [TAGS-DATA.md](TAGS-DATA.md) |
| `n:link` | Hyperlink | [TAGS-NAVIGATION.md](TAGS-NAVIGATION.md) |
| `n:submit` | Form submit button | [TAGS-NAVIGATION.md](TAGS-NAVIGATION.md) |
| `n:menu` | Menu rendering | [TAGS-NAVIGATION.md](TAGS-NAVIGATION.md) |
| `n:head` | Include CSS/JS resources | Utility |
| `n:messages` | Display messages | Utility |

### Template Tags (t:)

High-level view composition for CRUD screens:

| Tag | Description |
|-----|-------------|
| `t:view` | Base view container |
| `t:simplePanel` | Panel with title |
| `t:filterPanel` | Filter/search panel |
| `t:formPanel` | Data entry panel |
| `t:listPanel` | List display panel |
| `t:property` | Smart property display/input |
| `t:filterTable` | Filter form layout |
| `t:formTable` | Data entry form layout |
| `t:listTable` | Data listing with CRUD links |

See [TAGS-TEMPLATE.md](TAGS-TEMPLATE.md) for details.

### AJAX Tags (ajax:)

| Tag | Description |
|-----|-------------|
| `ajax:call` | Generate AJAX function |

See [TAGS-AJAX.md](TAGS-AJAX.md) for details.

---

## Common Patterns

### Data Entry Form

```jsp
<t:view title="Edit Product">
    <t:formPanel>
        <t:formTable>
            <t:property name="name"/>
            <t:property name="description"/>
            <t:property name="price"/>
            <!-- items inferred from property type (entity/enum) -->
            <t:property name="category"/>
        </t:formTable>
    </t:formPanel>
</t:view>
```

### List with Filter

```jsp
<t:view title="Products">
    <t:filterPanel>
        <t:filterTable>
            <t:property name="filter.name"/>
            <t:property name="filter.category"/>
        </t:filterTable>
    </t:filterPanel>

    <t:listPanel>
        <n:dataGrid itens="${products}">
            <t:property name="name"/>
            <t:property name="price"/>
        </n:dataGrid>
    </t:listPanel>
</t:view>
```

### Master-Detail

```jsp
<t:formPanel>
    <t:formTable>
        <t:property name="customer"/>
        <t:property name="date"/>
    </t:formTable>

    <t:detail name="items">
        <t:property name="product"/>
        <t:property name="quantity"/>
    </t:detail>
</t:formPanel>
```

---

## Common Attributes

### All Tags

| Attribute | Description |
|-----------|-------------|
| `id` | Element ID |
| `rendered` | Conditional rendering (boolean) |
| `bypass` | Skip tag, render only body |
| Dynamic attributes | Pass-through to HTML (class, style, etc.) |

### Style Customization

Tags support dynamic HTML attributes:

```jsp
<n:input name="price" class="form-control" style="width: 100px"/>
<n:panel class="highlight" style="background: yellow"/>
```

---

## Files

- [TEMPLATE-ENGINE.md](TEMPLATE-ENGINE.md) - Template system and customization
- [TAGS-FORM.md](TAGS-FORM.md) - Form, bean, input, output tags
- [TAGS-LAYOUT.md](TAGS-LAYOUT.md) - Layout tags (panelGrid, group, tabPanel)
- [TAGS-DATA.md](TAGS-DATA.md) - DataGrid and column tags
- [TAGS-NAVIGATION.md](TAGS-NAVIGATION.md) - Link, submit, menu, pagination
- [TAGS-TEMPLATE.md](TAGS-TEMPLATE.md) - High-level template tags (t:)
- [TAGS-AJAX.md](TAGS-AJAX.md) - AJAX functionality
- [TAGS-CODE.md](TAGS-CODE.md) - Reusable template fragments (code:)

---

## Dependencies

- `org.nextframework.chart` - Chart rendering
- `org.nextframework.controller` - Controller integration
- `org.nextframework.persistence` - Entity support
- `org.nextframework.web` - Web context
- Servlet API
- JSTL
