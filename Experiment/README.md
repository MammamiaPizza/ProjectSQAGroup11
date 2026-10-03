# Experiment

โฟลเดอร์นี้เก็บไฟล์สนับสนุนสำหรับการทดลอง ChatGPT และ GitHub Copilot บน Defects4J

ผลของแต่ละวิธีในโครงการแยกเก็บไว้ที่โฟลเดอร์หลักของวิธีนั้น

- [NSGA-II](../Algorithm1_NSGAII/)
- [Symbolic Execution](../Algorithm2_SymbolicExecution/)
- [ChatGPT](../AI1_ChatGPT/)
- [GitHub Copilot](../AI2_GitHubCopilot/)

ผลเปรียบเทียบระดับโครงการและเอกสารสำหรับรายงานอยู่ใน [Report](../Report/)

## โครงสร้าง

- [automation](automation/) — scripts ที่ใช้รัน ตรวจสอบ และสรุปผล AI
- [contexts](contexts/) — context ที่เตรียมจาก Defects4J buggy version
- [evaluations](evaluations/) — ผลการประเมิน test suite
- [diagnostics](diagnostics/) — ข้อมูลสำหรับตรวจสอบปัญหาระหว่างการทดลอง
- [protocol](protocol/) — ขั้นตอนการทดลอง AI
- [ai-results](ai-results/) — ผลสรุปรวมของ ChatGPT และ GitHub Copilot
- [archive](archive/) — pilot และข้อมูลเก่าที่เก็บไว้เพื่ออ้างอิง

## ผล AI ขั้นสุดท้าย

- ChatGPT: `run-final-opt`
- GitHub Copilot: `run-copilot-final-v2`

ผลสรุป:

- [AI Summary](ai-results/ai_summary.md)
- [AI Case Results](ai-results/ai_case_results.csv)
- [AI Summary JSON](ai-results/ai_summary.json)

รายละเอียดขั้นตอนการทดลองอยู่ที่  
[Final AI Experiment Protocol](protocol/ai_final_protocol.md)
