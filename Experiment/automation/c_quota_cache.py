#!/usr/bin/env python3

import sys
import json
import time
import os
import fcntl
import tempfile
import urllib.request
import urllib.error

from pathlib import Path
from datetime import datetime
from concurrent.futures import ThreadPoolExecutor

ROOT = Path("/root/SQAProjectGroup11-git")

CACHE = ROOT / "Experiment/runtime/c_quota_cache.json"
LOCK = Path("/tmp/sqa-c-quota-cache.lock")

KEY_DIR = Path.home() / ".sqa-keys"

URL = "https://gen.ai.kku.ac.th/api/v1/chat/completions"

INTERVAL = 900  # 15 minutes

WORKERS = [f"C{i}" for i in range(1, 15)]

# Previously returned HTTP 401.
# Remove accounts from this set after their keys are fixed.
SKIP = set()


def check(worker):

    if worker in SKIP:
        return worker, {
            "status": "SKIPPED_401",
            "used": None,
            "left": None,
            "limit": None,
        }

    keyfile = KEY_DIR / f"{worker}.key"

    if not keyfile.is_file():
        return worker, {
            "status": "NO_KEY",
            "used": None,
            "left": None,
            "limit": None,
        }

    key = keyfile.read_text().strip()

    payload = {
        "model": "deepseek-v4-pro",
        "messages": [
            {"role": "user", "content": "OK"}
        ],
        "max_tokens": 1,
    }

    req = urllib.request.Request(
        URL,
        data=json.dumps(payload).encode(),
        headers={
            "Authorization": f"Bearer {key}",
            "Content-Type": "application/json",
        },
        method="POST",
    )

    try:
        with urllib.request.urlopen(
            req, timeout=30
        ) as response:
            data = json.load(response)

        quota = data.get("model_quota") or {}

        if not quota:
            return worker, {
                "status": "NO_QUOTA_DATA",
                "used": None,
                "left": None,
                "limit": None,
            }

        return worker, {
            "status": "OK",
            "used": quota.get("daily_usage_tokens"),
            "left": quota.get("daily_remaining_tokens"),
            "limit": quota.get("daily_quota_tokens"),
        }

    except urllib.error.HTTPError as e:
        return worker, {
            "status": f"HTTP_{e.code}",
            "used": None,
            "left": None,
            "limit": None,
        }

    except Exception as e:
        return worker, {
            "status": type(e).__name__,
            "used": None,
            "left": None,
            "limit": None,
        }


def refresh():

    print("Refreshing KKU quota...", flush=True)

    with ThreadPoolExecutor(max_workers=3) as executor:
        results = dict(executor.map(check, WORKERS))

    data = {
        "updated_at": datetime.now().astimezone().isoformat(),
        "workers": results,
    }

    CACHE.parent.mkdir(parents=True, exist_ok=True)

    # Atomic replacement prevents the monitor
    # from reading a partially written JSON file.
    fd, temp = tempfile.mkstemp(
        prefix=".c-quota-",
        suffix=".json",
        dir=str(CACHE.parent),
    )

    try:
        with os.fdopen(fd, "w") as f:
            json.dump(data, f, indent=2)
            f.flush()
            os.fsync(f.fileno())

        os.replace(temp, CACHE)

    finally:
        if os.path.exists(temp):
            os.unlink(temp)

    print("Quota cache updated.", flush=True)


def fmt(value):

    if isinstance(value, int):
        return f"{value:,}"

    return "-"


def show():

    print()
    print("=" * 72)
    print("COPILOT / DEEPSEEK — ACTUAL KKU API QUOTA")
    print("=" * 72)

    if not CACHE.is_file():
        print("No cache available. Waiting for first refresh.")
        return

    try:
        data = json.loads(CACHE.read_text())
    except Exception:
        print("Cache unavailable.")
        return

    updated = datetime.fromisoformat(
        data["updated_at"]
    )

    age = max(
        0,
        int(
            (
                datetime.now().astimezone()
                - updated
            ).total_seconds()
        ),
    )

    print("LAST CHECK:", updated.strftime("%H:%M:%S"))
    print("CACHE AGE :", f"{age // 60} minutes")

    if age > 1200:
        print("WARNING: QUOTA DATA IS STALE")

    print("-" * 72)

    print(
        f"{'WORKER':<8}"
        f"{'STATUS':<16}"
        f"{'USED':>14}"
        f"{'LEFT':>14}"
        f"{'LIMIT':>14}"
    )

    print("-" * 72)

    workers = data.get("workers", {})

    for worker in WORKERS:

        q = workers.get(worker, {})

        print(
            f"{worker:<8}"
            f"{q.get('status', '?'):<16}"
            f"{fmt(q.get('used')):>14}"
            f"{fmt(q.get('left')):>14}"
            f"{fmt(q.get('limit')):>14}"
        )

    print()
    print("Source: KKU API model_quota")
    print("Values are from the last check, not live.")


def daemon():

    LOCK.parent.mkdir(parents=True, exist_ok=True)

    with LOCK.open("w") as lock:

        try:
            fcntl.flock(
                lock,
                fcntl.LOCK_EX | fcntl.LOCK_NB,
            )
        except BlockingIOError:
            print("Quota updater already running.")
            return

        print("Quota updater started.", flush=True)

        while True:

            try:
                refresh()
            except Exception as e:
                print(
                    "Refresh failed:",
                    type(e).__name__,
                    flush=True,
                )

            time.sleep(INTERVAL)


if __name__ == "__main__":

    mode = sys.argv[1] if len(sys.argv) > 1 else "--show"

    if mode == "--daemon":
        daemon()

    elif mode == "--refresh":
        refresh()

    elif mode == "--show":
        show()

    else:
        print("Usage: --daemon | --refresh | --show")
        sys.exit(2)
