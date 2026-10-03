TARGETS: UntypedObjectDeserializer.deserialize and mapObject handling nested untyped JSON objects.
TARGETS: mapArray/mapArrayToArray behavior when untyped values occur inside objects or arrays.
ORACLES: Trigger testNestedUntyped989 must deserialize without JsonMappingException at END_OBJECT.
ORACLES: Use resulting Java Object graph types/contents asserted by the existing trigger specification.
CASES: Nested object whose inner object closes with END_OBJECT while deserializing Object-typed values.
CASES: Nested objects combined with arrays, strings, numbers, null, and empty object/array boundaries.
CASES: Top-level object and array deserialization through Object target type.
RISKS: Expected exact graph/content is not provided; derive assertions only from available trigger test/source.