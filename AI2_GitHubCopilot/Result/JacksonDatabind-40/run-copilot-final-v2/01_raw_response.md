TARGETS: BooleanDeser,ByteDeser,ShortDeser,IntDeser,LongDeser,FloatDeser,DoubleDeser,CharDeser
deserialize(empty string)
ORACLES: Expect JsonMappingException when empty string maps to primitive (test fails with "Should
not have passed")
ORACLES: Wrapper deserializers should still coerce empty string to null (unchanged)
CASES: Empty string "" for each primitive type; whitespace-only " "; null token; valid number
strings
CASES: Boundary values: 0, false, '\0', MIN_VALUE, MAX_VALUE; empty input for wrapper types
CASES: Zero-length string for char; valid integer string for float; negative numerics
RISKS: PrimitiveOrWrapperDeserializer.getNullValue may mask fix; must not break wrapper empty->null
path
RISKS: Fix must cover all eight primitive deserializers; missing any will leave test failure
RISKS: Backward compat: ensure ACCEPT_EMPTY_STRAND_AS_NLLL_OBJCI feature doesn't interfere
RISKS: Interaction with DeserializationContext and custom converters could bypass check
RISKS: Must handle empty string before other coercion (e.g., leading whitespace); proper error
message