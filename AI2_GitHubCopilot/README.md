# ผลการทดลอง GitHub Copilot

โฟลเดอร์นี้เก็บผลการทดลองสร้าง Test Case ด้วย GitHub Copilot CLI

รอบที่ใช้เป็นผลสุดท้าย: `copilot-final-v2`

การตั้งค่าหลัก:
- GitHub Copilot CLI
- BYOK
- โมเดล `deepseek-v4-pro`

## โครงสร้าง

- `Prompt/` prompt ที่ใช้ในแต่ละเคส
- `Result/` ผลการรัน สถานะ และข้อมูล token
- `TestCode/` test code ที่สร้างจากแต่ละเคส

ชื่อโฟลเดอร์ใช้รูปแบบของ Defects4J เช่น `Lang-1`, `Math-75`, `Time-1`

ผลประเมินเพิ่มเติมอยู่ที่ `Experiment/evaluations/`
