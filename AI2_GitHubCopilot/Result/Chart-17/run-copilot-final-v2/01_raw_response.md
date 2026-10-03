TARGETS: TimeSeries.createCopy(RegularTimePeriod,RegularTimePeriod) resolving bounds via getIndex().
TARGETS: TimeSeries.createCopy(int,int) throws IllegalArgumentException "Requires start <= end".
TARGETS: getIndex(RegularTimePeriod) return values for unmatched periods; getDataItem(int) range
copies.
ORACLES: Trigger test expects copy to complete without IllegalArgumentException; no other source
given.
ORACLES: A valid copy is a TimeSeries carrying original name/subclass and inclusive range items.
CASES: Normal existing start/end; equal start/end (single-item copy).
CASES: Boundaries: endpoints before first, after last, between data points (unmatched/insertion
positions).
CASES: Empty series, single-item series, and start chronologically after end.
RISKS: No test body/javadoc; exact expected clamping or insertion-point semantics for absent periods
unknown.
RISKS: Reverse ranges may still legitimately throw; do not assert they succeed.