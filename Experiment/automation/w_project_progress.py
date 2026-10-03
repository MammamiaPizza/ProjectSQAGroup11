#!/usr/bin/env python3

import json
import subprocess
from pathlib import Path
from collections import Counter

ROOT = Path("AI1_ChatGPT/Result")
RUN = "run-final-opt"

PROJECTS = [
    "JacksonXml", "Csv", "Codec", "Gson", "JxPath",
    "Chart", "JacksonCore", "Time", "Collections",
    "Mockito", "Cli", "Compress", "Lang",
    "Jsoup", "Math", "Closure", "JacksonDatabind"
]

FINISHED = {
    "DONE",
    "INVALID_AFTER_REPAIR",
    "INVALID_AFTER_PROMPT04",
    "OUTPUT_INCOMPLETE",
}

def get_bug_ids(project):
    try:
        p = subprocess.run(
            ["defects4j", "bids", "-p", project],
            capture_output=True,
            text=True,
            timeout=30,
            check=True,
        )
        return {
            int(line.strip())
            for line in p.stdout.splitlines()
            if line.strip().isdigit()
        }
    except Exception:
        return None

print()
print("W / CHATGPT - PROJECT PROGRESS")
print("=" * 82)
print(
    f"{'PROJECT':<19}"
    f"{'FINISHED':>11}"
    f"{'RUNNING/WAIT':>15}"
    f"{'ERROR':>9}"
    f"{'QUOTA':>9}"
    f"{'PROGRESS':>14}"
)
print("-" * 82)

totals = Counter()
unknown_projects = []

for project in PROJECTS:
    ids = get_bug_ids(project)

    if ids is None:
        unknown_projects.append(project)
        continue

    counts = Counter()

    for bug in ids:
        status_file = ROOT / f"{project}-{bug}" / RUN / "case_status.json"

        if not status_file.exists():
            counts["WAIT"] += 1
            continue

        try:
            data = json.loads(status_file.read_text())
            status = data.get("status", "UNKNOWN")
        except Exception:
            status = "UNKNOWN"

        if status in FINISHED:
            counts["FINISHED"] += 1
        elif status == "ERROR":
            counts["ERROR"] += 1
        elif status == "QUOTA_PAUSED":
            counts["QUOTA"] += 1
        else:
            counts["WAIT"] += 1

    finished = counts["FINISHED"]
    total = len(ids)
    pending = total - finished - counts["ERROR"] - counts["QUOTA"]
    percent = finished / total * 100 if total else 0

    print(
        f"{project:<19}"
        f"{finished:>5}/{total:<6}"
        f"{pending:>15}"
        f"{counts['ERROR']:>9}"
        f"{counts['QUOTA']:>9}"
        f"{percent:>13.1f}%"
    )

    totals["finished"] += finished
    totals["total"] += total
    totals["pending"] += pending
    totals["error"] += counts["ERROR"]
    totals["quota"] += counts["QUOTA"]

print("-" * 82)

if totals["total"]:
    percent = totals["finished"] / totals["total"] * 100

    print(
        f"{'TOTAL':<19}"
        f"{totals['finished']:>5}/{totals['total']:<6}"
        f"{totals['pending']:>15}"
        f"{totals['error']:>9}"
        f"{totals['quota']:>9}"
        f"{percent:>13.1f}%"
    )

if unknown_projects:
    print()
    print("WARNING: Could not retrieve bug IDs for:")
    print(", ".join(unknown_projects))
    print("Totals above exclude these projects.")

print()
print("FINISHED includes DONE and measured terminal outcomes.")
print("RUNNING/WAIT includes unfinished and unrecorded cases.")
print("Source: Defects4J bug IDs + W run-final-opt case_status.json")
