TARGETS: ExternalTypeHandler.complete() and Builder.build() for type-property linking
ORACLES: ExternalTypeIdWithEnum1328Test::testExample expects successful deserialization without
InvalidDefinitionException
CASES: Enum type-id with @JsonTypeInfo(use=Id.NAME, property="type"), external property wrapping
CASES: Type-id matching via hasTypePropertyName() when type property is external
RISKS: ExtTypedProperty.linkTypeProperty() may not be called, leaving typeProperty null in
complete()
RISKS: argument type mismatch suggests type-id not resolved to correct deserializer before property
assignment