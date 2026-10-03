TARGETS: PeepholeSubstituteAlternateSyntax.apply and optimizeSubtree transformation behavior.
TARGETS: Exercise paths reachable by PeepholeSubstituteAlternateSyntaxTest.testIssue291.
ORACLES: Use the existing testIssue291 assertion as the expected-result source.
CASES: Reproduce the issue-291 input through the peephole optimization test harness.
CASES: Add nearby syntax variants only when their expected optimized output is established by the harness.
RISKS: No source diff, failing input, assertion text, or expected output is provided.
RISKS: Private helper behavior cannot be specified independently from visible optimization results.