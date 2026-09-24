# Prompt สำหรับ GitHub Copilot

ไฟล์ `01`–`04` เหมือนกับไฟล์ใน `ChatGPT/Prompt/` ทุกประการตามหัวข้อ 5.3–5.6 ของรายงานรอบแรก

ใช้ context snapshot ชุดเดียวกับ ChatGPT จาก `Experiment/context-template.md` แทน `[PLACEHOLDER]` ด้วยข้อมูลที่ตรวจสอบได้ และจำกัดไฟล์ที่ Copilot เข้าถึงให้ตรงกับชุด context ที่กำหนด บันทึกไฟล์ที่เปิดใน IDE และไฟล์แนบที่ส่งจริง

ลำดับการใช้คือ `01` วิเคราะห์ → `02` สร้าง test → `03` เมื่อ compile/run ผิดพลาด → `04` เมื่อมี coverage report จริง เก็บ Prompt ที่ส่งจริงกับคำตอบดิบใน `GitHubCopilot/Result/<project>-<bug>/run-<id>/` และ Java ใน `GitHubCopilot/TestCode/` บันทึกชื่อรุ่นโมเดลที่หน้าจอแสดง วันเวลา ช่องทาง และเวลาจริง
