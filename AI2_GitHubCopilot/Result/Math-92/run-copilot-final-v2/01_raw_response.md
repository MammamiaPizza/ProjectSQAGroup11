TARGETS: binomialCoefficient(int n, int k)
TARGETS: binomialCoefficientDouble(int n, int k), binomialCoefficientLog(int n, int k)
ORACLES: known correct values: C(48,22)=27385657281648; basic identities (C(n,0)=1, C(n,1)=n,
C(n,n)=1)
ORACLES: consistency: double variant rounded ≈ long result; symmetry C(n,k)==C(n,n-k); Pascal
recurrence
CASES: large fit: n=48..66,k=22..33; overflow boundary n=66,k=33 exceeds Long.MAX_VALUE; n<k returns
0
CASES: edge: k negative -> IllegalArgumentException; n negative -> IllegalArgumentException; n=0,k=0
CASES: stress: n up to 1000, small k=10 to test long multiplication overflow detection; n=k=0
special case
RISKS: double variant may lose precision for large values; log oracle unavailable; only
binomialCoefficient bug reported
RISKS: no spec for rounding of double; test data limited to failing trigger; other MathUtils methods
unchanged