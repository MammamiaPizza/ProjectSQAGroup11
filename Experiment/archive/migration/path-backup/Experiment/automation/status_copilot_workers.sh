#!/usr/bin/env bash
set -u
ROOT="/mnt/c/Users/User/Desktop/SQAProjectGroup11-git"
cd "$ROOT"
LOG_DIR="Experiment/logs/copilot-final-opt"
printf "%-7s %-10s %-8s %s\n" WORKER STATE PID LAST
shopt -s nullglob
for pf in "$LOG_DIR"/C*.pid; do
  w=$(basename "$pf" .pid)
  pid=$(cat "$pf" 2>/dev/null || true)
  log="$LOG_DIR/$w.log"
  state="STOPPED"
  if [[ -n "$pid" ]] && kill -0 "$pid" 2>/dev/null; then
    state="RUNNING"
    if tail -n 20 "$log" 2>/dev/null | grep -Eqi 'QUOTA PAUSED|DAILY QUOTA REACHED|quota reached'; then
      state="QUOTA"
    fi
  fi
  last=$(tail -n 1 "$log" 2>/dev/null | tr '\n' ' ' | cut -c1-100)
  printf "%-7s %-10s %-8s %s\n" "$w" "$state" "${pid:--}" "$last"
done
