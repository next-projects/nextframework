# org.nextframework.services

## Overview

Service locator with pluggable providers. Services can be loaded from multiple sources (Java ServiceLoader, static registration, ServletContext) with priority-based ordering.

```java
// Get any service from anywhere - no reference needed
MyService service = ServiceFactory.getService(MyService.class);
```

---

## Basic Usage

### Getting a Service

```java
import org.nextframework.service.ServiceFactory;

// Get a service by interface
MyService service = ServiceFactory.getService(MyService.class);

// Get all implementations of an interface
MyService[] services = ServiceFactory.loadServices(MyService.class);
```

---

## Registering Services

### Registering a Service Programmatically

```java
import org.nextframework.service.StaticServiceProvider;

// Register a service implementation
StaticServiceProvider.registerService(MyService.class, new MyServiceImpl());
```

### Registering in Web Applications

In web applications, services can be stored in `ServletContext` attributes using `ServletContextServiceProvider` (from the `web` module). This provider has the highest priority (30), so services registered here take precedence.

```java
import org.nextframework.web.service.ServletContextServiceProvider;

// With explicit ServletContext
ServletContextServiceProvider.registerService(servletContext, MyService.class, new MyServiceImpl());

// Using current WebContext (must be in a web request)
ServletContextServiceProvider.registerService(MyService.class, new MyServiceImpl());
```

The framework uses this mechanism to register Spring context services:

```java
// From NextWebApplicationContext - registers Spring beans as services
ServletContextServiceProvider.registerService(servletContext, ApplicationContext.class, this);
ServletContextServiceProvider.registerService(servletContext, BeanFactory.class, this);
ServletContextServiceProvider.registerService(servletContext, MessageSource.class, this);
```

This allows accessing Spring's `ApplicationContext` via `ServiceFactory`:

```java
ApplicationContext ctx = ServiceFactory.getService(ApplicationContext.class);
```

---

### Using Java ServiceLoader

Create a file `META-INF/services/com.example.MyService` containing:

```
com.example.MyServiceImpl
```

The service will be automatically discovered by the `JavaLoaderServiceProvider`.

---

## Priority

When multiple providers can supply a service, the one with the **lowest priority number** wins:

| Provider | Module | Priority | Description |
|----------|--------|----------|-------------|
| ServletContextServiceProvider | web | 30 | Services stored in `ServletContext` attributes |
| StaticServiceProvider | services | 40 | Manual registration via `registerService()` |
| JavaLoaderServiceProvider | services | 50 | Java ServiceLoader mechanism |
| LowPriorityStaticServiceProvider | services | 100 | Fallback static registration |

Custom providers can define their own priority by implementing the `priority()` method.

---

## Creating a Custom Provider

```java
import org.nextframework.service.ServiceProvider;
import org.nextframework.service.ServiceFactory;

public class MyProvider implements ServiceProvider {

    @Override
    public <E> E getService(Class<E> serviceInterface) {
        // Return service or null if not found
    }

    @Override
    public <E> E[] loadServices(Class<E> serviceInterface) {
        // Return all services for this interface
    }

    @Override
    public int priority() {
        return 30; // Higher priority than default providers
    }

    @Override
    public void release() {
        // Cleanup resources
    }
}

// Register the provider
ServiceFactory.registerProvider(new MyProvider());
```

Or register via Java ServiceLoader by creating `META-INF/services/org.nextframework.service.ServiceProvider`.
