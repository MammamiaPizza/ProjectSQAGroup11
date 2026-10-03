TARGETS: MathUtils.distance{,1,Inf}(double[],double[]) and int[] variants
ORACLES: Validate result against mathematical formula; for null/empty/length-mismatch, expect
IllegalArgumentException, not NPE
CASES: Non-null equal-length arrays (positive, negative, zero, single element); empty arrays; null
inputs; length mismatch; large/overflow values
RISKS: Exact contract for null/empty is undocumented; must infer from callers; integer arithmetic
overflow for int distances