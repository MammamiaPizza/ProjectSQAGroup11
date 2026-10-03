#!/usr/bin/env python3

import csv
import json
import re
from pathlib import Path
from collections import Counter, defaultdict

ROOT = Path("/root/SQAProjectGroup11-git")

METHODS = [
    {
        "method": "ChatGPT",
        "root": ROOT / "AI1_ChatGPT/Result",
        "run": "run-final-opt",
    },
    {
        "method": "GitHub Copilot",
        "root": ROOT / "AI2_GitHubCopilot/Result",
        "run": "run-copilot-final-v2",
    },
]

REPORT_DIR = ROOT / "Report"
DATA_DIR = REPORT_DIR / "data"
SUMMARY_DIR = REPORT_DIR / "summaries"

DATA_DIR.mkdir(parents=True, exist_ok=True)
SUMMARY_DIR.mkdir(parents=True, exist_ok=True)

OUT_CSV = DATA_DIR / "ai_case_results.csv"
OUT_JSON = DATA_DIR / "ai_summary.json"
OUT_MD = SUMMARY_DIR / "ai_summary.md"


def pct(covered, total):
    if not isinstance(covered, (int, float)):
        return None
    if not isinstance(total, (int, float)) or total == 0:
        return None
    return covered / total * 100.0


rows = []

for cfg in METHODS:
    method = cfg["method"]
    result_root = cfg["root"]
    run_name = cfg["run"]

    for status_file in sorted(result_root.glob(f"*/{run_name}/case_status.json")):
        case = status_file.parent.parent.name

        m = re.match(r"^(.+)-(\d+)$", case)
        if m:
            project = m.group(1)
            bug_id = int(m.group(2))
        else:
            project = case
            bug_id = None

        try:
            d = json.loads(status_file.read_text())
        except Exception as e:
            rows.append({
                "method": method,
                "case": case,
                "project": project,
                "bug_id": bug_id,
                "status": "BAD_JSON",
                "notes": str(e),
            })
            continue

        fixed_cov = d.get("fixed_coverage") or {}
        buggy_cov = d.get("buggy_coverage") or {}

        token_totals = (d.get("tokens") or {}).get("totals") or {}

        detected_tests = d.get("detected_tests") or []

        row = {
            "method": method,
            "case": case,
            "project": project,
            "bug_id": bug_id,
            "status": d.get("status"),
            "valid_on_fixed": d.get("valid_on_fixed"),
            "fault_detected": d.get("fault_detected"),
            "detected_test_count": len(detected_tests),

            "fixed_lines_total": fixed_cov.get("LinesTotal"),
            "fixed_lines_covered": fixed_cov.get("LinesCovered"),
            "fixed_line_coverage_pct": pct(
                fixed_cov.get("LinesCovered"),
                fixed_cov.get("LinesTotal"),
            ),

            "fixed_conditions_total": fixed_cov.get("ConditionsTotal"),
            "fixed_conditions_covered": fixed_cov.get("ConditionsCovered"),
            "fixed_condition_coverage_pct": pct(
                fixed_cov.get("ConditionsCovered"),
                fixed_cov.get("ConditionsTotal"),
            ),

            "buggy_lines_total": buggy_cov.get("LinesTotal"),
            "buggy_lines_covered": buggy_cov.get("LinesCovered"),
            "buggy_line_coverage_pct": pct(
                buggy_cov.get("LinesCovered"),
                buggy_cov.get("LinesTotal"),
            ),

            "buggy_conditions_total": buggy_cov.get("ConditionsTotal"),
            "buggy_conditions_covered": buggy_cov.get("ConditionsCovered"),
            "buggy_condition_coverage_pct": pct(
                buggy_cov.get("ConditionsCovered"),
                buggy_cov.get("ConditionsTotal"),
            ),

            "prompt_tokens": token_totals.get("prompt_tokens"),
            "completion_tokens": token_totals.get("completion_tokens"),
            "total_tokens": token_totals.get("total_tokens"),

            "token_target": d.get("token_target"),
            "over_token_target": d.get("over_token_target"),
            "worker": d.get("worker"),
            "run_id": d.get("run_id"),
            "notes": "",
        }

        rows.append(row)


fieldnames = [
    "method",
    "case",
    "project",
    "bug_id",
    "status",
    "valid_on_fixed",
    "fault_detected",
    "detected_test_count",

    "fixed_lines_total",
    "fixed_lines_covered",
    "fixed_line_coverage_pct",

    "fixed_conditions_total",
    "fixed_conditions_covered",
    "fixed_condition_coverage_pct",

    "buggy_lines_total",
    "buggy_lines_covered",
    "buggy_line_coverage_pct",

    "buggy_conditions_total",
    "buggy_conditions_covered",
    "buggy_condition_coverage_pct",

    "prompt_tokens",
    "completion_tokens",
    "total_tokens",

    "token_target",
    "over_token_target",
    "worker",
    "run_id",
    "notes",
]

with OUT_CSV.open("w", newline="", encoding="utf-8") as f:
    writer = csv.DictWriter(f, fieldnames=fieldnames)
    writer.writeheader()
    writer.writerows(rows)


summary = {}

for method in ["ChatGPT", "GitHub Copilot"]:
    rr = [r for r in rows if r["method"] == method]

    status_counts = Counter(r.get("status") for r in rr)

    measured = [
        r for r in rr
        if r.get("fault_detected") is not None
    ]

    detected = [
        r for r in measured
        if r.get("fault_detected") is True
    ]

    coverage_rows = [
        r for r in rr
        if isinstance(r.get("fixed_line_coverage_pct"), (int, float))
    ]

    condition_rows = [
        r for r in rr
        if isinstance(r.get("fixed_condition_coverage_pct"), (int, float))
    ]

    token_rows = [
        r for r in rr
        if isinstance(r.get("total_tokens"), (int, float))
    ]

    summary[method] = {
        "total_cases": len(rr),
        "status_counts": dict(status_counts),

        "fault_detection_measured_cases": len(measured),
        "fault_detected_cases": len(detected),
        "fault_detection_rate_pct": (
            len(detected) / len(measured) * 100
            if measured else None
        ),

        "coverage_measured_cases": len(coverage_rows),
        "average_fixed_line_coverage_pct": (
            sum(r["fixed_line_coverage_pct"] for r in coverage_rows)
            / len(coverage_rows)
            if coverage_rows else None
        ),

        "condition_coverage_measured_cases": len(condition_rows),
        "average_fixed_condition_coverage_pct": (
            sum(r["fixed_condition_coverage_pct"] for r in condition_rows)
            / len(condition_rows)
            if condition_rows else None
        ),

        "token_measured_cases": len(token_rows),
        "total_tokens": (
            sum(r["total_tokens"] for r in token_rows)
            if token_rows else None
        ),
        "average_tokens_per_case": (
            sum(r["total_tokens"] for r in token_rows) / len(token_rows)
            if token_rows else None
        ),
    }


with OUT_JSON.open("w", encoding="utf-8") as f:
    json.dump(summary, f, indent=2, ensure_ascii=False)


def fmt(v, digits=2):
    if v is None:
        return "-"
    if isinstance(v, float):
        return f"{v:.{digits}f}"
    return str(v)


lines = []

lines.append("# Final AI Experiment Summary")
lines.append("")
lines.append("ผลสรุปจาก source-of-truth:")
lines.append("")
lines.append("- ChatGPT: `run-final-opt`")
lines.append("- GitHub Copilot: `run-copilot-final-v2`")
lines.append("")

lines.append("## Final status")
lines.append("")
lines.append("| Method | Total | DONE | Invalid after repair | Invalid after P04 | Output incomplete |")
lines.append("|---|---:|---:|---:|---:|---:|")

for method, s in summary.items():
    sc = s["status_counts"]
    lines.append(
        f"| {method} "
        f"| {s['total_cases']} "
        f"| {sc.get('DONE', 0)} "
        f"| {sc.get('INVALID_AFTER_REPAIR', 0)} "
        f"| {sc.get('INVALID_AFTER_PROMPT04', 0)} "
        f"| {sc.get('OUTPUT_INCOMPLETE', 0)} |"
    )

lines.append("")
lines.append("## Fault detection")
lines.append("")
lines.append("| Method | Measured cases | Detected cases | Fault detection rate |")
lines.append("|---|---:|---:|---:|")

for method, s in summary.items():
    rate = s["fault_detection_rate_pct"]
    rate_s = f"{rate:.2f}%" if rate is not None else "-"
    lines.append(
        f"| {method} "
        f"| {s['fault_detection_measured_cases']} "
        f"| {s['fault_detected_cases']} "
        f"| {rate_s} |"
    )

lines.append("")
lines.append("## Fixed-version coverage")
lines.append("")
lines.append("| Method | Line coverage cases | Avg line coverage | Condition coverage cases | Avg condition coverage |")
lines.append("|---|---:|---:|---:|---:|")

for method, s in summary.items():
    line_cov = s["average_fixed_line_coverage_pct"]
    cond_cov = s["average_fixed_condition_coverage_pct"]

    lines.append(
        f"| {method} "
        f"| {s['coverage_measured_cases']} "
        f"| {fmt(line_cov)}% "
        f"| {s['condition_coverage_measured_cases']} "
        f"| {fmt(cond_cov)}% |"
    )

lines.append("")
lines.append("## Token usage")
lines.append("")
lines.append("| Method | Cases with token data | Total tokens | Average tokens/case |")
lines.append("|---|---:|---:|---:|")

for method, s in summary.items():
    lines.append(
        f"| {method} "
        f"| {s['token_measured_cases']} "
        f"| {fmt(s['total_tokens'], 0)} "
        f"| {fmt(s['average_tokens_per_case'])} |"
    )

lines.append("")
lines.append("หมายเหตุ: coverage และ fault detection สรุปเฉพาะกรณีที่มีค่าที่วัดได้จริง ไม่แทนค่าที่หายไปด้วย 0")

OUT_MD.write_text("\n".join(lines) + "\n", encoding="utf-8")

print("Generated:")
print(" ", OUT_CSV)
print(" ", OUT_JSON)
print(" ", OUT_MD)
print()
print("Rows:", len(rows))
print("Expected:", 854 * 2)
