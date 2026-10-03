TARGETS: updateJacobian and computeWeightedJacobian memory behavior for large target/sample sizes.  
TARGETS: Jacobian dimensions/contents, jacobian-evaluation count, and weight-square-root application.  
ORACLES: PolynomialFitterTest.testLargeSample must complete without OutOfMemoryError.  
ORACLES: Existing target, model, weights, residual, cost, RMS, and chi-square computations define results.  
CASES: Large polynomial-fitting sample exercising repeated Jacobian updates and optimizer convergence.  
CASES: Normal small sample confirms Jacobian rows/cols and weighted residual/cost-derived metrics.  
CASES: Boundary rows versus cols for guessParametersErrors and covariance/error calculations.  
RISKS: No fixed sample size, heap limit, expected coefficients, or exact allocation behavior is provided.