TARGETS: FlowSensitiveInlineVariables.process, visit, candidate analysis, and variable inlining behavior.
ORACLES: Existing trigger test FlowSensitiveInlineVariablesTest::testVarAssinInsideHookIssue965.
CASES: Variable assignment inside hook/conditional expression, ensuring safe flow-sensitive use handling.
CASES: Normal single-definition/single-use variable candidates versus candidates with later assignments.
CASES: Boundary control-flow joins and branch-local definitions/uses around hook expressions.
RISKS: Incorrect inlining when assignment expression appears within a conditional hook branch.
RISKS: Private Candidate/GatherCandiates methods require exercising behavior through process/compiler tests.
RISKS: Context lacks source implementation and explicit expected transformed JavaScript beyond trigger failure.