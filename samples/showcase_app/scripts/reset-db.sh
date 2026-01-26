#!/bin/bash
#
# Reset the database by deleting the H2 database file
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
DATA_DIR="$PROJECT_DIR/data"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

info() { echo -e "${GREEN}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }

if [ -d "$DATA_DIR" ] && ls "$DATA_DIR"/*.db 2>/dev/null | grep -q .; then
    rm -f "$DATA_DIR"/*.db "$DATA_DIR"/*.trace.db "$DATA_DIR"/*.lock.db
    info "Database deleted. Flyway will recreate it on next startup."
else
    warn "No database files found in $DATA_DIR"
fi
