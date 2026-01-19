#!/bin/bash
#
# Setup script for Next Framework build environment
#
# By default, runs ALL steps:
#   1. Download Ant + Ivy (if not present)
#   2. Set PATH for current shell
#   3. Download project dependencies
#   4. Compile all modules
#   5. Generate JavaScript files
#
# Usage:
#   source tools/setup-env.sh              # Full setup (recommended)
#   source tools/setup-env.sh --no-deps    # Skip dependency download
#   source tools/setup-env.sh --no-compile # Skip compilation
#   source tools/setup-env.sh --no-js      # Skip JavaScript generation
#
# Multiple flags can be combined:
#   source tools/setup-env.sh --no-compile --no-js
#
# Prerequisites:
#   - Java 8 JDK
#   - curl
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOOLS_BIN_DIR="$SCRIPT_DIR/bin"
BUILD_DIR="$(dirname "$SCRIPT_DIR")"

ANT_VERSION="1.10.14"
ANT_DIR="$TOOLS_BIN_DIR/apache-ant-$ANT_VERSION"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

info() { echo -e "${GREEN}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Check if Java 8 is installed
check_java() {
    if ! command -v java &> /dev/null; then
        error "Java is not installed. Please install Java 8."
        return 1
    fi

    local java_version
    java_version=$(java -version 2>&1 | head -1)

    if [[ "$java_version" =~ "1.8" ]] || [[ "$java_version" =~ '"8.' ]]; then
        info "Java 8 found: $java_version"
        return 0
    else
        error "Java 8 required. Found: $java_version"
        return 1
    fi
}

# Set environment variables
setup_environment() {
    export ANT_HOME="$ANT_DIR"
    export PATH="$ANT_DIR/bin:$PATH"
    info "PATH configured with Ant"
}

# Main
main() {
    local skip_deps=false
    local skip_compile=false
    local skip_js=false

    # Parse arguments
    for arg in "$@"; do
        case $arg in
            --no-deps)    skip_deps=true ;;
            --no-compile) skip_compile=true ;;
            --no-js)      skip_js=true ;;
        esac
    done

    echo ""
    echo "========================================"
    echo "  Next Framework Environment Setup"
    echo "========================================"
    echo ""

    # Step 1: Check Java
    check_java || return 1

    # Step 2: Download tools (Ant + Ivy)
    "$SCRIPT_DIR/download-tools.sh" || return 1

    # Step 3: Set PATH
    setup_environment

    # Step 4: Download dependencies
    if [ "$skip_deps" = false ]; then
        info "Downloading project dependencies..."
        "$SCRIPT_DIR/download-dependencies.sh" || return 1
    else
        warn "Skipping dependency download (--no-deps)"
    fi

    # Step 5: Compile
    if [ "$skip_compile" = false ]; then
        "$SCRIPT_DIR/compile-all.sh" || return 1
    else
        warn "Skipping compilation (--no-compile)"
    fi

    # Step 6: Generate JavaScript
    if [ "$skip_js" = false ] && [ "$skip_compile" = false ]; then
        "$SCRIPT_DIR/generate-js.sh" || return 1
    elif [ "$skip_js" = true ]; then
        warn "Skipping JavaScript generation (--no-js)"
    fi

    echo ""
    echo "========================================"
    info "Setup complete!"
    echo "========================================"
    echo ""
}

main "$@"
