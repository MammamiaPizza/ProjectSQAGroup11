TARGETS: tryRemoveUnconditionalBranching, EliminationPass.visit, computeFollowing, removeNode; must
detect try-finally context before removing branch.
ORACLES: Tests (testIssue4177428*, testDontRemoveBreakInTryFinally*) expect break/continue/return
retained inside try-finally; expected code strings in test assertions.
CASES: try-finally with break/continue/return; try-finally-switch; nested try-finally; loops
containing break/continue; unconditional return in finally; empty try block.
RISKS: Exact expected output not provided; cannot inspect test body; potential interplay with other
passes or ControlFlowGraph changes; limited to given API signatures.