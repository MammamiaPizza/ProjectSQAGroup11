TARGETS: StringCollectionDeserializer.deserialize(JsonParser, DeserializationContext)
TARGETS: Delegate/value-instantiator selection for collection-like String targets
ORACLES: DelegatingArrayCreator2324Test.testDeserializeBagOfStrings expected successful ImmutableBag creation
CASES: Deserialize JSON string array into ImmutableBag with an available delegating Creator
CASES: Verify no default-constructor requirement when a compatible Creator exists
CASES: Normal String collection array deserialization remains supported
RISKS: Context omits full deserialize branches, Creator annotations, and ImmutableBag implementation
RISKS: Expected contents/order are not specified outside the named trigger