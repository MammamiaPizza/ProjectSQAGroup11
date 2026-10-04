#!/usr/bin/env bash
# =============================================================================
# parallel_runner.sh — รัน Symflower หลาย bug พร้อมกันด้วย N workers บนเครื่องเดียว
# ใช้: bash parallel_runner.sh <start> <end> <Round> <workers>
# ตัวอย่าง: bash parallel_runner.sh 1 39 Round1 3      # Lang ทั้ง project, 3 workers
#
# หลักการ: แบ่ง bug เป็นคิว (xargs -P) แต่ละ worker รัน 1 bug ผ่าน run_symflower_lang.sh
# (workspace แยกต่อ bug อยู่แล้วจึงไม่ชน) — CSV แยกต่อ worker แล้วรวมตอนจบ
# หมายเหตุ: memory ต่อ worker = 2560MB (เหมาะกับ WSL 12GB / 4 workers)
# =============================================================================
set -u
BASE="$HOME/sqa-project"
CODE="$BASE/Symflower/Code"
ROUND="${3:-Round1}"
W="${4:-3}"
MEM=2560
START="${1:-1}"; END="${2:-11}"

export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export PATH="$JAVA_HOME/bin:$HOME/defects4j/framework/bin:$PATH"

# สคริปต์ runner ต่อ bug: ตั้ง memory ต่ำลง + CSV ของ worker เอง (ผ่าน env)
cat > /tmp/worker_one_bug.sh << 'INNER'
#!/usr/bin/env bash
set -u
N="$1"; ROUND="$2"; MEM="$3"
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export PATH="$JAVA_HOME/bin:$HOME/defects4j/framework/bin:$PATH"
RES="$HOME/sqa-project/Symflower/Result_$ROUND"
WS="$HOME/sqa-project/workspaces"
CSV="$RES/lang_results_bug${N}.csv"   # 1 bug = 1 CSV (ชนกันไม่ได้ตามนิยาม)
rm -f "$CSV"
[ -f "$CSV" ] || echo "round,scope,project,bug_id,n_test_files,n_generated_lines,fail_buggy,fail_fixed,detected,stmt_cov,branch_cov,gen_time_sec,notes" >> "$CSV"

WB="$WS/lang_${N}b"; WF="$WS/lang_${N}f"
rm -rf "$WB" "$WF"
# checkout ต้อง serialize (d4j ใช้ shared project repo — กัน git lock ชนกัน)
flock /tmp/d4j_checkout.lock -c "defects4j checkout -p Lang -v '${N}b' -w '$WB'" > "$RES/bug${N}_checkout_b.log" 2>&1 \
  || { echo "Round1,package,Lang,$N,-,-,-,-,-,-,-,-,checkout_b_fail" >> "$CSV"; exit 0; }
flock /tmp/d4j_checkout.lock -c "defects4j checkout -p Lang -v '${N}f' -w '$WF'" > "$RES/bug${N}_checkout_f.log" 2>&1 \
  || { echo "Round1,package,Lang,$N,-,-,-,-,-,-,-,-,checkout_f_fail" >> "$CSV"; exit 0; }

MCLASSES=$(python3 -c "
import csv
with open('$RES/lang_meta.csv') as f:
    for row in csv.reader(f):
        if row and row[0]=='$N':
            print(row[1]); print(row[2]); break
")
CLASSES=$(echo "$MCLASSES" | sed -n 1p)
TRIGGERS=$(echo "$MCLASSES" | sed -n 2p | tr ',' '\n' | sed 's/^ *//;s/ *$//' | grep '::' | sort -u)
FIRSTCLASS=$(echo "$CLASSES" | cut -d, -f1 | tr -d ' ')
PKGPATH=$(echo "$FIRSTCLASS" | tr '.' '/')
PKGDIR="${PKGPATH%/*}"
TARGET="src/main/java/$PKGDIR"
NJ=$(find "$WB/$TARGET" -maxdepth 1 -name "*.java" 2>/dev/null | wc -l)
[ "$NJ" -gt 10 ] && TARGET="src/main/java/$PKGPATH.java"

[ -f "$WB/pom.xml" ] && mv "$WB/pom.xml" "$WB/pom.xml.bak"
[ -f "$WF/pom.xml" ] && mv "$WF/pom.xml" "$WF/pom.xml.bak"

cd "$WB" || exit 0
T0=$(date +%s)
symflower unit-tests --memory-limit="$MEM" --java-test-framework=JUnit4 "$TARGET" > "$RES/bug${N}_symflower.log" 2>&1
GEN_TIME=$(( $(date +%s) - T0 ))

while IFS= read -r f; do
  d="src/test/java/${f#src/main/java/}"; mkdir -p "$(dirname "$d")"; mv "$f" "$d"
done < <(find src/main/java -name "*SymflowerTest.java" 2>/dev/null)
NFILES=$(find src/test/java -name "*SymflowerTest.java" | wc -l)
NLINES=$(find src/test/java -name "*SymflowerTest.java" -exec cat {} + 2>/dev/null | grep -c '@Test' || true)
mkdir -p "$HOME/sqa-project/Symflower/Test/bug$N"
find src/test/java -name "*SymflowerTest.java" -exec cp --parents {} "$HOME/sqa-project/Symflower/Test/bug$N/" \;

defects4j test > "$RES/bug${N}_test_buggy.log" 2>&1 || true
FAILB=$(awk '/^Failing tests:/{f=1;next} f && /::/{gsub(/^[ \t]+-[ \t]+/,""); print $1}' "$RES/bug${N}_test_buggy.log" | sort -u)
(cd "$WB" && find src/test/java -name '*SymflowerTest.java' -print0 | tar --null -cf - -T -) | (cd "$WF" && tar -xf -)
(cd "$WF" && defects4j test > "$RES/bug${N}_test_fixed.log" 2>&1) || true
FAILF=$( (cd "$WF" && awk '/^Failing tests:/{f=1;next} f && /::/{gsub(/^[ \t]+-[ \t]+/,""); print $1}' "$RES/bug${N}_test_fixed.log" | sort -u) || true)
ALLDET=$(comm -23 <(echo "$FAILB") <(echo "$FAILF"))
if [ -n "$TRIGGERS" ]; then
  DETECTED=$(echo "$ALLDET" | grep -vxF -f <(echo "$TRIGGERS") || true)
else
  DETECTED="$ALLDET"
fi

(cd "$WB" && defects4j coverage > "$RES/bug${N}_coverage.log" 2>&1) || true
STMT=$(grep -oE 'Line coverage: [0-9]+(\.[0-9]+)?%' "$RES/bug${N}_coverage.log" | grep -oE '[0-9]+(\.[0-9]+)?%' | head -1)
BRCH=$(grep -oE 'Condition coverage: [0-9]+(\.[0-9]+)?%' "$RES/bug${N}_coverage.log" | grep -oE '[0-9]+(\.[0-9]+)?%' | head -1)
[ -z "$STMT" ] && STMT="NA"; [ -z "$BRCH" ] && BRCH="NA"

NB=$(echo "$FAILB" | grep -c '::' || true)
NF=$(echo "$FAILF" | grep -c '::' || true)
ND=$(echo "$DETECTED" | grep -c '::' || true)
NOTE=ok
grep -q "Cannot compile test suite" "$RES/bug${N}_test_buggy.log" 2>/dev/null && NOTE=compile_fail
echo "Round1,package,Lang,$N,$NFILES,$NLINES,$NB,$NF,$ND,$STMT,$BRCH,$GEN_TIME,$NOTE" >> "$CSV"
echo "[done] bug $N: files=$NFILES tests=$NLINES det=$ND time=${GEN_TIME}s"
rm -rf "$WB" "$WF"   # ล้าง workspace ทันที กัน disk บาน
INNER
chmod +x /tmp/worker_one_bug.sh

RES="$BASE/Symflower/Result_$ROUND"
mkdir -p "$RES"
[ -f "$RES/lang_meta.csv" ] || { export PATH="$HOME/defects4j/framework/bin:$PATH"; export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64; defects4j query -p Lang -q "bug.id,classes.modified,tests.trigger" > "$RES/lang_meta.csv"; }

echo "== เริ่ม: Lang $START-$END, $W workers, mem=${MEM}MB/worker, round=$ROUND =="
rm -f "$RES"/lang_results_bug*.csv
seq "$START" "$END" | xargs -P "$W" -I{} bash /tmp/worker_one_bug.sh {} "$ROUND" "$MEM"

# รวม CSV ทุก bug
OUT="$RES/lang_results_parallel.csv"
echo "round,scope,project,bug_id,n_test_files,n_generated_lines,fail_buggy,fail_fixed,detected,stmt_cov,branch_cov,gen_time_sec,notes" > "$OUT"
cat "$RES"/lang_results_bug*.csv 2>/dev/null >> "$OUT"
sort -t, -k4 -n "$OUT" -o "$OUT"
echo "== เสร็จ =="
cat "$OUT"
