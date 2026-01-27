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

**UI Framework:** Bootstrap 5.3.3 (included via `<n:head/>`)

**Database:** H2 (file-based, auto-configured)

**Schema Migrations:** Flyway (runs on startup)

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
| `scripts/compile.sh` | Compile app source code only (after initial build) |
| `scripts/run.sh [--compile] [port]` | Start the application (default port: 8080) |
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

**WEB-INF/lib/** - All dependencies (JARs)
- Embedded Tomcat
- Spring Framework
- Hibernate
- H2 Database
- Flyway

This structure allows IDE hot-reload of Next Framework classes during development.

---

## Database

Next Framework auto-detects database configuration from `connection.properties` in the classpath. When found, the framework automatically creates a `DataSource` bean and `JdbcTemplate`.

**Configuration file:** `src/connection.properties`

**Database file:** `data/erplite.mv.db` (created on first run)

---

## Schema Migrations (Flyway)

Database schema is managed by Flyway, which runs migrations automatically on application startup.

**Migrations folder:** `src/db/migration/`

Migration files follow Flyway naming convention: `V1__description.sql`, `V2__description.sql`, etc.

---

## Authentication

The application uses Next Framework's authorization system with session-based authentication.

**Login page:** `/public/login`

**Protected module:** `/app/*` (requires authentication)

**Password hashing:** BCrypt

**Default users:** (all passwords are `admin`)
- `admin` - Administrator
- `sales` - Sales User
- `stock` - Inventory User
- `viewer` - Viewer

**Key files:**
- `src/authentication.properties` - Module security configuration
- `src/org/erplite/controller/LoginController.java` - Login/logout handling
- `src/org/erplite/dao/AuthorizationDAO.java` - User lookup

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
