#!/usr/bin/env bash
set -euo pipefail
ROOT="/root/SQAProjectGroup11-git"
RUN_ID="${RUN_ID:-final-opt}"
D4J_SLOTS="${D4J_SLOTS:-3}"
MODEL="${MODEL:-gpt-5.6-terra}"
CASES="${CASES:-JacksonXml-1,JacksonXml-2,JacksonXml-3}"
cd "$ROOT"
D4J_BIN=""
if command -v defects4j >/dev/null 2>&1; then D4J_BIN="$(dirname "$(command -v defects4j)")"
elif [[ -x "$HOME/defects4j/framework/bin/defects4j" ]]; then D4J_BIN="$HOME/defects4j/framework/bin"
elif [[ -x "/root/defects4j/framework/bin/defects4j" ]]; then D4J_BIN="/root/defects4j/framework/bin"
else echo "ERROR: defects4j not found"; exit 1; fi
export PATH="$D4J_BIN:$PATH"
read -r -s -p "IntelSphere API KEY for optimized pilot: " API_KEY; echo
export KKU_INTELSPHERE_API_KEY="$API_KEY"
python3 Experiment/automation/ai_runner.py \
  --provider chatgpt --model "$MODEL" \
  --run-id "$RUN_ID" --worker PILOT \
  --d4j-slots "$D4J_SLOTS" \
  --queue-cases "$CASES" --resume
unset KKU_INTELSPHERE_API_KEY API_KEY
python3 Experiment/automation/token_report_opt.py
