# Next Framework Documentation

Documentation for the Next Framework - a Java web application framework for building CRUD applications with JSP tag libraries, persistence, and MVC architecture.

---

## Structure

Each framework module has its own documentation folder:

```
documentation/
├── org.nextframework.core/
├── org.nextframework.web/
├── org.nextframework.controller/
├── org.nextframework.view/
│   ├── README.md
│   ├── TEMPLATE-ENGINE.md
│   ├── TAGS-FORM.md
│   ├── TAGS-LAYOUT.md
│   └── ...
├── org.nextframework.persistence/
└── ...
```

Each module folder contains:
- `README.md` - Module overview, architecture, and quick start
- Additional `.md` files for specific topics (where needed)

---

## Modules

| Module | Description |
|--------|-------------|
| [core](org.nextframework.core/) | Core utilities and base classes |
| [services](org.nextframework.services/) | Service layer abstractions |
| [beans](org.nextframework.beans/) | Bean introspection and property access |
| [context](org.nextframework.context/) | Application context and dependency injection |
| [compilation](org.nextframework.compilation/) | Runtime compilation utilities |
| [summary](org.nextframework.summary/) | Data aggregation and summarization |
| [types](org.nextframework.types/) | Custom types (Money, Phone, etc.) |
| [web](org.nextframework.web/) | Web infrastructure and request handling |
| [validation](org.nextframework.validation/) | Validation framework |
| [persistence](org.nextframework.persistence/) | JPA/Hibernate integration |
| [dao](org.nextframework.dao/) | Data access objects and generic DAO |
| [controller](org.nextframework.controller/) | MVC controllers and CRUD support |
| [authorization](org.nextframework.authorization/) | Security and permissions |
| [authorization.dashboard](org.nextframework.authorization.dashboard/) | Permission management UI |
| [view](org.nextframework.view/) | JSP tag libraries |
| [chart](org.nextframework.chart/) | Chart generation |
| [jsbuilder](org.nextframework.jsbuilder/) | JavaScript builder utilities |
| [stjs](org.nextframework.stjs/) | Java to JavaScript transpilation |
| [report](org.nextframework.report/) | Report generation |
| [report.generator](org.nextframework.report.generator/) | Dynamic report builder |
| [legacy](org.nextframework.legacy/) | JasperReports and RTF support |
| [build](org.nextframework.build/) | Build scripts and tools |

---

## Quick Links

### Getting Started
- [Build Module](org.nextframework.build/) - How to compile and set up the framework

### Core Concepts
- [Persistence](org.nextframework.persistence/) - Entity mapping and database access
- [DAO](org.nextframework.dao/) - Data access patterns
- [Controller](org.nextframework.controller/) - MVC and CRUD controllers

### View Layer
- [View Overview](org.nextframework.view/) - Tag library architecture
- [Template Engine](org.nextframework.view/TEMPLATE-ENGINE.md) - How tag templates work
- [Form Tags](org.nextframework.view/TAGS-FORM.md) - Forms and inputs
- [Template Tags](org.nextframework.view/TAGS-TEMPLATE.md) - High-level CRUD views
- [Data Tags](org.nextframework.view/TAGS-DATA.md) - DataGrid and tables
- [Layout Tags](org.nextframework.view/TAGS-LAYOUT.md) - Panels and grids

### Security
- [Authorization](org.nextframework.authorization/) - Permission system
- [Authorization Dashboard](org.nextframework.authorization.dashboard/) - Admin UI

### Reporting
- [Report](org.nextframework.report/) - Report definitions
- [Report Generator](org.nextframework.report.generator/) - Dynamic reports
