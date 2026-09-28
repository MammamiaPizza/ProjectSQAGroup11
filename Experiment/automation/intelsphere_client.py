#!/usr/bin/env python3

import argparse
import json
import os
import sys
import time
import urllib.error
import urllib.request
from pathlib import Path

API_URL = "https://gen.ai.kku.ac.th/api/v1/chat/completions"


def call_api(api_key: str, model: str, prompt: str, temperature: float):
    payload = {
        "model": model,
        "messages": [
            {
                "role": "user",
                "content": prompt
            }
        ],
        "temperature": temperature
    }

    body = json.dumps(payload).encode("utf-8")

    request = urllib.request.Request(
        API_URL,
        data=body,
        headers={
            "Authorization": f"Bearer {api_key}",
            "Content-Type": "application/json"
        },
        method="POST"
    )

    started = time.time()

    try:
        with urllib.request.urlopen(request, timeout=600) as response:
            raw = response.read().decode("utf-8")
    except urllib.error.HTTPError as exc:
        error_body = exc.read().decode("utf-8", errors="replace")
        print(f"HTTP {exc.code}", file=sys.stderr)
        print(error_body, file=sys.stderr)
        raise
    except urllib.error.URLError as exc:
        print(f"API connection error: {exc}", file=sys.stderr)
        raise

    elapsed = time.time() - started
    data = json.loads(raw)

    return data, elapsed


def main():
    parser = argparse.ArgumentParser()

    parser.add_argument(
        "--prompt",
        required=True,
        type=Path
    )

    parser.add_argument(
        "--output-dir",
        required=True,
        type=Path
    )

    parser.add_argument(
        "--model",
        default="gpt-5.6-terra"
    )

    parser.add_argument(
        "--temperature",
        type=float,
        default=0.0
    )

    args = parser.parse_args()

    api_key = os.environ.get("KKU_INTELSPHERE_API_KEY")

    if not api_key:
        raise SystemExit(
            "KKU_INTELSPHERE_API_KEY is not set"
        )

    prompt = args.prompt.read_text(encoding="utf-8")

    args.output_dir.mkdir(parents=True, exist_ok=True)

    data, elapsed = call_api(
        api_key=api_key,
        model=args.model,
        prompt=prompt,
        temperature=args.temperature
    )

    # Save exact input used.
    (args.output_dir / "prompt.txt").write_text(
        prompt,
        encoding="utf-8"
    )

    # Save complete provider response for reproducibility.
    (args.output_dir / "response.json").write_text(
        json.dumps(data, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8"
    )

    choices = data.get("choices") or []

    if choices:
        message = choices[0].get("message") or {}
        content = message.get("content") or ""
    else:
        content = ""

    (args.output_dir / "response.txt").write_text(
        content,
        encoding="utf-8"
    )

    usage = data.get("usage") or {}
    quota = data.get("model_quota") or {}

    metadata = {
        "provider": data.get("provider"),
        "requested_model": args.model,
        "returned_model": data.get("model"),
        "temperature": args.temperature,
        "finish_reason":
            choices[0].get("finish_reason") if choices else None,
        "prompt_tokens": usage.get("prompt_tokens"),
        "completion_tokens": usage.get("completion_tokens"),
        "total_tokens": usage.get("total_tokens"),
        "daily_quota_tokens": quota.get("daily_quota_tokens"),
        "daily_usage_tokens": quota.get("daily_usage_tokens"),
        "daily_remaining_tokens": quota.get("daily_remaining_tokens"),
        "elapsed_seconds": round(elapsed, 3),
        "response_id": data.get("id")
    }

    (args.output_dir / "metadata.json").write_text(
        json.dumps(metadata, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8"
    )

    print("API request completed")
    print("Provider:", metadata["provider"])
    print("Model:", metadata["returned_model"])
    print(
        "Tokens:",
        metadata["prompt_tokens"],
        "+",
        metadata["completion_tokens"],
        "=",
        metadata["total_tokens"]
    )
    print("Elapsed:", metadata["elapsed_seconds"], "sec")
    print(
        "Daily remaining:",
        metadata["daily_remaining_tokens"]
    )


if __name__ == "__main__":
    main()
