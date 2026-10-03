TARGETS: BrentSolver.solve(double min,double max) and solve(double min,double max,double initial)
ORACLES: Trigger expects IllegalArgumentException for non-bracketing endpoints
CASES: Function values at min/max with same nonzero sign; solve(min,max)
CASES: Same non-bracketing interval with an initial value; solve(min,max,initial)
CASES: Bracketing endpoints and endpoint root behavior, if supported by available test context
RISKS: No function setup/constructor details or exact exception message are specified
RISKS: Do not infer convergence, iteration, or initial-value semantics beyond non-bracketing validation