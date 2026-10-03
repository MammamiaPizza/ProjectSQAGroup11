TARGETS: PearsonsCorrelation.getCorrelationPValues() (main two-sided p-value for each cell).
ORACLES: For r=0, two-sided p-value=1.0 (any df≥1). For other r, p = 2P(T>|t|) with
t=rsqrt((n-2)/(1-r^2)), df=n-2.
CASES: r=0 with n=10,20,100 → expect p=1.0.
CASES: r=0.001, n=100 → p≫0.99; r=0.5, n=10 → p≈0.12; r=0.999, n=20 → p≫0; r=±1, n=any → p→0 (or
NaN).
CASES: n=2 → insufficient data exception; n=3, r=0 → p=1.0 (df=1).
CASES: Zero-variance column → correlation undefined (NaN p-values).
RISKS: testPValueNearZero failures suggest bug with p-value near zero (likely 0 returned instead of
1, or NaN).
RISKS: T-distribution from commons-math may have edge-case differences; verify against reference
TDistribution.
RISKS: Two-tailed vs one-tailed confusion could invert p-values for r≈0.