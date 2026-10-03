TARGETS: process(Token) – handling deeply nested start/end tags without stack overflow.
TARGETS: insertNode(Node) – managing formattingElements and stack under deep nesting.
TARGETS: clearStackToContext(String...) – correct context clearing after many levels.
ORACLES: No StackOverflowError; parsed Document should have expected nesting count.
ORACLES: Parent/child relationships and sibling order preserved after deep parsing.
CASES: Normal deep nesting (e.g. 500 <div>) succeeds; boundary near old recursion limit.
CASES: Extreme depth (10k+ nested elements) to validate iterative fallback and performance.
CASES: Deeply nested malformed HTML (unclosed tags) triggers error recovery; no crash.
RISKS: Exact old stack limit unknown; must deduce from test name “handlesDeepStack”.
RISKS: AssertionFailedError does not show whether expectation is no-error or exact DOM.