@Test
public void withoutSpacesInObjectEntriesCreatesImmutableConfiguration() throws Exception {
    com.fasterxml.jackson.core.util.DefaultPrettyPrinter base =
            new com.fasterxml.jackson.core.util.DefaultPrettyPrinter();
    com.fasterxml.jackson.core.util.DefaultPrettyPrinter compact =
            base.withoutSpacesInObjectEntries();

    org.junit.Assert.assertSame(base, base.withSpacesInObjectEntries());
    org.junit.Assert.assertNotSame(base, compact);
    org.junit.Assert.assertSame(compact, compact.withoutSpacesInObjectEntries());

    com.fasterxml.jackson.core.util.DefaultPrettyPrinter inlineBase =
            base.withObjectIndenter(com.fasterxml.jackson.core.util.DefaultPrettyPrinter.NopIndenter.instance);
    com.fasterxml.jackson.core.util.DefaultPrettyPrinter inlineCompact =
            compact.withObjectIndenter(com.fasterxml.jackson.core.util.DefaultPrettyPrinter.NopIndenter.instance);

    org.junit.Assert.assertEquals("{\"a\" : 1}", writeSingleEntryObject(inlineBase));
    org.junit.Assert.assertEquals("{\"a\":1}", writeSingleEntryObject(inlineCompact));
}

@Test
public void withArrayIndenterNullUsesInlineIndenterWithoutChangingOriginal() throws Exception {
    com.fasterxml.jackson.core.util.DefaultPrettyPrinter base =
            new com.fasterxml.jackson.core.util.DefaultPrettyPrinter();
    com.fasterxml.jackson.core.util.DefaultPrettyPrinter inline =
            base.withArrayIndenter(null);

    org.junit.Assert.assertNotSame(base, inline);
    org.junit.Assert.assertSame(inline, inline.withArrayIndenter(null));
    org.junit.Assert.assertEquals("[1,2]", writeTwoElementArray(inline));
}

@Test
public void mutatingIndentersAcceptNullAsNopIndenter() throws Exception {
    com.fasterxml.jackson.core.util.DefaultPrettyPrinter printer =
            new com.fasterxml.jackson.core.util.DefaultPrettyPrinter();

    printer.indentObjectsWith(null);
    printer.indentArraysWith(null);

    org.junit.Assert.assertEquals("{\"a\" : 1}", writeSingleEntryObject(printer));
    org.junit.Assert.assertEquals("[1,2]", writeTwoElementArray(printer));
}

@Test
public void withObjectIndenterUsesConfiguredNonInlineIndenter() throws Exception {
    com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter indenter =
            new com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter() {
                @Override
                public void writeIndentation(com.fasterxml.jackson.core.JsonGenerator generator, int level)
                        throws java.io.IOException {
                    generator.writeRaw(">" + level);
                }

                @Override
                public boolean isInline() {
                    return false;
                }
            };
    com.fasterxml.jackson.core.util.DefaultPrettyPrinter base =
            new com.fasterxml.jackson.core.util.DefaultPrettyPrinter();
    com.fasterxml.jackson.core.util.DefaultPrettyPrinter configured =
            base.withObjectIndenter(indenter);

    org.junit.Assert.assertNotSame(base, configured);
    org.junit.Assert.assertEquals("{>1\"a\" : 1>0}", writeSingleEntryObject(configured));
}

private String writeSingleEntryObject(com.fasterxml.jackson.core.util.DefaultPrettyPrinter printer)
        throws Exception {
    java.io.StringWriter writer = new java.io.StringWriter();
    com.fasterxml.jackson.core.JsonGenerator generator =
            new com.fasterxml.jackson.core.JsonFactory().createGenerator(writer);
    generator.setPrettyPrinter(printer);
    generator.writeStartObject();
    generator.writeNumberField("a", 1);
    generator.writeEndObject();
    generator.close();
    return writer.toString();
}

private String writeTwoElementArray(com.fasterxml.jackson.core.util.DefaultPrettyPrinter printer)
        throws Exception {
    java.io.StringWriter writer = new java.io.StringWriter();
    com.fasterxml.jackson.core.JsonGenerator generator =
            new com.fasterxml.jackson.core.JsonFactory().createGenerator(writer);
    generator.setPrettyPrinter(printer);
    generator.writeStartArray();
    generator.writeNumber(1);
    generator.writeNumber(2);
    generator.writeEndArray();
    generator.close();
    return writer.toString();
}