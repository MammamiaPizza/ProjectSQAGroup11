TARGETS: ObjectReader.readValue(JsonParser, Object) when _valueToUpdate != null (set via
withValueToUpdate).
ORACLES: DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES controls whether unknown properties
trigger UnrecognizedPropertyException.
ORACLES: Expected behavior: disable FAIL_ON_UNKNOWN_PROPERTIES (default is true) so extra fields are
silently ignored.
CASES: Normal—JSON payload {"da":..., "k":...} updates DataB fields da, k successfully.
CASES: Boundary—JSON with extra field "i" (not in DataB) should be ignored (no exception) when
feature disabled.
CASES: Error—same extra field with FAIL_ON_UNKNOWN_PROPERTIES enabled throws
UnrecognizedPropertyException.
RISKS: Prompt truncates ObjectReader signatures; methods like readerForUpdating or updateValue may
be hidden.
RISKS: ObjectReader configuration may be coupled to ObjectMapper defaults; per-reader feature
override must be verified.