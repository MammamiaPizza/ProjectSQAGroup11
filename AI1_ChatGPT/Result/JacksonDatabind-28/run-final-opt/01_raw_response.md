TARGETS: JsonNodeDeserializer.deserialize; ObjectDeserializer.deserialize; ArrayDeserializer.deserialize.
ORACLES: Trigger TestObjectNode.testIssue941 and absence of END_OBJECT ObjectNode mapping failure.
CASES: Deserialize an object token sequence into ObjectNode, including an empty object ending at END_OBJECT.
CASES: Deserialize nested object/array values through generic JsonNode deserialization.
CASES: Non-object input to ObjectDeserializer and non-array input to ArrayDeserializer should map-fail.
RISKS: Parser current-token/next-token handling near END_OBJECT is the likely regression point.
RISKS: Trigger body and intended assertions are not provided; limit oracle to reported failure.