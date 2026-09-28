#!/usr/bin/env python3
"""Evaluate one generated Java test class with Defects4J buggy/fixed versions.

Example:
 python3 Experiment/evaluate_suite.py --project Cli --bug 1 \
   --class-name org.apache.commons.cli.CommandLineCopilotGeneratedTest \
   --source AI2_GitHubCopilot/TestCode/Cli-1/run-01/coverage-extended/CommandLineCopilotGeneratedTest.java \
   --output Experiment/evaluations/Cli-1/copilot-prompt04

The source is archived and injected with Defects4J -s. The original project
tests never contribute to generated-suite coverage or failure counts.
"""
import argparse
import csv
import hashlib
import json
import re
import shutil
import subprocess
import tarfile
import tempfile
import time
from pathlib import Path


def run(command, log):
    with log.open("w") as stream:
        result = subprocess.run(command, stdout=stream, stderr=subprocess.STDOUT,
                                check=False)
    return result.returncode


def fails(path, fqcn):
    if not path.is_file():
        return []
    return sorted(set(re.findall(
        rf"(?:^|\n)---\s+({re.escape(fqcn)}::[^\s]+)", path.read_text())))


def coverage(path):
    with path.open(newline="") as stream:
        entry = next(csv.DictReader(stream))
    return {key: int(entry[key]) for key in (
        "LinesTotal", "LinesCovered", "ConditionsTotal", "ConditionsCovered")}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--project", required=True)
    parser.add_argument("--bug", required=True, type=int)
    parser.add_argument("--class-name", required=True)
    parser.add_argument("--source", required=True, type=Path)
    parser.add_argument("--output", required=True, type=Path)
    parser.add_argument("--work-root", type=Path,
                        default=Path.home() / "sqa-workspaces")
    args = parser.parse_args()
    if args.bug < 1 or not args.source.is_file():
        parser.error("--bug must be positive and --source must exist")
    if args.class_name.split(".")[-1] != args.source.stem:
        parser.error("Java filename must match --class-name")
    source = args.source.resolve()
    text = source.read_text()
    if not re.search(rf"\bclass\s+{re.escape(source.stem)}\b", text):
        parser.error("The class declaration does not match --class-name")
    package = ".".join(args.class_name.split(".")[:-1])
    if package and not re.search(rf"\bpackage\s+{re.escape(package)}\s*;", text):
        parser.error("The package declaration does not match --class-name")

    output = args.output.resolve()
    if output.exists() and any(output.iterdir()):
        parser.error("--output must be a new or empty directory (preserve past runs)")
    output.mkdir(parents=True, exist_ok=True)
    started = time.monotonic()
    relative = Path(*args.class_name.split(".")).with_suffix(".java")
    archive = output / "generated-suite.1.tar.bz2"
    with tarfile.open(archive, "w:bz2") as stream:
        stream.add(source, arcname=str(relative))
    shutil.copy2(source, output / source.name)
    sha = hashlib.sha256(source.read_bytes()).hexdigest()

    results = {}
    for version in ("f", "b"):
        workspace = (args.work_root / f"{args.project}-{args.bug}{version}-benchmark").resolve()
        if not workspace.exists():
            checkout = run(["defects4j", "checkout", "-p", args.project,
                            "-v", f"{args.bug}{version}", "-w", str(workspace)],
                           output / f"{version}_checkout.log")
            if checkout:
                raise RuntimeError(f"checkout {version} failed; see {version}_checkout.log")
        probe = subprocess.run(["defects4j", "export", "-w", str(workspace),
                                "-p", "classes.modified"], capture_output=True,
                               text=True, check=False)
        if probe.returncode or not probe.stdout.strip():
            raise RuntimeError(f"Not a valid Defects4J checkout: {workspace}")
        for stale in ("failing_tests", "summary.csv"):
            (workspace / stale).unlink(missing_ok=True)

        test_log = output / f"{version}_test.log"
        test_code = run(["defects4j", "test", "-w", str(workspace),
                         "-s", str(archive)], test_log)
        log_text = test_log.read_text()
        if (not re.search(r"Running ant \(compile.gen.tests\)\.* OK", log_text)
                or not re.search(r"Running ant \(run.gen.tests\)\.* OK", log_text)):
            raise RuntimeError(f"Generated suite did not compile and run: {test_log}")
        failing_file = workspace / "failing_tests"
        failed = fails(failing_file, args.class_name)
        if failing_file.is_file():
            shutil.copy2(failing_file, output / f"{version}_failing_tests.txt")
        else:
            (output / f"{version}_failing_tests.txt").write_text("")

        (workspace / "summary.csv").unlink(missing_ok=True)
        cov_log = output / f"{version}_coverage.log"
        cov_code = run(["defects4j", "coverage", "-w", str(workspace),
                        "-s", str(archive)], cov_log)
        summary = workspace / "summary.csv"
        if (cov_code or not summary.is_file()
                or not re.search(r"Running ant \(coverage.report\)\.* OK",
                                 cov_log.read_text())):
            raise RuntimeError(f"Coverage failed: {cov_log}")
        shutil.copy2(summary, output / f"{version}_coverage_summary.csv")
        results[version] = {"workspace": str(workspace), "test_exit": test_code,
                            "failing_generated": failed, "coverage": coverage(summary)}
        print(f"{version}: {len(failed)} failing generated tests, "
              f"{results[version]['coverage']['LinesCovered']} lines, "
              f"{results[version]['coverage']['ConditionsCovered']} conditions",
              flush=True)

    fixed_fails = set(results["f"]["failing_generated"])
    buggy_fails = set(results["b"]["failing_generated"])
    detected_tests = sorted(buggy_fails - fixed_fails) if not fixed_fails else []
    summary = {"project": args.project, "bug": args.bug,
               "test_class": args.class_name, "source_sha256": sha,
               "fixed_failing": sorted(fixed_fails),
               "buggy_failing": sorted(buggy_fails),
               "detected_tests": detected_tests,
               "fault_detected": bool(detected_tests),
               "valid_on_fixed": not bool(fixed_fails),
               "fixed_coverage": results["f"]["coverage"],
               "buggy_coverage": results["b"]["coverage"],
               "seconds": round(time.monotonic() - started, 2),
               "workspaces": {v: results[v]["workspace"] for v in ("f", "b")}}
    (output / "result.json").write_text(json.dumps(summary, indent=2) + "\n")
    print(f"Detected bug: {summary['fault_detected']} (test(s): {len(detected_tests)})")
    print("Saved:", output / "result.json")


if __name__ == "__main__":
    main()
