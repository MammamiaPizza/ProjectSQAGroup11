TARGETS: CaseInsensitiveMap.convertKey(Object); map lookup behavior using case-insensitive String keys
ORACLES: Trigger expects get("...") to return stored "value" regardless of default Locale
CASES: Put a String key, change default Locale, then get same key with different letter case
CASES: Exercise Turkish/Azeri-sensitive I/i casing and English locale independence
CASES: Verify non-String and null-key behavior only if supported by existing map operations
RISKS: convertKey is protected; test through public put/get behavior
RISKS: Context lacks implementation and full API semantics; avoid asserting unsupported null/non-String expectations