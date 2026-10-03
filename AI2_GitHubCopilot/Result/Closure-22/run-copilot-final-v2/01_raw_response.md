TARGETS: CheckSideEffects.visit() node-classification logic for side-effect-free code.
TARGETS: protectSideEffects() whitelist of safe nodes (e.g., assignments, calls).
TARGETS: StripProtection pass that removes protection from previously safe nodes.
ORACLES: Count of warnings emitted; test checks WarningCollector size equals expected.
ORACLES: DiagnosticGroup used; "CheckSideEffects" diagnostic type expectations.
CASES: Pure expression statements (e.g., x + 1;) → should trigger warning.
CASES: Expressions with side effects (assignment, call) → should not trigger warning.
CASES: Extern-protected statements; comma expressions with mixed side effects.
CASES: Boundary: empty statement, single identifier expression, nested blocks with no side effects.
RISKS: Exact buggy code pattern unknown; StripProtection may interfere with extern handling.