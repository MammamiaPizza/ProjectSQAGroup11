TARGETS: ExternalTypeHandler.complete external type-id/property completion; BeanDeserializerBase integration.
ORACLES: Existing trigger asserts deserialized value is "foo", not null.
CASES: External type id with its value property; verify property retained after completion.
CASES: Type id/value ordering and end-of-object completion paths.
RISKS: Null assignment when external-type handling buffers or resolves the property.
RISKS: Context lacks full method bodies and complete model/JSON input details.