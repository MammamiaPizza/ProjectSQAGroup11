TARGETS: NodeUtil value-conversion behavior exercised by PeepholeFoldConstantsTest.testIssue821.
ORACLES: Existing testIssue821 assertion and JavaScript constant-folding semantics encoded by NodeUtil.
CASES: Reproduce the exact expression shape from testIssue821 through Peephole constant folding.
CASES: Cover normal literal conversion plus boundary forms relevant to the failing folded expression.
RISKS: Modified-method diff and failing expression are unavailable; exact NodeUtil method is not identified.
RISKS: Do not derive expectations from another Closure version; use the buggy test/source context only.