TARGETS: Weight(double[]) large-obs constructor; Weight(RealMatrix) dense;
computeWeightedJacobian(params); getWeightSquareRoot()
ORACLES: Unweighted == Weight(all-ones) — same cost/RMS/chi-square; Weight(double[]) memory grows
~linearly, not O(n^2)
ORACLES: Weighted cost = sum(w_i * res_i^2); chi-square = sum(w_i * r_i^2) when σ_i² = 1/w_i;
precomputable for known linear model
CASES: 2-param linear, 3 obs, w=[1,2,0.5] -> compare cost/RMS/chi² with manual values
CASES: Weight(double[]) with zero entry -> expect IllegalArgumentException or leading to NaN
(boundary)
CASES: Weight(RealMatrix) non-diagonal (if accepted) -> check cost uses quadratic form r^T W r
CASES: 10⁵ obs, 2 params, Weight(double[]) with -Xmx32m -> expect OutOfMemoryError (bug indicator,
expected=OOME)
CASES: computeWeightedJacobian for small case: output dimensions (obs×par) and entry values based on
weight sqrt * Jacobian
RISKS: OOM test fragile (heap-dep); may use assumeTrue(maxHeap<256M) and
expected=OutOfMemoryError.class
RISKS: Protected computeWeightedJacobian needs subclass (e.g., LevenbergMarquardtOptimizer) or
reflection; Weight internals hidden