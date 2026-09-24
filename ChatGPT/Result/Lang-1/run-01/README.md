# ChatGPT — Lang-1 — run-01 (ผลดิบ)

สร้าง JUnit tests 56 ตัว และ compile ผ่านทั้งหมด
- Lang-1b: ล้มเหลว 2 ตัว
- Lang-1f: ล้มเหลว 1 ตัว
- ตรวจพบ bug: 2 tests ล้มเหลวเฉพาะบน 1b และผ่านบน 1f
- ข้อจำกัด: testCreateNumberNegativeHexWithinIntegerRange ล้มเหลวบน 1f เพราะ assertion คาดชนิด Integer แต่ได้ Long; ชุดดิบจึงยังไม่ผ่าน fixed ทั้งหมด
- Coverage 1b: lines 343/375 (91.5%), conditions 252/338 (74.6%)
- Coverage 1f: lines 347/380 (91.3%), conditions 259/350 (74.0%)

ไฟล์คำตอบดิบ Java test และ log อยู่ในโฟลเดอร์ ChatGPT ตามชื่อไฟล์
ยังไม่มีการแก้ assertion หรือทำ Prompt 3/4 ในผลรอบนี้
