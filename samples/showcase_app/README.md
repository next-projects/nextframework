# ERP-lite Sample Application

A small business management application built with Next Framework. The application handles the core operations of a commercial business: managing customers, tracking product inventory, processing sales orders, and recording payments.

The goal is to demonstrate all major framework features in a realistic business context, including CRUD operations, master-detail forms, reports, charts, and role-based authorization.

---

## Project Structure

```
showcase_app/
├── src/                    # Java sources (org.erplite.*)
├── WebContent/
│   └── WEB-INF/
├── scripts/                # Build and run scripts
├── ivy.xml                 # Dependencies
└── build.xml               # Ant build
```

**Package:** `org.erplite`

**Runtime:** Embedded Tomcat

**Next Framework:** 3.9.x

---

## Quick Start

```bash
# Build the project (first run may take a few minutes)
./scripts/build.sh

# Run the application
./scripts/run.sh

# Access the application
open http://localhost:8080/app
```

---

## Scripts

| Script | Description |
|--------|-------------|
| `scripts/build.sh` | Resolve dependencies, compile, and set up Next Framework |
| `scripts/run.sh [port]` | Start the application (default port: 8080) |
| `scripts/clean.sh [--all]` | Clean build outputs |

---

## Build Setup

The build script (`scripts/build.sh`) sets up the project structure for development:

**WEB-INF/classes/** - Next Framework classes (exploded)
- `org/nextframework/` - Core framework classes
- `org/stjs/` - JavaScript generation classes
- `META-INF/` - Framework metadata:
  - `*.tld` - Tag Library Descriptors (JSP tags)
  - `web-fragment.xml` - Servlet configuration (filters, listeners, servlets)
  - `services/` - Service loader configuration

**WEB-INF/lib/** - Dependencies (JARs)
- Spring Framework JARs
- Hibernate JARs
- Other third-party libraries

This structure allows IDE hot-reload of Next Framework classes during development.

---

## Entities

- User
- Company
- Customer
- Category
- Product
- Supplier
- Order
- OrderItem
- StockMovement
- Invoice
- Payment
