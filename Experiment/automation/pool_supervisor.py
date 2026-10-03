#!/usr/bin/env python3

import json
import os
import re
import signal
import subprocess
import time
from pathlib import Path

ROOT = Path("/root/SQAProjectGroup11-git")
KEYDIR = Path.home() / ".sqa-keys"

MAX_W = 4
MAX_C = 4

# ตาม configuration ปัจจุบัน
W_D4J_SLOTS = "4"
C_D4J_SLOTS = "3"

CHECK_EVERY = 20

# ตัวที่ quota ให้พักก่อนลองบัญชีนั้นใหม่
QUOTA_COOLDOWN = 30 * 60

STATE_FILE = ROOT / "Experiment/runtime/pool-supervisor-state.json"
LOG_FILE = ROOT / "Experiment/logs/pool-supervisor.log"

CONFIG = {
    "W": {
        "logdir": ROOT / "Experiment/logs/final-opt",
        "claims": ROOT / "Experiment/runtime/run-final-opt/claims/chatgpt",
        "starter": ROOT / "Experiment/automation/start_worker_opt.sh",
        "slots": W_D4J_SLOTS,
        "max": MAX_W,
    },
    "C": {
        "logdir": ROOT / "Experiment/logs/copilot-final-v2",
        "claims": ROOT / "Experiment/runtime/run-copilot-final-v2/claims/copilot",
        "starter": ROOT / "Experiment/automation/start_copilot_worker_v3.sh",
        "slots": C_D4J_SLOTS,
        "max": MAX_C,
    },
}


def log(msg):
    s = f"[{time.strftime('%Y-%m-%d %H:%M:%S')}] {msg}"
    print(s, flush=True)
    LOG_FILE.parent.mkdir(parents=True, exist_ok=True)
    with LOG_FILE.open("a") as f:
        f.write(s + "\n")


def load_state():
    try:
        return json.loads(STATE_FILE.read_text())
    except Exception:
        return {"cooldown": {}}


def save_state(state):
    STATE_FILE.parent.mkdir(parents=True, exist_ok=True)
    STATE_FILE.write_text(json.dumps(state, indent=2))


def worker_num(name):
    m = re.search(r"\d+", name)
    return int(m.group()) if m else 99999


def pid_for(side, worker):
    pf = CONFIG[side]["logdir"] / f"{worker}.pid"
    try:
        raw = pf.read_text(errors="ignore")
        digits = re.sub(r"\D", "", raw)
        return int(digits) if digits else 0
    except Exception:
        return 0


def alive(pid):
    return bool(pid) and Path(f"/proc/{pid}").exists()


def children(pid):
    try:
        out = subprocess.check_output(
            ["pgrep", "-P", str(pid)],
            text=True,
            stderr=subprocess.DEVNULL,
        )
        return [int(x) for x in out.split()]
    except Exception:
        return []


def kill_tree(pid, sig=signal.SIGTERM):
    for child in children(pid):
        kill_tree(child, sig)
    try:
        os.kill(pid, sig)
    except ProcessLookupError:
        pass
    except Exception:
        pass


def quota_state(side, worker):
    logf = CONFIG[side]["logdir"] / f"{worker}.log"

    if not logf.exists():
        return False

    try:
        lines = logf.read_text(errors="ignore").splitlines()[-150:]
    except Exception:
        return False

    last_quota = -1
    last_progress = -1

    for i, line in enumerate(lines):
        low = line.lower()

        if (
            "daily quota" in low
            or "quota reached" in low
            or "quota paused" in low
            or "quota exceeded" in low
            or "429" in low
            or "sleeping 60 min" in low
        ):
            last_quota = i

        if (
            "claimed " in low
            or "prompt 01" in low
            or "prompt 02" in low
            or "prompt 03" in low
            or "prompt 04" in low
            or "fixed-valid" in low
            or " -> done" in low
            or " -> invalid" in low
        ):
            last_progress = i

    return last_quota >= 0 and last_quota >= last_progress


def clear_dead_claims(side, dead_pid):
    base = CONFIG[side]["claims"]

    if not base.exists():
        return

    for owner in base.glob("*.claim/owner.json"):
        try:
            data = json.loads(owner.read_text())
        except Exception:
            continue

        if data.get("pid") != dead_pid:
            continue

        if alive(dead_pid):
            continue

        claim = owner.parent

        try:
            import shutil
            shutil.rmtree(claim)
            log(f"{side}: released stale claim {claim.name}")
        except Exception as e:
            log(f"{side}: could not remove {claim}: {e}")


def stop_worker(side, worker):
    pid = pid_for(side, worker)

    if not pid:
        return

    log(f"{worker}: stopping pid={pid}")

    kill_tree(pid, signal.SIGTERM)
    time.sleep(2)

    if alive(pid):
        kill_tree(pid, signal.SIGKILL)
        time.sleep(1)

    pf = CONFIG[side]["logdir"] / f"{worker}.pid"
    try:
        pf.unlink()
    except FileNotFoundError:
        pass

    clear_dead_claims(side, pid)


def available_accounts(side):
    result = []

    for key in KEYDIR.glob(f"{side}[0-9]*.key"):
        worker = key.stem
        if re.fullmatch(rf"{side}\d+", worker):
            result.append(worker)

    return sorted(result, key=worker_num)


def start_worker(side, worker):
    cfg = CONFIG[side]
    keyfile = KEYDIR / f"{worker}.key"

    if not keyfile.exists():
        return False

    env = os.environ.copy()
    env["D4J_SLOTS"] = cfg["slots"]

    # start scripts เดิมจะอ่าน key จาก stdin
    try:
        fin = keyfile.open("r")

        subprocess.Popen(
            ["bash", str(cfg["starter"]), worker],
            cwd=ROOT,
            stdin=fin,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
            env=env,
            start_new_session=True,
        )

        log(f"{worker}: START requested D4J_SLOTS={cfg['slots']}")
        return True

    except Exception as e:
        log(f"{worker}: start failed: {e}")
        return False


def manage_side(side, state):
    cfg = CONFIG[side]
    accounts = available_accounts(side)

    # ตรวจตัวที่ยัง alive
    running = []

    for worker in accounts:
        pid = pid_for(side, worker)

        if not alive(pid):
            continue

        if quota_state(side, worker):
            log(f"{worker}: QUOTA detected")

            stop_worker(side, worker)

            state["cooldown"][worker] = time.time() + QUOTA_COOLDOWN
            save_state(state)
            continue

        running.append(worker)

    # ไม่ให้เกิน max
    if len(running) > cfg["max"]:
        extra = running[cfg["max"]:]

        for worker in extra:
            log(f"{worker}: over pool limit -> stopping")
            stop_worker(side, worker)

        running = running[:cfg["max"]]

    need = cfg["max"] - len(running)

    if need <= 0:
        return running

    now = time.time()

    candidates = []

    for worker in accounts:
        if worker in running:
            continue

        pid = pid_for(side, worker)
        if alive(pid):
            continue

        until = state["cooldown"].get(worker, 0)

        if now < until:
            continue

        candidates.append(worker)

    for worker in candidates[:need]:
        if start_worker(side, worker):
            running.append(worker)
            # กัน supervisor start ซ้ำก่อน pid file ถูกสร้าง
            state["cooldown"][worker] = time.time() + 60
            save_state(state)

    return running


def main():
    state = load_state()

    log("POOL SUPERVISOR START")
    log(f"W max={MAX_W}, C max={MAX_C}")
    log(f"W D4J={W_D4J_SLOTS}, C D4J={C_D4J_SLOTS}")

    while True:
        try:
            w = manage_side("W", state)
            c = manage_side("C", state)

            log(
                f"ACTIVE W={len(w)}/{MAX_W} "
                f"[{','.join(w)}] | "
                f"C={len(c)}/{MAX_C} "
                f"[{','.join(c)}]"
            )

        except Exception as e:
            log(f"SUPERVISOR ERROR: {e}")

        time.sleep(CHECK_EVERY)


if __name__ == "__main__":
    main()
