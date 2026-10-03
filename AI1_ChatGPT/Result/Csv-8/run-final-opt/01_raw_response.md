TARGETS: CSVFormat.withHeader(String...) duplicate-header validation; trigger expects IllegalArgumentException.
ORACLES: Trigger specification is the sole expected-exception source; do not use another version.
CASES: Duplicate non-null names (e.g., "A","A") should fail with IllegalArgumentException.
CASES: Distinct headers should remain constructible; include null/empty only if current API behavior is observable.
RISKS: Validation may currently throw IllegalStateException; test exception type, not implementation details.
RISKS: Context lacks constructor/internal validation and precise null/empty-header specification.