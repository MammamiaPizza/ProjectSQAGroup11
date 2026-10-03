# ผลการทดลอง ChatGPT

โฟลเดอร์นี้เก็บผลการทดลองสร้าง Test Case ด้วย ChatGPT บน Defects4J

รอบที่ใช้เป็นผลสุดท้าย: `run-final-opt`

## โครงสร้าง

- `Prompt/` prompt ที่ใช้ในแต่ละเคส
- `Result/` ผลการรัน สถานะ และข้อมูล token
- `TestCode/` test code ที่สร้างจากแต่ละเคส

ชื่อโฟลเดอร์ใช้รูปแบบของ Defects4J เช่น `Lang-1`, `Math-75`, `Time-1`

ผลประเมินเพิ่มเติมอยู่ที่ `Experiment/evaluations/`
