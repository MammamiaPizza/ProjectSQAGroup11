TARGETS: doOptimize(), checkParameters() boundary handling, initializeCMA() boundary scaling,
FitnessFunction.value(), repairAndDecode(), repair(), penalty()
ORACLES: testFitAccuracyDependsOnBoundary expects fitness ~11.1; repaired points stay within
lower/upper bounds; convergence to feasible optimum
CASES: low/high boundary values, tight bounds, unfeasible start, zero-range bounds, high iterations,
different inputSigma, repair of out-of-bounds points
RISKS: No source code body; exact fitness expectation may depend on random seed; assertion value
11.100000000388787 is precise; boundary repair logic unknown