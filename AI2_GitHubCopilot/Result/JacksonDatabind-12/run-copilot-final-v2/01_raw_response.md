TARGETS: MapDeserializer.resolve(DeserializationContext) – wires _valueDeserializer;
_valueDeserializer selection logic
TARGETS: MapDeserializer.deserialize(JsonParser,DeserializationContext) – uses _valueDeserializer
for map values
ORACLES: Test expects value deserialized via custom JsonDeserializer (e.g., returns 1) vs default
(returns 100)
CASES: Map value with custom JsonDeserializer (module-registered/annotation-based); value
missing/null; empty map
CASES: JSON key collisions; nested map value; custom deserializer that throws; reference via
TypeDeserializer
RISKS: Cannot inspect TestCustomDeserializers source; unknown how custom deserializer is bound
(annotation/module)
RISKS: Actual root cause (e.g., resolve() overwrite, class loading) uncertain without test code