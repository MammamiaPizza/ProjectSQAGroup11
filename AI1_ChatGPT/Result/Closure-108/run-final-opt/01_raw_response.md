TARGETS: ScopedAliases.process, hotSwapScript, and Traversal alias discovery/usage handling.
ORACLES: Existing ScopedAliasesTest.testIssue1144; processing must not throw IllegalStateException.
CASES: Run the issue-1144 scoped-alias input through normal process traversal.
CASES: Exercise alias definitions/usages, including alias-to-alias references and type-node alias handling.
RISKS: No source body or trigger input is provided; exact transformed-output assertions are unavailable.