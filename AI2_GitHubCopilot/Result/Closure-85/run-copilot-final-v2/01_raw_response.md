TARGETS: UnreachableCodeElimination.process, tryRemoveUnconditionalBranching, computeFollowing,
removeDeadExprStatementSafely
ORACLES: No Internal Compiler Error (testIssue311); cascaded removal does not over-eliminate
reachable code; expected AST from trigger tests
CASES: Single unconditional jump removal; cascaded removal of subsequent unreachable block; nested
scopes; try-finally; original Issue311 input
RISKS: Bug details unknown—must rely on trigger tests for regression; other edge cases in
tryRemoveUnconditionalBranching may exist