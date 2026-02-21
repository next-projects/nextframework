#!/bin/bash
#
# Compiles and runs all unit tests
#
# On WSL, copies the project to a temp folder first for performance.
# On native Linux/macOS, runs directly from the project directory.
#
# Prerequisites: Run compile-all.sh first (source must be compiled)
#
# Usage:
#   ./tools/run-tests.sh              # compile tests + run all
#   ./tools/run-tests.sh --skip-compile  # skip test compilation, just run
#   ./tools/run-tests.sh TestContextUnit # run a specific test class (substring match)
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$(dirname "$SCRIPT_DIR")"
PROJECT_ROOT="$(dirname "$BUILD_DIR")"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
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

# Detect WSL
is_wsl() {
    if [ -f /proc/version ]; then
        grep -qi "microsoft\|wsl" /proc/version 2>/dev/null && return 0
    fi
    return 1
}

# Build full classpath (bin + lib + provided + lib-test for all modules)
build_full_classpath() {
    local root="$1"
    local cp=""
    for mod in "${ALL_MODULES[@]}"; do
        local mod_dir="$root/$mod"
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

# Find all test classes by scanning for files containing @Test
find_test_classes() {
    local root="$1"
    local filter="$2"
    local test_classes=()

    for mod in "${ALL_MODULES[@]}"; do
        local test_dir="$root/$mod/test"
        if [ ! -d "$test_dir" ]; then
            continue
        fi

        while IFS= read -r java_file; do
            # Check if the file contains @Test annotation
            if grep -q "@Test" "$java_file" 2>/dev/null; then
                # Convert file path to class name
                local rel_path="${java_file#$test_dir/}"
                local class_name="${rel_path%.java}"
                class_name="${class_name//\//.}"

                # Apply filter if provided
                if [ -n "$filter" ]; then
                    if [[ "$class_name" != *"$filter"* ]]; then
                        continue
                    fi
                fi

                test_classes+=("$class_name")
            fi
        done < <(find "$test_dir" -name "*.java" 2>/dev/null)
    done

    echo "${test_classes[@]}"
}

# Run a single test class and capture results
run_test_class() {
    local classpath="$1"
    local test_class="$2"
    local short_name="${test_class##*.}"

    local result
    result=$(java -cp "$classpath" org.junit.runner.JUnitCore "$test_class" 2>&1)
    local rc=$?

    if echo "$result" | grep -q "FAILURES"; then
        local tests
        tests=$(echo "$result" | grep "Tests run:" | sed 's/Tests run: \([0-9]*\).*/\1/')
        local fails
        fails=$(echo "$result" | grep "Tests run:" | sed 's/.*Failures: \([0-9]*\).*/\1/')
        local pass=$((tests - fails))
        echo -e "${RED}FAIL${NC} $short_name ($pass passed, $fails failed)"

        # Show failure details
        echo "$result" | grep -A 3 "^[0-9]*)" | while IFS= read -r line; do
            echo "       $line"
        done

        echo "RESULT:$pass:$fails"
    elif echo "$result" | grep -q "OK"; then
        local tests
        tests=$(echo "$result" | grep "OK" | sed 's/OK (\([0-9]*\) test.*/\1/')
        echo -e "${GREEN}OK${NC}   $short_name ($tests tests)"
        echo "RESULT:$tests:0"
    else
        echo -e "${RED}ERROR${NC} $short_name"
        echo "$result" | tail -3 | while IFS= read -r line; do
            echo "       $line"
        done
        echo "RESULT:0:1"
    fi
}

# Copy project to temp directory (for WSL)
# Outputs only the temp dir path to stdout (info messages go to stderr)
copy_to_temp() {
    local temp_dir="/tmp/nextframework-test"

    if [ -d "$temp_dir" ]; then
        info "Cleaning previous temp copy..." >&2
        rm -rf "$temp_dir"
    fi

    info "Copying project to $temp_dir (WSL optimization)..." >&2

    mkdir -p "$temp_dir"

    # Copy only what's needed: bin/, lib/, lib-test/, provided/, test/ for each module
    for mod in "${ALL_MODULES[@]}"; do
        local src="$PROJECT_ROOT/$mod"
        local dst="$temp_dir/$mod"

        if [ ! -d "$src" ]; then
            continue
        fi

        mkdir -p "$dst"

        for dir in bin lib lib-test provided test resources; do
            if [ -d "$src/$dir" ]; then
                cp -a "$src/$dir" "$dst/$dir"
            fi
        done

        # Copy .classpath if present (needed by compile-tests)
        if [ -f "$src/.classpath" ]; then
            cp "$src/.classpath" "$dst/"
        fi
    done

    # Copy build tools
    mkdir -p "$temp_dir/org.nextframework.build/tools"
    cp "$SCRIPT_DIR/compile-tests.sh" "$temp_dir/org.nextframework.build/tools/"

    echo "$temp_dir"
}

main() {
    local skip_compile=false
    local filter=""

    # Parse arguments
    for arg in "$@"; do
        case "$arg" in
            --skip-compile)
                skip_compile=true
                ;;
            *)
                filter="$arg"
                ;;
        esac
    done

    echo ""
    echo "========================================"
    echo "  Next Framework - Test Runner"
    echo "========================================"
    echo ""

    # Check Java
    if ! command -v java &> /dev/null; then
        error "java not found. Please install Java JDK."
        return 1
    fi

    local work_dir="$PROJECT_ROOT"

    # WSL detection and temp copy
    if is_wsl; then
        info "WSL detected - copying to /tmp for performance..."
        work_dir=$(copy_to_temp)
        info "Working from: $work_dir"
    else
        info "Running directly from project directory"
    fi

    echo ""

    # Compile tests
    if [ "$skip_compile" = false ]; then
        info "Compiling test classes..."
        echo ""

        # Update PROJECT_ROOT for compile-tests.sh if using temp dir
        if [ "$work_dir" != "$PROJECT_ROOT" ]; then
            local compile_script="$work_dir/org.nextframework.build/tools/compile-tests.sh"
            if [ -f "$compile_script" ]; then
                bash "$compile_script"
            else
                error "compile-tests.sh not found in temp directory"
                return 1
            fi
        else
            bash "$SCRIPT_DIR/compile-tests.sh"
        fi

        if [ $? -ne 0 ]; then
            error "Test compilation failed. Fix errors and retry."
            return 1
        fi
        echo ""
    fi

    # Build classpath
    info "Building classpath..."
    local classpath
    classpath=$(build_full_classpath "$work_dir")

    # Find test classes
    info "Discovering test classes..."
    local test_classes_str
    test_classes_str=$(find_test_classes "$work_dir" "$filter")

    if [ -z "$test_classes_str" ]; then
        if [ -n "$filter" ]; then
            error "No test classes found matching '$filter'"
        else
            error "No test classes found"
        fi
        return 1
    fi

    read -ra test_classes <<< "$test_classes_str"
    local count=${#test_classes[@]}

    if [ -n "$filter" ]; then
        info "Running $count test class(es) matching '$filter'..."
    else
        info "Running $count test classes..."
    fi
    echo ""

    # Run tests and collect results
    local total_pass=0
    local total_fail=0
    local failed_classes=()

    for test_class in "${test_classes[@]}"; do
        local output
        output=$(run_test_class "$classpath" "$test_class")

        # Print output (all lines except RESULT: marker)
        echo "$output" | grep -v "^RESULT:"

        # Parse result
        local result_line
        result_line=$(echo "$output" | grep "^RESULT:")
        local pass
        pass=$(echo "$result_line" | cut -d: -f2)
        local fail
        fail=$(echo "$result_line" | cut -d: -f3)

        total_pass=$((total_pass + pass))
        total_fail=$((total_fail + fail))

        if [ "$fail" -gt 0 ]; then
            failed_classes+=("${test_class##*.}")
        fi
    done

    # Summary
    local total=$((total_pass + total_fail))
    echo ""
    echo "========================================"
    if [ $total_fail -eq 0 ]; then
        echo -e "  ${GREEN}ALL TESTS PASSED${NC}"
    else
        echo -e "  ${RED}SOME TESTS FAILED${NC}"
    fi
    echo ""
    echo -e "  Total:  $total"
    echo -e "  Passed: ${GREEN}$total_pass${NC}"
    echo -e "  Failed: ${RED}$total_fail${NC}"

    if [ ${#failed_classes[@]} -gt 0 ]; then
        echo ""
        echo -e "  Failed classes:"
        for fc in "${failed_classes[@]}"; do
            echo -e "    ${RED}- $fc${NC}"
        done
    fi
    echo "========================================"

    # Cleanup temp dir on success
    if [ "$work_dir" != "$PROJECT_ROOT" ] && [ $total_fail -eq 0 ]; then
        info "Cleaning temp directory..."
        rm -rf "$work_dir"
    elif [ "$work_dir" != "$PROJECT_ROOT" ]; then
        info "Temp directory kept at: $work_dir"
    fi

    if [ $total_fail -gt 0 ]; then
        return 1
    fi
    return 0
}

main "$@"
