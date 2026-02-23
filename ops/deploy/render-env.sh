#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/common.sh"

require aws
require awk

# Usage: render-env.sh prod ap-northeast-2 /daeng-map/prod/be /opt/app/backend .env
ENV_NAME="${1:?env required (dev|prod)}"
REGION="${2:-ap-northeast-2}"
SSM_PREFIX="${3:-/daeng-map/${ENV_NAME}/be}"
OUT_DIR="${4:-/opt/app/backend}"
OUT_FILE="${5:-.env}"

mkdir -p "$OUT_DIR"
cd "$OUT_DIR"

tmp="${OUT_FILE}.tmp"
log "Rendering ${OUT_DIR}/${OUT_FILE} from SSM: ${SSM_PREFIX} (region=${REGION})"

aws ssm get-parameters-by-path \
  --path "$SSM_PREFIX" \
  --with-decryption \
  --recursive \
  --region "$REGION" \
  --query "Parameters[*].[Name,Value]" \
  --output text \
| awk -F '\t' -v p="${SSM_PREFIX}/" '{
    key=$1;
    sub(p,"",key);      # prefix 제거
    gsub(/\//,"_",key); # 혹시 남아있을지 모를 슬래시 방지
    gsub(/\r/,"",$2);
    print key"="$2
  }' > "$tmp"

chmod 644 "$tmp"
mv "$tmp" "$OUT_FILE"
log "✅ {OUT_DIR}/${OUT_FILE} 생성 (644)"

log "========= File Check ========="
ls -al /opt/app/backend/.env