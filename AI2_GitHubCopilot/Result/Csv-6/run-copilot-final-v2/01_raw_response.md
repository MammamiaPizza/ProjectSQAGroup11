TARGETS: CSVRecord.toMap(), CSVRecord.putIn(Map), CSVRecord.get(int) with indices at/beyond size
ORACLES: toMap() must not throw ArrayIndexOutOfBoundsException; output map should contain all header
keys
ORACLES: Expected behavior for missing columns: skip entry or map to null; consistency with putIn()
CASES: mapping key index == record.size() (just beyond last element)
CASES: mapping key index > record.size(); empty record with non-empty mapping
CASES: record length smaller than max mapping index; record length larger than mapping extent
CASES: zero-length mapping (empty headers) but non-empty record
RISKS: No spec for short-record handling; test must infer graceful outcome (no crash, no
inconsistent state)