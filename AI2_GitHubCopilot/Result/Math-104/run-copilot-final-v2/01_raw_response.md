TARGETS: org.apache.commons.math.special.Gamma.regularizedGammaP, regularizedGammaQ
ORACLES: Identity regularizedGammaQ(a,x)=1-regularizedGammaP(a,x) within epsilon
ORACLES: Expected value 0.632120558828558 from failing test (assumed correct reference)
CASES: Positive normal pairs e.g. a∈{0.5,1,2,5,10}, x∈{0.1,0.5,1,5,10,100}
CASES: Boundary: a very small (~0.01), x extremely large/small; a large, x~0 or x>>a
CASES: Error: a≤0 or x≤0 (should throw or return NaN; check behavior)
CASES: Pairs from bug trigger: testRegularizedGammaPositivePositive assertions
RISKS: Floating precision may break identity in extreme ranges; use relative/absolute tolerance
RISKS: Bug may reside in continued-fraction helpers getA/getB; indirect coverage is limited
RISKS: Only public Gamma API visible; cannot directly test protected methods getA, getB