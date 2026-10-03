TARGETS: ExternalTypeHandler.complete(JsonParser, DeserializationContext, Object) external type-id completion  
TARGETS: Builder.addExternal/build and ExtTypedProperty type-property linking/name lookup  
ORACLES: Existing trigger ExternalTypeIdWithEnum1328Test::testExample must deserialize without argument type mismatch  
ORACLES: Expected behavior is constrained to successful binding of external type-id data for AnimalAndType  
CASES: External type id represented by enum-associated property, with value and type id available at completion  
CASES: Normal external type-id/property ordering supported by handler buffering/completion paths  
CASES: Boundary missing external type id or value, exercising default-type and absent-property handling  
RISKS: No source/test fixture fields or JSON input shown; avoid asserting unprovided enum names, values, or APIs