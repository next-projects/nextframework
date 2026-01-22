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

cd "$PROJECT_DIR"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

info() { echo -e "${GREEN}[INFO]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }

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
