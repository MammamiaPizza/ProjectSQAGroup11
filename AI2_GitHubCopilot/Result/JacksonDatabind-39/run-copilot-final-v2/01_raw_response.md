TARGETS: NullifyingDeserializer.deserialize(JsonParser, DeserializationContext)
ORACLES: Returns null; parser advanced past the complete JSON value tree.
ORACLES: No UnrecognizedPropertyException thrown for any skipped fields.
CASES: Simple object with unknown properties → must skip all children.
CASES: Empty object, empty array → just consume tokens, return null.
CASES: Nested object/array structures → skip recursively, return null.
CASES: Primitive tokens (number, string, boolean, null) → consume, return null.
CASES: START_ARRAY with mixed elements → skip entire array, return null.
RISKS: DeserializationContext interaction may be minimal; need TypeIdResolver setup.
RISKS: Behavior of skipChildren() vs manual traversal not observable directly.