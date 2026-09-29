#!/bin/bash
# Fails when two NextFramework modules own the same compiled class path.

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$(dirname "$SCRIPT_DIR")"
PROJECT_ROOT="${1:-$(dirname "$BUILD_DIR")}"

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

info() { echo -e "${GREEN}[INFO]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1" >&2; }

list_class_paths() {
    local module_dir
    for module_dir in "$PROJECT_ROOT"/org.nextframework.*; do
        if [ -d "$module_dir/bin" ]; then
            find "$module_dir/bin" -type f -name "*.class" -print |
                sed 's|.*/bin/||'
        fi
    done
}

main() {
    local duplicates
    duplicates=$(list_class_paths | LC_ALL=C sort | uniq -d)

    if [ -z "$duplicates" ]; then
        info "No duplicate classes across module outputs"
        return 0
    fi

    error "Duplicate classes found across module outputs:"
    local duplicate_count
    duplicate_count=$(printf '%s\n' "$duplicates" | wc -l)
    printf '%s\n' "$duplicates" | head -50 | while IFS= read -r class_path; do
        error "  $class_path"
    done
    if [ "$duplicate_count" -gt 50 ]; then
        error "  ... and $((duplicate_count - 50)) more"
    fi
    return 1
}

main "$@"
