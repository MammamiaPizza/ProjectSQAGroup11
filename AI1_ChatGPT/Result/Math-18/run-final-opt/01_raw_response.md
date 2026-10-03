TARGETS: CMAESOptimizer.doOptimize boundary handling through FitnessFunction encode/repairAndDecode/value.  
ORACLES: Existing trigger expects 11.100000000388787, not 8.0, for boundary-dependent fit accuracy.  
ORACLES: PointValuePair result and objective value from optimize are observable public optimization outcomes.  
CASES: Reproduce testFitAccuracyDependsOnBoundary with its bounded optimization setup.  
CASES: Compare fitting behavior under the trigger's boundary configurations, including the failing boundary.  
CASES: Exercise feasible start/candidates at lower and upper bounds and candidates requiring repair.  
RISKS: CMA-ES is randomized; use deterministic RandomGenerator/configuration if exposed by existing test setup.  
RISKS: Private FitnessFunction helpers cannot be directly tested without reflection; test via public optimizer API.  
RISKS: Context omits trigger objective, bounds, and optimizer invocation details; derive only from existing test.