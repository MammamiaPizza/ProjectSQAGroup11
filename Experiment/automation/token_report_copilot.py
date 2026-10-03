#!/usr/bin/env python3
import argparse, json, statistics
from pathlib import Path
ap=argparse.ArgumentParser()
ap.add_argument('--run-id', default='copilot-final-v2')
a=ap.parse_args()
ROOT=Path(__file__).resolve().parents[2]
RUN=a.run_id
rows=[]; stages={s:[] for s in ('01','02','03','04')}; all_stage=[]
base=ROOT/'AI2_GitHubCopilot'/'Result'
for status_path in base.glob(f'*/run-{RUN}/case_status.json'):
    try: d=json.loads(status_path.read_text())
    except Exception: continue
    result_dir=status_path.parent
    total=((d.get('tokens') or {}).get('totals') or {}).get('total_tokens') or 0
    rows.append((status_path.parents[1].name,d.get('status'),total))
    for s in stages:
        mp=result_dir/f'{s}_metadata.json'
        if mp.is_file():
            try:
                v=json.loads(mp.read_text()).get('total_tokens')
                if isinstance(v,int): stages[s].append(v)
            except Exception: pass
for mp in base.glob(f'*/run-{RUN}/[0-9][0-9]_metadata.json'):
    try:
        d=json.loads(mp.read_text()); v=d.get('total_tokens')
        if isinstance(v,int): all_stage.append((mp.parents[1].name,mp.stem[:2],v))
    except Exception: pass
vals=[r[2] for r in rows if r[2]>0]
print(f'provider=copilot-byok model=deepseek-v4-pro run={RUN}')
print(f'status_cases={len(rows)} measured_status_cases={len(vals)} api_calls_with_tokens={len(all_stage)}')
if all_stage: print(f'live_total_tokens={sum(x[2] for x in all_stage):,}')
if vals:
    print(f'terminal_total_tokens={sum(vals):,}')
    print(f'mean_terminal={statistics.mean(vals):,.0f} median_terminal={statistics.median(vals):,.0f}')
for s,v in stages.items():
    if v: print(f'P{s}: n={len(v)} mean={statistics.mean(v):,.0f} median={statistics.median(v):,.0f}')
print('\ncase,status,tokens')
for case,status,t in sorted(rows): print(f'{case},{status},{t}')
