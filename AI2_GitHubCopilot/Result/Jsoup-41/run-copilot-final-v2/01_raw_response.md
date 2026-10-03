TARGETS: equals(Object), hashCode() of Element; must compare tag, attributes, children, not just
identity.
ORACLES: Elements with same tag, attributes, text nodes, child order must be equal; must return
consistent hashCode.
CASES: Two equal <p class="one">One</p> instances; different tag; different attributes; different
text; nested children.
CASES: Empty element; single child with whitespace-only text; null parent/sibling contexts; clone
equality.
RISKS: equals may incorrectly rely on ownerDocument or siblingIndex, causing identical markup to be
unequal.
ORACLES: Use existing test expectation: ElementTest.testHashAndEquals failure indicates equals
returns false for equal content.
RISKS: Unclear if equality should consider only direct text or all descendant text; assume
recursive?