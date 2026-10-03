TARGETS: FromXmlParser.nextToken() - verify token sequence for XML attributes (FIELD_NAME,
VALUE_STRING).
TARGETS: FromXmlParser.nextTextValue() - must return attribute value text for VALUE_STRING token.
TARGETS: FromXmlParser.getText() and getCurrentName() - return attribute value/name strings.
ORACLES: Compare nextTextValue() with getText() when current token is VALUE_STRING from attribute.
ORACLES: Use specific XML input with known attribute values, e.g., <root a='7'/>, expect
nextTextValue() returns "7".
CASES: Normal: numeric attribute "7"; boundary: empty attribute ""; attribute with leading/trailing
spaces.
CASES: Attribute after attribute name token (FIELD_NAME) - nextTextValue() returns null or value;
test to clarify.
CASES: Call nextTextValue() when current token is START_ELEMENT - expected null or exception.
RISKS: Token types for attribute name/value (FIELD_NAME/VALUE_STRG) not explicitly documented; rely
on observed behavior.
RISKS: Limited context on how _currText is populated; may need to test intermediate state.