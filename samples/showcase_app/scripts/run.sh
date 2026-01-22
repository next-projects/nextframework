#!/bin/bash
#
# Run the showcase app
#
# Usage: ./run.sh [port]
#   port - HTTP port (default: 8080)
#

PORT="${1:-8080}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
CONFIG_FILE="$PROJECT_DIR/build.config"

cd "$PROJECT_DIR"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

info() { echo -e "${GREEN}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Load NEXT_ROOT from config
if [ -f "$CONFIG_FILE" ]; then
    NEXT_ROOT=$(grep "^next.root=" "$CONFIG_FILE" | cut -d'=' -f2-)
fi

if [ -z "$NEXT_ROOT" ] || [ ! -d "$NEXT_ROOT/org.nextframework.build" ]; then
    error "Next Framework root not configured."
    echo ""
    echo "Run ./scripts/build.sh first to configure and build."
    exit 1
fi

# Check if compiled
if [ ! -d "build/classes" ]; then
    error "Application not compiled."
    echo ""
    echo "Run ./scripts/build.sh first."
    exit 1
fi

BUILD_DIR="$NEXT_ROOT/org.nextframework.build"
TOOLS_BIN="$BUILD_DIR/tools/bin"

# Find Ant (needed for classpath)
if ! command -v ant &> /dev/null; then
    ANT_HOME=$(find "$TOOLS_BIN" -maxdepth 1 -name "apache-ant-*" -type d 2>/dev/null | head -1)
    if [ -n "$ANT_HOME" ] && [ -f "$ANT_HOME/bin/ant" ]; then
        export ANT_HOME
        export PATH="$ANT_HOME/bin:$PATH"
    else
        error "Ant not found. Run ./scripts/build.sh first."
        exit 1
    fi
fi

# Build classpath
CLASSPATH="build/classes"

# Add lib jars
if [ -d "lib" ]; then
    for jar in lib/*.jar; do
        [ -f "$jar" ] && CLASSPATH="$CLASSPATH:$jar"
    done
fi

# Add provided jars
if [ -d "provided" ]; then
    for jar in provided/*.jar; do
        [ -f "$jar" ] && CLASSPATH="$CLASSPATH:$jar"
    done
fi

info "Starting ERP-lite on port $PORT..."
echo ""

java -cp "$CLASSPATH" org.erplite.Main "$PORT"
