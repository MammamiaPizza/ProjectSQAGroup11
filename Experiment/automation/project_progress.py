import json, re
from pathlib import Path
from collections import Counter, defaultdict

PROJECTS = [
    ("Chart",26), ("Cli",39), ("Closure",174), ("Codec",18),
    ("Collections",28), ("Compress",47), ("Csv",16), ("Gson",18),
    ("JacksonCore",26), ("JacksonDatabind",110), ("JacksonXml",6),
    ("Jsoup",93), ("JxPath",22), ("Lang",61), ("Math",106),
    ("Mockito",38), ("Time",26),
]

RUNS = {
    "W": {
        "result": Path("AI1_ChatGPT/Result"),
        "run": "run-final-opt",
        "logs": Path("Experiment/logs/final-opt"),
        "prefix": "W",
    },
    "C": {
        "result": Path("AI2_GitHubCopilot/Result"),
        "run": "run-copilot-final-v2",
        "logs": Path("Experiment/logs/copilot-final-v2"),
        "prefix": "C",
    },
}

def project_of(case):
    m = re.match(r"(.+)-(\d+)$", case)
    return m.group(1) if m else None

def results(cfg):
    stats = defaultdict(Counter)
    terminal_cases = set()

    for p in cfg["result"].glob(f"*/{cfg['run']}/case_status.json"):
        try:
            d = json.loads(p.read_text())
        except:
            continue

        case = p.parent.parent.name
        proj = project_of(case)
        if not proj:
            continue

        status = d.get("status", "UNKNOWN")
        terminal_cases.add(case)

        if status == "DONE":
            stats[proj]["D"] += 1
        elif "INVALID" in status or "OUTPUT_INCOMPLETE" in status:
            stats[proj]["I"] += 1
        elif status == "ERROR":
            stats[proj]["E"] += 1
        else:
            stats[proj]["O"] += 1

    return stats, terminal_cases

def active_cases(cfg, terminal):
    active = set()

    for log in cfg["logs"].glob(f"{cfg['prefix']}*.log"):
        case = None

        try:
            lines = log.read_text(errors="ignore").splitlines()
        except:
            continue

        for line in lines:
            m = re.search(r"\]\s+CLAIMED\s+(\S+)", line)
            if m:
                case = m.group(1)

            if case and re.search(rf"{re.escape(case)}\s+->\s+", line):
                case = None

            if "QUEUE COMPLETE" in line:
                case = None

        if case and case not in terminal:
            active.add(case)

    return active

data = {}

for name,cfg in RUNS.items():
    stats, terminal = results(cfg)
    active = active_cases(cfg, terminal)
    data[name] = (stats, terminal, active)

print("=" * 100)
print("17 PROJECTS — BENCHMARK PROGRESS")
print("=" * 100)
print(
    f"{'PROJECT':<18} {'TOTAL':>5} | "
    f"{'W D':>4} {'W I':>4} {'W E':>4} {'W A':>4} {'W LEFT':>6} | "
    f"{'C D':>4} {'C I':>4} {'C E':>4} {'C A':>4} {'C LEFT':>6}"
)
print("-" * 100)

totals = {
    "W": Counter(),
    "C": Counter(),
}

for proj,total in PROJECTS:
    row = []

    for name in ("W","C"):
        stats, terminal, active = data[name]

        d = stats[proj]["D"]
        i = stats[proj]["I"]
        e = stats[proj]["E"]
        o = stats[proj]["O"]

        a = sum(1 for c in active if project_of(c) == proj)
        finished = d+i+e+o
        left = max(0, total - finished - a)

        totals[name]["D"] += d
        totals[name]["I"] += i
        totals[name]["E"] += e
        totals[name]["A"] += a
        totals[name]["L"] += left

        row.append((d,i,e,a,left))

    w,c = row

    print(
        f"{proj:<18} {total:>5} | "
        f"{w[0]:>4} {w[1]:>4} {w[2]:>4} {w[3]:>4} {w[4]:>6} | "
        f"{c[0]:>4} {c[1]:>4} {c[2]:>4} {c[3]:>4} {c[4]:>6}"
    )

print("-" * 100)

w = totals["W"]
c = totals["C"]

print(
    f"{'TOTAL':<18} {sum(n for _,n in PROJECTS):>5} | "
    f"{w['D']:>4} {w['I']:>4} {w['E']:>4} {w['A']:>4} {w['L']:>6} | "
    f"{c['D']:>4} {c['I']:>4} {c['E']:>4} {c['A']:>4} {c['L']:>6}"
)

print()
print("D=Done  I=Invalid/Incomplete  E=Error  A=Active  LEFT=ยังไม่ terminal/ไม่กำลังรัน")
