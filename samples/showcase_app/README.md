# ERP-lite Sample Application

A small business management application built with Next Framework. The application handles the core operations of a commercial business: managing customers, tracking product inventory, processing sales orders, and recording payments.

The goal is to demonstrate all major framework features in a realistic business context, including CRUD operations, master-detail forms, reports, charts, and role-based authorization.

---

## Project Structure

```
showcase_app/
├── src/                # Java sources (org.erplite.*)
├── WebContent/
│   └── WEB-INF/
├── scripts/            # Build and run scripts
├── ivy.xml             # Dependencies
└── build.xml           # Ant build
```

**Package:** `org.erplite`

**Runtime:** Embedded Tomcat

---

## Scripts

| Script | Description |
|--------|-------------|
| `scripts/build.sh` | Resolve dependencies and compile |
| `scripts/run.sh` | Start the application (http://localhost:8080) |

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
