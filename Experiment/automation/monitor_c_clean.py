#!/usr/bin/env python3

import re
import subprocess

cmd = ["python3", "Experiment/automation/monitor_c_combined.py"]

try:
    result = subprocess.run(
        cmd,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        timeout=600,
    )
    text = result.stdout
except subprocess.TimeoutExpired as e:
    text = e.stdout or ""
    if isinstance(text, bytes):
        text = text.decode(errors="replace")
    print(text, end="")
    print("\nMONITOR TIMEOUT")
    raise SystemExit(1)

print(text, end="" if text.endswith("\n") else "\n")

def get(name):
    m = re.search(
        rf"^{re.escape(name)}\s*:\s*(\d+)",
        text,
        re.MULTILINE | re.IGNORECASE
    )
    return int(m.group(1)) if m else 0

finished = get("Finished")
running  = get("Running")
error    = get("Error")
quota    = get("Quota")
waiting  = get("Waiting")

remaining = running + error + quota + waiting
total = finished + remaining

print("-" * 34)
print(f"Remaining: {remaining}")
print(f"TOTAL    : {total} / 854")

if total != 854:
    print(f"WARNING  : count mismatch ({total - 854:+d})")
