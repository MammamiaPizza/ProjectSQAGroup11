# ขั้นตอนและสภาพแวดล้อม Symflower

## หลักฐาน configuration

อ้างอิง source ของ [project_worker.sh](../Algorithm2_SymbolicExecution/Code/project_worker.sh), [project_runner.sh](../Algorithm2_SymbolicExecution/Code/project_runner.sh), CSV และ logs ที่จัดเก็บ ไม่ใช้คำว่า build ล่าสุดแทนหมายเลขเวอร์ชันย้อนหลังซึ่งยังไม่ครบทุกการรัน

| รายการ | ค่าที่พบใน worker ปัจจุบัน |
|---|---|
| Java | `JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64` |
| Defects4J | ใช้ `framework/bin/defects4j` จาก installation ของผู้รัน |
| Memory limit | 2560 MB |
| Test framework | JUnit4 สำหรับโครงการส่วนใหญ่ และ JUnit5 สำหรับ Mockito |
| Target | Package ของ modified class หรือคลาสเดียวเมื่อ package มี Java มากกว่า 10 ไฟล์ |
| Generation source | Buggy checkout |
| เวลา | `date +%s` รอบคำสั่ง `symflower unit-tests` |

เอกสารและ runner รุ่นเก่าเคยระบุ memory limit 4096 MB จึงไม่อ้างว่าใช้ 2560 MB หรือ test framework เดียวกันทุกการรันย้อนหลัง Source ปัจจุบันไม่ยืนยันหมายเลข Symflower build ของผลทั้งหมด

## Build context

Worker ซ่อน `pom.xml` และ Gradle build files ในโครงการที่ configuration กำหนด เพื่อใช้การสร้างแบบ plain Java อาจทำให้ dependency และ API บางส่วนวิเคราะห์ไม่ครบ ความเข้ากันได้ต้องตรวจจาก logs ไม่ถือว่า plain mode รองรับทุกโครงการ

## การทดสอบและ fault

Worker หลักใช้ `defects4j test` บน buggy และ fixed แล้วอ่านชื่อ tests ที่ล้มเหลว ตัด developer trigger names ซึ่ง parser รองรับทั้ง `,` และ `;` ผล full-suite อาจรวม developer tests จึงไม่ใช้สถานะ `ok` ยืนยัน generated-only fixed validation

ข้อมูล Lang บางรายการระบุ per-method evaluation และบางรายการระบุการแก้ false positive ผลหลักรวบรวมจาก CSV รายโครงการ โดย Lang ใช้ `lang_results_full.csv` และตัด `deprecated_bug` รายละเอียดอยู่ใน [สรุป Symflower](summaries/symbolic_summary.md)

## Coverage

Worker เรียก `defects4j coverage` บน buggy และอ่าน `Line coverage` กับ `Condition coverage` แต่บันทึกในคอลัมน์เดิม `stmt_cov` และ `branch_cov` ค่าเหล่านี้รวม developer tests ไม่ใช่ generated-only coverage และไม่ถือว่า Line เท่ากับ Statement หรือ Condition เท่ากับ Branch โดยอัตโนมัติ

ความล้มเหลวของ instrumentation เป็นข้อจำกัดของการประเมิน ต้องแยกจากผล tests ไม่สรุปว่า Cobertura รองรับ bytecode ได้ถึง Java 6 เท่านั้น

## การทดลองเพิ่มเติม

[Round 2 sample](../Algorithm2_SymbolicExecution/Result_Round2/lang_results_R2_sample.csv) มี Lang 8 กรณี โดยใช้ timeout 60 วินาทีต่อ method ไม่พบ fault เพิ่มเติม ไม่ใช่ผลครบทั้ง 854 กรณี และไม่ใช้ยืนยันว่าการรันซ้ำเหมือนเดิมทุกช่อง

## ตำแหน่งการรัน

Runner ที่จัดเก็บอ้างอิง `$HOME/sqa-project/Symflower` และ installation ของผู้รันเดิม จึงเป็น source ของกระบวนการทดลองที่บันทึก ไม่ใช่คำสั่งพร้อมรันจากทุก checkout โดยไม่เตรียม paths และ dependencies

## ข้อมูลและหลักฐาน

- [CSV ต้นทาง](../Algorithm2_SymbolicExecution/Result_Round1/all_projects/)
- [Logs](../Algorithm2_SymbolicExecution/Result_Round1/logs_selected/)
- [Test source](../Algorithm2_SymbolicExecution/Test/)
- [ข้อมูลกลางและ source hashes](data/symbolic/summary.json)
