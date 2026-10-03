# Token-Saving Final Protocol

Final optimized run namespace: `run-final-opt`. Previous `run-final` artifacts are pilot/protocol-validation evidence and are not mixed into the optimized final dataset.

The four prompt stages remain P01 -> P02 -> optional single P03 -> actual coverage -> P04. The same prompt wording and deterministic context rules are used for ChatGPT and GitHub Copilot. No hard provider output cap is configured. Context is always derived from the buggy checkout; fixed code is never shown to the AI.

Token reduction rules:
1. P01 receives bug metadata and API signatures only and requests <=10 short lines.
2. P02 receives a deterministic source budget (12,000 characters maximum across relevant artifacts) plus the compact P01 plan; it returns Java only, with at most 12 high-value test methods.
3. P03 receives the generated suite, compact compiler/test failure excerpt, bug summary, and API signatures; one repair round only; it returns a minimal unified diff (the runner accepts complete-Java fallback without another request).
4. P04 receives compact actual coverage, a summary of existing tests, and buggy-source snippets near uncovered lines. It adds at most 4 tests and returns only new Java class members or `NO_CHANGE`; the runner merges them.
5. Duplicate provider JSON, duplicate sent-prompt copies, persistent suite archives, full successful validation logs, and full coverage XML are not stored. Raw assistant text, sent prompt, stage metadata, generated/repaired/final Java, compact validation evidence, coverage summary, case status, and final evaluator results remain.
6. Infrastructure errors may be retried; model outcomes such as invalid-after-repair or output-incomplete are not rerun merely to obtain a better result.

Measurement target: <=13,000 tokens/bug on average (soft target, not a hard cap).


## Continuation priority
After the 3-case optimized pilot, long-running workers use `--skip-terminal-from-run final`.
This means outcomes already measured in the old `run-final` (`DONE`,
`INVALID_AFTER_REPAIR`, `INVALID_AFTER_PROMPT04`, `OUTPUT_INCOMPLETE`) are not
regenerated yet. Old infrastructure `ERROR`/`QUOTA_PAUSED` cases and all untouched
cases are prioritized. This is a deadline-oriented continuation strategy; the report
must disclose the protocol transition instead of presenting both prompt protocols as
identical.


## v3 reliability rules
- Prompt 03 returns one complete corrected Java class instead of a unified diff. This removes diff-context parser failures; the repair budget remains exactly one AI call.
- Prompt 04 remains a compact extension-only call. If its returned extension is malformed or fails fixed-version validation, the extension is deterministically rejected and the already fixed-valid pre-P04 suite is retained. No extra AI call is made. The rejection is recorded in `04_extension_status.json`.
- These rules should be applied identically to ChatGPT and GitHub Copilot for comparable runs.
