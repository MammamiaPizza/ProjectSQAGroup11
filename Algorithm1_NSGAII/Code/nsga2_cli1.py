#!/usr/bin/env python3
"""Coverage-driven NSGA-II for Defects4J Cli-1, exploratory post-hoc run.

Candidates exercise the public CommandLine contract. This experiment is
post-hoc: the candidate set was assembled after inspecting the Cli-1 fix.
Do not pool its fault-detection outcome with preregistered AI comparisons.
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

RELATIVE_TEST = Path("org/apache/commons/cli/CommandLineNSGA2Test.java")
CLASS = "org.apache.commons.cli.CommandLineNSGA2Test"

# Each test uses public APIs; the 'object_long' candidate was chosen after
# seeing the fixed/buggy diff, so this run must be labelled exploratory.
CASES = (
    ("short_value", 'Options o=new Options();o.addOption("f", "file", true, "file");CommandLine c=new PosixParser().parse(o,new String[]{"-f","a"});assertEquals("a",c.getOptionValue("f"));'),
    ("long_value", 'Options o=new Options();o.addOption("f", "file", true, "file");CommandLine c=new PosixParser().parse(o,new String[]{"--file","a"});assertEquals("a",c.getOptionValue("file"));'),
    ("missing_value", 'Options o=new Options();o.addOption("f",true,"file");CommandLine c=new PosixParser().parse(o,new String[0]);assertNull(c.getOptionValue("f"));'),
    ("flag", 'Options o=new Options();o.addOption("v",false,"verbose");CommandLine c=new PosixParser().parse(o,new String[]{"-v"});assertTrue(c.hasOption("v"));assertNull(c.getOptionValue("v"));'),
    ("default_missing", 'Options o=new Options();o.addOption("f",true,"file");CommandLine c=new PosixParser().parse(o,new String[0]);assertEquals("fallback",c.getOptionValue("f","fallback"));'),
    ("default_present", 'Options o=new Options();o.addOption("f",true,"file");CommandLine c=new PosixParser().parse(o,new String[]{"-f","a"});assertEquals("a",c.getOptionValue("f","fallback"));'),
    ("multiple_values", 'Options o=new Options();Option f=new Option("f","file",true,"files");f.setArgs(2);o.addOption(f);CommandLine c=new PosixParser().parse(o,new String[]{"-f","one","two"});assertEquals(2,c.getOptionValues("f").length);assertEquals("one",c.getOptionValue("f"));'),
    ("hyphen_lookup", 'Options o=new Options();o.addOption("f","file",true,"file");CommandLine c=new PosixParser().parse(o,new String[]{"--file","a"});assertEquals("a",c.getOptionValue("--file"));'),
    ("object_short", 'Options o=new Options();Option n=new Option("n","number",true,"number");n.setType(PatternOptionBuilder.NUMBER_VALUE);o.addOption(n);CommandLine c=new PosixParser().parse(o,new String[]{"-n","42"});assertEquals(42,((Number)c.getOptionObject("n")).intValue());'),
    ("object_long", 'Options o=new Options();Option n=new Option("n","number",true,"number");n.setType(PatternOptionBuilder.NUMBER_VALUE);o.addOption(n);CommandLine c=new PosixParser().parse(o,new String[]{"--number","42"});assertEquals(42,((Number)c.getOptionObject("number")).intValue());'),
    ("object_missing", 'Options o=new Options();o.addOption("v",false,"verbose");CommandLine c=new PosixParser().parse(o,new String[]{"-v"});assertNull(c.getOptionObject("v"));'),
    ("remaining_args", 'Options o=new Options();CommandLine c=new PosixParser().parse(o,new String[]{"tail"});assertEquals("tail",c.getArgs()[0]);'),
    ("option_iterator", 'Options o=new Options();o.addOption("v",false,"verbose");CommandLine c=new PosixParser().parse(o,new String[]{"-v"});assertEquals(1,c.getOptions().length);assertTrue(c.iterator().hasNext());'),
)


def java_source(genes):
    methods = ["    public void testCase%02d%s() throws Exception { %s }" %
               (i, CASES[i][0].title().replace("_", ""), CASES[i][1]) for i in genes]
    return ("package org.apache.commons.cli;\nimport junit.framework.TestCase;\n"
            "public class CommandLineNSGA2Test extends TestCase {\n"
            + "\n".join(methods) + "\n}\n")


def run(command, log):
    with log.open("w") as output:
        return subprocess.run(command, stdout=output, stderr=subprocess.STDOUT,
                              check=False).returncode


def archive(source, path):
    with tarfile.open(path, "w:bz2") as tar:
        tar.add(source, arcname=str(RELATIVE_TEST))


def dominates(a, b):
    return (a["lines"] >= b["lines"] and a["conditions"] >= b["conditions"]
            and len(a["genes"]) <= len(b["genes"])
            and (a["lines"] > b["lines"] or a["conditions"] > b["conditions"]
                 or len(a["genes"]) < len(b["genes"])))


def fronts(population):
    remaining, result = list(population), []
    while remaining:
        front = [a for a in remaining if not any(
            dominates(b, a) for b in remaining if b is not a)]
        result.append(front)
        remaining = [a for a in remaining if a not in front]
    return result


def crowding(front):
    distances = {id(x): 0.0 for x in front}
    if len(front) < 3:
        return {id(x): float("inf") for x in front}
    for measure in (lambda x: x["lines"], lambda x: x["conditions"],
                    lambda x: -len(x["genes"])):
        ordered = sorted(front, key=measure)
        distances[id(ordered[0])] = distances[id(ordered[-1])] = float("inf")
        span = measure(ordered[-1]) - measure(ordered[0])
        if span:
            for i in range(1, len(ordered) - 1):
                distances[id(ordered[i])] += (
                    measure(ordered[i + 1]) - measure(ordered[i - 1])) / span
    return distances


def select(population, size):
    selected = []
    for front in fronts(population):
        distance = crowding(front)
        selected.extend(sorted(front, key=lambda x: distance[id(x)], reverse=True)
                        [:size - len(selected)])
        if len(selected) >= size:
            break
    return selected


def mutate(genes, rng):
    result = set(genes)
    if rng.random() < .35 and len(result) > 1:
        result.remove(rng.choice(sorted(result)))
    else:
        result.add(rng.randrange(len(CASES)))
    return tuple(sorted(result))


def crossover(a, b, rng):
    pool = sorted(set(a) | set(b))
    child = tuple(x for x in pool if rng.random() < .65)
    return child or (rng.choice(pool),)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--fixed", type=Path, required=True)
    parser.add_argument("--buggy", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--population", type=int, default=6)
    parser.add_argument("--generations", type=int, default=3)
    parser.add_argument("--seed", type=int, default=11)
    args = parser.parse_args()
    if args.population < 2 or args.generations < 1:
        parser.error("population >= 2 and generations >= 1 required")
    if not args.fixed.is_dir() or not args.buggy.is_dir():
        parser.error("Both Defects4J checkouts must already exist")
    if args.output.exists() and any(args.output.iterdir()):
        parser.error("Output must be a new/empty directory")
    args.output.mkdir(parents=True, exist_ok=True)
    logs = args.output / "evaluations"
    logs.mkdir()
    rng, started = random.Random(args.seed), time.monotonic()
    cache, rows = {}, []
    scratch = Path(tempfile.mkdtemp(prefix="nsga2-cli1-"))

    def evaluate(genes):
        genes = tuple(sorted(set(genes)))
        if genes in cache:
            return cache[genes]
        index = len(cache) + 1
        src = scratch / RELATIVE_TEST
        src.parent.mkdir(parents=True, exist_ok=True)
        src.write_text(java_source(genes))
        bundle = scratch / ("Cli-1f-candidate-%03d.tar.bz2" % index)
        archive(src, bundle)
        logfile = logs / ("candidate_%03d.log" % index)
        for report in ("summary.csv", "failing_tests"):
            (args.fixed / report).unlink(missing_ok=True)
        code = run(["defects4j", "coverage", "-w", str(args.fixed),
                    "-s", str(bundle)], logfile)
        summary = args.fixed / "summary.csv"
        failures = args.fixed / "failing_tests"
        valid = (code == 0 and summary.is_file()
                 and not (failures.is_file() and failures.read_text().strip()))
        totals = (0, 0, 0, 0)
        if valid:
            with summary.open(newline="") as stream:
                item = next(csv.DictReader(stream))
            totals = tuple(int(item[key]) for key in (
                "LinesTotal", "LinesCovered", "ConditionsTotal", "ConditionsCovered"))
        individual = dict(genes=genes, lines=totals[1],
                          conditions=totals[3], valid=valid)
        cache[genes] = individual
        rows.append(dict(candidate=index, cases="|".join(CASES[i][0] for i in genes),
                         tests=len(genes), valid_on_fixed=valid,
                         lines_total=totals[0], lines_covered=totals[1],
                         conditions_total=totals[2], conditions_covered=totals[3],
                         log=str(logfile.relative_to(args.output))))
        print("candidate %d: %d tests, %d lines, %d conditions, valid=%s" %
              (index, len(genes), totals[1], totals[3], valid), flush=True)
        return individual

    try:
        population = [evaluate(tuple(sorted(rng.sample(
            range(len(CASES)), rng.randint(3, 7))))) for _ in range(args.population)]
        for generation in range(args.generations):
            children = []
            for _ in range(args.population):
                a, b = rng.sample(population, 2)
                children.append(evaluate(mutate(crossover(a["genes"], b["genes"], rng), rng)))
            population = select(population + children, args.population)
            print("generation %d complete" % (generation + 1), flush=True)
        valid = [item for item in cache.values() if item["valid"]]
        if not valid:
            raise RuntimeError("No valid suite on Cli-1f; inspect evaluations logs")
        best = max(valid, key=lambda x: (x["lines"], x["conditions"], -len(x["genes"])))
        src = args.output / RELATIVE_TEST.name
        src.write_text(java_source(best["genes"]))
        bundle = args.output / "Cli-1-nsga2.1.tar.bz2"
        archive(src, bundle)
        run(["defects4j", "test", "-w", str(args.buggy), "-s", str(bundle)],
            args.output / "buggy_detection.log")
        run(["defects4j", "coverage", "-w", str(args.buggy), "-s", str(bundle)],
            args.output / "final_1b_coverage.log")
        run(["defects4j", "coverage", "-w", str(args.fixed), "-s", str(bundle)],
            args.output / "final_1f_coverage.log")
        with (args.output / "evaluations.csv").open("w", newline="") as stream:
            writer = csv.DictWriter(stream, fieldnames=rows[0])
            writer.writeheader()
            writer.writerows(rows)
        (args.output / "configuration.json").write_text(json.dumps({
            "algorithm": "NSGA-II", "project": "Cli", "bug": 1,
            "study_type": "post-hoc exploratory after reviewing fixed/buggy source diff",
            "seed": args.seed, "population": args.population,
            "generations": args.generations,
            "candidate_cases": [name for name, _ in CASES],
            "fitness": "Cli-1f Defects4J line/condition coverage; minimize tests",
            "evaluated_unique": len(cache),
            "elapsed_seconds": round(time.monotonic() - started, 3),
            "selected_cases": [CASES[i][0] for i in best["genes"]],
            "selected_lines": best["lines"],
            "selected_conditions": best["conditions"],
        }, indent=2) + "\n")
        print("selected:", [CASES[i][0] for i in best["genes"]])
        print("buggy detection log:", args.output / "buggy_detection.log")
    finally:
        shutil.rmtree(scratch)


if __name__ == "__main__":
    main()
