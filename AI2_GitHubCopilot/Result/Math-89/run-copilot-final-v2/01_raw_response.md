TARGETS: Frequency.addValue(Object) handling non-Comparable argument
ORACLES: testAddNonComparable expects no ClassCastException; likely IllegalArgumentException
CASES: add Integer/String (Comparable) then getCount/getPct
CASES: add non-Comparable Object (should not throw ClassCastException)
CASES: add null via deprecated addValue(Object) (expected NPE or similar)
CASES: boundary: empty Frequency, getSumFreq=0, getCount returns 0
CASES: multiple additions of same value, getCount returns multiplicity
RISKS: no formal spec for non-Comparable; expected exception type unknown