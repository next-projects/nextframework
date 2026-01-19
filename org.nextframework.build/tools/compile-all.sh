#!/bin/bash
#
# Compiles all project modules in dependency order
#
# Prerequisites: Run setup-env.sh --deps first
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

# Modules in dependency order
MODULES=(
    # Tier 1: No internal dependencies
    "org.nextframework.core"
    "org.nextframework.services"

    # Tier 2: Depends on tier 1
    "org.nextframework.beans"
    "org.nextframework.compilation"

    # Tier 3: Depends on tier 2
    "org.nextframework.context"
    "org.nextframework.types"
    "org.nextframework.validation"
    "org.nextframework.authorization"
    "org.nextframework.summary"

    # Tier 4: Depends on tier 3
    "org.nextframework.persistence"
    "org.nextframework.controller"
    "org.nextframework.web"
    "org.nextframework.authorization.dashboard"

    # Tier 5: Depends on tier 4
    "org.nextframework.dao"
    "org.nextframework.view"

    # Tier 6: Depends on tier 5
    "org.nextframework.chart"
    "org.nextframework.report"
    "org.nextframework.report.generator"
    "org.nextframework.legacy"
    "org.nextframework.stjs"
    "org.nextframework.jsbuilder"
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

# Compile a single module
compile_module() {
    local module="$1"
    local module_dir="$PROJECT_ROOT/$module"
    local src_dir="$module_dir/src"
    local bin_dir="$module_dir/bin"

    # Skip if no src directory
    if [ ! -d "$src_dir" ]; then
        warn "$module: No src/ directory, skipping"
        return 0
    fi

    # Find all Java files
    local java_files
    java_files=$(find "$src_dir" -name "*.java" 2>/dev/null)

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

    # Compile
    if [ -n "$classpath" ]; then
        javac -d "$bin_dir" -cp "$classpath" -source 1.8 -target 1.8 -encoding UTF-8 \
            $(find "$src_dir" -name "*.java") 2>&1
    else
        javac -d "$bin_dir" -source 1.8 -target 1.8 -encoding UTF-8 \
            $(find "$src_dir" -name "*.java") 2>&1
    fi

    if [ $? -eq 0 ]; then
        info "$module: OK"
        return 0
    else
        error "$module: FAILED"
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
    local skipped=0

    for module in "${MODULES[@]}"; do
        if compile_module "$module"; then
            ((compiled++))
        else
            ((failed++))
            # Continue compiling other modules
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
