TARGETS: InlineVariables.doInlinesForScope, InliningBehavior.afterExitScope,
IdentifyConstants.apply, isValidReference, isValidInitialization, isVarInlineForbidden
TARGETS: Reference-based inlining decisions: blacklistVarReferencesInTree, canMoveAggressively,
isStringWorthInlining
ORACLES: Regression test testSingletonGetter1 must pass; no JS output mismatch or assertion error
after fix.
ORACLES: Variables holding singleton getter calls (with possible side effects) must not be inlined
if unsafe.
CASES: Normal: variable declared and used once, twice, in expressions; inlining saves size.
CASES: Boundary: inlining inside conditional branches, loops, with aliases, multiple references,
nested scopes.
CASES: Error: variable initialized with non-pure call (getter, method, constructor) must be blocked
from inlining.
RISKS: No fixed version available; expected correct behavior inferred only from test name and
typical inlining rules.
RISKS: Limited to buggy source; cannot compare with correct behavior or review the actual failing JS
snippet.