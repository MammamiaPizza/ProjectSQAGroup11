TARGETS StringArrayDeserializer.deserialize array token loop and exception wrapping/index reporting.
TARGETS _deserializeCustom and handleNonArray paths when contextual String deserializer or non-array input applies.
ORACLES JsonMappingException path/reference index from TestCollectionDeserialization.testArrayIndexForExceptions.
CASES String array with failure at second element; reported array index must be 1, not 0.
CASES Normal multi-element String array preserves ordered values and successful completion.
CASES Boundary failure at first element reports index 0; later-element failure validates advancing index.
RISKS Context lacks source/expected exception message and custom-deserializer setup details.