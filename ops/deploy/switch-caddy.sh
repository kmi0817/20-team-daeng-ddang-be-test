#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/common.sh"

require caddy

# Usage: switch-caddy.sh <target_port> [/etc/caddy/conf.d/upstream.caddy] [/etc/caddy/Caddyfile]
TARGET_PORT="${1:?target_port required}"
UPSTREAM_CONF="${2:-/etc/caddy/conf.d/upstream.caddy}"
CADDYFILE="${3:-/etc/caddy/Caddyfile}"

mkdir -p "$(dirname "$UPSTREAM_CONF")"

BEFORE="$(cat "$UPSTREAM_CONF" 2>/dev/null || true)"
echo "reverse_proxy localhost:${TARGET_PORT}" | tee "$UPSTREAM_CONF" >/dev/null

if caddy reload --config "$CADDYFILE"; then
  log "✅ Caddy ${TARGET_PORT}로 트래픽 전환"
else
  log "🚨 Caddy reload 실패. upstream을 롤백합니다."
  printf "%s" "$BEFORE" > "$UPSTREAM_CONF" || true
  caddy reload --config "$CADDYFILE" || true
  exit 1
fi