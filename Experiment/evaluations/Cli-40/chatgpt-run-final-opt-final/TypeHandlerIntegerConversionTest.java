package org.apache.commons.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TypeHandlerIntegerConversionTest
{
    @Test(expected = ParseException.class)
    public void createValueWithIntegerClassCreatesInteger()
        throws Exception
    {
        TypeHandler.createValue("123", Integer.class);
    }

    @Test(expected = ParseException.class)
    public void createValueWithObjectDescriptorCreatesIntegerAtBoundary()
        throws Exception
    {
        TypeHandler.createValue("-2147483648", (Object) Integer.class);
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
