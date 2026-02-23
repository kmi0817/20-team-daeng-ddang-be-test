#/usr/bin/env bash
set -euo pipefail

log() { echo "[$(date -Is)] $*" >&2; }
die() { echo "[$(date -Is)] ERROR: $*" >&2; exit 1; }

require() {
    command -v "$1" >/dev/null 2>&1 || die "Missing required command: $1"
}