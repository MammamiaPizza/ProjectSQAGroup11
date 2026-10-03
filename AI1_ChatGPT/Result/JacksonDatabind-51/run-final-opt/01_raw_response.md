TARGETS: TypeDeserializerBase._deserializeWithNativeTypeId(JsonParser, DeserializationContext, Object)
TARGETS: TypeDeserializerBase default-implementation resolution/cache behavior
ORACLES: TestCustomTypeIdResolver.testPolymorphicTypeViaCustom expected successful subtype assignment
ORACLES: Trigger failure shows options must not deserialize as LinkedHashMap instead of Poly1Base
CASES: Custom type-id resolver deserializes polymorphic value held by Object-typed property
CASES: Native type id present: resolved deserializer returns the resolver-selected subtype
CASES: Native type id absent: mapping exception path remains applicable
RISKS: Context lacks source/test fixture details and intended behavior beyond the reported trigger