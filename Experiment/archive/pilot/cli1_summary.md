# Cli-1: สรุปผลการทดลองปัจจุบัน

| วิธี | Tests | Fixed line / condition | Buggy line / condition | ตรวจพบ CLI-13 |
|---|---:|---|---|---:|
| ChatGPT (Prompt 04) | 25 | 45/45 / 15/16 | 44/45 / 12/14 | 0 |
| GitHubCopilot (Prompt 04) | 11 | 45/45 / 16/16 | 44/45 / 13/14 | 0 |
| NSGA-II (exploratory) | 7 | 37/45 / 12/16 | 37/45 / 10/14 | 0 |

## การอ่านผล

- Coverage วัดจาก test suite ของแต่ละวิธีด้วย Defects4J; buggy และ fixed มีจำนวน conditions ต่างกัน
- Fault detection นับเมื่อ generated test ล้มบน buggy และผ่านบน fixed
- Manual diagnostic 1 test ผ่านบน fixed และล้มบน buggy; ไม่รวมในผลของ AI หรือ NSGA-II
- NSGA-II Cli-1 เป็น post-hoc exploratory เพราะกำหนด candidate pool หลังตรวจ diff
- ยังไม่มีผล symbolic ของ Cli-1 จึงยังเปรียบเทียบครบ 4 วิธีสำหรับเคสนี้ไม่ได้

## หลักฐาน

- ผล AI: `Experiment/cli1_ai_results.csv`
- ผล NSGA-II: `Algorithm1_NSGAII/Result_Round1/Cli-1/run-01/results.csv`
- การวินิจฉัยแยก: `Experiment/diagnostics/Cli-1/results.csv`
