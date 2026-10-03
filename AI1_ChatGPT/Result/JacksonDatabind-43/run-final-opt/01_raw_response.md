TARGETS: ObjectIdValueProperty handling of null String object-id property during deserialization.
ORACLES: Trigger expects no JsonMappingException for VALUE_NULL String id.
CASES: Deserialize object with String property-based object id explicitly set to null.
CASES: Compare normal non-null String object-id deserialization for regression.
RISKS: Null token may be passed to a value deserializer that rejects VALUE_NULL.
RISKS: Context omits deserialize/deserializeAndSet implementations and exact JSON fixture.