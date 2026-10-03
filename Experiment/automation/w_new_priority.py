#!/usr/bin/env python3

import json
import subprocess
from pathlib import Path
from collections import Counter

ROOT = Path("AI1_ChatGPT/Result")
OUTPUT = Path("Experiment/runtime/w-priority")
OUTPUT.mkdir(parents=True, exist_ok=True)

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

def bug_ids(project):
    p = subprocess.run(
        ["defects4j", "bids", "-p", project],
        capture_output=True,
        text=True,
        timeout=45,
        check=True,
    )

    return sorted({
        int(line.strip())
        for line in p.stdout.splitlines()
        if line.strip().isdigit()
    })


def inspect_case(project, bug):
    case = f"{project}-{bug}"
    directory = ROOT / case

    if not directory.exists():
        return "NEW"

    runs = [
        p for p in directory.iterdir()
        if p.is_dir() and p.name.startswith("run-")
    ]

    if not runs:
        # Preserve cases with other existing artifacts.
        if any(directory.iterdir()):
            return "PREVIOUS"
        return "NEW"

    current_finished = False
    attempted = False

    for run in runs:
        status_file = run / "case_status.json"

        if status_file.exists():
            attempted = True

            try:
                data = json.loads(status_file.read_text())
                status = data.get("status")
            except Exception:
                status = None

            if (
                run.name == "run-final-opt"
                and status in FINISHED
            ):
                current_finished = True

        # Other artifacts may remain after ERROR recovery.
        if any(run.iterdir()):
            attempted = True

    if current_finished:
        return "CURRENT"

    if attempted:
        return "PREVIOUS"

    return "NEW"


all_new = []
all_previous = []
all_current = []

print()
print("W - PRIORITY AUDIT")
print("=" * 69)

print(
    f"{'PROJECT':<20}"
    f"{'NEW':>9}"
    f"{'PREVIOUS':>12}"
    f"{'CURRENT':>11}"
    f"{'TOTAL':>9}"
)

print("-" * 69)

errors = []

for project in PROJECTS:
    try:
        ids = bug_ids(project)
    except Exception as e:
        errors.append(f"{project}: {e}")
        print(f"{project:<20} BUG IDS UNAVAILABLE")
        continue

    counts = Counter()

    for bug in ids:
        case = f"{project}-{bug}"
        category = inspect_case(project, bug)

        counts[category] += 1

        if category == "NEW":
            all_new.append(case)
        elif category == "PREVIOUS":
            all_previous.append(case)
        else:
            all_current.append(case)

    print(
        f"{project:<20}"
        f"{counts['NEW']:>9}"
        f"{counts['PREVIOUS']:>12}"
        f"{counts['CURRENT']:>11}"
        f"{len(ids):>9}"
    )

print("-" * 69)

total = len(all_new) + len(all_previous) + len(all_current)

print(
    f"{'TOTAL':<20}"
    f"{len(all_new):>9}"
    f"{len(all_previous):>12}"
    f"{len(all_current):>11}"
    f"{total:>9}"
)

if errors:
    print("\nERROR: Some projects could not be audited.")
    for error in errors:
        print(error)
    print("No queue files written.")
    raise SystemExit(1)

(OUTPUT / "new_cases.txt").write_text(
    "\n".join(all_new) + ("\n" if all_new else "")
)

(OUTPUT / "previous_cases.txt").write_text(
    "\n".join(all_previous) + ("\n" if all_previous else "")
)

(OUTPUT / "current_finished.txt").write_text(
    "\n".join(all_current) + ("\n" if all_current else "")
)

print()
print("NEW CASES:", len(all_new))
print("PREVIOUS:", len(all_previous))
print("CURRENT FINISHED:", len(all_current))

print()
print("FILES:")
print(OUTPUT / "new_cases.txt")
print(OUTPUT / "previous_cases.txt")
print(OUTPUT / "current_finished.txt")

print()
print("READ-ONLY AUDIT: Running workers were not modified.")
print("NEW means no attempt evidence found in the scanned result directory.")
