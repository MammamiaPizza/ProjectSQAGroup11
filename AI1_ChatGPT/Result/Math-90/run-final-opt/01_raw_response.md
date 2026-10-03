TARGETS: Frequency.addValue(Object), especially insertion of values non-comparable to existing keys.  
ORACLES: MATH-259 and FrequencyTest::testAddNonComparable are the expected-result sources.  
CASES: Add comparable values, then add a non-comparable Object value.  
CASES: Add a non-comparable value to an empty Frequency versus a populated Frequency.  
CASES: Verify state/counts remain consistent when incompatible insertion is rejected.  
RISKS: Natural ordering and a supplied Comparator may produce different comparability behavior.  
RISKS: Context provides only the failing exception, not the test's asserted expected outcome.