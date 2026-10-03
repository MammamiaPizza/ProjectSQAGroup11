TARGETS: process(Node,Node) & hotSwapScript for incremental updates.
TARGETS: Traversal.visit, validateScopeCall, fixTypeNode; AliasUsage.applyAlias.
ORACLES: Process does not throw IllegalStateException; scopedAliasNames consistent; AST no errors.
CASES: Normal aliases, aliased type nodes, scoped calls; edge: no aliases, dups, shadow $jscomp,
nested scopes.
RISKS: Missing input JS; preprocessorSymbolTable may be null; stateful hotSwapScript; order
sensitivity.