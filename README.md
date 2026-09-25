# SQA Test Generation Benchmark

โปรเจกต์วิชา CP353201 Software Quality Assurance: เปรียบเทียบการสร้าง JUnit tests สำหรับ Defects4J ด้วย NSGA-II, Symbolic Execution, ChatGPT และ GitHub Copilot ตามรายงานรอบแรกของกลุ่ม

## โครงสร้าง

```text
SQA-Test-Generation-Benchmark/
├── Algorithm1_NSGAII/
│   ├── Code/                 โค้ดตัวสร้าง test
│   ├── Configuration/        seed, budget, objective และค่าที่ใช้รัน
│   ├── Result_Round1/        ผลทดลองซ้ำรอบที่ 1
│   ├── Result_Round2/        ผลทดลองซ้ำรอบที่ 2
│   └── Test/                 JUnit tests ที่สร้างขึ้น
├── Algorithm2_SymbolicExecution/
│   ├── Code/
│   ├── Configuration/
│   ├── Result_Round1/
│   ├── Result_Round2/
│   └── Test/
├── AI1_ChatGPT/
│   ├── Prompt/               ข้อความที่ส่งจริงและข้อมูลบริบท
│   ├── Result/               คำตอบดิบและผลการรัน
│   └── TestCode/             JUnit tests ที่ได้
├── AI2_GitHubCopilot/
│   ├── Prompt/
│   ├── Result/
│   └── TestCode/
├── Experiment/                รายการกรณีศึกษาและวิธีวัดผล
├── Report/                    รายงานและภาพประกอบ
└── Presentation/              สไลด์และไฟล์ demo
```

`Result_Round1` และ `Result_Round2` หมายถึง **รอบที่ทดลองซ้ำของอัลกอริทึม** ไม่ใช่รอบส่งงานของวิชา ยังไม่มีผลทดลองจริงในโฟลเดอร์เหล่านี้

## เริ่มงานรอบถัดไป

1. ติดตั้ง Defects4J และตรวจ Java version ให้ตรงกับคู่มือของเวอร์ชันที่ใช้
2. ทดลอง checkout, compile และ test กรณีนำร่อง `Lang-1b`/`Lang-1f`
3. ตรวจ target class และ JUnit/build context จริง แล้วเพิ่มรายการที่ใช้ใน `Experiment/cases.csv`; กรอก `Experiment/context-template.md` ก่อนส่ง Prompt
4. ใช้ Prompt `01`–`04` ใน `AI1_ChatGPT/Prompt/` และ `AI2_GitHubCopilot/Prompt/` ตามลำดับเดียวกัน (รายละเอียดอยู่ใน README ของแต่ละโฟลเดอร์) และพัฒนาตัวสร้าง test ทั้งสองวิธี
5. เก็บ test ที่สร้างจริง, prompt/output ดิบ, configuration, log, coverage และผล buggy/fixed ทุก run
6. สรุปผลจากการวัดจริงใน `Experiment/summary.csv` แล้วจัดทำรายงานกับ demo

สำหรับกรณีนำร่อง Lang-1 หลัง checkout `Lang-1b` แล้ว ให้รัน `bash Experiment/prepare_lang1_context.sh "$HOME/sqa-workspaces/Lang-1b"` จากโฟลเดอร์โปรเจกต์บน WSL คำสั่งนี้บันทึก source และ metadata จาก buggy checkout แล้วกรอก Prompt 01 และ 02 **เนื้อหาเหมือนกัน** ในโฟลเดอร์ของ AI ทั้งสองตัว เปิดไฟล์ `Experiment/contexts/Lang-1/run-01/` เพื่อตรวจ context และที่มาของ bug report ก่อนส่ง Prompt; ห้ามส่ง fixed source หรือ test ที่เขียนเองเป็น input

วิธีนับผลและข้อควรระวังอยู่ใน [แผนการทดลอง](Experiment/protocol.md) การใช้ Symflower อย่างเดียวต้องระวัง เพราะโจทย์ให้นำอัลกอริทึมที่เลือกมาพัฒนาด้วย

## สมาชิกกลุ่มตามรายงานรอบแรก

| รหัสนักศึกษา | ชื่อ-นามสกุล |
| --- | --- |
| 673308279-7 | นายปองภพ ศรีรักษ์ |
| 673380271-3 | นายธนภูมิ แทนทุมมา |
| 673380510-1 | นางสาวจิรัชญา เป้าจันทึก |

สถานะ: ทดลองนำร่อง Defects4J Lang-1 แล้วทั้ง AI สองตัว และ NSGA-II ที่วัด fitness จาก coverage จริง; ตัวสร้าง symbolic แบบจำกัดเส้นทาง hex ตรวจพบ LANG-747 โดยเก็บข้อจำกัดของ Symflower แยกใน diagnostics งานยังต้องขยายกรณีทดลองและสรุปผลตามขอบเขตที่กำหนด
