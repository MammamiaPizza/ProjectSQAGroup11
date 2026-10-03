#!/usr/bin/env bash
set -euo pipefail
ROOT="/root/SQAProjectGroup11-git"
cd "$ROOT"
WORKER="${1:-}"
if [[ ! "$WORKER" =~ ^C[0-9]+$ ]]; then
  echo "Usage: D4J_SLOTS=3 $0 C1"; exit 2
fi
RUN_ID="copilot-final-v2"
MODEL="deepseek-v4-pro"
D4J_SLOTS="${D4J_SLOTS:-3}"
LOG_DIR="Experiment/logs/$RUN_ID"
mkdir -p "$LOG_DIR" /tmp/sqa-copilot-clean/"$WORKER"

if ! command -v copilot >/dev/null 2>&1; then echo "ERROR: copilot CLI not found"; exit 1; fi
if ! command -v defects4j >/dev/null 2>&1; then
  for d in "$HOME/defects4j/framework/bin" /root/defects4j/framework/bin; do
    if [[ -x "$d/defects4j" ]]; then export PATH="$d:$PATH"; break; fi
  done
fi
if ! command -v defects4j >/dev/null 2>&1; then echo "ERROR: defects4j not found"; exit 1; fi

read -s -p "KKU API Key for Copilot/DeepSeek ($WORKER): " KKU_KEY
echo
[[ -n "$KKU_KEY" ]] || { echo "ERROR: empty API key"; exit 1; }
PID_FILE="$LOG_DIR/$WORKER.pid"
LOG_FILE="$LOG_DIR/$WORKER.log"
if [[ -f "$PID_FILE" ]]; then
  OLD_PID=$(cat "$PID_FILE" 2>/dev/null || true)
  if [[ -n "$OLD_PID" ]] && kill -0 "$OLD_PID" 2>/dev/null; then
    echo "ERROR: $WORKER already running as PID $OLD_PID"; unset KKU_KEY; exit 1
  fi
fi
PROJECTS="JacksonXml,Csv,Codec,Gson,JxPath,Chart,JacksonCore,Time,Collections,Mockito,Cli,Compress,Lang,Jsoup,Math,JacksonDatabind,Closure"
(
  export COPILOT_PROVIDER_TYPE="openai"
  export COPILOT_PROVIDER_BASE_URL="https://gen.ai.kku.ac.th/api/v1"
  export COPILOT_PROVIDER_API_KEY="$KKU_KEY"
  export COPILOT_MODEL="$MODEL"
  export COPILOT_AUTO_UPDATE="false"
  nohup python3 Experiment/automation/ai2_runner.py \
    --provider copilot --model "$MODEL" --run-id "$RUN_ID" --worker "$WORKER" \
    --d4j-slots "$D4J_SLOTS" --queue-projects "$PROJECTS" --resume --wait-on-quota \
    >"$LOG_FILE" 2>&1 &
  echo $! > "$PID_FILE"
)
unset KKU_KEY
PID=$(cat "$PID_FILE")
echo "Started $WORKER PID=$PID model=$MODEL run=$RUN_ID D4J_SLOTS=$D4J_SLOTS"
echo "Log: $ROOT/$LOG_FILE"
