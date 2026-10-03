TARGETS: AsWrapperTypeDeserializer typed object/array/scalar/any paths and private wrapper-object parsing.
ORACLES: Existing WrapperObjectWithObjectIdTest::testSimple expected successful Computer deserialization.
CASES: Wrapper-object type id followed by object content containing object-id/reference fields.
CASES: Parser positioned at START_OBJECT versus FIELD_NAME after object-id handling.
CASES: Invalid non-object wrapper input should retain JsonMappingException expectation.
RISKS: _deserialize is private; exercise through ObjectMapper/type-deserialization behavior only.
RISKS: Context lacks full model JSON and assertions; derive inputs from existing trigger test.