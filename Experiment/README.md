# Experiment

โฟลเดอร์นี้เป็นพื้นที่ส่วนกลางสำหรับการดำเนินการ ประเมินผล และสรุปผลการทดลองของโครงการ

ผลดิบและรายละเอียดของแต่ละวิธีเก็บแยกไว้ในโฟลเดอร์ของวิธีนั้นโดยตรง

- `Algorithm1_NSGAII/` — NSGA-II
- `Algorithm2_SymbolicExecution/` — Symbolic Execution
- `AI1_ChatGPT/` — ChatGPT
- `AI2_GitHubCopilot/` — GitHub Copilot

`Experiment/` จึงไม่ใช่โฟลเดอร์ผลของวิธีใดวิธีหนึ่ง แต่ใช้เก็บข้อมูลและเครื่องมือที่เกี่ยวข้องกับการทดลองและการเปรียบเทียบร่วมกัน

## โครงสร้าง

- `protocol/` — กติกาและขั้นตอนการทดลองที่ใช้กับผลสุดท้าย
- `automation/` — script สำหรับรัน ตรวจสอบ ประเมิน และสรุปผล
- `contexts/` — context ที่สร้างจาก Defects4J buggy version สำหรับการทดลอง AI
- `evaluations/` — หลักฐานการประเมิน test suite บน fixed และ buggy versions
- `reports/` — ผลสรุปและข้อมูลสำหรับเปรียบเทียบแต่ละวิธี
- `archive/` — pilot, migration และข้อมูลจากช่วงพัฒนาที่ไม่ใช้เป็นผลสุดท้าย

## Source ของผลแต่ละวิธี

ผลรายละเอียดของแต่ละวิธีให้อ้างอิงจากโฟลเดอร์ต้นทางของวิธีนั้น ไม่คัดลอก raw result มาซ้ำใน `Experiment/`

| วิธี | แหล่งข้อมูลหลัก |
|---|---|
| NSGA-II | `Algorithm1_NSGAII/` |
| Symbolic Execution | `Algorithm2_SymbolicExecution/` |
| ChatGPT | `AI1_ChatGPT/` |
| GitHub Copilot | `AI2_GitHubCopilot/` |

เมื่อจัดทำผลเปรียบเทียบรวม จะนำเฉพาะค่าที่สรุปแล้วจากแต่ละวิธีมาไว้ใน `reports/`

## ผล AI ขั้นสุดท้าย

- ChatGPT: `run-final-opt`
- GitHub Copilot: `run-copilot-final-v2`

ผลสรุป AI ปัจจุบันอยู่ที่

- `reports/ai_case_results.csv`
- `reports/ai_summary.json`
- `reports/ai_summary.md`

ข้อมูล pilot หรือ run รุ่นก่อนหน้ายังคงเก็บไว้เพื่อการตรวจสอบย้อนหลัง แต่ไม่ใช้เป็น source-of-truth ของผล AI ขั้นสุดท้าย
