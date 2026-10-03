#!/usr/bin/env bash
set -u
ROOT="/mnt/c/Users/User/Desktop/SQAProjectGroup11-git"
RUN_ID="${RUN_ID:-final-opt}"
LOG_DIR="$ROOT/Experiment/logs/$RUN_ID"
shopt -s nullglob
for pf in "$LOG_DIR"/W*.pid; do
  w="$(basename "$pf" .pid)"
  pid="$(cat "$pf" 2>/dev/null || true)"
  if [[ -n "$pid" ]] && kill -0 "$pid" 2>/dev/null; then
    kill "$pid" && echo "Stopped $w PID=$pid"
  fi
done
