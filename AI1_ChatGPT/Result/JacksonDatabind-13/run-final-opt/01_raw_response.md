TARGETS: ObjectIdValueProperty.set/setAndReturn during null object-id deserialization.  
TARGETS: DefaultDeserializationContext.findObjectId handling object-id lookup/state.  
ORACLES: Trigger test testNullObjectId must complete without JsonMappingException/NPE.  
ORACLES: Expected behavior is inferred only from the named null-object-id trigger.  
CASES: Deserialize Identifiable with a null "id" value.  
CASES: Compare non-null object-id deserialization for regression safety.  
RISKS: Internal context/property APIs need fixture setup; public behavior is mainly via ObjectMapper.  
RISKS: No exact resulting Identifiable.id value or resolver semantics are supplied.