TARGETS: JsonReader.peek/next* for top-level scalars in lenient mode; JsonWriter.value/jsonValue
without beginArray/Object when lenient.
ORACLES: testTopLevelValueTypeWithSkipValue, testTopLevelValueTypes (reader+writer) define expected
success for top-level scalars.
CASES: Top-level string, number, boolean, null; boundary: empty string, large numbers, exponent;
error: malformed, strict mode rejection.
RISKS: Regression in strict mode, skipValue/hasNext interaction, writer nested vs root, false
lenient acceptance of invalid inputs.