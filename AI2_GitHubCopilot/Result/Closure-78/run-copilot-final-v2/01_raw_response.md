TARGETS: tryFoldArithmeticOp, performArithmeticOp (division-by-zero handling).
ORACLES: testFoldArithmetic asserts 0 JSC_DIVIDE_BY_0 errors; failure shows expected 0 got 1.
CASES: 1/0 (should keep error or produce Infinity, not fold to 0/1), 0/0 (NaN), -1/0 (-Infinity),
MAX_FOLD_NUMBER ops.
RISKS: May fold away runtime divide-by-zero diagnostic; floating-point precision; string/NaN
coercion in folds.