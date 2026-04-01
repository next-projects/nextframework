#!/bin/bash
#
# Compiles all test classes across modules
#
# Prerequisites: Run compile-all.sh first (source must be compiled)
#
# Usage:
#   ./tools/compile-tests.sh
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

# All modules (for classpath building)
ALL_MODULES=(
    "org.nextframework.core"
    "org.nextframework.services"
    "org.nextframework.beans"
    "org.nextframework.compilation"
    "org.nextframework.context"
    "org.nextframework.summary"
    "org.nextframework.types"
    "org.nextframework.web"
    "org.nextframework.validation"
    "org.nextframework.persistence"
    "org.nextframework.authorization"
    "org.nextframework.controller"
    "org.nextframework.dao"
    "org.nextframework.jsbuilder"
    "org.nextframework.view"
    "org.nextframework.chart"
    "org.nextframework.authorization.dashboard"
    "org.nextframework.report"
    "org.nextframework.stjs"
    "org.nextframework.report.generator"
    "org.nextframework.legacy"
)

# Build full classpath (bin + lib + provided + lib-test for all modules)
build_full_classpath() {
    local cp=""
    for mod in "${ALL_MODULES[@]}"; do
        local mod_dir="$PROJECT_ROOT/$mod"
        if [ -d "$mod_dir/bin" ]; then
            cp="$cp:$mod_dir/bin"
        fi
        for dir in lib provided lib-test; do
            if [ -d "$mod_dir/$dir" ]; then
                for jar in "$mod_dir/$dir"/*.jar; do
                    if [ -f "$jar" ]; then
                        cp="$cp:$jar"
                    fi
                done
            fi
        done
    done
    echo "${cp#:}"
}

# Compile test classes for a module
compile_test_module() {
    local module="$1"
    local classpath="$2"
    local module_dir="$PROJECT_ROOT/$module"
    local test_dir="$module_dir/test"
    local bin_dir="$module_dir/bin"

    if [ ! -d "$test_dir" ]; then
        return 0
    fi

    local java_files
    java_files=$(find "$test_dir" -name "*.java" 2>/dev/null)

    if [ -z "$java_files" ]; then
        return 0
    fi

    local file_count
    file_count=$(echo "$java_files" | wc -l)

    info "Compiling tests: $module ($file_count files)..."

    mkdir -p "$bin_dir"

    local output
    output=$(javac -d "$bin_dir" -cp "$classpath" $java_files 2>&1)
    local rc=$?

    if [ $rc -eq 0 ]; then
        # Show warnings but don't fail
        if [ -n "$output" ]; then
            echo "$output" | grep -v "^$" | head -5
        fi
        return 0
    else
        error "$module tests: COMPILATION FAILED"
        echo "$output"
        return 1
    fi
}

main() {
    echo ""
    echo "========================================"
    echo "  Compiling Test Classes"
    echo "========================================"
    echo ""

    if ! command -v javac &> /dev/null; then
        error "javac not found. Please install Java JDK."
        return 1
    fi

    info "Building classpath..."
    local classpath
    classpath=$(build_full_classpath)

    local failed=0
    local compiled=0

    for mod in "${ALL_MODULES[@]}"; do
        if [ -d "$PROJECT_ROOT/$mod/test" ]; then
            if compile_test_module "$mod" "$classpath"; then
                ((compiled++))
            else
                ((failed++))
            fi
        fi
    done

    echo ""
    echo "========================================"
    info "Test modules compiled: $compiled, Failed: $failed"
    echo "========================================"

    if [ $failed -gt 0 ]; then
        return 1
    fi
    return 0
}

main "$@"
