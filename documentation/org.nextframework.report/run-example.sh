#!/bin/bash
#
# Compiles and runs the SalesReportExample
#
# Usage: ./run-example.sh
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

echo "Project root: $PROJECT_ROOT"

# All modules needed for the report
MODULES=(
    "org.nextframework.core"
    "org.nextframework.services"
    "org.nextframework.beans"
    "org.nextframework.compilation"
    "org.nextframework.context"
    "org.nextframework.summary"
    "org.nextframework.types"
    "org.nextframework.persistence"
    "org.nextframework.jsbuilder"
    "org.nextframework.view"
    "org.nextframework.chart"
    "org.nextframework.report"
)

# Build classpath from bin directories, resources, and lib jars
CP=""
for mod in "${MODULES[@]}"; do
    mod_dir="$PROJECT_ROOT/$mod"

    # Add bin directory
    if [ -d "$mod_dir/bin" ]; then
        CP="$CP:$mod_dir/bin"
    fi

    # Add resources directory (for META-INF/services)
    if [ -d "$mod_dir/resources" ]; then
        CP="$CP:$mod_dir/resources"
    fi

    # Add source directories with resources (for .jrxml templates, etc.)
    for src_dir in "$mod_dir"/*-src; do
        if [ -d "$src_dir" ]; then
            CP="$CP:$src_dir"
        fi
    done

    # Add lib jars
    if [ -d "$mod_dir/lib" ]; then
        for jar in "$mod_dir/lib"/*.jar; do
            [ -f "$jar" ] && CP="$CP:$jar"
        done
    fi

    # Add provided jars
    if [ -d "$mod_dir/provided" ]; then
        for jar in "$mod_dir/provided"/*.jar; do
            [ -f "$jar" ] && CP="$CP:$jar"
        done
    fi
done

# Remove leading colon
CP="${CP#:}"

# Create temp directory for compilation
TEMP_DIR="$SCRIPT_DIR/temp"
mkdir -p "$TEMP_DIR"

echo ""
echo "Compiling Sale.java and SalesReportExample.java..."
javac -d "$TEMP_DIR" -cp "$CP" -source 1.8 -target 1.8 -encoding UTF-8 \
    "$SCRIPT_DIR/Sale.java" "$SCRIPT_DIR/SalesReportExample.java"

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo "Compilation successful."
echo ""
echo "Running SalesReportExample..."
java -cp "$TEMP_DIR:$CP" org.nextframework.report.example.SalesReportExample "$SCRIPT_DIR/SalesReport.pdf"

if [ $? -eq 0 ]; then
    echo ""
    echo "Success! PDF generated at: $SCRIPT_DIR/SalesReport.pdf"
else
    echo "Execution failed!"
    exit 1
fi

# Cleanup
rm -rf "$TEMP_DIR"
