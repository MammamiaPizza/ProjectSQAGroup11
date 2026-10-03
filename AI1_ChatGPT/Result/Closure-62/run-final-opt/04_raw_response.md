@org.junit.Test
public void testFormatErrorWithSourceNameAndNoLineNumber() {
  LightweightMessageFormatter formatter =
      LightweightMessageFormatter.withoutSource();
  JSError error = JSError.make(
      "test", 0, -1, DiagnosticType.error("TEST", "description"));

  assertEquals(
      "test: ERROR - description\n",
      formatter.formatError(error));
}