#!/usr/bin/env python3
"""Coverage-driven NSGA-II for the public NumberUtils API on Defects4J Lang-1.

Run separately from run-01. The fixed version supplies coverage and validity;
the buggy version is used only for fault detection after selection.
"""
import argparse
import csv
import json
import random
import shutil
import subprocess
import tarfile
import tempfile
import time
from pathlib import Path

RELATIVE_TEST = Path("org/apache/commons/lang3/math/NumberUtilsNSGA2Run02Test.java")
TEST = "org.apache.commons.lang3.math.NumberUtilsNSGA2Run02Test"

# Each independent test has an oracle derived from the documented public API
# or standard Java numeric semantics. No fixed-source implementation is read.
CASES = (
    ("hex_small", 'assertEquals(1, NumberUtils.createNumber("0x1").intValue());'),
    ("hex_boundary", 'assertEquals(2147483648L, NumberUtils.createNumber("0x80000000").longValue());'),
    ("hex_hash", 'assertEquals(2147483648L, NumberUtils.createNumber("#80000000").longValue());'),
    ("hex_ff", 'assertEquals(4294967295L, NumberUtils.createNumber("0xFFFFFFFF").longValue());'),
    ("hex_big", 'assertEquals(new BigInteger("18446744073709551616"), NumberUtils.createNumber("0x10000000000000000"));'),
    ("decimal", 'assertEquals(123, NumberUtils.createNumber("123").intValue());'),
    ("decimal_big", 'assertEquals(new BigInteger("18446744073709551616"), NumberUtils.createNumber("18446744073709551616"));'),
    ("decimal_negative", 'assertEquals(-123, NumberUtils.createNumber("-123").intValue());'),
    ("number_null", 'assertNull(NumberUtils.createNumber(null));'),
    ("number_blank", 'try { NumberUtils.createNumber(" "); fail("Expected NumberFormatException"); } catch (NumberFormatException expected) { }'),
    ("to_int", 'assertEquals(42, NumberUtils.toInt("42")); assertEquals(-5, NumberUtils.toInt("bad", -5));'),
    ("to_long", 'assertEquals(12345678901L, NumberUtils.toLong("12345678901")); assertEquals(7L, NumberUtils.toLong(null, 7L));'),
    ("to_short", 'assertEquals((short) 12, NumberUtils.toShort("12")); assertEquals((short) 3, NumberUtils.toShort("bad", (short) 3));'),
    ("to_byte", 'assertEquals((byte) 12, NumberUtils.toByte("12")); assertEquals((byte) 3, NumberUtils.toByte("bad", (byte) 3));'),
    ("to_float", 'assertEquals(1.5f, NumberUtils.toFloat("1.5"), 0.0f); assertEquals(2.0f, NumberUtils.toFloat(null, 2.0f), 0.0f);'),
    ("to_double", 'assertEquals(1.5d, NumberUtils.toDouble("1.5"), 0.0d); assertEquals(2.0d, NumberUtils.toDouble("bad", 2.0d), 0.0d);'),
    ("create_integer", 'assertEquals(Integer.valueOf(15), NumberUtils.createInteger("0xF"));'),
    ("create_long", 'assertEquals(Long.valueOf(15L), NumberUtils.createLong("0xF"));'),
    ("create_big_integer", 'assertEquals(new BigInteger("15"), NumberUtils.createBigInteger("15"));'),
    ("create_big_decimal", 'assertEquals(new BigDecimal("1.25"), NumberUtils.createBigDecimal("1.25"));'),
    ("create_float", 'assertEquals(Float.valueOf(1.25f), NumberUtils.createFloat("1.25"));'),
    ("create_double", 'assertEquals(Double.valueOf(1.25d), NumberUtils.createDouble("1.25"));'),
    ("is_digits", 'assertTrue(NumberUtils.isDigits("123")); assertFalse(NumberUtils.isDigits("12a"));'),
    ("is_number", 'assertTrue(NumberUtils.isNumber("123")); assertFalse(NumberUtils.isNumber("abc"));'),
    ("min_int_array", 'assertEquals(-3, NumberUtils.min(new int[] {4, -3, 7}));'),
    ("max_int_array", 'assertEquals(7, NumberUtils.max(new int[] {4, -3, 7}));'),
    ("min_long_array", 'assertEquals(-3L, NumberUtils.min(new long[] {4L, -3L, 7L}));'),
    ("max_long_array", 'assertEquals(7L, NumberUtils.max(new long[] {4L, -3L, 7L}));'),
    ("min_double_array", 'assertEquals(-3.0d, NumberUtils.min(new double[] {4.0d, -3.0d, 7.0d}), 0.0d);'),
    ("max_double_array", 'assertEquals(7.0d, NumberUtils.max(new double[] {4.0d, -3.0d, 7.0d}), 0.0d);'),
    ("min_three", 'assertEquals(-3, NumberUtils.min(4, -3, 7));'),
    ("max_three", 'assertEquals(7, NumberUtils.max(4, -3, 7));'),
)


def java_source(genes):
    methods = ["    @Test public void case_%02d_%s() { %s }" % (i, CASES[i][0], CASES[i][1])
               for i in genes]
    return ("package org.apache.commons.lang3.math;\n"
            "import java.math.BigInteger;\nimport java.math.BigDecimal;\n"
            "import org.junit.Test;\nimport static org.junit.Assert.*;\n"
            "public class NumberUtilsNSGA2Run02Test {\n" + "\n".join(methods) + "\n}\n")


def run(command, logfile):
    with logfile.open("w") as output:
        return subprocess.run(command, stdout=output, stderr=subprocess.STDOUT,
                              check=False).returncode


def archive_source(source, archive):
    with tarfile.open(archive, "w:bz2") as tar:
        tar.add(source, arcname=str(RELATIVE_TEST))


def dominates(a, b):
    return (a["lines"] >= b["lines"] and a["conditions"] >= b["conditions"]
            and len(a["genes"]) <= len(b["genes"])
            and (a["lines"] > b["lines"] or a["conditions"] > b["conditions"]
                 or len(a["genes"]) < len(b["genes"])))


def fronts(pop):
    remaining, result = list(pop), []
    while remaining:
        front = [a for a in remaining if not any(dominates(b, a) for b in remaining if b is not a)]
        result.append(front)
        remaining = [a for a in remaining if a not in front]
    return result


def crowding(front):
    distance = {id(x): 0.0 for x in front}
    if len(front) < 3:
        return {id(x): float("inf") for x in front}
    for accessor in (lambda x: x["lines"], lambda x: x["conditions"], lambda x: -len(x["genes"])):
        ordered = sorted(front, key=accessor)
        distance[id(ordered[0])] = distance[id(ordered[-1])] = float("inf")
        span = accessor(ordered[-1]) - accessor(ordered[0])
        if span:
            for i in range(1, len(ordered) - 1):
                distance[id(ordered[i])] += (accessor(ordered[i + 1]) - accessor(ordered[i - 1])) / span
    return distance


def select(pop, size):
    chosen = []
    for front in fronts(pop):
        distance = crowding(front)
        chosen.extend(sorted(front, key=lambda x: distance[id(x)], reverse=True)[:size - len(chosen)])
        if len(chosen) >= size:
            break
    return chosen


def mutate(genes, rng):
    result = set(genes)
    if rng.random() < 0.35 and len(result) > 1:
        result.remove(rng.choice(sorted(result)))
    else:
        result.add(rng.randrange(len(CASES)))
    return tuple(sorted(result))


def crossover(a, b, rng):
    pool = sorted(set(a) | set(b))
    child = tuple(x for x in pool if rng.random() < 0.65)
    return child or (rng.choice(pool),)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--fixed", type=Path, required=True)
    parser.add_argument("--buggy", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--population", type=int, default=8)
    parser.add_argument("--generations", type=int, default=5)
    parser.add_argument("--seed", type=int, default=11)
    args = parser.parse_args()
    if args.population < 2 or args.generations < 1:
        parser.error("population >= 2 and generations >= 1 are required")
    if not args.fixed.is_dir() or not args.buggy.is_dir():
        parser.error("--fixed and --buggy must be existing Defects4J checkouts")
    if args.output.exists() and any(args.output.iterdir()):
        parser.error("--output must be a new or empty directory; preserve prior results")
    args.output.mkdir(parents=True, exist_ok=True)
    logdir = args.output / "evaluations"
    logdir.mkdir()
    rng = random.Random(args.seed)
    start = time.monotonic()
    cache, rows = {}, []
    scratch = Path(tempfile.mkdtemp(prefix="nsga2-lang1-run02-"))

    def evaluate(genes):
        genes = tuple(sorted(set(genes)))
        if genes in cache:
            return cache[genes]
        index = len(cache) + 1
        source = scratch / RELATIVE_TEST
        source.parent.mkdir(parents=True, exist_ok=True)
        source.write_text(java_source(genes))
        archive = scratch / ("Lang-1f-nsga2.%d.tar.bz2" % index)
        archive_source(source, archive)
        logfile = logdir / ("candidate_%03d.log" % index)
        # Remove stale reports: a failed compile must not reuse previous fitness.
        for report in ("summary.csv", "failing_tests"):
            (args.fixed / report).unlink(missing_ok=True)
        code = run(["defects4j", "coverage", "-w", str(args.fixed), "-s", str(archive)], logfile)
        failing = args.fixed / "failing_tests"
        failed = failing.exists() and bool(failing.read_text().strip())
        summary = args.fixed / "summary.csv"
        valid = code == 0 and not failed and summary.exists()
        totals = (0, 0, 0, 0)
        if valid:
            with summary.open(newline="") as stream:
                entry = next(csv.DictReader(stream))
            totals = tuple(int(entry[field]) for field in (
                "LinesTotal", "LinesCovered", "ConditionsTotal", "ConditionsCovered"))
        individual = {"genes": genes, "lines": totals[1], "conditions": totals[3], "valid": valid}
        cache[genes] = individual
        rows.append({"candidate": index, "cases": "|".join(CASES[i][0] for i in genes),
                     "tests": len(genes), "valid_on_fixed": valid,
                     "lines_total": totals[0], "lines_covered": totals[1],
                     "conditions_total": totals[2], "conditions_covered": totals[3],
                     "log": str(logfile.relative_to(args.output))})
        print("candidate %d: %d tests, %d lines, %d conditions, valid=%s" %
              (index, len(genes), totals[1], totals[3], valid), flush=True)
        return individual

    try:
        population = [evaluate(tuple(sorted(rng.sample(range(len(CASES)), rng.randint(4, 10)))))
                      for _ in range(args.population)]
        for generation in range(args.generations):
            children = []
            for _ in range(args.population):
                a, b = rng.sample(population, 2)
                children.append(evaluate(mutate(crossover(a["genes"], b["genes"], rng), rng)))
            population = select(population + children, args.population)
            print("generation %d complete" % (generation + 1), flush=True)
        valid = [x for x in cache.values() if x["valid"]]
        if not valid:
            raise RuntimeError("No suite passed on fixed; inspect evaluations/*.log")
        best = max(valid, key=lambda x: (x["lines"], x["conditions"], -len(x["genes"])))
        output_source = args.output / "NumberUtilsNSGA2Run02Test.java"
        output_source.write_text(java_source(best["genes"]))
        archive = args.output / "Lang-1-nsga2-run02.1.tar.bz2"
        archive_source(output_source, archive)
        buglog = args.output / "buggy_detection.log"
        run(["defects4j", "test", "-w", str(args.buggy), "-s", str(archive)], buglog)
        with (args.output / "evaluations.csv").open("w", newline="") as stream:
            writer = csv.DictWriter(stream, fieldnames=rows[0])
            writer.writeheader()
            writer.writerows(rows)
        (args.output / "configuration.json").write_text(json.dumps({
            "algorithm": "NSGA-II", "project": "Lang", "bug": 1,
            "seed": args.seed, "population": args.population, "generations": args.generations,
            "candidate_cases": [name for name, _ in CASES],
            "fitness": "Lang-1f Defects4J line and condition coverage; minimize tests",
            "evaluated_unique": len(cache), "elapsed_seconds": round(time.monotonic() - start, 3),
            "selected_cases": [CASES[i][0] for i in best["genes"]],
            "selected_lines": best["lines"], "selected_conditions": best["conditions"],
        }, indent=2) + "\n")
        print("selected:", [CASES[i][0] for i in best["genes"]])
        print("buggy detection log:", buglog)
    finally:
        shutil.rmtree(scratch)


if __name__ == "__main__":
    main()
