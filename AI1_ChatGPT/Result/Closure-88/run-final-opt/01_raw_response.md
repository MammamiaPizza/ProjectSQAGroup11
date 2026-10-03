TARGETS: DeadAssignmentsElimination.process, scope entry/exit, post-order visit, and assignment liveness decisions.
ORACLES: Existing Issue297 trigger tests and their compiler-output assertions are the expected-result source.
CASES: Issue297a–f inputs covering dead-assignment elimination in the existing unit-test fixtures.
CASES: CommandLineRunnerTest.testIssue297 for end-to-end compiler integration behavior.
RISKS: Assignment removal must preserve observable effects and scope-sensitive variable liveness.
RISKS: No trigger source, expected transformed JavaScript, or assertion details are provided.