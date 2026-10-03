TARGETS: PoissonDistributionImpl constructors and getMean(); trigger is PoissonDistributionTest::testMean.  
ORACLES: Bug report says Poisson mean must be positive; constructor/getMean behavior is the available expectation source.  
CASES: Construct with a positive mean and assert getMean returns that configured mean.  
CASES: Boundary/error means: zero and negative values, especially -1 from trigger; assert documented validation outcome.  
RISKS: No explicit expected exception type/message or zero-mean policy is provided.