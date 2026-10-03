TARGETS: ParserMinimalBase.getValueAsString, ReaderBasedJsonParser.getText,
UTF8StreamJsonParser.getText
ORACLES: For FIELD_NAME token, getValueAsString must return field name string, not null
ORACLES: Compare result of getValueAsString against known expected string from source JSON
ORACLES: For string value token, getValueAsString must equal the JSON string literal content
CASES: Parse simple object with field "a":1; at field name "a", getValueAsString returns "a"
CASES: Empty field name "", Unicode escapes, escaped quotes; getValueAsString returns exact text
CASES: After reading value token (string "hello"), getValueAsString returns "hello"
RISKS: Cannot inspect parser internals; assume JSON parsing is correct before token event
RISKS: Only parser-level methods available; no serialization/deserialization context to validate