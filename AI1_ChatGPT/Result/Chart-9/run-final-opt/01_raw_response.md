TARGETS: TimeSeries.createCopy(RegularTimePeriod start, RegularTimePeriod end).
TARGETS: TimeSeries.createCopy(int start, int end) range handling.
ORACLES: Existing TimeSeriesTests.testBug1864222 is the available expected-result source.
CASES: Period-based copy with valid ordered start/end periods.
CASES: Requested periods outside stored data, including ranges after the final item.
CASES: Boundary copy at first/last stored periods and an empty resulting range.
RISKS: Trigger currently throws IllegalArgumentException: "Requires start <= end."
RISKS: No source/docs for intended result beyond the trigger failure and exposed API context.