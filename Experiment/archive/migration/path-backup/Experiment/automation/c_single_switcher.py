#!/usr/bin/env python3

import os
import re
import signal
import subprocess
import time
from pathlib import Path

ROOT = Path("/mnt/c/Users/User/Desktop/SQAProjectGroup11-git")
KEYDIR = Path.home() / ".sqa-keys"
LOGDIR = ROOT / "Experiment/logs/copilot-final-v2"

CHECK_EVERY = 60
D4J_SLOTS = 4
D4J_TIMEOUT = 900


def active_c_workers():
    out = subprocess.run(
        ["ps", "-eo", "pid,args"],
        capture_output=True,
        text=True
    ).stdout

    found = {}

    for line in out.splitlines():
        m = re.search(
            r"^\s*(\d+)\s+.*ai2_runner\.py.*--worker\s+(C\d+)(?:\s|$)",
            line
        )
        if m:
            found[m.group(2)] = int(m.group(1))

    return found


def quota_paused(worker):
    log = LOGDIR / f"{worker}.log"

    if not log.exists():
        return False

    lines = log.read_text(errors="ignore").splitlines()[-150:]

    # หา event ล่าสุดระหว่าง "งานเดินต่อ" กับ "quota"
    for line in reversed(lines):
        if re.search(
            r"QUOTA PAUSED|daily quota reached|daily limit|HTTP 401",
            line,
            re.I
        ):
            return True

        if re.search(
            r"CLAIMED|acquired Defects4J|Prompt 0[1-4]|->\s+"
            r"(DONE|ERROR|INVALID|OUTPUT)",
            line,
            re.I
        ):
            return False

    return False


def stop_worker(worker, pid):
    print(f"[switcher] stopping quota worker {worker} pid={pid}", flush=True)

    try:
        os.kill(pid, signal.SIGTERM)
    except ProcessLookupError:
        return

    for _ in range(20):
        if not Path(f"/proc/{pid}").exists():
            return
        time.sleep(0.5)

    try:
        os.kill(pid, signal.SIGKILL)
    except ProcessLookupError:
        pass


def get_real_quota():
    p = subprocess.run(
        ["python3", "Experiment/automation/c_real_quota.py"],
        cwd=ROOT,
        capture_output=True,
        text=True,
        timeout=180
    )

    candidates = []

    # ตัวอย่าง:
    # C3    81,768   918,232   1,000,000
    for line in p.stdout.splitlines():
        m = re.match(
            r"^\s*(C\d+)\s+([\d,]+)\s+([\d,]+)\s+([\d,]+)\s*$",
            line
        )

        if not m:
            continue

        worker = m.group(1)
        left = int(m.group(3).replace(",", ""))

        if left > 0:
            candidates.append((left, worker))

    # เลือกตัวเหลือ token มากสุด
    candidates.sort(reverse=True)

    return candidates


def start_worker(worker):
    keyfile = KEYDIR / f"{worker}.key"

    if not keyfile.exists() or keyfile.stat().st_size == 0:
        print(f"[switcher] {worker}: missing key", flush=True)
        return False

    key = keyfile.read_text(errors="ignore").strip()

    env = os.environ.copy()
    env["D4J_SLOTS"] = str(D4J_SLOTS)
    env["D4J_TIMEOUT_SEC"] = str(D4J_TIMEOUT)

    print(f"[switcher] starting {worker}", flush=True)

    p = subprocess.run(
        [
            "bash",
            "Experiment/automation/start_copilot_worker_v3.sh",
            worker
        ],
        cwd=ROOT,
        input=key + "\n",
        text=True,
        env=env
    )

    return p.returncode == 0


def main():
    print("[switcher] C SINGLE SWITCHER START", flush=True)

    while True:
        try:
            active = active_c_workers()

            # ถ้ามีมากกว่า 1 จะไม่ยุ่งกับตัวอื่น
            # แต่ switcher จะไม่เปิดเพิ่ม
            if active:
                for worker, pid in list(active.items()):
                    if quota_paused(worker):
                        stop_worker(worker, pid)

                active = active_c_workers()

            if not active:
                quota = get_real_quota()

                if not quota:
                    print("[switcher] no C account with quota; retry in 5 min", flush=True)
                    time.sleep(300)
                    continue

                left, worker = quota[0]

                print(
                    f"[switcher] selected {worker}, left={left:,}",
                    flush=True
                )

                start_worker(worker)

            time.sleep(CHECK_EVERY)

        except KeyboardInterrupt:
            break

        except Exception as e:
            print(f"[switcher] ERROR: {e}", flush=True)
            time.sleep(60)


if __name__ == "__main__":
    main()
