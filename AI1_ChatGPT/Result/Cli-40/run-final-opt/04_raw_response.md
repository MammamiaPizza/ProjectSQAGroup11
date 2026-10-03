@org.junit.Test
public void createNumberParsesIntegralAndDecimalValuesAndRejectsInvalidText() throws org.apache.commons.cli.ParseException
{
    org.junit.Assert.assertEquals(java.lang.Long.valueOf(42L), org.apache.commons.cli.TypeHandler.createNumber("42"));
    org.junit.Assert.assertEquals(java.lang.Double.valueOf(3.5d), org.apache.commons.cli.TypeHandler.createNumber("3.5"));

    try
    {
        org.apache.commons.cli.TypeHandler.createNumber("not-a-number");
        org.junit.Assert.fail("Expected ParseException for an invalid number");
    }
    catch (final org.apache.commons.cli.ParseException expected)
    {
        // expected
    }
}

@org.junit.Test
public void createClassAndObjectHandleResolvableAndUnresolvableClasses() throws org.apache.commons.cli.ParseException
{
    org.junit.Assert.assertSame(java.lang.String.class,
            org.apache.commons.cli.TypeHandler.createClass("java.lang.String"));
    org.junit.Assert.assertTrue(org.apache.commons.cli.TypeHandler.createObject("java.lang.String")
            instanceof java.lang.String);

    try
    {
        org.apache.commons.cli.TypeHandler.createClass("java.lang.NoSuchClass");
        org.junit.Assert.fail("Expected ParseException for an unknown class");
    }
    catch (final org.apache.commons.cli.ParseException expected)
    {
        // expected
    }

    try
    {
        org.apache.commons.cli.TypeHandler.createObject("java.lang.Integer");
        org.junit.Assert.fail("Expected ParseException for a class without a no-argument constructor");
    }
    catch (final org.apache.commons.cli.ParseException expected)
    {
        // expected
    }
}

@org.junit.Test
public void createURLParsesValidUrlsAndRejectsMalformedUrls() throws org.apache.commons.cli.ParseException
{
    final java.net.URL url = org.apache.commons.cli.TypeHandler.createURL("http://example.com/path");
    org.junit.Assert.assertEquals("http://example.com/path", url.toExternalForm());

    try
    {
        org.apache.commons.cli.TypeHandler.createURL("not a url");
        org.junit.Assert.fail("Expected ParseException for a malformed URL");
    }
    catch (final org.apache.commons.cli.ParseException expected)
    {
        // expected
    }
}

@org.junit.Test
public void createFileCreatesFileAndUnimplementedDateAndFilesOperationsThrow()
{
    final java.io.File file = org.apache.commons.cli.TypeHandler.createFile("sample.txt");
    org.junit.Assert.assertEquals("sample.txt", file.getPath());

    try
    {
        org.apache.commons.cli.TypeHandler.createDate("2020-01-01");
        org.junit.Assert.fail("Expected UnsupportedOperationException for date creation");
    }
    catch (final java.lang.UnsupportedOperationException expected)
    {
        // expected
    }

    try
    {
        org.apache.commons.cli.TypeHandler.createFiles("sample.txt");
        org.junit.Assert.fail("Expected UnsupportedOperationException for file list creation");
    }
    catch (final java.lang.UnsupportedOperationException expected)
    {
        // expected
    }
}