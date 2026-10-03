# Experiment Reports

โฟลเดอร์นี้ใช้เก็บผลสรุปสำหรับการเปรียบเทียบวิธีสร้างชุดทดสอบทั้ง 4 วิธี ได้แก่

- NSGA-II
- Symbolic Execution
- ChatGPT
- GitHub Copilot

ผลดิบของแต่ละวิธีจะยังเก็บอยู่ในโฟลเดอร์ของวิธีนั้นโดยตรง

- `Algorithm1_NSGAII/`
- `Algorithm2_SymbolicExecution/`
- `AI1_ChatGPT/`
- `AI2_GitHubCopilot/`

โฟลเดอร์ `Experiment/reports/` ใช้สำหรับเก็บเฉพาะผลที่สรุปแล้วและข้อมูลที่ต้องนำมาเปรียบเทียบร่วมกัน ไม่ใช้เก็บ raw result ซ้ำ

## โครงสร้างผลสรุป

ผลของแต่ละวิธีควรถูกสรุปให้อยู่ในรูปแบบที่สามารถนำมาเปรียบเทียบกันได้ เช่น

- จำนวนกรณีทดลอง
- จำนวน test ที่สร้าง
- Fault detection
- Line coverage
- Condition coverage
- Generation time เมื่อมีข้อมูล
- Resource usage ที่เกี่ยวข้อง เช่น token สำหรับวิธี AI

ผลที่ใช้เปรียบเทียบต้องระบุจำนวนกรณีที่วัดได้จริงและเงื่อนไขของการทดลองให้ชัดเจน

## ผลของแต่ละวิธี

รายละเอียดและหลักฐานของแต่ละวิธีให้อ้างอิงจากโฟลเดอร์ต้นทาง

| วิธี | แหล่งข้อมูล |
|---|---|
| NSGA-II | `Algorithm1_NSGAII/` |
| Symbolic Execution | `Algorithm2_SymbolicExecution/` |
| ChatGPT | `AI1_ChatGPT/` |
| GitHub Copilot | `AI2_GitHubCopilot/` |

เมื่อผลของแต่ละวิธีพร้อม จะนำค่าที่สรุปแล้วมาใช้สร้างผลเปรียบเทียบรวมในโฟลเดอร์นี้

## ไฟล์ผลสรุปปัจจุบัน

ขณะนี้มีผลสรุปของการทดลอง AI ได้แก่

- `ai_case_results.csv`
- `ai_summary.json`
- `ai_summary.md`

ไฟล์เหล่านี้เป็นผลสรุปเฉพาะ ChatGPT และ GitHub Copilot ยังไม่ใช่ผลเปรียบเทียบครบทั้ง 4 วิธี

เมื่อผลของ NSGA-II และ Symbolic Execution พร้อม จะสร้างผลสรุปรวมของทั้ง 4 วิธีเพิ่มเติม
