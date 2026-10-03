package org.apache.commons.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.Test;

public class TypeHandlerFileInputStreamTest
{
    @Test
    public void testCreateValueForExistingFileReturnsReadableFileInputStream() throws Exception
    {
        File file = File.createTempFile("cli-typehandler", ".tmp");
        FileOutputStream output = new FileOutputStream(file);
        output.write(42);
        output.close();

        FileInputStream input = null;
        try
        {
            Object value = TypeHandler.createValue(file.getAbsolutePath(),
                PatternOptionBuilder.EXISTING_FILE_VALUE);

            assertTrue(value instanceof FileInputStream);
            input = (FileInputStream) value;
            assertEquals(42, input.read());
        }
        finally
        {
            if (input != null)
            {
                input.close();
            }
            file.delete();
        }
    }

    @Test(expected = ParseException.class)
    public void testCreateValueForNonexistentExistingFileReturnsNull() throws Exception
    {
        File file = File.createTempFile("cli-typehandler-missing", ".tmp");
        file.delete();

        try
        {
            TypeHandler.createValue(file.getAbsolutePath(),
                PatternOptionBuilder.EXISTING_FILE_VALUE);
        }
        finally
        {
            file.delete();
        }
    }

    @Test
    public void testExistingFilePatternParsesToFileInputStream() throws Exception
    {
        File file = File.createTempFile("cli-pattern", ".tmp");
        FileInputStream input = null;

        try
        {
            Options options = PatternOptionBuilder.parsePattern("g<");
            CommandLine commandLine = new PosixParser().parse(options,
                new String[] { "-g", file.getAbsolutePath() });

            Object value = commandLine.getOptionObject("g");
            assertTrue(value instanceof FileInputStream);

            input = (FileInputStream) value;
            assertTrue(input.available() >= 0);
        }
        finally
        {
            if (input != null)
            {
                input.close();
            }
            file.delete();
        }
    }

    @Test
    public void testExistingFilePatternForMissingFileParsesToNull() throws Exception
    {
        File file = File.createTempFile("cli-pattern-missing", ".tmp");
        file.delete();

        try
        {
            Options options = PatternOptionBuilder.parsePattern("f<");
            CommandLine commandLine = new PosixParser().parse(options,
                new String[] { "-f", file.getAbsolutePath() });

            assertNull(commandLine.getOptionObject("f"));
        }
        finally
        {
            file.delete();
        }
    }

    @Test
    public void testRegularFileValueDoesNotRequireFileToExist() throws Exception
    {
        File file = File.createTempFile("cli-file-value-missing", ".tmp");
        file.delete();

        try
        {
            Object value = TypeHandler.createValue(file.getAbsolutePath(),
                PatternOptionBuilder.FILE_VALUE);

            assertTrue(value instanceof File);
            assertEquals(file.getAbsolutePath(), ((File) value).getAbsolutePath());
        }
        finally
        {
            file.delete();
        }
    }

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
}
