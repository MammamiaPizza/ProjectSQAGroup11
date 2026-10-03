@Test
public void escapeNonAsciiFeatureSetsMaximumAndEscapesOutput() throws Exception {
    java.io.StringWriter output = new java.io.StringWriter();
    com.fasterxml.jackson.core.JsonGenerator generator = new com.fasterxml.jackson.core.JsonFactory()
            .enable(com.fasterxml.jackson.core.JsonGenerator.Feature.ESCAPE_NON_ASCII)
            .createGenerator(output);

    org.junit.Assert.assertEquals(127, generator.getHighestEscapedChar());
    generator.writeString("\u00E9");
    generator.close();

    org.junit.Assert.assertEquals("\"\\u00E9\"", output.toString());
}

@Test
public void highestNonEscapedCharCanBeConfiguredAndCleared() throws Exception {
    java.io.StringWriter output = new java.io.StringWriter();
    com.fasterxml.jackson.core.JsonGenerator generator = new com.fasterxml.jackson.core.JsonFactory()
            .createGenerator(output);

    org.junit.Assert.assertSame(generator, generator.setHighestNonEscapedChar(-1));
    org.junit.Assert.assertEquals(0, generator.getHighestEscapedChar());
    org.junit.Assert.assertSame(generator, generator.setHighestNonEscapedChar(127));
    org.junit.Assert.assertEquals(127, generator.getHighestEscapedChar());

    generator.writeString("\u00E9");
    generator.close();

    org.junit.Assert.assertEquals("\"\\u00E9\"", output.toString());
}

@Test
public void characterEscapesCanBeInstalledAndReset() throws Exception {
    final int[] escapes = new int[128];
    escapes['@'] = com.fasterxml.jackson.core.io.CharacterEscapes.ESCAPE_CUSTOM;
    com.fasterxml.jackson.core.io.CharacterEscapes customEscapes =
            new com.fasterxml.jackson.core.io.CharacterEscapes() {
                @Override
                public int[] getEscapeCodesForAscii() {
                    return escapes;
                }

                @Override
                public com.fasterxml.jackson.core.SerializableString getEscapeSequence(int ch) {
                    return (ch == '@')
                            ? new com.fasterxml.jackson.core.io.SerializedString("[at]")
                            : null;
                }
            };

    java.io.StringWriter escapedOutput = new java.io.StringWriter();
    com.fasterxml.jackson.core.JsonGenerator escapedGenerator = new com.fasterxml.jackson.core.JsonFactory()
            .createGenerator(escapedOutput);
    escapedGenerator.setCharacterEscapes(customEscapes);
    org.junit.Assert.assertSame(customEscapes, escapedGenerator.getCharacterEscapes());
    escapedGenerator.writeString("a@b");
    escapedGenerator.close();
    org.junit.Assert.assertEquals("\"a[at]b\"", escapedOutput.toString());

    java.io.StringWriter resetOutput = new java.io.StringWriter();
    com.fasterxml.jackson.core.JsonGenerator resetGenerator = new com.fasterxml.jackson.core.JsonFactory()
            .createGenerator(resetOutput);
    resetGenerator.setCharacterEscapes(customEscapes);
    resetGenerator.setCharacterEscapes(null);
    org.junit.Assert.assertNull(resetGenerator.getCharacterEscapes());
    resetGenerator.writeString("@");
    resetGenerator.close();
    org.junit.Assert.assertEquals("\"@\"", resetOutput.toString());
}

@Test
public void writeStringFieldHonorsDisabledFieldNameQuoting() throws Exception {
    java.io.StringWriter output = new java.io.StringWriter();
    com.fasterxml.jackson.core.JsonGenerator generator = new com.fasterxml.jackson.core.JsonFactory()
            .disable(com.fasterxml.jackson.core.JsonGenerator.Feature.QUOTE_FIELD_NAMES)
            .createGenerator(output);

    generator.writeStartObject();
    generator.writeStringField("foo", "bar");
    generator.writeEndObject();
    generator.close();

    org.junit.Assert.assertEquals("{foo:\"bar\"}", output.toString());
}