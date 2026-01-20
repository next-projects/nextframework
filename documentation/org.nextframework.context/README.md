# org.nextframework.context

## Overview

Core runtime context for Next Framework applications. Provides access to Spring beans, class scanning, utility methods, and request/application context.

```java
// Get any Spring bean from anywhere
MyService service = Next.getObject(MyService.class);

// Find all classes with an annotation
Class<?>[] entities = ClassManagerFactory.getClassManager()
    .getClassesWithAnnotation(Entity.class);

// Utility methods
Util.strings.isEmpty(value);
Util.dates.format(date, "dd/MM/yyyy");
```

---

## Next

Static access to Spring beans and context:

```java
// Get bean by class
MyService service = Next.getObject(MyService.class);

// Get bean by name
Object bean = Next.getObject("myBeanName");

// Get Spring's BeanFactory
DefaultListableBeanFactory factory = Next.getBeanFactory();

// Get MessageSource for i18n
MessageSource messages = Next.getMessageSource();

// Get application/request context
ApplicationContext appCtx = Next.getApplicationContext();
RequestContext reqCtx = Next.getRequestContext();
```

---

## ClassManager

Find classes by type or annotation (scans configured packages):

```java
ClassManager cm = ClassManagerFactory.getClassManager();

// All classes of a type
Class<?>[] daos = cm.getAllClassesOfType(GenericDAO.class);

// All classes with annotation
Class<?>[] entities = cm.getClassesWithAnnotation(Entity.class);
Class<?>[] controllers = cm.getClassesWithAnnotation(Controller.class);

// All registered classes
Class<?>[] all = cm.getAllClasses();
```

---

## Util

Utility methods organized by type:

```java
// Strings
Util.strings.isEmpty(value);
Util.strings.toUncapitalize("MyClass");  // "myClass"

// Dates
Util.dates.format(date, "dd/MM/yyyy");
Util.dates.parse("25/12/2024", "dd/MM/yyyy");

// Numbers
Util.numbers.parseInt(str, defaultValue);

// Booleans
Util.booleans.isTrue(value);

// Collections
Util.collections.isEmpty(list);

// Objects
Util.objects.equals(a, b);

// Beans (reflection)
Util.beans.getProperty(obj, "name");
Util.beans.setProperty(obj, "name", value);

// Exceptions
Util.exceptions.getRootCause(exception);
```

---

## Exceptions

Framework exception hierarchy:

| Exception | Usage |
|-----------|-------|
| `NextException` | Base exception |
| `BusinessException` | Business rule violations (shown to user) |
| `ApplicationException` | Application errors |
| `ConfigurationException` | Configuration problems |
