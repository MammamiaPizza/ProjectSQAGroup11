TARGETS: Weight(double[]), Weight(RealMatrix), getWeight(); optimizer weight-square-root and weighted Jacobian paths.  
ORACLES: Existing PolynomialFitterTest::testLargeSample is the only stated regression oracle; it must avoid heap OOM.  
CASES: Large double[] weight/sample path used by PolynomialFitterTest::testLargeSample.  
CASES: Matrix-weight constructor and getWeight behavior, including weight representation consumed by optimizer.  
RISKS: Large dense weight or square-root/intermediate matrix allocation can cause OutOfMemoryError.  
RISKS: No explicit expected numeric values, invalid-input behavior, or full call setup is provided.