TARGETS: PeepholeFoldConstants binary arithmetic folding, especially division in tryFoldArithmeticOp/performArithmeticOp.
ORACLES: Existing testFoldArithmetic expectation; compiler diagnostics must contain no JSC_DIVIDE_BY_0_ERROR.
CASES: Constant nonzero division that folds to a numeric literal without emitting a divide-by-zero diagnostic.
CASES: Division boundary where result is zero (as in trigger) and divisor is nonzero.
RISKS: Private methods require exercising optimizer through existing compiler/test harness.
RISKS: Context omits exact source expression and intended folding output beyond no error and expected zero diagnostics.