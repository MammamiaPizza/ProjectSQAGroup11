# Code — คำอธิบายไฟล์แต่ละตัว

## ไฟล์หลัก (ใช้จริงในการทดลอง)

| ไฟล์ | หน้าที่ | วิธีใช้ |
|---|---|---|
| `project_runner.sh` | ตัวจัดคิวหลัก: รับ project ใดก็ได้ + ช่วง bug, ข้าม deprecated ids, แจกงานขนานให้ workers, รวม CSV | `bash project_runner.sh <Project> <start> <end> Round1 3` |
| `project_worker.sh` | ทำ 1 bug จบวงจร: checkout buggy+fixed -> Symflower generate -> รัน buggy/fixed -> coverage -> CSV (ถูก project_runner.sh เรียกอัตโนมัติ) | เรียกผ่าน project_runner.sh |
| `run_demo.sh` | Demo สด 6 ขั้นบน Lang-1 (~5 นาที) สำหรับนำเสนอ | `bash run_demo.sh` (ล้าง: `run_demo.sh clean`) |
| `DEMO_Symflower.md` | คู่มือผู้นำเสนอ: สคริปต์พูดรายขั้น + backup outputs + Q&A | อ่านประกอบการ demo |
| `symbolic_lang1_hex.py` | อัลกอริทึม bounded_symbolic_hex ที่กลุ่มพัฒนาเอง (จำกัด path ฝั่ง hex ของ Lang-1) — ตรวจพบ LANG-747 ด้วย 6 failures ตาม Experiment/summary.csv | `python3 symbolic_lang1_hex.py --help` |

## archive/ (ไฟล์รุ่นเก่า — เก็บไว้อ้างอิงวิวัฒนาการ ไม่ใช้แล้ว)

| ไฟล์ | หมายเหตุ |
|---|---|
| `parallel_runner.sh` | รุ่นขนานจำเพาะ Lang (ถูกแทนด้วย project_runner/worker ที่รับทุก project) |
| `run_symflower_lang.sh` | รุ่นแรกสุด รันเดี่ยวต่อเนื่องจำเพาะ Lang |
