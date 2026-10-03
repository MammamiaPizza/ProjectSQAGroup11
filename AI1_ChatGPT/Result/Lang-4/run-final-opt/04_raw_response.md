@Test
public void testNullLookupConsumesNothingAndWritesNothing() throws java.io.IOException {
    final LookupTranslator translator = new LookupTranslator((CharSequence[][]) null);
    final java.io.StringWriter writer = new java.io.StringWriter();

    final int consumed = translator.translate("input", 0, writer);

    assertEquals(0, consumed);
    assertEquals("", writer.toString());
}