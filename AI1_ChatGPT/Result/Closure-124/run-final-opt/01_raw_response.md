TARGETS: ExploitAssigns peephole optimization, especially assignment exploitation in testIssue1017.
TARGETS: isCollapsibleValue(Node, boolean), isSafeReplacement(Node, Node), isNameAssignedTo(String, Node).
ORACLES: Existing ExploitAssignsTest::testIssue1017 assertions are the available expected-result source.
CASES: Reproduce the Issue 1017 JavaScript input through the optimizer test harness.
CASES: Cover safe versus unsafe replacements where a name may be assigned within the relevant node.
CASES: Cover collapsible values in both l-value and non-l-value contexts.
RISKS: Private helpers require indirect testing via optimization output, not direct invocation.
RISKS: No failing input, expected transformed output, or surrounding AST context is provided.