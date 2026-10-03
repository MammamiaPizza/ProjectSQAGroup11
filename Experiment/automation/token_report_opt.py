#!/usr/bin/env python3
import json, statistics
from pathlib import Path
ROOT = Path(__file__).resolve().parents[2]
RUN = "final-opt"
rows=[]
stages={s:[] for s in ("01","02","03","04")}
for status_path in (ROOT/"AI1_ChatGPT"/"Result").glob(f"*/run-{RUN}/case_status.json"):
    try: d=json.loads(status_path.read_text())
    except Exception: continue
    result_dir=status_path.parent
    total=((d.get("tokens") or {}).get("totals") or {}).get("total_tokens") or 0
    rows.append((status_path.parents[1].name,d.get("status"),total))
    for s in stages:
        mp=result_dir/f"{s}_metadata.json"
        if mp.is_file():
            try:
                v=json.loads(mp.read_text()).get("total_tokens")
                if isinstance(v,int): stages[s].append(v)
            except Exception: pass
vals=[r[2] for r in rows if r[2]>0]
print(f"run={RUN} cases={len(rows)} measured_token_cases={len(vals)}")
if vals:
    n_ok=sum(v<=13000 for v in vals)
    print(f"total_tokens={sum(vals):,}")
    print(f"mean={statistics.mean(vals):,.0f} median={statistics.median(vals):,.0f} target=13,000")
    print(f"<=13k={n_ok}/{len(vals)} ({100*n_ok/len(vals):.1f}%)")
for s,v in stages.items():
    if v: print(f"P{s}: n={len(v)} mean={statistics.mean(v):,.0f} median={statistics.median(v):,.0f}")
print("\ncase,status,tokens")
for case,status,t in sorted(rows): print(f"{case},{status},{t}")
