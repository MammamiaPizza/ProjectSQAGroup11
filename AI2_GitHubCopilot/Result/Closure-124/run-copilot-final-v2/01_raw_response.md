TARGETS: isCollapsibleValue (decides value exploitability; may misclassify qualified/property
expressions)
TARGETS: isSafeReplacement (safety check; may miss unsafe substitution when name is reassigned
later)
TARGETS: isNameAssignedTo (checks reassignment; may not detect assignments inside
loops/conditionals)
ORACLES: testIssue1017 likely compares expected AST (from issue 1017) with actual transformed code
ORACLES: Expected result may come from hardcoded string constant in test or comments (not supplied)
CASES: Normal: var a = x; use(a) → should collapse if x is pure
CASES: Boundary: var a = b.c; where b.c is getter or later mutated → must not collapse
CASES: Boundary: name assigned inside a nested function/closure but local variable used outer
CASES: Error: name assigned after use in same block but before second use, collapse is unsafe
RISKS: Only partial signatures; no full source or test input; bug could be missing AST node type