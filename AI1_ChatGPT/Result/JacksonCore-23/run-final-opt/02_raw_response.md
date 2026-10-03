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
}