TARGETS: GeneratorBase._asString(BigDecimal) and BigDecimal write paths in UTF8JsonGenerator/WriterBasedJsonGenerator.
ORACLES: Trigger requires an exception, not output, for BigDecimal 1E+10000 under plain-number serialization.
CASES: Exercise 1E+10000 through both byte-stream and Writer generators with plain BigDecimal output enabled.
CASES: Check allowed-scale boundary versus out-of-range exponent/scale values if feature setup is available.
RISKS: Context omits full feature setup, exception type/message, exact scale limit, and complete writeNumber signatures.