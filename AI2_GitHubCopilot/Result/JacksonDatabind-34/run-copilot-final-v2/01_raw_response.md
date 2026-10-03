TARGETS: NumberSerializer.getSchema(SerializerProvider, Type) for schema type output.
TARGETS: NumberSerializer.acceptJsonFormatVisitor for JSON format type hint.
ORACLES: BigDecimal → getSchema numberType=BIG_DECIMAL; BigInteger → BIG_INTEGER (JSON
Schema/NewSchemaTest).
ORACLES: Existing NewSchemaTest::testSimpleNumbers provides expected schema strings.
CASES: getSchema with BigDecimal.class → BIG_DECIMAL; BigInteger.class → BIG_INTEGER; Long →
INTEGER; Double → NUMBER.
CASES: acceptJsonFormatVisitor with BigDecimal.class → visitor.numberFormat(BIG_DECIMAL); BigInteger
→ BIG_INTEGER.
CASES: Boundary: null typeHint → rawType from NumberSerializer(Class<? extends Number>) used for
inference.
CASES: getSchema with Integer.class → NUMBER or INTEGER? Ensure mapping consistent with schema spec.
RISKS: Only schema output is verified; serialization not implicated but could regress.
RISKS: Full getSchema/acceptJsonFormatVisitor implementation not provided; limited to available
signatures.