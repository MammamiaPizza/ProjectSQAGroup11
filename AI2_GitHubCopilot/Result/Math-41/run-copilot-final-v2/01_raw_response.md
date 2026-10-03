TARGETS: Variance weighted segment: evaluate(double[],double[],int,int) with bias correction
TARGETS: evaluate with given mean, unweighted segment, and isBiasCorrected flag interactions
ORACLES: expected = unbiased weighted sample variance (segment values/weights) per test expectation
CASES: normal segment with varied positive weights; full array; zero-length; single element
CASES: segment at offset > 0; equal weights; biasCorrected true/false; provided non-zero mean
CASES: weight sum = 0? (should throw NotPositiveException); negative weights? (same)
RISKS: Weighted segment evaluate signature absent from given APIs; rely on WeightedEvaluation
contract
RISKS: Likely bug: denominator uses full array length instead of segment length when weighted
RISKS: Only one known failing test; limited coverage of edge-case weighted population variance