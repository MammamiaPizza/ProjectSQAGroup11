TARGETS: IR factory methods implicated by IRTest.testIssue727_1/_2/_3; inspect their Node validation behavior.
ORACLES: Existing IRTest issue-727 assertions and Node structural/token invariants are the expected-result source.
CASES: Reproduce each issue-727 input shape; assert construction succeeds and produced tree has expected children.
CASES: Cover relevant empty/null/optional-child boundary forms only where the triggered tests exercise them.
RISKS: Trigger evidence only reports IllegalStateException; exact faulty factory method/input is not provided.
RISKS: No fixed-version comparison or inferred API contract is available; derive expectations solely from current tests/source.