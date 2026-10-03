#!/usr/bin/env python3

import json
import re
import subprocess
from pathlib import Path

RESULT = Path("AI2_GitHubCopilot/Result")
CURRENT_RUN = "run-copilot-final-v2"
TOTAL = 854

FINISHED = {
    "DONE",
    "INVALID_AFTER_REPAIR",
    "INVALID_AFTER_PROMPT04",
    "OUTPUT_INCOMPLETE",
}


def read_status(path):
    try:
        return json.loads(path.read_text()).get("status")
    except (OSError, ValueError):
        return None


def main():

    # Account Pool
    subprocess.run(
        [
            "python3",
            "Experiment/automation/pool_status_short.py",
            "C",
        ],
        check=False,
    )

    # Read existing worker information.
    result = subprocess.run(
        [
            "python3",
            "Experiment/automation/monitor_c_short.py",
        ],
        capture_output=True,
        text=True,
        timeout=60,
    )

    if result.returncode:
        print(result.stdout)
        print(result.stderr)
        return

    worker_rows = []
    active = set()

    for line in result.stdout.splitlines():

        match = re.match(
            r"^\s*(C\d+)\s+"
            r"(RUNNING|QUOTA|STOPPED|IDLE|ERROR)\s+"
            r"(\S+)",
            line,
        )

        if not match:
            continue

        worker_rows.append(line)

        if match.group(2) == "RUNNING":
            case = match.group(3)

            if re.fullmatch(
                r"[A-Za-z][A-Za-z0-9]*-\d+",
                case,
            ):
                active.add(case)

    print()
    print("=" * 82)
    print("COPILOT / DEEPSEEK WORKERS — CURRENT")
    print("=" * 82)

    print(
        f"{'WORKER':<7}"
        f"{'STATE':<10}"
        f"{'CASE':<20}"
        f"{'STAGE':<7}"
        f"{'RAM':<7}"
        f"{'USED':>9}"
        f"{'LEFT~':>10}"
    )

    print("-" * 82)

    for row in worker_rows:
        print(row)

    # Count current and previous results.
    current = {}
    old_finished = set()

    if RESULT.exists():

        for path in RESULT.glob("*/run-*/case_status.json"):

            case = path.parent.parent.name
            status = read_status(path)

            if path.parent.name == CURRENT_RUN:
                current[case] = status

            elif status in FINISHED:
                old_finished.add(case)

    current_finished = {
        case
        for case, status in current.items()
        if status in FINISHED
    }

    combined = current_finished | old_finished
    old_only = old_finished - current_finished

    running = active - combined

    errors = {
        case
        for case, status in current.items()
        if status == "ERROR"
    } - combined - running

    quota = {
        case
        for case, status in current.items()
        if status == "QUOTA_PAUSED"
    } - combined - running

    waiting = max(
        0,
        TOTAL
        - len(combined)
        - len(running)
        - len(errors)
        - len(quota)
    )

    print()
    print("=" * 82)
    print("QUEUE - CURRENT + PREVIOUS (DISTINCT CASES)")
    print("=" * 82)

    print(f"Finished : {len(combined)}")
    print(f"  Current : {len(current_finished)}")
    print(f"  Old only: {len(old_only)}")
    print(f"Running  : {len(running)}")
    print(f"Error    : {len(errors)}")
    print(f"Quota    : {len(quota)}")
    print(f"Waiting  : {waiting}")

    print(
        "Old results are counted once; "
        "prompt versions may differ."
    )


if __name__ == "__main__":
    main()
