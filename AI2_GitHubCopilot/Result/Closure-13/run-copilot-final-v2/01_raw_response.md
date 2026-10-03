TARGETS: shouldRetraverse(Node) — determines if node needs re-optimization after change
TARGETS: reportChange() in PeepholeChangeHandler — triggers retraversal flag
TARGETS: traverse(Node) — recursive descent with change-aware re-entry logic
TARGETS: beginTraversal()/endTraversal() — state-stack lifecycle across passes
ORACLES: After process() completes, repeated process() calls must not alter AST further
ORACLES: IntegrationTest::testIssue787 output must match expected optimized JS
CASES: Change in child creates new optimization opportunity in parent (cascading)
CASES: Multiple successive changes on same node requiring repeated retraversal
CASES: Deeply nested scope changes; state stack must reflect correct scope on re-entry
RISKS: Only testIssue787 provides known failing input; other retraversal gaps may exist