# Next Framework - Modules Overview

## Project Information

- **Project Type:** Multi-module Java web framework
- **Build System:** Apache Ant + Apache IVY
- **Version:** 4.0.0
- **Total Modules:** 23 core modules
- **Java Baseline:** Java 25
- **Base Framework:** Spring 7.0.5 + Hibernate ORM 7.2.5.Final
- **Platform Migration:** Jakarta Servlet / JSP / Persistence APIs

---

## Module List

| Module | Purpose | Key Dependencies |
|--------|---------|------------------|
| org.nextframework | Root/Base module | Configuration only |
| org.nextframework.core | Utilities, exceptions | Spring Core 7.0.5 |
| org.nextframework.beans | Bean introspection | Spring Beans 7.0.5 |
| org.nextframework.context | Spring context integration | Spring Context 7.0.5, AOP, Micrometer |
| org.nextframework.controller | MVC controllers, JSON | Spring WebMVC 7.0.5, Jackson 2.21 |
| org.nextframework.persistence | Hibernate ORM | Hibernate ORM 7.2.5, Jakarta Persistence 3.2 |
| org.nextframework.dao | Generic DAOs | Spring JDBC 7.0.5, Spring TX 7.0.5 |
| org.nextframework.types | Custom types (Cpf, Cnpj, Money) | Hibernate ORM 7.2.5 (provided) |
| org.nextframework.validation | Validation framework | Commons Validator 1.10.1 |
| org.nextframework.view | JSP tags, UI components | OGNL 3.4.10, Jakarta JSTL 3.0 |
| org.nextframework.web | Servlet/JSP integration | Spring Web 7.0.5, Jakarta Servlet 6.1 |
| org.nextframework.services | Logging, services | Log4j 2.25.3 |
| org.nextframework.authorization | Security interfaces | None |
| org.nextframework.authorization.dashboard | Security UI | None |
| org.nextframework.report | JasperReports integration | JasperReports 7.0.7, OpenPDF |
| org.nextframework.report.generator | Report generation | Jakarta Servlet/JSP APIs |
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
- **External Dependencies:** None declared directly in Ivy

### org.nextframework.core
- **Path:** `org.nextframework.core/`
- **Purpose:** Core utilities and exception framework
- **Packages:**
  - `org.nextframework.exception` - Exception handling
  - `org.nextframework.util` - Utility functions
- **External Dependencies:** Spring Core 7.0.5

### org.nextframework.beans
- **Path:** `org.nextframework.beans/`
- **Purpose:** Bean introspection, reflection, and property descriptors
- **Packages:**
  - `org.nextframework.bean` - Bean metadata and descriptors
- **External Dependencies:** Spring Beans 7.0.5
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
- **External Dependencies:** Spring Context 7.0.5, Spring Context Support 7.0.5, Spring AOP 7.0.5, Spring Expression 7.0.5, Micrometer Observation 1.16.3, Micrometer Commons 1.16.3, JSpecify 1.0.0, AOP Alliance 1.0
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
  - Spring WebMVC 7.0.5
  - Jackson Core 2.21.0
  - Jackson Databind 2.21.0
  - Jackson Annotations 2.21
  - Jackson Model Versioning 1.2.2
  - Commons IO 2.21.0
  - Spring Test 7.0.5 and Mockito 5.23.0 for tests

### org.nextframework.persistence
- **Path:** `org.nextframework.persistence/`
- **Purpose:** ORM layer, Hibernate integration
- **Packages:**
  - `org.nextframework.persistence` - DAO interfaces and utilities
- **External Dependencies:**
  - Hibernate Core 7.2.5.Final
  - Hibernate Models 1.0.1
  - Jakarta Persistence API 3.2.0
  - Spring ORM 7.0.5
  - Spring TX 7.0.5
  - DOM4J 2.2.0
  - Hibernate Commons Annotations 7.0.3.Final
  - Jakarta Transaction API 2.0.1
  - Jakarta XML Bind API 4.0.5
  - Byte Buddy 1.18.5
  - ANTLR 4.13.2
  - JBoss Logging 3.6.2.Final
  - HSQLDB 2.7.4 (test)
- **Test Support:** Yes

### org.nextframework.dao
- **Path:** `org.nextframework.dao/`
- **Purpose:** Generic DAO implementations, data access layer
- **Packages:**
  - `org.nextframework.controller` - DAO controllers
  - `org.nextframework.persistence` - Data access objects
- **External Dependencies:**
  - Spring JDBC 7.0.5
  - Spring TX 7.0.5
  - Mockito 5.23.0 for tests
- **Test Support:** Yes

### org.nextframework.types
- **Path:** `org.nextframework.types/`
- **Purpose:** Custom Java types (Cpf, Cnpj, Money, Phone, Cep, etc.)
- **Packages:**
  - `org.nextframework.types` - Custom type system
- **External Dependencies:** Hibernate Core 7.2.5.Final (provided), Jakarta Persistence API 3.2.0 (provided), Mockito 5.23.0 for tests
- **Test Support:** Yes

### org.nextframework.validation
- **Path:** `org.nextframework.validation/`
- **Purpose:** Server-side and client-side (JavaScript) validation
- **Packages:**
  - `org.nextframework.validation` - Validation framework
- **Features:** Annotation support, validation registry, JS validation generation
- **External Dependencies:** Commons Validator 1.10.1

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
- **External Dependencies:** OGNL 3.4.10, Javassist 3.30.2-GA, Jakarta JSTL API 3.0.2, Jakarta JSTL implementation 3.0.1

### org.nextframework.web
- **Path:** `org.nextframework.web/`
- **Purpose:** Servlet/JSP integration, web context initialization
- **Packages:**
  - `org.nextframework.core.web` - Web context interfaces
  - `org.nextframework.web` - Servlet integration
- **External Dependencies:**
  - Spring Web 7.0.5
  - Spring WebMVC 7.0.5
  - Jakarta Servlet API 6.1.0 (provided)
  - Jakarta JSP API 4.0.0 (provided)
  - Jakarta EL API 6.0.1 (provided)

### org.nextframework.services
- **Path:** `org.nextframework.services/`
- **Purpose:** Logging, service provider abstraction
- **Packages:**
  - `org.nextframework.service` - Service layer abstractions
- **External Dependencies:**
  - Log4j 2.25.3 (API, Core, JCL bridge)
  - Commons Logging 1.3.5

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
  - JasperReports 7.0.7
  - JasperReports PDF 7.0.7
  - Jackson Core 2.21.0
  - Jackson Databind 2.21.0
  - Jackson Annotations 2.21
  - Jackson Dataformat XML 2.21.0
  - Commons Logging 1.3.6
  - Commons Collections4 4.5.0
  - Commons BeanUtils2 2.0.0-M2
  - Commons Lang3 3.20.0
  - OpenPDF 1.3.43
  - XMP Core 6.1.11
  - Commons Codec 1.21.0
  - Barbecue (barcode library) 1.5-beta1

### org.nextframework.report.generator
- **Path:** `org.nextframework.report.generator/`
- **Purpose:** Compile-time report generation
- **Packages:**
  - `org.nextframework.report` - Report generation
- **External Dependencies:** Jakarta Servlet API 6.1.0 (provided), Jakarta JSP API 4.0.0 (provided), Jakarta EL API 6.0.1 (provided)

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
- **External Dependencies:** BCEL 6.12.0, Commons IO 2.21.0, Commons Lang3 3.20.0, Gson 2.13.2, Guava 33.6.0-jre, JavaParser 1.0.8, Error Prone Annotations 2.48.0 (provided)

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
- Built on Spring Framework 7.0.5
- Uses Spring Context, Beans, WebMVC, Web, JDBC, ORM and TX
- Spring-based service discovery via `ServiceFactory`

### Hibernate/JPA ORM
- Hibernate ORM 7.2.5.Final as primary ORM
- Jakarta Persistence API 3.2.0
- Generic DAO pattern for database access
- Custom types via Hibernate type system

### Java/Jakarta Platform
- Compiles and packages against Java 25
- Servlet stack migrated to Jakarta Servlet 6.1 / JSP 4.0 / EL 6.0
- Persistence and XML binding migrated to Jakarta Persistence 3.2 / JAXB 4.0

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
