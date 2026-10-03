#!/usr/bin/env bash
set -euo pipefail

root="$(cd "$(dirname "$0")/.." && pwd)"
result="$root/AI2_GitHubCopilot/Result/Cli-1/run-01"
test_file="$root/AI2_GitHubCopilot/TestCode/Cli-1/run-01/repaired-corrected/CommandLineCopilotGeneratedTest.java"

for version in 1b 1f; do
  workspace="$HOME/sqa-workspaces/Cli-$version-copilot"
  cp "$test_file" "$workspace/src/test/org/apache/commons/cli/CommandLineCopilotGeneratedTest.java"

  archive="$result/Cli-$version-copilot-corrected.1.tar.bz2"
  tar -cjf "$archive" -C "$workspace/src/test" \
    org/apache/commons/cli/CommandLineCopilotGeneratedTest.java

  defects4j coverage -w "$workspace" -s "$archive" \
    > "$result/13_${version}_corrected_coverage.log" 2>&1
  cp "$workspace/coverage.xml" "$result/13_${version}_corrected_coverage.xml"

  echo "=== Cli-$version coverage ==="
  grep -E 'Lines total:|Lines covered:|Conditions total:|Conditions covered:|Line coverage:|Condition coverage:' \
    "$result/13_${version}_corrected_coverage.log"
done

python3 - "$root" <<'PY'
from pathlib import Path
from xml.etree import ElementTree
import re
import sys

root = Path(sys.argv[1])
ctx = root / "Experiment/contexts/Cli-1/run-01"
tool = root / "AI2_GitHubCopilot"
run = tool / "Result/Cli-1/run-01"
template = (tool / "Prompt/04_extend_coverage.txt").read_text()
suite = (tool / "TestCode/Cli-1/run-01/repaired-corrected/CommandLineCopilotGeneratedTest.java").read_text()

xml = ElementTree.parse(run / "13_1b_corrected_coverage.xml").getroot()
matches = [c for c in xml.iter("class")
           if c.get("name", "").replace("/", ".") == "org.apache.commons.cli.CommandLine"]
if len(matches) != 1:
    raise SystemExit(f"พบ CommandLine ใน coverage.xml {len(matches)} รายการ; ยังไม่สร้าง prompt")

log = (run / "13_1b_corrected_coverage.log").read_text()
summary = [line.strip() for line in log.splitlines()
           if re.search(r"^(Lines total|Lines covered|Conditions total|Conditions covered|Line coverage|Condition coverage):", line.strip())]
observations = []
for line in matches[0].iter("line"):
    observations.append(
        f"line={line.get('number')} hits={line.get('hits')} "
        f"branch={line.get('branch', 'false')} "
        f"condition-coverage={line.get('condition-coverage', 'N/A')}"
    )

values = {
    "[PROJECT_ID]": "Cli",
    "[BUG_ID]": "1 (CLI-13)",
    "[SOURCE_VERSION]": "Buggy (Cli-1b)",
    "[JUNIT_VERSION]": "JUnit 3.8.1",
    "[BUILD_TOOL]": "Ant via Defects4J",
    "[TARGET_CLASS]": "org.apache.commons.cli.CommandLine",
    "[SPECIFICATION_OR_BUG_REPORT]": (ctx / "permitted-specification.txt").read_text().strip(),
    "[JAVA_SOURCE_CODE]": (ctx / "CommandLine.java").read_text().strip(),
    "[PROJECT_CONTEXT]": (ctx / "project-context.txt").read_text().strip(),
    "[CURRENT_TEST_SUITE]": suite.strip(),
    "[COVERAGE_REPORT]": "\n".join(summary + observations),
}
for key, value in values.items():
    if key not in template:
        raise SystemExit(f"แม่แบบไม่มี {key}")
    template = template.replace(key, value)

destination = tool / "Prompt/Cli-1/run-01/04_extend_coverage.txt"
destination.parent.mkdir(parents=True, exist_ok=True)
destination.write_text(template)
(run / "04_sent.txt").write_text(template)
print(f"สร้าง Prompt 04 แล้ว: {run / '04_sent.txt'}")
PY
