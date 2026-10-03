TARGETS: getHeaderMap() returns null headerMap causing NPE when no header defined
ORACLES: getHeaderMap() should return null (or empty) if no header; test expects no exception
CASES: parse CSV with no header; call getHeaderMap(); parse with header and verify non-null
CASES: test after init but before any record read; test after reading records without header
RISKS: initializeHeader() sets headerMap only if formatHeader!=null; missing null check in
getHeaderMap usage