TARGETS: addRecordValue() null/empty handling, initializeHeader(), getHeaderMap(), getRecords(),
CSVRecord iteration
ORACLES: CSVFormat.getNullString() contract; record size must match header count; null substitution
only for matching tokens
CASES: header=null,nullString set; empty header; missing-header CSV; all-null values; token equals
nullString case-insensitive
RISKS: CSVFormat configuration unpinned; expected null-to-header mapping unspecified; buggy source
may misbehave on duplicates