#!/usr/bin/env bash
set -euo pipefail

ROOT="/mnt/c/Users/User/Desktop/SQAProjectGroup11-git"
RUN_ID="final"
LOG_DIR="$ROOT/Experiment/logs/$RUN_ID"

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 W1"
  exit 2
fi

WORKER="$1"
PID_FILE="$LOG_DIR/$WORKER.pid"

if [[ ! -f "$PID_FILE" ]]; then
  echo "No PID file for $WORKER"
  exit 1
fi

PID="$(cat "$PID_FILE")"

if kill -0 "$PID" 2>/dev/null; then
  kill "$PID"
  echo "Stopped $WORKER (PID $PID)"
else
  echo "$WORKER is not running."
fi
