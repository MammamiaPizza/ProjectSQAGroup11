# ผลการทดลอง NSGA-II: Lang-1, Round 1, run-01

รอบทดลองแรกใช้ตัวเลือกเลขฐานสิบหก 16 ค่า ตั้งค่า population = 4,
generations = 2 และ seed = 11 โดยวัด fitness จาก Defects4J coverage
บน Lang-1f จริง แล้วใช้ Lang-1b ตรวจว่าชุดที่เลือกจับ LANG-747 ได้หรือไม่

ชุดที่เลือกมี 4 test cases:

| เวอร์ชัน | Line coverage | Condition coverage | ผลการทดสอบ |
|---|---:|---:|---|
| Lang-1f (fixed) | 51/380 (13.4%) | 21/350 (6.0%) | ผ่านทั้งหมด |
| Lang-1b (buggy) | 44/375 (11.7%) | 14/338 (4.1%) | ล้มเหลว 2 เคส |

coverage ของทั้งคลาสค่อนข้างต่ำ เพราะตัวสร้างรอบนี้ทดสอบเฉพาะ
เลขฐานสิบหกใน `createNumber` เก็บผลนี้เป็น pilot ตามที่วัดได้จริง
ส่วน Round 2 ขยายตัวเลือกและ budget เป็นอีก configuration หนึ่ง
