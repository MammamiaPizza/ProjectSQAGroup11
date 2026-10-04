#!/usr/bin/env bash
# =============================================================================
# project_runner.sh — จัดคิว project ใด ๆ ข้าม deprecated ids แล้วรันขนาน
# ใช้: bash project_runner.sh <Project> <start> <end> <Round> [workers]
# ตัวอย่าง: bash project_runner.sh Csv 1 16 Round1 3
# =============================================================================
set -u
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export PATH="$JAVA_HOME/bin:$HOME/defects4j/framework/bin:$PATH"

PROJ="${1:?usage: project_runner.sh <Project> <start> <end> <Round> [workers]}"
START="${2:-1}"; END="${3:-10}"
ROUND="${4:-Round1}"
W="${5:-3}"

BASE="$HOME/sqa-project"
RES="$BASE/Symflower/Result_$ROUND"
CODE="$BASE/Symflower/Code"
mkdir -p "$RES"

# metadata (active/deprecated + modified classes + triggers)
META="$RES/meta_$(echo "$PROJ" | tr 'A-Z' 'a-z').csv"
[ -s "$META" ] || defects4j query -p "$PROJ" -q "bug.id,classes.modified,tests.trigger" > "$META" 2>"$RES/meta_$(echo "$PROJ" | tr 'A-Z' 'a-z').err"

echo "== $PROJ $START-$END | workers=$W | round=$ROUND =="
JOBS=""
for N in $(seq "$START" "$END"); do
  if awk -F, -v id="$N" '$1==id{f=1} END{exit !f}' "$META"; then
    JOBS="$JOBS $N"
  else
    echo "Round$ROUND,package,$PROJ,$N,-,-,-,-,-,-,-,-,deprecated" >> "$RES/${PROJ,,}_results_summary.log"
    echo "  [deprecated] $PROJ-$N ข้าม"
  fi
done

[ -z "${JOBS// /}" ] && { echo "ไม่มี active bug ในช่วงนี้"; exit 0; }
echo "  คิว:$(echo "$JOBS" | tr ' ' '\n' | grep -v '^$' | tr '\n' ' ')"

rm -f "$RES"/$(echo "$PROJ" | tr 'A-Z' 'a-z')_bug*.csv 2>/dev/null
echo "$JOBS" | tr ' ' '\n' | grep -v '^$' | \
  xargs -P "$W" -I{} bash "$CODE/project_worker.sh" "$PROJ" {} "$ROUND"

# รวมผล
OUT="$RES/${PROJ,,}_results.csv"
echo "round,scope,project,bug_id,n_test_files,n_generated_lines,fail_buggy,fail_fixed,detected,stmt_cov,branch_cov,gen_time_sec,notes" > "$OUT"
cat "$RES"/$(echo "$PROJ" | tr 'A-Z' 'a-z')_bug*.csv 2>/dev/null >> "$OUT"
sort -t, -k4 -n "$OUT" -o "$OUT"
echo "== $PROJ เสร็จ — $OUT =="
