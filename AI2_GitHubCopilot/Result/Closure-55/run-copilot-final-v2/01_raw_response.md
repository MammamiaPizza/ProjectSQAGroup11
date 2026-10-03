TARGETS: isReduceableFunctionExpression must reject non-function nodes (CALL, GETPROP, NEW, etc.).
TARGETS: ReductionGatherer.visit must safely skip non-reduceable function expressions.
ORACLES: testIssue538 expects FunctionRewriter to handle call reference without
IllegalStateException.
ORACLES: Helper source transformation should not crash on unmatched reducers.
CASES: test a CALL reference node triggering bug; test valid FUNCTION node reduction; empty,
identity, getter, setter cases.
RISKS: Only CALL fixed; GETPROP or NEW may still throw. No contract on error handling in reducers.