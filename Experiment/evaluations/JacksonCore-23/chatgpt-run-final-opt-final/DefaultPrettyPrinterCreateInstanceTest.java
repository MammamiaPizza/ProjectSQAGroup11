package com.fasterxml.jackson.core.util;

import java.io.StringWriter;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class DefaultPrettyPrinterCreateInstanceTest
{
    @Test
    public void basePrinterCreatesIndependentUsableInstance() throws Exception
    {
        DefaultPrettyPrinter original = new DefaultPrettyPrinter("|");
        DefaultPrettyPrinter copy = original.createInstance();

        assertNotSame(original, copy);
        assertEquals("1|2", writeTwoRootValues(copy));
    }

    @Test(expected = IllegalStateException.class)
    public void subclassThatDoesNotOverrideCreateInstanceIsRejected()
    {
        new NonOverridingPrettyPrinter().createInstance();
    }

    @Test
    public void subclassThatOverridesCreateInstanceCanCreateItsOwnType() throws Exception
    {
        ValidPrettyPrinter original = new ValidPrettyPrinter("|");
        DefaultPrettyPrinter copy = original.createInstance();

        assertEquals(ValidPrettyPrinter.class, copy.getClass());
        assertNotSame(original, copy);
        assertEquals("1|2", writeTwoRootValues(copy));
    }

    private String writeTwoRootValues(DefaultPrettyPrinter printer) throws Exception
    {
        StringWriter output = new StringWriter();
        JsonGenerator generator = new JsonFactory().createGenerator(output);
        generator.setPrettyPrinter(printer);
        generator.writeNumber(1);
        generator.writeNumber(2);
        generator.close();
        return output.toString();
    }

    private static class NonOverridingPrettyPrinter extends DefaultPrettyPrinter
    {
    }

    private static class ValidPrettyPrinter extends DefaultPrettyPrinter
    {
        ValidPrettyPrinter(String rootSeparator)
        {
            super(rootSeparator);
        }

        ValidPrettyPrinter(ValidPrettyPrinter source)
        {
            super(source);
        }

        @Override
        public DefaultPrettyPrinter createInstance()
        {
            return new ValidPrettyPrinter(this);
        }
    }

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
}
