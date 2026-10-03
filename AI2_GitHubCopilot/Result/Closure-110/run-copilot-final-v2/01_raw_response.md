TARGETS: ScopedAliases.Traversal.validateScopeCall flags function declarations as non-alias.
ScopedAliases.Traversal.findAliases maybe misidentifies aliases. Node type checks (FUNCTION) may be
involved.
ORACLES: 0 errors expected; diagnostic JSC_GOOG_SCOPE_NON_ALIAS_LOCAL must not fire for function
declarations inside goog.scope.
CASES: Hoisted function declaration, non-hoisted function declaration, function expression (should
still error?), var assignment that is not an alias (still error).
CASES: Multiple functions, mixed with valid alias assignments, nested scopes, edge of goog.scope
boundary.
RISKS: API hides validateScopeCall implementation; TARGETS may miss other locals (class/let/const)
wrongly flagged. Test code not provided.