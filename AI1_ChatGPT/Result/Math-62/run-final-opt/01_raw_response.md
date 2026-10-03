TARGETS: MultiStartUnivariateRealOptimizer multi-start optimization and getOptima result ordering  
ORACLES: testQuinticMin expects optimum point -0.2719561293 (trigger assertion)  
CASES: quintic minimization with deterministic first start; verify best optimum precision  
CASES: getOptima sorted by GoalType using returned point-value pairs  
CASES: evaluation accounting across starts; max-evaluation limit behavior  
RISKS: Random subsequent starts can make exact numeric assertions unstable  
RISKS: optimize method signature and expected exception behavior are not provided