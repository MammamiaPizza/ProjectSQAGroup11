TARGETS: gcd(int u, int v) – sign, zero, magnitude, Integer.MIN_VALUE combos.
ORACLES: Expected = gcd(|u|,|v|); verify match with known correct Euclidean algo; reference expected
98304 from test.
CASES: (pos,pos), (pos,neg), (neg,neg), (0,x), (x,0), (0,0), (MIN_VALUE,0), (MIN_VALUE,-1),
(MIN_VALUE,MIN_VALUE), large near MAX_VALUE, input pair that yields 98304.
RISKS: Integer.MIN_VALUE negation overflows; no explicit spec; oracle derived solely from testGcd
expectation; gcd(0,0) semantics ambiguous.