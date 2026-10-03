TARGETS: static encodeBase64 encodeBase64String encodeBase64URLSafe encodeBase64URLSafeString
encodeBase64Chunked encodeToString encode
ORACLES: non-chunked output must have no trailing line separator; chunked output must have correct
separator (e.g., "\r\n" or user‑supplied)
ORACLES: known plaintext "Hello World" → "SGVsbG8gV29ybGQ="; "f" → "Zg=="; RFC 4648 §10 vectors
CASES: empty input, single byte, "Hello World", all‑byte values 0‑255, large binary (multiple
chunks), length 0‑76 boundary
CASES: URL‑safe variants (no '+' '/'), decode‑then‑encode roundtrip, instance vs static methods,
custom line separators
RISKS: exact static‑method implementation not shown; relies on observed test failures for expected
behavior; need to confirm \n vs \r\n line separator default