#!/usr/bin/env python3

import json
import urllib.request
import urllib.error
from pathlib import Path
from concurrent.futures import ThreadPoolExecutor

KEY_DIR = Path.home() / ".sqa-keys"
URL = "https://gen.ai.kku.ac.th/api/v1/chat/completions"

def check(worker):
    keyfile = KEY_DIR / f"{worker}.key"

    if not keyfile.is_file():
        return worker, "NO KEY", "-", "-"

    key = keyfile.read_text().strip()

    payload = {
        "model": "deepseek-v4-pro",
        "messages": [
            {"role": "user", "content": "OK"}
        ],
        "max_tokens": 1
    }

    request = urllib.request.Request(
        URL,
        data=json.dumps(payload).encode(),
        headers={
            "Authorization": f"Bearer {key}",
            "Content-Type": "application/json"
        },
        method="POST"
    )

    try:
        with urllib.request.urlopen(request, timeout=30) as response:
            result = json.load(response)

        quota = result.get("model_quota") or {}

        return (
            worker,
            quota.get("daily_usage_tokens", "?"),
            quota.get("daily_remaining_tokens", "?"),
            quota.get("daily_quota_tokens", "?")
        )

    except urllib.error.HTTPError as e:
        return worker, f"HTTP {e.code}", "-", "-"

    except Exception as e:
        return worker, type(e).__name__, "-", "-"


workers = [f"C{i}" for i in range(1, 15)]

print(f"{'WORKER':<8}{'USED':>14}{'LEFT':>14}{'DAILY LIMIT':>16}")
print("-" * 52)

with ThreadPoolExecutor(max_workers=3) as executor:
    for worker, used, left, limit in executor.map(check, workers):
        def fmt(n):
            return f"{n:,}" if isinstance(n, int) else str(n)

        print(
            f"{worker:<8}"
            f"{fmt(used):>14}"
            f"{fmt(left):>14}"
            f"{fmt(limit):>16}"
        )
