# Final AI Experiment Summary

ผลสรุปจาก source-of-truth:

- ChatGPT: `run-final-opt`
- GitHub Copilot: `run-copilot-final-v2`

## Final status

| Method | Total | DONE | Invalid after repair | Invalid after P04 | Output incomplete |
|---|---:|---:|---:|---:|---:|
| ChatGPT | 854 | 561 | 272 | 8 | 13 |
| GitHub Copilot | 854 | 229 | 599 | 0 | 26 |

## Fault detection

| Method | Measured cases | Detected cases | Fault detection rate |
|---|---:|---:|---:|
| ChatGPT | 561 | 403 | 71.84% |
| GitHub Copilot | 229 | 166 | 72.49% |

## Fixed-version coverage

| Method | Line coverage cases | Avg line coverage | Condition coverage cases | Avg condition coverage |
|---|---:|---:|---:|---:|
| ChatGPT | 559 | 51.00% | 556 | 42.60% |
| GitHub Copilot | 227 | 48.29% | 226 | 41.85% |

## Token usage

| Method | Cases with token data | Total tokens | Average tokens/case |
|---|---:|---:|---:|
| ChatGPT | 854 | 9759806 | 11428.34 |
| GitHub Copilot | 854 | 22888274 | 26801.26 |

หมายเหตุ: coverage และ fault detection สรุปเฉพาะกรณีที่มีค่าที่วัดได้จริง ไม่แทนค่าที่หายไปด้วย 0
