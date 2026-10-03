TARGETS: JsonMappingException._buildMessage / prependPath and message construction chain
TARGETS: DeserializationContext.weirdKeyException / handleWeirdKey adding location markers
TARGETS: StdKeyDeserializer.deserializeKey and its error-reporting logic
ORACLES: Exception.getMessage() must contain exactly one "at [" substring per error chain
ORACLES: Trigger test assert: count of "at [" in message == 1 (not 2)
CASES: Map key deserialization fails with invalid Enum value or bad String representation
CASES: Nested deserialization where inner key failure could cause duplicate path prepend
CASES: Boundary: empty error message, deeply wrapped exception, multiple context levels
RISKS: Full method signatures partially truncated; exact message-prepend code may differ across
Jackson versions
RISKS: Bug may be specific to StdKeyDeserializer + Map type; must test through full deserialization
pipeline