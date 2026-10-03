TARGETS: BOBYQAOptimizer#doOptimize called via optimize(ConstrainedRosenbrock,...), npt>n+2
(>default)
TARGETS: PathIsExploredException must not be thrown
ORACLES: Normal termination returns RealPointValuePair; value approx 0.0; no RuntimeException
ORACLES: Source: Bug report MATH-728: exception at line 1752 should be removed (preliminary)
CASES: Rosenbrock n=2, bounds=[-5,5], interpolation points=6 (n+4); maxEval=1e6
CASES: n=3, points=8 (2n+2); n=5, points=12 (2n+2), bounds=[-2,2]
CASES: boundary: npt=2*n+1 (max allowed), ensure no exception
CASES: error: npt<n+2 should throw IllegalArgumentException from constructor
RISKS: Non-deterministic trust-region steps; could mask exception; run test repeatedly or large
maxEval
RISKS: Only known to trigger on constrained Rosenbrock; unknown if other functions trigger