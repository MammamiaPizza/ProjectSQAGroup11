TARGETS: CSVFormat.withHeader(String...) duplicate-header detection
ORACLES: API contract / Javadoc says IllegalArgumentException on duplicate header names
CASES: Duplicate at start, middle, end; all duplicate; single repeated element; normal unique
CASES: Empty header array; null elements in header; case-sensitive duplicates; large header set
RISKS: No official spec cited; other methods (toString) may also throw wrong exception type
RISKS: Only String... variant tested; duplicate detection via Collection may be missed
RISKS: Behavior for null/empty header, or null elements in header, may be unspecified