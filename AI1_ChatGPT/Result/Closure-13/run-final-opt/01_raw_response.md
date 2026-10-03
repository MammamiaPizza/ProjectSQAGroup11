TARGETS: PeepholeOptimizationsPass.process and traversal/retraversal behavior over AST nodes/scopes.
ORACLES: IntegrationTest::testIssue787 is the only stated expected-result source; it currently fails.
CASES: Reproduce the Issue 787 input through the existing integration-test/compiler harness.
CASES: Exercise optimization requiring node revisit after a reported code change.
CASES: Include nested scopes/traversal state if represented by the Issue 787 regression input.
RISKS: Most relevant methods are private; test via public compiler/pass integration behavior.
RISKS: No Issue 787 source, expected output, or assertion details are provided in this context.