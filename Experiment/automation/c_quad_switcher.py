#!/usr/bin/env python3

import os
import re
import signal
import subprocess
import time
from pathlib import Path
from datetime import datetime

ROOT = Path("/root/SQAProjectGroup11-git")
AUTO = ROOT / "Experiment" / "automation"
LOG_ROOT = ROOT / "Experiment" / "logs" / "copilot-final-v2"

MAX_C = 4
CHECK_SEC = 30
MIN_LEFT = 25000

WORKERS = [f"C{i}" for i in range(1, 15)]
COOLDOWN = set()


def stamp():
    return datetime.now().strftime("%Y-%m-%d %H:%M:%S")


def log(msg):
    print(f"[{stamp()}] {msg}", flush=True)


def active_workers():
    p = subprocess.run(
        ["ps", "-eo", "pid,args"],
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.DEVNULL,
    )

    found = {}

    for line in p.stdout.splitlines():
        if "ai2_runner.py" not in line:
            continue

        m = re.search(r"--worker\s+(C\d+)", line)
        if not m:
            continue

        try:
            pid = int(line.strip().split(None, 1)[0])
        except Exception:
            continue

        found[m.group(1)] = pid

    return found


def real_quota():
    try:
        p = subprocess.run(
            ["python3", str(AUTO / "c_real_quota.py")],
            cwd=ROOT,
            text=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            timeout=180,
        )
    except subprocess.TimeoutExpired:
        log("WARNING: c_real_quota.py timed out")
        return {}

    result = {}

    for line in p.stdout.splitlines():
        m = re.match(
            r"^(C\d+)\s+([0-9,]+)\s+([0-9,]+)\s+([0-9,]+)\s*$",
            line.strip(),
        )

        if m:
            worker = m.group(1)
            result[worker] = {
                "used": int(m.group(2).replace(",", "")),
                "left": int(m.group(3).replace(",", "")),
                "ok": True,
            }
            continue

        m = re.match(r"^(C\d+)\s+HTTP\s+(\d+)", line.strip())
        if m:
            result[m.group(1)] = {
                "used": None,
                "left": 0,
                "ok": False,
                "http": int(m.group(2)),
            }

    return result


def waiting_count():
    try:
        p = subprocess.run(
            ["python3", str(AUTO / "copilot_queue_status.py")],
            cwd=ROOT,
            text=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            timeout=120,
        )
    except subprocess.TimeoutExpired:
        log("WARNING: queue status timed out")
        return None

    for pattern in (
        r"Waiting\s*:\s*(\d+)",
        r"WAITING\s*[:=]\s*(\d+)",
    ):
        m = re.search(pattern, p.stdout, re.I)
        if m:
            return int(m.group(1))

    return None


def worker_log(worker):
    return LOG_ROOT / f"{worker}.log"


def quota_paused(worker):
    path = worker_log(worker)

    if not path.is_file():
        return False

    try:
        with path.open("rb") as f:
            f.seek(0, os.SEEK_END)
            size = f.tell()
            f.seek(max(0, size - 12000))
            text = f.read().decode("utf-8", errors="replace")
    except Exception:
        return False

    # Only inspect the recent tail.
    return bool(
        re.search(
            r"QUOTA\s+PAUSED|daily quota reached|quota exhausted",
            text,
            re.I,
        )
    )


def stop_worker(worker, pid):
    log(f"STOP {worker} pid={pid} (quota paused)")

    try:
        os.kill(pid, signal.SIGTERM)
    except ProcessLookupError:
        return

    for _ in range(10):
        time.sleep(1)
        try:
            os.kill(pid, 0)
        except ProcessLookupError:
            return

    log(f"{worker} did not exit after SIGTERM; sending SIGKILL")
    try:
        os.kill(pid, signal.SIGKILL)
    except ProcessLookupError:
        pass


def start_worker(worker):
    key_path = Path.home() / ".sqa-keys" / f"{worker}.key"

    if not key_path.is_file():
        log(f"SKIP {worker}: key file missing")
        return False

    key = key_path.read_text().strip()

    try:
        p = subprocess.run(
            [
                "bash",
                str(AUTO / "start_copilot_worker_v3.sh"),
                worker,
            ],
            cwd=ROOT,
            input=key + "\n",
            text=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            timeout=45,
            env={
                **os.environ,
                "D4J_SLOTS": "4",
                "D4J_TIMEOUT_SEC": "900",
            },
        )
    except subprocess.TimeoutExpired:
        log(f"START {worker}: launcher timed out")
        return False

    log(f"START {worker}: {p.stdout.strip()}")

    time.sleep(3)
    return worker in active_workers()


def best_standby(active, quota):
    choices = []

    for worker in WORKERS:
        if worker in active:
            continue
        if worker in COOLDOWN:
            continue

        q = quota.get(worker)
        if not q:
            continue
        if not q.get("ok"):
            continue
        if q.get("left", 0) < MIN_LEFT:
            continue

        choices.append((q["left"], worker))

    choices.sort(reverse=True)
    return choices[0][1] if choices else None


def main():
    log(f"C QUAD SWITCHER START — MAX_C={MAX_C}")

    while True:
        active = active_workers()

        # Workers that safely reached quota WAIT can be rotated out.
        for worker, pid in list(active.items()):
            if quota_paused(worker):
                stop_worker(worker, pid)
                COOLDOWN.add(worker)

        active = active_workers()

        wait = waiting_count()

        if wait is not None:
            log(
                f"ACTIVE={','.join(sorted(active)) or '-'} "
                f"WAITING={wait} "
                f"COOLDOWN={','.join(sorted(COOLDOWN)) or '-'}"
            )

            if wait <= 0:
                log("No WAITING cases remain — switcher finished")
                return
        else:
            log(
                f"ACTIVE={','.join(sorted(active)) or '-'} "
                f"WAITING=?"
            )

        # Fill vacant slots only while work may remain.
        if wait is None or wait > 0:
            if len(active) < MAX_C:
                quota = real_quota()

                while len(active) < MAX_C:
                    candidate = best_standby(active, quota)

                    if candidate is None:
                        log("No eligible standby C worker with usable quota")
                        break

                    left = quota[candidate]["left"]
                    log(f"ROTATE IN {candidate} — real LEFT={left:,}")

                    if not start_worker(candidate):
                        COOLDOWN.add(candidate)
                        log(f"{candidate} failed to start; placed in cooldown")

                    active = active_workers()

                    # Prevent repeatedly selecting a failed candidate.
                    if candidate not in active:
                        quota.pop(candidate, None)

        time.sleep(CHECK_SEC)


if __name__ == "__main__":
    main()
