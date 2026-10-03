TARGETS: complete() handling of VALUE_NULL token; Builder.addExternal indexing
TARGETS: ExtTypedProperty.getDefaultTypeId() fallback for null/absent type
ORACLES: Jackson external type spec: null → default type; TestExternalId usage
ORACLES: testExternalTypeIdWithNull expects deserialization without exception
CASES: null type property value → no JsonMappingException; empty string; property missing; valid
string id
RISKS: impacts all @JsonTypeInfo(include=As.EXTERNAL_PROPERTY) deserialization; verify polymorphic
bean creation still works