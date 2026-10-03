TARGETS: FunctionToBlockMutator local-name uniquification and uninitialized var declaration repair.
TARGETS: RenameLabels label declarations plus break/continue label-reference renaming across scopes.
ORACLES: InlineFunctionsTest::testInlineFunctions31 assertion is the available expected-result source.
ORACLES: Compiler output/AST label bindings must remain valid after function inlining.
CASES: Inline a function containing labels and labeled break/continue statements.
CASES: Inline in a loop (isCallInLoop) with local declarations lacking initializers.
CASES: Nested labels/scopes with same label names, including references to enclosing labels.
RISKS: Label namespace stack or rename map may bind references to the wrong renamed label.
RISKS: Context provides no exact input JS or expected transformed output beyond the failing test.