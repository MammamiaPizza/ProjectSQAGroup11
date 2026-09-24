# วิธีทดลองเบื้องต้น

1. กำหนดรายการ project, bug ID และ target class จาก Defects4J ที่ติดตั้งจริง เริ่มจาก `Lang-1` เพื่อทดสอบกระบวนการ แล้วขยายขอบเขตตามโจทย์อาจารย์
2. สำหรับ bug เดียวกัน ใช้ source version, target class และสภาพแวดล้อมเดียวกันทั้ง 4 วิธี บันทึก Java version, Defects4J version, เวลา, seed และ budget
3. สร้าง test จาก buggy version โดยไม่ให้ตัวสร้างเห็น fixed source ระหว่างสร้าง test กำหนด specification และ context ที่ส่งให้ AI ทั้งสองตัวเหมือนกัน
4. เก็บ prompt, output ดิบ, test code, configuration และเวลาที่ใช้ก่อนแก้ไข หากซ่อม test ให้บันทึกรอบและ error
5. Compile และ run JUnit tests ที่สร้างขึ้นจริง วัด statement/branch coverage ด้วยเครื่องมือเดียวกันและ target classes เดียวกัน
6. รัน test เดียวกันกับ buggy และ fixed version นับว่าตรวจพบ bug เมื่อ test ล้มเหลวบน buggy แต่ผ่านบน fixed โดยมี assertion ที่ตรวจพฤติกรรมที่คาดหวัง
7. แยก compile error, timeout และกรณีที่รันไม่ได้จากผล fault detection; คิดอัตราตรวจพบจากจำนวน bug ที่ประเมินได้จริง และรายงานจำนวนที่ประเมินไม่ได้
8. ทำซ้ำตามรอบ/seed ที่กำหนดไว้ก่อนทดลอง แล้วคำนวณค่าเฉลี่ยจากผลเครื่องมือจริงเท่านั้น

`Result_Round1` และ `Result_Round2` ในแต่ละอัลกอริทึมเป็นช่องสำหรับการทดลองซ้ำ ไม่ใช่รายงานรอบส่งงาน การทดลองนำร่องหนึ่ง bug ยังไม่ครบข้อกำหนดที่ให้ทดสอบทุก Java project ในขอบเขตงาน
