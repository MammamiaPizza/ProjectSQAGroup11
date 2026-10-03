TARGETS: TokenBuffer.writeObject or _append (raw value handling), TokenBuffer.asParser (parser for
embedded objects), roundtrip via ObjectMapper
TARGETS: TokenBuffer.deserialize(JsonParser, DeserializationContext) used in converting POJOs
ORACLES: Expected JsonToken stream: START_OBJECT/fields/END_OBJECT, not VALUE_EMBEDDED_OBJECT;
conforms to ObjectMapper tree conversion roundtip
ORACLES: Existing TestConversions.testConversionOfPojos serves as oracle; Jackson API contract:
writeObject uses ObjectCodec to produce tree tokens
CASES: Simple bean with String/int fields, nested bean, bean with List/array, null property, empty
bean, @JsonTypeInfo annotated polymorphc bean
CASES: Boundary: bean with many fields, deep nesting, bean with @JsonObjectId; error: cyclic
reference (may be unsupported), null input
RISKS: Fault location unknown; could be in _append, asParser, or serialization of native ids;
without source, external behavior tests only
RISKS: Bug may only manifest with certain POJOs (e.g., those with ObjectId/ TypeId); limited to
TokenBuffer public API for verification