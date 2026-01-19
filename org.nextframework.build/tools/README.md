# Build Tools

## Quick Start

```bash
cd org.nextframework.build
source tools/setup-env.sh
```

This runs the full setup:
1. Download Ant + Ivy (cached in `tools/bin/`)
2. Set PATH for current shell
3. Download all module dependencies
4. Compile all modules
5. Generate JavaScript files

**Prerequisites:** Java 8 JDK, curl

---

## Scripts

| Script | Description |
|--------|-------------|
| `setup-env.sh` | Full setup (runs all steps below) |
| `download-tools.sh` | Downloads Ant and Ivy |
| `download-dependencies.sh` | Downloads module dependencies via Ivy |
| `compile-all.sh` | Compiles all modules in dependency order |
| `generate-js.sh` | Generates JavaScript from Java using STJS |
| `clean.sh` | Removes all compiled classes |
| `show-dependencies.sh` | Prints module dependencies in YAML format |

---

## Skipping Steps

Use `--no-*` flags to skip steps:

```bash
# Skip dependency download (use existing)
source tools/setup-env.sh --no-deps

# Skip compilation
source tools/setup-env.sh --no-compile

# Skip JavaScript generation
source tools/setup-env.sh --no-js

# Combine flags
source tools/setup-env.sh --no-compile --no-js
```

---

## Running Individual Steps

```bash
# Download tools only
./tools/download-tools.sh

# Download dependencies only
./tools/download-dependencies.sh

# Compile only
./tools/compile-all.sh

# Generate JavaScript only
./tools/generate-js.sh

# Clean compiled classes
./tools/clean.sh

# Show module dependencies (YAML)
./tools/show-dependencies.sh
```

---

## New Terminal Session

Re-run setup to restore PATH (tools are cached):

```bash
source tools/setup-env.sh --no-deps --no-compile --no-js
```
