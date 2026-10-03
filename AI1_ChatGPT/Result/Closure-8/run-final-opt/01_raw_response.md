TARGETS: CollapseVariableDeclarations.process(Node externs, Node root)
TARGETS: GatherCollapses.visit, blacklistStubVars, canBeRedeclared, applyCollapses
ORACLES: Existing CollapseVariableDeclarationsTest::testIssue820 assertion
CASES: Reproduce Issue 820 input through process and assert transformed AST/output
CASES: Variable declarations eligible for collapse versus redeclaration candidates
CASES: Stub variable declarations and variables blacklisted from collapsing
CASES: Scope-sensitive redeclaration checks, including nested scopes
RISKS: Inner implementation methods are private; test observable process output only
RISKS: No source input or expected transformed output is provided in this context