TARGETS: ReferenceCollectingCallback.process/visit block tracking and reference collection during traversal
TARGETS: isBlockBoundary and BasicBlock behavior for loop-related scopes/aliases
ORACLES: InlineVariablesTest.testNoInlineAliasesInLoop assertion failure is the only stated oracle
CASES: Alias/reference in a loop should be collected with correct block/scope context
CASES: Normal traversal across externs/root; references retrievable via getReferenceCollection(Var)
CASES: Boundary nodes entering/exiting loop blocks and nested scopes
RISKS: No source diff or expected assertion details are provided; avoid assuming exact collection contents