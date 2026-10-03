TARGETS: put(Object,Object) and putAll(Map) — likely return incorrect values
ORACLES: put returns null for new key, existing collection for duplicate key
ORACLES: putAll should return true if map changed, false otherwise
CASES: new-key put then existing-key put with same key; check return values
CASES: putAll with empty, non‑overlapping, and overlapping entries; check boolean
CASES: boundary: large values, null keys/values, duplicate keys in source map
RISKS: MultiValueMap.putAll return semantics not fully documented; incomplete test context
RISKS: tests require decorate() creation as internal factory is not public