#!/usr/bin/env python3
"""NSGA-II pilot for Defects4J Lang-1 NumberUtils.createNumber.

Fitness is measured with Defects4J coverage on Lang-1f, never estimated
from source text.  The buggy version is used only for final fault detection.
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

TARGET = "org.apache.commons.lang3.math.NumberUtils"
TEST = "org.apache.commons.lang3.math.NumberUtilsNSGA2Test"
RELATIVE_TEST = Path("org/apache/commons/lang3/math/NumberUtilsNSGA2Test.java")
# Candidate inputs come from public hexadecimal syntax and numeric boundaries.
INPUTS = (
    "0x0", "0x1", "0x7F", "0x7FFFFFFF", "0x80000000",
    "0xFFFFFFFF", "0x100000000", "0x7FFFFFFFFFFFFFFF",
    "0x8000000000000000", "0x10000000000000000",
    "-0x1", "-0x7FFFFFFF", "-0x80000000", "-0x80000001",
    "#80000000", "0X80000000",
)


def java_source(indices):
    methods = []
    for index in indices:
        value = INPUTS[index]
        number = int(value.replace("#", "0x"), 0)
        methods.append(
            "    @Test public void input_%02d() {\n" % index
            + '        Number actual = NumberUtils.createNumber("%s");\n' % value
            + '        assertEquals(new BigInteger("%d"),\n' % number
            + '            new BigInteger(actual.toString()));\n'
            + "    }\n"
        )
    return (
        "package org.apache.commons.lang3.math;\n"
        "import java.math.BigInteger;\n"
        "import org.junit.Test;\n"
        "import static org.junit.Assert.assertEquals;\n"
        "public class NumberUtilsNSGA2Test {\n"
        + "\n".join(methods) + "}\n"
    )


def run(command, logfile):
    with logfile.open("w") as output:
        result = subprocess.run(command, stdout=output, stderr=subprocess.STDOUT,
                                check=False)
    return result.returncode


def archive_source(source, archive):
    with tarfile.open(archive, "w:bz2") as tar:
        tar.add(source, arcname=str(RELATIVE_TEST))


def dominates(a, b):
    # More covered lines and conditions; fewer tests (all three are objectives).
    x, y = a["lines"], a["conditions"]
    p, q = b["lines"], b["conditions"]
    return x >= p and y >= q and len(a["genes"]) <= len(b["genes"]) and (
        x > p or y > q or len(a["genes"]) < len(b["genes"])
    )


def fronts(pop):
    remaining = list(pop)
    result = []
    while remaining:
        front = [a for a in remaining if not any(
            dominates(b, a) for b in remaining if b is not a)]
        result.append(front)
        remaining = [a for a in remaining if a not in front]
    return result


def crowding(front):
    distance = {id(x): 0.0 for x in front}
    if len(front) < 3:
        return {id(x): float("inf") for x in front}
    for accessor in (lambda x: x["lines"], lambda x: x["conditions"],
                     lambda x: -len(x["genes"])):
        ordered = sorted(front, key=accessor)
        distance[id(ordered[0])] = distance[id(ordered[-1])] = float("inf")
        span = accessor(ordered[-1]) - accessor(ordered[0])
        if span:
            for i in range(1, len(ordered) - 1):
                distance[id(ordered[i])] += (
                    accessor(ordered[i + 1]) - accessor(ordered[i - 1])) / span
    return distance


def select(population, size):
    chosen = []
    for front in fronts(population):
        distance = crowding(front)
        chosen.extend(sorted(front, key=lambda x: distance[id(x)], reverse=True)
                      [:size - len(chosen)])
        if len(chosen) >= size:
            break
    return chosen


def mutate(genes, rng):
    result = set(genes)
    if rng.random() < 0.5 and len(result) > 1:
        result.remove(rng.choice(sorted(result)))
    else:
        result.add(rng.randrange(len(INPUTS)))
    return tuple(sorted(result))


def crossover(a, b, rng):
    pool = sorted(set(a) | set(b))
    child = tuple(x for x in pool if rng.random() < 0.5)
    return child or (rng.choice(pool),)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--fixed", type=Path, required=True)
    parser.add_argument("--buggy", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--population", type=int, default=4)
    parser.add_argument("--generations", type=int, default=2)
    parser.add_argument("--seed", type=int, default=11)
    args = parser.parse_args()
    if args.population < 2 or args.generations < 1:
        parser.error("population >= 2 and generations >= 1 are required")
    if not args.fixed.is_dir() or not args.buggy.is_dir():
        parser.error("--fixed and --buggy must be existing Defects4J checkouts")
    args.output.mkdir(parents=True, exist_ok=True)
    logdir = args.output / "evaluations"
    logdir.mkdir(exist_ok=True)
    rng = random.Random(args.seed)
    start = time.monotonic()
    cache = {}
    rows = []
    scratch = Path(tempfile.mkdtemp(prefix="nsga2-lang1-"))

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
        code = run(["defects4j", "coverage", "-w", str(args.fixed),
                    "-s", str(archive)], logfile)
        # Failing tests invalidate coverage as a fitness result.
        failing = args.fixed / "failing_tests"
        failed = failing.exists() and bool(failing.read_text().strip())
        totals = (0, 0, 0, 0)
        summary = args.fixed / "summary.csv"
        if code == 0 and not failed and summary.exists():
            with summary.open(newline="") as stream:
                entry = next(csv.DictReader(stream))
            totals = tuple(int(entry[field]) for field in (
                "LinesTotal", "LinesCovered", "ConditionsTotal", "ConditionsCovered"))
        individual = {"genes": genes, "lines": totals[1],
                      "conditions": totals[3], "valid": code == 0 and not failed}
        cache[genes] = individual
        rows.append({"candidate": index, "inputs": "|".join(INPUTS[x] for x in genes),
                     "tests": len(genes), "valid_on_fixed": individual["valid"],
                     "lines_total": totals[0], "lines_covered": totals[1],
                     "conditions_total": totals[2], "conditions_covered": totals[3],
                     "log": str(logfile.relative_to(args.output))})
        print("candidate %d: %d tests, %d lines, %d conditions, valid=%s" %
              (index, len(genes), totals[1], totals[3], individual["valid"]), flush=True)
        return individual

    try:
        population = [evaluate(tuple(sorted(rng.sample(range(len(INPUTS)),
                         rng.randint(1, 4))))) for _ in range(args.population)]
        for generation in range(args.generations):
            children = []
            for _ in range(args.population):
                a, b = rng.sample(population, 2)
                child = mutate(crossover(a["genes"], b["genes"], rng), rng)
                children.append(evaluate(child))
            population = select(population + children, args.population)
            print("generation %d complete" % (generation + 1), flush=True)
        valid = [x for x in cache.values() if x["valid"]]
        if not valid:
            raise RuntimeError("No suite passed on fixed; inspect evaluations/*.log")
        best = max(valid, key=lambda x: (x["lines"], x["conditions"],
                                          -len(x["genes"])))
        output_source = args.output / "NumberUtilsNSGA2Test.java"
        output_source.write_text(java_source(best["genes"]))
        buggy_archive = scratch / "Lang-1b-nsga2.1.tar.bz2"
        archive_source(output_source, buggy_archive)
        buglog = args.output / "buggy_detection.log"
        run(["defects4j", "test", "-w", str(args.buggy),
             "-s", str(buggy_archive)], buglog)
        with (args.output / "evaluations.csv").open("w", newline="") as stream:
            writer = csv.DictWriter(stream, fieldnames=rows[0])
            writer.writeheader()
            writer.writerows(rows)
        (args.output / "configuration.json").write_text(json.dumps({
            "algorithm": "NSGA-II", "project": "Lang", "bug": 1,
            "seed": args.seed, "population": args.population,
            "generations": args.generations, "candidate_inputs": INPUTS,
            "fitness": "Defects4J fixed-version line and condition coverage; minimize tests",
            "evaluated_unique": len(cache), "elapsed_seconds": round(time.monotonic()-start, 3),
            "selected_inputs": [INPUTS[x] for x in best["genes"]],
            "selected_lines": best["lines"],
            "selected_conditions": best["conditions"],
        }, indent=2) + "\n")
        print("selected:", [INPUTS[x] for x in best["genes"]])
        print("buggy detection log:", buglog)
    finally:
        shutil.rmtree(scratch)


if __name__ == "__main__":
    main()
