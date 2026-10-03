TARGETS: TypeDeserializerBase._deserializeWithNativeTypeId and its property-based type-id resolution
path
TARGETS: TypeDeserializerBase._findDefaultImplDeserializer and default-implementation fallback
selection
TARGETS: TypeDeserializerBase constructor that sets _typePropertyName, _idResolver, _typeIdVisible
ORACLES: Verify correct sub-type created for nested polymorphic property when type-id is present
ORACLES: Verify that known type-id uses registered deserializer, unknown falls back to default impl
CASES: Nested polymorphic property with a custom TypeIdResolver using a JSON property to carry
type-id
CASES: Type-id property missing — expect mapping exception or default impl fallback
CASES: _typeIdVisible=true causes type-id property to remain in POJO; verify deserialization still
works
RISKS: Abstract class — requires concrete subclass instantiation or heavy mocking of
DeserializationContext/JsonParser
RISKS: Bug is integration-level; reproducing exact failure may need full Jackson pipeline with
ObjectMapper