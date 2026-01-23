#!/bin/bash
#
# Compile the showcase app source code
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
CONFIG_FILE="$PROJECT_DIR/build.config"

cd "$PROJECT_DIR"

GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

info() { echo -e "${GREEN}[INFO]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Find Next Framework root
if [ -f "$CONFIG_FILE" ]; then
    NEXT_ROOT=$(grep "^next.root=" "$CONFIG_FILE" | cut -d'=' -f2-)
fi

if [ -z "$NEXT_ROOT" ] || [ ! -d "$NEXT_ROOT/org.nextframework.build" ]; then
    for path in "$PROJECT_DIR/../.." "$PROJECT_DIR/.."; do
        if [ -d "$path/org.nextframework.build" ]; then
            NEXT_ROOT="$(cd "$path" && pwd)"
            break
        fi
    done
fi

if [ -z "$NEXT_ROOT" ]; then
    error "Next Framework root not found."
    exit 1
fi

# Find Ant
TOOLS_BIN="$NEXT_ROOT/org.nextframework.build/tools/bin"
ANT_HOME=$(find "$TOOLS_BIN" -maxdepth 1 -name "apache-ant-*" -type d 2>/dev/null | head -1)

if [ -z "$ANT_HOME" ]; then
    error "Ant not found. Run build.sh first."
    exit 1
fi

export PATH="$ANT_HOME/bin:$PATH"

info "Compiling app source code..."
ant -Dnext.root="$NEXT_ROOT" compile-app-only > /dev/null 2>&1 || { error "Failed to compile app"; exit 1; }
info "Compile complete!"
