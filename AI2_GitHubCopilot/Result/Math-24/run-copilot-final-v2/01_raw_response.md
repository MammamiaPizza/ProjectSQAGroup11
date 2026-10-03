TARGETS: BrentOptimizer.doOptimize() and UnivariatePointValuePair return
TARGETS: Optimizer state after partial convergence; getGoalType(), getMin(), getMax()
ORACLES: Best point (x) and value (f(x)) must match expected optimum
ORACLES: JUnit4 assert; check returned UnivariatePointValuePair non-null and correct
CASES: MATH-855 trigger test with unbounded/limited search intervals
CASES: Boundary: tol1/tol2 threshold; mid==lo; mid==hi; relative tolerance near
MIN_RELATIVE_TOLERANCE
CASES: GoalType.MAXIMIZE and MINIMIZE paths through golden-section + parabolic
RISKS: No access to full source; only partial doOptimize snippet provided
RISKS: Exact expected values for MATH-855 unknown without running fixed version