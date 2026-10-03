TARGETS: BeanDeserializer.deserialize and _deserializeUsingPropertyBased for creator/object-id deserialization.
ORACLES: ObjectWithCreator1261Test::testObjectIds1261 should complete without the reported JsonMappingException.
CASES: Object-id reference resolving to a Child created through creator/property-based deserialization.
CASES: Input path encountering START_ARRAY during the trigger’s Child deserialization.
RISKS: Assert deserialization success and resulting object relationships only where exposed by the trigger test.
RISKS: No source diff or trigger JSON/expected field values provided; avoid inferring token handling beyond failure.