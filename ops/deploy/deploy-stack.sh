#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/common.sh"

require docker
require curl

# Usage: deploy-stack.sh <color> <host_port> <app_dir> <compose_file>
COLOR="${1:?color required (blue|green)}"
HOST_PORT="${2:?host_port required (8080|8081)}"
APP_DIR="${3:-/opt/app/backend}"
COMPOSE_FILE="${4:-${APP_DIR}/ops/compose/docker-compose.bg.yml}"

: "${IMAGE_REPO:?IMAGE_REPO required}"
: "${IMAGE_TAG:?IMAGE_TAG required}"

log "Deploy stack color=${COLOR} host_port=${HOST_PORT}"
log "IMAGE=${IMAGE_REPO}:${IMAGE_TAG}"
log "compose=${COMPOSE_FILE}"

cd "$APP_DIR"

export COLOR HOST_PORT IMAGE_REPO IMAGE_TAG

docker compose -p "daeng-backend-${COLOR}" -f "$COMPOSE_FILE" pull
docker compose -p "daeng-backend-${COLOR}" -f "$COMPOSE_FILE" up -d --remove-orphans

HEALTH_URL="http://127.0.0.1:${HOST_PORT}/api/v3/health"
ok=0
for i in {1..30}; do
  if curl -fsS --max-time 2 "$HEALTH_URL" >/dev/null; then ok=1; break; fi
  sleep 2
done

if [ "$ok" -ne 1 ]; then
  log "❌ 헬스체크 실패"
  docker compose -p "daeng-backend-${COLOR}" -f "$COMPOSE_FILE" logs --tail=200 || true
  docker compose -p "daeng-backend-${COLOR}" -f "$COMPOSE_FILE" down || true
  exit 1
fi

log "✅ 헬스체크 성공"