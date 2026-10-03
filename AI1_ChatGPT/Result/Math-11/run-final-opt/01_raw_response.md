TARGETS: MultivariateNormalDistribution.density(double[]), sample(), getStandardDeviations()
TARGETS: covariance eigendecomposition/eigenvector-derived samplingMatrix construction
ORACLES: Existing testUnivariateDistribution expected density 0.23644016090654427
ORACLES: Trigger reports actual density 0.5926675925866471 for its univariate case
CASES: Reproduce the existing univariate distribution density input and expected value
CASES: Check sample output dimensionality and standard-deviation values for univariate covariance
RISKS: Exact trigger constructor arguments and density input are not provided in this context
RISKS: Avoid assuming sampling randomness values without a controlled RNG or existing oracle