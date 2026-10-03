TARGETS: deserializeTypedFromObject, _deserializeTypedForId, deserializeTypedFromAny
ORACLES: Expected: no JsonMappingException on empty-string token; default impl or null per
TypeResolverBuilder config
CASES: Empty string as object value with @JsonTypeInfo(As.PROPERTY,defaultImpl=...) triggers
missing-type-property path
CASES: Normal valid type id string, missing type property, null token, whitespace-only token
CASES: Empty string for element in array/Collection where type-deser for property uses defaultImpl
RISKS: Cannot verify exact deserialized value (defaultImpl class) without ObjectMapper
integration-scope tests
RISKS: Limited to AsPropertyTypeDeserialzer; behavior under other As inclusion variants not covered