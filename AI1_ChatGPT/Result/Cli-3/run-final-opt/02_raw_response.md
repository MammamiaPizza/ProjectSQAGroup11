package org.apache.commons.cli;

import junit.framework.TestCase;

public class TypeHandlerBug3Test extends TestCase
{
    public void testCreateNumberWithDecimalReturnsDouble()
    {
        Number value = TypeHandler.createNumber("4.5");

        assertEquals(new Double("4.5"), value);
        assertEquals(Double.class, value.getClass());
    }

    public void testPatternNumberOptionCreatesDouble()
        throws Exception
    {
        Options options = PatternOptionBuilder.parsePattern("n%");
        CommandLine line = new PosixParser().parse(options,
                                                   new String[] { "-n", "4.5" });

        Object value = line.getOptionObject("n");

        assertEquals(new Double("4.5"), value);
        assertEquals(Double.class, value.getClass());
    }

    public void testCreateNumberWithIntegralValueReturnsLong()
    {
        Number value = TypeHandler.createNumber("42");

        assertEquals(new Long(42L), value);
        assertEquals(Long.class, value.getClass());
    }

    public void testCreateValueWithNumberClassUsesNumberConversion()
    {
        Object value = TypeHandler.createValue("42",
                                               PatternOptionBuilder.NUMBER_VALUE);

        assertEquals(new Long(42L), value);
        assertEquals(Long.class, value.getClass());
    }

    public void testCreateValueWithNumberObjectMarkerUsesNumberConversion()
    {
        Object value = TypeHandler.createValue("4.5",
                                               (Object) PatternOptionBuilder.NUMBER_VALUE);

        assertEquals(new Double("4.5"), value);
        assertEquals(Double.class, value.getClass());
    }

    public void testCreateNumberWithInvalidTextReturnsNull()
    {
        assertNull(TypeHandler.createNumber("not-a-number"));
    }

    public void testCreateNumberWithNullReturnsNull()
    {
        assertNull(TypeHandler.createNumber(null));
    }

    public void testCreateValueWithStringClassReturnsOriginalValue()
    {
        String input = "command line value";

        Object value = TypeHandler.createValue(input, PatternOptionBuilder.STRING_VALUE);

        assertSame(input, value);
    }

    public void testCreateValueWithUnsupportedClassReturnsNull()
    {
        assertNull(TypeHandler.createValue("value", Boolean.class));
    }
}