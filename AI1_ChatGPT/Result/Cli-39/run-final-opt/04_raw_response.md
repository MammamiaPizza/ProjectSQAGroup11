@org.junit.Test
public void testCreateValueForSupportedScalarTypes() throws Exception
{
    org.junit.Assert.assertEquals("value",
            org.apache.commons.cli.TypeHandler.createValue("value",
                    org.apache.commons.cli.PatternOptionBuilder.STRING_VALUE));

    final Object object = org.apache.commons.cli.TypeHandler.createValue("java.lang.String",
            org.apache.commons.cli.PatternOptionBuilder.OBJECT_VALUE);
    org.junit.Assert.assertTrue(object instanceof String);

    final Object number = org.apache.commons.cli.TypeHandler.createValue("12.5",
            org.apache.commons.cli.PatternOptionBuilder.NUMBER_VALUE);
    org.junit.Assert.assertTrue(number instanceof Double);
    org.junit.Assert.assertEquals(12.5d, ((Number) number).doubleValue(), 0.0d);

    org.junit.Assert.assertEquals(String.class,
            org.apache.commons.cli.TypeHandler.createValue("java.lang.String",
                    org.apache.commons.cli.PatternOptionBuilder.CLASS_VALUE));

    final Object url = org.apache.commons.cli.TypeHandler.createValue("http://example.com/path",
            org.apache.commons.cli.PatternOptionBuilder.URL_VALUE);
    org.junit.Assert.assertTrue(url instanceof java.net.URL);
    org.junit.Assert.assertEquals("http://example.com/path", url.toString());
}

@org.junit.Test
public void testCreateNumberSupportsIntegralAndRejectsInvalidValues() throws Exception
{
    final Number integral = org.apache.commons.cli.TypeHandler.createNumber("42");
    org.junit.Assert.assertTrue(integral instanceof Long);
    org.junit.Assert.assertEquals(42L, integral.longValue());

    try
    {
        org.apache.commons.cli.TypeHandler.createNumber("not-a-number");
        org.junit.Assert.fail("Expected ParseException");
    }
    catch (final org.apache.commons.cli.ParseException expected)
    {
        // expected
    }
}

@org.junit.Test
public void testReflectionAndUrlFailuresAreReportedAsParseExceptions() throws Exception
{
    try
    {
        org.apache.commons.cli.TypeHandler.createClass("no.such.Type");
        org.junit.Assert.fail("Expected ParseException");
    }
    catch (final org.apache.commons.cli.ParseException expected)
    {
        // expected
    }

    try
    {
        org.apache.commons.cli.TypeHandler.createObject("no.such.Type");
        org.junit.Assert.fail("Expected ParseException");
    }
    catch (final org.apache.commons.cli.ParseException expected)
    {
        // expected
    }

    try
    {
        org.apache.commons.cli.TypeHandler.createObject("java.lang.Integer");
        org.junit.Assert.fail("Expected ParseException");
    }
    catch (final org.apache.commons.cli.ParseException expected)
    {
        // expected
    }

    try
    {
        org.apache.commons.cli.TypeHandler.createURL("not a url");
        org.junit.Assert.fail("Expected ParseException");
    }
    catch (final org.apache.commons.cli.ParseException expected)
    {
        // expected
    }
}

@org.junit.Test
public void testCreateDateIsNotImplemented() throws Exception
{
    try
    {
        org.apache.commons.cli.TypeHandler.createValue("2020-01-01",
                org.apache.commons.cli.PatternOptionBuilder.DATE_VALUE);
        org.junit.Assert.fail("Expected UnsupportedOperationException");
    }
    catch (final UnsupportedOperationException expected)
    {
        // expected
    }
}