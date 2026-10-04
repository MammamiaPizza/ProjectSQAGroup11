# AI Experiment Support

โฟลเดอร์นี้เก็บไฟล์ที่ใช้สนับสนุนการทดลอง ChatGPT และ GitHub Copilot บน Defects4J

ผลการทดลองจริงของแต่ละ AI แยกเก็บไว้ที่

- [ChatGPT](../AI1_ChatGPT/)
- [GitHub Copilot](../AI2_GitHubCopilot/)

ผลสรุปสำหรับอ่านและใช้ในรายงานอยู่ที่  
[Report](../Report/)

## โครงสร้าง

- [automation](automation/) — scripts สำหรับรัน ตรวจสอบ และประมวลผลการทดลอง
- [contexts](contexts/) — context ที่สร้างจาก Defects4J buggy version
- [evaluations](evaluations/) — ผลการประเมิน test suite
- [diagnostics](diagnostics/) — ข้อมูลสำหรับตรวจสอบปัญหาระหว่างการทดลอง
- [protocol](protocol/) — ขั้นตอนและกติกาการทดลอง AI
- [archive](archive/) — ข้อมูล pilot และไฟล์เก่าที่เก็บไว้เพื่ออ้างอิง

รายละเอียดขั้นตอนการทดลองขั้นสุดท้าย:

[Final AI Experiment Protocol](protocol/ai_final_protocol.md)
