TARGETS: FDistribution.isSupportLowerBoundInclusive(),
UniformRealDistribution.isSupportUpperBoundInclusive()
ORACLES: F-distribution support (0,∞) → lower bound not inclusive; UniformReal support [lower,upper]
→ upper bound inclusive
CASES: FDist with numeratorDF=1, denominatorDF=1; FDist with numeratorDF=10, denominatorDF=100; both
inheriting DEFAULT_INVERSE_ABSOLUTE_ACCURACY
CASES: UniformReal default(-1,1); UniformReal(0,1); UniformReal(-100,100); UniformReal(1e-9,1e9)
ORACLES: Respect mathematical definition; not dependent on constructor parameters;
isSupportConnected must return true
RISKS: Ensure both inclusivity methods are tested; no side-effects on
getSupportLowerBound/getSupportUpperBound
RISKS: FDistribution lower bound always 0.0 (not inclusive); UniformReal lower bound inclusive,
upper bound inclusive