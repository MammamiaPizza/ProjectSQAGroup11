TARGETS: Normalize.removeDuplicateDeclarations handles duplicate var in externs and local rename
TARGETS: SyntacticScopeCreator scans var/name declarations and redeclaration reporting
ORACLES: NormalizeTest::testDuplicateVarInExterns and testMakeLocalNamesUnique expected pass
ORACLES: expected AST shape after normalizing declarations (no valid API here to assert result)
CASES: duplicate var in externs keeping single declaration, dropping dup
CASES: local names colliding with globals/externs require renaming to unique identifiers
CASES: boundary: empty externs/root, nested scopes, function params vs local vars
CASES: error: redeclaration in block/function scope, name collision after rename
RISKS: two modified classes interact; scope creation must precede normalize pass
RISKS: limited API list; cannot verify exact rename suffix/constant propagation behavior