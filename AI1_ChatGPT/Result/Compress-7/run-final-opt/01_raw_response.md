TARGETS: TarUtils.formatNameBytes and parseName round-trip non-ASCII names (trigger behavior).
ORACLES: Exact original String after formatNameBytes into fixed buffer then parseName.
CASES: Trigger-like name containing ± and non-ASCII characters within the field length.
CASES: ASCII name round-trip; name shorter than field should parse without trailing NULs.
CASES: Boundary names at field capacity and names exceeding capacity; assert returned formatted length.
RISKS: Character-to-byte encoding is unspecified by provided context; avoid assuming encoding beyond observed round-trip.
RISKS: No source/body context for truncation, NUL handling, or error behavior; test only observable API contracts.