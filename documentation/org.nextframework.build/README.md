# org.nextframework.build

Build infrastructure for the Next Framework. Provides shell scripts and Ant build files for compiling, packaging, and deploying framework modules.

---

## Quick Start

```bash
cd org.nextframework.build
source tools/setup-env.sh
```

This runs the full setup:
1. Downloads Ant and Ivy (cached in `tools/bin/`)
2. Sets PATH for current shell
3. Downloads all module dependencies
4. Compiles all modules
5. Generates JavaScript files

**Prerequisites:** Java 8 JDK, curl

---

## Shell Scripts

Located in `tools/`:

| Script | Description |
|--------|-------------|
| `setup-env.sh` | Full setup (runs all steps) |
| `download-tools.sh` | Downloads Ant and Ivy |
| `download-dependencies.sh` | Downloads module dependencies via Ivy |
| `compile-all.sh` | Compiles all modules in dependency order |
| `generate-js.sh` | Generates JavaScript from Java using STJS |
| `clean.sh` | Removes all compiled classes |
| `show-dependencies.sh` | Prints module dependencies in YAML format |
| `show-missing-docs.sh` | Shows undocumented modules |

### Skipping Steps

```bash
# Skip dependency download
source tools/setup-env.sh --no-deps

# Skip compilation
source tools/setup-env.sh --no-compile

# Skip JavaScript generation
source tools/setup-env.sh --no-js

# Combine flags
source tools/setup-env.sh --no-compile --no-js
```

### New Terminal Session

Re-run setup to restore PATH (tools are cached):

```bash
source tools/setup-env.sh --no-deps --no-compile --no-js
```

---

## Ant Build Files

### build-dependencies.xml

Downloads module dependencies using Apache Ivy.

```bash
ant -f build-dependencies.xml
```

**Targets:**
- `Download Next Ivy Dependencies` - Downloads all module dependencies
- `Remove Next Ivy Dependencies` - Deletes downloaded libraries

### build-package.xml

Packages modules into JARs and creates distribution files.

```bash
ant -f build-package.xml
```

**Targets:**
- `create-distribution-file` - Creates `next-{version}-full.zip` distribution
- `package-all-modules` - Packages all modules into JARs

**Output:**
- `dist/lib/` - Module JARs
- `dist/lib/sources/` - Source JARs
- `dist/lib/dependencies/` - Third-party dependencies

### build-js.xml

Generates JavaScript files from Java using STJS (Strongly Typed JavaScript).

```bash
ant -f build-js.xml
```

**Targets:**
- `Build JS Files` - Generates JS from `js-builder/` directories

### build-web-deploy.xml

Deploys framework and application to a web server.

**Required Property:**
```xml
<property name="deploy.dir" value="/path/to/tomcat/webapps/myapp"/>
<import file="path/to/build-web-deploy.xml"/>
```

**Targets:**
- `Deploy Project on Server` - Full deployment
- `Copy Application Files` - Deploy only application files
- `Copy Next Files` - Deploy only framework classes
- `Undeploy` - Remove deployment
- `Retrieve Ivy Dependencies` - Download project dependencies
- `Generate Javascript Files` - Build JS files

---

## Module Compilation Order

The `compile-all.sh` script compiles modules in dependency order, handling circular dependencies:

```
Phase 1 (Independent):
  core → services → beans → compilation → context → summary → types → web → validation → persistence → authorization

Phase 2 (Circular: controller ↔ dao):
  controller + dao (compiled together)

Phase 3 (Pre-view):
  jsbuilder

Phase 4 (Circular: view ↔ chart):
  view + chart (compiled together)

Phase 5 (Dependent):
  authorization.dashboard → report → stjs → report.generator → legacy
```

---

## Ivy Configuration

### configurations.xml

Defines dependency configurations:

| Configuration | Description |
|---------------|-------------|
| `provided` | Compile-time only (not packaged) |
| `lib` | Runtime dependencies (packaged) |
| `lib-test` | Test dependencies |
| `extras/stjs` | STJS dependencies |

### ivysettings.xml

Configures Ivy resolvers for Maven Central and other repositories.

---

## Creating a Distribution

```bash
# 1. Download dependencies
ant -f build-dependencies.xml

# 2. Compile all modules
./tools/compile-all.sh

# 3. Generate JavaScript
./tools/generate-js.sh

# 4. Create distribution
ant -f build-package.xml
```

Output: `next-{version}-full.zip`

---

## Project Integration

### Using build-web-deploy.xml

Create a `build.xml` in your project:

```xml
<project name="My App" default="Deploy Project on Server">
    <property name="deploy.dir" value="${user.home}/tomcat/webapps/myapp"/>
    <property name="webroot.dir" value="WebContent"/>

    <import file="../org.nextframework.build/build-web-deploy.xml"/>

    <!-- Override for multi-module projects -->
    <target name="Project Modules">
        <module name="my-module-1"/>
        <module name="my-module-2"/>
    </target>
</project>
```

### Using Ivy for Dependencies

Create an `ivy.xml` in your project:

```xml
<ivy-module version="2.0">
    <info organisation="com.mycompany" module="myapp"/>
    <configurations>
        <include file="../org.nextframework.build/configurations.xml"/>
    </configurations>
    <dependencies>
        <dependency org="org.nextframework" name="next-web" rev="latest.integration"/>
        <!-- your dependencies -->
    </dependencies>
</ivy-module>
```

---

## Directory Structure

```
org.nextframework.build/
├── tools/
│   ├── setup-env.sh           # Full setup script
│   ├── download-tools.sh      # Download Ant/Ivy
│   ├── download-dependencies.sh
│   ├── compile-all.sh         # Compile all modules
│   ├── generate-js.sh         # Generate JavaScript
│   ├── clean.sh               # Clean compiled classes
│   ├── show-dependencies.sh   # Print dependencies
│   ├── show-missing-docs.sh   # Check documentation
│   ├── bin/                   # Downloaded tools (Ant, Ivy)
│   └── README.md              # Tools documentation
├── include/
│   ├── build-commons.xml      # Common macros
│   ├── build-ivy.xml          # Ivy initialization
│   └── build-module-basics.xml
├── samples/
│   ├── build-sample-app.xml   # Sample project build
│   ├── ivy-sample-app.xml     # Sample Ivy config
│   └── build-install.xml      # Installation script
├── build-dependencies.xml     # Download dependencies
├── build-package.xml          # Create distribution
├── build-js.xml               # Generate JavaScript
├── build-web-deploy.xml       # Web deployment
├── configurations.xml         # Ivy configurations
├── ivysettings.xml           # Ivy settings
└── build.properties          # Version properties
```
