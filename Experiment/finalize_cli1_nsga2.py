#!/usr/bin/env python3
from pathlib import Path
import csv
import json
import re

root = Path(__file__).resolve().parent.parent
result = root / "Algorithm1_NSGAII/Result_Round1/Cli-1/run-01"
config = json.loads((result / "configuration.json").read_text())
bug_log = (result / "buggy_detection.log").read_text()

def coverage(version):
    path = result / f"final_{version}_coverage.log"
    text = path.read_text()
    if "Running ant (coverage.report)" not in text:
        raise SystemExit(f"coverage ไม่สมบูรณ์: {path}")
    values = {}
    for name in ("Lines total", "Lines covered",
                 "Conditions total", "Conditions covered"):
        found = re.search(rf"{re.escape(name)}:\s*(\d+)", text)
        if not found:
            raise SystemExit(f"ไม่พบ {name}: {path}")
        values[name] = int(found.group(1))
    return values

fixed = coverage("1f")
buggy = coverage("1b")

if "Running ant (run.gen.tests)" not in bug_log:
    raise SystemExit("buggy_detection.log ยังไม่ยืนยันว่ารัน generated tests")
if "Running ant (compile.gen.tests)" not in bug_log:
    raise SystemExit("buggy_detection.log ยังไม่ยืนยันว่า compile generated tests")

class_name = "org.apache.commons.cli.CommandLineNSGA2Test"
failed = sorted(set(re.findall(
    rf"^\s*-\s+({re.escape(class_name)}::[^\s]+)",
    bug_log, re.MULTILINE
)))
selected = config["selected_cases"]
test_source = (result / "CommandLineNSGA2Test.java").read_text()
method_count = len(re.findall(r"\bpublic void testCase\d+", test_source))
if method_count != len(selected):
    raise SystemExit("จำนวน methods ใน Java ไม่ตรงกับ selected_cases")

row = {
    "project": "Cli",
    "bug": 1,
    "method": "NSGA-II",
    "study_type": "post-hoc exploratory",
    "candidates_evaluated": config["evaluated_unique"],
    "selected_tests": method_count,
    "fixed_lines": f'{fixed["Lines covered"]}/{fixed["Lines total"]}',
    "fixed_conditions": (
        f'{fixed["Conditions covered"]}/{fixed["Conditions total"]}'
    ),
    "buggy_lines": f'{buggy["Lines covered"]}/{buggy["Lines total"]}',
    "buggy_conditions": (
        f'{buggy["Conditions covered"]}/{buggy["Conditions total"]}'
    ),
    "generated_tests_failing_on_buggy": len(failed),
    "fault_detected": int(bool(failed)),
}
with (result / "results.csv").open("w", newline="") as output:
    writer = csv.DictWriter(output, fieldnames=list(row))
    writer.writeheader()
    writer.writerow(row)

readme = f"""# NSGA-II exploratory run: Cli-1

- Candidate suites evaluated: {row['candidates_evaluated']}
- Selected tests: {row['selected_tests']} ({', '.join(selected)})
- Fixed coverage: {row['fixed_lines']} lines; {row['fixed_conditions']} conditions
- Buggy coverage: {row['buggy_lines']} lines; {row['buggy_conditions']} conditions
- Generated tests failing on buggy: {len(failed)}
- Detected fault: {row['fault_detected']}
- Failing generated tests: {', '.join(failed) if failed else 'none'}

Fitness ใช้ coverage จาก Defects4J บน Cli-1f; ตรวจ fault ด้วยชุดที่เลือกบน Cli-1b
การทดลองนี้เป็น post-hoc exploratory: candidate pool ถูกกำหนดหลังเห็น diff
ของ buggy/fixed จึงไม่รวมอัตราตรวจพบนี้กับผล AI แบบปิดข้อมูล
"""
(result / "README.md").write_text(readme)
print((result / "results.csv").read_text())
print("รายละเอียด:", result / "README.md")
