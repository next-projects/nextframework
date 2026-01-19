# Next Framework - Modules Overview

## Project Information

- **Project Type:** Multi-module Java web framework
- **Build System:** Apache Ant + Apache IVY
- **Version:** 3.9.0-beta
- **Total Modules:** 23 core modules
- **Base Framework:** Spring 4.1.5.RELEASE + Hibernate 4.3.8.Final

---

## Module List

| Module | Purpose | Key Dependencies |
|--------|---------|------------------|
| org.nextframework | Root/Base module | javax.mail, aopalliance |
| org.nextframework.core | Utilities, exceptions | Spring Core |
| org.nextframework.beans | Bean introspection | Spring Beans |
| org.nextframework.context | Spring context integration | Spring Context, AOP |
| org.nextframework.controller | MVC controllers, JSON | Spring WebMVC, Jackson |
| org.nextframework.persistence | Hibernate ORM | Hibernate Core, JPA |
| org.nextframework.dao | Generic DAOs | Spring JDBC, ORM, TX |
| org.nextframework.types | Custom types (Cpf, Cnpj, Money) | Hibernate (provided) |
| org.nextframework.validation | Validation framework | Commons Validator |
| org.nextframework.view | JSP tags, UI components | OGNL, JSTL |
| org.nextframework.web | Servlet/JSP integration | Spring Web, Servlet API |
| org.nextframework.services | Logging, services | Log4j 2.17.1 |
| org.nextframework.authorization | Security interfaces | None |
| org.nextframework.authorization.dashboard | Security UI | None |
| org.nextframework.report | JasperReports integration | JasperReports, iTextPDF |
| org.nextframework.report.generator | Report generation | Servlet/JSP API |
| org.nextframework.chart | Charting | JFreeChart, Batik SVG |
| org.nextframework.stjs | Java to JavaScript conversion | BCEL, Gson, Guava |
| org.nextframework.jsbuilder | JavaScript generation | None |
| org.nextframework.legacy | Legacy code support | None |
| org.nextframework.summary | Summary/aggregation utilities | None |
| org.nextframework.compilation | Compilation utilities | None |
| org.nextframework.build | Build configuration | N/A (Ant scripts) |

---

## Module Details

### org.nextframework (Root)
- **Path:** `org.nextframework/`
- **Purpose:** Base framework module with sample data
- **Packages:** None (configuration only)
- **External Dependencies:** javax.mail:mail v1.4.7, aopalliance v1.0

### org.nextframework.core
- **Path:** `org.nextframework.core/`
- **Purpose:** Core utilities and exception framework
- **Packages:**
  - `org.nextframework.exception` - Exception handling
  - `org.nextframework.util` - Utility functions
- **External Dependencies:** Spring Core 4.1.5.RELEASE

### org.nextframework.beans
- **Path:** `org.nextframework.beans/`
- **Purpose:** Bean introspection, reflection, and property descriptors
- **Packages:**
  - `org.nextframework.bean` - Bean metadata and descriptors
- **External Dependencies:** Spring Beans 4.1.5.RELEASE
- **Test Support:** Yes

### org.nextframework.context
- **Path:** `org.nextframework.context/`
- **Purpose:** Spring application context integration and initialization
- **Packages:**
  - `org.nextframework.bean` - Bean utilities
  - `org.nextframework.classmanager` - Class loading
  - `org.nextframework.context` - Application context
  - `org.nextframework.core` - Core abstractions
  - `org.nextframework.exception` - Exception handling
  - `org.nextframework.message` - Messaging
  - `org.nextframework.util` - Utilities
- **External Dependencies:** Spring Context, Context Support, AOP, Expression
- **Test Support:** Yes

### org.nextframework.controller
- **Path:** `org.nextframework.controller/`
- **Purpose:** Request handling, MVC controller dispatch, bean property editing, JSON processing
- **Packages:**
  - `org.nextframework.bean` - Property editors (Cal, Cep, Cnpj, Cpf, Money, Phone, etc.)
  - `org.nextframework.controller` - MVC controller classes
  - `org.nextframework.service` - Service layer
- **Key Classes:** MultiActionController, NextDispatcherServlet, Custom property editors
- **External Dependencies:**
  - Spring WebMVC 4.1.5.RELEASE
  - Jackson Databind 2.7.4
  - Commons FileUpload 1.2.2
  - Commons IO 1.4

### org.nextframework.persistence
- **Path:** `org.nextframework.persistence/`
- **Purpose:** ORM layer, Hibernate integration
- **Packages:**
  - `org.nextframework.persistence` - DAO interfaces and utilities
- **External Dependencies:**
  - Hibernate Core 4.3.8.Final
  - Hibernate JPA 2.1-api
  - DOM4J, ANTLR, JavaAssist
  - HSQLDB 2.3.2 (test)
- **Test Support:** Yes

### org.nextframework.dao
- **Path:** `org.nextframework.dao/`
- **Purpose:** Generic DAO implementations, data access layer
- **Packages:**
  - `org.nextframework.controller` - DAO controllers
  - `org.nextframework.persistence` - Data access objects
- **External Dependencies:**
  - Spring JDBC 4.1.5.RELEASE
  - Spring ORM 4.1.5.RELEASE
  - Spring TX 4.1.5.RELEASE
- **Test Support:** Yes

### org.nextframework.types
- **Path:** `org.nextframework.types/`
- **Purpose:** Custom Java types (Cpf, Cnpj, Money, Phone, Cep, etc.)
- **Packages:**
  - `org.nextframework.types` - Custom type system
- **External Dependencies:** Hibernate Core, Hibernate JPA (provided)
- **Test Support:** Yes

### org.nextframework.validation
- **Path:** `org.nextframework.validation/`
- **Purpose:** Server-side and client-side (JavaScript) validation
- **Packages:**
  - `org.nextframework.validation` - Validation framework
- **Features:** Annotation support, validation registry, JS validation generation
- **External Dependencies:** Commons Validator 1.4.1

### org.nextframework.view
- **Path:** `org.nextframework.view/`
- **Purpose:** JSP tag library implementation, form rendering, UI components
- **Packages:**
  - `org.nextframework.core` - View core abstractions
  - `org.nextframework.filter` - Filtering utilities
  - `org.nextframework.view` - JSP tag libraries and components
- **Features:**
  - Input components (checkbox, radio, select, combo, file, etc.)
  - Table/grid components
  - Form templates
  - Chart integration
  - AJAX callbacks
  - Menu builders
- **External Dependencies:** OGNL 2.6.7, JSTL 1.2

### org.nextframework.web
- **Path:** `org.nextframework.web/`
- **Purpose:** Servlet/JSP integration, web context initialization
- **Packages:**
  - `org.nextframework.core.web` - Web context interfaces
  - `org.nextframework.web` - Servlet integration
- **External Dependencies:**
  - Spring Web 4.1.5.RELEASE
  - Spring WebMVC 4.1.5.RELEASE
  - Servlet API 3.1.0 (provided)
  - JSP API 2.2.1 (provided)

### org.nextframework.services
- **Path:** `org.nextframework.services/`
- **Purpose:** Logging, service provider abstraction
- **Packages:**
  - `org.nextframework.service` - Service layer abstractions
- **External Dependencies:**
  - Log4j 2.17.1 (API, Core, JCL bridge)
  - Commons Logging 1.2

### org.nextframework.authorization
- **Path:** `org.nextframework.authorization/`
- **Purpose:** Security, authorization, authentication interfaces
- **Packages:**
  - `org.nextframework.authorization` - Authentication/authorization framework
- **Web Sources:** `web-src/` (JSP/web resources)
- **External Dependencies:** None

### org.nextframework.authorization.dashboard
- **Path:** `org.nextframework.authorization.dashboard/`
- **Purpose:** Authorization dashboard UI components
- **Packages:**
  - `org.nextframework.authorization.dashboard` - Dashboard UI
- **External Dependencies:** None

### org.nextframework.report
- **Path:** `org.nextframework.report/`
- **Purpose:** Reporting framework with JasperReports integration
- **Contains:**
  - JasperReports base sources
  - JasperReports HTML sources
  - JasperReports implementation sources
  - Report builder sources
- **External Dependencies:**
  - JasperReports 6.0.3
  - iTextPDF 5.5.5
  - Commons Collections 3.2.1
  - Commons Digest, BeanUtils, Codec
  - Barbecue (barcode library) 1.5-beta1

### org.nextframework.report.generator
- **Path:** `org.nextframework.report.generator/`
- **Purpose:** Compile-time report generation
- **Packages:**
  - `org.nextframework.report` - Report generation
- **External Dependencies:** Servlet API 3.0.1, JSP API 2.2.1 (provided)

### org.nextframework.chart
- **Path:** `org.nextframework.chart/`
- **Purpose:** Chart rendering with JFreeChart and Google Visualization
- **Packages:**
  - `org.nextframework.chart` - Chart rendering
  - `org.nextframework.view` - View integration
- **External Dependencies:**
  - JFreeChart 1.0.19
  - JCommon 1.0.23
  - Batik (SVG rendering) 1.6-1

### org.nextframework.stjs
- **Path:** `org.nextframework.stjs/`
- **Purpose:** Static Type JavaScript (Java to JavaScript conversion)
- **Sub-modules:** Generator, JS builder, js-lib, js-next, js-google
- **External Dependencies:** BCEL 5.2, Gson 2.1, Guava 16.0, JavaParser 1.0.8 (provided)

### org.nextframework.jsbuilder
- **Path:** `org.nextframework.jsbuilder/`
- **Purpose:** JavaScript generation and building
- **Packages:**
  - `org.nextframework.view` - View integration
- **Features:** Google Visualization source, Next.js builder

### org.nextframework.legacy
- **Path:** `org.nextframework.legacy/`
- **Purpose:** Legacy code compatibility (RTF, deprecated APIs)
- **Packages:**
  - `org.nextframework.report` - Legacy report support
  - `org.nextframework.rtf` - RTF support
- **External Dependencies:** None

### org.nextframework.summary
- **Path:** `org.nextframework.summary/`
- **Purpose:** Summary/aggregation utilities
- **Packages:**
  - `org.nextframework.summary` - Summary/aggregation utilities
- **External Dependencies:** None
- **Test Support:** Yes

### org.nextframework.compilation
- **Path:** `org.nextframework.compilation/`
- **Purpose:** Compilation utilities
- **Packages:**
  - `org.nextframework.compilation` - Compilation utilities
- **External Dependencies:** None (build-time only)

### org.nextframework.build
- **Path:** `org.nextframework.build/`
- **Purpose:** Shared build configuration and Ant scripts
- **Contents:**
  - `configurations.xml` - IVY configurations
  - `build-package.xml` - Package building
  - `build-web-deploy.xml` - Web deployment
  - `build-js.xml` - JavaScript building
  - `build-dependencies.xml` - Dependency resolution
  - `ivysettings.xml` - IVY settings
  - `build.properties` - Version info

---

## Module Dependency Graph

```
                           org.nextframework (Base)
                                    |
                    +---------------+---------------+
                    |                               |
            org.nextframework.core          org.nextframework.services
                    |                               |
            org.nextframework.beans                 |
                    |                               |
            org.nextframework.context <-------------+
                    |
        +-----------+-----------+
        |           |           |
        v           v           v
    .controller .persistence  .web
        |           |           |
        v           v           v
      .dao   <-- .types       .view
        |                       |
        v                       v
  .validation              .authorization
                                |
                                v
                     .authorization.dashboard

    .report --> .report.generator
    .chart
    .stjs --> .jsbuilder
    .legacy
    .summary
    .compilation
```

### Dependency Flow

1. **Core Layer:**
   - `org.nextframework.core` - Utilities, exceptions
   - `org.nextframework.beans` - Bean introspection (depends on core)
   - `org.nextframework.context` - Spring integration (depends on beans)

2. **Data Layer:**
   - `org.nextframework.persistence` - Hibernate ORM
   - `org.nextframework.dao` - Generic DAOs (depends on persistence)
   - `org.nextframework.types` - Custom types (used by persistence)

3. **Web Layer:**
   - `org.nextframework.controller` - MVC controllers (depends on context)
   - `org.nextframework.web` - Servlet integration
   - `org.nextframework.view` - JSP tags (depends on controller, web)

4. **Cross-cutting:**
   - `org.nextframework.validation` - Validation (used by controller, view)
   - `org.nextframework.services` - Logging (used by all)
   - `org.nextframework.authorization` - Security (used by controller, view)

5. **Specialized:**
   - `org.nextframework.report` - Reporting
   - `org.nextframework.chart` - Charting
   - `org.nextframework.stjs` - JavaScript generation

---

## Architectural Patterns

### Layered Architecture
- **Persistence Layer:** persistence + dao modules
- **Service Layer:** services + controller modules
- **Controller Layer:** controller module
- **View Layer:** view + web modules

### Spring Framework Integration
- Built on Spring 4.1.5.RELEASE
- Uses Spring Context, Beans, WebMVC, JDBC, ORM, TX
- Spring-based service discovery via `ServiceFactory`

### Hibernate/JPA ORM
- Hibernate 4.3.8.Final as primary ORM
- Generic DAO pattern for database access
- Custom types via Hibernate type system

### JSP Tag Library System
- Extensive custom tag library for form rendering
- Template-based view generation
- AJAX callback support
- Input components with automatic property binding

### Custom Type System
- Brazilian document types: Cpf, Cnpj, Phone, Cep
- Money type with validation
- File type with persistence
- OGNL-based expression language

---

## Directory Structure

```
nextframework/
├── org.nextframework/                      (Root module)
├── org.nextframework.build/                (Build configuration)
├── org.nextframework.core/                 (Utilities, exceptions)
├── org.nextframework.beans/                (Bean introspection)
├── org.nextframework.context/              (Spring context integration)
├── org.nextframework.controller/           (MVC controllers, JSON)
├── org.nextframework.persistence/          (Hibernate ORM)
├── org.nextframework.dao/                  (Generic DAOs)
├── org.nextframework.types/                (Custom types)
├── org.nextframework.validation/           (Validation framework)
├── org.nextframework.view/                 (JSP tags, UI components)
├── org.nextframework.web/                  (Servlet/JSP integration)
├── org.nextframework.services/             (Logging, services)
├── org.nextframework.authorization/        (Security)
├── org.nextframework.authorization.dashboard/ (Security UI)
├── org.nextframework.report/               (JasperReports)
├── org.nextframework.report.generator/     (Report generation)
├── org.nextframework.chart/                (Charting)
├── org.nextframework.stjs/                 (JavaScript generation)
├── org.nextframework.jsbuilder/            (JS builder)
├── org.nextframework.legacy/               (Legacy support)
├── org.nextframework.summary/              (Summary utilities)
├── org.nextframework.compilation/          (Compilation utilities)
└── documentation/                          (Documentation)
```
