package org.apache.commons.cli;

import junit.framework.TestCase;

public class ParserRequiredOptionsFormattingTest extends TestCase
{
    private Option requiredOption(String opt)
    {
        Option option = new Option(opt, false, "");
        option.setRequired(true);
        return option;
    }

    public void testMultipleMissingRequiredOptionsAreSeparatedByCommaAndSpace()
        throws Exception
    {
        Options options = new Options();
        options.addOption(requiredOption("f"));
        options.addOption(requiredOption("x"));

        try
        {
            new PosixParser().parse(options, new String[0]);
            fail("Expected MissingOptionException");
        }
        catch (MissingOptionException e)
        {
            assertEquals("Missing required options: f, x", e.getMessage());
        }
    }

    public void testMultipleMissingRequiredOptionsUseCommaAndSpaceForDifferentKeys()
        throws Exception
    {
        Options options = new Options();
        options.addOption(requiredOption("b"));
        options.addOption(requiredOption("c"));

        try
        {
            new PosixParser().parse(options, new String[0]);
            fail("Expected MissingOptionException");
        }
        catch (MissingOptionException e)
        {
            assertEquals("Missing required options: b, c", e.getMessage());
        }
    }

    public void testSingleMissingRequiredOptionKeepsSingularMessage()
        throws Exception
    {
        Options options = new Options();
        options.addOption(requiredOption("f"));

        try
        {
            new PosixParser().parse(options, new String[0]);
            fail("Expected MissingOptionException");
        }
        catch (MissingOptionException e)
        {
            assertEquals("Missing required option: f", e.getMessage());
        }
    }

    public void testOnlyUnsatisfiedRequiredOptionsAppearInMessage()
        throws Exception
    {
        Options options = new Options();
        options.addOption(requiredOption("a"));
        options.addOption(requiredOption("b"));
        options.addOption(requiredOption("c"));

        try
        {
            new PosixParser().parse(options, new String[] { "-b" });
            fail("Expected MissingOptionException");
        }
        catch (MissingOptionException e)
        {
            assertEquals("Missing required options: a, c", e.getMessage());
        }
    }

    public void testAllRequiredOptionsMayBeSupplied()
        throws Exception
    {
        Options options = new Options();
        options.addOption(requiredOption("f"));
        options.addOption(requiredOption("x"));

        CommandLine commandLine =
            new PosixParser().parse(options, new String[] { "-f", "-x" });

        assertTrue(commandLine.hasOption("f"));
        assertTrue(commandLine.hasOption("x"));
    }

    public void testUnrecognizedOptionStillProducesParseException()
        throws Exception
    {
        Options options = new Options();

        try
        {
            new PosixParser().parse(options, new String[] { "-z" });
            fail("Expected ParseException");
        }
        catch (ParseException e)
        {
            assertEquals("Unrecognized option: -z", e.getMessage());
        }
    }
}