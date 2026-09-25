# ผลการทดลอง NSGA-II: Lang-1, Round 2, run-01

## การตั้งค่า

รอบนี้ขยายตัวเลือกเป็น 32 test cases จากเมธอดต่าง ๆ ของ `NumberUtils`
ตั้งค่า population = 8, generations = 5 และ seed = 11

NSGA-II วัด fitness จาก line coverage และ condition coverage จริงด้วย
Defects4J บน `Lang-1f` พร้อมพิจารณาจำนวน test cases ชุดที่เลือกแล้ว
จึงนำไปทดสอบกับ `Lang-1b` เพื่อดูว่าตรวจพบบั๊ก LANG-747 หรือไม่

## ผลลัพธ์

| เวอร์ชัน | Line coverage | Condition coverage | ผลการทดสอบ |
|---|---:|---:|---|
| Lang-1f (fixed) | 124/380 (32.6%) | 71/350 (20.3%) | ผ่านทั้ง 12 เคส |
| Lang-1b (buggy) | 118/375 (31.5%) | 65/338 (19.2%) | ล้มเหลว 1 เคส |

ชุดที่เลือกมี 12 test cases โดยเคส `case_02_hex_hash` ล้มเหลวบน
`Lang-1b` แต่ผ่านบน `Lang-1f` จึงตรวจพบ LANG-747

## ขอบเขตของผล

Round 1 ใช้ตัวเลือกเฉพาะเลขฐานสิบหกและ budget ที่เล็กกว่า Round 2
จึงใช้สองรอบนี้เปรียบเทียบผลที่เกิดขึ้นได้ แต่ไม่ควรนำมาเฉลี่ยรวมกัน
หรือสรุปว่า coverage ที่เพิ่มขึ้นเกิดจากการเปลี่ยนปัจจัยใดปัจจัยหนึ่ง

การทดลองนี้ประเมิน Lang-1 เพียงบั๊กรายการเดียว ผลนี้จึงยังไม่ใช่
fault detection rate ของ Defects4J ทั้งชุดข้อมูล

## ไฟล์ประกอบ

- `configuration.json`: การตั้งค่าและชุด test cases ที่เลือก
- `evaluations.csv` และ `evaluations/`: ผลและ log ของแต่ละชุดที่ประเมิน
- `NumberUtilsNSGA2Run02Test.java`: JUnit test suite ที่เลือก
- `Lang-1-nsga2-run02.1.tar.bz2`: ชุดทดสอบสำหรับ Defects4J
- `final_*_coverage_summary.csv`: ตัวเลข coverage ของชุดสุดท้าย
- `final_*_failing_tests.txt`: รายละเอียด test ที่ล้มเหลว
- `final_*_coverage.log` และ `buggy_detection.log`: log การรัน
