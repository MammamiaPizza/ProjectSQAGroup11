#!/usr/bin/env bash
set -euo pipefail
ROOT="/mnt/c/Users/User/Desktop/SQAProjectGroup11-git"
cd "$ROOT"
W="${1:-}"
PF="Experiment/logs/copilot-final-opt/$W.pid"
if [[ -z "$W" || ! -f "$PF" ]]; then echo "Usage: $0 C1"; exit 2; fi
PID=$(cat "$PF" || true)
if [[ -n "$PID" ]] && kill -0 "$PID" 2>/dev/null; then
  kill "$PID"
  echo "Stopped $W PID=$PID"
else
  echo "$W is not running"
fi
