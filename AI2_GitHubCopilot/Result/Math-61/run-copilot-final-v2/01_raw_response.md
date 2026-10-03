TARGETS: PoissonDistributionImpl constructors; validate mean p rejects non-positive values.
ORACLES: Expect MathRuntimeException with "mean must be positive" for p <= 0; getMean() returns p.
CASES: p=0.0, p=-0.001, p=-1.0, p=NaN? (boundary/invalid); p=0.5, p=1E-12, p=1E6 (valid
normal/boundary).
RISKS: Exception subtype unknown (MathRuntimeException subclass); no spec for NaN/Infinity mean.