TARGETS: DeadAssignmentsElimination.process and traversal callbacks apply/visit across scopes and expressions.
ORACLES: Existing failing tests testInExpression2 and testIssue384b/c/d provide expected transformed output.
CASES: Dead assignments embedded in expressions; preserve expression value and side effects.
CASES: Issue 384 variants, including nested/comma/assignment expression contexts covered by named triggers.
CASES: Scope entry/exit and variable liveness across reads, writes, and control-flow boundaries.
RISKS: No source or assertions supplied; exact AST rewrites and expected JavaScript output are unavailable.
