TARGETS: NumberSerializer.getSchema(SerializerProvider, Type) for BigDecimal and BigInteger serializers
TARGETS: NumberSerializer.acceptJsonFormatVisitor(JsonFormatVisitorWrapper, JavaType) numeric type reporting
ORACLES: NewSchemaTest.testSimpleNumbers expected schema property numberType=BIG_DECIMAL for BigDecimal
ORACLES: Existing schema conventions distinguish BigDecimal from BigInteger numberType values
CASES: BigDecimal schema: numeric schema with numberType BIG_DECIMAL
CASES: BigInteger schema: integer schema with numberType BIG_INTEGER
CASES: Other Number schema behavior remains covered without asserting undocumented numberType values
RISKS: Context provides only the failing schema assertion; serialization output expectations are not specified