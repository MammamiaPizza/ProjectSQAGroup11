#!/usr/bin/env python3

import argparse
import json
import subprocess
from pathlib import Path


def run(cmd, cwd=None):
    result = subprocess.run(
        cmd,
        cwd=cwd,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        check=True
    )
    return result.stdout.strip()


def d4j_export(workspace, prop):
    return run(
        ["defects4j", "export", "-p", prop],
        cwd=workspace
    )


def class_to_path(class_name):
    # Modified-class entries are normally fully-qualified Java classes.
    # For nested classes, source belongs to outer class.
    outer = class_name.split("$", 1)[0]
    return Path(*outer.split(".")).with_suffix(".java")


def detect_build_tool(workspace):
    workspace = Path(workspace)

    if (workspace / "pom.xml").exists():
        return "Maven"

    if (
        (workspace / "build.xml").exists()
        or (workspace / "build.xml").is_file()
    ):
        return "Ant"

    if (
        (workspace / "gradlew").exists()
        or (workspace / "build.gradle").exists()
    ):
        return "Gradle"

    return "Defects4J project build"


def main():
    parser = argparse.ArgumentParser()

    parser.add_argument("--project", required=True)
    parser.add_argument("--bug", required=True, type=int)
    parser.add_argument("--workspace", required=True, type=Path)
    parser.add_argument("--output", required=True, type=Path)

    args = parser.parse_args()

    workspace = args.workspace.resolve()
    output = args.output.resolve()
    output.mkdir(parents=True, exist_ok=True)

    modified_raw = d4j_export(workspace, "classes.modified")
    modified = [
        x.strip()
        for x in modified_raw.splitlines()
        if x.strip()
    ]

    src_dir = d4j_export(workspace, "dir.src.classes").strip()
    test_dir = d4j_export(workspace, "dir.src.tests").strip()

    try:
        trigger_raw = d4j_export(workspace, "tests.trigger")
        triggers = [
            x.strip()
            for x in trigger_raw.splitlines()
            if x.strip()
        ]
    except subprocess.CalledProcessError:
        triggers = []

    sources = []

    for class_name in modified:
        rel = class_to_path(class_name)
        source_path = workspace / src_dir / rel

        entry = {
            "class": class_name,
            "path": str(source_path),
            "exists": source_path.exists(),
            "source": None,
        }

        if source_path.exists():
            entry["source"] = source_path.read_text(
                encoding="utf-8",
                errors="replace"
            )

        sources.append(entry)

    try:
        info = run(
            [
                "defects4j",
                "info",
                "-p",
                args.project,
                "-b",
                str(args.bug)
            ]
        )
    except subprocess.CalledProcessError as exc:
        info = exc.stdout or ""

    context = {
        "project": args.project,
        "bug_id": args.bug,
        "source_version": f"{args.project}-{args.bug}b",
        "workspace": str(workspace),
        "classes_modified": modified,
        "dir_src_classes": src_dir,
        "dir_src_tests": test_dir,
        "trigger_tests": triggers,
        "build_tool": detect_build_tool(workspace),
        "defects4j_info": info,
        "sources": sources,
    }

    (output / "context.json").write_text(
        json.dumps(
            context,
            indent=2,
            ensure_ascii=False
        ) + "\n",
        encoding="utf-8"
    )

    print("Project:", context["project"])
    print("Bug:", context["bug_id"])
    print("Modified classes:", len(modified))

    for item in sources:
        print(
            "-",
            item["class"],
            "FOUND" if item["exists"] else "MISSING"
        )

    print("Build tool:", context["build_tool"])
    print("Trigger tests:", len(triggers))
    print("Saved:", output / "context.json")


if __name__ == "__main__":
    main()
