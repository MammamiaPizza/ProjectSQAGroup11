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

## Prompt 03 — Repair suite

The original 56-test suite had one failure on Lang-1f caused by requiring
a specific numeric class for -0x80000000. The repaired suite checks its
numeric value instead. After repair, all 56 tests passed on Lang-1f; two
LANG-747 regression tests failed on Lang-1b.

Repaired-suite coverage:
- Lang-1f: 347/380 lines (91.3%), 259/350 conditions (74.0%).
- Lang-1b: 343/375 lines (91.5%), 252/338 conditions (74.6%).

## Prompt 04 — Extend coverage

ChatGPT preserved the 56 repaired tests and added 19 tests (75 total).
All 75 passed on Lang-1f. On Lang-1b, only the positive and negative
eight-digit hexadecimal LANG-747 regression tests failed.

Extended-suite coverage:
- Lang-1f: 379/380 lines (99.7%), 319/350 conditions (91.1%).
- Lang-1b: 375/375 lines (100.0%), 312/338 conditions (92.3%).

The raw AI responses, separate test versions, test logs, and coverage
reports are retained. Coverage denominators differ between Lang-1b
and Lang-1f, so their percentages are reported separately.
