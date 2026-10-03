: a block of lines, each starting with label colon spaceTARGETS: NodeUtil.isBooleanResult for
DELETE, VOID, TYPEOF, relational, logical nodes.
TARGETS: NodeUtil.evaluatesToLocalValue handling of DELPROP, COMMA, ASSIGN, HOOK nodes.
ORACLES: Current failed test expectations in NodeUtilTest; ECMAScript spec for boolean/delete.
CASES: Normal boolean expressions: ==, !=, ===, !==, >, <, >=, <=, &&, ||, !.
CASES: Boundary: DELETE property (DELPROP) in evaluatesToLocalValue; combined with other ops.
CASES: Error: DELPROP unhandled causing IllegalStateException; fix should prevent crash.
RISKS: Full contract for isBooleanResult (e.g., should DELETE be boolean?) unclear from test
summaries.
RISKS: evaluatesToLocalValue expected result for DELPROP not explicitly specified; infer from test.
That's 8 lines. I could add more CASES: For isBooleanResult, test with non-boolean nodes like number
literals.
Let's output now.