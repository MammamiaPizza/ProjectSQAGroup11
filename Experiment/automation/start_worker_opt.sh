#!/usr/bin/env bash
set -euo pipefail

ROOT="/root/SQAProjectGroup11-git"
RUN_ID="${RUN_ID:-final-opt}"
D4J_SLOTS="${D4J_SLOTS:-3}"
MODEL="${MODEL:-gpt-5.6-terra}"

PROJECTS="JacksonXml,Csv,Codec,Gson,JxPath,Chart,JacksonCore,Time,Collections,Mockito,Cli,Compress,Lang,Jsoup,Math,JacksonDatabind,Closure"

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 W1"
  exit 2
fi

WORKER="$1"

if [[ ! "$WORKER" =~ ^W[0-9]+$ ]]; then
  echo "Worker name must look like W1, W2, W8, W12, ..."
  exit 2
fi

cd "$ROOT"

# Make Defects4J available even after WSL restart / in nohup workers.
D4J_BIN=""
if command -v defects4j >/dev/null 2>&1; then
  D4J_BIN="$(dirname "$(command -v defects4j)")"
elif [[ -x "$HOME/defects4j/framework/bin/defects4j" ]]; then
  D4J_BIN="$HOME/defects4j/framework/bin"
elif [[ -x "/root/defects4j/framework/bin/defects4j" ]]; then
  D4J_BIN="/root/defects4j/framework/bin"
else
  echo "ERROR: defects4j executable not found."
  echo "Expected one of:"
  echo "  $HOME/defects4j/framework/bin/defects4j"
  echo "  /root/defects4j/framework/bin/defects4j"
  exit 1
fi

export PATH="$D4J_BIN:$PATH"

if ! defects4j pids >/dev/null 2>&1; then
  echo "ERROR: defects4j exists but cannot run correctly."
  defects4j pids || true
  exit 1
fi

RUNNER="Experiment/automation/ai_runner.py"
LOG_DIR="Experiment/logs/$RUN_ID"
LOG_FILE="$LOG_DIR/$WORKER.log"
PID_FILE="$LOG_DIR/$WORKER.pid"

if [[ ! -f "$RUNNER" ]]; then
  echo "Missing runner: $RUNNER"
  exit 1
fi

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
  echo "API key is empty."
  exit 1
fi

nohup env \
  PATH="$PATH" \
  KKU_INTELSPHERE_API_KEY="$API_KEY" \
  python3 "$RUNNER" \
    --provider chatgpt \
    --model "$MODEL" \
    --run-id "$RUN_ID" \
    --worker "$WORKER" \
    --d4j-slots "$D4J_SLOTS" \
    --queue-projects "$PROJECTS" \
    --skip-terminal-from-run final \
    --resume \
    --wait-on-quota \
  > "$LOG_FILE" 2>&1 &

PID=$!
echo "$PID" > "$PID_FILE"

unset API_KEY

echo "Started $WORKER"
echo "PID: $PID"
echo "Log: $LOG_FILE"
echo "Defects4J: $(command -v defects4j)"
echo "Defects4J projects: $(defects4j pids | tr '\n' ' ' | sed 's/[[:space:]]*$//')"
echo "D4J slots: $D4J_SLOTS"
echo
echo "Watch:"
echo "  tail -f $LOG_FILE"
