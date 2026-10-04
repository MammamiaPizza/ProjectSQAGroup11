# Prompt สำหรับ ChatGPT

โฟลเดอร์นี้เก็บ Prompt ที่ใช้ในการทดลองสร้าง JUnit test ด้วย ChatGPT บน Defects4J

## Master Prompt

ไฟล์ต่อไปนี้ที่อยู่โดยตรงในโฟลเดอร์ `Prompt/` เป็น Master Prompt Template ของการทดลอง

- `01_analyze_context.txt` — วิเคราะห์ข้อมูลของ bug และวางแผนการทดสอบ
- `02_generate_suite.txt` — สร้าง JUnit test suite
- `03_repair_suite.txt` — แก้ไข test suite เมื่อไม่ผ่าน fixed-version validation โดยอนุญาตให้ repair ด้วย AI เพียง 1 รอบ
- `04_extend_coverage.txt` — เพิ่ม test จากข้อมูล coverage เมื่อเข้าเงื่อนไข

Master Prompt ทั้ง 4 ไฟล์ใช้ข้อความเดียวกับ Master Prompt ของ GitHub Copilot เพื่อให้การเปรียบเทียบระหว่าง AI ทั้งสองอยู่ภายใต้คำสั่งเดียวกัน

## Prompt ที่ส่งจริง

ก่อนส่งให้โมเดล ระบบจะนำ Master Prompt มาเติมข้อมูลของแต่ละ Defects4J case เช่น project, bug ID, target class, API, bug information, source context และผลจากขั้นตอนก่อนหน้า

Prompt ที่ถูกเติมข้อมูลและส่งจริงจะถูกเก็บแยกตาม case เช่น

`AI1_ChatGPT/Prompt/<project>-<bug>/run-final-opt/`

ดังนั้นไฟล์ภายในโฟลเดอร์ของแต่ละ case เป็นหลักฐานของ Prompt ที่โมเดลได้รับจริง ไม่ใช่ Master Template

## Final Experiment

- Final run: `run-final-opt`
- Model: `gpt-5.6-terra`
- ใช้ข้อมูลจาก buggy version เท่านั้นในการสร้าง context
- ไม่ส่ง fixed source code ให้โมเดล
- P03 และ P04 เป็นขั้นตอนตามเงื่อนไข จึงไม่ได้เกิดขึ้นในทุก case
- Model failure จะไม่ถูก rerun เพียงเพื่อให้ได้ผลลัพธ์ที่ดีขึ้น
- Infrastructure failure สามารถ recovery ได้โดยแยกออกจากผลของโมเดล

รายละเอียด workflow และกติกาการทดลองฉบับสุดท้ายอยู่ที่

`Experiment/protocol/ai_final_protocol.md`
