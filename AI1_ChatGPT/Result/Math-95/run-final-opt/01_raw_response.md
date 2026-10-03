TARGETS: FDistributionImpl.inverseCumulativeProbability and protected domain-bound/initial-domain methods.  
ORACLES: Existing FDistributionTest::testSmallDegreesOfFreedom and inverse-CDF endpoint validity/no exception.  
CASES: Small numerator/denominator degrees of freedom with probability requiring inverse-CDF solving.  
CASES: Verify initial domain lies within lower/upper bounds for relevant small-DF probabilities.  
CASES: Boundary probabilities 0 and 1 if supported by existing inverse-CDF behavior.  
RISKS: Reported failure has initial=-1.0 below lowerBound=0.0; avoid assuming unprovided numeric quantiles.  
RISKS: Only buggy-version context is available; expected values must come from existing tests/specification.