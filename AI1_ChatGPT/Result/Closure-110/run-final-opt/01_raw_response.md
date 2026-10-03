TARGETS: ScopedAliases Traversal validation of locals in goog.scope, especially function declarations named f.
ORACLES: Existing trigger expectation: no JSC_GOOG_SCOPE_NON_ALIAS_LOCAL diagnostic for both declarations.
CASES: goog.scope containing a hoisted function declaration f before/after executable statements.
CASES: goog.scope containing a non-hoisted function declaration f should also be accepted.
CASES: Preserve validation of actual non-alias local variables, if accessible via existing tests.
RISKS: Node changes are unspecified; avoid direct Node API assertions without source context.
RISKS: Available context omits constructors/compiler setup and exact goog.scope test harness APIs.