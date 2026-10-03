TARGETS: setMeanImpl/getMean, setGeoMeanImpl/getGeometricMean, setVarianceImpl/getVariance after addValue  
TARGETS: Same override behavior through SynchronizedSummaryStatistics inheritance/delegation  
ORACLES: Trigger expectations: values 1,2,3,4 yield mean 2.5, geometric mean 2.213363839400643, variance 1.25  
CASES: Configure each implementation override before adding 1.0,2.0,3.0,4.0; assert its getter uses added data  
CASES: Verify n remains 4 and unaffected aggregate overrides do not prevent value ingestion  
CASES: Boundary: setter behavior on empty instance is relevant; checkEmpty may reject replacement after additions  
RISKS: Custom StorelessUnivariateStatistic construction/API behavior is not provided; avoid assuming unspecified implementations  
RISKS: No independent specification for population variance, clear/copy, or exact setter exception types provided