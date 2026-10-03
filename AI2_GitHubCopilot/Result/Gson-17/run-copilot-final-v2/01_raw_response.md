TARGETS: read(JsonReader): null token returns null; non-string tokens throw JsonParseException
ORACLES: TypeAdapter contract: read() must return null on JSON null, not throw
ORACLES: Expected exception message: "The date should be a string value"
CASES: read() with null JSON token (→ null), valid date strings, malformed dates
CASES: read() with numeric, boolean, object-start, array-start tokens
CASES: empty date string, whitespace-only, leading/trailing spaces
RISKS: write(JsonWriter,Date) behavior not directly tested by given triggers
RISKS: Exact exception subclass or message for non-string may differ; rely on test names