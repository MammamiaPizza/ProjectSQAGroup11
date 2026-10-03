#!/usr/bin/env bash
set -euo pipefail
ROOT="/root/SQAProjectGroup11-git"
cd "$ROOT"
MODEL="deepseek-v4-pro"
RUN_ID="final-opt"
mkdir -p /tmp/sqa-copilot-clean/Cpilot

if ! command -v copilot >/dev/null 2>&1; then echo "copilot not found"; exit 1; fi
if ! copilot --help 2>&1 | grep -q -- '--usage-output-file'; then
  echo "This CLI does not expose --usage-output-file; send the output of: copilot --help | grep -A2 -B2 usage-output-file"
  exit 1
fi
if ! command -v defects4j >/dev/null 2>&1; then
  for d in "$HOME/defects4j/framework/bin" /root/defects4j/framework/bin; do
    if [[ -x "$d/defects4j" ]]; then export PATH="$d:$PATH"; break; fi
  done
fi
read -s -p "KKU API Key for Copilot/DeepSeek pilot: " KKU_KEY
echo
export COPILOT_PROVIDER_TYPE="openai"
export COPILOT_PROVIDER_BASE_URL="https://gen.ai.kku.ac.th/api/v1"
export COPILOT_PROVIDER_API_KEY="$KKU_KEY"
export COPILOT_MODEL="$MODEL"
export COPILOT_AUTO_UPDATE="false"
trap 'unset KKU_KEY COPILOT_PROVIDER_API_KEY' EXIT

python3 Experiment/automation/ai2_runner.py \
  --provider copilot \
  --model "$MODEL" \
  --run-id "$RUN_ID" \
  --worker Cpilot \
  --d4j-slots 3 \
  --queue-cases "JacksonXml-1,JacksonXml-2,JacksonXml-3" \
  --resume

python3 Experiment/automation/token_report_copilot.py
