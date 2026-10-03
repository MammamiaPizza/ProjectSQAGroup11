TARGETS: CollectionDeserializer.deserialize(JsonParser, DeserializationContext) for collection construction  
ORACLES: Trigger expects deserialization of an unmodifiable collection without missing-default-constructor failure  
CASES: UnmodifiableSet target using array/delegating creator path from ArrayDelegatorCreatorForCollectionTest  
CASES: Verify resulting collection contents/type constraints supplied by the trigger test  
RISKS: Failure is IllegalStateException when no default constructor exists for Collections$UnmodifiableSet  
RISKS: Context lacks JSON input, creator annotations, and exact expected collection assertions