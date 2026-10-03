TARGETS: process() minimizes exit points; visit() handles each node; matchingExitNode() checks label
match.
ORACLES: break in try-finally must NOT be removed (testDontRemoveBreakInTryFinally); function return
optimization must be correct.
CASES: break inside try-finally (must keep); break in try-catch; labeled break; return in nested
functions; early return combined with finally; multiple exits.
RISKS: only process/visit/matchingExitNode visible; exact bug patch unknown; AST traversal context
limited; label matching subtle.