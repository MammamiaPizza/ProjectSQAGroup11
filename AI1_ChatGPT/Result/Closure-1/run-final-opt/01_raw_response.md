TARGETS: RemoveUnusedVars.process and unused global/local/function-argument removal behavior.
TARGETS: CallSiteOptimizer.optimize/applyChanges and assignment/reference tracking affect safe signature changes.
ORACLES: Existing trigger assertions are the only stated expected-result source.
CASES: Unused globals: testRemoveGlobal1/2/3; issue-specific retention/removal: testIssue168b, testIssue787.
CASES: Simple mode preserves unused parameters; forward declarations drop types; debug flag output behavior.
CASES: Function calls with removable arguments, including trailing arguments and side-effect-sensitive call sites.
RISKS: Internal/package-private class and private helpers limit direct unit tests to compiler/integration harnesses.
RISKS: No AST/output expectations are provided here; derive exact assertions only from existing buggy-version tests.