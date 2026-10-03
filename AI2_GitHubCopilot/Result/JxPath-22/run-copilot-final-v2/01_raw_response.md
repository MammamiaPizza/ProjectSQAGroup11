TARGETS: DOMNodePointer.asPath(), getRelativePositionByQName(), matchesQName(Node)
TARGETS: testNode(NodeTest) path-building for element with empty/default namespace
ORACLES: JXPath154Test expected path "/b:foo[1]/[test[1]]" vs actual "/b:foo[1]/[node()[2]]"
ORACLES: JUnit ComparisonFailure message is only supplied expected-value source
CASES: child test node after namespaced parent, no inner namespace binding
CASES: sibling text node before target causes "node()[2]" instead of positional "test[1]"
CASES: boundary first/last child, repeated same QName, absent namespace prefix
RISKS: no source diff or full test body; trigger failure string only
RISKS: do not assume fixed implementation or extra DOM/namespace APIs