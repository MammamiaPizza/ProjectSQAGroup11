#!/usr/bin/env bash
set -u

ROOT="/root/SQAProjectGroup11-git"
RUN_ID="${RUN_ID:-final-opt}"
LOG_DIR="$ROOT/Experiment/logs/$RUN_ID"

cd "$ROOT"

echo "=== Workers ==="
if [[ ! -d "$LOG_DIR" ]]; then
  echo "No workers started yet."
  exit 0
fi

shopt -s nullglob
PID_FILES=("$LOG_DIR"/W*.pid)

if [[ ${#PID_FILES[@]} -eq 0 ]]; then
  echo "No worker PID files."
else
  for pf in "${PID_FILES[@]}"; do
    worker="$(basename "$pf" .pid)"
    pid="$(cat "$pf" 2>/dev/null || true)"
    if [[ -n "$pid" ]] && kill -0 "$pid" 2>/dev/null; then
      state="RUNNING"
    else
      state="STOPPED"
    fi
    printf "%-6s PID %-8s %s\n" "$worker" "${pid:-?}" "$state"
  done
fi

echo
echo "=== Recent log lines ==="
for log in "$LOG_DIR"/W*.log; do
  [[ -f "$log" ]] || continue
  echo "--- $(basename "$log") ---"
  tail -n 3 "$log"
done

echo
echo "=== Defects4J slot owner ==="
SLOT_ROOT="$ROOT/Experiment/runtime/run-$RUN_ID/d4j-slots"
if [[ -d "$SLOT_ROOT" ]]; then
  find "$SLOT_ROOT" -maxdepth 2 -name owner.json -print -exec cat {} \; 2>/dev/null
else
  echo "No D4J slot currently held."
fi
