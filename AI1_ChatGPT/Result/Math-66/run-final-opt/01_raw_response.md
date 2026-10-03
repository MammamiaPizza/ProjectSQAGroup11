TARGETS: BrentOptimizer.optimize overloads; doOptimize minimization/maximization convergence and evaluation statistics.
ORACLES: Trigger assertions: quintic minimum -0.2719561270319131; sin minimum 4.71238898038469.
ORACLES: Existing BrentOptimizerTest expected evaluation statistic 1880.5, not observed 18.0.
CASES: Quintic and sin minimization over trigger-provided intervals/start values; assert result within test tolerance.
CASES: MultiStartUnivariateRealOptimizer using BrentOptimizer; verify best minimum selection across starts.
CASES: Boundary/start-value variants only where available APIs/tests establish expected behavior.
RISKS: Exact intervals, tolerances, function definitions, and statistic meaning are not provided in this context.
RISKS: No source/test bodies supplied; avoid inferring exception behavior or additional numerical expectations.