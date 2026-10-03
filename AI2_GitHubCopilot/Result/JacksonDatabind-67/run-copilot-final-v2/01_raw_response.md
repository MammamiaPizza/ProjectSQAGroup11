TARGETS: createKeyDeserializer(DeserializationConfig,JavaType) in BasicDeserializerFactory for enum
key types
TARGETS: Integration of custom KeyDeserializers (via DeserializerModifier) for enum map keys
TARGETS: _createEnumKeyDeserializer or equivalent method that resolves key deserializer for enums
ORACLES: Expected: custom key deserializer is used when registered; no InvalidFormatException for
variant key strings
ORACLES: Verify that a KeyDeserializer from KeyDeserializers interface handles String→Enum matching
(case-insensitive if configured)
CASES: Normal: exact enum-name key (e.g., "replacements") → deserialized correctly via custom
deserializer
CASES: Boundary: case-variant key (e.g., "REPlaceMENTS") → accepted by custom deserializer, maps to
KeyEnum.REPLACEMENTS
CASES: Error: key string not handled by custom deserializer (e.g., "unknown") → still throws
InvalidFormatException
RISKS: The exact factory method responsible for enum key deserialization is not visible; may be
createKeyDeserializer or a new internal method
RISKS: The custom key deserializer implementation in the test is unknown; assumption: it does
case-insensitive lookup via @JsonCreator or explicit mapping