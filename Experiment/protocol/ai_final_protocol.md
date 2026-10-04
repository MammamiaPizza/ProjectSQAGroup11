# ขั้นตอนการทดลอง AI

## ชุดผลที่ใช้สรุป

- ChatGPT: `run-final-opt`
- GitHub Copilot: `run-copilot-final-v2`

ผลจาก `run-final` เป็นหลักฐานของขั้นตอนก่อนปรับปรุง และไม่รวมในการสร้างสรุปจากสองชุดผลข้างต้น

## การเตรียมข้อมูล

ใช้ source และ context จาก buggy version เพื่อสร้าง tests โดยไม่ส่ง fixed source ให้ AI ส่วน fixed version ใช้ตรวจสอบชุดทดสอบที่สร้างได้

Prompt และ context ที่ส่งจริงในแต่ละ case เป็นหลักฐานสำหรับตรวจสอบข้อมูลที่ AI ได้รับ

## ขั้นตอน P01–P04

| ขั้นตอน | การทำงาน |
|---|---|
| P01 | วิเคราะห์ข้อมูล bug และ API signatures เพื่อเตรียมแผนทดสอบ โดยขอคำตอบสั้นไม่เกิน 10 บรรทัด |
| P02 | สร้าง Java test class จาก source/context ที่คัดเลือกแบบ deterministic โดยจำกัดส่วน source/context ไม่เกิน 12,000 ตัวอักษร และขอไม่เกิน 12 test methods |
| P03 | เมื่อชุดทดสอบเดิมไม่ผ่าน validation ให้ AI แก้ไขหนึ่งครั้ง โดยส่ง test code, ข้อมูล bug, API signatures และข้อความ error ที่ย่อแล้ว รับคำตอบเป็น Java class ฉบับเต็ม จากนั้นตรวจด้วย fixed version |
| P04 | วัด coverage ของชุดที่ผ่าน fixed validation แล้ว ส่ง coverage, สรุป tests และ source snippets เพื่อขอเพิ่มไม่เกิน 4 tests หรือคืน `NO_CHANGE` |

P03 ใน runner ปัจจุบันใช้ `extract_java()` อ่าน Java class ฉบับเต็ม ฟังก์ชันรองรับ unified diff ที่ยังอยู่ในไฟล์เป็นโค้ดจากรูปแบบเดิม ไม่ใช่ตัวอ่านคำตอบที่เรียกใน `stage03()` ปัจจุบัน

## การจัดการผล P04

1. หากตอบ `NO_CHANGE` ใช้ชุดทดสอบเดิมที่ผ่าน fixed validation แล้ว
2. หากคืนส่วนเพิ่มที่อ่านและรวมกับ class ได้ ให้นำชุดที่รวมแล้วไปตรวจด้วย fixed version
3. หากอ่านส่วนเพิ่มไม่ได้ หรือชุดที่รวมแล้วไม่ผ่าน fixed validation ให้ปฏิเสธส่วนเพิ่มและใช้ชุดเดิม
4. ไม่เรียก AI เพิ่มเพื่อแก้ส่วนเพิ่มที่ถูกปฏิเสธ
5. บันทึกผลการใช้หรือปฏิเสธส่วนเพิ่มใน `04_extension_status.json`

เมื่อใช้ชุดเดิม runner บันทึก `reused_prior_validation: true` และ `elapsed_seconds: 0` ค่า 0 นี้หมายถึงไม่มีการรัน validation ใหม่ในขั้นตอนดังกล่าว ไม่ใช่เวลาทดสอบที่วัดใหม่ จึงต้องแยกจากเวลาที่ได้จากการรันจริงเมื่อวิเคราะห์ประสิทธิภาพ

## หลักฐานที่จัดเก็บ

เก็บ prompt ที่ส่งจริง, คำตอบดิบ, stage metadata, Java tests, หลักฐาน validation, coverage summary, case status และผล final evaluation

ตามแนวทางลดข้อมูลซ้ำ ไม่เก็บ provider JSON ซ้ำ, prompt สำเนาซ้ำ, suite archives ถาวร, full successful validation logs และ full coverage XML

เป้าหมาย token เฉลี่ยคือไม่เกิน 13,000 tokens ต่อ bug เป็นเป้าหมายสำหรับติดตามการใช้ ไม่ใช่ hard cap

## การเรียกซ้ำ

ปัญหาระบบหรือการเชื่อมต่อสามารถ retry ได้ ส่วนผลที่ AI สร้างไม่สำเร็จ เช่น `INVALID_AFTER_REPAIR` หรือ `OUTPUT_INCOMPLETE` ไม่เรียกใหม่เพียงเพื่อให้ได้ผลที่ดีขึ้น

## ประวัติการปรับขั้นตอน

- รูปแบบ P03 เดิมใช้ unified diff ต่อมาปรับเป็น Java class ฉบับเต็ม
- P04 ใน runner ปัจจุบันปฏิเสธส่วนเพิ่มที่ผิดและเก็บชุดเดิมที่ผ่าน validation
- ช่วงเริ่ม optimized run ใช้ `--skip-terminal-from-run final` เพื่อให้ความสำคัญกับกรณีที่ยังไม่ได้วัดและกรณีที่ติดปัญหาระบบก่อน
- การอธิบายขั้นตอนของผลย้อนหลังต้องอ้างอิง prompt และ stage artifacts ของแต่ละ case เพราะชื่อ run เพียงอย่างเดียวไม่ได้ระบุ revision ของ runner

## การสรุปผล

- แยกจำนวน attempted cases ออกจาก successfully evaluated cases
- แสดงจำนวนกรณีที่มีค่าจริงและ denominator ของแต่ละตัวชี้วัด
- ไม่แทน coverage หรือ fault-detection result ที่ไม่มีค่าด้วย 0
- แยก line coverage และ condition coverage ตามชื่อค่าที่เครื่องมือรายงาน
- ตรวจผลเปรียบเทียบจาก case และขอบเขตการวัดที่เทียบกันได้

ตำแหน่งผลสรุป:

- [ผลราย case](../../Report/data/ai/case_results.csv)
- [ข้อมูลสรุป JSON](../../Report/data/ai/summary.json)
- [สรุปผล AI](../../Report/summaries/ai_summary.md)

## สภาพแวดล้อมที่ตรวจสอบ

ข้อมูลเครื่องที่ใช้ทำงานฝั่ง AI ตรวจสอบเมื่อวันที่ 4 ตุลาคม 2569 ตามเวลาไทย

### เครื่องและระบบปฏิบัติการ

| รายการ | ข้อมูล |
|---|---|
| ระบบปฏิบัติการ | Ubuntu 24.04.1 LTS บน WSL2 |
| Kernel | 6.18.40.1-microsoft-standard-WSL2 |
| สถาปัตยกรรม | x86_64 |
| CPU | Intel Core i5-8265U @ 1.60 GHz |
| Logical CPUs ที่ WSL มองเห็น | 4 |
| RAM ที่ WSL มองเห็น | 4.8 GiB |
| Swap | 8.0 GiB |

RAM และ Swap ในตารางเป็นทรัพยากรของ WSL ไม่ใช่ค่าการใช้หน่วยความจำระหว่างทดลอง

### เครื่องมือ

| เครื่องมือ | เวอร์ชัน |
|---|---|
| Java runtime | OpenJDK 11.0.32.1 |
| Java compiler | javac 11.0.32.1 |
| Python | 3.12.3 |
| Git | 2.43.0 |
| Perl | 5.38.2 |
| Subversion | 1.14.3 |
| GitHub Copilot CLI | 1.0.89 |
| Defects4J Git description | v3.0.1-7-g8c16da82 |
| Defects4J commit | 8c16da8230843cdc918eaf4ddb449637f02b83c6 |

### Dependencies และเครื่องมือทดสอบ

- ตรวจ imports ของสคริปต์ที่มีอยู่ด้วย Python environment ปัจจุบันแล้ว ไม่พบ external package ที่จับคู่กับ package ที่ติดตั้งได้
- พบ `cobertura-2.0.3.jar` ใน Defects4J
- พบ JUnit หลายเวอร์ชันใน Defects4J จึงต้องเลือกตาม dependency ของแต่ละ project ไม่กำหนดเวอร์ชันเดียวให้ทุก case
- รายการ JAR ที่ติดตั้งเป็นข้อมูลสภาพแวดล้อม ส่วนเครื่องมือที่ใช้วัดผลแต่ละ case ให้อ้างอิงคำสั่งและหลักฐานการประเมินของ case นั้น

### ขอบเขตของข้อมูลเครื่อง

ข้อมูลในหัวข้อนี้เป็นสภาพแวดล้อมเครื่องฝั่ง AI ไม่ใช้แทนข้อมูลเครื่องของเพื่อนที่รัน NSGA-II หรือ Symbolic Execution

สคริปต์ EvoSuite ระบุ Java 8 ไว้ที่ `/usr/lib/jvm/java-8-openjdk-amd64/bin/java` แต่ไม่พบในเครื่องนี้ ข้อมูล Java และ configuration ของ NSGA-II จึงต้องบันทึกจากเครื่องที่ใช้รันวิธีนั้นจริง
