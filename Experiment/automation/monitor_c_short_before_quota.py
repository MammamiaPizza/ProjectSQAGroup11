import json, re, os
from pathlib import Path

LOGDIR = Path("Experiment/logs/copilot-final-v2")
RESULT = Path("AI2_GitHubCopilot/Result")
RUN = "run-copilot-final-v2"
TOTAL = 854
QUOTA = 1_000_000

def fmt(n):
    if n is None: return "-"
    if n >= 1_000_000: return f"{n/1_000_000:.2f}M"
    if n >= 1000: return f"{n/1000:.1f}k"
    return str(n)

def num(p):
    m = re.search(r'\d+', p.stem)
    return int(m.group()) if m else 999

print("="*84)
print("COPILOT WORKERS — CURRENT")
print("="*84)
print(f"{'WORKER':<7}{'STATE':<10}{'CASE':<20}{'STAGE':<7}{'RAM':<7}{'USED':>9}{'LEFT~':>10}")
print("-"*84)

active = set()

for pf in sorted(LOGDIR.glob("C*.pid"), key=num):
    w = pf.stem
    log = LOGDIR / f"{w}.log"

    try:
        pid = int(re.sub(r'\D', '', pf.read_text()))
    except:
        pid = 0

    alive = pid and Path(f"/proc/{pid}").exists()

    case = "-"
    stage = "-"
    used = 0
    last_quota = -1
    last_work = -1

    lines = log.read_text(errors="ignore").splitlines() if log.exists() else []

    for i, line in enumerate(lines):
        m = re.search(r'\]\s+CLAIMED\s+(\S+)', line)
        if m:
            case = m.group(1)
            stage = "CLAIM"
            last_work = i

        if case != "-":
            if "Prompt 01" in line:
                stage = "P01"; last_work = i
            elif "Prompt 02" in line:
                stage = "P02"; last_work = i
            elif "Prompt 03" in line:
                stage = "P03"; last_work = i
            elif "Prompt 04" in line:
                stage = "P04"; last_work = i
            elif "acquired Defects4J" in line:
                stage = "D4J"; last_work = i

            if re.search(rf'{re.escape(case)}\s+->\s+', line):
                case = "-"
                stage = "-"
                last_work = i

        if "QUEUE COMPLETE" in line:
            case = "-"
            stage = "DONE"
            last_work = i

        if re.search(
            r'quota|daily.*limit|rate.*limit|429',
            line, re.I
        ):
            last_quota = i

        s = line.strip()
        if s.startswith("{") and s.endswith("}"):
            try:
                d = json.loads(s)
                if isinstance(d.get("total_tokens"), int):
                    used += d["total_tokens"]
            except:
                pass

    if not alive:
        state = "STOPPED"
        ram = "-"
    else:
        try:
            rss = int(os.popen(
                f"ps -p {pid} -o rss="
            ).read().strip() or 0)
            ram = f"{rss/1024:.0f}M"
        except:
            ram = "-"

        if last_quota >= last_work and last_quota >= 0:
            state = "QUOTA"
            stage = "WAIT"
        else:
            state = "RUNNING"

    if alive and case != "-":
        active.add(case)

    left = max(0, QUOTA - used)

    print(
        f"{w:<7}{state:<10}{case:<20}{stage:<7}"
        f"{ram:<7}{fmt(used):>9}{fmt(left):>10}"
    )

finished = 0

for p in RESULT.glob(f"*/{RUN}/case_status.json"):
    try:
        json.loads(p.read_text())
        finished += 1
    except:
        pass

waiting = max(0, TOTAL - finished - len(active))

print()
print("="*84)
print("QUEUE")
print("="*84)
print(f"Finished : {finished}")
print(f"Running  : {len(active)}")
print(f"Waiting  : {waiting}")

import subprocess
subprocess.run(
    ["python3", "Experiment/automation/pool_status_short.py", "C"],
    check=False
)

