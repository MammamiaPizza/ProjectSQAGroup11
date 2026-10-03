TARGETS: ArchiveUtils.sanitize(String) — truncation logic for strings exceeding max length.
ORACLES: Expected sanitize output must shorten to a fixed char limit plus "..." (only if input
exceeds limit).
CASES: Short/no-op strings; exactly-at-limit; one-over; far-over limit with varied char types; null
input; empty string.
RISKS: Max-length constant not visible; true expected truncation limit must be inferred from the
test assertion differences.