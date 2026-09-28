# NSGA-II exploratory run: Cli-1

- Candidate suites evaluated: 24
- Selected tests: 7 (missing_value, flag, default_present, hyphen_lookup, object_short, remaining_args, option_iterator)
- Fixed coverage: 37/45 lines; 12/16 conditions
- Buggy coverage: 37/45 lines; 10/14 conditions
- Generated tests failing on buggy: 0
- Detected fault: 0
- Failing generated tests: none

Fitness ใช้ coverage จาก Defects4J บน Cli-1f; ตรวจ fault ด้วยชุดที่เลือกบน Cli-1b
การทดลองนี้เป็น post-hoc exploratory: candidate pool ถูกกำหนดหลังเห็น diff
ของ buggy/fixed จึงไม่รวมอัตราตรวจพบนี้กับผล AI แบบปิดข้อมูล
