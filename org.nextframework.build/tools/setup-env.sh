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
#   - Java 25 JDK
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

# Extract the major version from output such as:
#   openjdk version "25.0.2" ...
#   javac 25.0.2
get_java_major_version() {
    local version_output="$1"

    if [[ "$version_output" =~ \"([0-9]+) ]]; then
        echo "${BASH_REMATCH[1]}"
    elif [[ "$version_output" =~ ([0-9]+) ]]; then
        echo "${BASH_REMATCH[1]}"
    fi
}

# Check if the Java 25 JDK is installed
check_java() {
    if ! command -v java &> /dev/null; then
        error "Java is not installed. Please install Java 25 JDK."
        return 1
    fi

    if ! command -v javac &> /dev/null; then
        error "javac is not installed. Please install Java 25 JDK."
        return 1
    fi

    local java_version
    local javac_version
    local java_major
    local javac_major
    java_version=$(java -version 2>&1 | head -1)
    javac_version=$(javac -version 2>&1)
    java_major=$(get_java_major_version "$java_version")
    javac_major=$(get_java_major_version "$javac_version")

    if [ "$java_major" != "25" ] || [ "$javac_major" != "25" ]; then
        error "Java 25 JDK required. Found runtime: $java_version; compiler: $javac_version"
        return 1
    fi

    info "Java 25 JDK found: $java_version; $javac_version"
    return 0
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
