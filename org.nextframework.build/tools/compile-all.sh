#!/bin/bash
#
# Compiles all project modules in dependency order
#
# Prerequisites: Run setup-env.sh first (for dependencies)
#
# Usage:
#   ./tools/compile-all.sh
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$(dirname "$SCRIPT_DIR")"
PROJECT_ROOT="$(dirname "$BUILD_DIR")"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

info() { echo -e "${GREEN}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Modules in dependency order (excluding circular dependencies handled separately)
MODULES_PHASE1=(
    # Tier 1: No internal dependencies
    "org.nextframework.core"
    "org.nextframework.services"

    # Tier 2: Depends on tier 1
    "org.nextframework.beans"
    "org.nextframework.compilation"

    # Tier 3: Depends on tier 2
    "org.nextframework.context"

    # Tier 4: Depends on context
    "org.nextframework.summary"      # Has Incrementable interface

    # Tier 5: Depends on summary
    "org.nextframework.types"        # Needs Incrementable from summary

    # Tier 6: Depends on context
    "org.nextframework.web"

    # Tier 7: Depends on types and web
    "org.nextframework.validation"

    # Tier 8: Depends on tier 6/7
    "org.nextframework.persistence"
    "org.nextframework.authorization" # web-src needs web
)

# Circular dependency group 1: controller <-> dao
# controller needs ListViewFilter, PageAndOrder, ResultList from dao
# dao has classes in org.nextframework.controller.crud package
MODULES_CIRCULAR_1=(
    "org.nextframework.controller"
    "org.nextframework.dao"
)

# jsbuilder must be compiled before view/chart (chart google-src needs view.js package)
MODULES_PRE_VIEW=(
    "org.nextframework.jsbuilder"
)

# Circular dependency group 2: view <-> chart
MODULES_CIRCULAR_2=(
    "org.nextframework.view"
    "org.nextframework.chart"
)

# Modules that depend on circular modules
MODULES_PHASE2=(
    "org.nextframework.authorization.dashboard"
    "org.nextframework.report"
    "org.nextframework.stjs"           # Must be before report.generator (js-builder needs stjs)
    "org.nextframework.report.generator"
    "org.nextframework.legacy"
)

# All modules for classpath building
MODULES=(
    "${MODULES_PHASE1[@]}"
    "${MODULES_CIRCULAR_1[@]}"
    "${MODULES_PRE_VIEW[@]}"
    "${MODULES_CIRCULAR_2[@]}"
    "${MODULES_PHASE2[@]}"
)

# Build classpath for a module (includes ALL lib/, provided/, and bin/ directories)
build_classpath() {
    local module="$1"
    local cp=""

    # Add ALL module lib and provided jars (dependencies are distributed across modules)
    for mod in "${MODULES[@]}"; do
        local mod_dir="$PROJECT_ROOT/$mod"
        if [ -d "$mod_dir/lib" ]; then
            for jar in "$mod_dir/lib"/*.jar; do
                [ -f "$jar" ] && cp="$cp:$jar"
            done
        fi
        if [ -d "$mod_dir/provided" ]; then
            for jar in "$mod_dir/provided"/*.jar; do
                [ -f "$jar" ] && cp="$cp:$jar"
            done
        fi
    done

    # Add all module bin directories (for cross-module dependencies)
    for mod in "${MODULES[@]}"; do
        local bin_dir="$PROJECT_ROOT/$mod/bin"
        if [ -d "$bin_dir" ]; then
            cp="$cp:$bin_dir"
        fi
    done

    # Remove leading colon
    echo "${cp#:}"
}

# Get source directories from .classpath file
get_src_dirs_from_classpath() {
    local module_dir="$1"
    local classpath_file="$module_dir/.classpath"
    local src_dirs=""

    if [ -f "$classpath_file" ]; then
        # Extract src paths from .classpath (exclude project references starting with /)
        while IFS= read -r path; do
            # Skip project references (start with /)
            # Skip test directories (require JUnit)
            # Skip resources directories (no Java files)
            # Skip generated directories
            if [[ ! "$path" =~ ^/ ]] && \
               [[ "$path" != "test" ]] && \
               [[ "$path" != "resources" ]] && \
               [[ "$path" != "generated" ]] && \
               [[ ! "$path" =~ /test$ ]] && \
               [[ ! "$path" =~ /resources$ ]]; then
                local full_path="$module_dir/$path"
                if [ -d "$full_path" ]; then
                    src_dirs="$src_dirs $full_path"
                fi
            fi
        done < <(grep 'kind="src"' "$classpath_file" | sed 's/.*path="\([^"]*\)".*/\1/')
    fi

    echo "$src_dirs"
}

# Copy resources to bin directory
copy_resources() {
    local module_dir="$1"
    local bin_dir="$module_dir/bin"
    local resources_dir="$module_dir/resources"

    # Copy from resources/ directory
    if [ -d "$resources_dir" ]; then
        cp -r "$resources_dir"/* "$bin_dir/" 2>/dev/null || true
    fi

    # Copy non-Java files from src directories (JSP, properties, xml, etc.)
    for src_dir in "$module_dir"/src "$module_dir"/*-src; do
        if [ -d "$src_dir" ]; then
            find "$src_dir" -type f \( -name "*.jsp" -o -name "*.properties" -o -name "*.xml" -o -name "*.tld" -o -name "*.js" -o -name "*.css" \) 2>/dev/null | while read file; do
                rel_path="${file#$src_dir/}"
                target_dir="$bin_dir/$(dirname "$rel_path")"
                mkdir -p "$target_dir"
                cp "$file" "$target_dir/" 2>/dev/null || true
            done
        fi
    done
}

# Compile a single module
compile_module() {
    local module="$1"
    local module_dir="$PROJECT_ROOT/$module"
    local bin_dir="$module_dir/bin"

    # Get source directories from .classpath file
    local src_dirs
    src_dirs=$(get_src_dirs_from_classpath "$module_dir")

    # Fallback to pattern matching if .classpath not found or empty
    if [ -z "$src_dirs" ]; then
        for pattern in "src" "web-src" "*-src"; do
            for dir in "$module_dir"/$pattern; do
                if [ -d "$dir" ]; then
                    src_dirs="$src_dirs $dir"
                fi
            done
        done
    fi

    # Skip if no source directories
    if [ -z "$src_dirs" ]; then
        warn "$module: No source directories, skipping"
        return 0
    fi

    # Find all Java files across all source directories
    local java_files
    java_files=$(find $src_dirs -name "*.java" 2>/dev/null)

    if [ -z "$java_files" ]; then
        warn "$module: No .java files found, skipping"
        return 0
    fi

    # Create bin directory
    mkdir -p "$bin_dir"

    # Build classpath
    local classpath
    classpath=$(build_classpath "$module")

    # Count files
    local file_count
    file_count=$(echo "$java_files" | wc -l)

    info "Compiling $module ($file_count files)..."

    # Compile all source directories together
    if [ -n "$classpath" ]; then
        javac -d "$bin_dir" -cp "$classpath" -source 1.8 -target 1.8 -encoding UTF-8 \
            $(find $src_dirs -name "*.java") 2>&1
    else
        javac -d "$bin_dir" -source 1.8 -target 1.8 -encoding UTF-8 \
            $(find $src_dirs -name "*.java") 2>&1
    fi

    if [ $? -eq 0 ]; then
        # Copy resources (META-INF, etc.) to bin
        copy_resources "$module_dir"
        info "$module: OK"
        return 0
    else
        error "$module: FAILED"
        return 1
    fi
}

# Compile multiple modules together (for circular dependencies)
compile_modules_together() {
    local modules=("$@")
    local all_src_dirs=""
    local first_module="${modules[0]}"

    info "Compiling together: ${modules[*]}..."

    # Collect all source directories from all modules
    for module in "${modules[@]}"; do
        local module_dir="$PROJECT_ROOT/$module"
        local module_src_dirs
        module_src_dirs=$(get_src_dirs_from_classpath "$module_dir")

        # Fallback to pattern matching
        if [ -z "$module_src_dirs" ]; then
            for pattern in "src" "web-src" "*-src"; do
                for dir in "$module_dir"/$pattern; do
                    if [ -d "$dir" ]; then
                        module_src_dirs="$module_src_dirs $dir"
                    fi
                done
            done
        fi

        all_src_dirs="$all_src_dirs $module_src_dirs"
    done

    # Find all Java files
    local java_files
    java_files=$(find $all_src_dirs -name "*.java" 2>/dev/null)

    if [ -z "$java_files" ]; then
        warn "No .java files found, skipping"
        return 0
    fi

    # Create bin directories for all modules
    for module in "${modules[@]}"; do
        mkdir -p "$PROJECT_ROOT/$module/bin"
    done

    # Build classpath
    local classpath
    classpath=$(build_classpath "$first_module")

    local file_count
    file_count=$(echo "$java_files" | wc -l)
    info "Compiling $file_count files..."

    # Compile to first module's bin (will contain all classes)
    local bin_dir="$PROJECT_ROOT/$first_module/bin"
    javac -d "$bin_dir" -cp "$classpath" -source 1.8 -target 1.8 -encoding UTF-8 \
        $(find $all_src_dirs -name "*.java") 2>&1

    if [ $? -eq 0 ]; then
        # Copy classes to each module's bin based on package (exclude META-INF)
        for module in "${modules[@]}"; do
            if [ "$module" != "$first_module" ]; then
                # Copy only class files, not META-INF (to avoid duplicate web-fragments)
                find "$bin_dir" -name "*.class" -exec cp --parents {} "$PROJECT_ROOT/$module/bin/" \; 2>/dev/null || \
                    rsync -a --include='*/' --include='*.class' --exclude='*' "$bin_dir/" "$PROJECT_ROOT/$module/bin/" 2>/dev/null || true
            fi
            # Copy resources (META-INF, etc.) to bin - only for this module's own resources
            copy_resources "$PROJECT_ROOT/$module"
        done
        info "${modules[*]}: OK"
        return 0
    else
        error "${modules[*]}: FAILED"
        return 1
    fi
}

# Main
main() {
    echo ""
    echo "========================================"
    echo "  Compiling Next Framework Modules"
    echo "========================================"
    echo ""

    # Check Java
    if ! command -v javac &> /dev/null; then
        error "javac not found. Please install Java 8 JDK."
        return 1
    fi

    local java_version
    java_version=$(javac -version 2>&1)
    info "Using: $java_version"
    echo ""

    local failed=0
    local compiled=0

    # Phase 1: Compile modules without circular dependencies
    for module in "${MODULES_PHASE1[@]}"; do
        if compile_module "$module"; then
            ((compiled++))
        else
            ((failed++))
        fi
    done

    # Phase 2a: Compile controller <-> dao together (circular dependency)
    if compile_modules_together "${MODULES_CIRCULAR_1[@]}"; then
        ((compiled+=${#MODULES_CIRCULAR_1[@]}))
    else
        ((failed+=${#MODULES_CIRCULAR_1[@]}))
    fi

    # Phase 2b: Compile jsbuilder (needed by chart google-src for view.js package)
    for module in "${MODULES_PRE_VIEW[@]}"; do
        if compile_module "$module"; then
            ((compiled++))
        else
            ((failed++))
        fi
    done

    # Phase 2c: Compile view <-> chart together (circular dependency)
    if compile_modules_together "${MODULES_CIRCULAR_2[@]}"; then
        ((compiled+=${#MODULES_CIRCULAR_2[@]}))
    else
        ((failed+=${#MODULES_CIRCULAR_2[@]}))
    fi

    # Phase 3: Compile remaining modules
    for module in "${MODULES_PHASE2[@]}"; do
        if compile_module "$module"; then
            ((compiled++))
        else
            ((failed++))
        fi
    done

    echo ""
    echo "========================================"
    info "Compiled: $compiled, Failed: $failed"
    echo "========================================"

    if [ $failed -gt 0 ]; then
        return 1
    fi
    return 0
}

main "$@"
