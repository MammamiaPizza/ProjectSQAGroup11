#!/usr/bin/env python3
"""
AI benchmark runner for Defects4J.

Supports:
- Single-stage debugging (Prompt 01/02/03)
- End-to-end batch mode:
  Prompt 01 -> Prompt 02 -> fixed validation
      -> Prompt 03 once if needed
      -> fixed coverage -> Prompt 04
      -> final fixed/buggy evaluation
- Resume without re-sending completed prompts
- Multiple projects in one long run
- No max_tokens is sent to IntelSphere (provider/model decides output ceiling)
- Raw responses, prompts, Java, logs, coverage, metadata, and final result are preserved

Important methodology:
- Test generation context comes only from the buggy version.
- Fixed version is used only by the external evaluator/validator.
- Prompt 03 is allowed at most once per case.
- Prompt 04 runs only after a suite is valid on fixed.
"""

import argparse
import csv
import hashlib
import contextlib
import json
import os
import re
import shutil
import subprocess
import sys
import tarfile
import tempfile
import time
import socket
import urllib.error
import urllib.request
import xml.etree.ElementTree as ET
from datetime import datetime
from pathlib import Path


ROOT = Path(__file__).resolve().parents[2]
WORK_ROOT = Path.home() / "sqa-workspaces"
INTELSPHERE_URL = "https://gen.ai.kku.ac.th/api/v1/chat/completions"


# Runtime configuration; populated from CLI.
RUN_ID = "final-opt"
WORKER_ID = "single"
D4J_SLOTS = 1
RUNTIME_ROOT = ROOT / "Experiment" / "runtime"


class QuotaExhausted(RuntimeError):
    pass


class OutputIncomplete(RuntimeError):
    pass


def process_alive(pid):
    try:
        os.kill(int(pid), 0)
        return True
    except (ProcessLookupError, ValueError):
        return False
    except PermissionError:
        return True


def _read_owner(path):
    try:
        return json.loads((path / "owner.json").read_text(encoding="utf-8"))
    except Exception:
        return {}


def _cleanup_dead_lock(path):
    if not path.exists():
        return
    owner = _read_owner(path)
    pid = owner.get("pid")
    if pid and process_alive(pid):
        return
    try:
        shutil.rmtree(path)
    except FileNotFoundError:
        pass
    except OSError:
        pass


@contextlib.contextmanager
def acquire_d4j_slot():
    """Cross-process semaphore using atomic mkdir; shared across all workers on one WSL host."""
    slots_root = RUNTIME_ROOT / f"run-{RUN_ID}" / "d4j-slots"
    slots_root.mkdir(parents=True, exist_ok=True)

    acquired = None
    while acquired is None:
        for i in range(1, D4J_SLOTS + 1):
            slot = slots_root / f"slot-{i}"
            _cleanup_dead_lock(slot)
            try:
                slot.mkdir()
            except FileExistsError:
                continue

            owner = {
                "pid": os.getpid(),
                "worker": WORKER_ID,
                "host": socket.gethostname(),
                "acquired_at": now_iso() if "now_iso" in globals() else time.time(),
            }
            (slot / "owner.json").write_text(
                json.dumps(owner, indent=2) + "\n",
                encoding="utf-8",
            )
            acquired = slot
            print(
                f"[{WORKER_ID}] acquired Defects4J slot {i}/{D4J_SLOTS}",
                flush=True,
            )
            break

        if acquired is None:
            time.sleep(2)

    try:
        yield
    finally:
        if acquired is not None:
            try:
                shutil.rmtree(acquired)
            except FileNotFoundError:
                pass
            print(f"[{WORKER_ID}] released Defects4J slot", flush=True)


def is_heavy_d4j_command(args):
    if not args:
        return False
    first = Path(str(args[0])).name
    if first != "defects4j" or len(args) < 2:
        return False
    return str(args[1]) in {"checkout", "compile", "test", "coverage"}


# ---------------------------- basic helpers ----------------------------

def cmd(args, cwd=None, check=True):
    def execute():
        return subprocess.run(
            [str(x) for x in args],
            cwd=cwd,
            text=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            check=False,
        )

    if is_heavy_d4j_command(args):
        with acquire_d4j_slot():
            p = execute()
    else:
        p = execute()

    if check and p.returncode != 0:
        raise RuntimeError(
            f"Command failed ({p.returncode}): {' '.join(map(str, args))}\n"
            f"STDOUT:\n{p.stdout}\nSTDERR:\n{p.stderr}"
        )
    return p


def clean_export_text(text):
    out = []
    for raw in text.splitlines():
        line = raw.strip()
        if not line:
            continue
        if line.startswith("Running ant ("):
            continue
        if re.fullmatch(r"\.*\s*OK", line):
            continue
        out.append(line)
    return "\n".join(out).strip()


def d4j_export(workspace, prop):
    p = cmd(["defects4j", "export", "-p", prop], cwd=workspace)
    merged = "\n".join(x for x in (p.stdout, p.stderr) if x)
    return clean_export_text(merged)


def ensure_checkout(project, bug, version):
    workspace = WORK_ROOT / f"{project}-{bug}{version}-ai"
    if workspace.exists():
        probe = cmd(
            ["defects4j", "export", "-p", "classes.modified"],
            cwd=workspace,
            check=False,
        )
        if probe.returncode == 0:
            return workspace
        # Infrastructure recovery only: a stale/incomplete checkout must not
        # kill a long-running worker or consume another AI request.
        print(f"[{WORKER_ID}] removing stale workspace: {workspace}", flush=True)
        shutil.rmtree(workspace, ignore_errors=True)

    workspace.parent.mkdir(parents=True, exist_ok=True)
    cmd([
        "defects4j", "checkout",
        "-p", project,
        "-v", f"{bug}{version}",
        "-w", str(workspace),
    ])
    return workspace


def detect_build_tool(workspace):
    if (workspace / "pom.xml").is_file():
        return "Maven"
    if (workspace / "build.gradle").is_file() or (workspace / "gradlew").exists():
        return "Gradle"
    if (workspace / "build.xml").is_file():
        return "Ant"
    return "Defects4J project build"


def detect_junit(cp_test):
    for part in cp_test.split(":"):
        name = Path(part.strip()).name
        if re.search(r"junit", name, re.I):
            return name
    return "Unknown"


def class_to_source_candidates(src_root, class_name):
    """Candidates for top-level and nested Java class names, preserving $-prefixed files."""
    simple = class_name.rsplit(".", 1)[-1]
    package = class_name.rsplit(".", 1)[0] if "." in class_name else ""
    pkg_path = Path(*package.split(".")) if package else Path()

    # Exact file first. This is required for real top-level names such as
    # com.google.gson.internal.$Gson$Types -> $Gson$Types.java.
    yield src_root / pkg_path / f"{simple}.java"

    # If it looks like a normal nested class, try its outer class source.
    if "$" in simple and not simple.startswith("$"):
        outer = simple.split("$", 1)[0]
        if outer:
            yield src_root / pkg_path / f"{outer}.java"


def now_iso():
    return datetime.now().astimezone().isoformat(timespec="seconds")


def _java_package(path):
    try:
        head = path.read_text(encoding="utf-8", errors="replace")[:12000]
    except Exception:
        return ""
    m = re.search(r"(?m)^\s*package\s+([A-Za-z_$][\w.$]*)\s*;", head)
    return m.group(1) if m else ""


def resolve_java_source(workspace, src_root, class_name):
    """Resolve Defects4J classes.modified without assuming every '$' is nesting."""
    for direct in class_to_source_candidates(src_root, class_name):
        if direct.is_file():
            return direct

    simple = class_name.rsplit(".", 1)[-1]
    expected_pkg = class_name.rsplit(".", 1)[0] if "." in class_name else ""
    names = [simple]
    if "$" in simple and not simple.startswith("$"):
        names.append(simple.split("$", 1)[0])

    candidates = []
    for name in dict.fromkeys(names):
        for path in workspace.rglob(f"{name}.java"):
            p = str(path).replace("\\", "/")
            if any(x in p for x in ("/target/", "/build/", "/.git/")):
                continue
            candidates.append(path)

    if expected_pkg:
        pkg_matches = [p for p in candidates if _java_package(p) == expected_pkg]
        if pkg_matches:
            return sorted(pkg_matches, key=lambda p: len(str(p)))[0]

    if candidates:
        prod = [
            p for p in candidates
            if not re.search(r"(^|/)(test|tests|src/test)(/|$)", str(p).replace("\\", "/"), re.I)
        ]
        return sorted(prod or candidates, key=lambda p: len(str(p)))[0]

    # Fallback: Defects4J classes.modified may name a package-private,
    # nested, enum, or other type declared inside a differently named
    # production .java file. Search only its expected package directory.
    if expected_pkg and src_root.exists():
        pkg_dir = src_root / Path(*expected_pkg.split("."))
        if pkg_dir.is_dir():
            decl = re.compile(
                rf"\\b(?:class|interface|enum|record)\\s+{re.escape(simple)}\\b"
            )
            declaration_matches = []

            for path in pkg_dir.glob("*.java"):
                try:
                    body = path.read_text(
                        encoding="utf-8",
                        errors="replace",
                    )
                except Exception:
                    continue

                if decl.search(body):
                    declaration_matches.append(path)

            if len(declaration_matches) == 1:
                return declaration_matches[0]

            if len(declaration_matches) > 1:
                pkg_matches = [
                    path for path in declaration_matches
                    if _java_package(path) == expected_pkg
                ]
                if len(pkg_matches) == 1:
                    return pkg_matches[0]

    return None


def resolve_resource_source(workspace, entry):
    prefixes = {
        "src.main.resources.": Path("src/main/resources"),
        "src.test.resources.": Path("src/test/resources"),
    }
    for prefix, root_rel in prefixes.items():
        if not entry.startswith(prefix):
            continue
        rest = entry[len(prefix):].split(".")
        if len(rest) < 2:
            continue
        rel = Path(*rest[:-2]) / f"{rest[-2]}.{rest[-1]}"
        candidate = workspace / root_rel / rel
        if candidate.is_file():
            return candidate
        filename = f"{rest[-2]}.{rest[-1]}"
        matches = [
            p for p in workspace.rglob(filename)
            if "/target/" not in str(p).replace("\\", "/")
            and "/build/" not in str(p).replace("\\", "/")
        ]
        if matches:
            return sorted(matches, key=lambda p: len(str(p)))[0]
    return None


def related_java_for_resource(src_root, resource_path, limit=2):
    terms = {resource_path.name, resource_path.stem}
    found = []
    if not src_root.exists():
        return found
    for path in src_root.rglob("*.java"):
        try:
            body = path.read_text(encoding="utf-8", errors="replace")
        except Exception:
            continue
        if any(term and term in body for term in terms):
            found.append(path)
            if len(found) >= limit:
                break
    return found


# ---------------------------- deterministic context ----------------------------

def _source_entry(entry, kind, path):
    source = path.read_text(encoding="utf-8", errors="replace")
    return {
        "class": entry,
        "kind": kind,
        "path": str(path),
        "sha256": hashlib.sha256(source.encode("utf-8", errors="replace")).hexdigest(),
        "bytes": len(source.encode("utf-8", errors="replace")),
        "source": source,
    }


def build_context(project, bug, workspace):
    modified = [
        x.strip() for x in d4j_export(workspace, "classes.modified").splitlines()
        if x.strip()
    ]
    if not modified:
        raise RuntimeError("Defects4J returned no modified classes/artifacts.")

    src_dir = d4j_export(workspace, "dir.src.classes").splitlines()[-1].strip()
    tests_dir = d4j_export(workspace, "dir.src.tests").splitlines()[-1].strip()
    cp_test = d4j_export(workspace, "cp.test")
    try:
        trigger_text = d4j_export(workspace, "tests.trigger")
        triggers = [x for x in trigger_text.splitlines() if "::" in x]
    except Exception:
        triggers = []

    info = cmd(["defects4j", "info", "-p", project, "-b", str(bug)]).stdout.strip()
    src_root = workspace / src_dir
    sources, unresolved, added = [], [], set()

    for entry in modified:
        java_path = resolve_java_source(workspace, src_root, entry)
        if java_path is not None:
            key = str(java_path.resolve())
            if key not in added:
                sources.append(_source_entry(entry, "java", java_path))
                added.add(key)
            continue

        resource_path = resolve_resource_source(workspace, entry)
        if resource_path is not None:
            key = str(resource_path.resolve())
            if key not in added:
                sources.append(_source_entry(entry, "resource", resource_path))
                added.add(key)
            for ref in related_java_for_resource(src_root, resource_path):
                rkey = str(ref.resolve())
                if rkey not in added:
                    sources.append(_source_entry(f"related:{ref.stem}", "java-related-to-resource", ref))
                    added.add(rkey)
            continue
        unresolved.append(entry)

    # Some Defects4J classes.modified entries do not map 1:1 to a
    # physical .java source file. Keep them as metadata rather than
    # failing the entire case when other modified production sources
    # were resolved successfully.
    if unresolved and not sources:
        raise RuntimeError(
            "Modified source/artifact could not be resolved:\n"
            + "\n".join(f"  - {x}" for x in unresolved)
        )

    if unresolved:
        print(
            f"[{project}-{bug}] WARNING: {len(unresolved)} modified "
            f"class/artifact(s) unresolved; continuing with "
            f"{len(sources)} resolved source file(s): "
            + ", ".join(unresolved),
            flush=True,
        )

    return {
        "project": project,
        "bug_id": bug,
        "source_version": f"{project}-{bug}b",
        "workspace": str(workspace),
        "classes_modified": modified,
        "unresolved_modified": unresolved,
        "dir_src_classes": src_dir,
        "dir_src_tests": tests_dir,
        "trigger_tests": triggers,
        "build_tool": detect_build_tool(workspace),
        "junit_version": detect_junit(cp_test),
        "defects4j_info": info,
        "sources": sources,
    }


def _context_manifest(context):
    data = {k: v for k, v in context.items() if k != "sources"}
    data["sources"] = [
        {k: v for k, v in item.items() if k != "source"}
        for item in context["sources"]
    ]
    return data


def _hydrate_manifest(data):
    hydrated = dict(data)
    hydrated["sources"] = []
    for item in data.get("sources", []):
        path = Path(item["path"])
        if not path.is_file():
            return None
        full = dict(item)
        full["source"] = path.read_text(encoding="utf-8", errors="replace")
        hydrated["sources"].append(full)
    return hydrated


def load_or_build_context(project, bug):
    # Separate compact-manifest namespace from the old pilot contexts.
    case = f"{project}-{bug}"
    context_dir = ROOT / "Experiment" / "contexts_opt" / case
    context_path = context_dir / "context.json"

    if context_path.is_file():
        try:
            cached = json.loads(context_path.read_text(encoding="utf-8"))
            hydrated = _hydrate_manifest(cached)
            if hydrated is not None:
                return hydrated, context_path
        except Exception:
            pass

    buggy = ensure_checkout(project, bug, "b")
    context = build_context(project, bug, buggy)
    context_dir.mkdir(parents=True, exist_ok=True)
    context_path.write_text(
        json.dumps(_context_manifest(context), indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8",
    )
    return context, context_path


# ---------------------------- token-saving context ----------------------------

STOP_WORDS = {
    "the", "and", "for", "with", "from", "this", "that", "test", "tests", "bug",
    "class", "java", "method", "should", "when", "then", "into", "using", "fixed",
    "version", "project", "defects4j", "failure", "failing", "expected", "actual",
}


def compact_text(text, max_chars):
    text = re.sub(r"[ \t]+", " ", text or "")
    text = re.sub(r"\n{3,}", "\n\n", text).strip()
    if len(text) <= max_chars:
        return text
    head = max_chars * 2 // 3
    tail = max_chars - head
    return text[:head].rstrip() + "\n...[truncated deterministically]...\n" + text[-tail:].lstrip()


def relevance_terms(context):
    blob = "\n".join([
        context.get("defects4j_info", ""),
        " ".join(context.get("trigger_tests", [])),
        " ".join(context.get("classes_modified", [])),
    ])
    words = re.findall(r"[A-Za-z_$][A-Za-z0-9_$]{2,}", blob)
    out = []
    for word in words:
        low = word.lower()
        if low not in STOP_WORDS and low not in out:
            out.append(low)
    return set(out[:80])


def java_signatures(source, max_chars=6500):
    out = []
    for raw in source.splitlines():
        line = raw.strip()
        if not line or line.startswith("//") or line.startswith("*"):
            continue
        if line.startswith(("package ", "import ", "@")):
            if line.startswith("import "):
                continue
            out.append(line)
            continue
        if re.search(r"\b(class|interface|enum|record)\b", line):
            out.append(line)
            continue
        if "(" in line and ")" in line and re.search(
            r"\b(public|protected|private|static|final|synchronized|native|abstract)\b", line
        ):
            out.append(line[:500])
            continue
        if re.search(r"\b(public|protected)\b", line) and ";" in line:
            out.append(line[:500])
    return compact_text("\n".join(dict.fromkeys(out)), max_chars)


def _find_method_blocks(source):
    # Lightweight Java method/constructor block extraction; deterministic and
    # intentionally conservative. Signatures are always supplied separately.
    pattern = re.compile(
        r"(?m)^[ \t]*(?:@[A-Za-z_$][\w.$]*(?:\([^\n]*\))?[ \t]*\n[ \t]*)*"
        r"(?:(?:public|protected|private|static|final|synchronized|abstract|native|default|strictfp)\s+)*"
        r"(?:<[^{;\n>]+>\s*)?(?:[A-Za-z_$][\w.$<>\[\], ?]*\s+)?"
        r"([A-Za-z_$][\w$]*)\s*\([^;{}]*\)\s*(?:throws\s+[^\{]+)?\{"
    )
    control = {"if", "for", "while", "switch", "catch", "synchronized", "try"}
    blocks = []
    for m in pattern.finditer(source):
        name = m.group(1)
        if name in control:
            continue
        open_pos = source.find("{", m.start(), m.end())
        if open_pos < 0:
            continue
        depth = 0
        quote = None
        escape = False
        end = None
        for i in range(open_pos, len(source)):
            ch = source[i]
            if quote:
                if escape:
                    escape = False
                elif ch == "\\":
                    escape = True
                elif ch == quote:
                    quote = None
                continue
            if ch in ('"', "'"):
                quote = ch
                continue
            if ch == "{":
                depth += 1
            elif ch == "}":
                depth -= 1
                if depth == 0:
                    end = i + 1
                    break
        if end:
            blocks.append((m.start(), end, name, source[m.start():end]))
    return blocks


def compact_java_source(source, budget, terms):
    if len(source) <= budget:
        return source

    sigs = java_signatures(source, max_chars=min(5000, budget // 3))
    blocks = _find_method_blocks(source)
    scored = []
    for pos, end, name, block in blocks:
        low = block[:1200].lower()
        score = 0
        if name.lower() in terms:
            score += 10
        score += sum(1 for t in terms if t in low)
        header = block[: min(500, len(block))]
        if re.search(r"\b(public|protected)\b", header):
            score += 4
        scored.append((score, pos, name, block))
    scored.sort(key=lambda x: (-x[0], x[1]))

    prefix_lines = []
    for raw in source.splitlines()[:120]:
        s = raw.strip()
        if s.startswith(("package ", "import ", "public class ", "class ", "interface ", "enum ")):
            prefix_lines.append(raw)
    pieces = ["/* API SIGNATURES */\n" + sigs]
    if prefix_lines:
        pieces.append("/* HEADER */\n" + "\n".join(prefix_lines))

    used = sum(len(x) for x in pieces)
    for score, pos, name, block in scored:
        if used >= budget:
            break
        room = budget - used
        if room < 500:
            break
        block = compact_text(block, min(room, 5000))
        pieces.append(f"/* METHOD {name} */\n{block}")
        used += len(pieces[-1])

    return compact_text("\n\n".join(pieces), budget)


def api_summary(context, max_chars=7000):
    parts = []
    per = max(1200, max_chars // max(1, len(context["sources"])))
    for item in context["sources"]:
        kind = item.get("kind", "java")
        if kind.startswith("java"):
            body = java_signatures(item["source"], max_chars=per)
        else:
            body = f"resource: {Path(item['path']).name}"
        parts.append(f"[{item['class']} | {kind}]\n{body}")
    return compact_text("\n\n".join(parts), max_chars)


def generation_source(context, max_chars=12000):
    terms = relevance_terms(context)
    n = max(1, len(context["sources"]))
    per = max(2500, max_chars // n)
    parts = []
    for item in context["sources"]:
        kind = item.get("kind", "java")
        if kind.startswith("java"):
            body = compact_java_source(item["source"], per, terms)
        else:
            body = compact_text(item["source"], min(per, 3500))
        parts.append(f"===== {item['class']} ({kind}) =====\n{body}")
    return compact_text("\n\n".join(parts), max_chars)


def compact_project_context(context):
    return (
        "Modified: " + ", ".join(context.get("classes_modified", []))
        + "\nTriggers: " + (", ".join(context.get("trigger_tests", [])) or "none reported")
    )


def compact_bug_summary(context, max_chars=1800):
    info = context.get("defects4j_info", "")
    parts = []
    m = re.search(r"Bug report id:\s*\n([^\n]+)", info)
    if m:
        parts.append("Bug report: " + m.group(1).strip())
    m = re.search(
        r"Root cause in triggering tests:\s*\n(.*?)(?:-{20,}|List of modified sources:)",
        info, flags=re.S
    )
    if m:
        root = compact_text(m.group(1), 1300)
        if root:
            parts.append("Trigger failures:\n" + root)
    if context.get("classes_modified"):
        parts.append("Modified: " + ", ".join(context["classes_modified"]))
    return compact_text("\n".join(parts) or compact_text(info, max_chars), max_chars)


def base_values(context):
    return {
        "PROJECT_ID": context["project"],
        "BUG_ID": str(context["bug_id"]),
        "SOURCE_VERSION": context["source_version"],
        "TARGET_CLASS": ", ".join(context["classes_modified"]),
        "JUNIT_VERSION": context["junit_version"],
        "BUILD_TOOL": context["build_tool"],
        "BUG_SUMMARY": compact_bug_summary(context),
        "API_SUMMARY": api_summary(context, max_chars=4000),
        "PROJECT_CONTEXT_COMPACT": compact_project_context(context),
    }


def fill_template(template, values):
    # Only placeholders that existed in the template before substitution count.
    # This prevents Java literals such as [NULL] or [DEFAULT_BUFFER_SIZE] from
    # being mistaken for prompt placeholders after source insertion.
    required = set(re.findall(r"\[([A-Z][A-Z0-9_]+)\]", template))
    missing = sorted(k for k in required if k not in values)
    if missing:
        raise RuntimeError(f"Missing template values: {missing}")
    result = template
    for key in required:
        result = result.replace(f"[{key}]", str(values[key]))
    return result


def compact_error_output(log, failing, max_chars=3000):
    keep = []
    patterns = re.compile(
        r"(?i)(error|fail|exception|assert|expected|actual|cannot find|symbol:|location:|"
        r"incompatible|no suitable|package .* does not exist|compilation|--- )"
    )
    lines = (log or "").splitlines()
    for i, line in enumerate(lines):
        if patterns.search(line):
            for j in range(max(0, i - 1), min(len(lines), i + 4)):
                if lines[j] not in keep:
                    keep.append(lines[j])
        if len("\n".join(keep)) >= max_chars:
            break
    if failing:
        keep.append("Failing generated tests: " + ", ".join(failing))
    if not keep:
        keep = lines[-40:]
    return compact_text("\n".join(keep), max_chars)


def test_suite_summary(java, max_chars=1600):
    names = re.findall(r"@Test(?:\([^)]*\))?\s+(?:public\s+)?void\s+([A-Za-z_$][\w$]*)", java)
    asserts = []
    for line in java.splitlines():
        s = line.strip()
        if re.search(r"\b(assert\w*|fail|expect\w*)\s*\(", s):
            asserts.append(s[:240])
    text = "Test methods: " + (", ".join(names) if names else "unknown")
    if asserts:
        text += "\nRepresentative assertions:\n" + "\n".join(asserts[:12])
    return compact_text(text, max_chars)


def coverage_source_snippets(context, coverage_report, max_chars=3500):
    nums = []
    for line in coverage_report.splitlines():
        if line.startswith("Uncovered lines:") or line.startswith("Partially covered branch lines:"):
            nums.extend(int(x) for x in re.findall(r"\b\d+\b", line))
    nums = list(dict.fromkeys(nums))[:24]
    if not nums:
        return "(no line-level coverage gaps available)"

    parts = []
    for item in context["sources"]:
        if not item.get("kind", "java").startswith("java"):
            continue
        lines = item["source"].splitlines()
        snippets = []
        for n in nums:
            if 1 <= n <= len(lines):
                lo, hi = max(1, n - 2), min(len(lines), n + 2)
                block = "\n".join(f"{i}: {lines[i-1]}" for i in range(lo, hi + 1))
                snippets.append(block)
        if snippets:
            parts.append(f"[{item['class']}]\n" + "\n---\n".join(snippets))
    return compact_text("\n\n".join(parts), max_chars) or "(no matching buggy-source snippets)"

# ---------------------------- provider calls ----------------------------

def call_intelsphere(prompt, model):
    key = os.environ.get("KKU_INTELSPHERE_API_KEY")
    if not key:
        raise RuntimeError("KKU_INTELSPHERE_API_KEY is not set.")

    # Intentionally NO max_tokens field.
    payload = {
        "model": model,
        "messages": [{"role": "user", "content": prompt}],
        "temperature": 0,
    }

    request = urllib.request.Request(
        INTELSPHERE_URL,
        data=json.dumps(payload).encode("utf-8"),
        headers={
            "Authorization": f"Bearer {key}",
            "Content-Type": "application/json",
        },
        method="POST",
    )

    started = time.monotonic()
    try:
        with urllib.request.urlopen(request, timeout=1200) as response:
            data = json.loads(response.read().decode("utf-8"))
    except urllib.error.HTTPError as exc:
        body = exc.read().decode("utf-8", errors="replace")
        lowered = body.lower()
        if (
            "daily limit" in lowered
            or "quota" in lowered
            or "reached daily" in lowered
        ):
            raise QuotaExhausted(
                f"IntelSphere daily quota reached (HTTP {exc.code}):\n{body}"
            ) from exc
        raise RuntimeError(f"IntelSphere HTTP {exc.code}:\n{body}") from exc

    elapsed = round(time.monotonic() - started, 3)
    choices = data.get("choices") or []
    if not choices:
        raise RuntimeError("IntelSphere returned no choices.")

    content = (choices[0].get("message") or {}).get("content") or ""
    if not content.strip():
        raise RuntimeError("IntelSphere returned an empty assistant response.")

    return content, data, elapsed


def call_copilot(prompt, model):
    if not model:
        raise RuntimeError("Copilot requires --model for reproducibility.")

    p = cmd([
        "copilot",
        "-s",
        "-p", prompt,
        f"--model={model}",
        "--no-ask-user",
        "--no-custom-instructions",
        "--output-format=text",
    ], cwd=ROOT, check=False)

    if p.returncode != 0:
        raise RuntimeError(
            f"Copilot CLI failed ({p.returncode}).\n"
            f"STDOUT:\n{p.stdout}\nSTDERR:\n{p.stderr}"
        )
    if not p.stdout.strip():
        raise RuntimeError("Copilot returned an empty response.")

    return p.stdout.strip(), {
        "provider": "GitHub Copilot CLI",
        "model": model,
        "stderr": p.stderr,
    }, None


def call_provider(provider, prompt, model):
    if provider == "chatgpt":
        return call_intelsphere(prompt, model or "gpt-5.6-terra")
    return call_copilot(prompt, model)


def metadata_for_response(provider, raw, elapsed, requested_model):
    if provider == "chatgpt":
        usage = raw.get("usage") or {}
        quota = raw.get("model_quota") or {}
        choices = raw.get("choices") or []
        finish_reason = choices[0].get("finish_reason") if choices else None
        return {
            "provider": raw.get("provider"),
            "model": raw.get("model"),
            "finish_reason": finish_reason,
            "elapsed_seconds": elapsed,
            "prompt_tokens": usage.get("prompt_tokens"),
            "completion_tokens": usage.get("completion_tokens"),
            "total_tokens": usage.get("total_tokens"),
            "daily_quota_tokens": quota.get("daily_quota_tokens"),
            "daily_usage_tokens": quota.get("daily_usage_tokens"),
            "daily_remaining_tokens": quota.get("daily_remaining_tokens"),
        }

    return {
        "provider": raw.get("provider"),
        "model": raw.get("model") or requested_model,
        "finish_reason": None,
        "elapsed_seconds": elapsed,
    }


def ensure_complete_response(provider, meta, stage):
    if provider != "chatgpt":
        return
    reason = meta.get("finish_reason")
    if reason not in (None, "stop"):
        raise OutputIncomplete(
            f"Prompt {stage} did not finish normally: finish_reason={reason}"
        )


def save_ai_result(result_dir, stage_prefix, prompt, content, raw, meta):
    # Prompt is already preserved under AI*/Prompt/...; avoid duplicating it in Result.
    # Preserve the raw assistant text and compact measured metadata only.
    result_dir.mkdir(parents=True, exist_ok=True)
    (result_dir / f"{stage_prefix}_raw_response.md").write_text(
        content, encoding="utf-8"
    )
    (result_dir / f"{stage_prefix}_metadata.json").write_text(
        json.dumps(meta, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8",
    )


# ---------------------------- Java extraction ----------------------------

def extract_java(response):
    fenced = re.findall(
        r"```(?:java)?\s*\n(.*?)```",
        response,
        flags=re.I | re.S,
    )
    candidates = fenced if fenced else [response]

    for text in candidates:
        if re.search(r"\b(?:public\s+)?class\s+\w+", text):
            starts = [
                pos for pos in (
                    text.find("package "),
                    text.find("import "),
                    text.find("public class "),
                    text.find("class "),
                )
                if pos >= 0
            ]
            if starts:
                text = text[min(starts):]
            text = text.strip() + "\n"

            # A cheap truncation guard before compilation.
            if text.count("{") != text.count("}"):
                raise OutputIncomplete(
                    "Extracted Java has unbalanced braces; response may be truncated."
                )
            return text

    raise OutputIncomplete(
        "Could not extract a complete Java Test Class from the AI response."
    )


def java_identity(java):
    package_match = re.search(
        r"(?m)^\s*package\s+([A-Za-z_][\w.]*)\s*;",
        java,
    )
    class_match = re.search(
        r"\bpublic\s+class\s+([A-Za-z_]\w*)\b",
        java,
    )
    if not class_match:
        class_match = re.search(
            r"\bclass\s+([A-Za-z_]\w*)\b",
            java,
        )
    if not class_match:
        raise OutputIncomplete("Generated Java has no identifiable class name.")

    package = package_match.group(1) if package_match else ""
    simple = class_match.group(1)
    fqcn = f"{package}.{simple}" if package else simple
    return package, simple, fqcn


# ---------------------------- validation and coverage ----------------------------

def make_suite_archive(java_path, fqcn, archive_path):
    relative = Path(*fqcn.split(".")).with_suffix(".java")
    with tarfile.open(archive_path, "w:bz2") as tf:
        tf.add(java_path, arcname=str(relative))


def parse_failing_tests(path, fqcn):
    if not path.is_file():
        return []
    text = path.read_text(encoding="utf-8", errors="replace")
    return sorted(set(re.findall(
        rf"(?:^|\n)---\s+({re.escape(fqcn)}::[^\s]+)",
        text,
    )))


def validate_on_fixed(project, bug, java_path, fqcn, output_dir, stage_prefix):
    fixed = ensure_checkout(project, bug, "f")
    output_dir.mkdir(parents=True, exist_ok=True)
    (fixed / "failing_tests").unlink(missing_ok=True)

    with tempfile.TemporaryDirectory(prefix="sqa-suite-") as td:
        archive = Path(td) / "suite.tar.bz2"
        make_suite_archive(java_path, fqcn, archive)
        started = time.monotonic()
        p = cmd(
            ["defects4j", "test", "-w", str(fixed), "-s", str(archive)],
            check=False,
        )
        elapsed = round(time.monotonic() - started, 3)

    log = (p.stdout or "") + ("\n" + p.stderr if p.stderr else "")
    compile_ok = bool(re.search(r"Running ant \(compile\.gen\.tests\)\.* OK", log))
    run_ok = bool(re.search(r"Running ant \(run\.gen\.tests\)\.* OK", log))
    failing = parse_failing_tests(fixed / "failing_tests", fqcn)
    valid = compile_ok and run_ok and not failing

    validation = {
        "fixed_workspace": str(fixed),
        "test_exit_code": p.returncode,
        "compile_generated_tests_ok": compile_ok,
        "run_generated_tests_ok": run_ok,
        "fixed_failing_generated_tests": failing,
        "valid_on_fixed": valid,
        "elapsed_seconds": elapsed,
        "suite_sha256": hashlib.sha256(java_path.read_bytes()).hexdigest(),
    }
    (output_dir / f"{stage_prefix}_validation.json").write_text(
        json.dumps(validation, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8",
    )
    if not valid:
        excerpt = compact_error_output(log, failing)
        (output_dir / f"{stage_prefix}_error_excerpt.txt").write_text(
            excerpt + "\n", encoding="utf-8"
        )
    return validation

def _fmt_line_list(values, limit=30):
    if not values:
        return "(none)"
    shown = values[:limit]
    suffix = f" ... (+{len(values)-limit} more)" if len(values) > limit else ""
    return ", ".join(shown) + suffix


def summarize_coverage_xml(path, modified_classes):
    if not path.is_file():
        return ""
    try:
        root = ET.parse(path).getroot()
    except Exception:
        return ""

    modified = set(modified_classes)
    lines = []
    for cls in root.iter("class"):
        name = cls.attrib.get("name", "").replace("/", ".")
        if modified and name not in modified:
            # Allow nested/top-level $ names and resource-related classes to pass
            # only when exact matching is unavailable; keeping prompt compact wins.
            continue
        uncovered, partial = [], []
        for line in cls.iter("line"):
            number = line.attrib.get("number")
            hits = line.attrib.get("hits")
            branch = line.attrib.get("branch", "").lower()
            cond = line.attrib.get("condition-coverage", "")
            try:
                hit_count = int(hits or "0")
            except ValueError:
                hit_count = 0
            if hit_count == 0 and number:
                uncovered.append(number)
            elif branch == "true" and cond and "100%" not in cond and number:
                partial.append(f"{number} ({cond})")
        lines.append(f"Class: {name}")
        lines.append("Uncovered lines: " + _fmt_line_list(uncovered))
        lines.append("Partially covered branch lines: " + _fmt_line_list(partial))
    return "\n".join(lines)


def measure_fixed_coverage(project, bug, java_path, fqcn, context, result_dir):
    report_path = result_dir / "04_coverage_report.txt"
    if report_path.is_file():
        return report_path.read_text(encoding="utf-8", errors="replace")

    fixed = ensure_checkout(project, bug, "f")
    for stale in ("summary.csv", "coverage.xml"):
        (fixed / stale).unlink(missing_ok=True)

    with tempfile.TemporaryDirectory(prefix="sqa-coverage-") as td:
        archive = Path(td) / "suite.tar.bz2"
        make_suite_archive(java_path, fqcn, archive)
        p = cmd(
            ["defects4j", "coverage", "-w", str(fixed), "-s", str(archive)],
            check=False,
        )

    log = (p.stdout or "") + ("\n" + p.stderr if p.stderr else "")
    summary = fixed / "summary.csv"
    if p.returncode != 0 or not summary.is_file():
        (result_dir / "04_coverage_error.log").write_text(log, encoding="utf-8")
        raise RuntimeError(f"Coverage failed for {project}-{bug}")

    summary_text = summary.read_text(encoding="utf-8", errors="replace").strip()
    xml_summary = summarize_coverage_xml(fixed / "coverage.xml", context.get("classes_modified", []))
    report = "Actual coverage summary:\n" + summary_text
    if xml_summary:
        report += "\n\nLine/branch gaps:\n" + xml_summary
    report = compact_text(report, 5000)
    report_path.write_text(report + "\n", encoding="utf-8")
    return report

def extract_extension_members(response):
    stripped = response.strip()
    if stripped == "NO_CHANGE":
        return ""
    fenced = re.findall(r"```(?:java)?\s*\n(.*?)```", response, flags=re.I | re.S)
    body = (fenced[0] if fenced else response).strip()

    # Tolerate an accidental class wrapper by extracting its body.
    class_m = re.search(r"\bclass\s+[A-Za-z_$][\w$]*[^\{]*\{", body)
    if class_m:
        open_pos = body.find("{", class_m.start())
        if body.rstrip().endswith("}"):
            body = body[open_pos + 1: body.rfind("}")].strip()

    # P04 must not rewrite package/import/class structure; new types should use FQNs.
    body = re.sub(r"(?m)^\s*(?:package|import)\s+[^;]+;\s*$", "", body).strip()
    if not body:
        return ""
    if body.count("{") != body.count("}"):
        raise OutputIncomplete("Prompt 04 extension block has unbalanced braces.")
    if "@Test" not in body and "org.junit.Test" not in body:
        raise OutputIncomplete("Prompt 04 returned no @Test method or NO_CHANGE.")
    return body + "\n"


def merge_extension_members(java, members):
    if not members:
        return java
    pos = java.rfind("}")
    if pos < 0:
        raise OutputIncomplete("Current Java suite has no closing class brace.")
    merged = java[:pos].rstrip() + "\n\n" + members.rstrip() + "\n" + java[pos:]
    if merged.count("{") != merged.count("}"):
        raise OutputIncomplete("Merged Prompt 04 Java has unbalanced braces.")
    return merged


def apply_unified_diff(original, response):
    """Apply a compact model-produced unified diff; tolerate full-Java fallback."""
    fenced = re.findall(r"```(?:diff|patch|java)?\s*\n(.*?)```", response, flags=re.I | re.S)
    patch_text = (fenced[0] if fenced else response).strip()

    if "@@" not in patch_text:
        # If the model disobeys and returns a complete class, still preserve the
        # one-repair-round result rather than spending another request.
        return extract_java(response)

    orig = original.splitlines(keepends=True)
    plines = patch_text.splitlines()
    out = []
    cursor = 0
    i = 0
    saw_hunk = False
    while i < len(plines):
        line = plines[i]
        m = re.match(r"@@\s+-(\d+)(?:,(\d+))?\s+\+(\d+)(?:,(\d+))?\s+@@", line)
        if not m:
            i += 1
            continue
        saw_hunk = True
        old_start = int(m.group(1)) - 1
        if old_start < cursor or old_start > len(orig):
            raise OutputIncomplete("Prompt 03 diff has invalid hunk position.")
        out.extend(orig[cursor:old_start])
        cursor = old_start
        i += 1
        while i < len(plines) and not plines[i].startswith("@@"):
            d = plines[i]
            if d.startswith("--- ") or d.startswith("+++ "):
                i += 1
                continue
            if d.startswith("\\ No newline"):
                i += 1
                continue
            if not d:
                # Empty diff line should carry a prefix; tolerate as context blank.
                prefix, payload = " ", ""
            else:
                prefix, payload = d[0], d[1:]
            if prefix == " ":
                if cursor >= len(orig):
                    raise OutputIncomplete("Prompt 03 diff context exceeds source.")
                if orig[cursor].rstrip("\r\n") != payload.rstrip("\r\n"):
                    raise OutputIncomplete("Prompt 03 diff context does not match generated test.")
                out.append(orig[cursor]); cursor += 1
            elif prefix == "-":
                if cursor >= len(orig):
                    raise OutputIncomplete("Prompt 03 diff deletion exceeds source.")
                if orig[cursor].rstrip("\r\n") != payload.rstrip("\r\n"):
                    raise OutputIncomplete("Prompt 03 diff deletion does not match generated test.")
                cursor += 1
            elif prefix == "+":
                out.append(payload + "\n")
            else:
                # End of diff metadata/prose.
                break
            i += 1
    if not saw_hunk:
        raise OutputIncomplete("Prompt 03 returned neither a unified diff nor a complete Java class.")
    out.extend(orig[cursor:])
    repaired = "".join(out)
    if repaired.count("{") != repaired.count("}"):
        raise OutputIncomplete("Prompt 03 repaired Java has unbalanced braces.")
    return repaired


# ---------------------------- stage helpers ----------------------------

def ai_root_for(provider):
    return ROOT / (
        "AI1_ChatGPT"
        if provider == "chatgpt"
        else "AI2_GitHubCopilot"
    )


def case_paths(provider, project, bug):
    case = f"{project}-{bug}"
    ai_root = ai_root_for(provider)
    return {
        "case": case,
        "ai_root": ai_root,
        "prompt_dir": ai_root / "Prompt" / case / f"run-{RUN_ID}",
        "result_dir": ai_root / "Result" / case / f"run-{RUN_ID}",
        "test_root": ai_root / "TestCode" / case / f"run-{RUN_ID}",
    }


def send_prompt(args, prompt, result_dir, prefix):
    content, raw, elapsed = call_provider(args.provider, prompt, args.model)
    meta = metadata_for_response(args.provider, raw, elapsed, args.model)
    save_ai_result(result_dir, prefix, prompt, content, raw, meta)
    ensure_complete_response(args.provider, meta, prefix)
    return content, meta


def _read_stage01_analysis(result_dir):
    p = result_dir / "01_raw_response.md"
    return compact_text(p.read_text(encoding="utf-8", errors="replace"), 1600) if p.is_file() else ""


def stage01(args, context, context_path):
    paths = case_paths(args.provider, args.project, args.bug)
    values = base_values(context)
    template_path = paths["ai_root"] / "Prompt" / "01_analyze_context.txt"
    prompt = fill_template(template_path.read_text(encoding="utf-8"), values)
    paths["prompt_dir"].mkdir(parents=True, exist_ok=True)
    (paths["prompt_dir"] / "01_analyze_context.txt").write_text(prompt, encoding="utf-8")

    print(f"[{paths['case']}] Prompt 01 compact", flush=True)
    if args.dry_run:
        print(f"P01 chars={len(prompt)}", flush=True)
        return None
    content, meta = send_prompt(args, prompt, paths["result_dir"], "01")
    print(json.dumps(meta, ensure_ascii=False), flush=True)
    return meta


def stage02(args, context):
    paths = case_paths(args.provider, args.project, args.bug)
    values = base_values(context)
    values["ANALYSIS_SUMMARY"] = _read_stage01_analysis(paths["result_dir"])
    values["GENERATION_SOURCE"] = generation_source(context)
    template_path = paths["ai_root"] / "Prompt" / "02_generate_suite.txt"
    prompt = fill_template(template_path.read_text(encoding="utf-8"), values)
    paths["prompt_dir"].mkdir(parents=True, exist_ok=True)
    (paths["prompt_dir"] / "02_generate_suite.txt").write_text(prompt, encoding="utf-8")

    print(f"[{paths['case']}] Prompt 02 compact", flush=True)
    if args.dry_run:
        print(f"P02 chars={len(prompt)}", flush=True)
        return None
    content, meta = send_prompt(args, prompt, paths["result_dir"], "02")
    java = extract_java(content)
    package, simple, fqcn = java_identity(java)

    test_dir = paths["test_root"] / "generated"
    test_dir.mkdir(parents=True, exist_ok=True)
    java_path = test_dir / f"{simple}.java"
    java_path.write_text(java, encoding="utf-8")
    identity = {"package": package, "class_name": simple, "fqcn": fqcn, "source": str(java_path)}
    (paths["result_dir"] / "02_test_identity.json").write_text(
        json.dumps(identity, indent=2, ensure_ascii=False) + "\n", encoding="utf-8"
    )
    validation = validate_on_fixed(args.project, args.bug, java_path, fqcn, paths["result_dir"], "02")
    print(json.dumps(meta, ensure_ascii=False), flush=True)
    print(f"[{paths['case']}] P02 fixed-valid={validation['valid_on_fixed']}", flush=True)
    return validation


def stage03(args, context):
    paths = case_paths(args.provider, args.project, args.bug)
    result_dir = paths["result_dir"]
    identity = json.loads((result_dir / "02_test_identity.json").read_text(encoding="utf-8"))
    previous = json.loads((result_dir / "02_validation.json").read_text(encoding="utf-8"))
    generated_path = Path(identity["source"])
    generated_test = generated_path.read_text(encoding="utf-8", errors="replace")
    excerpt_path = result_dir / "02_error_excerpt.txt"
    error_excerpt = excerpt_path.read_text(encoding="utf-8", errors="replace") if excerpt_path.is_file() else "validation failed"

    values = base_values(context)
    values["API_SUMMARY"] = api_summary(context, max_chars=2500)
    values["GENERATED_TEST"] = generated_test
    values["ERROR_OUTPUT"] = compact_text(error_excerpt, 3000)
    template_path = paths["ai_root"] / "Prompt" / "03_repair_suite.txt"
    prompt = fill_template(template_path.read_text(encoding="utf-8"), values)
    paths["prompt_dir"].mkdir(parents=True, exist_ok=True)
    (paths["prompt_dir"] / "03_repair_suite.txt").write_text(prompt, encoding="utf-8")

    print(f"[{paths['case']}] Prompt 03 compact (single repair)", flush=True)
    if args.dry_run:
        print(f"P03 chars={len(prompt)}", flush=True)
        return None
    content, meta = send_prompt(args, prompt, result_dir, "03")
    # Optimized v3: Prompt 03 returns one complete corrected Java class.
    # The previous unified-diff format saved some completion tokens but proved
    # brittle in practice because harmless context/whitespace drift made the
    # deterministic patch applier reject otherwise usable repairs.  A complete
    # class costs slightly more only on cases that need the single repair round,
    # while eliminating that parser failure mode.
    java = extract_java(content)
    package, simple, fqcn = java_identity(java)
    repaired_dir = paths["test_root"] / "repaired"
    repaired_dir.mkdir(parents=True, exist_ok=True)
    java_path = repaired_dir / f"{simple}.java"
    java_path.write_text(java, encoding="utf-8")
    repaired_identity = {
        "package": package, "class_name": simple, "fqcn": fqcn,
        "source": str(java_path), "repaired_from": str(generated_path),
    }
    (result_dir / "03_test_identity.json").write_text(
        json.dumps(repaired_identity, indent=2, ensure_ascii=False) + "\n", encoding="utf-8"
    )
    validation = validate_on_fixed(args.project, args.bug, java_path, fqcn, result_dir, "03")
    print(json.dumps(meta, ensure_ascii=False), flush=True)
    print(f"[{paths['case']}] P03 fixed-valid={validation['valid_on_fixed']}", flush=True)
    return validation


def _reuse_valid_suite_for_p04(args, paths, current_java, current_fqcn, reason, meta=None):
    """Deterministically reject a bad P04 extension and keep the valid pre-P04 suite.

    Prompt 04 is an optional coverage-extension step.  The suite entering P04 has
    already passed validation on the fixed version.  A malformed or non-compiling
    extension must not destroy that previously valid suite and must not trigger an
    extra AI request.  We record the rejected extension and continue evaluation
    with the pre-P04 suite.
    """
    result_dir = paths["result_dir"]
    current_text = current_java.read_text(encoding="utf-8", errors="replace")
    package, simple, fqcn = java_identity(current_text)
    final_dir = paths["test_root"] / "coverage-extended"
    final_dir.mkdir(parents=True, exist_ok=True)
    java_path = final_dir / f"{simple}.java"
    java_path.write_text(current_text, encoding="utf-8")

    identity = {
        "package": package,
        "class_name": simple,
        "fqcn": fqcn,
        "source": str(java_path),
        "extended_from": str(current_java),
        "prompt04_no_change": True,
        "prompt04_extension_applied": False,
        "prompt04_extension_rejected": True,
        "prompt04_rejection_reason": str(reason),
    }
    (result_dir / "04_test_identity.json").write_text(
        json.dumps(identity, indent=2, ensure_ascii=False) + "\n", encoding="utf-8"
    )

    validation = {
        "fixed_workspace": str(ensure_checkout(args.project, args.bug, "f")),
        "test_exit_code": 0,
        "compile_generated_tests_ok": True,
        "run_generated_tests_ok": True,
        "fixed_failing_generated_tests": [],
        "valid_on_fixed": True,
        "elapsed_seconds": 0,
        "suite_sha256": hashlib.sha256(java_path.read_bytes()).hexdigest(),
        "reused_prior_validation": True,
        "prompt04_extension_applied": False,
        "prompt04_extension_rejected": True,
        "prompt04_rejection_reason": str(reason),
    }
    (result_dir / "04_validation.json").write_text(
        json.dumps(validation, indent=2, ensure_ascii=False) + "\n", encoding="utf-8"
    )
    (result_dir / "04_extension_status.json").write_text(
        json.dumps({
            "applied": False,
            "rejected": True,
            "reason": str(reason),
        }, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8",
    )
    return validation


def stage04(args, context, current_java, current_fqcn):
    paths = case_paths(args.provider, args.project, args.bug)
    result_dir = paths["result_dir"]
    coverage_report = measure_fixed_coverage(
        args.project, args.bug, current_java, current_fqcn, context, result_dir
    )
    current_text = current_java.read_text(encoding="utf-8", errors="replace")
    values = base_values(context)
    values["API_SUMMARY"] = api_summary(context, max_chars=2500)
    values["TEST_SUMMARY"] = test_suite_summary(current_text)
    values["COVERAGE_REPORT"] = compact_text(coverage_report, 3500)
    values["COVERAGE_SOURCE_SNIPPETS"] = coverage_source_snippets(context, coverage_report)

    template_path = paths["ai_root"] / "Prompt" / "04_extend_coverage.txt"
    prompt = fill_template(template_path.read_text(encoding="utf-8"), values)
    paths["prompt_dir"].mkdir(parents=True, exist_ok=True)
    (paths["prompt_dir"] / "04_extend_coverage.txt").write_text(prompt, encoding="utf-8")

    print(f"[{paths['case']}] Prompt 04 compact extension", flush=True)
    if args.dry_run:
        print(f"P04 chars={len(prompt)}", flush=True)
        return None
    content, meta = send_prompt(args, prompt, result_dir, "04")

    # Parse failures are a rejected *extension*, not failure of the already-valid
    # suite.  No extra model call is made.
    try:
        members = extract_extension_members(content)
        java = merge_extension_members(current_text, members)
        package, simple, fqcn = java_identity(java)
    except OutputIncomplete as exc:
        validation = _reuse_valid_suite_for_p04(
            args, paths, current_java, current_fqcn, f"parse: {exc}", meta
        )
        print(json.dumps(meta, ensure_ascii=False), flush=True)
        print(f"[{paths['case']}] P04 rejected; reused valid pre-P04 suite: {exc}", flush=True)
        return validation

    final_dir = paths["test_root"] / "coverage-extended"
    final_dir.mkdir(parents=True, exist_ok=True)
    java_path = final_dir / f"{simple}.java"
    java_path.write_text(java, encoding="utf-8")
    identity = {
        "package": package, "class_name": simple, "fqcn": fqcn,
        "source": str(java_path), "extended_from": str(current_java),
        "prompt04_no_change": not bool(members),
        "prompt04_extension_applied": bool(members),
        "prompt04_extension_rejected": False,
    }
    (result_dir / "04_test_identity.json").write_text(
        json.dumps(identity, indent=2, ensure_ascii=False) + "\n", encoding="utf-8"
    )

    if not members:
        # Explicit NO_CHANGE: current suite was already fixed-valid.
        validation = {
            "fixed_workspace": str(ensure_checkout(args.project, args.bug, "f")),
            "test_exit_code": 0,
            "compile_generated_tests_ok": True,
            "run_generated_tests_ok": True,
            "fixed_failing_generated_tests": [],
            "valid_on_fixed": True,
            "elapsed_seconds": 0,
            "suite_sha256": hashlib.sha256(java_path.read_bytes()).hexdigest(),
            "reused_prior_validation": True,
            "prompt04_extension_applied": False,
            "prompt04_extension_rejected": False,
        }
        (result_dir / "04_validation.json").write_text(
            json.dumps(validation, indent=2, ensure_ascii=False) + "\n", encoding="utf-8"
        )
        (result_dir / "04_extension_status.json").write_text(
            json.dumps({"applied": False, "rejected": False, "reason": "NO_CHANGE"}, indent=2) + "\n",
            encoding="utf-8",
        )
    else:
        validation = validate_on_fixed(args.project, args.bug, java_path, fqcn, result_dir, "04")
        if not validation.get("valid_on_fixed"):
            # Invalid additions are discarded deterministically.  The original
            # suite is retained, preserving the one-call P04 budget.
            validation = _reuse_valid_suite_for_p04(
                args, paths, current_java, current_fqcn,
                "extension did not compile/run on fixed version", meta
            )
        else:
            (result_dir / "04_extension_status.json").write_text(
                json.dumps({"applied": True, "rejected": False, "reason": None}, indent=2) + "\n",
                encoding="utf-8",
            )

    print(json.dumps(meta, ensure_ascii=False), flush=True)
    print(f"[{paths['case']}] P04 fixed-valid={validation['valid_on_fixed']}", flush=True)
    return validation

# ---------------------------- final evaluation ----------------------------

def run_final_evaluation(provider, project, bug, java_path, fqcn):
    case = f"{project}-{bug}"
    output = ROOT / "Experiment" / "evaluations" / case / f"{provider}-run-{RUN_ID}-final"
    result_json = output / "result.json"

    if result_json.is_file():
        return json.loads(result_json.read_text(encoding="utf-8"))

    if output.exists() and any(output.iterdir()):
        stamp = datetime.now().strftime("%Y%m%d-%H%M%S")
        failed = output.with_name(output.name + f"-incomplete-{stamp}")
        output.rename(failed)

    output.mkdir(parents=True, exist_ok=True)

    evaluator = ROOT / "Experiment" / "evaluate_suite.py"
    with acquire_d4j_slot():
        p = cmd([
            sys.executable,
            str(evaluator),
            "--project", project,
            "--bug", str(bug),
            "--class-name", fqcn,
            "--source", str(java_path),
            "--output", str(output),
        ], check=False)

    (output / "runner_stdout.log").write_text(
        (p.stdout or "") + ("\n" + p.stderr if p.stderr else ""),
        encoding="utf-8",
    )

    if p.returncode != 0 or not result_json.is_file():
        raise RuntimeError(
            f"Final evaluator failed for {case}; see {output / 'runner_stdout.log'}"
        )

    return json.loads(result_json.read_text(encoding="utf-8"))


def token_summary(result_dir):
    rows = []
    totals = {
        "prompt_tokens": 0,
        "completion_tokens": 0,
        "total_tokens": 0,
    }

    for stage in ("01", "02", "03", "04"):
        path = result_dir / f"{stage}_metadata.json"
        if not path.is_file():
            continue
        data = json.loads(path.read_text(encoding="utf-8"))
        row = {"stage": stage}
        for key in totals:
            val = data.get(key)
            row[key] = val
            if isinstance(val, int):
                totals[key] += val
        row["daily_remaining_tokens"] = data.get("daily_remaining_tokens")
        rows.append(row)

    return {"stages": rows, "totals": totals}


def write_case_status(paths, status, extra=None):
    result_dir = paths["result_dir"]
    result_dir.mkdir(parents=True, exist_ok=True)
    data = {
        "case": paths["case"],
        "status": status,
        "run_id": RUN_ID,
        "worker": WORKER_ID,
        "updated_at": now_iso(),
    }
    if extra:
        data.update(extra)
    data["tokens"] = token_summary(result_dir)
    total = data["tokens"]["totals"].get("total_tokens", 0)
    data["token_target"] = 13000
    data["over_token_target"] = bool(total and total > 13000)
    (result_dir / "case_status.json").write_text(
        json.dumps(data, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8",
    )
    return data


def read_case_status(paths):
    path = paths["result_dir"] / "case_status.json"
    if not path.is_file():
        return None
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except Exception:
        return None


# ---------------------------- end-to-end case ----------------------------

def run_case(args, project, bug):
    local = argparse.Namespace(**vars(args))
    local.project = project
    local.bug = bug
    paths = case_paths(local.provider, project, bug)

    status = read_case_status(paths)
    if args.resume and status and status.get("status") in {
        "DONE",
        "INVALID_AFTER_REPAIR",
        "INVALID_AFTER_PROMPT04",
        "OUTPUT_INCOMPLETE",
    }:
        print(
            f"[{paths['case']}] SKIP {status['status']}",
            flush=True,
        )
        return status

    result_dir = paths["result_dir"]

    try:
        context, context_path = load_or_build_context(project, bug)

        # Prompt 01: analysis artifact.
        if not (result_dir / "01_metadata.json").is_file():
            stage01(local, context, context_path)

        # Prompt 02: initial generated suite.
        if not (result_dir / "02_validation.json").is_file():
            stage02(local, context)

        p02_validation = json.loads(
            (result_dir / "02_validation.json").read_text(encoding="utf-8")
        )

        if p02_validation.get("valid_on_fixed"):
            identity_path = result_dir / "02_test_identity.json"
        else:
            # Exactly one repair attempt.
            if not (result_dir / "03_validation.json").is_file():
                stage03(local, context)

            p03_validation = json.loads(
                (result_dir / "03_validation.json").read_text(encoding="utf-8")
            )
            if not p03_validation.get("valid_on_fixed"):
                return write_case_status(
                    paths,
                    "INVALID_AFTER_REPAIR",
                    {
                        "fixed_failing": p03_validation.get(
                            "fixed_failing_generated_tests", []
                        )
                    },
                )
            identity_path = result_dir / "03_test_identity.json"

        current_identity = json.loads(
            identity_path.read_text(encoding="utf-8")
        )
        current_java = Path(current_identity["source"])
        current_fqcn = current_identity["fqcn"]

        # Prompt 04: only once the current suite is fixed-valid.
        if not (result_dir / "04_validation.json").is_file():
            stage04(local, context, current_java, current_fqcn)

        p04_validation = json.loads(
            (result_dir / "04_validation.json").read_text(encoding="utf-8")
        )
        if not p04_validation.get("valid_on_fixed"):
            return write_case_status(
                paths,
                "INVALID_AFTER_PROMPT04",
                {
                    "fixed_failing": p04_validation.get(
                        "fixed_failing_generated_tests", []
                    )
                },
            )

        final_identity = json.loads(
            (result_dir / "04_test_identity.json").read_text(encoding="utf-8")
        )
        final_java = Path(final_identity["source"])
        final_fqcn = final_identity["fqcn"]

        final = run_final_evaluation(
            local.provider,
            project,
            bug,
            final_java,
            final_fqcn,
        )

        return write_case_status(
            paths,
            "DONE",
            {
                "fault_detected": final.get("fault_detected"),
                "valid_on_fixed": final.get("valid_on_fixed"),
                "detected_tests": final.get("detected_tests", []),
                "fixed_coverage": final.get("fixed_coverage"),
                "buggy_coverage": final.get("buggy_coverage"),
                "final_result": str(
                    ROOT / "Experiment" / "evaluations"
                    / paths["case"] / f"{local.provider}-run-{RUN_ID}-final"
                    / "result.json"
                ),
            },
        )

    except QuotaExhausted:
        write_case_status(paths, "QUOTA_PAUSED")
        raise
    except OutputIncomplete as exc:
        return write_case_status(
            paths,
            "OUTPUT_INCOMPLETE",
            {"error": str(exc)},
        )
    except Exception as exc:
        return write_case_status(
            paths,
            "ERROR",
            {"error": str(exc)},
        )


TERMINAL_STATUSES = {
    "DONE",
    "INVALID_AFTER_REPAIR",
    "INVALID_AFTER_PROMPT04",
    "OUTPUT_INCOMPLETE",
    "ERROR",
}

# Outcomes from an older run that count as already measured and should not be
# regenerated when the user wants to prioritize unfinished work. Infrastructure
# states (ERROR/QUOTA_PAUSED) are intentionally NOT included, so they are retried.
PRIOR_MEASURED_STATUSES = {
    "DONE",
    "INVALID_AFTER_REPAIR",
    "INVALID_AFTER_PROMPT04",
    "OUTPUT_INCOMPLETE",
}


def prior_run_case_status(provider, project, bug, prior_run_id):
    if not prior_run_id:
        return None
    ai_root = ai_root_for(provider)
    path = ai_root / "Result" / f"{project}-{bug}" / f"run-{prior_run_id}" / "case_status.json"
    if not path.is_file():
        return None
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except Exception:
        return None


def already_measured_in_prior_run(args, project, bug):
    prior = prior_run_case_status(
        args.provider, project, bug, getattr(args, "skip_terminal_from_run", None)
    )
    return bool(prior and prior.get("status") in PRIOR_MEASURED_STATUSES)


def queue_case_status(provider, project, bug):
    return read_case_status(case_paths(provider, project, bug))


def claim_path(provider, case):
    return (
        RUNTIME_ROOT
        / f"run-{RUN_ID}"
        / "claims"
        / provider
        / f"{case}.claim"
    )


def try_claim_case(provider, case):
    path = claim_path(provider, case)
    path.parent.mkdir(parents=True, exist_ok=True)

    _cleanup_dead_lock(path)
    try:
        path.mkdir()
    except FileExistsError:
        return None

    owner = {
        "pid": os.getpid(),
        "worker": WORKER_ID,
        "host": socket.gethostname(),
        "claimed_at": now_iso(),
        "case": case,
    }
    (path / "owner.json").write_text(
        json.dumps(owner, indent=2) + "\n",
        encoding="utf-8",
    )
    return path


def release_claim(path):
    if not path:
        return
    try:
        shutil.rmtree(path)
    except FileNotFoundError:
        pass


def build_queue(projects):
    queue = []
    for project in projects:
        for bug in project_bug_ids(project):
            queue.append((project, bug))
    return queue


def _parse_case_list(raw):
    queue = []
    for item in (raw or "").split(","):
        item = item.strip()
        if not item:
            continue
        m = re.fullmatch(r"(.+)-(\d+)", item)
        if not m:
            raise RuntimeError(f"Invalid case in --queue-cases: {item}")
        queue.append((m.group(1), int(m.group(2))))
    return queue


def _run_queue(args, queue, label):
    print(
        f"[{WORKER_ID}] {label}: {len(queue)} bugs; D4J slots={D4J_SLOTS}; run={RUN_ID}",
        flush=True,
    )
    while True:
        claimed_work = False
        for project, bug in queue:
            case = f"{project}-{bug}"
            status = queue_case_status(args.provider, project, bug)
            if (
                status
                and status.get("status") in TERMINAL_STATUSES
                and status.get("status") not in {"ERROR", "QUOTA_PAUSED"}
            ):
                continue
            if already_measured_in_prior_run(args, project, bug):
                continue
            claim = try_claim_case(args.provider, case)
            if claim is None:
                continue
            claimed_work = True
            print(f"\n[{WORKER_ID}] CLAIMED {case}", flush=True)
            try:
                row = run_case(args, project, bug)
                total = (row.get("tokens") or {}).get("totals", {}).get("total_tokens")
                mark = " OVER-13K" if isinstance(total, int) and total > 13000 else ""
                print(f"[{WORKER_ID}] {case} -> {row.get('status')} tokens={total}{mark}", flush=True)
            except QuotaExhausted as exc:
                print(f"[{WORKER_ID}] QUOTA PAUSED on {case}: {exc}", flush=True)
                release_claim(claim)
                if not args.wait_on_quota:
                    return 3
                print(f"[{WORKER_ID}] sleeping {args.quota_retry_minutes} min", flush=True)
                time.sleep(args.quota_retry_minutes * 60)
                break
            except Exception as exc:
                row = write_case_status(
                    case_paths(args.provider, project, bug), "ERROR",
                    {"error": f"queue-level: {type(exc).__name__}: {exc}"},
                )
                print(f"[{WORKER_ID}] {case} -> ERROR (isolated): {exc}", flush=True)
            finally:
                release_claim(claim)

        remaining = [
            (p, b) for p, b in queue
            if not (
                (queue_case_status(args.provider, p, b) or {}).get("status")
                in (TERMINAL_STATUSES - {"ERROR", "QUOTA_PAUSED"})
            )
            and not already_measured_in_prior_run(args, p, b)
        ]
        if not remaining:
            print(f"[{WORKER_ID}] QUEUE COMPLETE", flush=True)
            return 0
        if not claimed_work:
            print(f"[{WORKER_ID}] {len(remaining)} cases still active; waiting...", flush=True)
            time.sleep(15)


def run_shared_queue(args):
    if args.queue_cases:
        queue = _parse_case_list(args.queue_cases)
        if not queue:
            raise RuntimeError("--queue-cases is empty.")
        return _run_queue(args, queue, "shared explicit-case queue")

    projects = [x.strip() for x in (args.queue_projects or "").split(",") if x.strip()]
    if not projects:
        raise RuntimeError("--queue-projects is empty.")
    return _run_queue(args, build_queue(projects), f"shared project queue ({len(projects)} projects)")


# ---------------------------- batch ----------------------------

def project_bug_ids(project):
    p = cmd(["defects4j", "bids", "-p", project], check=False)
    if p.returncode != 0:
        raise RuntimeError(
            f"Could not list bugs for {project}:\n{p.stdout}\n{p.stderr}"
        )
    values = sorted(
        {int(x) for x in re.findall(r"(?m)^\s*(\d+)\s*$", p.stdout)}
    )
    if not values:
        # Some versions may print space-separated IDs.
        values = sorted({int(x) for x in re.findall(r"\b\d+\b", p.stdout)})
    if not values:
        raise RuntimeError(f"No active bug IDs found for project {project}.")
    return values


def write_batch_summary(provider, projects, rows):
    state_dir = ROOT / "Experiment" / "batch" / f"run-{RUN_ID}"
    state_dir.mkdir(parents=True, exist_ok=True)

    tag = "_".join(projects)
    if len(tag) > 100:
        tag = hashlib.sha1(tag.encode()).hexdigest()[:12]

    json_path = state_dir / f"{provider}_{tag}.json"
    csv_path = state_dir / f"{provider}_{tag}.csv"

    payload = {
        "provider": provider,
        "run_id": RUN_ID,
        "worker": WORKER_ID,
        "projects": projects,
        "updated_at": now_iso(),
        "cases": rows,
    }
    json_path.write_text(
        json.dumps(payload, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8",
    )

    fields = [
        "case", "status", "fault_detected",
        "total_tokens", "updated_at", "error",
    ]
    with csv_path.open("w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=fields)
        writer.writeheader()
        for item in rows:
            tokens = (item.get("tokens") or {}).get("totals") or {}
            writer.writerow({
                "case": item.get("case"),
                "status": item.get("status"),
                "fault_detected": item.get("fault_detected"),
                "total_tokens": tokens.get("total_tokens"),
                "updated_at": item.get("updated_at"),
                "error": item.get("error"),
            })

    return json_path, csv_path


def run_batch(args):
    projects = [
        x.strip()
        for x in args.batch_projects.split(",")
        if x.strip()
    ]
    if not projects:
        raise RuntimeError("--batch-projects is empty.")

    all_rows = []

    for project in projects:
        bugs = project_bug_ids(project)
        print(
            f"\n=== PROJECT {project}: {len(bugs)} active bugs ===",
            flush=True,
        )

        for bug in bugs:
            print(f"\n--- {project}-{bug} ---", flush=True)

            while True:
                try:
                    row = run_case(args, project, bug)
                    break
                except QuotaExhausted as exc:
                    print("\n=== DAILY QUOTA REACHED ===", flush=True)
                    print(str(exc), flush=True)
                    write_batch_summary(
                        args.provider, projects, all_rows
                    )

                    if not args.wait_on_quota:
                        print(
                            "Progress is saved. Run the SAME command again after quota reset.",
                            flush=True,
                        )
                        return 3

                    seconds = args.quota_retry_minutes * 60
                    print(
                        f"Waiting {args.quota_retry_minutes} minutes, then "
                        "retrying the SAME case from saved progress...",
                        flush=True,
                    )
                    time.sleep(seconds)

            all_rows.append(row)
            print(
                f"[{project}-{bug}] STATUS={row.get('status')} "
                f"TOKENS={(row.get('tokens') or {}).get('totals', {}).get('total_tokens')}",
                flush=True,
            )
            write_batch_summary(
                args.provider, projects, all_rows
            )

    json_path, csv_path = write_batch_summary(
        args.provider, projects, all_rows
    )
    print("\n=== BATCH COMPLETE ===", flush=True)
    print("JSON:", json_path, flush=True)
    print("CSV :", csv_path, flush=True)
    return 0


# ---------------------------- CLI ----------------------------

def main():
    ap = argparse.ArgumentParser()

    ap.add_argument(
        "--provider",
        choices=["chatgpt", "copilot"],
        required=True,
    )
    ap.add_argument("--model")
    ap.add_argument(
        "--run-id",
        default="final-opt",
        help="Fresh optimized-run namespace; old pilot/final artifacts remain untouched.",
    )
    ap.add_argument(
        "--worker",
        default="single",
        help="Worker label, e.g. W1 ... W7.",
    )
    ap.add_argument(
        "--d4j-slots",
        type=int,
        default=1,
        help="Global number of concurrent heavy Defects4J jobs across workers.",
    )
    ap.add_argument(
        "--queue-projects",
        help=(
            "Shared dynamic bug queue. All workers may use the same project "
            "list; each bug is atomically claimed by only one worker."
        ),
    )
    ap.add_argument(
        "--queue-cases",
        help="Comma-separated explicit cases, e.g. JacksonXml-1,JacksonXml-2,JacksonXml-3.",
    )

    # Single-case mode.
    ap.add_argument("--project")
    ap.add_argument("--bug", type=int)
    ap.add_argument("--stage", type=int, choices=[1, 2, 3, 4])

    # Long-running batch mode.
    ap.add_argument(
        "--batch-projects",
        help=(
            "Comma-separated project list. Runs every active bug in each "
            "project from start to finish, then continues to the next project."
        ),
    )
    ap.add_argument(
        "--resume",
        action="store_true",
        help="Skip terminal cases and continue from saved stage artifacts.",
    )
    ap.add_argument(
        "--skip-terminal-from-run",
        default=None,
        help=(
            "Prior run id whose measured terminal outcomes should be skipped. "
            "ERROR and QUOTA_PAUSED are retried. Example: final"
        ),
    )
    ap.add_argument(
        "--wait-on-quota",
        action="store_true",
        help=(
            "Keep the batch process alive when the daily model quota is reached. "
            "It sleeps and retries the same case until the quota resets."
        ),
    )
    ap.add_argument(
        "--quota-retry-minutes",
        type=int,
        default=60,
        help="Minutes to wait between quota-reset checks (default: 60).",
    )
    ap.add_argument(
        "--dry-run",
        action="store_true",
        help="Prepare inputs only; do not call an AI.",
    )

    args = ap.parse_args()

    global RUN_ID, WORKER_ID, D4J_SLOTS
    RUN_ID = re.sub(r"[^A-Za-z0-9_.-]+", "_", args.run_id).strip("_") or "final-opt"
    WORKER_ID = re.sub(r"[^A-Za-z0-9_.-]+", "_", args.worker).strip("_") or "worker"
    D4J_SLOTS = args.d4j_slots

    if D4J_SLOTS < 1 or D4J_SLOTS > 4:
        ap.error("--d4j-slots must be between 1 and 4.")
    if args.quota_retry_minutes < 5:
        ap.error("--quota-retry-minutes must be at least 5.")

    if args.queue_projects or args.queue_cases:
        if args.dry_run:
            ap.error("--dry-run is not supported with shared queues.")
        return run_shared_queue(args)

    if args.batch_projects:
        if args.dry_run:
            ap.error("--dry-run is intended for single-case debugging.")
        return run_batch(args)

    if not args.project or not args.bug or not args.stage:
        ap.error(
            "Use --queue-cases ..., --queue-projects ..., --batch-projects ..., OR "
            "--project P --bug N --stage 1|2|3|4"
        )

    context, context_path = load_or_build_context(
        args.project, args.bug
    )

    if args.stage == 1:
        stage01(args, context, context_path)
    elif args.stage == 2:
        stage02(args, context)
    elif args.stage == 3:
        stage03(args, context)
    else:
        paths = case_paths(args.provider, args.project, args.bug)
        result_dir = paths["result_dir"]

        if (result_dir / "03_validation.json").is_file():
            validation = json.loads(
                (result_dir / "03_validation.json").read_text(
                    encoding="utf-8"
                )
            )
            if validation.get("valid_on_fixed"):
                identity_path = result_dir / "03_test_identity.json"
            else:
                raise RuntimeError("Stage 03 is still invalid on fixed.")
        else:
            validation = json.loads(
                (result_dir / "02_validation.json").read_text(
                    encoding="utf-8"
                )
            )
            if not validation.get("valid_on_fixed"):
                raise RuntimeError("Stage 02 is invalid; run Stage 03 first.")
            identity_path = result_dir / "02_test_identity.json"

        identity = json.loads(identity_path.read_text(encoding="utf-8"))
        stage04(
            args,
            context,
            Path(identity["source"]),
            identity["fqcn"],
        )

    return 0


if __name__ == "__main__":
    try:
        sys.exit(main() or 0)
    except Exception as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        sys.exit(1)
