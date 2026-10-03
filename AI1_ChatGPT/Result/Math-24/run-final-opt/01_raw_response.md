TARGETS: BrentOptimizer.doOptimize(), especially returned UnivariatePointValuePair selection on termination.  
ORACLES: Existing trigger BrentOptimizerTest::testMath855 asserts "Best point not reported".  
CASES: Minimize a univariate objective where the best sampled point must be retained and returned.  
CASES: Exercise convergence/stop condition near interval midpoint and tolerance boundary.  
CASES: Verify returned point/value correspond to the best evaluated point, not merely final iterate.  
RISKS: Available context omits constructors, optimize-call API, objective functions, and full test setup.