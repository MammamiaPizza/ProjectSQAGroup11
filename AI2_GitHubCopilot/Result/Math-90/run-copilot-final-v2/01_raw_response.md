TARGETS: addValue(Object) must handle non-Comparable inputs without throwing unwanted
IllegalArgumentException; also getCount(Object) for such objects.
ORACLES: Test expects no exception when adding non-Comparable value (or expects a different
exception); derived from testAddNonComparable triggering failure.
CASES: Add non-Comparable (e.g., new Object()) with empty frequency; add after some Comparable; add
null; boundary: large numbers of distinct non-Comparables.
RISKS: Unknown exact test assertions; only stacktrace available; may be that exception message is
wrong rather than type.