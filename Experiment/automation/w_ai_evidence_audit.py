#!/usr/bin/env python3

import json
import csv
import subprocess
from pathlib import Path
from collections import Counter

ROOT = Path("AI1_ChatGPT/Result")
OUT = Path("Experiment/runtime/w-ai-evidence")
OUT.mkdir(parents=True, exist_ok=True)

PROJECTS = [
    "JacksonXml", "Csv", "Codec", "Gson",
    "JxPath", "Chart", "JacksonCore", "Time",
    "Collections", "Mockito", "Cli", "Compress",
    "Lang", "Jsoup", "Math", "Closure",
    "JacksonDatabind"
]

FINISHED = {
    "DONE",
    "INVALID_AFTER_REPAIR",
    "INVALID_AFTER_PROMPT04",
    "OUTPUT_INCOMPLETE"
}

def get_ids(project):
    p = subprocess.run(
        ["defects4j", "bids", "-p", project],
        capture_output=True,
        text=True,
        check=True,
        timeout=45
    )

    return sorted({
        int(x)
        for x in p.stdout.splitlines()
        if x.strip().isdigit()
    })

def read_status(path):
    try:
        return json.loads(path.read_text()).get("status")
    except (OSError, ValueError):
        return None

def has_ai_evidence(run):
    for stage in ("01", "02", "03", "04"):

        raw = run / f"{stage}_raw_response.md"

        if raw.is_file() and raw.stat().st_size > 0:
            return True

        response = run / f"{stage}_response.json"

        if response.is_file():
            try:
                data = json.loads(response.read_text())
                if data.get("choices") or data.get("usage"):
                    return True
            except (OSError, ValueError):
                pass

        meta = run / f"{stage}_metadata.json"

        if meta.is_file():
            try:
                data = json.loads(meta.read_text())

                tokens = data.get("total_tokens")

                if isinstance(tokens, (int, float)) and tokens > 0:
                    return True
            except (OSError, ValueError):
                pass

    return False

rows = []
stats = {}

for project in PROJECTS:

    counts = Counter()
    ids = get_ids(project)

    for bug in ids:

        case = f"{project}-{bug}"
        case_dir = ROOT / case

        runs = (
            list(case_dir.glob("run-*"))
            if case_dir.exists()
            else []
        )

        completed = False
        ai_evidence = False
        current_completed = False
        previous_completed = False
        evidence_runs = []

        for run in runs:

            if not run.is_dir():
                continue

            status = read_status(
                run / "case_status.json"
            )

            if status in FINISHED:
                completed = True

                if run.name == "run-final-opt":
                    current_completed = True
                else:
                    previous_completed = True

            if has_ai_evidence(run):
                ai_evidence = True
                evidence_runs.append(run.name)

        if completed:
            category = "COMPLETED"
        elif ai_evidence:
            category = "AI_PARTIAL"
        elif any(
            p.is_file()
            for run in runs if run.is_dir()
            for p in run.rglob("*")
        ):
            category = "PREPARED_ONLY"
        else:
            category = "NO_EVIDENCE"

        counts[category] += 1

        if current_completed:
            counts["CURRENT_COMPLETED"] += 1

        if previous_completed and not current_completed:
            counts["PREVIOUS_ONLY"] += 1

        rows.append({
            "project": project,
            "case": case,
            "category": category,
            "current_completed": current_completed,
            "previous_completed": previous_completed,
            "ai_evidence": ai_evidence,
            "ai_evidence_runs": ",".join(evidence_runs)
        })

    stats[project] = counts

print()
print("W - VERIFIED EXPERIMENT EVIDENCE")
print("=" * 105)

print(
    f"{'PROJECT':<19}"
    f"{'COMPLETED':>12}"
    f"{'AI PARTIAL':>12}"
    f"{'PREPARED':>11}"
    f"{'NEW':>8}"
    f"{'OLD ONLY':>11}"
)

print("-" * 105)

total = Counter()

for project in PROJECTS:

    s = stats[project]
    total.update(s)

    print(
        f"{project:<19}"
        f"{s['COMPLETED']:>12}"
        f"{s['AI_PARTIAL']:>12}"
        f"{s['PREPARED_ONLY']:>11}"
        f"{s['NO_EVIDENCE']:>8}"
        f"{s['PREVIOUS_ONLY']:>11}"
    )

print("-" * 105)

print(
    f"{'TOTAL':<19}"
    f"{total['COMPLETED']:>12}"
    f"{total['AI_PARTIAL']:>12}"
    f"{total['PREPARED_ONLY']:>11}"
    f"{total['NO_EVIDENCE']:>8}"
    f"{total['PREVIOUS_ONLY']:>11}"
)

print()
print("TOTAL CASES:", len(rows))
print("CURRENT COMPLETED:", total["CURRENT_COMPLETED"])

with (OUT / "details.csv").open(
    "w", newline=""
) as f:

    writer = csv.DictWriter(
        f,
        fieldnames=list(rows[0])
    )

    writer.writeheader()
    writer.writerows(rows)

for category in (
    "COMPLETED",
    "AI_PARTIAL",
    "PREPARED_ONLY",
    "NO_EVIDENCE"
):

    cases = [
        row["case"]
        for row in rows
        if row["category"] == category
    ]

    (OUT / f"{category.lower()}.txt").write_text(
        "\n".join(cases) +
        ("\n" if cases else "")
    )

print()
print("DETAILS:", OUT / "details.csv")
print("READ-ONLY AUDIT")
