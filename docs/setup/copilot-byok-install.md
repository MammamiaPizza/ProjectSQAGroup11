# การตั้งค่า Copilot BYOK

ไฟล์นี้เป็นโน้ตการตั้งค่า GitHub Copilot CLI สำหรับการทดลอง

## การตั้งค่าที่ใช้

- GitHub Copilot CLI
- BYOK ผ่าน KKU IntelSphere
- โมเดล `deepseek-v4-pro`
- ปิด tools และ custom instructions
- ใช้ temporary directory แยกตาม worker
- ใช้ `ai2_runner.py` แยกจากฝั่ง ChatGPT

## หมายเหตุ

- รอบทดลองสุดท้ายใช้ run id `copilot-final-v2`
- API key กรอกตอนรันและไม่ได้เก็บไว้ใน repository
- รายละเอียดการตั้งค่ารอบสุดท้ายดูได้ที่ `copilot-v2.md` และ `copilot-full-run-v3.md`

## ตรวจสอบก่อนรัน

~~~bash
copilot --version
python3 -m py_compile Experiment/automation/ai2_runner.py
~~~

ไฟล์นี้เก็บไว้เป็นบันทึกการตั้งค่าที่ใช้ในการทดลอง
