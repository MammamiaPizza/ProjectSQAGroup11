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

MAX_C = 2
CHECK_EVERY = 60
NO_QUOTA_RETRY = 300
D4J_SLOTS = 4
D4J_TIMEOUT = 900


def log(msg):
    print(
        time.strftime("[%Y-%m-%d %H:%M:%S]"),
        msg,
        flush=True
    )


def active_c_workers():
    p = subprocess.run(
        ["ps", "-eo", "pid,args"],
        capture_output=True,
        text=True
    )

    found = {}

    for line in p.stdout.splitlines():
        m = re.search(
            r"^\s*(\d+)\s+.*Experiment/automation/ai2_runner\.py"
            r".*--worker\s+(C\d+)(?:\s|$)",
            line
        )

        if m:
            found[m.group(2)] = int(m.group(1))

    return found


def direct_children(pid):
    p = subprocess.run(
        ["pgrep", "-P", str(pid)],
        capture_output=True,
        text=True
    )

    return [
        int(x)
        for x in p.stdout.split()
        if x.isdigit()
    ]


def quota_paused(worker):
    log_file = LOGDIR / f"{worker}.log"

    if not log_file.exists():
        return False

    try:
        lines = log_file.read_text(errors="ignore").splitlines()[-200:]
    except Exception:
        return False

    # อ่านจากท้าย log เพื่อดู event ล่าสุด
    for line in reversed(lines):

        if re.search(
            r"QUOTA PAUSED|DAILY QUOTA REACHED|daily quota reached|"
            r"This model reached daily limit|HTTP 401",
            line,
            re.I
        ):
            return True

        # ถ้ามี activity ใหม่กว่าข้อความ quota แปลว่าตัวนี้กลับมาทำงานแล้ว
        if re.search(
            r"CLAIMED|Prompt 0[1-4]|acquired Defects4J|"
            r"->\s+(DONE|ERROR|INVALID|OUTPUT)",
            line,
            re.I
        ):
            return False

    return False


def stop_quota_worker(worker, pid):
    # ไม่ฆ่าถ้ายังมี child ทำงานอยู่
    children = direct_children(pid)

    if children:
        log(
            f"{worker}: quota detected but still has children "
            f"{children}; wait before stopping"
        )
        return False

    log(f"{worker}: quota exhausted -> stopping pid={pid}")

    try:
        os.kill(pid, signal.SIGTERM)
    except ProcessLookupError:
        return True

    for _ in range(20):
        if not Path(f"/proc/{pid}").exists():
            log(f"{worker}: stopped")
            return True
        time.sleep(0.5)

    try:
        os.kill(pid, signal.SIGKILL)
        log(f"{worker}: SIGKILL fallback")
    except ProcessLookupError:
        pass

    return True


def real_quota():
    """
    c_real_quota.py ยิง KKU API จริงแล้ว output เช่น:
    C7     12    999,988    1,000,000
    C4     HTTP 401 ...
    """
    try:
        p = subprocess.run(
            ["python3", "Experiment/automation/c_real_quota.py"],
            cwd=ROOT,
            capture_output=True,
            text=True,
            timeout=240
        )
    except subprocess.TimeoutExpired:
        log("quota API check timed out")
        return []

    candidates = []

    for line in p.stdout.splitlines():
        m = re.match(
            r"^\s*(C\d+)\s+([\d,]+)\s+([\d,]+)\s+([\d,]+)\s*$",
            line
        )

        if not m:
            continue

        worker = m.group(1)
        used = int(m.group(2).replace(",", ""))
        left = int(m.group(3).replace(",", ""))
        limit = int(m.group(4).replace(",", ""))

        if left > 0:
            candidates.append({
                "worker": worker,
                "used": used,
                "left": left,
                "limit": limit,
            })

    # quota มากสุดก่อน
    candidates.sort(
        key=lambda x: (
            -x["left"],
            int(x["worker"][1:])
        )
    )

    return candidates


def start_worker(worker):
    keyfile = KEYDIR / f"{worker}.key"

    if not keyfile.exists() or keyfile.stat().st_size == 0:
        log(f"{worker}: missing key")
        return False

    key = keyfile.read_text(errors="ignore").strip()

    if not key:
        log(f"{worker}: empty key")
        return False

    env = os.environ.copy()
    env["D4J_SLOTS"] = str(D4J_SLOTS)
    env["D4J_TIMEOUT_SEC"] = str(D4J_TIMEOUT)

    log(f"{worker}: starting")

    try:
        p = subprocess.run(
            [
                "bash",
                "Experiment/automation/start_copilot_worker_v3.sh",
                worker
            ],
            cwd=ROOT,
            input=key + "\n",
            text=True,
            env=env,
            timeout=60
        )
    except subprocess.TimeoutExpired:
        log(f"{worker}: launcher timeout")
        return False

    time.sleep(3)

    active = active_c_workers()

    if worker in active:
        log(f"{worker}: started pid={active[worker]}")
        return True

    log(f"{worker}: launcher finished but worker not found")
    return False


def main():
    log(f"C DUAL SWITCHER START — MAX_C={MAX_C}")

    while True:
        try:
            active = active_c_workers()

            # 1) เอาตัวที่ quota หมดออกก่อน
            for worker, pid in list(active.items()):
                if quota_paused(worker):
                    stop_quota_worker(worker, pid)

            active = active_c_workers()

            # ปลอดภัยไว้ก่อน ถ้ามี >2 จากการเปิด manual
            # จะไม่ฆ่า healthy worker เอง
            if len(active) > MAX_C:
                log(
                    "WARNING: more than 2 C workers active: "
                    + " ".join(sorted(active))
                    + " — not starting more"
                )
                time.sleep(CHECK_EVERY)
                continue

            need = MAX_C - len(active)

            if need <= 0:
                log(
                    "ACTIVE 2/2: "
                    + " ".join(sorted(
                        active,
                        key=lambda x: int(x[1:])
                    ))
                )
                time.sleep(CHECK_EVERY)
                continue

            log(
                f"ACTIVE {len(active)}/{MAX_C}: "
                + (" ".join(sorted(active)) if active else "-")
                + f" | need {need}"
            )

            # 2) ต้องหาตัวใหม่ -> ค่อยยิง API จริง
            quotas = real_quota()

            if not quotas:
                log(
                    f"no C account with quota; "
                    f"retry in {NO_QUOTA_RETRY}s"
                )
                time.sleep(NO_QUOTA_RETRY)
                continue

            active_now = active_c_workers()

            available = [
                x for x in quotas
                if x["worker"] not in active_now
            ]

            started = 0

            for item in available:
                if started >= need:
                    break

                worker = item["worker"]

                log(
                    f"select {worker}: "
                    f"left={item['left']:,}/"
                    f"{item['limit']:,}"
                )

                if start_worker(worker):
                    started += 1

            if started < need:
                log(
                    f"only started {started}/{need} "
                    f"replacement worker(s)"
                )

            time.sleep(CHECK_EVERY)

        except KeyboardInterrupt:
            log("switcher stopped")
            break

        except Exception as e:
            log(f"ERROR: {type(e).__name__}: {e}")
            time.sleep(60)


if __name__ == "__main__":
    main()
