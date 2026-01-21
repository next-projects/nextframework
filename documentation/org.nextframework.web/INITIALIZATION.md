# Web Initialization & Bootstrap

Automatic Spring context setup and component scanning.

## Auto-Configuration

The module uses `web-fragment.xml` for zero-configuration setup:

```xml
<!-- Automatically loaded by Servlet 3.0+ containers -->
<web-fragment>
    <name>next_web</name>

    <filter>
        <filter-name>WebContextFilter</filter-name>
        <filter-class>org.nextframework.web.WebContextFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>WebContextFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <listener>
        <listener-class>org.nextframework.web.WebContextFilter</listener-class>
    </listener>
    <listener>
        <listener-class>org.nextframework.web.context.MemoryLeakDestroyerListener</listener-class>
    </listener>
</web-fragment>
```

No `web.xml` configuration required.

---

## Bootstrap Sequence

```
Application Startup
    │
    ▼
1. WebContextFilter.contextInitialized()
    │
    ├─► WebContext.setServletContext()
    │
    └─► NextWeb.createApplicationContext()
            │
            ▼
2. NextWebApplicationInitializer.onStartup()
    │
    └─► Register NextContextLoaderListener
            │
            ▼
3. NextContextLoaderListener.contextInitialized()
    │
    └─► Create NextWebApplicationContext
            │
            ▼
4. NextWebApplicationContext.loadBeanDefinitions()
    │
    ├─► Scan application packages
    │
    ├─► Apply AUTOWIRE_BY_TYPE
    │
    └─► Load custom BeanDefinitionLoaders
```

---

## Package Scanning

The framework automatically discovers and scans application packages.

### How It Works

`WebInitUtils.findScanPaths()`:

1. Scans `/WEB-INF/classes/` directory
2. Finds all `.class` files
3. Extracts package names
4. Filters out `org.nextframework.*` packages
5. Returns unique root packages

```java
// Example: if your classes are in
//   /WEB-INF/classes/com/myapp/service/UserService.class
//   /WEB-INF/classes/com/myapp/dao/UserDao.class
//
// Discovered scan paths: ["com.myapp"]
```

### What Gets Scanned

All classes in application packages with:
- `@Component`, `@Service`, `@Repository`, `@Controller`
- `@Configuration`
- Any Spring stereotype annotation

### Auto-Wiring

Application beans automatically use `AUTOWIRE_BY_TYPE`:

```java
@Service
public class MyService {
    // These are automatically injected (no @Autowired needed)
    private UserDao userDao;
    private EmailService emailService;

    // Setter injection works automatically
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
```

---

## NextWebApplicationContext

Extends Spring's `AnnotationConfigWebApplicationContext`:

```java
public class NextWebApplicationContext extends AnnotationConfigWebApplicationContext {

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        // 1. Find application packages
        String[] scanPaths = getApplicationScanPaths();

        // 2. Scan packages
        scan(scanPaths);

        // 3. Load standard Spring beans
        super.loadBeanDefinitions(beanFactory);

        // 4. Apply AUTOWIRE_BY_TYPE to application beans
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition def = beanFactory.getBeanDefinition(beanName);
            if (isApplicationComponent(def, scanPaths)) {
                ((AbstractBeanDefinition) def).setAutowireMode(AUTOWIRE_BY_TYPE);
            }
        }

        // 5. Load custom bean definitions via SPI
        for (BeanDefinitionLoader loader : ServiceFactory.loadServices(BeanDefinitionLoader.class)) {
            loader.loadBeanDefinitions(this, beanFactory);
        }
    }
}
```

### Service Registration

On initialization, registers itself as multiple service types:

```java
// Available via ServiceFactory.getService()
ServiceFactory.getService(ApplicationContext.class);
ServiceFactory.getService(WebApplicationContext.class);
ServiceFactory.getService(BeanFactory.class);
ServiceFactory.getService(MessageSource.class);
ServiceFactory.getService(ResourceLoader.class);
```

---

## Custom Bean Definition Loading

Implement `BeanDefinitionLoader` to register beans programmatically:

```java
public class MyBeanDefinitionLoader implements BeanDefinitionLoader {

    private String[] applicationScanPaths;

    @Override
    public void setApplicationScanPaths(String[] paths) {
        this.applicationScanPaths = paths;
    }

    @Override
    public void loadBeanDefinitions(ApplicationContext context,
                                    DefaultListableBeanFactory beanFactory) {
        // Register custom beans
        GenericBeanDefinition def = new GenericBeanDefinition();
        def.setBeanClass(MyCustomBean.class);
        beanFactory.registerBeanDefinition("myCustomBean", def);
    }
}
```

Register via SPI in `META-INF/services/org.nextframework.context.BeanDefinitionLoader`:

```
com.myapp.MyBeanDefinitionLoader
```

---

## Custom Context Loader Listener

Override the default listener for custom initialization:

```java
public class MyContextLoaderListener extends NextContextLoaderListener {

    @Override
    protected Class<?> determineContextClass(ServletContext servletContext) {
        // Use custom context class
        return MyCustomWebApplicationContext.class;
    }
}
```

Register via SPI in `META-INF/services/javax.servlet.ServletContextListener`:

```
com.myapp.MyContextLoaderListener
```

---

## Memory Leak Prevention

`MemoryLeakDestroyerListener` cleans up on shutdown:

```java
public class MemoryLeakDestroyerListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Release Commons Logging cache
        LogFactory.release(Thread.currentThread().getContextClassLoader());

        // Flush Introspector cache
        Introspector.flushCaches();
    }
}
```

Prevents common memory leaks during hot redeploys.

---

## Initialization Order

The `web-fragment.xml` ordering ensures:

1. `WebContextFilter` (listener) - Sets up ServletContext ThreadLocal
2. `NextWebApplicationInitializer` - Creates Spring context
3. `MemoryLeakDestroyerListener` - Cleanup on shutdown

Filters:

1. `WebContextFilter` - First filter, sets up request ThreadLocal

---

## Troubleshooting

### "No packages found in application"

The scanner couldn't find any classes in `/WEB-INF/classes/`. Check:
- Classes are compiled to the correct location
- Not using an embedded server without proper class path setup

### NotInNextContextException

Code is running outside a web request. Common causes:
- Background thread without context propagation
- Static initializer
- Timer/scheduled task

Solution: Use `InheritableThreadLocal` or manually propagate context.

### Bean not auto-wired

Ensure the bean is in an application package (not `org.nextframework.*`):
- Beans in framework packages use explicit wiring
- Only application beans get `AUTOWIRE_BY_TYPE`
