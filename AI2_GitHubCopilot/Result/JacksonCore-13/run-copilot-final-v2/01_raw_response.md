TARGETS: JsonGeneratorImpl.writeStringField(), writeStartObject/writeEndObject pairs for JSON output
control
ORACLES: Assert generated String equals expected JSON; disable QUOTE_FIELD_NAMES via enable/disable
ORACLES: Use Feature.QUOTE_FIELD_NAMES; test via JsonFactory.createGenerator(StringWriter)
CASES: Simple field name, QUOTE_FIELD_NAMES off → unquoted {"foo":1}
CASES: Simple field name, QUOTE_FIELD_NAMES on (default) → quoted {""foo"":1}
CASES: Field name with space, QUOTE_FIELD_NAMES off → still quoted {""foo bar"":1}
CASES: Boundary: empty field name, QUOTE_FIELD_NAMES off → empty unquoted? (check spec)
CASES: Multiple fields, mixed quoting
RISKS: Concrete generator may be WriterBasedJsonGenerator; test via factory to respect abstraction
RISKS: Unknown internal writeFieldName method; rely on writeStringField final implementation