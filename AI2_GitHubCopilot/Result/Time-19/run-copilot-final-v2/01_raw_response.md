TARGETS: DateTimeZone.getOffsetFromLocal(long), convertLocalToUTC(long,boolean), getOffset(long)
ORACLES: Test expects +01:00 for Europe/London at 1891-10-30T01:15:00 (gap time); derived from
historical timezone data
CASES: Instant in DST gap (01:15), gap start/end (01:00:00.000, 02:00:00.000)
CASES: Non-gap times before/after transition, winter UTC+0, ambiguous overlap times
RISKS: Pre-1900 timezone data may differ; test expectation is oracle; no access to buggy diff