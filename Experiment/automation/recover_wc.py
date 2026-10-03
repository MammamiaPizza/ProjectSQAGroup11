#!/usr/bin/env python3

import argparse
import hashlib
import json
import os
import shutil
import sys
from collections import Counter
from datetime import datetime
from pathlib import Path

ROOT = Path.cwd()
BACKUP_ROOT = Path("/mnt/e/SQA-Recovery/queue-recovery")

CONFIGS = {
    "W": {
        "results": ROOT / "AI1_ChatGPT/Result",
        "run": "run-final-opt",
        "claims": ROOT / "Experiment/runtime/run-final-opt/claims/chatgpt",
    },
    "C": {
        "results": ROOT / "AI2_GitHubCopilot/Result",
        "run": "run-copilot-final-v2",
        "claims": ROOT / "Experiment/runtime/run-copilot-final-v2/claims/copilot",
    },
}


def sha256(path):
    h = hashlib.sha256()
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()


def active_processes():
    targets = {
        "ai_runner.py",
        "ai2_runner.py",
        "w_quota_switcher.py",
        "pool_supervisor.py",
    }

    found = []

    for proc in Path("/proc").iterdir():
        if not proc.name.isdigit():
            continue

        try:
            args = (proc / "cmdline").read_bytes().split(b"\0")
            names = {
                Path(a.decode(errors="ignore")).name
                for a in args if a
            }

            if names & targets:
                found.append(proc.name)
        except (OSError, PermissionError):
            pass

    return found


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--apply", action="store_true")
    args = parser.parse_args()

    if args.apply:
        running = active_processes()

        if running:
            print("STOP: Worker or supervisor processes exist:", running)
            sys.exit(1)

        if not BACKUP_ROOT.parent.is_dir():
            print("STOP: E: recovery directory unavailable")
            sys.exit(1)

    stamp = datetime.now().strftime("%Y%m%d-%H%M%S")
    destination = BACKUP_ROOT / stamp

    total_candidates = 0

    for worker, cfg in CONFIGS.items():
        counts = Counter()
        candidates = []

        files = cfg["results"].glob(
            f"*/{cfg['run']}/case_status.json"
        )

        for path in files:
            try:
                data = json.loads(path.read_text())
            except Exception:
                counts["UNREADABLE"] += 1
                continue

            status = data.get("status", "UNKNOWN")
            counts[status] += 1

            tokens = (
                data.get("tokens", {})
                .get("totals", {})
                .get("total_tokens", 0)
            ) or 0

            if status == "ERROR" and tokens == 0:
                candidates.append(path)

        print(f"\n{'=' * 55}")
        print(worker)
        print("=" * 55)

        for status, count in counts.most_common():
            print(f"{status:<30} {count}")

        print("RECOVERY CANDIDATES:", len(candidates))

        total_candidates += len(candidates)

        if not args.apply:
            continue

        for src in candidates:
            rel = src.relative_to(ROOT)
            dst = destination / rel

            dst.parent.mkdir(parents=True, exist_ok=True)

            # Copy and verify before changing the original.
            shutil.copy2(src, dst)

            if sha256(src) != sha256(dst):
                print("STOP: Backup verification failed:", src)
                sys.exit(2)

            src.unlink()

        print("RECOVERED:", len(candidates))

    print("\nTOTAL CANDIDATES:", total_candidates)

    if args.apply:
        print("STATUS BACKUP:", destination)
        print("Recovery completed.")
    else:
        print("DRY RUN: No files modified.")
        print("Use --apply after reviewing the counts.")


if __name__ == "__main__":
    main()
