TARGETS: FlowSensitiveInlineVariables.process, visit, candidate definition/use analysis, and catch-expression inlining.
ORACLES: Existing trigger tests' AST/output assertions are the only stated expected-result source.
CASES: Catch expression whose value must not be inlined (testDoNotInlineCatchExpression1, 1a, 3).
CASES: Exercise distinct catch-expression forms covered by the three named regressions.
RISKS: Incorrect candidate/use CFG accounting may inline a catch-bound value across exception-sensitive flow.
RISKS: Context omits test inputs, asserted transformed output, and implementation details; derive cases from existing tests only.