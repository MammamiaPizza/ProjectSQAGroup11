TARGETS: NormalizeStatements.shouldTraverse, splitVarDeclarations, moveNamedFunctions,
removeDuplicateDeclarations
ORACLES: No duplicate var decls after normalization; function decls hoisted correctly; expected AST
structure per NormalizeTest assertions
CASES: Normal/boundary: single var decl, multi-var decl, nested functions; error: duplicate var
names; nested scopes with same-name vars
RISKS: Exact expected AST/error counts not fully specified; normalization may interact with other
passes; internal traversal order sensitive