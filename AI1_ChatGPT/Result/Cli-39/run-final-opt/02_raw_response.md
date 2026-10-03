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

    @Test
    public void testCreateValueForNonexistentExistingFileReturnsNull() throws Exception
    {
        File file = File.createTempFile("cli-typehandler-missing", ".tmp");
        file.delete();

        try
        {
            Object value = TypeHandler.createValue(file.getAbsolutePath(),
                PatternOptionBuilder.EXISTING_FILE_VALUE);

            assertNull(value);
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
}