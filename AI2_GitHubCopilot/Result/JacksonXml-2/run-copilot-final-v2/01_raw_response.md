TARGETS: next() token XML_TEXT for text, getText() for character data, _collectUntilTag(),
_skipUntilTag(), _initStartElement(), repeatStartElement()
ORACLES: testMixedContent expects getText()="27", token sequence
XML_START_ELEMENT→XML_TEXT→XML_END_ELEMENT, text equals concatenated CDATA between tags
CASES: Mixed content: "27" text, whitespace-only text, text split across multiple CHARACTERS events,
empty text, attributes then text, nested elements with text
RISKS: Unknown test input XML; _collectUntilTag() internals not visible; rely on constants; must
infer from token types XML_TEXT=5; no spec for mixed content ordering