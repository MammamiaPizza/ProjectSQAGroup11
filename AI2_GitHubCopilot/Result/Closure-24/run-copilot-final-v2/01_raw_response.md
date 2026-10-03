TARGETS: ScopedAliases.validateScopeCall, findAliases, visit, shouldTraverse, fixTypeNode
ORACLES: Compiler error reporter count; testNonAliasLocal expects 1 error, observed 0
CASES: non-alias local var declarations inside goog.scope should trigger validation error
CASES: valid alias definitions and aliased references must still compile without errors
CASES: boundary: nested scopes, aliases only at local scope, non-alias type calls
RISKS: exact error type/position not given; only public/private signatures supplied, no test source
RISKS: bug may be in missing detection rather than wrong error message; confirm via reporter count