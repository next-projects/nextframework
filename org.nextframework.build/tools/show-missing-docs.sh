#!/bin/bash
#
# Shows modules missing documentation that have at least one dependency with docs.
# Format: table with module on left, dependencies on right (✓ = has docs)
#
# Usage: ./show-missing-docs.sh [--all]
#   --all  Show all modules, marking documented ones with ✓
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
DOC_DIR="$PROJECT_ROOT/documentation"

SHOW_ALL=false
if [ "$1" = "--all" ]; then
    SHOW_ALL=true
fi

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
if $SHOW_ALL; then
    printf "%-${COL1}s  %s\n" "MODULE (✓ = has docs)" "DEPENDENCIES (✓ = has docs)"
    printf "%-${COL1}s  %s\n" "---------------------" "----------------------------"
else
    printf "%-${COL1}s  %s\n" "MODULE (missing docs)" "DEPENDENCIES (✓ = has docs)"
    printf "%-${COL1}s  %s\n" "---------------------" "----------------------------"
fi

# Counters for --all mode
total_count=0
docs_count=0

# Process each module
for module in $ALL_MODULES; do
    total_count=$((total_count + 1))

    module_has_docs=false
    if has_docs "$module"; then
        module_has_docs=true
        docs_count=$((docs_count + 1))
    fi

    # Skip if module has docs (unless --all)
    if ! $SHOW_ALL && $module_has_docs; then
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

    # Only show if has at least one dependency with docs (unless --all)
    if $SHOW_ALL || $has_dep_with_docs; then
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
        if $SHOW_ALL && $module_has_docs; then
            printf "✓ %-$((COL1-2))s  %s\n" "$short_module" "$dep_list"
        else
            printf "%-${COL1}s  %s\n" "$short_module" "$dep_list"
        fi
    fi
done

# Print summary for --all mode
if $SHOW_ALL; then
    echo ""
    echo "Documented: $docs_count / $total_count"
fi
