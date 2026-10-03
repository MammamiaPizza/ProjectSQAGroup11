#!/usr/bin/env bash
set -u

ROOT="/mnt/c/Users/User/Desktop/SQAProjectGroup11-git"
LOGDIR="$ROOT/Experiment/logs/copilot-final-v2"
RESULT="$ROOT/AI2_GitHubCopilot/Result"
RUN_ID="run-copilot-final-v2"
QUOTA=1000000

cd "$ROOT" || exit 1

clear
echo "======================================================================"
echo "          SQA COPILOT + KKU DEEPSEEK LIVE MONITOR"
echo "======================================================================"
echo "Time      : $(date '+%Y-%m-%d %H:%M:%S')"
echo "Provider  : GitHub Copilot CLI BYOK"
echo "Model     : deepseek-v4-pro"
echo "Quota     : 1,000,000 tokens/account/day"
echo "Run       : copilot-final-v2"
echo

echo "=== MEMORY / SYSTEM ===================================================="
free -h | sed -n '1,3p'
echo
printf "CPU cores : "
nproc
echo

echo "=== COPILOT WORKERS ===================================================="
printf "%-5s %-10s %-8s %-9s %-11s %-11s %-22s %-6s\n" \
  "WORK" "STATE" "PID" "RAM" "RUN TOKENS" "EST LEFT" "CURRENT/LAST CASE" "STAGE"
printf "%-5s %-10s %-8s %-9s %-11s %-11s %-22s %-6s\n" \
  "----" "-----" "---" "---" "----------" "--------" "-----------------" "-----"

worker_count=0
running_count=0
quota_count=0
total_run_tokens=0

for pf in "$LOGDIR"/C*.pid; do
    [[ -e "$pf" ]] || continue

    w=$(basename "$pf" .pid)
    pid=$(cat "$pf" 2>/dev/null || true)
    log="$LOGDIR/$w.log"

    worker_count=$((worker_count + 1))

    if [[ -n "$pid" ]] && kill -0 "$pid" 2>/dev/null; then
        state="RUNNING"
        running_count=$((running_count + 1))
        ram=$(ps -p "$pid" -o rss= 2>/dev/null | awk '
            { if ($1 >= 1048576) printf "%.1fG",$1/1048576;
              else if ($1 > 0) printf "%.0fM",$1/1024;
              else print "-" }')
        [[ -n "$ram" ]] || ram="-"
    else
        state="STOPPED"
        ram="-"
    fi

    # Detect quota message if the runner/provider reports one.
    if [[ -f "$log" ]] && tail -60 "$log" 2>/dev/null | \
        grep -Eqi 'quota|daily.*limit|rate.*limit|429|insufficient.*token'; then
        if [[ "$state" == "RUNNING" ]]; then
            state="QUOTA"
            quota_count=$((quota_count + 1))
        fi
    fi

    # Sum actual token totals printed by this worker.
    wtokens=0
    if [[ -f "$log" ]]; then
        wtokens=$(python3 - "$log" <<'PY'
import json, sys
from pathlib import Path

p = Path(sys.argv[1])
total = 0
try:
    for line in p.read_text(errors="ignore").splitlines():
        line=line.strip()
        if not (line.startswith("{") and line.endswith("}")):
            continue
        try:
            d=json.loads(line)
        except Exception:
            continue
        v=d.get("total_tokens")
        if isinstance(v,int):
            total += v
except Exception:
    pass
print(total)
PY
)
    fi

    total_run_tokens=$((total_run_tokens + wtokens))

    # This is run-relative remaining, not exact KKU account balance.
    est_left=$((QUOTA - wtokens))
    (( est_left < 0 )) && est_left=0

    if (( wtokens >= 1000000 )); then
        tokdisp=$(printf "%.2fM" "$(awk "BEGIN{print $wtokens/1000000}")")
    elif (( wtokens >= 1000 )); then
        tokdisp=$(awk "BEGIN{printf \"%.1fk\",$wtokens/1000}")
    else
        tokdisp="$wtokens"
    fi

    if (( est_left >= 1000000 )); then
        leftdisp="1.00M"
    elif (( est_left >= 1000 )); then
        leftdisp=$(awk "BEGIN{printf \"%.1fk\",$est_left/1000}")
    else
        leftdisp="$est_left"
    fi

    case_now="-"
    stage="-"

    if [[ -f "$log" ]]; then
        case_now=$(grep -E '\] CLAIMED ' "$log" 2>/dev/null |
            tail -1 | sed -E 's/.*CLAIMED ([^ ]+).*/\1/')
        [[ -n "$case_now" ]] || case_now="-"

        last_stage=$(grep -E \
          'Prompt 01|Prompt 02|Prompt 03|Prompt 04|fixed-valid| -> ' \
          "$log" 2>/dev/null | tail -1)

        case "$last_stage" in
          *"Prompt 01"*) stage="P01" ;;
          *"Prompt 02"*) stage="P02" ;;
          *"Prompt 03"*) stage="P03" ;;
          *"Prompt 04"*) stage="P04" ;;
          *"fixed-valid"*) stage="D4J" ;;
          *" -> "*) stage="DONE" ;;
          *) stage="-" ;;
        esac
    fi

    printf "%-5s %-10s %-8s %-9s %-11s %-11s %-22s %-6s\n" \
      "$w" "$state" "${pid:-?}" "$ram" "$tokdisp" "$leftdisp" \
      "$case_now" "$stage"
done

echo
echo "Workers : total=$worker_count  running=$running_count  quota=$quota_count"
printf "Run tokens observed in worker logs: %'d\n" "$total_run_tokens"
echo
echo "NOTE: EST LEFT = 1,000,000 - tokens used by that worker in THIS run."
echo "      It is not the exact KKU account balance if that account was used earlier today."

echo
echo "=== RESULT STATUS ======================================================"
python3 - <<'PY'
import json
from pathlib import Path
from collections import Counter

root = Path("AI2_GitHubCopilot/Result")
run = "run-copilot-final-v2"

statuses = Counter()
tokens = 0
cases = 0

for p in root.glob(f"*/{run}/case_status.json"):
    try:
        d=json.loads(p.read_text())
    except Exception:
        continue

    cases += 1
    statuses[d.get("status","UNKNOWN")] += 1

    t=((d.get("tokens") or {}).get("totals") or {}).get("total_tokens")
    if isinstance(t,int):
        tokens += t

print(f"Terminal cases : {cases}")
for k in [
    "DONE",
    "INVALID_AFTER_REPAIR",
    "INVALID_AFTER_PROMPT04",
    "OUTPUT_INCOMPLETE",
    "ERROR",
]:
    print(f"{k:25s}: {statuses.get(k,0)}")

if cases:
    print(f"\nTerminal tokens: {tokens:,}")
    print(f"Mean terminal : {tokens/cases:,.0f} tokens/case")
PY

echo
echo "=== PROMPT TOKEN REPORT ==============================================="
python3 Experiment/automation/token_report_copilot.py \
  --run-id copilot-final-v2 2>/dev/null | head -25 || true

echo
echo "=== SHARED DEFECTS4J SLOTS — CHATGPT + COPILOT ========================"
active_slots=0

for d in Experiment/runtime/run-final-opt/d4j-slots/slot-*; do
    [[ -d "$d" ]] || continue
    active_slots=$((active_slots + 1))

    slot=$(basename "$d")
    owner=$(python3 - "$d/owner.json" <<'PY'
import json,sys
try:
    d=json.load(open(sys.argv[1]))
    print(
        f"worker={d.get('worker','?')} "
        f"pid={d.get('pid','?')} "
        f"case={d.get('case','?')}"
    )
except Exception:
    print("?")
PY
)
    echo "$slot  $owner"
done

[[ "$active_slots" -eq 0 ]] && echo "No active D4J slots"
echo "Active: $active_slots / 3"

echo
echo "=== JAVA / PYTHON RESOURCE USE ========================================"
ps -eo pid,comm,%cpu,%mem,rss --sort=-rss |
  awk 'NR==1 || $2=="java" || $2=="python3" {print}' |
  head -15

echo
echo "=== RECENT COPILOT ACTIVITY =========================================="
for log in "$LOGDIR"/C*.log; do
    [[ -f "$log" ]] || continue
    w=$(basename "$log" .log)

    last=$(grep -E \
      'CLAIMED|Prompt 0[1-4]|fixed-valid| -> |QUEUE COMPLETE|quota|ERROR' \
      "$log" 2>/dev/null | tail -1)

    [[ -n "$last" ]] && printf "%-5s %s\n" "$w" "$last"
done

echo
echo "======================================================================"
