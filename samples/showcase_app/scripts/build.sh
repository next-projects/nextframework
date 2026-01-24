#!/bin/bash
#
# Build the showcase app
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
CONFIG_FILE="$PROJECT_DIR/build.config"

VERBOSE=false
if [[ "$1" == "--verbose" || "$1" == "-v" ]]; then
    VERBOSE=true
fi

cd "$PROJECT_DIR"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

info() { echo -e "${GREEN}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Warn if running in WSL on Windows filesystem
if grep -qi microsoft /proc/version 2>/dev/null && [[ "$PROJECT_DIR" == /mnt/* ]]; then
    warn "Running in WSL on Windows filesystem - build may be slow"
fi

# Find Next Framework root
if [ -f "$CONFIG_FILE" ]; then
    NEXT_ROOT=$(grep "^next.root=" "$CONFIG_FILE" | cut -d'=' -f2-)
fi

if [ -z "$NEXT_ROOT" ] || [ ! -d "$NEXT_ROOT/org.nextframework.build" ]; then
    for path in "$PROJECT_DIR/../.." "$PROJECT_DIR/.."; do
        if [ -d "$path/org.nextframework.build" ]; then
            NEXT_ROOT="$(cd "$path" && pwd)"
            echo "next.root=$NEXT_ROOT" > "$CONFIG_FILE"
            break
        fi
    done
fi

if [ -z "$NEXT_ROOT" ]; then
    error "Next Framework root not found."
    exit 1
fi

BUILD_DIR="$NEXT_ROOT/org.nextframework.build"
TOOLS_DIR="$BUILD_DIR/tools"
TOOLS_BIN="$TOOLS_DIR/bin"

echo ""
echo "========================================"
echo "  Building Showcase App"
echo "========================================"
echo ""
info "This may take a few minutes on first run..."
echo ""

# Step 1: Download tools
info "Step 1: Downloading build tools..."
"$TOOLS_DIR/download-tools.sh" || { error "Failed to download tools"; exit 1; }

ANT_HOME=$(find "$TOOLS_BIN" -maxdepth 1 -name "apache-ant-*" -type d 2>/dev/null | head -1)
export PATH="$ANT_HOME/bin:$PATH"

# Step 2: Download dependencies
info "Step 2: Downloading dependencies..."
if $VERBOSE; then
    "$TOOLS_DIR/download-dependencies.sh" || { error "Failed to download framework dependencies"; exit 1; }
    ant -Dnext.root="$NEXT_ROOT" resolve || { error "Failed to resolve app dependencies"; exit 1; }
else
    "$TOOLS_DIR/download-dependencies.sh" > /dev/null 2>&1 || { error "Failed to download framework dependencies"; exit 1; }
    ant -Dnext.root="$NEXT_ROOT" resolve > /dev/null 2>&1 || { error "Failed to resolve app dependencies"; exit 1; }
fi
info "App dependencies resolved"

# Step 3: Compile modules
info "Step 3: Compiling Next Framework modules..."
"$TOOLS_DIR/compile-all.sh" || { error "Failed to compile Next Framework"; exit 1; }

# Step 4: Deploy
info "Step 4: Deploying project..."
if $VERBOSE; then
    ant -Dnext.root="$NEXT_ROOT" "Deploy Project on Server" || { error "Failed to deploy"; exit 1; }
else
    ant -Dnext.root="$NEXT_ROOT" "Deploy Project on Server" > /dev/null 2>&1 || { error "Failed to deploy"; exit 1; }
fi

# Step 5: Compile app
info "Step 5: Compiling app source code..."
if $VERBOSE; then
    "$SCRIPT_DIR/compile.sh" || { error "Failed to compile app"; exit 1; }
else
    "$SCRIPT_DIR/compile.sh" > /dev/null 2>&1 || { error "Failed to compile app"; exit 1; }
fi

echo ""
echo "========================================"
info "Build complete!"
echo "========================================"
info "Run the app with: ./scripts/run.sh [port]"
