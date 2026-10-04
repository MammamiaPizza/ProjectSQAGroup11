# Result Data

โฟลเดอร์นี้เก็บข้อมูลที่ผ่านการรวบรวมหรือจัดรูปแบบแล้ว เพื่อใช้วิเคราะห์และสร้างผลสรุปของแต่ละวิธี

ข้อมูลดิบและหลักฐานการทดลองยังคงอยู่ในโฟลเดอร์หลักของแต่ละวิธี

## Current data

### ChatGPT and GitHub Copilot

- [AI Case Results](ai/case_results.csv) — ผลราย case ของ ChatGPT และ GitHub Copilot
- [AI Summary JSON](ai/summary.json) — ค่าสรุปจากผล AI สำหรับนำไปประมวลผลต่อ

### NSGA-II

ข้อมูลราย case ปัจจุบันยังอยู่ที่:

[NSGA-II Raw Result Data Round 1](/Report/data/nsga2/summary_nsga2Round1.csv)	- ข้อมูลรวมจาก log ในรอบที่ 1
[NSGA-II Raw Result Data Round 2](/Report/data/nsga2/summary_nsga2Round2.csv)	- ข้อมูลรวมจาก log ในรอบที่ 2
[NSGA-II Analysis The Result of Raw Data](/Report/data/nsga2/nsga2_Data_Analysis.json)	-การวิเคราะห์ข้อมูลดิบ แปลงเป็นข้อมูล Json

เมื่อกำหนดผล NSGA-II ที่ใช้เป็น final source-of-truth แล้ว สามารถนำข้อมูลที่ต้องใช้สำหรับการเปรียบเทียบมาเพิ่มในโฟลเดอร์นี้ได้

### Symbolic Execution

ข้อมูลปัจจุบันยังอยู่ใน:

[Symbolic Execution Results](../../Algorithm2_SymbolicExecution/)

เมื่อผลพร้อมสำหรับการเปรียบเทียบ สามารถเพิ่มข้อมูลที่จัดรูปแบบแล้วในโฟลเดอร์นี้ได้

## Naming convention

แนะนำให้ใช้ชื่อไฟล์ดังนี้เมื่อข้อมูลพร้อม:

- `nsga2_case_results.csv`
- `symbolic_case_results.csv`
- `final_comparison.csv`

ไม่ควรสร้างไฟล์เปล่าก่อนมีข้อมูลจริง
