#!/usr/bin/env python3

import csv
import json
import re
import subprocess
from pathlib import Path
from collections import defaultdict, Counter
from datetime import datetime

PROJECT_ROOT = Path.cwd()

RESULT = PROJECT_ROOT / "AI1_ChatGPT/Result"

RECOVERY = Path("/mnt/e/SQA-Recovery")

OUTPUT = PROJECT_ROOT / "Experiment/runtime/w-history-audit"
OUTPUT.mkdir(parents=True, exist_ok=True)

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
    "OUTPUT_INCOMPLETE",
}

CASE_RE = re.compile(
    r"^(?:" +
    "|".join(re.escape(x) for x in PROJECTS) +
    r")-\d+$"
)

# Evidence indexed by distinct case.
history = defaultdict(list)

# Files inside result directories.
artifact_files = defaultdict(list)

# Evidence from backup archives.
backup_files = defaultdict(list)


def case_from_path(path):
    for part in path.parts:
        if CASE_RE.fullmatch(part):
            return part
    return None


def read_status(path):
    try:
        data = json.loads(path.read_text(errors="replace"))
        return data.get("status", "UNKNOWN")
    except Exception:
        return "UNREADABLE"


def read_bug_ids(project):
    result = subprocess.run(
        ["defects4j", "bids", "-p", project],
        capture_output=True,
        text=True,
        timeout=45,
        check=True
    )

    return sorted({
        int(line.strip())
        for line in result.stdout.splitlines()
        if line.strip().isdigit()
    })


print("Scanning W result files...", flush=True)

# --------------------------------------------------
# 1. All current and historical W result directories
# --------------------------------------------------

if RESULT.exists():

    for case_dir in RESULT.iterdir():

        if not case_dir.is_dir():
            continue

        case = case_dir.name

        if not CASE_RE.fullmatch(case):
            continue

        for run_dir in case_dir.iterdir():

            if not run_dir.is_dir():
                continue

            if not run_dir.name.startswith("run-"):
                continue

            status_path = run_dir / "case_status.json"

            if status_path.exists():

                history[case].append({
                    "source": "RESULT",
                    "run": run_dir.name,
                    "status": read_status(status_path),
                    "path": str(status_path),
                })

            # Record every existing artifact, not only status.
            for file in run_dir.rglob("*"):

                if not file.is_file():
                    continue

                if file.name == "case_status.json":
                    continue

                try:
                    if file.stat().st_size == 0:
                        continue
                except OSError:
                    continue

                artifact_files[case].append({
                    "run": run_dir.name,
                    "path": str(file),
                    "size": file.stat().st_size,
                })


# --------------------------------------------------
# 2. Archived case_status.json in recovery backups
# --------------------------------------------------

print("Scanning recovery backups...", flush=True)

if RECOVERY.exists():

    for path in RECOVERY.rglob("case_status.json"):

        # Avoid unrelated C archives using path indicators.
        parts_lower = [
            p.lower()
            for p in path.parts
        ]

        if (
            "copilot" in str(path).lower()
            or "ai2_githubcopilot" in parts_lower
        ):
            continue

        case = case_from_path(path)

        if not case:
            continue

        status = read_status(path)

        backup_files[case].append({
            "path": str(path),
            "status": status,
        })


# --------------------------------------------------
# 3. Build expected Defects4J case list
# --------------------------------------------------

print("Loading Defects4J case lists...", flush=True)

expected = {}
errors = []

for project in PROJECTS:

    try:
        ids = read_bug_ids(project)
        expected[project] = [
            f"{project}-{bug}"
            for bug in ids
        ]

    except Exception as exc:
        errors.append(
            f"{project}: {type(exc).__name__}: {exc}"
        )


if errors:
    print("\nCannot complete audit:")
    print("\n".join(errors))
    raise SystemExit(1)


# --------------------------------------------------
# 4. Classify every expected case
# --------------------------------------------------

rows = []
details = []

project_stats = defaultdict(Counter)

for project in PROJECTS:

    for case in expected[project]:

        runs = history.get(case, [])
        artifacts = artifact_files.get(case, [])
        backups = backup_files.get(case, [])

        measured = any(
            x["status"] in FINISHED
            for x in runs
        )

        current = any(
            x["run"] == "run-final-opt"
            and x["status"] in FINISHED
            for x in runs
        )

        # A recovered backup could establish an old
        # measured result even if original was moved.
        backup_measured = any(
            x["status"] in FINISHED
            for x in backups
        )

        measured = measured or backup_measured

        attempted = bool(runs or artifacts or backups)

        if measured:
            category = "MEASURED"
        elif attempted:
            category = "ATTEMPTED"
        else:
            category = "NO_EVIDENCE"

        stats = project_stats[project]

        stats["TOTAL"] += 1
        stats[category] += 1

        if current:
            stats["CURRENT"] += 1

        if backups:
            stats["BACKUP"] += 1

        rows.append({
            "project": project,
            "case": case,
            "category": category,
            "current_measured": current,
            "result_status_files": len(runs),
            "other_artifact_files": len(artifacts),
            "backup_status_files": len(backups),
        })

        details.append({
            "case": case,
            "category": category,
            "current_measured": current,
            "status_history": runs,
            "artifacts": artifacts,
            "backups": backups,
        })


# --------------------------------------------------
# 5. Print summary
# --------------------------------------------------

print()
print("=" * 94)
print("COMPLETE W HISTORY AUDIT")
print("=" * 94)

print(
    f"{'PROJECT':<20}"
    f"{'MEASURED':>12}"
    f"{'ATTEMPTED':>12}"
    f"{'NO EVIDENCE':>15}"
    f"{'CURRENT':>11}"
    f"{'BACKUP':>10}"
    f"{'TOTAL':>10}"
)

print("-" * 94)

totals = Counter()

for project in PROJECTS:

    s = project_stats[project]

    totals.update(s)

    print(
        f"{project:<20}"
        f"{s['MEASURED']:>12}"
        f"{s['ATTEMPTED']:>12}"
        f"{s['NO_EVIDENCE']:>15}"
        f"{s['CURRENT']:>11}"
        f"{s['BACKUP']:>10}"
        f"{s['TOTAL']:>10}"
    )

print("-" * 94)

print(
    f"{'TOTAL':<20}"
    f"{totals['MEASURED']:>12}"
    f"{totals['ATTEMPTED']:>12}"
    f"{totals['NO_EVIDENCE']:>15}"
    f"{totals['CURRENT']:>11}"
    f"{totals['BACKUP']:>10}"
    f"{totals['TOTAL']:>10}"
)

print()
print(
    "ANY EVIDENCE:",
    totals["MEASURED"] + totals["ATTEMPTED"]
)

print(
    "NO EVIDENCE:",
    totals["NO_EVIDENCE"]
)


# --------------------------------------------------
# 6. Export full history
# --------------------------------------------------

with (OUTPUT / "summary.csv").open(
    "w", newline=""
) as f:

    writer = csv.DictWriter(
        f,
        fieldnames=list(rows[0].keys())
    )

    writer.writeheader()
    writer.writerows(rows)


with (OUTPUT / "details.json").open("w") as f:

    json.dump(
        {
            "created_at": datetime.now().isoformat(),
            "details": details,
        },
        f,
        indent=2
    )


for category, filename in [
    ("MEASURED", "measured.txt"),
    ("ATTEMPTED", "attempted.txt"),
    ("NO_EVIDENCE", "no_evidence.txt"),
]:

    cases = [
        x["case"]
        for x in rows
        if x["category"] == category
    ]

    (OUTPUT / filename).write_text(
        "\n".join(cases) +
        ("\n" if cases else "")
    )


print()
print("Saved:")
print(OUTPUT / "summary.csv")
print(OUTPUT / "details.json")
print(OUTPUT / "measured.txt")
print(OUTPUT / "attempted.txt")
print(OUTPUT / "no_evidence.txt")

print()
print("READ-ONLY AUDIT OF EXPERIMENT DATA")
print("Existing result and backup files were not changed.")
