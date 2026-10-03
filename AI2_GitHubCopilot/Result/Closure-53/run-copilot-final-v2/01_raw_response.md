TARGETS: process() triggers inlining; InliningBehavior.afterExitScope; isInlinableObject() validates
references
TARGETS: fillInitialValues() populates property map; blacklistVarReferencesInTree avoids forbidden
inlining
ORACLES: testBug545 expects no RuntimeException (INTERNAL COMPILER ERROR) and successful compilation
CASES: Object literal assigned to var, used in multiple scopes, nested properties, boundary: empty
object
CASES: Patterns that cause var to be blacklisted then inlining attempt leads to crash (bug 545
trigger)
CASES: Error case: input provoking internal error; normal: inlining without crash
RISKS: No access to buggy source; exact crash condition unknown; only post-fix behavior oracle
available