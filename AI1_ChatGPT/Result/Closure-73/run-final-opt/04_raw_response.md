@Test
public void testConsecutiveDeleteCharactersAreEachEscaped() {
  assertEquals(
      "\"\\u007f\\u007f\"",
      CodeGenerator.jsString("\u007f\u007f", null));
}

@Test
public void testDeleteCharacterIsEscapedWithUsAsciiOutputCharset() {
  assertEquals(
      "\"\\u007f\"",
      CodeGenerator.jsString("\u007f", com.google.common.base.Charsets.US_ASCII));
}