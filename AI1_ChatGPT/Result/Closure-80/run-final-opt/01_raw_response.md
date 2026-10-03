TARGETS: isBooleanResult(Node): boolean-result classification covered by testIsBooleanResult.
TARGETS: evaluatesToLocalValue(Node[, Predicate]): DELPROP must not throw in testLocalValue1.
ORACLES: Existing NodeUtilTest trigger assertions are the expected-result source.
CASES: Boolean-result expressions exercised by testIsBooleanResult, including its assertion boundaries.
CASES: Parenthesized DELPROP expression should complete local-value evaluation without IllegalStateException.
RISKS: API methods are package-private; tests require com.google.javascript.jscomp package access.
RISKS: Context omits NodeUtil implementation and complete trigger test inputs/assertions.