TARGETS: deserialize plus helpers _deserializeCustom, handleNonArray, deserializeWithType,
createContextual.
TARGETS: Trigger testStringArray primarily exercises deserialize producing String[] from JSON array
input.
ORACLES: TestArrayDeserialization::testStringArray assertions are the oracle; exact expected arrays
not supplied here.
CASES: normal JSON string array, empty array, single element.
CASES: many elements to force ObjectBuffer growth; non-array scalar via handleNonArray; null
token/array boundaries.
CASES: custom contextual element-deserializer path via createContextual/context property.
RISKS: NPE may stem from null _elementDeserializer or buffer/chunk completion on empty/malformed
arrays.
RISKS: static instance has mutable _elementDeserializer risking cross-test leakage; only signatures
provided, not method bodies.