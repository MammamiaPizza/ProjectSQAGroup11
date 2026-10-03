TARGETS: TokenBuffer.asParser() token positioning;
writeStartObject/writeFieldName/writeEndObject/writeString sequencing
ORACLES: asParser().nextToken() sequence must match the exact order of written JSON tokens
ORACLES: asParser() after empty object must yield START_OBJECT then END_OBJECT immediately
CASES: write empty object, parse: nextToken()→START_OBJECT, END_OBJECT (no FIELD_NAME)
CASES: write object with 1 key-value, parse: START_OBJECT, FIELD_NAME, VALUE, END_OBJECT
CASES: write nested objects and arrays, parse to verify correct nesting and token order
CASES: append two TokenBuffers and parse the merged token stream for correct sequence
CASES: verify asParser() on closed buffer throws or returns null; test close() state
RISKS: API list truncated; internal write/parse helpers omitted; cannot inspect implementation
RISKS: bug likely involves asParser() not rewinding after writeFieldName; test may miss native-id
edge cases