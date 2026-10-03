# Final AI Experiment Summary

ผลสรุปจาก source-of-truth:

- ChatGPT: `run-final-opt`
- GitHub Copilot: `run-copilot-final-v2`

## Case outcomes

`Successfully evaluated` หมายถึง raw status `DONE`: test suite ผ่าน fixed-version validation และมีผล final evaluation แล้ว ไม่ได้หมายความว่าทุก attempted case สำเร็จ

| Method | Attempted cases | Successfully evaluated | Evaluation rate | Invalid after repair | Invalid after P04 | Incomplete output |
|---|---:|---:|---:|---:|---:|---:|
| ChatGPT | 854 | 561 | 65.69% | 272 | 8 | 13 |
| GitHub Copilot | 854 | 229 | 26.81% | 599 | 0 | 26 |

## Fault detection

| Method | Cases with fault-detection result | Fault-detecting cases | Detection rate among measured cases |
|---|---:|---:|---:|
| ChatGPT | 561 | 403 | 71.84% |
| GitHub Copilot | 229 | 166 | 72.49% |

## Fixed-version coverage

| Method | Cases with line coverage | Avg fixed line coverage | Cases with condition coverage | Avg fixed condition coverage |
|---|---:|---:|---:|---:|
| ChatGPT | 559 | 51.00% | 556 | 42.60% |
| GitHub Copilot | 227 | 48.29% | 226 | 41.85% |

## Token usage

| Method | Cases with token records | Total tokens | Average tokens per recorded case |
|---|---:|---:|---:|
| ChatGPT | 854 | 9759806 | 11428.34 |
| GitHub Copilot | 854 | 22888274 | 26801.26 |

หมายเหตุ: coverage และ fault detection สรุปเฉพาะกรณีที่มีค่าที่วัดได้จริง ไม่แทนค่าที่หายไปด้วย 0
