TARGETS: FromStringDeserializer.deserialize(JsonParser, DeserializationContext)
TARGETS: FromStringDeserializer._deserialize(String, DeserializationContext)
TARGETS: FromStringDeserializer.Std._deserialize(String, DeserializationContext)
TARGETS: _deserializeFromEmptyString() for empty-input handling
ORACLES: Invalid UUID string must invoke ProblemHandler, not throw uncaught InvalidFormatException
ORACLES: Expected behavior from ProblemHandlerTest.testWeirdStringHandling: no uncaught exceptions
CASES: Valid UUID "550e8400-e29b-41d4-a716-446655440000" → correct UUID object
CASES: Invalid UUID "not a uuid!" → ProblemHandler intercepts, no InvalidFormatException
CASES: Empty string "" → _deserializeFromEmptyString returns null or empty value
RISKS: UUIDDeserializer subclass not shown; limited to FromStringDeserializer modification context