# Optimized Prompt Protocol (Final)

This prompt set keeps the same four-stage experiment for both AI providers while reducing repeated context and completion text.

- P01: compact bug/API analysis only; no production source dump; maximum 10 short lines requested.
- P02: one complete Java test class, at most 12 high-value tests; Java only; production context is deterministically compacted from the buggy version.
- P03: one repair round only; current test + compact failure evidence + API signatures; returns a minimal unified diff (complete Java fallback accepted).
- P04: actual coverage + existing-test summary + buggy-source snippets; adds at most 4 new tests and returns only new class members (or `NO_CHANGE`) for runner merge.
- No hard `max_tokens` is sent to the provider.
- Fixed source is never supplied to the AI. The fixed version is used only for external validation/coverage as defined by the experiment.
- Target: approximately <=13,000 tokens per bug on average; this is a measurement target, not a hard per-case cap.
