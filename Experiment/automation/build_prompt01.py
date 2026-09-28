#!/usr/bin/env python3

import argparse
import json
import subprocess
from pathlib import Path


def run(cmd, cwd=None):
    p = subprocess.run(
        cmd,
        cwd=cwd,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        check=True,
    )
    return p.stdout.strip()


def detect_junit_version(workspace: Path):
    try:
        cp = run(
            ["defects4j", "export", "-p", "cp.test"],
            cwd=workspace,
        )
    except Exception:
        return "Unknown"

    low = cp.lower()

    # Keep exact jar clue when possible.
    for part in cp.split(":"):
        name = Path(part).name.lower()
        if "junit" in name:
            return Path(part).name

    if "junit" in low:
        return "JUnit (version unresolved from classpath)"

    return "Unknown"


def source_block(context):
    blocks = []

    for item in context["sources"]:
        if not item.get("source"):
            continue

        blocks.append(
            "===== TARGET CLASS: "
            + item["class"]
            + " =====\n"
            + item["source"]
        )

    return "\n\n".join(blocks)


def project_context_block(context):
    triggers = context.get("trigger_tests") or []

    pieces = [
        "Defects4J project information:",
        context.get("defects4j_info", "").strip(),
        "",
        "Modified classes:",
        "\n".join(
            "- " + x
            for x in context.get("classes_modified", [])
        ),
        "",
        "Triggering tests:",
        "\n".join("- " + x for x in triggers)
        if triggers else "(none reported)",
    ]

    return "\n".join(pieces).strip()


def replace_required(template, values):
    result = template

    for key, value in values.items():
        placeholder = f"[{key}]"

        if placeholder not in result:
            raise RuntimeError(
                f"Missing placeholder in template: {placeholder}"
            )

        result = result.replace(
            placeholder,
            str(value),
        )

    return result


def main():
    ap = argparse.ArgumentParser()

    ap.add_argument(
        "--context",
        required=True,
        type=Path,
    )

    ap.add_argument(
        "--template",
        required=True,
        type=Path,
    )

    ap.add_argument(
        "--output",
        required=True,
        type=Path,
    )

    args = ap.parse_args()

    context = json.loads(
        args.context.read_text(encoding="utf-8")
    )

    workspace = Path(context["workspace"])

    template = args.template.read_text(
        encoding="utf-8"
    )

    target_classes = context.get(
        "classes_modified", []
    )

    if not target_classes:
        raise RuntimeError(
            "No modified classes found"
        )

    java_source = source_block(context)

    if not java_source:
        raise RuntimeError(
            "No production source was extracted"
        )

    junit_version = detect_junit_version(
        workspace
    )

    # For batch mode, use Defects4J metadata as the supplied
    # defect/specification context. Do not inspect fixed source.
    specification = (
        context.get("defects4j_info", "").strip()
        or
        "No additional behavioral specification was supplied."
    )

    values = {
        "PROJECT_ID":
            context["project"],

        "BUG_ID":
            context["bug_id"],

        "SOURCE_VERSION":
            context["source_version"],

        "TARGET_CLASS":
            ", ".join(target_classes),

        "JUNIT_VERSION":
            junit_version,

        "BUILD_TOOL":
            context.get(
                "build_tool",
                "Defects4J project build"
            ),

        "SPECIFICATION_OR_BUG_REPORT":
            specification,

        "JAVA_SOURCE_CODE":
            java_source,

        "PROJECT_CONTEXT":
            project_context_block(context),
    }

    prompt = replace_required(
        template,
        values,
    )

    # Fail if any known placeholder survives.
    leftovers = [
        x for x in values
        if f"[{x}]" in prompt
    ]

    if leftovers:
        raise RuntimeError(
            "Unresolved placeholders: "
            + ", ".join(leftovers)
        )

    args.output.parent.mkdir(
        parents=True,
        exist_ok=True,
    )

    args.output.write_text(
        prompt,
        encoding="utf-8",
    )

    metadata = {
        "project":
            context["project"],

        "bug_id":
            context["bug_id"],

        "source_version":
            context["source_version"],

        "target_classes":
            target_classes,

        "junit_version":
            junit_version,

        "build_tool":
            context.get("build_tool"),

        "prompt_chars":
            len(prompt),

        "prompt_bytes":
            len(prompt.encode("utf-8")),
    }

    meta_path = args.output.with_suffix(
        args.output.suffix + ".meta.json"
    )

    meta_path.write_text(
        json.dumps(
            metadata,
            indent=2,
            ensure_ascii=False,
        ) + "\n",
        encoding="utf-8",
    )

    print("Prompt built:", args.output)
    print(
        "Target classes:",
        len(target_classes)
    )
    print(
        "JUnit:",
        junit_version
    )
    print(
        "Characters:",
        metadata["prompt_chars"]
    )
    print(
        "Bytes:",
        metadata["prompt_bytes"]
    )


if __name__ == "__main__":
    main()
