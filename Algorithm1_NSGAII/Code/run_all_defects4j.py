#!/usr/bin/env python3
"""Driver: วนทุก project / bug ของ Defects4J แล้วเก็บผลลง CSV

สิ่งที่สคริปต์นี้ทำต่อ 1 บั๊ก
  1. checkout buggy (Xb) และ fixed (Xf) ไปไว้ใน --workspace (นอก git repo)
  2. หา target class จาก `defects4j export -p classes.modified`
  3. เรียก generator (ถ้าระบุ) ให้สร้าง test จาก *buggy เท่านั้น*
     generator ต้องเขียนไฟล์ {out}/tests.tar.bz2 (รูปแบบที่ `defects4j test -s` รับได้)
  4. รัน test บน fixed (วัด coverage) และบน buggy
     ตรวจพบบั๊ก = fail บน buggy และ pass บน fixed
  5. บันทึกผลลง CSV แล้วลบ checkout ทิ้ง (ยกเว้นใช้ --keep)

ถ้าไม่ระบุ --generator สคริปต์จะทำแค่ checkout + compile เพื่อทดสอบว่าระบบพร้อม

ตัวอย่าง
  python run_all_defects4j.py --dry-run
  python run_all_defects4j.py --projects Lang --bugs 1-3
  python run_all_defects4j.py --projects Lang --bugs 1-3 --method nsga2 \
      --generator "python Algorithm1_NSGAII/Code/my_gen.py --buggy {buggy} --fixed {fixed} --out {out}"

placeholder ใน --generator: {pid} {bid} {buggy} {fixed} {out} {classes} {seed}
"""
import argparse
import csv
import os
import shlex
import shutil
import subprocess
import time
from pathlib import Path

FIELDS = [
    "project_id", "bug_id", "method", "run_id", "target_classes",
    "generation_seconds", "lines_covered", "lines_total",
    "conditions_covered", "conditions_total",
    "statement_coverage_pct", "branch_coverage_pct",
    "buggy_result", "fixed_result", "fault_detected", "status", "raw_evidence_path",
]


def d4j(*args, timeout):
    """เรียก defects4j แล้วคืน CompletedProcess (ไม่ raise เมื่อ exit code != 0)"""
    try:
        return subprocess.run(["defects4j", *map(str, args)], capture_output=True,
                              text=True, timeout=timeout)
    except subprocess.TimeoutExpired as e:
        return subprocess.CompletedProcess(e.cmd, 124, "", "timeout")


def lines_of(proc):
    return [x.strip() for x in proc.stdout.splitlines() if x.strip()]


def parse_ranges(text):
    """'1-3,7' -> {1,2,3,7}"""
    result = set()
    for part in text.split(","):
        if "-" in part:
            a, b = part.split("-")
            result.update(range(int(a), int(b) + 1))
        elif part:
            result.add(int(part))
    return result


def outcome(ws, proc):
    failing = ws / "failing_tests"
    if failing.exists() and failing.read_text().strip():
        return "fail"
    return "pass" if proc.returncode == 0 else "error"


def clear_reports(ws):
    for name in ("summary.csv", "failing_tests"):
        (ws / name).unlink(missing_ok=True)


def process_bug(args, pid, bid, row):
    ws_root = Path(args.workspace).expanduser()
    buggy, fixed = ws_root / f"{pid}-{bid}b", ws_root / f"{pid}-{bid}f"
    out = Path(args.out_root).expanduser() / f"{pid}-{bid}" / f"run-{args.run_id}"
    out.mkdir(parents=True, exist_ok=True)
    row["raw_evidence_path"] = str(out)
    try:
        for version, ws in (("b", buggy), ("f", fixed)):
            if ws.exists():
                shutil.rmtree(ws)
            ws_root.mkdir(parents=True, exist_ok=True)
            p = d4j("checkout", "-p", pid, "-v", f"{bid}{version}", "-w", ws, timeout=args.timeout)
            if p.returncode != 0:
                row["status"] = f"checkout_failed_{version}"
                (out / f"checkout_{version}.log").write_text(p.stdout + p.stderr)
                return
        classes = d4j("export", "-p", "classes.modified", "-w", buggy, timeout=args.timeout)
        target_classes = lines_of(classes)
        row["target_classes"] = "|".join(target_classes)
        if not target_classes:
            # บั๊กที่ Defects4J ไม่ระบุคลาสที่แก้ (เช่น แก้ที่ build/config)
            # ต้องแยกจาก generation_failed ไม่งั้นจะหาว่า EvoSuite พังทั้งที่ไม่มีใครให้สร้าง
            row["status"] = "no_modified_classes"
            return

        # ต้อง compile ทั้งคู่ก่อนเสมอ: EvoSuite ต้องมี .class ให้ instrument (buggy)
        # และ `defects4j coverage` ก็ต้องการ .class ของ fixed ด้วย
        for version, ws in (("b", buggy), ("f", fixed)):
            p = d4j("compile", "-w", ws, timeout=args.timeout)
            (out / f"compile_{version}.log").write_text(p.stdout + p.stderr)
            if p.returncode != 0:
                row["status"] = f"compile_failed_{version}"
                return

        if not args.generator:                      # โหมดตรวจความพร้อม (ไม่มี generator)
            row["status"] = "checkout_compile_ok"
            return

        # ---- สร้าง test จาก buggy เท่านั้น ----
        cmd = args.generator.format(pid=pid, bid=bid, buggy=buggy, fixed=fixed, out=out,
                                    classes=",".join(target_classes), seed=args.seed)
        # shlex ไม่ขยาย "~" ให้ ถ้าไม่ขยายตรงนี้ subprocess จะมองไม่เห็นไฟล์และ
        # ทำให้ทุกบั๊กกลายเป็น generation_failed ทั้งที่ generator ทำงานได้
        argv = [os.path.expanduser(t) for t in shlex.split(cmd)]
        start = time.monotonic()
        try:
            g = subprocess.run(argv, capture_output=True, text=True,
                               timeout=args.gen_timeout)
            (out / "generator.log").write_text(g.stdout + g.stderr)
            gen_ok = g.returncode == 0
        except subprocess.TimeoutExpired:
            gen_ok = False
            (out / "generator.log").write_text("generator timeout\n")
        row["generation_seconds"] = round(time.monotonic() - start, 3)
        archive = out / "tests.tar.bz2"
        if not gen_ok or not archive.exists():
            row["status"] = "generation_failed"
            return

        # ---- fixed: coverage + pass/fail ----
        clear_reports(fixed)
        pf = d4j("coverage", "-w", fixed, "-s", archive, timeout=args.timeout)
        (out / "fixed_coverage.log").write_text(pf.stdout + pf.stderr)
        row["fixed_result"] = outcome(fixed, pf)
        summary = fixed / "summary.csv"
        if summary.exists():
            shutil.copy(summary, out / "fixed_coverage_summary.csv")
            with summary.open(newline="") as s:
                e = next(csv.DictReader(s))
            lt, lc, ct, cc = (int(e[k]) for k in
                              ("LinesTotal", "LinesCovered", "ConditionsTotal", "ConditionsCovered"))
            row.update(lines_total=lt, lines_covered=lc, conditions_total=ct, conditions_covered=cc,
                       statement_coverage_pct=round(100 * lc / lt, 1) if lt else "",
                       branch_coverage_pct=round(100 * cc / ct, 1) if ct else "")

        # ---- buggy: pass/fail ----
        clear_reports(buggy)
        pb = d4j("test", "-w", buggy, "-s", archive, timeout=args.timeout)
        (out / "buggy_test.log").write_text(pb.stdout + pb.stderr)
        row["buggy_result"] = outcome(buggy, pb)
        if (buggy / "failing_tests").exists():
            shutil.copy(buggy / "failing_tests", out / "buggy_failing_tests.txt")

        if "error" in (row["fixed_result"], row["buggy_result"]):
            row["fault_detected"], row["status"] = "n/a", "not_evaluable"
        else:
            row["fault_detected"] = row["buggy_result"] == "fail" and row["fixed_result"] == "pass"
            row["status"] = "ok"
    finally:
        if not args.keep:
            shutil.rmtree(buggy, ignore_errors=True)
            shutil.rmtree(fixed, ignore_errors=True)


def load_done(path):
    if not path.exists():
        return set()
    with path.open(newline="") as f:
        return {(r["project_id"], r["bug_id"], r["method"], r["run_id"]) for r in csv.DictReader(f)}


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--projects", help="เช่น Lang,Math (ค่าเริ่มต้น: ทุกโปรเจกต์)")
    ap.add_argument("--bugs", help="เช่น 1-5,9 (ใช้กับทุกโปรเจกต์ที่เลือก; ค่าเริ่มต้น: ทุกบั๊ก)")
    ap.add_argument("--limit", type=int, help="ทำแค่ N บั๊กแรก (ไว้ทดสอบ)")
    ap.add_argument("--generator", help="คำสั่งสร้าง test (ดู placeholder ด้านบน)")
    ap.add_argument("--method", default="baseline", help="ชื่อวิธี เช่น nsga2 / copilot")
    ap.add_argument("--run-id", default="01")
    ap.add_argument("--seed", type=int, default=11)
    ap.add_argument("--workspace", default="~/sqa-workspaces", help="ที่ checkout ชั่วคราว (นอก git)")
    ap.add_argument("--out-root", default="results", help="โฟลเดอร์เก็บหลักฐานต่อบั๊ก")
    ap.add_argument("--results", default="results/summary_auto.csv")
    ap.add_argument("--timeout", type=int, default=1800, help="วินาทีต่อคำสั่ง defects4j")
    ap.add_argument("--gen-timeout", type=int, default=3600, help="วินาทีต่อการ generate 1 บั๊ก")
    ap.add_argument("--keep", action="store_true", help="ไม่ลบ checkout หลังจบแต่ละบั๊ก")
    ap.add_argument("--force", action="store_true", help="ทำซ้ำแม้เคยบันทึกผลแล้ว")
    ap.add_argument("--dry-run", action="store_true", help="แค่นับจำนวนงาน ไม่ checkout/รันอะไร")
    args = ap.parse_args()

    projects = lines_of(d4j("pids", timeout=60))
    if args.projects:
        wanted = set(args.projects.split(","))
        unknown = wanted - set(projects)
        if unknown:
            ap.error("ไม่รู้จักโปรเจกต์: %s (มีให้เลือก: %s)" % (", ".join(sorted(unknown)), ", ".join(projects)))
        projects = [p for p in projects if p in wanted]
    bug_filter = parse_ranges(args.bugs) if args.bugs else None

    jobs = []
    for pid in projects:
        for b in lines_of(d4j("bids", "-p", pid, timeout=60)):
            if b.isdigit() and (bug_filter is None or int(b) in bug_filter):
                jobs.append((pid, b))
    if args.limit:
        jobs = jobs[:args.limit]

    print("โปรเจกต์: %d | บั๊กที่จะทำ: %d" % (len(projects), len(jobs)))
    if args.dry_run:
        for pid in projects:
            print("  %-16s %d บั๊ก" % (pid, sum(1 for p, _ in jobs if p == pid)))
        return

    results = Path(args.results).expanduser()
    results.parent.mkdir(parents=True, exist_ok=True)
    done = set() if args.force else load_done(results)
    new_file = not results.exists()
    with results.open("a", newline="") as f:
        writer = csv.DictWriter(f, fieldnames=FIELDS)
        if new_file:
            writer.writeheader()
        for n, (pid, bid) in enumerate(jobs, 1):
            key = (pid, bid, args.method, args.run_id)
            if key in done:
                print("[%d/%d] %s-%s ข้าม (เคยทำแล้ว)" % (n, len(jobs), pid, bid))
                continue
            row = {k: "" for k in FIELDS}
            row.update(project_id=pid, bug_id=bid, method=args.method, run_id=args.run_id, status="unknown")
            t = time.monotonic()
            try:
                process_bug(args, pid, bid, row)
            except Exception as e:                       # บั๊กหนึ่งพังไม่ควรหยุดทั้งชุด
                row["status"] = "driver_error: %s" % e
            writer.writerow(row)
            f.flush()                                    # เก็บผลทันที เผื่อ Ctrl+C
            print("[%d/%d] %s-%s %-22s detected=%s (%.0fs)" %
                  (n, len(jobs), pid, bid, row["status"], row["fault_detected"], time.monotonic() - t))


if __name__ == "__main__":
    main()
