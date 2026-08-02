# org.nextframework

## Overview

Root/base module for the framework distribution. This module does not expose the main runtime APIs; instead, it contains shared sample application files, release notes, and the minimal Ivy/build metadata used by the framework packaging process.

---

## Key Contents

| Path | Purpose |
|------|---------|
| `ivy.xml` | Declares the `next-base` module and shared Ivy configuration include |
| `build-next-module.xml` | Forwards packaging/dependency tasks to `org.nextframework.build` |
| `releaseNotes.txt` | Framework version history and migration notes |
| `sample/` | Example JSPs and baseline application config files |

---

## Sample Files

The `sample/` directory provides a small reference application layout:

- `base.jsp` and `base-bootstrap.jsp` - page layout examples
- `autorizacao.jsp` - authorization page sample
- `web.xml` - web application descriptor sample
- `connection.properties` and `hibernate.properties` - persistence configuration examples
- `menu.xml` and `messages.properties` - UI/navigation and message resources
- `log4j2.xml` - logging configuration sample

---

## Build Role

This module has no declared third-party dependencies in its own `ivy.xml`. Its build file delegates to the shared build infrastructure in `org.nextframework.build`, so packaging and dependency tasks stay centralized there.

---

## Version Notes

`releaseNotes.txt` is the main source for framework-level upgrade guidance. In the current repository it describes the `4.0.0` migration work, including the move to Jakarta APIs and updated Spring/Hibernate/JasperReports dependencies.
