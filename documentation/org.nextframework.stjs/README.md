# org.nextframework.stjs

## Overview

Java-to-JavaScript transpiler. Write browser UI code in type-safe Java with full IDE support (refactoring, autocomplete, compile-time checking), then transpile to JavaScript for browser execution.

```java
// Java (with IDE support, refactoring, type checking)
public class MyButton {
    public void click(Element el) {
        el.innerHTML = "Clicked!";
    }
}
```

```javascript
// Generated JavaScript
MyButton = function() {};
MyButton.prototype.click = function(el) {
    el.innerHTML = "Clicked!";
};
```

---

## Architecture

```
org.nextframework.stjs/
├── generator/          # Java-to-JS transpiler engine
├── shared/             # Java↔JS type interfaces (Array, Map, Date, functions)
├── js-lib/             # Browser/DOM API wrappers
├── js-next/            # Next Framework JS bridge (next.dom, next.ajax, etc.)
├── js-google/          # Google Maps API wrappers
├── js-builder/         # Next Framework UI components
├── server/             # Server-side JS collection implementations
└── provided/           # Dependencies (BCEL, Gson, Guava, JavaParser)
```

---

## Writing Java for JavaScript

### Basic Example

```java
package org.myapp.client;

import org.stjs.javascript.dom.Element;
import org.stjs.javascript.functions.Function1;
import org.stjs.javascript.dom.DOMEvent;
import static org.nextframework.js.NextGlobalJs.next;

public class MyButton {

    public void createButton(Element container) {
        // Create DOM element
        Element button = next.dom.newElement("button");
        button.innerHTML = "Click Me";

        // Add click handler (becomes JS function)
        button.onclick = new Function1<DOMEvent, Boolean>() {
            public Boolean $invoke(DOMEvent event) {
                next.dialogs.alert("Button clicked!");
                return true;
            }
        };

        container.appendChild(button);
    }
}
```

### Generated JavaScript

```javascript
MyButton = function() {};

MyButton.prototype.createButton = function(container) {
    var button = next.dom.newElement("button");
    button.innerHTML = "Click Me";

    button.onclick = function(event) {
        next.dialogs.alert("Button clicked!");
        return true;
    };

    container.appendChild(button);
};
```

### Using in HTML Page

```html
<!DOCTYPE html>
<html>
<head>
    <!-- Next Framework core scripts -->
    <script src="/resources/next.js"></script>
    <script src="/resources/stjs.js"></script>

    <!-- Generated JavaScript -->
    <script src="/resources/MyButton.js"></script>
</head>
<body>
    <div id="buttonContainer"></div>

    <script>
        // Create instance and call method
        var myButton = new MyButton();
        myButton.createButton(document.getElementById("buttonContainer"));
    </script>
</body>
</html>
```

### Using in JSP (Next Framework)

```jsp
<%@ taglib prefix="n" uri="next" %>
<html>
<head>
    <n:head/>
</head>
<body>
    <n:panel>
        <div id="buttonContainer"></div>
    </n:panel>

    <script>
        var myButton = new MyButton();
        myButton.createButton(document.getElementById("buttonContainer"));
    </script>
</body>
</html>
```

---

## Next Framework Bridge API

Access via static import: `import static org.nextframework.js.NextGlobalJs.next;`

| API | Purpose |
|-----|---------|
| `next.dom` | DOM manipulation (newElement, query, etc.) |
| `next.ajax` | AJAX requests |
| `next.http` | HTTP utilities |
| `next.style` | CSS/styling |
| `next.events` | Event handling |
| `next.effects` | Visual effects |
| `next.util` | Utility functions |
| `next.dialogs` | Dialog components (alert, confirm, popup) |
| `next.datagrid` | Data grid component |
| `next.suggest` | Auto-suggest component |
| `next.reload` | Page reload utilities |

### AJAX Example

```java
import static org.nextframework.js.NextGlobalJs.next;
import org.nextframework.js.ajax.RequestInfo;

public class DataLoader {

    public void loadData() {
        RequestInfo request = new RequestInfo();
        request.url = "/api/data";
        request.callback = new Function1<Object, Void>() {
            public Void $invoke(Object response) {
                // Handle response
                return null;
            }
        };
        next.ajax.send(request);
    }
}
```

### Autobind - DOM Element Binding

The `next.dom.autobind()` function automatically binds DOM elements to Java class fields using naming conventions. This eliminates manual `getElementById` calls and event listener setup.

**Naming Conventions:**

| Pattern | Description |
|---------|-------------|
| Field `foo` | Binds to element with `id="#foo"` |
| Method `fooOnClick` | Binds `onclick` event of `#foo` to this method |
| Method `fooOnChange` | Binds `onchange` event of `#foo` to this method |

**Java Class:**

```java
package org.myapp.client;

import org.stjs.javascript.dom.Element;
import org.stjs.javascript.dom.Input;
import org.stjs.javascript.dom.DOMEvent;
import static org.nextframework.js.NextGlobalJs.next;

public class UserForm {

    // Fields bound to DOM elements by id
    public Input nameInput;
    public Input emailInput;
    public Element saveButton;
    public Element statusMessage;

    public void init(Element container) {
        // Bind all fields and events to DOM elements within container
        next.dom.autobind(this, container);
    }

    // Event handler: bound to #saveButton onclick
    public Boolean saveButtonOnClick(DOMEvent event) {
        String name = nameInput.value;
        String email = emailInput.value;

        if (name.length() == 0 || email.length() == 0) {
            statusMessage.innerHTML = "Please fill all fields";
            return false;
        }

        statusMessage.innerHTML = "Saving...";
        // Save logic here
        return true;
    }

    // Event handler: bound to #nameInput onchange
    public void nameInputOnChange(DOMEvent event) {
        next.log.debug("Name changed to: " + nameInput.value);
    }
}
```

**HTML Structure:**

```html
<div id="formContainer">
    <div>
        <label>Name:</label>
        <input id="#nameInput" type="text" />
    </div>
    <div>
        <label>Email:</label>
        <input id="#emailInput" type="email" />
    </div>
    <button id="#saveButton">Save</button>
    <div id="#statusMessage"></div>
</div>
```

**Initialization:**

```html
<script>
    var form = new UserForm();
    form.init(document.getElementById("formContainer"));
</script>
```

**Using in JSP (Next Framework):**

```jsp
<%@ taglib prefix="n" uri="next" %>
<html>
<head>
    <n:head/>
</head>
<body>
    <n:panel id="formContainer">
        <n:column>
            <label>Name:</label>
            <input id="#nameInput" type="text" />
        </n:column>
        <n:column>
            <label>Email:</label>
            <input id="#emailInput" type="email" />
        </n:column>
        <n:column>
            <button id="#saveButton">Save</button>
            <span id="#statusMessage"></span>
        </n:column>
    </n:panel>

    <script>
        var form = new UserForm();
        form.init(document.getElementById("formContainer"));
    </script>
</body>
</html>
```

**How It Works:**

1. `autobind` iterates over all properties in the object's prototype
2. For each non-function property (e.g., `nameInput`), it searches for an element with `id="#nameInput"` and assigns it to the property
3. For each function with `On` in the name (e.g., `saveButtonOnClick`), it extracts the element id (`#saveButton`) and event type (`click`), then binds the function as an event listener

**Implementation:** `org.nextframework.view/resources/org/nextframework/resource/next-modules.js` (NextDom.prototype.autobind)

---

## JavaScript Type Wrappers

Located in `org.stjs.javascript`:

| Java Type | JavaScript Equivalent |
|-----------|----------------------|
| `Array<T>` | JavaScript Array |
| `Map<K,V>` | JavaScript Object (as map) |
| `Date` | JavaScript Date |
| `Function1<P,R>` | Function with 1 param |
| `Function2<P1,P2,R>` | Function with 2 params |
| `Callback0` | Void callback, no params |
| `Callback1<P>` | Void callback, 1 param |

### Array Usage

```java
import org.stjs.javascript.Array;

Array<String> items = new Array<>();
items.push("item1");
items.push("item2");

for (int i = 0; i < items.$length(); i++) {
    String item = items.$get(i);
}
```

### Map Usage

```java
import org.stjs.javascript.Map;
import static org.stjs.javascript.JSCollections.$map;

Map<String, Object> options = $map(
    "width", 100,
    "height", 200,
    "visible", true
);

Object width = options.$get("width");
options.$put("visible", false);
```

---

## DOM API

Located in `org.stjs.javascript.dom`:

| Class | Purpose |
|-------|---------|
| `Document` | document object |
| `Element` | DOM element |
| `Input` | Input element |
| `Select` | Select element |
| `Table`, `TableRow`, `TableCell` | Table elements |
| `Form` | Form element |
| `DOMEvent` | Event object |

### DOM Example

```java
import org.stjs.javascript.Global;
import org.stjs.javascript.dom.Document;
import org.stjs.javascript.dom.Element;

public class DomExample {

    public void manipulate() {
        Document doc = Global.window.document;

        Element div = doc.getElementById("myDiv");
        div.innerHTML = "<p>Hello World</p>";
        div.style.display = "block";

        Element newSpan = doc.createElement("span");
        newSpan.className = "highlight";
        div.appendChild(newSpan);
    }
}
```

---

## Key Annotations

| Annotation | Purpose |
|------------|---------|
| `@GlobalScope` | Static members callable without class prefix |
| `@Namespace("name")` | JavaScript namespace for generated code |
| `@STJSBridge` | Marks external JavaScript library bridge |
| `@DataType` | Marks as data-only type (no methods generated) |
| `@JavascriptFunction` | Marks interface as function type |

### GlobalScope Example

```java
@GlobalScope
public class Utils {
    public static void log(String message) {
        Global.console.log(message);
    }
}

// Usage - no class prefix needed in generated JS:
// log("message") instead of Utils.log("message")
```

---

## Google Maps Integration

Located in `google.maps`:

```java
import google.maps.GoogleNamespace;
import google.maps.Map;
import google.maps.MapOptions;
import google.maps.LatLng;

public class MapExample {

    public void createMap(Element container) {
        MapOptions options = new MapOptions();
        options.center = new LatLng(-23.5505, -46.6333);
        options.zoom = 12;

        Map map = new Map(container, options);
    }
}
```

---

## Generating JavaScript (ANT)

ANT is the preferred way to transpile Java to JavaScript. The process requires two steps:
1. **Compile** the Java source files to `.class` files
2. **Generate** JavaScript from the compiled classes

> **Note:** In practice, the IDE (Eclipse, IntelliJ) automatically compiles Java files on save. You typically only need to run the generate step manually.

> **Important:** Java code intended for JavaScript transpilation must be placed in a separate folder (e.g., `js-builder/`) from your regular application sources. The transpiler processes all Java files in the source directory, and regular server-side classes cannot be converted to JavaScript. Only classes using STJS types (`Array`, `Map`, `Element`, etc.) are valid for transpilation.

### Project Structure

```
myproject/
├── js-builder/              # Java source files for transpilation
│   └── org/myapp/client/
│       └── MyComponent.java
├── js-bin/                  # Compiled .class files (generated)
├── generated-js/            # Output JavaScript files (generated)
└── build.xml                # ANT build file
```

### build.xml

```xml
<project name="MyApp JS Generator" default="generate-js">

    <property name="javascript.builder.dir" value="${basedir}/js-builder"/>
    <property name="javascript.generated.dir" value="${basedir}/generated-js"/>
    <property name="javascript.bin.dir" value="${basedir}/js-bin"/>

    <!-- Path to Next Framework installation -->
    <property name="nextframework.dir" value="/path/to/nextframework"/>

    <!-- Classpath for compilation (STJS types) -->
    <path id="compile.cp">
        <pathelement location="${nextframework.dir}/org.nextframework.stjs/bin"/>
        <pathelement location="${nextframework.dir}/org.nextframework.stjs/js-next/bin"/>
        <pathelement location="${nextframework.dir}/org.nextframework.stjs/js-lib/bin"/>
    </path>

    <!-- Classpath for generation -->
    <path id="stjs.cp">
        <!-- STJS generator and dependencies -->
        <pathelement location="${nextframework.dir}/org.nextframework.stjs/bin"/>
        <fileset dir="${nextframework.dir}/org.nextframework.stjs/provided">
            <include name="*.jar"/>
        </fileset>

        <!-- STJS types -->
        <pathelement location="${nextframework.dir}/org.nextframework.stjs/js-next/bin"/>
        <pathelement location="${nextframework.dir}/org.nextframework.stjs/js-lib/bin"/>

        <!-- Application compiled classes -->
        <pathelement location="${javascript.bin.dir}"/>
    </path>

    <!-- Step 1: Compile Java files -->
    <target name="compile-js">
        <mkdir dir="${javascript.bin.dir}"/>
        <javac srcdir="${javascript.builder.dir}"
               destdir="${javascript.bin.dir}"
               source="1.8"
               target="1.8"
               encoding="UTF-8"
               includeantruntime="false">
            <classpath refid="compile.cp"/>
        </javac>
    </target>

    <!-- Step 2: Generate JavaScript files -->
    <target name="generate-js" depends="compile-js">
        <antcall target="generate-js-only"/>
    </target>

    <!-- Generate only (use when IDE compiles the classes) -->
    <target name="generate-js-only">
        <mkdir dir="${javascript.generated.dir}"/>
        <apply executable="java" verbose="true">
            <fileset dir="${javascript.builder.dir}">
                <include name="**/*.java"/>
            </fileset>
            <arg value="-cp"/>
            <arg pathref="stjs.cp"/>
            <arg value="org.stjs.generator.QuickGenerator"/>
            <arg value="${javascript.builder.dir}"/>
            <arg value="${javascript.generated.dir}"/>
            <arg value="[]"/>
            <arg pathref="stjs.cp"/>
        </apply>
    </target>

    <!-- Clean generated files -->
    <target name="clean">
        <delete dir="${javascript.bin.dir}"/>
        <delete dir="${javascript.generated.dir}"/>
    </target>

</project>
```

### Running the Build

```bash
# Generate JavaScript (compiles first, then transpiles)
ant generate-js

# Generate only (when IDE already compiled the classes)
ant generate-js-only

# Clean generated files
ant clean
```

---

## Runtime Support

The generated JavaScript requires the STJS runtime library which provides:

- Java-like String methods (`equals`, `startsWith`, `endsWith`, `trim`, `compareTo`)
- Type conversion utilities (`stjs.typefy`, `stjs.stringify`)
- Collection method support

The runtime is located at `generator/src/main/resources/stjs.js`.

---

## Programmatic Usage

```java
import org.stjs.generator.Generator;
import org.stjs.generator.GeneratorConfigurationBuilder;

public class GenerateJs {

    public static void main(String[] args) throws Exception {
        GeneratorConfigurationBuilder config = new GeneratorConfigurationBuilder();
        config.allowedPackage("org.myapp.client");
        config.allowedPackage("org.stjs.javascript");
        config.allowedPackage("org.nextframework.js");

        Generator generator = new Generator();
        generator.generateJavascript(
            Thread.currentThread().getContextClassLoader(),
            "org.myapp.client.MyButton",
            new File("src/main/java"),
            new File("target/generated-js"),
            new File("target"),
            config.build()
        );
    }
}
```

---

## Output Files

The generator produces:

| File | Description |
|------|-------------|
| `*.js` | Generated JavaScript code |
| `*.stjs` | Metadata file with dependencies and source info |

The `.stjs` files are Java properties files containing:
- `dependencies` - Required classes
- `generated.js` - Generated JS file location
- `source` - Original Java source location

---

## Building the Framework

> **Note:** This section is for developers building the Next Framework itself, not for application developers using the framework.

### Compile the Module

The stjs module is compiled as part of the full build:

```bash
cd org.nextframework.build
source tools/setup-env.sh
```

Or compile individually (after dependencies are downloaded):

```bash
./tools/compile-all.sh
```

### Generate JavaScript

After compilation, generate JavaScript files from Java sources:

```bash
./tools/generate-js.sh
```

This runs the stjs `build.xml` which:
1. Reads Java files from `js-builder/` directories
2. Transpiles them to JavaScript using `QuickGenerator`
3. Outputs to `org.nextframework.view/resources/`

Generated files:
- `NextDataGrid.js`
- `NextDialogs.js`
- `NextReload.js`
- `NextSuggest.js`
- `NextSuggestAjaxProvider.js`
- `NextSuggestStaticListProvider.js`
- `NextSuggestSuggestionProvider.js`
