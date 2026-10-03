TARGETS: JsonNodeDeserializer.ObjectDeserializer.deserialize handling current token for ObjectNode
TARGETS: ObjectDeserializer path after consuming START_OBJECT/fields, especially END_OBJECT token
ORACLES: testIssue941 tree equality vs expected ObjectNode; bug report expected no mapping exception
CASES: empty object "{}" producing empty ObjectNode
CASES: object with fields; END_OBJECT token reached after last field
CASES: boundary current-token END_OBJECT passed directly to ObjectDeserializer
RISKS: test body not supplied; expected tokens/output inferred only from bug 941 summary
RISKS: do not assume APIs/behavior beyond listed JsonNodeDeserializer signatures