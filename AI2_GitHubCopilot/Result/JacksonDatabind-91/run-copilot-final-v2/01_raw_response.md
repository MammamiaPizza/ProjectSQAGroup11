TARGETS: DeserializerCache key deserializer caching; _findCachedDeserializer for JavaType with
custom KeyDeserializer
TARGETS: _incompleteDeserializers sync block for key deserializer caching; _hasCustomHandlers
influence
ORACLES: Test expects second map deserialization with same custom key deserializer to use cached
instance
ORACLES: Expected map keys formatted by custom key deserializer (e.g., "1st") not default toString
CASES: Same JavaType map deserialized twice -> second call retrieves cached key deserializer
CASES: Two maps with distinct key types and distinct custom key deserializers -> cache isolation
CASES: Map with null key, empty map, single entry -> key deserializer still cached/retrieved
properly
CASES: Missing key deserializer triggers _handleUnknownKeyDeserializer and caches None
RISKS: Internal cache key resolution (type+annotations?) unknown; only API signatures available