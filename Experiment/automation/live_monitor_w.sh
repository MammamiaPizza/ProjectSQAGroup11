#!/usr/bin/env bash
set -u

ROOT="/root/SQAProjectGroup11-git"
LOGDIR="$ROOT/Experiment/logs/final-opt"
RESULT="$ROOT/AI1_ChatGPT/Result"
RUN_ID="run-final-opt"

cd "$ROOT" || exit 1

clear

echo "============================================================================="
echo "                 CHATGPT / TERRA WORKERS LIVE MONITOR"
echo "============================================================================="
echo "Time     : $(date '+%Y-%m-%d %H:%M:%S')"
echo "Provider : KKU IntelSphere"
echo "Model    : gpt-5.6-terra"
echo "Run      : final-opt"
echo

echo "=== SYSTEM ==================================================================="
free -h | sed -n '1,3p'
printf "CPU cores: "
nproc
echo

echo "=== W WORKERS ================================================================="
printf "%-5s %-9s %-8s %-7s %-10s %-10s %-22s %-7s\n" \
       "WORK" "STATE" "PID" "RAM" "USED" "LEFT" "CASE" "STAGE"
printf "%-5s %-9s %-8s %-7s %-10s %-10s %-22s %-7s\n" \
       "----" "-----" "---" "---" "----" "----" "----" "-----"

workers=0
running=0
quota=0
stopped=0

# Sort W1 W2 ... W10 numerically
mapfile -t PIDFILES < <(
  find "$LOGDIR" -maxdepth 1 -type f -name 'W*.pid' 2>/dev/null |
  sort -V
)

for pf in "${PIDFILES[@]}"; do
    w=$(basename "$pf" .pid)
    log="$LOGDIR/$w.log"

    # Prevent hidden newline / CR in pid files
    pid=$(tr -dc '0-9' < "$pf" 2>/dev/null)

    workers=$((workers + 1))

    if [[ -n "$pid" ]] && kill -0 "$pid" 2>/dev/null; then
        state="RUNNING"
        running=$((running + 1))

        ram=$(ps -p "$pid" -o rss= 2>/dev/null | awk '
        {
            if ($1 >= 1048576)
                printf "%.1fG", $1/1048576;
            else if ($1 > 0)
                printf "%.0fM", $1/1024;
            else
                printf "-"
        }')
        [[ -n "$ram" ]] || ram="-"

        # Only treat it as QUOTA when the most recent relevant state is quota.
        if [[ -f "$log" ]]; then
            recent=$(tail -n 120 "$log" 2>/dev/null)
            if echo "$recent" | grep -Eqi \
              'QUOTA PAUSED|DAILY QUOTA REACHED|daily quota|quota.*reached|wait.*quota'; then
                # If work has resumed after quota, don't keep showing QUOTA.
                last_quota_line=$(grep -nEi \
                  'QUOTA PAUSED|DAILY QUOTA REACHED|daily quota|quota.*reached|wait.*quota' \
                  "$log" 2>/dev/null | tail -1 | cut -d: -f1)

                last_work_line=$(grep -nE \
                  'CLAIMED|Prompt 0[1-4]|fixed-valid|acquired Defects4J' \
                  "$log" 2>/dev/null | tail -1 | cut -d: -f1)

                last_quota_line=${last_quota_line:-0}
                last_work_line=${last_work_line:-0}

                if (( last_quota_line >= last_work_line )); then
                    state="QUOTA"
                    quota=$((quota + 1))
                    running=$((running - 1))
                fi
            fi
        fi
    else
        state="STOPPED"
        stopped=$((stopped + 1))
        ram="-"
    fi

    # Parse token usage + latest real daily remaining from API responses.
    token_info=$(python3 - "$log" <<'PY'
import json, sys
from pathlib import Path

p = Path(sys.argv[1])
used = 0
remaining = None

if p.exists():
    for line in p.read_text(errors="ignore").splitlines():
        line = line.strip()
        if not (line.startswith("{") and line.endswith("}")):
            continue
        try:
            d = json.loads(line)
        except Exception:
            continue

        t = d.get("total_tokens")
        if isinstance(t, (int, float)):
            used += int(t)

        r = d.get("daily_remaining_tokens")
        if isinstance(r, (int, float)):
            remaining = int(r)

print(used)
print("-" if remaining is None else remaining)
PY
)

    used=$(echo "$token_info" | sed -n '1p')
    left=$(echo "$token_info" | sed -n '2p')

    [[ -n "$used" ]] || used=0
    [[ -n "$left" ]] || left="-"

    if (( used >= 1000000 )); then
        used_disp=$(awk "BEGIN {printf \"%.2fM\", $used/1000000}")
    elif (( used >= 1000 )); then
        used_disp=$(awk "BEGIN {printf \"%.1fk\", $used/1000}")
    else
        used_disp="$used"
    fi

    if [[ "$left" =~ ^[0-9]+$ ]]; then
        if (( left >= 1000000 )); then
            left_disp=$(awk "BEGIN {printf \"%.2fM\", $left/1000000}")
        elif (( left >= 1000 )); then
            left_disp=$(awk "BEGIN {printf \"%.1fk\", $left/1000}")
        else
            left_disp="$left"
        fi
    else
        left_disp="-"
    fi

    # Find current case + stage by replaying important log events.
    case_stage=$(python3 - "$log" "$state" <<'PY'
import re, sys
from pathlib import Path

p = Path(sys.argv[1])
state = sys.argv[2]

case = "-"
stage = "-"

if p.exists():
    for line in p.read_text(errors="ignore").splitlines():

        m = re.search(r'\]\s+CLAIMED\s+(\S+)', line)
        if m:
            case = m.group(1)
            stage = "CLAIM"

        if case != "-":
            if "Prompt 01" in line:
                stage = "P01"
            elif "Prompt 02" in line:
                stage = "P02"
            elif "Prompt 03" in line:
                stage = "P03"
            elif "Prompt 04" in line:
                stage = "P04"
            elif "acquired Defects4J" in line:
                stage = "D4J"

            if re.search(rf'{re.escape(case)}\s+->\s+', line):
                case = "-"
                stage = "-"

        if "QUEUE COMPLETE" in line:
            case = "-"
            stage = "DONE"

if state == "QUOTA":
    stage = "WAIT"

print(case)
print(stage)
PY
)

    case_now=$(echo "$case_stage" | sed -n '1p')
    stage=$(echo "$case_stage" | sed -n '2p')

    printf "%-5s %-9s %-8s %-7s %-10s %-10s %-22s %-7s\n" \
           "$w" "$state" "${pid:-"-"}" "$ram" \
           "$used_disp" "$left_disp" "$case_now" "$stage"
done

echo
echo "Workers: total=$workers  running=$running  quota=$quota  stopped=$stopped"
echo "LEFT = latest real daily_remaining_tokens returned by KKU API"
echo

echo "=== RESULT STATUS ============================================================="
python3 - <<'PY'
import json
from pathlib import Path
from collections import Counter

root = Path("AI1_ChatGPT/Result")
run = "run-final-opt"

statuses = Counter()
tokens = 0
cases = 0

for p in root.glob(f"*/{run}/case_status.json"):
    try:
        d = json.loads(p.read_text())
    except Exception:
        continue

    cases += 1
    statuses[d.get("status", "UNKNOWN")] += 1

    t = d.get("tokens", {})
    if isinstance(t, dict):
        v = (t.get("totals") or {}).get("total_tokens")
    else:
        v = None

    if not isinstance(v, int):
        v = d.get("total_tokens")

    if isinstance(v, int):
        tokens += v

print(f"Terminal cases              : {cases}")
print(f"DONE                        : {statuses['DONE']}")
print(f"INVALID_AFTER_REPAIR        : {statuses['INVALID_AFTER_REPAIR']}")
print(f"INVALID_AFTER_PROMPT04      : {statuses['INVALID_AFTER_PROMPT04']}")
print(f"OUTPUT_INCOMPLETE           : {statuses['OUTPUT_INCOMPLETE']}")
print(f"ERROR                       : {statuses['ERROR']}")

if cases:
    print(f"\nTerminal tokens             : {tokens:,}")
    if tokens:
        print(f"Mean terminal tokens/case   : {tokens/cases:,.0f}")
PY

echo
echo "=== TOKEN REPORT ==============================================================="
python3 Experiment/automation/token_report_opt.py 2>/dev/null | head -22 || true

echo
echo "=== SHARED DEFECTS4J SLOTS ====================================================="
slot_count=0

for d in Experiment/runtime/run-final-opt/d4j-slots/slot-*; do
    [[ -d "$d" ]] || continue

    slot_count=$((slot_count + 1))
    slot=$(basename "$d")

    owner=$(python3 - "$d/owner.json" <<'PY'
import json, sys
try:
    d = json.load(open(sys.argv[1]))
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

[[ "$slot_count" -eq 0 ]] && echo "No active D4J slots"
echo "Active slots: $slot_count / 3"

echo
echo "=== JAVA / PYTHON TOP RESOURCE USE ============================================="
ps -eo pid,comm,%cpu,%mem,rss --sort=-rss |
awk 'NR==1 || $2=="java" || $2=="python3" {print}' |
head -15

echo
echo "=== RECENT W ACTIVITY =========================================================="
for log in $(find "$LOGDIR" -maxdepth 1 -type f -name 'W*.log' 2>/dev/null | sort -V); do
    w=$(basename "$log" .log)

    last=$(grep -E \
      'CLAIMED|Prompt 0[1-4]|fixed-valid|QUOTA|DAILY QUOTA| -> |QUEUE COMPLETE|ERROR' \
      "$log" 2>/dev/null | tail -1)

    [[ -n "$last" ]] && printf "%-5s %s\n" "$w" "$last"
done

echo
echo "============================================================================="
