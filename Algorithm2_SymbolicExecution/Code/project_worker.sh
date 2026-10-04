#!/usr/bin/env bash
# =============================================================================
# project_worker.sh — ทำ 1 bug ของ project ใดก็ได้ (ตาม per-project config)
# ใช้: bash project_worker.sh <Project> <bug_id> <Round>
# ผล: Symflower/Result_<Round>/<proj>_bug<N>.csv + logs + test copies
# =============================================================================
set -u
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export PATH="$JAVA_HOME/bin:$HOME/defects4j/framework/bin:$PATH"

P="${1:?usage: project_worker.sh <Project> <bug> <Round>}"
N="${2:?}"
ROUND="${3:-Round1}"
ROUND_LOCAL="$ROUND"
MEM=2560

BASE="$HOME/sqa-project"
RES="$BASE/Symflower/Result_$ROUND"
WS="$BASE/workspaces"
TESTOUT="$BASE/Symflower/Test"
pl="$(echo "$P" | tr 'A-Z' 'a-z')"
CSVF="$RES/${pl}_bug${N}.csv"
WB="$WS/${pl}_${N}b"; WF="$WS/${pl}_${N}f"

project_cfg() {
  case "$1" in
    Lang)            HIDE_POM=1; JUNIT=JUnit4;;
    Chart)           HIDE_POM=0; JUNIT=JUnit4;;   # ant เก่า — probe ก่อน
    Cli)             HIDE_POM=1; JUNIT=JUnit4;;
    Closure)         HIDE_POM=0; JUNIT=JUnit4;;
    Codec)           HIDE_POM=1; JUNIT=JUnit4;;
    Collections)     HIDE_POM=1; JUNIT=JUnit4;;
    Compress)        HIDE_POM=1; JUNIT=JUnit4;;
    Csv)             HIDE_POM=1; JUNIT=JUnit4;;
    Gson)            HIDE_POM=1; JUNIT=JUnit4;;   # gradle
    JacksonCore)     HIDE_POM=1; JUNIT=JUnit4;;
    JacksonDatabind) HIDE_POM=1; JUNIT=JUnit4;;
    JacksonXml)      HIDE_POM=1; JUNIT=JUnit4;;
    Jsoup)           HIDE_POM=1; JUNIT=JUnit4;;
    JxPath)          HIDE_POM=1; JUNIT=JUnit4;;
    Math)            HIDE_POM=1; JUNIT=JUnit4;;
    Mockito)         HIDE_POM=1; JUNIT=JUnit5;;
    Time)            HIDE_POM=0; JUNIT=JUnit4;;
    *)               HIDE_POM=1; JUNIT=JUnit4;;
  esac
}
project_cfg "$P"

rm -rf "$WB" "$WF"
flock /tmp/d4j_checkout.lock -c "defects4j checkout -p '$P' -v '${N}b' -w '$WB'" > "$RES/${pl}_bug${N}_checkout_b.log" 2>&1 \
  || { echo "Round$ROUND,package,$P,$N,-,-,-,-,-,-,-,-,checkout_b_fail" > "$CSVF"; echo "[skip] $P-$N checkout_b"; exit 0; }
flock /tmp/d4j_checkout.lock -c "defects4j checkout -p '$P' -v '${N}f' -w '$WF'" > "$RES/${pl}_bug${N}_checkout_f.log" 2>&1 \
  || { echo "Round$ROUND,package,$P,$N,-,-,-,-,-,-,-,-,checkout_f_fail" > "$CSVF"; echo "[skip] $P-$N checkout_f"; exit 0; }

MCLASSES=$(python3 - "$RES/meta_${pl}.csv" "$N" <<'PY'
import csv, sys
with open(sys.argv[1]) as f:
    for row in csv.reader(f):
        if row and row[0] == sys.argv[2]:
            print(row[1]); print(row[2]); break
PY
)
CLASSES=$(echo "$MCLASSES" | sed -n 1p)
TRIGGERS=$(echo "$MCLASSES" | sed -n 2p | tr ',;' '\n\n' | sed 's/^ *//;s/ *$//' | grep '::' | sort -u)
FIRSTCLASS=$(echo "$CLASSES" | tr -d '"' | cut -d';' -f1 | cut -d, -f1 | tr -d ' ')
PKGPATH=$(echo "$FIRSTCLASS" | tr '.' '/')
PKGDIR="${PKGPATH%/*}"
TARGET="src/main/java/$PKGDIR"
if [ ! -d "$WB/$TARGET" ] && [ ! -f "$WB/$TARGET" ]; then
  TARGET=$(dirname "$(find "$WB/src" -path "*${PKGPATH}.java" 2>/dev/null | head -1)")
fi
NJ=$(find "$WB/$TARGET" -maxdepth 1 -name "*.java" 2>/dev/null | wc -l)
[ "$NJ" -gt 10 ] && TARGET="src/main/java/$PKGPATH.java"
[ -d "$WB/$TARGET" ] || [ -f "$WB/$TARGET" ] || TARGET="src/main/java"

if [ "$HIDE_POM" = 1 ]; then
  for W2 in "$WB" "$WF"; do
    [ -f "$W2/pom.xml" ] && mv "$W2/pom.xml" "$W2/pom.xml.bak"
    [ -f "$W2/build.gradle" ] && mv "$W2/build.gradle" "$W2/build.gradle.bak"
    [ -f "$W2/build.gradle.kts" ] && mv "$W2/build.gradle.kts" "$W2/build.gradle.kts.bak"
  done
fi

cd "$WB" || { echo "Round$ROUND,package,$P,$N,-,-,-,-,-,-,-,-,cd_fail" > "$CSVF"; exit 0; }
T0=$(date +%s)
symflower unit-tests --memory-limit="$MEM" --java-test-framework="$JUNIT" $SYMFLAGS "$TARGET" > "$RES/${pl}_bug${N}_symflower.log" 2>&1
GEN_TIME=$(( $(date +%s) - T0 ))

while IFS= read -r f; do
  d="src/test/java/${f#src/main/java/}"; mkdir -p "$(dirname "$d")"; mv "$f" "$d"
done < <(find src/main/java -name "*SymflowerTest.java" 2>/dev/null)
NFILES=$(find src/test/java -name "*SymflowerTest.java" 2>/dev/null | wc -l)
NLINES=$(find src/test/java -name "*SymflowerTest.java" -exec cat {} + 2>/dev/null | grep -c '@Test' || true)
mkdir -p "$TESTOUT/${P}-${N}"
find src/test/java -name "*SymflowerTest.java" -exec cp --parents {} "$TESTOUT/${P}-${N}/" \; 2>/dev/null

defects4j test > "$RES/${pl}_bug${N}_test_buggy.log" 2>&1 || true
FAILB=$(awk '/^Failing tests:/{f=1;next} f && /::/{gsub(/^[ \t]+-[ \t]+/,""); print $1}' "$RES/${pl}_bug${N}_test_buggy.log" | sort -u)
(cd "$WB" && find src/test/java -name '*SymflowerTest.java' -print0 | tar --null -cf - -T -) | (cd "$WF" && tar -xf -)
(cd "$WF" && defects4j test > "$RES/${pl}_bug${N}_test_fixed.log" 2>&1) || true
FAILF=$( (cd "$WF" && awk '/^Failing tests:/{f=1;next} f && /::/{gsub(/^[ \t]+-[ \t]+/,""); print $1}' "$RES/${pl}_bug${N}_test_fixed.log" | sort -u) || true)

ALLDET=$(comm -23 <(echo "$FAILB") <(echo "$FAILF"))
if [ -n "$TRIGGERS" ]; then
  DETECTED=$(echo "$ALLDET" | grep -vxF -f <(echo "$TRIGGERS") || true)
else
  DETECTED="$ALLDET"
fi

(cd "$WB" && defects4j coverage > "$RES/${pl}_bug${N}_coverage.log" 2>&1) || true
STMT=$(grep -oE 'Line coverage: [0-9]+(\.[0-9]+)?%' "$RES/${pl}_bug${N}_coverage.log" | grep -oE '[0-9]+(\.[0-9]+)?%' | head -1)
BRCH=$(grep -oE 'Condition coverage: [0-9]+(\.[0-9]+)?%' "$RES/${pl}_bug${N}_coverage.log" | grep -oE '[0-9]+(\.[0-9]+)?%' | head -1)
[ -z "$STMT" ] && STMT="NA"; [ -z "$BRCH" ] && BRCH="NA"

NB=$(echo "$FAILB" | grep -c '::' || true)
NF=$(echo "$FAILF" | grep -c '::' || true)
ND=$(echo "$DETECTED" | grep -c '::' || true)
NOTE=ok
grep -q "Cannot compile test suite" "$RES/${pl}_bug${N}_test_buggy.log" 2>/dev/null && NOTE=compile_fail

echo "Round$ROUND,package,$P,$N,$NFILES,$NLINES,$NB,$NF,$ND,$STMT,$BRCH,$GEN_TIME,$NOTE" > "$CSVF"
echo "[done] $P-$N: files=$NFILES tests=$NLINES det=$ND time=${GEN_TIME}s"
rm -rf "$WB" "$WF"
