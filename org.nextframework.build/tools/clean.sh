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
    for module_dir in "$PROJECT_ROOT"/org.nextframework.*; do
        local cleaned=false
        for output_dir in bin bin-test; do
            if [ -d "$module_dir/$output_dir" ]; then
                rm -rf -- "$module_dir/$output_dir"
                cleaned=true
            fi
        done
        if [ "$cleaned" = true ]; then
            info "Cleaned $(basename "$module_dir")"
            ((count++))
        fi
    done

    echo ""
    echo "========================================"
    info "Cleaned $count modules"
    echo "========================================"
}

main "$@"
