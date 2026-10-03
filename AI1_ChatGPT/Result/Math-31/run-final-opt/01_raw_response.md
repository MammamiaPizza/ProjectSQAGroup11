TARGETS: ContinuedFraction.evaluate(x), evaluate(x,epsilon), evaluate(x,maxIterations), evaluate(x,epsilon,maxIterations)
TARGETS: Recurrence using getA(n,x)/getB(n,x); scaling via FastMath.max(a,b)
ORACLES: Existing trigger tests: BinomialDistributionTest::testMath718 and FDistributionTest::testMath785
ORACLES: Expected failures absent: no NaN-divergence at x=0.5; inverse cumulative probability calculates
CASES: Custom ContinuedFraction coefficients yielding normal finite convergence at x=0.5
CASES: Coefficients with zero/very small values exercising scaling and NaN/infinity safeguards
CASES: epsilon and maxIterations overloads: convergence and iteration-limit/error behavior
RISKS: Abstract getA/getB behavior is caller-defined; no coefficient formula/specification provided
RISKS: Do not derive expected numeric values from another program version