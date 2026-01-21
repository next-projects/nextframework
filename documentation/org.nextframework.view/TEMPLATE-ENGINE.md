# Template Engine

How tags render HTML through JSP templates and how to customize them.

---

## Overview

Most tags delegate their HTML rendering to JSP template files. This separation allows:

- Clean tag logic (Java) vs. presentation (JSP)
- Easy customization by overriding templates
- Type-specific rendering (e.g., different templates for text vs. select inputs)

```
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│   Tag Class     │ ──→  │  Template JSP   │ ──→  │   HTML Output   │
│   (Java)        │      │   (JSP/EL)      │      │                 │
└─────────────────┘      └─────────────────┘      └─────────────────┘

Example:
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│   InputTag      │ ──→  │ InputTag-text   │ ──→  │ <input type=    │
│   type="text"   │      │    .jsp         │      │   "text" .../>  │
└─────────────────┘      └─────────────────┘      └─────────────────┘
```

---

## Template Location

### In Framework JAR

Templates are stored alongside their tag classes:

```
org.nextframework.view.jar
├── org/nextframework/view/
│   ├── InputTag.class
│   ├── InputTag-text.jsp
│   ├── InputTag-select_one.jsp
│   ├── InputTag-checkbox.jsp
│   ├── FormTag.class
│   ├── FormTag.jsp
│   └── ...
└── org/nextframework/view/template/
    ├── PropertyTag.class
    ├── PropertyTag.jsp
    └── ...
```

### At Runtime

On first use, templates are extracted to the webapp:

```
/WEB-INF/classes/org/nextframework/view/InputTag-text.jsp
/WEB-INF/classes/org/nextframework/view/FormTag.jsp
/WEB-INF/classes/org/nextframework/view/template/PropertyTag.jsp
```

---

## Template Naming Convention

### Simple Template

```
{FullyQualifiedClassName}.jsp
```

Example: `org/nextframework/view/FormTag.jsp`

### Type-Specific Template

```
{FullyQualifiedClassName}-{suffix}.jsp
```

Examples:
- `org/nextframework/view/InputTag-text.jsp`
- `org/nextframework/view/InputTag-select_one.jsp`
- `org/nextframework/view/InputTag-checkbox.jsp`
- `org/nextframework/view/LinkTag-button.jsp`
- `org/nextframework/view/SubmitTag-link.jsp`

---

## How Templates Work

### Tag Invokes Template

```java
public class FormTag extends BaseTag {

    @Override
    protected void doComponent() throws Exception {
        // Tag logic here...

        // Delegate to template
        includeJspTemplate();  // Uses FormTag.jsp
    }
}

public class InputTag extends BaseTag {

    @Override
    protected void doComponent() throws Exception {
        // Determine input type
        String type = getType();  // e.g., "text", "select_one"

        // Delegate to type-specific template
        includeJspTemplate(type);  // Uses InputTag-{type}.jsp
    }
}
```

### Template Access to Tag

In the template, the tag instance is available as `${tag}`:

```jsp
<%-- InputTag-text.jsp --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>

<input
    id="${tag.id}"
    name="${tag.name}"
    type="text"
    value="${tag.valueToString}"
    ${tag.dynamicAttributesToString}/>

<c:if test="${!empty tag.pattern}">
    <script>
        IMask(document.getElementById("${tag.id}"), {
            mask: '${tag.pattern}'
        });
    </script>
</c:if>
```

### Available Template Variables

| Variable | Description |
|----------|-------------|
| `${tag}` | The tag instance |
| `${tag.id}` | Tag ID |
| `${tag.name}` | Input name |
| `${tag.value}` | Raw value |
| `${tag.valueToString}` | Formatted value |
| `${tag.dynamicAttributesMap}` | Map of dynamic attributes |
| `${tag.dynamicAttributesToString}` | Rendered HTML attributes |
| `${tag.*}` | Any tag property via getter |

### Including Tag Body

Templates can invoke the tag's body content:

```jsp
<%-- Wrapper template example --%>
<div class="wrapper">
    <n:doBody/>  <%-- Renders child content --%>
</div>
```

---

## Template Extraction Process

```
1. Tag calls includeJspTemplate()
       │
       ▼
2. BaseTagTemplateManager.checkTemplate()
       │
       ├─► Template exists in webapp? → Use it
       │
       └─► Template missing?
               │
               ▼
           3. Search JAR files in /WEB-INF/lib
               │
               ▼
           4. Extract template from JAR
               │
               ▼
           5. Write to /WEB-INF/classes/{package}/{template}.jsp
               │
               ▼
           6. Include template via RequestDispatcher
```

---

## Customizing Templates

### Method 1: Override in Webapp

Copy the template from the framework and place in your webapp:

1. Find the original template in the framework JAR
2. Copy to `/WEB-INF/classes/{same-path}`
3. Modify as needed

```
Your webapp:
/WEB-INF/classes/org/nextframework/view/InputTag-text.jsp  ← Your custom version
```

The framework will use your version instead of extracting from JAR.

### Method 2: Extend Tag Class

Create a subclass with a different template:

```java
package com.myapp.view;

public class MyInputTag extends InputTag {

    @Override
    protected String getTemplateName() {
        // Use custom template path
        return "com/myapp/view/MyInputTag";
    }
}
```

Create template at:
```
/WEB-INF/classes/com/myapp/view/MyInputTag-text.jsp
```

Register in your TLD:
```xml
<tag>
    <name>input</name>
    <tag-class>com.myapp.view.MyInputTag</tag-class>
    ...
</tag>
```

### Method 3: Custom Tag with New Template

Create a completely new tag:

```java
package com.myapp.view;

public class CustomPanelTag extends BaseTag {

    private String title;

    @Override
    protected void doComponent() throws Exception {
        includeJspTemplate();  // Uses CustomPanelTag.jsp
    }

    // Getters/setters...
}
```

Create template at:
```
/WEB-INF/classes/com/myapp/view/CustomPanelTag.jsp
```

```jsp
<%-- CustomPanelTag.jsp --%>
<div class="custom-panel">
    <h3>${tag.title}</h3>
    <div class="content">
        <n:doBody/>
    </div>
</div>
```

---

## Template Types

### JSP Templates (Most Common)

Full JSP with tag libraries and EL:

```jsp
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="n" uri="http://www.nextframework.org/tag-lib/next"%>

<div id="${tag.id}" class="${tag.styleClass}">
    <c:if test="${tag.showHeader}">
        <div class="header">${tag.title}</div>
    </c:if>
    <div class="body">
        <n:doBody/>
    </div>
</div>
```

### Text Templates (Simple)

For simple tags, text templates use EL with `<dobody/>` marker:

```
<span id="${tag.id}">${tag.label}: <dobody/></span>
```

The `<dobody/>` marker splits the template into before/after body sections.

Invoked via `includeTextTemplate()` instead of `includeJspTemplate()`.

---

## InputTag Templates

InputTag has the most templates, one per input type:

| Type | Template |
|------|----------|
| text | InputTag-text.jsp |
| password | InputTag-password.jsp |
| hidden | InputTag-hidden.jsp |
| checkbox | InputTag-checkbox.jsp |
| select_one | InputTag-select_one.jsp |
| select_many | InputTag-select_many.jsp |
| date | InputTag-date.jsp |
| time | InputTag-time.jsp |
| money | InputTag-money.jsp |
| integer | InputTag-integer.jsp |
| float | InputTag-float.jsp |
| text_area | InputTag-text_area.jsp |
| file | InputTag-file.jsp |
| suggest | InputTag-suggest.jsp |
| cpf | InputTag-cpf.jsp |
| cnpj | InputTag-cnpj.jsp |
| cep | InputTag-cep.jsp |
| phone | InputTag-phone.jsp |

---

## ViewConfig Integration

Default style classes can be configured via `ViewConfig`:

```java
public class MyViewConfig extends DefaultViewConfig {

    @Override
    public String getDefaultStyleClass(Class<? extends BaseTag> tagClass, String field) {
        if (tagClass == InputTag.class && "class".equals(field)) {
            return "form-control";  // Bootstrap styling
        }
        return super.getDefaultStyleClass(tagClass, field);
    }
}
```

Tags apply these defaults in `applyDefaultStyleClasses()`.

---

## Debugging Templates

### Check Extracted Templates

Look in `/WEB-INF/classes/org/nextframework/view/` to see extracted templates.

### Template Not Found

If a template is not found, you'll see:

```
NextException: Template for org/nextframework/view/MyTag.jsp not found
```

Ensure your tag class's JAR is in `/WEB-INF/lib` and `isTagFromJar()` returns true for it.

### Override isTagFromJar()

If your custom tags are in a different JAR:

```java
@Override
protected boolean isTagFromJar(String resourcePath) {
    return resourcePath.contains("next") || resourcePath.contains("myapp");
}
```

---

## Best Practices

1. **Don't modify extracted templates directly** - They may be overwritten. Copy to your own path instead.

2. **Keep templates simple** - Complex logic belongs in the tag class, not the template.

3. **Use tag properties** - Expose configuration via tag attributes, access via `${tag.property}`.

4. **Test template changes** - Delete `/WEB-INF/classes/org/nextframework/` to force re-extraction.

5. **Consider ViewConfig** - For styling changes, configure defaults rather than overriding templates.
