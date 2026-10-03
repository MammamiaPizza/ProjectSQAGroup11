TARGETS: InlineObjectLiterals.process; inlining eligibility, initial-value collection, stale-variable handling.  
TARGETS: InliningBehavior.afterExitScope, isInlinableObject, fillInitialValues, blacklistVarReferencesInTree.  
ORACLES: Existing failing InlineObjectLiteralsTest testObject10/12/22 and testIssue724 assertions.  
ORACLES: IntegrationTest.testIssue724 provides end-to-end compiler-output behavior.  
CASES: Object literals with property reads/writes and declarations versus assignment-expression initialization.  
CASES: Issue724 regression input; verify optimized output matches existing test expectations.  
CASES: Multiple references/aliases and references in nested trees/scopes that must be blacklisted.  
RISKS: Private helpers require exercising through process/compiler test harness, not direct API calls.  
RISKS: No source bodies or assertion expected strings supplied; derive expected results only from existing tests.