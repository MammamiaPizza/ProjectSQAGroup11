TARGETS: ExternalTypeHandler.complete(JsonParser, DeserializationContext, Object) external type-id handling  
ORACLES: Existing trigger TestExternalId.testExternalTypeIdWithNull expected to deserialize ValueBean  
CASES: External type id is JSON null; value property follows or precedes its type-id property  
CASES: Normal non-null external type id with matching value property  
CASES: Missing external type id/value and scalar VALUE_STRING input error handling  
RISKS: Context omits ValueBean fields, JSON fixtures, and intended null-type-id semantics beyond trigger failure