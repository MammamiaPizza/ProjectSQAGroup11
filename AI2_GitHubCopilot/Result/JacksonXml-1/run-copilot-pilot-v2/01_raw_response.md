TARGETS: FromXmlParser.nextToken() and token sequence for nested unwrapped list elements.
TARGETS: Behavior for empty child elements within unwrapped list wrapping.
ORACLES: Assert list size==1 from testNestedUnwrappedLists180 and
testNestedWithEmpty/testNestedWithEmpty2.
ORACLES: Expected-value assertions (expected:<1> but was:<0>) signal a missing list entry.
CASES: One nested list with a single empty element.
CASES: Two consecutive empty nested elements between populated items.
CASES: Empty element followed by normal element (boundary).
CASES: Empty nested list at start and end of a parent list.
RISKS: Only signatures shown; no FromXmlParser implementation to infer state transitions.