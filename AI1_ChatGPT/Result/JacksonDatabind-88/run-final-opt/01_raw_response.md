TARGETS: ClassNameIdResolver.typeFromId/_typeFromId resolving CLASS ids against base JavaType  
TARGETS: Nested generic property deserialization through ClassNameIdResolver  
ORACLES: getMechanism() returns JsonTypeInfo.Id.CLASS  
ORACLES: Trigger expects JsonMappingException text containing "not subtype of"  
CASES: Valid class-name id resolving to a subtype of the declared base type  
CASES: Nested generic id resolving to HashMap where declared type is Payload1735  
CASES: Non-subtype class-name id must fail during type-id resolution, not field assignment  
CASES: Unknown/unloadable class id follows DatabindContext unknown-type-id handling  
RISKS: Context lacks source body and concrete fixture JSON/types beyond trigger summary