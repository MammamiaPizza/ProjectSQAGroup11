# การตั้งค่า Copilot BYOK

ไฟล์นี้เป็นโน้ตการตั้งค่า GitHub Copilot CLI ที่ใช้ในการทดลอง

## การตั้งค่าที่ใช้

- GitHub Copilot CLI `1.0.88`
- BYOK ผ่าน KKU IntelSphere แบบ OpenAI-compatible endpoint
- โมเดล `deepseek-v4-pro`
- ใช้ `Experiment/automation/ai2_runner.py` แยกจากฝั่ง ChatGPT
- ปิด tools
- ปิด built-in MCP
- ปิด custom instructions
- ใช้ temporary directory แยกตาม worker ที่ `/tmp/sqa-copilot-clean/<worker>`
- ใช้ prompt P01-P04 จาก `AI2_GitHubCopilot/Prompt`

## หมายเหตุ

- การตั้งค่านี้เป็นส่วนเสริมของระบบ Optimized v3 เดิม
- ไม่ได้แทนที่ `Experiment/automation/ai_runner.py` ที่ใช้กับ ChatGPT
- รอบทดลองสุดท้ายใช้ run id `copilot-final-v2`
- API key กรอกตอนรันและไม่ได้เก็บไว้ใน repository
- รายละเอียดการตั้งค่ารอบสุดท้ายดูได้ที่ `copilot-v2.md` และ `copilot-full-run-v3.md`

## ตรวจสอบก่อนรัน

~~~bash
copilot --version
python3 -m py_compile Experiment/automation/ai2_runner.py
~~~
