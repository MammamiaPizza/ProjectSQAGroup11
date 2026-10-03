TARGETS: BrentOptimizer.doOptimize() result selection for GoalType.MINIMIZE/MAXIMIZE over [min,max] with start value.
ORACLES: Existing BrentOptimizerTest.testKeepInitIfBest asserts the best point is reported.
CASES: Start value is already best; verify returned UnivariatePointValuePair retains that point.
CASES: Normal interior optimum and interval-boundary optimum; compare reported point/value to objective evaluations.
CASES: Both minimization and maximization, since doOptimize branches on GoalType.
RISKS: Regression may replace the initial best point during Brent interpolation/golden-section updates.
RISKS: Context omits constructors, public optimize signatures, and exact objective/test values.