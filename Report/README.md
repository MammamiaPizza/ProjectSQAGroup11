# ผลการทดลองและการเปรียบเทียบ

โฟลเดอร์นี้รวบรวมข้อมูลและผลสรุปของการทดลองสร้างชุดทดสอบด้วย NSGA-II, Symbolic Execution, ChatGPT และ GitHub Copilot

## โครงสร้าง

| โฟลเดอร์ | เนื้อหา |
|---|---|
| [data](data/) | ข้อมูลสำหรับคำนวณและวิเคราะห์ผล |
| [summaries](summaries/) | สรุปผลรายวิธีและผลเปรียบเทียบ |

Source code, test code, prompt, configuration และหลักฐานการรันจัดเก็บในโฟลเดอร์ของแต่ละวิธี

## ผลการทดลองแต่ละวิธี

| วิธี | ผลและหลักฐาน | สรุปผล |
|---|---|---|
| NSGA-II | [NSGA-II](../Algorithm1_NSGAII/) | [สรุป NSGA-II](summaries/nsga2_summary.md) |
| Symbolic Execution | [Symbolic Execution](../Algorithm2_SymbolicExecution/) | [สรุป Symbolic Execution](summaries/symbolic_summary.md) |
| ChatGPT | [ChatGPT](../AI1_ChatGPT/) | [สรุป ChatGPT และ GitHub Copilot](summaries/ai_summary.md) |
| GitHub Copilot | [GitHub Copilot](../AI2_GitHubCopilot/) | [สรุป ChatGPT และ GitHub Copilot](summaries/ai_summary.md) |

สรุปผลแต่ละวิธีระบุขอบเขตการทดลอง การตั้งค่า ผลการทดสอบ ความครอบคลุมของโค้ด การตรวจพบข้อบกพร่อง และข้อจำกัด โดยอ้างอิงหลักฐานการรันจริง

## ข้อมูลสำหรับวิเคราะห์

| ชุดข้อมูล | ตำแหน่ง |
|---|---|
| ChatGPT และ GitHub Copilot รายกรณี | [case_results.csv](data/ai/case_results.csv) |
| ค่าสรุป ChatGPT และ GitHub Copilot | [summary.json](data/ai/summary.json) |
| NSGA-II รอบที่ 1 | [summary_nsga2.csv](../Algorithm1_NSGAII/Result_Round1/summary_nsga2.csv) |
| ผลและหลักฐาน Symbolic Execution | [Result Round 1](../Algorithm2_SymbolicExecution/Result_Round1/) |

รายละเอียดชุดข้อมูลและการจัดเก็บดูที่ [คำอธิบายข้อมูล](data/README.md)

## การเปรียบเทียบ

[ผลเปรียบเทียบทั้ง 4 วิธี](summaries/final_comparison.md) ใช้กรณีทดลองและตัวชี้วัดที่เปรียบเทียบกันได้ พร้อมระบุจำนวนกรณีที่มีผลการวัดและข้อจำกัดของข้อมูล

ความพร้อมของผลแต่ละวิธีดูที่ [สถานะการทดลอง](benchmark_status.md)

## ขั้นตอนและสภาพแวดล้อม

ขั้นตอน เครื่องมือ และ configuration อ้างอิงจากเอกสารและหลักฐานของแต่ละวิธี

- [NSGA-II](../Algorithm1_NSGAII/)
- [Symbolic Execution](../Algorithm2_SymbolicExecution/)
- [ขั้นตอนการทดลอง ChatGPT และ GitHub Copilot](../Experiment/protocol/ai_final_protocol.md)
