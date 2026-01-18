#!/bin/bash
#
# Setup script for Next Framework build environment
# Downloads and configures Ant + Ivy if not already installed
#
# Usage:
#   source tools/setup-env.sh    # Set up environment in current shell
#   ./tools/setup-env.sh         # Just download tools (won't set PATH in parent shell)
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOOLS_BIN_DIR="$SCRIPT_DIR/bin"
BUILD_DIR="$(dirname "$SCRIPT_DIR")"

ANT_VERSION="1.10.14"
IVY_VERSION="2.5.2"

ANT_DIR="$TOOLS_BIN_DIR/apache-ant-$ANT_VERSION"
IVY_JAR="$TOOLS_BIN_DIR/ivy-$IVY_VERSION.jar"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Java 8 is installed
check_java() {
    if ! command -v java &> /dev/null; then
        error "Java is not installed. Please install Java 8."
        return 1
    fi

    local java_version
    java_version=$(java -version 2>&1 | head -1)

    # Check for Java 8 (version string contains "1.8" or "8.0")
    if [[ "$java_version" =~ "1.8" ]] || [[ "$java_version" =~ '"8.' ]]; then
        info "Java 8 found: $java_version"
        return 0
    else
        error "Java 8 required. Found: $java_version"
        error "Please install Java 8 (1.8.x)"
        return 1
    fi
}

# Download Ant if not present
download_ant() {
    if [ -d "$ANT_DIR" ] && [ -f "$ANT_DIR/bin/ant" ]; then
        info "Ant $ANT_VERSION already installed at $ANT_DIR"
        return 0
    fi

    mkdir -p "$TOOLS_BIN_DIR"

    info "Downloading Apache Ant $ANT_VERSION..."
    local ant_url="https://dlcdn.apache.org/ant/binaries/apache-ant-${ANT_VERSION}-bin.tar.gz"
    local ant_tar="$TOOLS_BIN_DIR/apache-ant-${ANT_VERSION}-bin.tar.gz"

    curl -L -o "$ant_tar" "$ant_url" || {
        error "Failed to download Ant"
        return 1
    }

    info "Extracting Ant..."
    tar -xzf "$ant_tar" -C "$TOOLS_BIN_DIR" || {
        error "Failed to extract Ant"
        return 1
    }

    rm -f "$ant_tar"
    info "Ant installed successfully"
    return 0
}

# Download Ivy if not present
download_ivy() {
    if [ -f "$IVY_JAR" ]; then
        info "Ivy $IVY_VERSION already installed at $IVY_JAR"
        return 0
    fi

    mkdir -p "$TOOLS_BIN_DIR"

    info "Downloading Apache Ivy $IVY_VERSION..."
    local ivy_url="https://repo1.maven.org/maven2/org/apache/ivy/ivy/${IVY_VERSION}/ivy-${IVY_VERSION}.jar"

    curl -L -o "$IVY_JAR" "$ivy_url" || {
        error "Failed to download Ivy"
        return 1
    }

    info "Ivy installed successfully"
    return 0
}

# Setup Ivy in Ant's lib directory
setup_ivy_for_ant() {
    local ant_lib="$ANT_DIR/lib"
    local ivy_dest="$ant_lib/ivy-$IVY_VERSION.jar"

    if [ -f "$ivy_dest" ]; then
        info "Ivy already configured in Ant lib"
        return 0
    fi

    info "Configuring Ivy for Ant..."
    cp "$IVY_JAR" "$ivy_dest" || {
        error "Failed to copy Ivy to Ant lib"
        return 1
    }

    info "Ivy configured for Ant"
    return 0
}

# Set environment variables
setup_environment() {
    export ANT_HOME="$ANT_DIR"
    export PATH="$ANT_DIR/bin:$PATH"
    info "Environment configured:"
    info "  ANT_HOME=$ANT_HOME"
    info "  PATH includes $ANT_DIR/bin"
}

# Verify installation
verify_installation() {
    info "Verifying installation..."
    if "$ANT_DIR/bin/ant" -version &> /dev/null; then
        info "Ant verification: $("$ANT_DIR/bin/ant" -version)"
        return 0
    else
        error "Ant verification failed"
        return 1
    fi
}

# Download project dependencies
download_dependencies() {
    info "Downloading project dependencies..."
    cd "$BUILD_DIR" || return 1
    "$ANT_DIR/bin/ant" -f build-dependencies.xml "Download Next Ivy Dependencies" || {
        error "Failed to download dependencies"
        return 1
    }
    info "Dependencies downloaded successfully"
    return 0
}

# Main
main() {
    echo ""
    echo "========================================"
    echo "  Next Framework Environment Setup"
    echo "========================================"
    echo ""

    check_java || return 1
    download_ant || return 1
    download_ivy || return 1
    setup_ivy_for_ant || return 1
    setup_environment
    verify_installation || return 1

    echo ""
    info "Setup complete!"
    echo ""
    echo "To download project dependencies, run:"
    echo "  cd $BUILD_DIR"
    echo "  ant -f build-dependencies.xml 'Download Next Ivy Dependencies'"
    echo ""
    echo "Or run this script with --deps flag:"
    echo "  source $SCRIPT_DIR/setup-env.sh --deps"
    echo ""
}

# Compile all modules
compile_all() {
    info "Compiling all modules..."
    "$SCRIPT_DIR/compile-all.sh" || {
        error "Compilation failed"
        return 1
    }
    return 0
}

# Check for flags
case "$1" in
    --deps)
        main && download_dependencies
        ;;
    --compile)
        main && download_dependencies && compile_all
        ;;
    *)
        main
        ;;
esac
