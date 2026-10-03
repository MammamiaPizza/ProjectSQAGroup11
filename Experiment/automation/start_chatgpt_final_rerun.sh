#!/usr/bin/env bash
set -euo pipefail

ROOT="/root/SQAProjectGroup11-git"
RUN_ID="final-opt"
MODEL="gpt-5.6-terra"
D4J_SLOTS="${D4J_SLOTS:-3}"

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 W8"
  exit 2
fi

WORKER="$1"

if [[ ! "$WORKER" =~ ^W[0-9]+$ ]]; then
  echo "Worker name must look like W8, W9, W10, ..."
  exit 2
fi

cd "$ROOT"

export PATH="$HOME/defects4j/framework/bin:$PATH"

QUEUE_FILE="Experiment/final-rerun/w_rerun_queue.txt"

if [[ ! -f "$QUEUE_FILE" ]]; then
  echo "Missing queue: $QUEUE_FILE"
  exit 1
fi

COUNT="$(grep -cve '^[[:space:]]*$' "$QUEUE_FILE")"

if [[ "$COUNT" -ne 68 ]]; then
  echo "Expected 68 cases, found $COUNT"
  exit 1
fi

CASES="$(grep -v '^[[:space:]]*$' "$QUEUE_FILE" | paste -sd, -)"

LOG_DIR="Experiment/logs/final-opt-rerun68"
LOG_FILE="$LOG_DIR/$WORKER.log"
PID_FILE="$LOG_DIR/$WORKER.pid"

mkdir -p "$LOG_DIR"

if [[ -f "$PID_FILE" ]]; then
  OLD_PID="$(cat "$PID_FILE" 2>/dev/null || true)"
  if [[ -n "${OLD_PID:-}" ]] && kill -0 "$OLD_PID" 2>/dev/null; then
    echo "$WORKER is already running as PID $OLD_PID"
    exit 1
  fi
fi

read -r -s -p "API KEY $WORKER: " API_KEY
echo

if [[ -z "$API_KEY" ]]; then
  echo "API key is empty"
  exit 1
fi

nohup env \
  PATH="$PATH" \
  KKU_INTELSPHERE_API_KEY="$API_KEY" \
  python3 Experiment/automation/ai_runner.py \
    --provider chatgpt \
    --model "$MODEL" \
    --run-id "$RUN_ID" \
    --worker "$WORKER" \
    --d4j-slots "$D4J_SLOTS" \
    --queue-cases "$CASES" \
    --resume \
    --wait-on-quota \
  > "$LOG_FILE" 2>&1 &

PID=$!
echo "$PID" > "$PID_FILE"

unset API_KEY

echo "Started $WORKER"
echo "PID: $PID"
echo "Cases: $COUNT"
echo "Run: $RUN_ID"
echo "Log: $LOG_FILE"
