TARGETS: FromXmlParser.nextTextValue(); also nextToken()/getText() interactions.
ORACLES: Test assert: nextTextValue() returns "7" after attr token. Spec: should return element text
content.
CASES: Normal: element with 1 attr+text; multiple attrs; empty text; no attr (regression); boundary:
large text, CDATA, special chars.
RISKS: _mayBeLeaf/_currText state after attribute parsing; Feature flags; call order of
nextToken/nextTextValue.