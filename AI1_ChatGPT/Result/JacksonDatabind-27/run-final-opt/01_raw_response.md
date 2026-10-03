TARGETS: BeanDeserializer.deserializeUsingPropertyBasedWithExternalTypeId(JsonParser, DeserializationContext)
TARGETS: External type-id handling when a creator property participates in deserialization
ORACLES: Trigger test TestExternalId.testInverseExternalId928 is the expected-result source
ORACLES: Deserialization must not throw IllegalStateException about missing fallback setter/field
CASES: Inverse external type-id input requiring property-based/creator construction
CASES: Creator property resolved before and after its external type-id token
CASES: Normal external type-id deserialization path remains successful
RISKS: CreatorProperty cannot be used as a fallback setter/field
RISKS: Context lacks JSON fixture, target bean shape, and expected deserialized values