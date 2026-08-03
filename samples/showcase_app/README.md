# ERP-lite Showcase App

Small sample application for the local NextFramework mirror. It demonstrates an end-to-end embedded-webapp setup with:

- Embedded Tomcat started from `org.erplite.Main`
- Next Framework deployment into `WebContent/WEB-INF`
- H2 file-based persistence
- Flyway migrations on startup
- Next authorization login flow
- A working CRUD for `User`

The repository currently contains a focused showcase, not a full ERP implementation. The landing and app home pages describe broader ERP features, but the implemented domain model is currently centered on users and authentication.

---

## Project Structure

```text
showcase_app/
├── src/
│   ├── org/erplite/              # Java sources
│   ├── db/migration/             # Flyway SQL migrations
│   ├── META-INF/services/        # Next service registrations
│   ├── authentication.properties
│   ├── connection.properties
│   └── messages.properties
├── WebContent/
│   ├── WEB-INF/jsp/              # JSP views
│   ├── css/                      # App, landing, and login styles
│   └── index.jsp                 # Redirects to /public/home
├── scripts/                      # Bash helper scripts
├── ivy.xml                       # App-only dependencies
├── build.xml                     # Ant build entrypoint
└── build.properties
```

**Package:** `org.erplite`

**Context path:** `/app`

**Entry URL:** `http://localhost:8080/app`

---

## What Is Implemented

### Controllers

- `HomeController` -> `/public/home`
- `LoginController` -> `/public/login`
- `AppHomeController` -> `/app/home`
- `UserCrudController` -> `/app/users`

### Persistence

- Entity: `User`
- DAO: `UserDAO`
- Authorization DAO: `AuthorizationDAO`
- Service: `UserService`

### Configuration

- `FlywayInitializer` runs migrations at startup
- `ErpliteViewConfig` customizes the view layer
- `MessageSourceConfig` loads application messages

### Views

- Public landing page
- Login page
- Authenticated home page
- User list view
- User form view

---

## Runtime and Dependencies

- Next Framework source is taken from the local mirror via `next.root`
- Embedded servlet container: Tomcat `11.0.24`
- Database: H2 `2.2.220`
- Migrations: Flyway `9.22.3`
- Logging bridge: `log4j-jul`
- Password hashing: BCrypt

This sample targets a Java 25 compilation/runtime toolchain in its app build:

- `build.xml` compiles with `source="25"` and `target="25"`
- the helper scripts delegate to `org.nextframework.build/tools/*`

---

## Quick Start

The helper scripts are Bash scripts, so run them from Git Bash, WSL, or another Unix-like shell.

```bash
# First-time setup: download tools, resolve deps, compile framework, deploy app, compile app
./scripts/build.sh

# Start on the default port (8080)
./scripts/run.sh

# Or start on a custom port
./scripts/run.sh 8081
```

Open:

```text
http://localhost:8080/app
```

`WebContent/index.jsp` redirects to `/public/home`.

---

## Scripts

| Script | Description |
|--------|-------------|
| `scripts/build.sh` | Downloads Ant/Ivy, resolves dependencies, compiles the framework, deploys it into `WebContent`, and compiles the app |
| `scripts/compile.sh` | Recompiles app source only |
| `scripts/run.sh [--compile] [port]` | Starts embedded Tomcat with context path `/app` |
| `scripts/clean.sh [--all]` | Removes compiled classes and copied JARs; `--all` also removes `build/tomcat` |
| `scripts/reset-db.sh` | Deletes local H2 database files under `data/` |

---

## Build Notes

`build.sh` auto-detects the local Next Framework root and stores it in `build.config` when needed.

The build flow is:

1. Download Ant and Ivy with `org.nextframework.build/tools/download-tools.sh`
2. Resolve framework and app dependencies
3. Compile Next Framework modules
4. Deploy framework classes/resources into `WebContent/WEB-INF`
5. Compile `showcase_app` sources

For app-only recompilation after the initial setup:

```bash
./scripts/compile.sh
```

---

## Database

Database configuration lives in `src/connection.properties`.

Current JDBC URL:

```properties
jdbc:h2:file:./data/erplite;AUTO_SERVER=TRUE;MODE=LEGACY
```

Notes:

- database files are created under `data/`
- `AUTO_SERVER=TRUE` allows multiple processes to access the database
- Flyway migrations are loaded from `src/db/migration/`

Current migrations:

- `V1__users_table.sql`
- `V2__users_seed.sql`

---

## Authentication

Module access is configured in `src/authentication.properties`:

- `public=false`
- `app=true`

In practice:

- `/public/*` is the anonymous area
- `/app/*` is the authenticated area

`LoginController` extends the framework login controller and:

- validates passwords with BCrypt
- redirects successful logins to `/app/home`
- exposes logout via `/public/login?action=logout`

Seed users created by `V2__users_seed.sql`:

| Username | Password | Display Name |
|----------|----------|--------------|
| `admin` | `admin` | `Administrator` |
| `sales` | `admin` | `Sales User` |
| `stock` | `admin` | `Inventory User` |
| `viewer` | `admin` | `Viewer` |

---

## User CRUD Sample

The implemented CRUD example is `UserCrudController` at `/app/users`.

Current behavior:

- lists users with `id`, `username`, `name`, and `createdAt`
- edits and creates users with the template tag views
- preserves `createdAt` on update
- hashes new passwords with BCrypt before saving
- keeps the existing password when an edited form leaves the password blank

Relevant files:

- `src/org/erplite/controller/UserCrudController.java`
- `WebContent/WEB-INF/jsp/app/crud/userList.jsp`
- `WebContent/WEB-INF/jsp/app/crud/userForm.jsp`

---

## Embedded Tomcat Notes

`org.erplite.Main` starts Tomcat directly and mounts `WebContent` at `/app`.

It also narrows JAR scanning to:

- `next-view-*.jar`
- `next-web-*.jar`

That keeps startup faster while still allowing TLD and `web-fragment.xml` discovery for the framework modules that need it.
