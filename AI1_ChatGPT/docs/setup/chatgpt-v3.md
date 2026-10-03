SQA Final Optimized v3

Install only after currently running workers are stopped.
This v3 keeps the v2 token-saving design but fixes two reliability bottlenecks:
1) P03 complete Java class instead of brittle unified diff.
2) P04 invalid/malformed additions are rejected locally and the already-valid suite is retained; no extra AI call.

Do NOT overwrite ai_runner.py while workers are still running.
