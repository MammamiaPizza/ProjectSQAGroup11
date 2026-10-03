TARGETS: SimpleRegression.getSumSquaredErrors() after addData; related regression summary values  
ORACLES: Trigger test SimpleRegressionTest::testSSENonNegative; SSE must not be negative  
CASES: Add numerically challenging data yielding near-zero residual error; assert SSE >= 0  
CASES: Exact linear data, repeated addData calls, and addData(double[][]) accumulation  
CASES: Boundary sample sizes before/after enough points for regression statistics  
RISKS: Floating-point cancellation may produce a small negative SSE  
RISKS: Context provides no source/body details or broader expected values beyond the trigger