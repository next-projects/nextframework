#!/bin/bash
#
# Generates JavaScript files from Java sources using STJS
#
# This runs the stjs build.xml which converts Java files in js-builder
# directories to JavaScript, outputting to org.nextframework.view/resources
#
# Prerequisites:
#   - Run setup-env.sh first (for Ant)
#   - Run compile-all.sh first (stjs needs compiled classes)
#
# Usage:
#   ./tools/generate-js.sh
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$(dirname "$SCRIPT_DIR")"
PROJECT_ROOT="$(dirname "$BUILD_DIR")"
TOOLS_BIN="$SCRIPT_DIR/bin"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

info() { echo -e "${GREEN}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }

main() {
    echo ""
    echo "========================================"
    echo "  Generating JavaScript Files (STJS)"
    echo "========================================"
    echo ""

    # Check if Ant is available
    local ant_cmd=""
    if [ -d "$TOOLS_BIN/apache-ant"* ] 2>/dev/null; then
        ant_cmd=$(echo "$TOOLS_BIN"/apache-ant-*/bin/ant)
    elif command -v ant &> /dev/null; then
        ant_cmd="ant"
    else
        error "Ant not found. Run setup-env.sh first."
        return 1
    fi

    # Check if stjs is compiled
    if [ ! -d "$PROJECT_ROOT/org.nextframework.stjs/bin" ]; then
        error "stjs module not compiled. Run compile-all.sh first."
        return 1
    fi

    info "Using: $($ant_cmd -version 2>&1 | head -1)"
    echo ""

    # Run the stjs build
    info "Running stjs build.xml..."
    cd "$PROJECT_ROOT/org.nextframework.stjs"

    if $ant_cmd -f build.xml; then
        echo ""
        info "JavaScript generation complete"
        info "Output: org.nextframework.view/resources"
        return 0
    else
        echo ""
        error "JavaScript generation failed"
        return 1
    fi
}

main "$@"
