TARGETS: NodeUtil.mayHaveSideEffects for HOOK/OR nodes; PureFunctionIdentifier.markPureFunctionCalls
callee traversal.
ORACLES: Failing tests: testCanExposeExpression2/7 expect MOVABLE for pure OR/hook; PureFunction
tests expect detected calls for (f||g)(h).
CASES: Normal: (pure||pure)(arg), (pure?t:f)() detected as pure; Boundary: nested OR/hook; mixed
side-effect operands ⇒ not pure.
RISKS: mayHaveSideEffects change impacts inlining, code removal, expression decomposition;
regression risk for impure OR/hook expressions.