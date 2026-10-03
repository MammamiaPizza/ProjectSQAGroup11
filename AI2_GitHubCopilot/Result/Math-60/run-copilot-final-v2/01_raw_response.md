TARGETS: cumulativeProbability(x) for extreme x (large finite, ±INF, NaN);
inverseCumulativeProbability(p) for p near 0/1.
TARGETS: density(x) for extreme x to verify no NaN.
ORACLES: CDF monotonic, in [0,1]; CDF→0 for x→-∞,→1 for x→+∞; asymptotic 1 - density(x)/x for x>>μ.
ORACLES: inverseCDF(p) should satisfy CDF(inverseCDF(p)) ≈ p for p in (0,1); check consistency.
CASES: x = mean ± 38sd, mean ± 100sd (deep tail), ±Double.MAX_VALUE (max finite).
CASES: x = Double.POSITIVE_INFINITY (expect 1.0), Double.NEGATIVE_INFINITY (expect 0.0), NaN.
CASES: p = 0.0, 1.0 for inverse (should throw or return ±∞); p = 1e-300, 1-1e-300 for extreme finite
p.
RISKS: ConvergenceException may still occur for extreme finite x with large sd or non-zero mean;
continued fraction fragile.
RISKS: No specification for infinite or NaN input; implementation behaviour (e.g., return or throw)
is an implicit oracle.
RISKS: InverseCDF near p=0/1 may overflow to ±Double.MAX_VALUE or produce NaN; unclear expected
error tolerance.