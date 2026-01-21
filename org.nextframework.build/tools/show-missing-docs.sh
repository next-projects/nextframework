#!/bin/bash
#
# Shows modules missing documentation that have at least one dependency with docs.
# Format: table with module on left, dependencies on right (✓ = has docs)
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
DOC_DIR="$PROJECT_ROOT/documentation"

# Get all modules
ALL_MODULES=$(ls -d "$PROJECT_ROOT"/org.nextframework.* 2>/dev/null | xargs -n1 basename | sort)

# Get modules with documentation
MODULES_WITH_DOCS=$(ls -d "$DOC_DIR"/org.nextframework.* 2>/dev/null | xargs -n1 basename | sort)

# Function to check if module has docs
has_docs() {
    local module="$1"
    for doc_module in $MODULES_WITH_DOCS; do
        if [ "$module" = "$doc_module" ]; then
            return 0
        fi
    done
    return 1
}

# Function to get dependencies from .classpath
get_dependencies() {
    local module="$1"
    local classpath_file="$PROJECT_ROOT/$module/.classpath"
    if [ -f "$classpath_file" ]; then
        grep 'kind="src" path="/org.nextframework' "$classpath_file" | \
            sed 's/.*path="\/\(org\.nextframework[^"]*\)".*/\1/' | \
            sort -u
    fi
}

# Function to get short module name (remove org.nextframework. prefix)
short_name() {
    echo "$1" | sed 's/org\.nextframework\.//'
}

# Column width
COL1=25

# Print header
printf "%-${COL1}s  %s\n" "MODULE (missing docs)" "DEPENDENCIES (✓ = has docs)"
printf "%-${COL1}s  %s\n" "---------------------" "----------------------------"

# Process each module
for module in $ALL_MODULES; do
    # Skip if module has docs
    if has_docs "$module"; then
        continue
    fi

    # Get dependencies
    deps=$(get_dependencies "$module")

    # Check if at least one dependency has docs
    has_dep_with_docs=false

    for dep in $deps; do
        if has_docs "$dep"; then
            has_dep_with_docs=true
            break
        fi
    done

    # Only show if has at least one dependency with docs
    if $has_dep_with_docs; then
        # Build dependency list
        first=true
        dep_list=""
        for dep in $deps; do
            short=$(short_name "$dep")
            if has_docs "$dep"; then
                entry="✓ $short"
            else
                entry="$short"
            fi
            if $first; then
                dep_list="$entry"
                first=false
            else
                dep_list="$dep_list, $entry"
            fi
        done

        short_module=$(short_name "$module")
        printf "%-${COL1}s  %s\n" "$short_module" "$dep_list"
    fi
done
