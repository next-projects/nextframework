#!/bin/bash
#
# Downloads Ant and Ivy build tools
#
# Usage:
#   ./tools/download-tools.sh
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOOLS_BIN_DIR="$SCRIPT_DIR/bin"

ANT_VERSION="1.10.14"
IVY_VERSION="2.5.2"

ANT_DIR="$TOOLS_BIN_DIR/apache-ant-$ANT_VERSION"
IVY_JAR="$TOOLS_BIN_DIR/ivy-$IVY_VERSION.jar"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

info() { echo -e "${GREEN}[INFO]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }

download_ant() {
    if [ -d "$ANT_DIR" ] && [ -f "$ANT_DIR/bin/ant" ]; then
        info "Ant $ANT_VERSION already installed"
        return 0
    fi

    mkdir -p "$TOOLS_BIN_DIR"

    info "Downloading Apache Ant $ANT_VERSION..."
    local ant_url="https://dlcdn.apache.org/ant/binaries/apache-ant-${ANT_VERSION}-bin.tar.gz"
    local ant_tar="$TOOLS_BIN_DIR/apache-ant-${ANT_VERSION}-bin.tar.gz"

    curl -L -o "$ant_tar" "$ant_url" || { error "Failed to download Ant"; return 1; }

    info "Extracting Ant..."
    tar -xzf "$ant_tar" -C "$TOOLS_BIN_DIR" || { error "Failed to extract Ant"; return 1; }

    rm -f "$ant_tar"
    info "Ant installed successfully"
}

download_ivy() {
    if [ -f "$IVY_JAR" ]; then
        info "Ivy $IVY_VERSION already installed"
        return 0
    fi

    mkdir -p "$TOOLS_BIN_DIR"

    info "Downloading Apache Ivy $IVY_VERSION..."
    local ivy_url="https://repo1.maven.org/maven2/org/apache/ivy/ivy/${IVY_VERSION}/ivy-${IVY_VERSION}.jar"

    curl -L -o "$IVY_JAR" "$ivy_url" || { error "Failed to download Ivy"; return 1; }

    info "Ivy installed successfully"
}

setup_ivy_for_ant() {
    local ant_lib="$ANT_DIR/lib"
    local ivy_dest="$ant_lib/ivy-$IVY_VERSION.jar"

    if [ -f "$ivy_dest" ]; then
        info "Ivy already configured in Ant lib"
        return 0
    fi

    info "Configuring Ivy for Ant..."
    cp "$IVY_JAR" "$ivy_dest" || { error "Failed to copy Ivy to Ant lib"; return 1; }
    info "Ivy configured for Ant"
}

main() {
    echo ""
    echo "========================================"
    echo "  Downloading Build Tools"
    echo "========================================"
    echo ""

    download_ant || return 1
    download_ivy || return 1
    setup_ivy_for_ant || return 1

    echo ""
    info "Build tools ready"
    echo ""
}

main "$@"
