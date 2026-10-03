#!/usr/bin/env bash

ROOT="/root/SQAProjectGroup11-git"
LOG_DIR="$ROOT/Experiment/logs/final"
SLOT_DIR="$ROOT/Experiment/runtime/run-final/d4j-slots"

echo "======================================================================"
echo " SQA FINAL LIVE MONITOR     $(date '+%Y-%m-%d %H:%M:%S')"
echo "======================================================================"

echo
echo "=== MEMORY ==="
free -h

echo
echo "=== WORKERS ==="
printf "%-6s %-9s %-8s %-12s %-12s %-8s\n" \
       "WORKER" "STATE" "PID" "RAM(MB)" "TOKEN LEFT" "D4J"
printf "%-6s %-9s %-8s %-12s %-12s %-8s\n" \
       "------" "---------" "--------" "------------" "------------" "--------"

shopt -s nullglob

for pf in "$LOG_DIR"/W*.pid; do
    worker="$(basename "$pf" .pid)"
    pid="$(cat "$pf" 2>/dev/null)"
    log="$LOG_DIR/$worker.log"

    state="STOPPED"
    ram="-"
    quota="-"
    d4j="-"

    if [[ -n "$pid" ]] && kill -0 "$pid" 2>/dev/null; then
        state="RUNNING"

        rss="$(ps -o rss= -p "$pid" 2>/dev/null | tr -d ' ')"
        if [[ -n "$rss" ]]; then
            ram="$(awk -v r="$rss" 'BEGIN {printf "%.1f", r/1024}')"
        fi

        if [[ -f "$log" ]]; then
            quota="$(grep -o '"daily_remaining_tokens": [0-9]*' "$log" \
                | tail -1 | grep -o '[0-9]*$')"
            [[ -z "$quota" ]] && quota="-"

            recent="$(tail -n 15 "$log")"

            if echo "$recent" | grep -qE 'QUOTA PAUSED|DAILY QUOTA REACHED'; then
                state="QUOTA"
            fi
        fi
    fi

    for owner in "$SLOT_DIR"/slot-*/owner.json; do
        [[ -f "$owner" ]] || continue
        if grep -q "\"worker\": \"$worker\"" "$owner"; then
            d4j="$(basename "$(dirname "$owner")")"
        fi
    done

    printf "%-6s %-9s %-8s %-12s %-12s %-8s\n" \
        "$worker" "$state" "${pid:--}" "$ram" "$quota" "$d4j"
done

echo
echo "=== ACTIVE DEFECTS4J SLOTS ==="
found=0
for owner in "$SLOT_DIR"/slot-*/owner.json; do
    [[ -f "$owner" ]] || continue
    found=1
    slot="$(basename "$(dirname "$owner")")"
    worker="$(grep '"worker"' "$owner" | cut -d'"' -f4)"
    pid="$(grep '"pid"' "$owner" | grep -o '[0-9]*')"
    echo "$slot  ->  $worker (PID $pid)"
done
[[ "$found" -eq 0 ]] && echo "No heavy Defects4J job right now."

echo
echo "=== TOP RAM PROCESSES ==="
ps -eo pid,comm,rss,%cpu --sort=-rss \
    | awk 'NR==1 {printf "%-8s %-18s %-10s %s\n",$1,$2,"RAM(MB)",$4; next}
           NR<=11 {printf "%-8s %-18s %-10.1f %s\n",$1,$2,$3/1024,$4}'

echo
echo "=== JAVA / PYTHON TOTAL RAM ==="
ps -eo comm=,rss= | awk '
    $1=="java"    {java += $2}
    $1=="python3" {python += $2}
    END {
        printf "Java / Defects4J : %.1f MB\n", java/1024
        printf "Python workers    : %.1f MB\n", python/1024
    }'

echo
echo "=== LATEST ACTIVITY ==="
for log in "$LOG_DIR"/W*.log; do
    [[ -f "$log" ]] || continue
    worker="$(basename "$log" .log)"
    last="$(tail -n 1 "$log" | cut -c1-100)"
    printf "%-5s %s\n" "$worker" "$last"
done
