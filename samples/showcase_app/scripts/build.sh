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

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

info() { echo -e "${GREEN}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Find Next Framework root
find_next_root() {
    local search_paths=(
        "$PROJECT_DIR/../.."                    # Inside samples/showcase_app
        "$PROJECT_DIR/.."                       # Sibling of nextframework
        "$PROJECT_DIR/../nextframework"         # Sibling folder named nextframework
    )

    for path in "${search_paths[@]}"; do
        if [ -d "$path/org.nextframework.build" ]; then
            echo "$(cd "$path" && pwd)"
            return 0
        fi
    done
    return 1
}

# Load or detect NEXT_ROOT
if [ -f "$CONFIG_FILE" ]; then
    # Read properties format (next.root=value)
    NEXT_ROOT=$(grep "^next.root=" "$CONFIG_FILE" | cut -d'=' -f2-)
fi

if [ -z "$NEXT_ROOT" ] || [ ! -d "$NEXT_ROOT/org.nextframework.build" ]; then
    NEXT_ROOT=$(find_next_root)
    if [ -z "$NEXT_ROOT" ]; then
        error "Next Framework root not found."
        echo ""
        echo "Create build.config with:"
        echo "  next.root=/path/to/nextframework"
        exit 1
    fi
    # Save to config (properties format for Ant compatibility)
    echo "next.root=$NEXT_ROOT" > "$CONFIG_FILE"
    info "Detected Next Framework: $NEXT_ROOT"
fi

BUILD_DIR="$NEXT_ROOT/org.nextframework.build"
TOOLS_BIN="$BUILD_DIR/tools/bin"

# Run command with filtered output
run_filtered() {
    local label="$1"
    shift

    if $VERBOSE; then
        "$@"
        return $?
    fi

    info "$label"

    # Run and filter output
    "$@" 2>&1 | while IFS= read -r line; do
        # Show module being processed
        if [[ "$line" =~ "Downloading dependencies for:" ]]; then
            module=$(echo "$line" | sed 's/.*org\.nextframework\./  → org.nextframework./' | sed 's/.*showcase_app/  → showcase_app/')
            echo "$module"
        elif [[ "$line" =~ "BUILD SUCCESSFUL" ]]; then
            info "Done"
        elif [[ "$line" =~ "BUILD FAILED" ]]; then
            error "Build failed"
        elif [[ "$line" =~ ^[[:space:]]*\[javac\].*error: ]]; then
            echo "$line"
        fi
    done

    return ${PIPESTATUS[0]}
}

# Check if Ant is available
if ! command -v ant &> /dev/null; then
    # Check if already downloaded in tools/bin
    ANT_HOME=$(find "$TOOLS_BIN" -maxdepth 1 -name "apache-ant-*" -type d 2>/dev/null | head -1)

    if [ -n "$ANT_HOME" ] && [ -f "$ANT_HOME/bin/ant" ]; then
        # Use cached installation
        export ANT_HOME
        export PATH="$ANT_HOME/bin:$PATH"
    else
        # Need to download
        echo ""
        info "Setting up build tools..."
        echo ""
        echo "NOTE: This setup is LOCAL to the project only."
        echo "      No global system configurations will be changed."
        echo ""

        if $VERBOSE; then
            source "$BUILD_DIR/tools/setup-env.sh" --no-compile --no-js
        else
            info "Downloading Ant and Ivy..."
            source "$BUILD_DIR/tools/setup-env.sh" --no-compile --no-js > /dev/null 2>&1
        fi

        if ! command -v ant &> /dev/null; then
            error "Failed to set up Ant."
            exit 1
        fi
        info "Build tools ready"
        echo ""
    fi
fi

# Resolve dependencies
run_filtered "Resolving dependencies..." ant -Dnext.root="$NEXT_ROOT" resolve
if [ $? -ne 0 ]; then
    error "Failed to resolve dependencies"
    exit 1
fi

# Compile
run_filtered "Compiling..." ant -Dnext.root="$NEXT_ROOT" compile
if [ $? -ne 0 ]; then
    error "Compilation failed"
    exit 1
fi

echo ""
info "Build complete"
