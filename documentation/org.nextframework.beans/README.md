# org.nextframework.beans

## Overview

Bean introspection with support for display names and description properties. Simplifies reflection with a cleaner API and adds metadata for UI generation (form labels, dropdown displays, grid headers).

```java
@DisplayName("Customer")
public class Customer {

    @DisplayName("Full Name")
    @DescriptionProperty
    public String getName() { return name; }
}

BeanDescriptor bd = BeanDescriptorFactory.forBean(customer);
bd.getDisplayName();       // "Customer"
bd.getDescription();       // value of getName()
bd.getPropertyDescriptor("name").getDisplayName();  // "Full Name"
bd.getPropertyDescriptor("name").getValue();        // "John Doe"
```

---

## BeanDescriptor

Introspect a bean or class:

```java
// From an instance
BeanDescriptor bd = BeanDescriptorFactory.forBean(customer);

// From a class
BeanDescriptor bd = BeanDescriptorFactory.forClass(Customer.class);

// Get properties
PropertyDescriptor[] props = bd.getPropertyDescriptors();
PropertyDescriptor nameProp = bd.getPropertyDescriptor("name");

// Get values
Object id = bd.getId();                    // Value of getId()
Object desc = bd.getDescription();         // Value of @DescriptionProperty
String display = bd.getDisplayName();      // @DisplayName or class name
```

---

## PropertyDescriptor

Access property metadata:

```java
PropertyDescriptor prop = bd.getPropertyDescriptor("email");

prop.getName();          // "email"
prop.getValue();         // property value
prop.getDisplayName();   // @DisplayName or property name
prop.getRawType();       // String.class
prop.getType();          // generic type
prop.getAnnotations();   // all annotations
prop.getAnnotation(Email.class);  // specific annotation
```

---

## Annotations

### @DisplayName

Human-readable label for classes or properties:

```java
@DisplayName("Product Category")
public class Category {

    @DisplayName("Category Name")
    public String getName() { ... }

    @DisplayName("Parent Category")
    public Category getParent() { ... }
}
```

### @DescriptionProperty

Marks the property that best describes the bean (used in dropdowns, labels, etc.):

```java
public class Employee {

    @DescriptionProperty
    public String getName() { return name; }

    // Or combine multiple fields
    @DescriptionProperty(usingFields = {"firstName", "lastName"})
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
```
