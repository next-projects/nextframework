#!/bin/bash
#
# Run the showcase app
#
# Usage: ./run.sh [--compile] [port]
#   --compile  Compile app before running
#   port       HTTP port (default: 8080)
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

info() { echo -e "${GREEN}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Warn if running in WSL on Windows filesystem
if grep -qi microsoft /proc/version 2>/dev/null && [[ "$PROJECT_DIR" == /mnt/* ]]; then
    warn "Running in WSL on Windows filesystem - startup may be slow"
fi

# Parse arguments
COMPILE=false
PORT="8080"

for arg in "$@"; do
    if [[ "$arg" == "--compile" || "$arg" == "-c" ]]; then
        COMPILE=true
    elif [[ "$arg" =~ ^[0-9]+$ ]]; then
        PORT="$arg"
    fi
done

# Compile if requested
if [ "$COMPILE" = true ]; then
    "$SCRIPT_DIR/compile.sh" || { error "Compilation failed"; exit 1; }
fi

# Check if compiled
if [ ! -d "WebContent/WEB-INF/classes" ]; then
    error "Application not compiled."
    echo ""
    echo "Run ./scripts/build.sh first."
    exit 1
fi

# Build classpath (only Tomcat + Main class)
CLASSPATH="WebContent/WEB-INF/classes"

# Add Tomcat embed jars
if [ -d "lib" ]; then
    for jar in lib/*.jar; do
        [ -f "$jar" ] && CLASSPATH="$CLASSPATH:$jar"
    done
fi

info "Starting ERP-lite on port $PORT..."
echo ""

java -cp "$CLASSPATH" org.erplite.Main "$PORT"
