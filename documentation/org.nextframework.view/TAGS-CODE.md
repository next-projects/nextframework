# Code Tags (Reusable Templates)

Define reusable template fragments within a page using the `code:` tag library.

```jsp
<%@ taglib prefix="code" uri="code"%>
```

---

**Example of use:**

File: `samples/showcase_app/WebContent/WEB-INF/jsp/public/home.jsp`

```jsp
<code:class>
    <code:method name="featureCard">
        <div class="col-lg-4 col-md-6">
            <div class="feature-card p-4">
                <div class="feature-icon mb-3">
                    <i class="bi ${icon}"></i>
                </div>
                <h5 class="fw-bold">${title}</h5>
                <p class="text-muted mb-0">${CALL_BODY}</p>
            </div>
        </div>
    </code:method>

    <code:main>
        <code:call method="featureCard" icon="bi-people" title="Customer Management">
            Keep track of all your customers in one organized place.
        </code:call>
    </code:main>
</code:class>
```

## Basic Structure

```jsp
<code:class>
    <code:method name="renderCard">
        <div class="card">
            <h3>${title}</h3>
            <p>${description}</p>
        </div>
    </code:method>

    <code:main>
        <code:call method="renderCard" title="First Card" description="Content 1"/>
        <code:call method="renderCard" title="Second Card" description="Content 2"/>
        <code:call method="renderCard" title="Third Card" description="Content 3"/>
    </code:main>
</code:class>
```

---

## Tags

| Tag | Description |
|-----|-------------|
| `code:class` | Container for methods and main content |
| `code:main` | Default method executed when the class runs |
| `code:method` | Defines a reusable template fragment |
| `code:call` | Invokes a method with parameters |

---

## How It Works

1. Parameters passed to `code:call` become page context attributes
2. Inside the method body, access parameters using `${paramName}`
3. The `code:main` block runs automatically after methods are registered

---

## Examples

### Reusable Form Section

```jsp
<code:class>
    <code:method name="addressSection">
        <t:simplePanel title="${sectionTitle}">
            <t:property name="${prefix}street"/>
            <t:property name="${prefix}city"/>
            <t:property name="${prefix}state"/>
            <t:property name="${prefix}zipCode"/>
        </t:simplePanel>
    </code:method>

    <code:main>
        <t:formView title="Edit Customer">
            <t:formPanel>
                <t:formTable>
                    <t:property name="name"/>
                    <t:property name="email"/>
                </t:formTable>

                <code:call method="addressSection"
                           sectionTitle="Shipping Address"
                           prefix="shipping"/>

                <code:call method="addressSection"
                           sectionTitle="Billing Address"
                           prefix="billing"/>
            </t:formPanel>
        </t:formView>
    </code:main>
</code:class>
```

### Repeated Data Display

```jsp
<code:class>
    <code:method name="statBox">
        <div class="stat-box ${styleClass}">
            <span class="value">${value}</span>
            <span class="label">${label}</span>
        </div>
    </code:method>

    <code:main>
        <div class="dashboard">
            <code:call method="statBox" label="Total Sales" value="${totalSales}" styleClass="highlight"/>
            <code:call method="statBox" label="Orders" value="${orderCount}"/>
            <code:call method="statBox" label="Customers" value="${customerCount}"/>
        </div>
    </code:main>
</code:class>
```
