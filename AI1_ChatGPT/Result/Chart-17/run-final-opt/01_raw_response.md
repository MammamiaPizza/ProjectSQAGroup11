TARGETS: TimeSeries.createCopy(RegularTimePeriod start, RegularTimePeriod end)
TARGETS: TimeSeries.createCopy(int start, int end) boundary/range validation
ORACLES: Trigger testBug1832432 and bug report 803 identify expected copy behavior
CASES: Copy a populated series using valid chronological start/end periods
CASES: Start/end periods that map to boundary items and preserve selected items
CASES: Reversed period order; observe whether failure is required or erroneous
CASES: Missing start/end periods and empty series copy behavior
RISKS: Context lacks trigger test source and exact expected copied contents
RISKS: Do not infer behavior beyond available API and existing trigger/oracles