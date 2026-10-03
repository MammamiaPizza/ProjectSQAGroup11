TARGETS: com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser.nextTextValue()
ORACLES: nextTextValue() must return the text "7" for an XML element with attributes and text
content
ORACLES: expected string matches the XML text child, not the attribute values
CASES: XML element with multiple attributes followed by a text child; verify nextTextValue() after
START_ELEMENT
CASES: element with attributes but no text child (nextTextValue() returns null or empty)
CASES: element with attributes and empty text (""); element with text but no attributes
CASES: nested elements where inner has attributes+text; verify after inner element end
RISKS: token-state transition after attribute-only events may not advance to text token; bug likely
in _updateState or token-advance logic
RISKS: implementation details hidden; _xmlTokens and nextToken() internals unknown; context limited
to provided API