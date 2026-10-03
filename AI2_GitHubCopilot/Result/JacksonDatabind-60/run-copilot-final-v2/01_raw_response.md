TARGETS: JsonValueSerializer.serialize() (type-id writing), isNaturalTypeWithStdHandling,
acceptJsonFormatVisitor.
ORACLES: Type id must match enclosing bean type, not @JsonValue return type; see
TypeSerializer.writeTypePrefix in JsonGenerator.
CASES: Bean with @JsonValue returning byte[], external type-id on property referencing @JsonValue
bean, native array/primitive value with type info.
RISKS: Full delegate stack (BeanSerializer, TypeSerializer) not visible; only modified class
context; missing test fixture details.