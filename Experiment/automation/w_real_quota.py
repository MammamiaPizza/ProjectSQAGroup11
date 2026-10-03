#!/usr/bin/env python3

import json
import urllib.request
import urllib.error
from pathlib import Path

URL = "https://gen.ai.kku.ac.th/api/v1/chat/completions"
MODEL = "gpt-5.6-terra"
KEYDIR = Path.home() / ".sqa-keys"

def fmt(n):
    if not isinstance(n, int):
        return "-"
    if n >= 1_000_000:
        return f"{n/1_000_000:.2f}M"
    if n >= 1000:
        return f"{n/1000:.1f}k"
    return str(n)

print("=" * 76)
print("W REAL API QUOTA — KKU IntelSphere")
print("=" * 76)
print(f"{'WORKER':<8}{'STATE':<14}{'USED':>12}{'LEFT':>12}{'LIMIT':>12}  DETAIL")
print("-" * 76)

available = []
exhausted = []
errors = []

for i in range(1, 15):
    worker = f"W{i}"
    keyfile = KEYDIR / f"{worker}.key"

    if not keyfile.exists() or keyfile.stat().st_size == 0:
        print(f"{worker:<8}{'NO_KEY':<14}{'-':>12}{'-':>12}{'-':>12}  missing key")
        errors.append(worker)
        continue

    key = keyfile.read_text(errors="ignore").strip()

    # Tiny real request so the API returns current model_quota.
    # No max_tokens, matching the production runner behavior.
    payload = {
        "model": MODEL,
        "messages": [
            {
                "role": "user",
                "content": "Reply only: OK"
            }
        ],
        "stream": False
    }

    req = urllib.request.Request(
        URL,
        data=json.dumps(payload).encode("utf-8"),
        headers={
            "Authorization": f"Bearer {key}",
            "Content-Type": "application/json",
            "Accept": "application/json",
        },
        method="POST",
    )

    try:
        with urllib.request.urlopen(req, timeout=60) as r:
            data = json.loads(r.read().decode("utf-8", errors="replace"))

        quota = data.get("model_quota") or {}

        limit = quota.get("daily_quota_tokens")
        left = quota.get("daily_remaining_tokens")

        used = quota.get("daily_usage_tokens")
        if not isinstance(used, int):
            used = quota.get("daily_usage")

        if not isinstance(used, int) and isinstance(limit, int) and isinstance(left, int):
            used = max(0, limit - left)

        if isinstance(left, int):
            if left > 0:
                state = "AVAILABLE"
                available.append(worker)
            else:
                state = "EXHAUSTED"
                exhausted.append(worker)
        else:
            state = "OK_NO_QUOTA"
            errors.append(worker)

        print(
            f"{worker:<8}{state:<14}"
            f"{fmt(used):>12}{fmt(left):>12}{fmt(limit):>12}  API 200"
        )

    except urllib.error.HTTPError as e:
        body = e.read().decode("utf-8", errors="replace")

        if e.code == 401 and (
            "daily limit" in body.lower()
            or "quota" in body.lower()
            or "limit" in body.lower()
        ):
            state = "EXHAUSTED"
            exhausted.append(worker)
        else:
            state = f"HTTP_{e.code}"
            errors.append(worker)

        detail = body.replace("\n", " ")[:100]

        print(
            f"{worker:<8}{state:<14}"
            f"{'-':>12}{'-':>12}{'-':>12}  {detail}"
        )

    except Exception as e:
        errors.append(worker)
        print(
            f"{worker:<8}{'ERROR':<14}"
            f"{'-':>12}{'-':>12}{'-':>12}  {type(e).__name__}: {e}"
        )

print("-" * 76)
print("AVAILABLE :", " ".join(available) if available else "-")
print("EXHAUSTED :", " ".join(exhausted) if exhausted else "-")
print("ERROR     :", " ".join(errors) if errors else "-")
print("=" * 76)
