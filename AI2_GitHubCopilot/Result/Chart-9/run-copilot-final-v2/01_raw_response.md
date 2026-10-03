TARGETS: TimeSeries.createCopy(RegularTimePeriod,RegularTimePeriod) start/end validation; also
validate delete(int,int) if impacted.
ORACLES: Empty series with start==end returns empty copy (no exception); start>end throws
IllegalArgumentException; valid range returns correct subset.
CASES: empty series copy with start==end; non-empty valid range; start>end; boundary periods;
periods not in series.
RISKS: Only bug report 818 and test name; exact expected behavior (e.g., start>end must throw)
inferred from existing exception.