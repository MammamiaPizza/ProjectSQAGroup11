TARGETS: AbstractEstimator.updateJacobian and parameter/measurement indexing during bounded-parameter estimation  
ORACLES: GaussNewtonEstimatorTest::testBoundParameters; no ArrayIndexOutOfBoundsException (index 6)  
CASES: Bound/fixed parameters with measurements; Jacobian dimension and evaluation counters  
CASES: Normal multi-parameter estimate; parameters at lower/upper bounds  
RISKS: Protected internals require a concrete estimator/test fixture to exercise updateJacobian  
RISKS: Context lacks source and exact expected numeric estimates; derive assertions from available test behavior