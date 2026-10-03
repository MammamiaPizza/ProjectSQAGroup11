TARGETS: FromXmlParser.nextToken()/_updateState() transitions when nested unwrapped list child is
empty element
TARGETS: isExpectedStartArrayToken(), _mayBeLeaf, addVirtualWrapping(...) behavior for empty
unwrapped entries
TARGETS: getCurrentName()/getParsingContext()/nextTextValue() values after empty inner element
ORACLES: trigger tests assert list sizes; NestedWithEmpty/NestedWithEmpty2 expect 1 but buggy
returns 0
ORACLES: fixed behavior preserves one list item for nested unwrapped empty element (from assertions
only)
CASES: nested list with non-empty children; empty inner element yields one item; multiple empty
elements
CASES: boundary: single empty unwrapped element, trailing empty, empty document, missing end tag
RISKS: exact POJOs/XML wrappers from trigger tests not provided; use only listed test names and
assertion counts
RISKS: _mayBeLeaf and XmlReadContext internals are package-private, so assert via public
parser/data-binding output only