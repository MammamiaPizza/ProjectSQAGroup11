TARGETS: AttributeContext.setPosition() must advance to matching attributes in the parent's
attribute iterator
ORACLES: Expected values "10%", "20%" from DOM/JDOM documents with amount attributes; test asserts
iterator returns both
CASES: Single attribute node, multiple attributes, no matching attributes, boundary at last
position, reset then nextNode
RISKS: Limited to supplied API signatures; cannot inspect parent context's attribute iteration logic
or NodeTest matching behavior