package org.apache.commons.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TypeHandlerIntegerConversionTest
{
    @Test
    public void createValueWithIntegerClassCreatesInteger()
        throws Exception
    {
        Object value = TypeHandler.createValue("123", Integer.class);

        assertTrue(value instanceof Integer);
        assertEquals(Integer.valueOf(123), value);
    }

    @Test
    public void createValueWithObjectDescriptorCreatesIntegerAtBoundary()
        throws Exception
    {
        Object value = TypeHandler.createValue("-2147483648", (Object) Integer.class);

        assertTrue(value instanceof Integer);
        assertEquals(Integer.valueOf(Integer.MIN_VALUE), value);
    }

    @Test(expected = ParseException.class)
    public void createValueWithIntegerClassRejectsMalformedInteger()
        throws Exception
    {
        TypeHandler.createValue("twelve", Integer.class);
    }

    @Test(expected = ParseException.class)
    public void createValueWithIntegerClassRejectsOverflow()
        throws Exception
    {
        TypeHandler.createValue("2147483648", Integer.class);
    }

    @Test(expected = ParseException.class)
    public void createValueWithObjectDescriptorRejectsEmptyInteger()
        throws Exception
    {
        TypeHandler.createValue("", (Object) Integer.class);
    }
}