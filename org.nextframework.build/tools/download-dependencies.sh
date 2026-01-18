#!/bin/bash
#
# Downloads all project dependencies using Ivy
#
# Prerequisites: Run setup-env.sh first
#
# Usage:
#   ./tools/download-dependencies.sh
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$(dirname "$SCRIPT_DIR")"
ANT_BIN="$SCRIPT_DIR/bin/apache-ant-1.10.14/bin/ant"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Ant is installed
if [ ! -f "$ANT_BIN" ]; then
    error "Ant not found. Run setup-env.sh first:"
    echo "  source $SCRIPT_DIR/setup-env.sh"
    exit 1
fi

info "Downloading project dependencies..."
cd "$BUILD_DIR" || exit 1

"$ANT_BIN" -f build-dependencies.xml "Download Next Ivy Dependencies"

if [ $? -eq 0 ]; then
    info "Dependencies downloaded successfully!"
else
    error "Failed to download dependencies"
    exit 1
fi
