#!/usr/bin/env python3

import json
import re
import subprocess
from pathlib import Path

ROOT = Path(".")
LOG_DIR = ROOT / "Experiment/logs/final-opt"

def human_tokens(n):
    if n is None:
        return "-"
    try:
        n = int(n)
    except Exception:
        return "-"
    if abs(n) >= 1_000_000:
        return f"{n/1_000_000:.2f}M"
    if abs(n) >= 1000:
        return f"{n/1000:.1f}k"
    return str(n)

def active_workers():
    try:
        out = subprocess.check_output(
            ["ps", "-eo", "pid,rss,args"],
            text=True,
            errors="replace"
        )
    except Exception:
        return []

    rows = []

    for line in out.splitlines():
        if "Experiment/automation/ai_runner.py" not in line:
            continue
        if "--provider chatgpt" not in line:
            continue

        m = re.search(r"^\s*(\d+)\s+(\d+)\s+.*--worker\s+(W\d+)(?:\s|$)", line)
        if not m:
            continue

        pid = int(m.group(1))
        rss_kb = int(m.group(2))
        worker = m.group(3)

        rows.append((worker, pid, rss_kb))

    return sorted(
        rows,
        key=lambda x: int(x[0][1:])
    )

def parse_log(worker):
    log = LOG_DIR / f"{worker}.log"

    case = "-"
    stage = "-"
    used = None
    left = None

    if not log.exists():
        return case, stage, used, left

    try:
        lines = log.read_text(errors="replace").splitlines()
    except Exception:
        return case, stage, used, left

    # อ่านท้ายไฟล์พอ ไม่ต้อง scan log ใหญ่ทั้งไฟล์
    tail = lines[-150:]

    # case ล่าสุด
    for line in reversed(tail):
        m = re.search(r"\[(?:W\d+)\]\s+CLAIMED\s+([A-Za-z0-9_-]+)", line)
        if m:
            case = m.group(1)
            break

        m = re.search(r"\[([A-Za-z]+-\d+)\]", line)
        if m:
            case = m.group(1)
            break

    # stage ล่าสุด
    for line in reversed(tail):
        if re.search(r"Prompt 04|P04", line):
            stage = "P04"
            break
        if re.search(r"Prompt 03|P03", line):
            stage = "P03"
            break
        if re.search(r"Prompt 02|P02", line):
            stage = "P02"
            break
        if re.search(r"Prompt 01|P01", line):
            stage = "P01"
            break
        if "acquired Defects4J slot" in line:
            stage = "D4J"
            break

    # quota/token JSON ล่าสุด
    for line in reversed(tail):
        if '"daily_usage_tokens"' not in line:
            continue
        try:
            data = json.loads(line)
            used = data.get("daily_usage_tokens")
            left = data.get("daily_remaining_tokens")
            break
        except Exception:
            pass

    return case, stage, used, left


workers = active_workers()

print("=" * 79)
print("CHATGPT / TERRA — ACTIVE WORKERS")
print("=" * 79)

if not workers:
    print("NO W WORKERS RUNNING")
else:
    print(
        f"{'WORKER':<7}"
        f"{'STATE':<10}"
        f"{'CASE':<22}"
        f"{'STAGE':<7}"
        f"{'RAM':>7}"
        f"{'USED':>11}"
        f"{'LEFT':>11}"
    )
    print("-" * 79)

    for worker, pid, rss_kb in workers:
        case, stage, used, left = parse_log(worker)

        ram_mb = max(1, round(rss_kb / 1024))

        print(
            f"{worker:<7}"
            f"{'RUNNING':<10}"
            f"{case:<22}"
            f"{stage:<7}"
            f"{str(ram_mb)+'M':>7}"
            f"{human_tokens(used):>11}"
            f"{human_tokens(left):>11}"
        )

print()
print("=" * 79)
print(f"ACTIVE W: {len(workers)}")
print("=" * 79)

# switcher สั้น ๆ
switch_log = ROOT / "Experiment/logs/w-quota-switcher.log"
if switch_log.exists():
    lines = switch_log.read_text(errors="replace").splitlines()
    if lines:
        print("SWITCHER:", lines[-1])
