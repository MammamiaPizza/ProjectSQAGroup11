TARGETS: toMap() NPE when mapping is null (no header)
ORACLES: null mapping → consistent empty Map or documented exception
CASES: no-header; header with missing cols; empty record; get(String) on no-header
RISKS: toMap() impl may not null-guard mapping field