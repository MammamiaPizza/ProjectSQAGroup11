# Benchmark Status

ไฟล์นี้สรุปสถานะของผลการทดลองที่มีอยู่ใน repository และใช้เป็นจุดอ้างอิงไปยังผลของแต่ละวิธี

## Current Results

| Method | Current result scope | Main result source | Comparison status |
|---|---|---|---|
| NSGA-II | Result Round 1 และ Round 2 มีผลการทดลองแล้ว | [NSGA-II Results](../Algorithm1_NSGAII/) | รอสรุปให้อยู่ในรูปแบบเดียวกับวิธีอื่น |
| Symbolic Execution | ปัจจุบันมีผล Round 1 สำหรับ Lang-1 และ Cli-1 | [Symbolic Execution Results](../Algorithm2_SymbolicExecution/) | ผลยังไม่ครอบคลุม benchmark เท่ากับวิธีอื่น |
| ChatGPT | 854 cases attempted, 561 cases successfully evaluated | [ChatGPT Results](../AI1_ChatGPT/Result/) | มีผลสรุป AI แล้ว |
| GitHub Copilot | 854 cases attempted, 229 cases successfully evaluated | [GitHub Copilot Results](../AI2_GitHubCopilot/Result/) | มีผลสรุป AI แล้ว |

## Available Summaries

- [AI Experiment Summary](summaries/ai_summary.md)
- [AI Case Results](data/ai_case_results.csv)
- [NSGA-II Round 1 Summary](../Algorithm1_NSGAII/Result_Round1/summary_nsga2.csv)

## Final Comparison

ยังไม่สร้างผลเปรียบเทียบรวมของทั้ง 4 วิธี เนื่องจากผลของแต่ละวิธียังต้องจัดให้อยู่ภายใต้ขอบเขตกรณีทดลองและตัวชี้วัดที่เปรียบเทียบกันได้ก่อน

ผลรายวิธีจะยังคงอ้างอิงจากโฟลเดอร์ของวิธีนั้นโดยตรง และผลเปรียบเทียบรวมจะเพิ่มใน `Report/` เมื่อข้อมูลพร้อม
