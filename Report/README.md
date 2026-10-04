# Experiment Results

โฟลเดอร์นี้เป็นจุดรวมสำหรับอ่านผลการทดลองและเตรียมข้อมูลสำหรับเปรียบเทียบทั้ง 4 วิธี

ผลดิบ หลักฐาน และ test code ของแต่ละวิธีจะยังคงอยู่ในโฟลเดอร์ของวิธีนั้นโดยตรง

## ขอบเขตงานและการรวมผล

สมาชิกแบ่งงานทดลองตามวิธีที่รับผิดชอบ ดังนี้

| ผู้รับผิดชอบ | วิธีทดลอง |
|---|---|
| จิรัชญา | ChatGPT และ GitHub Copilot |
| สมาชิกผู้รับผิดชอบ NSGA-II | NSGA-II |
| สมาชิกผู้รับผิดชอบ Symbolic Execution | Symbolic Execution |

ข้อมูล AI ที่จัดเตรียมสำหรับรายงานอยู่ใน `data/ai/` และสรุปผลอยู่ใน `summaries/ai_summary.md`

ไฟล์ `summaries/nsga2_summary.md` และ `summaries/symbolic_summary.md` เตรียมไว้ให้ผู้รับผิดชอบกรอกผลที่ตรวจสอบแล้ว พร้อมระบุขอบเขตทดลอง configuration และแหล่งหลักฐาน

ไฟล์ `summaries/final_comparison.md` เตรียมไว้สำหรับรวมผลทั้ง 4 วิธี เมื่อได้ข้อมูลที่มีขอบเขตและตัวชี้วัดที่เปรียบเทียบกันได้

ข้อมูลเครื่องและขั้นตอนของฝั่ง AI ดูที่ [ขั้นตอนการทดลอง AI](../Experiment/protocol/ai_final_protocol.md) ส่วนข้อมูลเครื่องและ configuration ของแต่ละอัลกอริทึมให้ผู้รับผิดชอบบันทึกจากการทดลองของตนเอง

## ผลการทดลองแต่ละวิธี

| วิธี | ผลและหลักฐาน | ผลสรุปสำหรับอ่าน |
|---|---|---|
| NSGA-II | [NSGA-II Results](../Algorithm1_NSGAII/) | [NSGA-II Summary](summaries/nsga2_summary.md) |
| Symbolic Execution | [Symbolic Results](../Algorithm2_SymbolicExecution/) | [Symbolic Summary](summaries/symbolic_summary.md) |
| ChatGPT | [ChatGPT Results](../AI1_ChatGPT/Result/) | [AI Summary](summaries/ai_summary.md) |
| GitHub Copilot | [Copilot Results](../AI2_GitHubCopilot/Result/) | [AI Summary](summaries/ai_summary.md) |

## ข้อมูลสำหรับวิเคราะห์

ไฟล์ใน [data](data/) เป็นข้อมูลที่จัดไว้สำหรับนำไปคำนวณ วิเคราะห์ หรือสร้างตารางเปรียบเทียบต่อ ไม่ใช่เอกสารสรุปสำหรับอ่านโดยตรง

ปัจจุบันมีข้อมูล AI:

- [AI Case Results](data/ai/case_results.csv) — ผลราย case ของ ChatGPT และ GitHub Copilot
- [AI Summary JSON](data/ai/summary.json) — ค่าสรุป AI ในรูปแบบ machine-readable

ข้อมูล NSGA-II ราย case ปัจจุบันยังอยู่ที่:

- [NSGA-II Round 1 Data](../Algorithm1_NSGAII/Result_Round1/summary_nsga2.csv)

ข้อมูลของแต่ละวิธีจะยังอ้างอิงจาก source ของวิธีนั้นจนกว่าจะกำหนดชุดผลสุดท้ายที่ใช้ในการเปรียบเทียบ

## ผลสรุปสำหรับอ่าน

ไฟล์ใน [summaries](summaries/) เป็นผลที่สรุปจากข้อมูลราย case เพื่อให้สามารถอ่านและนำไปใช้ในรายงานได้

- [AI Experiment Summary](summaries/ai_summary.md)
- [NSGA-II Summary](summaries/nsga2_summary.md)
- [Symbolic Execution Summary](summaries/symbolic_summary.md)

NSGA-II และ Symbolic Execution มี template เตรียมไว้แล้ว สามารถกรอกผล final ของแต่ละวิธีได้โดยไม่ต้องเปลี่ยนโครงสร้าง repository เพิ่ม

## เปรียบเทียบทั้ง 4 วิธี

เมื่อผลของทุกวิธีใช้ขอบเขตและตัวชี้วัดที่เปรียบเทียบกันได้ จะรวมผลไว้ที่:

[Final Method Comparison](summaries/final_comparison.md)

## Benchmark Status

สถานะว่าผลของแต่ละวิธีพร้อมถึงขั้นไหนอยู่ที่:

[Benchmark Status](benchmark_status.md)
