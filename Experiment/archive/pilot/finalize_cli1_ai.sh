#!/usr/bin/env bash
set -euo pipefail

root="$(cd "$(dirname "$0")/.." && pwd)"
chat_result="$root/AI1_ChatGPT/Result/Cli-1/run-01"
chat_test="$root/AI1_ChatGPT/TestCode/Cli-1/run-01/coverage-extended/CommandLineGeneratedTest.java"

# วัด coverage ของ ChatGPT Prompt 04 จาก test ที่สร้างจริง
for version in 1b 1f; do
  workspace="$HOME/sqa-workspaces/Cli-$version-chatgpt"
  destination="$workspace/src/test/org/apache/commons/cli/CommandLineGeneratedTest.java"
  archive="$chat_result/Cli-$version-chatgpt-extended.1.tar.bz2"

  cmp "$chat_test" "$destination"
  tar -cjf "$archive" -C "$workspace/src/test" \
    org/apache/commons/cli/CommandLineGeneratedTest.java

  defects4j coverage -w "$workspace" -s "$archive" \
    > "$chat_result/16_${version}_extended_coverage.log" 2>&1
  cp "$workspace/coverage.xml" \
    "$chat_result/16_${version}_extended_coverage.xml"
done

# อ่านตัวเลขและจำนวน test จาก log จริง ไม่กรอกผลด้วยมือ
python3 - "$root" <<'PY'
from pathlib import Path
import csv
import re
import sys

root = Path(sys.argv[1])
diagnostic = root / "Experiment/diagnostics/Cli-1/results.csv"
if not diagnostic.is_file():
    raise SystemExit("ไม่พบผล manual diagnostic")

rows = []
for tool, folder, count, test_prefix, coverage_prefix in [
    ("ChatGPT", "AI1_ChatGPT", 25, "09", "16"),
    ("GitHubCopilot", "AI2_GitHubCopilot", 11, "14", "15"),
]:
    run = root / folder / "Result/Cli-1/run-01"
    recorded = {}

    for version in ("1b", "1f"):
        test_log = run / f"{test_prefix}_{version}_extended_full.log"
        coverage_log = run / f"{coverage_prefix}_{version}_extended_coverage.log"

        test_text = test_log.read_text()
        coverage_text = coverage_log.read_text()

        if f"OK ({count} tests)" not in test_text:
            raise SystemExit(f"จำนวนหรือผล test ไม่ตรง: {test_log}")
        if "Running ant (coverage.report)" not in coverage_text:
            raise SystemExit(f"coverage ไม่สมบูรณ์: {coverage_log}")

        values = {}
        for label in (
            "Lines total", "Lines covered",
            "Conditions total", "Conditions covered",
        ):
            match = re.search(rf"{label}:\s*(\d+)", coverage_text)
            if not match:
                raise SystemExit(f"ไม่พบ {label}: {coverage_log}")
            values[label] = int(match.group(1))
        recorded[version] = values

    buggy, fixed = recorded["1b"], recorded["1f"]
    rows.append({
        "project": "Cli",
        "bug": "1",
        "method": tool,
        "stage": "Prompt 04 extended",
        "tests": count,
        "buggy_passing": count,
        "fixed_passing": count,
        "detected_bug": 0,
        "buggy_lines": f'{buggy["Lines covered"]}/{buggy["Lines total"]}',
        "buggy_conditions": (
            f'{buggy["Conditions covered"]}/{buggy["Conditions total"]}'
        ),
        "fixed_lines": f'{fixed["Lines covered"]}/{fixed["Lines total"]}',
        "fixed_conditions": (
            f'{fixed["Conditions covered"]}/{fixed["Conditions total"]}'
        ),
    })

output = root / "Experiment/cli1_ai_results.csv"
with output.open("w", newline="") as f:
    writer = csv.DictWriter(f, fieldnames=list(rows[0]))
    writer.writeheader()
    writer.writerows(rows)

print("\n=== AI results: Cli-1 ===")
print(output.read_text())
print("=== Manual diagnostic (ไม่รวมในผล AI) ===")
print(diagnostic.read_text())
PY

echo "=== Git status: ยังไม่ได้ commit หรือ push ==="
git -C "$root" status -sb
