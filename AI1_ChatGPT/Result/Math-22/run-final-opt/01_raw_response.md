TARGETS: FDistribution.isSupportLowerBoundInclusive(), UniformRealDistribution.isSupportUpperBoundInclusive()
ORACLES: Trigger assertions: F lower inclusivity=false; Uniform upper inclusivity=true
CASES: Construct valid FDistribution; assert lower-bound flag is false
CASES: Construct UniformRealDistribution(lower, upper); assert upper-bound flag is true
CASES: Check flags across multiple valid parameter values; flags should not depend on parameters
RISKS: Context provides no constructor signatures for FDistribution or parameter-validation behavior