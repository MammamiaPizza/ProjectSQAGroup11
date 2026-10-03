TARGETS: TokenBuffer.asParser(JsonParser), asParser(), and parser token positioning/nextToken traversal  
ORACLES: Trigger expects START_OBJECT before FIELD_NAME for delegated TokenBuffer parsing  
CASES: Buffer writeStartObject/writeFieldName/writeEndObject; parser firstToken and successive nextToken order  
CASES: Parse nested object/array buffers and verify structural start/end tokens are preserved  
CASES: Construct parser with source JsonParser and verify initial token is not advanced to FIELD_NAME  
RISKS: Parser initial-current-token semantics may differ between asParser overloads  
RISKS: Context lacks modified diff and full trigger source; limit assertions to exposed token behavior