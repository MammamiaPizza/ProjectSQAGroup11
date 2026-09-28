# สถานะผลทดลองที่ตรวจแล้ว

## Lang-1 — มีผลครบ 4 วิธี

| วิธี | Tests | Fixed line / condition | Buggy line / condition | ตรวจพบ bug |
|---|---:|---|---|---:|
| NSGA-II (expanded run) | 12 | 124/380 / 71/350 | 118/375 / 65/338 | 1 |
| Bounded symbolic hex | 24 | 54/380 / 30/350 | 49/375 / 18/338 | 1 |
| ChatGPT | 75 | 379/380 / 319/350 | 375/375 / 312/338 | 1 |
| GitHub Copilot | 37 | 362/380 / 291/350 | 356/375 / 279/338 | 1 |

Lang-1 symbolic ในตารางคือ bounded symbolic hex ของกลุ่ม ส่วนผล Symflower ของเพื่อนต้องตรวจและรายงานแยก

## Cli-1 — มีผล 3 วิธี

| วิธี | Tests | Fixed line / condition | Buggy line / condition | ตรวจพบ bug |
|---|---:|---|---|---:|
| ChatGPT | 25 | 45/45 / 15/16 | 44/45 / 12/14 | 0 |
| GitHubCopilot | 11 | 45/45 / 16/16 | 44/45 / 13/14 | 0 |
| NSGA-II (post-hoc exploratory) | 7 | 37/45 / 12/16 | 37/45 / 10/14 | 0 |

- Cli-1 manual diagnostic ตรวจพบ bug แต่ไม่รวมเป็นผลของ AI/NSGA-II
- Cli-1 ยังไม่มีผล symbolic จึงยังไม่ใช่การเทียบครบ 4 วิธี
- ตัวหาร coverage ของ buggy และ fixed อาจต่างกัน ให้เทียบภายในเวอร์ชัน
