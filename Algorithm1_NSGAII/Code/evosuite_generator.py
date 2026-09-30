#!/usr/bin/env python3
"""เรียก EvoSuite ให้สร้าง JUnit test จาก buggy checkout ของบั๊กเดียว
แล้วห่อไฟล์ .java ที่ได้เป็น {out}/tests.tar.bz2 (รูปแบบที่ `defects4j test -s` รับได้)

run_all_defects4j.py เรียกไฟล์นี้ผ่าน --generator โดยแทน placeholder เองตามคำสั่งนี้:

  --generator "python evosuite_generator.py --buggy {buggy} --fixed {fixed}
               --out {out} --classes {classes} --seed {seed} --tests-out {tests_dir}
               --evosuite-jar /path/to/evosuite-*.jar"

ถ้าระบุ --tests-out ไฟล์ .java ทั้งหมดจะถูก *ย้าย* (หลังห่อ tests.tar.bz2 แล้ว) ไปไว้ที่นั่น
เพื่อให้ใน {out} เหลือแต่ log กับ archive ไม่มี source ของ test ปนอยู่กับ evidence

หมายเหตุสำคัญ: สคริปต์นี้อ่าน *เฉพาะ* {buggy} เท่านั้น ไม่แตะ {fixed} เลย
(ตรงตาม protocol ข้อ 3 ของกลุ่ม ที่ห้ามตัวสร้าง test เห็น fixed source)
พารามิเตอร์ --fixed รับไว้เฉยๆ เพื่อให้ placeholder ของ driver ใช้ชุดเดียวกันได้
"""
import argparse
import shutil
import subprocess
import sys
import tarfile
from pathlib import Path


def run(cmd, timeout):
    return subprocess.run(cmd, capture_output=True, text=True, timeout=timeout)


def d4j_export(prop, ws, timeout):
    p = run(["defects4j", "export", "-p", prop, "-w", str(ws)], timeout)
    return p.stdout.strip()


def generate_one_class(java_bin, tools_jar, evosuite_jar, classpath, klass, work_dir,
                        seed, algorithm, budget, criterion, timeout, log_dir):
    """รัน EvoSuite กับ 1 คลาส คืน True ถ้าสำเร็จ (มีไฟล์ test ออกมาจริง)"""
    cmd = [java_bin]
    if tools_jar:
        # ต้องมาก่อน -jar เพราะเป็น JVM system property ไม่ใช่ EvoSuite property
        # (ยืนยันจากการรันจริง: EvoSuite 1.2.0 บน Java 8 หา tools.jar เองไม่เจอ ต้องชี้ path ให้)
        cmd.append("-Dtools_jar_location=%s" % tools_jar)
    cmd += [
        "-jar", str(evosuite_jar),
        "-generateSuite",
        "-class", klass,
        "-projectCP", classpath,
        "-base_dir", str(work_dir),
        "-seed", str(seed),                       # ยืนยันจาก -help: เป็น top-level flag ไม่ใช่ -D
        "-Dalgorithm=%s" % algorithm,              # ยืนยันจาก -listParameters ว่ามีค่า NSGAII จริง
        "-Dsearch_budget=%d" % budget,
        "-Dtest_format=JUNIT4",
        "-Dassertions=true",
    ]
    if criterion:
        # ถ้าไม่ระบุ ปล่อยให้ EvoSuite ใช้ default ของมันเอง
        # (ยืนยันจาก statistics.csv ว่า default คือ LINE:BRANCH:EXCEPTION:WEAKMUTATION:
        #  OUTPUT:METHOD:METHODNOEXCEPTION:CBRANCH อยู่แล้ว)
        cmd.append("-criterion")
        cmd.append(criterion)
    log = log_dir / ("%s.log" % klass)
    try:
        p = run(cmd, timeout)
        log.write_text("CMD: %s\n\n%s\n%s" % (" ".join(cmd), p.stdout, p.stderr))
        ok = p.returncode == 0
    except subprocess.TimeoutExpired:
        log.write_text("CMD: %s\n\nTIMEOUT after %ds" % (" ".join(cmd), timeout))
        ok = False

    pkg_dir = work_dir / "evosuite-tests" / klass.replace(".", "/")
    test_file = pkg_dir.parent / ("%s_ESTest.java" % pkg_dir.name)
    return ok and test_file.exists()


def move_tests(tests_root, dst):
    """ย้าย .java ที่ EvoSuite สร้างไปเก็บแยกจาก log โดยคง package path เดิมไว้

    ต้องเรียกหลังห่อ tests.tar.bz2 แล้ว เพราะ archive ยังต้องอ่านไฟล์จาก tests_root อยู่
    """
    dst.mkdir(parents=True, exist_ok=True)
    moved = 0
    for src in sorted(tests_root.rglob("*.java")):
        target = dst / src.relative_to(tests_root)
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.move(str(src), str(target))
        moved += 1
    return moved


def main():
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--buggy", required=True, help="path ของ checkout เวอร์ชัน buggy (เห็นได้อย่างเดียว)")
    ap.add_argument("--fixed", help="ไม่ใช้ในสคริปต์นี้ รับไว้เผื่อ placeholder ของ driver ส่งมาด้วย")
    ap.add_argument("--out", required=True, help="โฟลเดอร์ที่ driver รอผล (จะเขียน tests.tar.bz2 ที่นี่)")
    ap.add_argument("--tests-out",
                    help="โฟลเดอร์ปลายทางของ JUnit test ที่สร้างได้ (driver ส่งมาทาง placeholder "
                         "{tests_dir}) ไม่ระบุ = ปล่อยให้ .java อยู่ใน evosuite-work เหมือนเดิม")
    ap.add_argument("--classes", required=True, help="รายชื่อคลาสเป้าหมาย คั่นด้วย , (มาจาก classes.modified)")
    ap.add_argument("--seed", type=int, default=11)
    ap.add_argument("--algorithm", default="NSGAII")
    ap.add_argument("--budget", type=int, default=60, help="วินาทีต่อคลาส")
    ap.add_argument("--criterion", default=None,
                     help="เป้าหมายที่ EvoSuite ค้นหา คั่นด้วย ':' เช่น "
                          "LINE:BRANCH:WEAKMUTATION:STRONGMUTATION ไม่ระบุ = ใช้ default ของ EvoSuite เอง "
                          "(ยืนยันแล้วว่า default คือ LINE:BRANCH:EXCEPTION:WEAKMUTATION:OUTPUT:METHOD:"
                          "METHODNOEXCEPTION:CBRANCH) เพิ่ม STRONGMUTATION ถ้าอยากให้ค้นหาแบบใกล้เคียง "
                          "'ความต่างเล็กๆ ในพฤติกรรม' ซึ่งใกล้เคียงนิยามของบั๊กมากกว่า criterion อื่น")
    ap.add_argument("--evosuite-jar", required=True)
    ap.add_argument("--java-bin", default="/usr/lib/jvm/java-8-openjdk-amd64/bin/java",
                     help="EvoSuite ต้อง Java 8 (มี tools.jar) ไม่ใช่ java เริ่มต้นของระบบที่ Defects4J ใช้ (Java 11)")
    ap.add_argument("--tools-jar", default="/usr/lib/jvm/java-8-openjdk-amd64/lib/tools.jar",
                     help="path ของ tools.jar ใน Java 8 JDK เดียวกับ --java-bin ใส่ '' เพื่อปิด ถ้า java-bin หาเองได้แล้ว")
    ap.add_argument("--per-class-timeout", type=int, default=180)
    args = ap.parse_args()

    buggy = Path(args.buggy)
    out = Path(args.out)
    out.mkdir(parents=True, exist_ok=True)
    work_dir = out / "evosuite-work"
    work_dir.mkdir(exist_ok=True)
    log_dir = out / "evosuite-logs"
    log_dir.mkdir(exist_ok=True)

    if not Path(args.evosuite_jar).exists():
        print("หา evosuite jar ไม่เจอ: %s" % args.evosuite_jar, file=sys.stderr)
        return 1

    classpath = d4j_export("cp.compile", buggy, timeout=120)
    if not classpath:
        print("defects4j export -p cp.compile ไม่คืนค่า (buggy ยัง compile ไม่ผ่านหรือเปล่า?)", file=sys.stderr)
        return 1

    classes = [c.strip() for c in args.classes.split(",") if c.strip()]
    if not classes:
        print("ไม่มี target class ให้สร้าง test (--classes ว่าง)", file=sys.stderr)
        return 1

    succeeded = []
    for klass in classes:
        ok = generate_one_class(args.java_bin, args.tools_jar, args.evosuite_jar, classpath, klass,
                                 work_dir, args.seed, args.algorithm, args.budget, args.criterion,
                                 args.per_class_timeout, log_dir)
        print(("  ok  " if ok else "  fail"), klass)
        if ok:
            succeeded.append(klass)

    if not succeeded:
        print("ทุกคลาสสร้าง test ไม่สำเร็จเลย ไม่ห่อ tar.bz2", file=sys.stderr)
        return 1

    tests_root = work_dir / "evosuite-tests"
    archive = out / "tests.tar.bz2"
    with tarfile.open(archive, "w:bz2") as tar:
        tar.add(tests_root, arcname=".")

    if args.tests_out:
        moved = move_tests(tests_root, Path(args.tests_out))
        print("ย้าย JUnit test %d ไฟล์ -> %s" % (moved, args.tests_out))

    print("สร้าง test สำเร็จ %d/%d คลาส -> %s" % (len(succeeded), len(classes), archive))
    return 0


if __name__ == "__main__":
    sys.exit(main())
