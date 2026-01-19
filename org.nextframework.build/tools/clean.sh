#!/bin/bash
#
# Cleans all compiled classes from project modules
#
# Usage:
#   ./tools/clean.sh
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$(dirname "$SCRIPT_DIR")"
PROJECT_ROOT="$(dirname "$BUILD_DIR")"

# Colors
GREEN='\033[0;32m'
NC='\033[0m'

info() { echo -e "${GREEN}[INFO]${NC} $1"; }

main() {
    echo ""
    echo "========================================"
    echo "  Cleaning Next Framework Modules"
    echo "========================================"
    echo ""

    local count=0
    for bin_dir in "$PROJECT_ROOT"/org.nextframework.*/bin; do
        if [ -d "$bin_dir" ]; then
            local module=$(basename "$(dirname "$bin_dir")")
            rm -rf "$bin_dir"
            info "Cleaned $module"
            ((count++))
        fi
    done

    echo ""
    echo "========================================"
    info "Cleaned $count modules"
    echo "========================================"
}

main "$@"
