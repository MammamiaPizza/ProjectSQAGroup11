TARGETS: process, findAliases, renameNamespaceShadows, validateScopeCall, fixTypeNode,
AliasUsage.applyAlias
ORACLES: IntegrationTest expects no warnings/errors; ScopedAliasesTest expects null (AST equality)
for issue772 input
CASES: goog.scope with aliases, type aliases, nested scopes, shadowed names, empty alias block,
multiple scope calls, type-node-rewrite cases
RISKS: exact issue772 input unknown; ambiguity which alias/shadow triggers; limited API surface; may
miss new alias syntax cases