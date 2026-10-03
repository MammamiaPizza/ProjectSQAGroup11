TARGETS: Frequency.getPct(Object v) – buggy; must test with object-type keys
TARGETS: Frequency.getPct(Comparable<?> v) – regression check; may share code path
TARGETS: Frequency.addValue(Object v), getCount(Object v), getSumFreq() – test setup dependencies
ORACLES: getPct(v) = (double) getCount(v) / getSumFreq(); expected ratio 0..1, not percent*100
ORACLES: Known frequencies: add "A" 3x, "B" 1x → getPct("A")=0.75, getPct("B")=0.25
CASES: Add values via addValue(Object) and addValue(Comparable) with same counts; assert getPct
equal
CASES: Mix addValue(int), addValue(Integer), and addValue(Object) for same int; check getPct
consistency
CASES: Empty frequency table → getPct gives Double.NaN or 0; single entry → returns 1.0
CASES: Multiple objects with equal frequency; verify each getPct matches expected reciprocal
RISKS: getCumPct(Object) may share the same miscalculation; needs separate verification