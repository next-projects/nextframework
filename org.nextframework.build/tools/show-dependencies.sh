#!/bin/bash
#
# Prints module dependencies in YAML format
#
# Usage:
#   ./tools/show-dependencies.sh
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$(dirname "$SCRIPT_DIR")"
PROJECT_ROOT="$(dirname "$BUILD_DIR")"

echo "# Next Framework Module Dependencies"
echo "# Generated: $(date -Iseconds)"
echo ""
echo "modules:"

for proj in "$PROJECT_ROOT"/org.nextframework.*/; do
    name=$(basename "$proj")

    # Skip build module
    [[ "$name" == "org.nextframework.build" ]] && continue

    echo "  $name:"

    # Get internal dependencies from .classpath
    deps=$(grep 'kind="src"' "$proj/.classpath" 2>/dev/null | grep 'path="/' | sed 's/.*path="\/\([^"]*\)".*/\1/')

    if [ -n "$deps" ]; then
        echo "    depends_on:"
        echo "$deps" | while read -r dep; do
            [ -n "$dep" ] && echo "      - $dep"
        done
    else
        echo "    depends_on: []"
    fi
done
