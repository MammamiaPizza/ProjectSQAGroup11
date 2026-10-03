TARGETS: checkParameters() boundary-range validation; doOptimize() calls it.
ORACLES: Expect org.apache.commons.math3.exception.NumberIsTooLargeException.
CASES: Range just above limit (trigger), range at limit (passed), null bounds, one bound very far
from other.
RISKS: Unknown exact max allowed difference; only one trigger test; fix may alter other exception
sites.