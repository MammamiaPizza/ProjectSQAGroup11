TARGETS: Element.siblingElements(), elementSiblingIndex(), indexInList(),
next/previous/first/lastElementSibling.
ORACLES: ElementTest::testElementSiblingIndexSameContent and testGetSiblingsWithDuplicateContent
assertions; position-based, not content-based.
CASES: duplicate-content siblings; same tag+text; first/middle/last index; no parent; only child;
mixed distinct and duplicate siblings.
RISKS: Only failing test names and truncated API given; fix intent inferred, no post-fix source to
confirm exact expected values.