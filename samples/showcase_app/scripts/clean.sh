#!/bin/bash
#
# Clean the showcase app build outputs
#
# Usage: ./scripts/clean.sh [--all]
#   --all  Also clean Tomcat work dir
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

# Colors
GREEN='\033[0;32m'
NC='\033[0m'

info() { echo -e "${GREEN}[INFO]${NC} $1"; }

echo ""
echo "========================================"
echo "  Cleaning Showcase App"
echo "========================================"
echo ""

# Clean compiled classes
if [ -d "WebContent/WEB-INF/classes" ]; then
    rm -rf WebContent/WEB-INF/classes
    info "Cleaned WEB-INF/classes"
fi

# Clean all JARs (framework + app dependencies)
if ls WebContent/WEB-INF/lib/*.jar 1>/dev/null 2>&1; then
    rm -f WebContent/WEB-INF/lib/*.jar
    info "Cleaned WEB-INF/lib"
fi

# If --all flag, also clean Tomcat work dir
if [[ "$1" == "--all" ]]; then
    if [ -d "build/tomcat" ]; then
        rm -rf build/tomcat
        info "Cleaned build/tomcat"
    fi
    if [ -d "build" ]; then
        rmdir build 2>/dev/null && info "Cleaned build/"
    fi
fi

echo ""
echo "========================================"
info "Clean complete!"
echo "========================================"
echo ""
