TARGETS: Constructor OpenMapRealMatrix(int row, int col), createMatrix, multiply(RealMatrix);
validate dimension overflow.
ORACLES: NumberIsTooLargeException expected when row*col overflows int or exceeds internal limit;
NotStrictlyPositiveException for <=0.
CASES: (46341,46341) → NumberIsTooLargeException; (0,5) → NotStrictlyPositiveException; normal
(3,4); boundary (1,Integer.MAX_VALUE).
RISKS: Overflow threshold may depend on OpenIntToDoubleHashMap capacity; no visibility into internal
computeKey overflow checks.