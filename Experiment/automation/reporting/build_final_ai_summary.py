#!/usr/bin/env python3

import csv
import json
import re
from pathlib import Path
from collections import Counter, defaultdict

ROOT = Path(__file__).resolve().parents[3]

METHODS = [
    {
        "method": "ChatGPT",
        "root": ROOT / "AI1_ChatGPT/Result",
        "run": "run-final-opt",
    },
    {
        "method": "GitHub Copilot",
        "root": ROOT / "AI2_GitHubCopilot/Result",
        "run": "run-copilot-final-v2",
    },
]

REPORT_DIR = ROOT / "Report"
DATA_DIR = REPORT_DIR / "data" / "ai"
SUMMARY_DIR = REPORT_DIR / "summaries"

DATA_DIR.mkdir(parents=True, exist_ok=True)
SUMMARY_DIR.mkdir(parents=True, exist_ok=True)

OUT_CSV = DATA_DIR / "case_results.csv"
OUT_JSON = DATA_DIR / "summary.json"
OUT_MD = SUMMARY_DIR / "ai_summary.md"


def pct(covered, total):
    if not isinstance(covered, (int, float)):
        return None
    if not isinstance(total, (int, float)) or total == 0:
        return None
    return covered / total * 100.0


rows = []

for cfg in METHODS:
    method = cfg["method"]
    result_root = cfg["root"]
    run_name = cfg["run"]

    for status_file in sorted(result_root.glob(f"*/{run_name}/case_status.json")):
        case = status_file.parent.parent.name

        m = re.match(r"^(.+)-(\d+)$", case)
        if m:
            project = m.group(1)
            bug_id = int(m.group(2))
        else:
            project = case
            bug_id = None

        try:
            d = json.loads(status_file.read_text())
        except Exception as e:
            rows.append({
                "method": method,
                "case": case,
                "project": project,
                "bug_id": bug_id,
                "status": "BAD_JSON",
                "notes": str(e),
            })
            continue

        fixed_cov = d.get("fixed_coverage") or {}
        buggy_cov = d.get("buggy_coverage") or {}

        token_totals = (d.get("tokens") or {}).get("totals") or {}

        detected_tests = d.get("detected_tests") or []

        row = {
            "method": method,
            "case": case,
            "project": project,
            "bug_id": bug_id,
            "status": d.get("status"),
            "valid_on_fixed": d.get("valid_on_fixed"),
            "fault_detected": d.get("fault_detected"),
            "detected_test_count": len(detected_tests),

            "fixed_lines_total": fixed_cov.get("LinesTotal"),
            "fixed_lines_covered": fixed_cov.get("LinesCovered"),
            "fixed_line_coverage_pct": pct(
                fixed_cov.get("LinesCovered"),
                fixed_cov.get("LinesTotal"),
            ),

            "fixed_conditions_total": fixed_cov.get("ConditionsTotal"),
            "fixed_conditions_covered": fixed_cov.get("ConditionsCovered"),
            "fixed_condition_coverage_pct": pct(
                fixed_cov.get("ConditionsCovered"),
                fixed_cov.get("ConditionsTotal"),
            ),

            "buggy_lines_total": buggy_cov.get("LinesTotal"),
            "buggy_lines_covered": buggy_cov.get("LinesCovered"),
            "buggy_line_coverage_pct": pct(
                buggy_cov.get("LinesCovered"),
                buggy_cov.get("LinesTotal"),
            ),

            "buggy_conditions_total": buggy_cov.get("ConditionsTotal"),
            "buggy_conditions_covered": buggy_cov.get("ConditionsCovered"),
            "buggy_condition_coverage_pct": pct(
                buggy_cov.get("ConditionsCovered"),
                buggy_cov.get("ConditionsTotal"),
            ),

            "prompt_tokens": token_totals.get("prompt_tokens"),
            "completion_tokens": token_totals.get("completion_tokens"),
            "total_tokens": token_totals.get("total_tokens"),

            "token_target": d.get("token_target"),
            "over_token_target": d.get("over_token_target"),
            "run_id": d.get("run_id"),
            "notes": "",
        }

        rows.append(row)


fieldnames = [
    "method",
    "case",
    "project",
    "bug_id",
    "status",
    "valid_on_fixed",
    "fault_detected",
    "detected_test_count",

    "fixed_lines_total",
    "fixed_lines_covered",
    "fixed_line_coverage_pct",

    "fixed_conditions_total",
    "fixed_conditions_covered",
    "fixed_condition_coverage_pct",

    "buggy_lines_total",
    "buggy_lines_covered",
    "buggy_line_coverage_pct",

    "buggy_conditions_total",
    "buggy_conditions_covered",
    "buggy_condition_coverage_pct",

    "prompt_tokens",
    "completion_tokens",
    "total_tokens",

    "token_target",
    "over_token_target",
    "run_id",
    "notes",
]

with OUT_CSV.open("w", newline="", encoding="utf-8") as f:
    writer = csv.DictWriter(f, fieldnames=fieldnames)
    writer.writeheader()
    writer.writerows(rows)


summary = {}

for method in ["ChatGPT", "GitHub Copilot"]:
    rr = [r for r in rows if r["method"] == method]

    status_counts = Counter(r.get("status") for r in rr)

    measured = [
        r for r in rr
        if r.get("fault_detected") is not None
    ]

    detected = [
        r for r in measured
        if r.get("fault_detected") is True
    ]

    coverage_rows = [
        r for r in rr
        if isinstance(r.get("fixed_line_coverage_pct"), (int, float))
    ]

    condition_rows = [
        r for r in rr
        if isinstance(r.get("fixed_condition_coverage_pct"), (int, float))
    ]

    token_rows = [
        r for r in rr
        if isinstance(r.get("total_tokens"), (int, float))
    ]

    summary[method] = {
        "total_cases": len(rr),
        "status_counts": dict(status_counts),

        "fault_detection_measured_cases": len(measured),
        "fault_detected_cases": len(detected),
        "fault_detection_rate_pct": (
            len(detected) / len(measured) * 100
            if measured else None
        ),

        "coverage_measured_cases": len(coverage_rows),
        "average_fixed_line_coverage_pct": (
            sum(r["fixed_line_coverage_pct"] for r in coverage_rows)
            / len(coverage_rows)
            if coverage_rows else None
        ),

        "condition_coverage_measured_cases": len(condition_rows),
        "average_fixed_condition_coverage_pct": (
            sum(r["fixed_condition_coverage_pct"] for r in condition_rows)
            / len(condition_rows)
            if condition_rows else None
        ),

        "token_measured_cases": len(token_rows),
        "total_tokens": (
            sum(r["total_tokens"] for r in token_rows)
            if token_rows else None
        ),
        "average_tokens_per_case": (
            sum(r["total_tokens"] for r in token_rows) / len(token_rows)
            if token_rows else None
        ),
    }


with OUT_JSON.open("w", encoding="utf-8") as f:
    json.dump(summary, f, indent=2, ensure_ascii=False)


def fmt(v, digits=2):
    if v is None:
        return "-"
    if isinstance(v, float):
        return f"{v:.{digits}f}"
    return str(v)



lines = [
    "# สรุปผลการทดลอง ChatGPT และ GitHub Copilot",
    "",
    "## วัตถุประสงค์และขอบเขต",
    "",
    "ศึกษาการสร้าง JUnit unit tests ด้วย ChatGPT และ GitHub Copilot บน Defects4J โดยตรวจความถูกต้องของชุดทดสอบ วัดความครอบคลุมของโค้ด และประเมินการตรวจพบข้อบกพร่อง",
    "",
    "หนึ่ง case ในข้อมูลสรุปหมายถึงหนึ่ง Project–Bug ID ไม่ใช่หนึ่ง test method จำนวนกรณีที่เริ่มทดลองจึงต้องแยกจากจำนวนชุดทดสอบที่ประเมินสำเร็จ",
    "",
    "| วิธี | ชุดผลที่ใช้ |",
    "|---|---|",
    "| ChatGPT | `run-final-opt` |",
    "| GitHub Copilot | `run-copilot-final-v2` |",
    "",
    "ผลของสองวิธีนี้เป็นส่วนหนึ่งของการเปรียบเทียบร่วมกับ NSGA-II และ Symbolic Execution ดู [ผลเปรียบเทียบทั้ง 4 วิธี](final_comparison.md)",
    "",
    "## ขั้นตอนและหลักการวัด",
    "",
    "1. เตรียม source/context จาก buggy version และข้อมูลพฤติกรรมที่คาดหวัง",
    "2. ใช้ P01 วิเคราะห์ และ P02 สร้างชุดทดสอบ",
    "3. ตรวจชุดทดสอบด้วย fixed version และใช้ P03 แก้ไขตามงบการเรียกที่กำหนด",
    "4. วัด coverage และใช้ P04 เพิ่มกรณีทดสอบตามขั้นตอนของ runner",
    "5. ประเมินชุดทดสอบกับ buggy/fixed versions และรวบรวมผลราย case",
    "",
    "รายละเอียด prompt การจัดการ P04 ประวัติการปรับขั้นตอน และสภาพแวดล้อมอยู่ใน [ขั้นตอนการทดลอง](../../Experiment/protocol/ai_final_protocol.md) ขั้นตอนที่ใช้กับผลย้อนหลังตรวจจาก prompt และ stage artifacts ของแต่ละ case",
    "",
    "## ผลการประเมินชุดทดสอบ",
    "",
    "`DONE` หมายถึงชุดทดสอบผ่าน fixed-version validation และมีผลประเมินจาก final evaluation หรือ test-only recovery ไม่ได้หมายความว่ามี coverage หรือตรวจพบ bug ทุกกรณี",
    "",
    "| วิธี | เริ่มทดลอง (cases) | ประเมินสำเร็จ | อัตราประเมินสำเร็จ | ไม่ผ่านหลัง P03 | ไม่ผ่านหลัง P04 | คำตอบไม่ครบ |",
    "|---|---:|---:|---:|---:|---:|---:|",
]

for method, s in summary.items():
    count = s["total_cases"]
    sc = s["status_counts"]
    done = sc.get("DONE", 0)
    rate = f"{done / count * 100:.2f}%" if count else "-"
    lines.append(
        f"| {method} | {count} | {done} | {rate} | "
        f"{sc.get('INVALID_AFTER_REPAIR', 0)} | "
        f"{sc.get('INVALID_AFTER_PROMPT04', 0)} | "
        f"{sc.get('OUTPUT_INCOMPLETE', 0)} |"
    )

lines += [
    "",
    "อัตราประเมินสำเร็จ = จำนวน `DONE` ÷ จำนวน cases ที่เริ่มทดลอง × 100",
    "",
    "สถานะไม่ผ่านหลัง P04 เป็นสถานะที่บันทึกในชุดผล ไม่ควรตีความว่าทุก case ใช้กติกา P04 revision เดียวกัน",
    "",
    "## การตรวจพบข้อบกพร่อง",
    "",
    "| วิธี | Cases ที่มีผลวัด | Cases ที่ตรวจพบ fault | อัตราในกลุ่มที่มีผลวัด | ตรวจพบ fault ต่อ cases ที่เริ่มทดลอง |",
    "|---|---:|---:|---:|---:|",
]

for method, s in summary.items():
    measured = s["fault_detection_measured_cases"]
    detected = s["fault_detected_cases"]
    count = s["total_cases"]
    measured_rate = f"{detected / measured * 100:.2f}%" if measured else "-"
    attempted_rate = f"{detected / count * 100:.2f}%" if count else "-"
    lines.append(
        f"| {method} | {measured} | {detected} | "
        f"{measured_rate} | {attempted_rate} |"
    )

lines += [
    "",
    "อัตราในกลุ่มที่มีผลวัดใช้ cases ที่มี fault-detection result เป็น denominator ส่วนอัตราต่อ cases ที่เริ่มทดลองแสดงผลสำเร็จของกระบวนการโดยรวม ไม่แทนผลที่ไม่มีค่าด้วยข้อสรุปว่าชุดทดสอบตรวจไม่พบ fault",
    "",
    "## ความครอบคลุมของโค้ด",
    "",
    "| วิธี | Cases ที่มี line coverage | Line coverage เฉลี่ย | Cases ที่มี condition coverage | Condition coverage เฉลี่ย |",
    "|---|---:|---:|---:|---:|",
]

def show_pct(value):
    return "-" if value is None else f"{value:.2f}%"

for method, s in summary.items():
    lines.append(
        f"| {method} | {s['coverage_measured_cases']} | "
        f"{show_pct(s['average_fixed_line_coverage_pct'])} | "
        f"{s['condition_coverage_measured_cases']} | "
        f"{show_pct(s['average_fixed_condition_coverage_pct'])} |"
    )

lines += [
    "",
    "Coverage ราย case = จำนวนที่ครอบคลุม ÷ จำนวนทั้งหมด × 100 ตารางแสดงค่าเฉลี่ยราย case บน fixed version โดยเฉลี่ยเฉพาะค่าที่มีจริง ไม่ใช่การรวม covered/total ของทุก case ก่อนหาร",
    "",
    "ใช้ชื่อ Line Coverage และ Condition Coverage ตามข้อมูลที่เครื่องมือรายงาน ไม่เปลี่ยนชื่อเป็น Statement Coverage หรือ Branch Coverage โดยไม่มีการตรวจนิยาม",
    "",
    "## การใช้ token",
    "",
    "| วิธี | Cases ที่มีข้อมูล token | Token รวม | Token เฉลี่ยต่อ case ที่มีข้อมูล |",
    "|---|---:|---:|---:|",
]

for method, s in summary.items():
    total = s["total_tokens"]
    average = s["average_tokens_per_case"]
    total_text = "-" if total is None else f"{total:,}"
    average_text = "-" if average is None else f"{average:,.2f}"
    lines.append(
        f"| {method} | {s['token_measured_cases']} | "
        f"{total_text} | {average_text} |"
    )

lines += [
    "",
    "Token เป็นตัวชี้วัดเพิ่มเติมสำหรับการใช้บริการโมเดล ไม่ใช้แทนเวลาสร้าง tests หรือหน่วยความจำของเครื่อง",
    "",
    "## วิเคราะห์ผล",
    "",
    "จากชุดผลนี้ ChatGPT มีจำนวน cases ที่ประเมินสำเร็จและตรวจพบ fault มากกว่า GitHub Copilot และใช้ token รวมน้อยกว่า",
    "",
    "GitHub Copilot มีอัตราตรวจพบ fault ในกลุ่มที่มีผลวัดสูงกว่าเล็กน้อย แต่กลุ่มดังกล่าวมีขนาดและองค์ประกอบต่างจาก ChatGPT จึงยังใช้สรุปว่า Copilot ตรวจพบข้อบกพร่องได้ดีกว่าโดยรวมไม่ได้",
    "",
    "ค่าเฉลี่ย coverage คำนวณจาก cases ที่มีค่าของแต่ละวิธี การเปรียบเทียบโดยตรงควรเพิ่มการวิเคราะห์เฉพาะ Project–Bug ID ที่ทั้งสองวิธีมีผลวัดร่วมกัน",
    "",
    "## ปัญหาและข้อจำกัด",
    "",
    "- พบคำตอบไม่ครบและชุดทดสอบที่ไม่ผ่าน validation ดังแสดงในตารางสถานะ",
    "- จำนวน cases ที่มี coverage และ fault-detection result ไม่เท่ากัน ต้องรายงาน denominator ของแต่ละค่า",
    "- จำนวน test methods ใน source กับจำนวน tests ที่รันจริงต้องแยกกัน ข้อมูลสรุปปัจจุบันยังไม่มีจำนวน tests ที่รัน/ผ่าน/ล้มเหลวครบทุก case จึงยังไม่ประมาณจำนวนดังกล่าว",
    "- ผลอาจได้รับอิทธิพลจากโมเดล context budget และขั้นตอนแก้ไข จึงต้องอ้างอิง configuration ควบคู่กับชื่อเครื่องมือ",
    "",
    "## สิ่งที่เรียนรู้",
    "",
    "การสร้าง test code ได้ไม่เพียงพอสำหรับประเมินเครื่องมือ ต้องตรวจ compile/run วัด coverage และตรวจ fault detection ด้วยหลักฐานจริง รวมทั้งแยกความสำเร็จในการสร้างชุดทดสอบออกจากความสามารถของชุดที่ประเมินได้",
    "",
    "## ข้อมูลและหลักฐานสำหรับทำซ้ำ",
    "",
    "- [ข้อมูลราย case](../data/ai/case_results.csv)",
    "- [ค่าสรุป JSON](../data/ai/summary.json)",
    "- [ChatGPT: prompt, test code และผลทดลอง](../../AI1_ChatGPT/)",
    "- [GitHub Copilot: prompt, test code และผลทดลอง](../../AI2_GitHubCopilot/)",
    "- [ขั้นตอนและสภาพแวดล้อม](../../Experiment/protocol/ai_final_protocol.md)",
    "- [สคริปต์สร้างสรุป](../../Experiment/automation/reporting/build_final_ai_summary.py)",
]


# BEGIN AI ADDITIONAL METRICS
import math

def measured_number(value):
    try:
        value = float(value)
        return value if math.isfinite(value) and value >= 0 else None
    except (ValueError, TypeError):
        return None

lines += [
    "",
    "## เวลาเรียกบริการ AI",
    "",
    "| วิธี | Cases ที่มีเวลาครบใน metadata | เวลา P01–P04 รวมเฉลี่ย (วินาที) | Cases ที่มีเวลา P02 | เวลา P02 เฉลี่ย (วินาที) |",
    "|---|---:|---:|---:|---:|",
]

for method, folder, run in [
    ("ChatGPT", "AI1_ChatGPT", "run-final-opt"),
    ("GitHub Copilot", "AI2_GitHubCopilot", "run-copilot-final-v2"),
]:
    totals, p02 = [], []
    for status_path in sorted(
        (ROOT / folder / "Result").glob(f"*/{run}/case_status.json")
    ):
        metas = sorted(status_path.parent.glob("0[1-4]_metadata.json"))
        times = []
        for path in metas:
            meta = json.loads(path.read_text(encoding="utf-8"))
            value = measured_number(meta.get("elapsed_seconds"))
            if value is not None:
                times.append(value)
                if path.name.startswith("02"):
                    p02.append(value)
        if metas and len(times) == len(metas):
            totals.append(sum(times))
    total_mean = f"{sum(totals)/len(totals):.2f}" if totals else "-"
    p02_mean = f"{sum(p02)/len(p02):.2f}" if p02 else "-"
    lines.append(
        f"| {method} | {len(totals)} | {total_mean} | "
        f"{len(p02)} | {p02_mean} |"
    )

lines += [
    "",
    "คำนวณจาก elapsed_seconds ของ stage metadata ที่จัดเก็บในแต่ละ case รวมเฉพาะ stages ที่ถูกเรียก ไม่รวมเวลา compile/test/coverage เวลารอคิว และประวัติการ retry ที่ไม่ได้อยู่ใน metadata ชุดนี้ จึงเป็นเวลาเรียกบริการ AI ไม่ใช่เวลาทดลองทั้งหมด",
    "",
    "## ผลเปรียบเทียบเฉพาะ cases ร่วมกัน",
    "",
    "เลือก Project–Bug ID ที่ทั้งสองวิธีมีสถานะ DONE และมีค่าของตัวชี้วัดนั้นจริง จำนวน cases จึงอาจต่างกันระหว่างตัวชี้วัด",
    "",
    "| ตัวชี้วัด | Cases ร่วมกัน | ChatGPT | GitHub Copilot |",
    "|---|---:|---:|---:|",
]

groups = {}
for method in ("ChatGPT", "GitHub Copilot"):
    selected = [r for r in rows if r["method"] == method]
    group = {r["case"]: r for r in selected}
    if len(group) != len(selected):
        raise ValueError(f"Duplicate cases: {method}")
    groups[method] = group

for field, label in [
    ("fault_detected", "ตรวจพบ fault"),
    ("fixed_line_coverage_pct", "Line coverage เฉลี่ย"),
    ("fixed_condition_coverage_pct", "Condition coverage เฉลี่ย"),
]:
    def valid(row):
        value = row.get(field)
        if field == "fault_detected":
            return str(value).lower() in ("true", "false")
        return measured_number(value) is not None

    left = groups["ChatGPT"]
    right = groups["GitHub Copilot"]
    common = sorted(
        case for case in left.keys() & right.keys()
        if left[case]["status"] == right[case]["status"] == "DONE"
        and valid(left[case]) and valid(right[case])
    )
    values = []
    for group in (left, right):
        if not common:
            values.append("-")
        elif field == "fault_detected":
            count = sum(
                str(group[c][field]).lower() == "true"
                for c in common
            )
            values.append(f"{count} ({count/len(common)*100:.2f}%)")
        else:
            mean = sum(float(group[c][field]) for c in common) / len(common)
            values.append(f"{mean:.2f}%")
    lines.append(
        f"| {label} | {len(common)} | {values[0]} | {values[1]} |"
    )

lines += [
    "",
    "ผลตารางนี้เปรียบเทียบบน cases ชุดเดียวกันของแต่ละตัวชี้วัด เป็นสถิติเชิงพรรณนา ไม่ใช่ข้อสรุปความแตกต่างอย่างมีนัยสำคัญทางสถิติ",
    "",
    "## ข้อจำกัดของ coverage และหลักฐาน recovery",
    "",
    "ChatGPT Jsoup-4/Jsoup-9 และ GitHub Copilot Jsoup-4/Jsoup-6 มีหลักฐาน compile/run บน fixed และ buggy แต่ Cobertura 2.0.3 instrument Entities.class ไม่สำเร็จ โดยแจ้ง Method code too large",
    "",
    "กรณีเหล่านี้ใช้ผลทดสอบสำหรับประเมิน fault detection และเก็บ coverage เป็นค่าที่ไม่มีข้อมูล ไม่แทนด้วย 0 หลักฐานอยู่ในโฟลเดอร์ coverage-recovery และ logs ที่ case_status.json อ้างอิง",
    "",
    "ดังนั้น DONE ในชุดข้อมูลนี้หมายถึงมีผลประเมินชุดทดสอบ ซึ่งอาจเป็น final result.json หรือหลักฐาน test-only recovery ไม่รับประกันว่ามี coverage ทุกกรณี",
    "",
    "กรณีที่ประเมินซ้ำเพื่อแก้ปัญหาระบบให้ใช้ final_result ที่ case_status.json อ้างอิง ไม่เลือกผลจากชื่อโฟลเดอร์มาตรฐานหรือจากวิธีทดลองอื่น",
]
# END AI ADDITIONAL METRICS

# BEGIN JSOUP71 SOURCE VERIFICATION NOTE
lines.extend(['', '## การตรวจสอบหลักฐานชุดทดสอบ', '', 'ChatGPT Jsoup-71 มี hash ใน `04_validation.json` ไม่ตรงกับ Java ที่ใช้ประเมินสุดท้าย การตรวจสอบพบว่า Java ปัจจุบัน สำเนาในโฟลเดอร์ประเมิน และ Java ใน archive มี hash ตรงกับ `result.json` โดย logs ยืนยันว่า fixed ผ่านและ buggy ล้มเหลว 7 tests จึงใช้หลักฐานการประเมินสุดท้ายรับรองผล และเก็บ P04 validation เดิมไว้ สาเหตุของ hash ที่ต่างกันยังไม่ได้ยืนยัน รายละเอียดอยู่ใน [หลักฐานตรวจสอบ Jsoup-71](../../AI1_ChatGPT/Result/Jsoup-71/run-final-opt/final_source_verification.json)', ''])
# END JSOUP71 SOURCE VERIFICATION NOTE

OUT_MD.write_text("\n".join(lines) + "\n", encoding="utf-8")
