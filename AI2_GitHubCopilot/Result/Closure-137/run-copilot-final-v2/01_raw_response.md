TARGETS: MakeDeclaredNamesUnique.ContextualRenameInverter (getOrginalName inversion);
ContextualRenamer.addDeclaredName / getReplacementName for 'arguments'; NodeUtil.isConstantName;
Normalize.removeDupliateDeclarations (DuplicateDecarationHandler).
ORACLES: testOnlyInversion3/4 expect inverted names round-trip to original; testArguments expects
'arguments' not renamed; testMakeLoclNamesUniqueWithContex1 expects unique local names per scope;
testRemoveDupliateVarDeclarations2 expects no duplicate var stmts.
CASES: 'arguments' identifier renaming; duplicate var declarations after normalization in same
scope; conditional scopes with local rename; constant-marked names affecting inverson.
RISKS: Exact bug unknown; test expected values must be inferred from test method bodies (not
provided); interaction between Normalize duplicate removal and MakeDeclaredNamesUnique rename may
cause failures.