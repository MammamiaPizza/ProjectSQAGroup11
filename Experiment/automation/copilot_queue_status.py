import json, re
from pathlib import Path

LOGDIR = Path("Experiment/logs/copilot-final-v2")
RESULT = Path("AI2_GitHubCopilot/Result")
RUN = "run-copilot-final-v2"

projects = [
    ("Csv",16), ("Codec",18), ("Gson",18), ("JxPath",22),
    ("Chart",26), ("JacksonCore",26), ("Time",26),
    ("Collections",28), ("Mockito",38), ("Cli",39),
    ("Compress",47), ("Lang",61), ("Jsoup",93),
    ("Math",106), ("JacksonDatabind",110),
    ("Closure",174), ("JacksonXml",6),
]

queue = [f"{p}-{i}" for p,n in projects for i in range(1,n+1)]

print("=" * 72)
print("COPILOT WORKERS — CURRENT")
print("=" * 72)
print(f"{'WORKER':<7} {'STATE':<9} {'CASE':<22} {'STAGE':<7}")
print("-" * 72)

active = set()

def worker_num(p):
    m = re.search(r'\d+', p.stem)
    return int(m.group()) if m else 999

for pf in sorted(LOGDIR.glob("C*.pid"), key=worker_num):
    worker = pf.stem

    try:
        pid = int(pf.read_text().strip())
    except:
        pid = 0

    alive = pid and Path(f"/proc/{pid}").exists()
    state = "RUNNING" if alive else "STOPPED"

    log = LOGDIR / f"{worker}.log"
    case = "-"
    stage = "-"

    if log.exists():
        lines = log.read_text(errors="ignore").splitlines()

        for line in lines:
            m = re.search(r'\]\s+CLAIMED\s+(\S+)', line)
            if m:
                case = m.group(1)
                stage = "CLAIM"

            if case != "-":
                if "Prompt 01" in line:
                    stage = "P01"
                elif "Prompt 02" in line:
                    stage = "P02"
                elif "Prompt 03" in line:
                    stage = "P03"
                elif "Prompt 04" in line:
                    stage = "P04"
                elif "acquired Defects4J" in line:
                    stage = "D4J"

                if re.search(rf'{re.escape(case)}\s+->\s+', line):
                    case = "-"
                    stage = "-"

            if "QUEUE COMPLETE" in line:
                case = "-"
                stage = "DONE"

    if alive and case != "-":
        active.add(case)

    print(f"{worker:<7} {state:<9} {case:<22} {stage:<7}")

terminal = set()

for p in RESULT.glob(f"*/{RUN}/case_status.json"):
    try:
        json.loads(p.read_text())
        terminal.add(p.parent.parent.name)
    except:
        pass

pending = [
    case for case in queue
    if case not in terminal and case not in active
]

print()
print("=" * 72)
print("QUEUE")
print("=" * 72)
print(f"Finished : {len(terminal)}")
print(f"Running  : {len(active)}")
print(f"Waiting  : {len(pending)}")

print("\nNEXT 20")
for i, case in enumerate(pending[:20], 1):
    print(f"{i:2}. {case}")
