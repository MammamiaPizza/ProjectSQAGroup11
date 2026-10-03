TARGETS: Core logic in process→GatherCollapses.visit identifies var nodes; canBeRedeclared gates
collapsing; applyCollapses merges. Bug likely in canBeRedeclared mis-evaluating assignments with
self-referencing closures.
ORACLES: testIssue820 expects two var statements remain separate after the pass, not merged into
one. The test’s expected output string is the oracle.
ORACLES: Expected behavior: declarations where an assignment uses a function expression that closes
over the variable’s own name must not be collapsed (preserve separate var).
CASES: Normal: two independent var statements in same scope with primitive assignments →
successfully collapsed into a single var.
CASES: Boundary: var a; var a = function() { return a; }; — the declarations must not be collapsed
because the inner reference would break.
CASES: Edge: var declarations with assignments that shadow outer variables via closure; collapsing
could cause runtime reference-before-declaration error.
CASES: Blacklisted stubs: variables marked by blacklistStubVars() should remain untouched; test with
a var declaration containing a “stub” like a property access.
RISKS: The exact input script that triggers issue 820 is unknown; the bug scenario must be
reconstructed from the class and test name only.
RISKS: The failure may depend on the order of traversal or scope analysis details not apparent from
the signatures; an incomplete reproduction is possible.