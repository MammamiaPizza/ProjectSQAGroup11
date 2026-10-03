TARGETS: JsonValueSerializer.serialize: @JsonValue output with polymorphic/default and external type ids.
TARGETS: isNaturalTypeWithStdHandling: String,Integer,Boolean,Double versus other accessor result types.
ORACLES: Trigger assertions: deserialization yields Bean1385, not byte[].
ORACLES: External type-id JSON must contain `"type":"thingy"` rather than `"type":"date"`.
CASES: @JsonValue creator-backed bean with default typing; round-trip runtime class assertion.
CASES: External type-id serialization of @JsonValue value 12345 with declared type identity.
CASES: Natural accessor result types and a non-natural result type under type-information handling.
RISKS: Type serializer selection may use runtime value type instead of declared/containing bean type.
RISKS: Context is limited to signatures and trigger outcomes; no source body or broader API behavior supplied.