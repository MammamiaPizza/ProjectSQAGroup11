TARGETS: JSON_ELEMENT.read(JsonReader) – return correct subtype (primitive/array/object/null)
without ClassCastException.
TARGETS: JSON_ELEMENT.write(JsonWriter, JsonElement) – serialize any JsonElement faithfully.
ORACLES: DefaultTypeAdaptersTest.testJsonElementTypeMismatch expects no ClassCastException;
round-trip preserves element type.
CASES: write+read JsonPrimitive(string), JsonPrimitive(number), JsonNull, JsonArray, JsonObject;
empty & nested structures.
RISKS: Only signature-level API available; internal read logic unknown. Behavior deduced from bug
report ClassCastException detail.