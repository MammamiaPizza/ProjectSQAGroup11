# Experiment

โฟลเดอร์นี้เก็บเครื่องมือและหลักฐานส่วนกลางที่ใช้ในการดำเนินการและประเมินผลการทดลอง

ผลของแต่ละวิธีแยกเก็บไว้ในโฟลเดอร์ของวิธีนั้นโดยตรง ได้แก่

- `Algorithm1_NSGAII/`
- `Algorithm2_SymbolicExecution/`
- `AI1_ChatGPT/`
- `AI2_GitHubCopilot/`

ดังนั้น `Experiment/` ไม่ได้ใช้เก็บผลของวิธีใดวิธีหนึ่ง แต่ใช้สำหรับข้อมูลและขั้นตอนที่เกี่ยวข้องกับการประเมินผลร่วมกัน

## โครงสร้าง

- `protocol/` กติกาและขั้นตอนการทดลองฉบับที่ใช้กับผลสุดท้าย
- `automation/` script สำหรับรัน ตรวจสอบ และสรุปผลการทดลอง
- `contexts/` context ที่สร้างจาก Defects4J buggy version สำหรับการทดลอง AI
- `evaluations/` ผลการประเมินชุดทดสอบบน fixed และ buggy versions
- `reports/` ผลสรุปที่สร้างจากข้อมูลการทดลอง
- `archive/` ข้อมูล pilot และไฟล์จากขั้นตอนพัฒนาที่ไม่ใช้เป็นผลสุดท้าย

## ผล AI ที่ใช้ในรายงาน

- ChatGPT: `run-final-opt`
- GitHub Copilot: `run-copilot-final-v2`

ผลสรุปหลักอยู่ที่

- `reports/final_case_results.csv`
- `reports/final_summary.json`
- `reports/final_summary.md`

ข้อมูล pilot หรือ run รุ่นก่อนหน้ายังคงเก็บไว้เพื่อการตรวจสอบย้อนหลัง แต่ไม่ใช้เป็น source-of-truth ของผล AI ขั้นสุดท้าย
