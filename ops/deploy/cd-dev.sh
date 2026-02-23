#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/common.sh"

require curl
require docker
require awk
require aws

# ENV expected: IMAGE_REPO, IMAGE_TAG
APP_DIR="${APP_DIR:-/opt/app/backend}"
COMPOSE_FILE="${COMPOSE_FILE:-${APP_DIR}/ops/compose/docker-compose.bg.yml}"
REGION="${REGION:-ap-northeast-2}"
SSM_PREFIX="${SSM_PREFIX:-/daeng-map/dev/be}"
SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-dev}"

: "${IMAGE_REPO:?}"
: "${IMAGE_TAG:?}"

log "=== CD DEV start ==="
log "APP_DIR=${APP_DIR}"
log "SSM_PREFIX=${SSM_PREFIX}"
log "IMAGE=${IMAGE_REPO}:${IMAGE_TAG}"

# 1) env 렌더
bash "$(dirname "$0")/render-env.sh" dev "$REGION" "$SSM_PREFIX" "$APP_DIR" ".env"

# 2) current/target 결정
IS_8080_ALIVE=$(curl -s -o /dev/null -w "%{http_code}" --max-time 2 http://127.0.0.1:8080/api/v3/health || echo "fail")
if [ "$IS_8080_ALIVE" = "200" ]; then
  OLD_COLOR=blue; OLD_PORT=8080
  NEW_COLOR=green; NEW_PORT=8081
else
  OLD_COLOR=green; OLD_PORT=8081
  NEW_COLOR=blue; NEW_PORT=8080
fi
log "ℹ️ Current: ${OLD_COLOR}(${OLD_PORT}) -> Target: ${NEW_COLOR}(${NEW_PORT})"

export IMAGE_REPO IMAGE_TAG SPRING_PROFILES_ACTIVE

# 3) target 배포
bash "$(dirname "$0")/deploy-stack.sh" "$NEW_COLOR" "$NEW_PORT" "$APP_DIR" "$COMPOSE_FILE"

# 4) Caddy 스위치
bash "$(dirname "$0")/switch-caddy.sh" "$NEW_PORT"

# 5) old 종료
docker compose -p "daeng-backend-${OLD_COLOR}" -f "$COMPOSE_FILE" down || true
docker image prune -f || true

log "=== CD DEV 종료 ==="