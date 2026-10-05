# ผลการทดลอง Symflower

## ขอบเขตและวิธีทดลอง

ทดลองบน 854 active bugs ใน 17 โครงการของ Defects4J โดยสร้าง tests จาก buggy source และประเมินบน buggy/fixed ข้อมูลหลักมาจาก CSV รายโครงการใน `Result_Round1/all_projects/` สำหรับ Lang ใช้ `lang_results_full.csv` และตัดรายการ `deprecated_bug` ออก ไม่รวม CSV รุ่นเก่าซ้ำกับผลชุดหลัก

ใช้ Symflower CLI และ Defects4J โดยเลือกเป้าหมายจาก modified classes ตัว worker เลือก package และอาจจำกัดเป็นคลาสเดียวเมื่อ package มี Java มากกว่า 10 ไฟล์ ค่า scope ใน CSV จึงไม่ใช่หลักฐานว่าใช้ package ทุกกรณี รายละเอียดอยู่ใน [ขั้นตอนและสภาพแวดล้อม](../symflower_protocol.md)

## ผลตามขั้นตอน

| ผลลัพธ์ | จำนวนกรณี | สัดส่วนต่อ 854 กรณี |
|---|---:|---:|
| Checkout ไม่สำเร็จ | 26 | 3.04% |
| ไม่มีไฟล์ชุดทดสอบหลัง checkout | 672 | 78.69% |
| มีไฟล์ชุดทดสอบและระบุ compile fail | 107 | 12.53% |
| มีไฟล์ชุดทดสอบและไม่ระบุ compile fail | 49 | 5.74% |
| **รวม** | **854** | **100.00%** |

มีไฟล์ชุดทดสอบ 156 กรณี หรือ 18.27% โดย 107 กรณีเป็น compile fail คิดเป็น 68.59% ของกรณีที่มีไฟล์ อีก 49 กรณีไม่ยืนยันว่าชุดทดสอบที่สร้างผ่าน fixed validation ทั้งหมด เพราะผลบางรายการรวม developer tests ส่วน 698 กรณีที่ไม่มีไฟล์รวม checkout fail 26 กรณี ไม่ถือว่าทั้งหมดเป็นความล้มเหลวของการวิเคราะห์ด้วย Symflower

## ผลรายโครงการ

| โครงการ | กรณีทั้งหมด | กรณีที่มีไฟล์ทดสอบ | @Test declarations | กรณีที่รายงาน fault |
|---|---:|---:|---:|---:|
| Chart | 26 | 0 | 0 | 0 |
| Cli | 39 | 5 | 125 | 0 |
| Closure | 174 | 0 | 0 | 0 |
| Codec | 18 | 5 | 64 | 0 |
| Collections | 28 | 4 | 9 | 0 |
| Compress | 47 | 33 | 2,858 | 0 |
| Csv | 16 | 6 | 18 | 0 |
| Gson | 18 | 0 | 0 | 0 |
| JacksonCore | 26 | 14 | 3,896 | 0 |
| JacksonDatabind | 110 | 8 | 42 | 0 |
| JacksonXml | 6 | 0 | 0 | 0 |
| Jsoup | 93 | 33 | 1,788 | 0 |
| JxPath | 22 | 0 | 0 | 0 |
| Lang | 61 | 22 | 1,510 | 0 |
| Math | 106 | 19 | 150 | 0 |
| Mockito | 38 | 0 | 0 | 0 |
| Time | 26 | 7 | 217 | 0 |
| **รวม** | **854** | **156** | **10,677** | **0** |

สร้างไฟล์ชุดทดสอบได้ใน 11 โครงการ รวม 10,677 `@Test` declarations ตัวเลขนี้นับจาก source ไม่ใช่จำนวน executions ชื่อคอลัมน์ต้นทาง `n_generated_lines` เป็นชื่อเดิมที่ runner ใช้นับ `@Test` ไม่ใช่จำนวนบรรทัดโค้ด

## Fault detection และการตรวจผล

อัตราตรวจพบ fault ต่อกรณีทดลองทั้งหมดเท่ากับ 0/854 หรือ 0.00% ส่วน 49 กรณีที่ไม่ระบุ compile fail ไม่ใช่จำนวนกรณีที่ผ่าน generated-only fixed validation จึงไม่ใช้จำนวนนี้แทนจำนวนกรณีที่ประเมินได้อย่างสมบูรณ์

ข้อมูลหลังแก้ไขรายงาน fault เป็น 0 กรณี ผลเดิมของ Lang-19, Lang-20, Lang-22 และ Lang-36 เป็น false positive จาก developer trigger tests ที่ parser ตัดไม่ครบเมื่อชื่อ tests คั่นด้วย `;` หลังแก้ผล CSV ระบุ `trigger_parse_false_positive_corrected`

ตัว worker หลักใช้ full-suite test และตัด trigger names จากผลล้มเหลว ส่วนบางรายการของ Lang มีบันทึกการประเมินราย method จึงไม่อ้างว่าทั้ง 854 กรณีผ่าน generated-only validation แบบเดียวกัน ผลไม่พบ fault จำกัดอยู่ภายใต้กระบวนการนี้ ไม่ยืนยันว่า Symflower หรือ Symbolic Execution ตรวจพบข้อบกพร่องไม่ได้ทุกประเภท

## Coverage และเวลา

Coverage ต้นทางเป็น Line/Condition coverage จาก full-suite บน buggy ซึ่งรวม developer tests คอลัมน์ `stmt_cov` และ `branch_cov` เป็นชื่อเดิม จึงไม่ใช้ค่าดังกล่าวเทียบโดยตรงกับ generated-test coverage บน fixed ของวิธีอื่น ค่าไม่มีข้อมูลไม่แทนด้วยศูนย์

เวลา generation มีข้อมูล 828 กรณี เฉลี่ย 97.69 วินาที และมัธยฐาน 15.00 วินาที จับเวลารอบคำสั่ง Symflower และรวมกรณีที่ไม่มีไฟล์ทดสอบเมื่อมีเวลาบันทึก ไม่ใช่เวลารวม checkout, compile, test และ coverage

## Round 2

ทดลอง timeout 60 วินาทีต่อ method บน Lang-1, Lang-3, Lang-6, Lang-7, Lang-8, Lang-15, Lang-19 และ Lang-22 รวม 8 กรณี ไม่พบ fault เพิ่มเติม จำนวนไฟล์และ declarations ของตัวอย่างไม่เปลี่ยนจากรอบแรก แต่รายละเอียดเวลาและผล test บางช่องต่างกัน จึงไม่กล่าวว่าผลทุกช่องเหมือนเดิมหรือใช้ยืนยัน determinism

การทดลองนี้ไม่ครอบคลุมทั้ง 854 กรณี และไม่ยืนยันว่าไม่มี method ใช้เวลาเกิน budget

## ข้อจำกัดและกรณีศึกษา

ปัญหาที่รายงานเกี่ยวข้องกับ build context, dependencies, API/test framework และ internal errors เช่น `cannot set a final field` การไม่พบ fault ไม่เพียงพอที่จะระบุว่า test oracle เป็นสาเหตุเดียวหรือว่า tests ผ่านบน buggy เสมอ

`bounded_symbolic_hex` เป็นตัวสร้างเฉพาะทางของกลุ่มสำหรับ Lang-1 มี 24 tests ล้มเหลวบน buggy 6 รายการและ fixed 0 รายการ โดยมี Line coverage 54/380 ตาม [ผลกรณีศึกษา](../../Experiment/archive/pilot/summary.csv) รายงานแยกจาก Symflower และไม่รวมเป็น fault ของ Symflower ในชุด 854 กรณี

## ข้อมูลและหลักฐาน

- [ผลรายกรณีที่รวบรวม](../data/symbolic/case_results.csv)
- [ผลรวมรายโครงการ](../data/symbolic/project_summary.csv)
- [ค่าสรุปและ source hashes](../data/symbolic/summary.json)
- [CSV ต้นทางรอบที่ 1](../../Algorithm2_SymbolicExecution/Result_Round1/all_projects/)
- [ตัวอย่างรอบที่ 2](../../Algorithm2_SymbolicExecution/Result_Round2/lang_results_R2_sample.csv)
- [Logs ที่จัดเก็บ](../../Algorithm2_SymbolicExecution/Result_Round1/logs_selected/)
- [Test source ที่จัดเก็บ](../../Algorithm2_SymbolicExecution/Test/)
- [ผลเปรียบเทียบทั้ง 4 วิธี](final_comparison.md)
