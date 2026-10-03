package org.apache.commons.cli;

import junit.framework.TestCase;

public class PosixParserDefects4JTest extends TestCase
{
    public void testUnknownSingleOptionThrowsWhenNotStoppingAtNonOption()
    {
        Options options = new Options();
        options.addOption("a", false, "known option");

        try
        {
            new PosixParser().parse(options, new String[] { "-z" }, false);
            fail("An unrecognized single option must cause UnrecognizedOptionException");
        }
        catch (UnrecognizedOptionException expected)
        {
            assertNotNull(expected);
        }
        catch (ParseException unexpected)
        {
            fail("Expected UnrecognizedOptionException but got "
                 + unexpected.getClass().getName());
        }
    }

    public void testUnknownSingleOptionAfterRecognizedOptionThrows()
    {
        Options options = new Options();
        options.addOption("a", false, "known option");

        try
        {
            new PosixParser().parse(options, new String[] { "-a", "-z" }, false);
            fail("An unrecognized option must not be silently discarded after a valid option");
        }
        catch (UnrecognizedOptionException expected)
        {
            assertNotNull(expected);
        }
        catch (ParseException unexpected)
        {
            fail("Expected UnrecognizedOptionException but got "
                 + unexpected.getClass().getName());
        }
    }

    public void testUnknownOptionInBurstThrowsWhenNotStoppingAtNonOption()
    {
        Options options = new Options();
        options.addOption("a", false, "known option");

        try
        {
            new PosixParser().parse(options, new String[] { "-az" }, false);
            fail("A burst containing an unrecognized option must cause UnrecognizedOptionException");
        }
        catch (UnrecognizedOptionException expected)
        {
            assertNotNull(expected);
        }
        catch (ParseException unexpected)
        {
            fail("Expected UnrecognizedOptionException but got "
                 + unexpected.getClass().getName());
        }
    }

    public void testUnknownOptionIsAnArgumentWhenStoppingAtNonOption() throws Exception
    {
        Options options = new Options();
        options.addOption("a", false, "known option");

        CommandLine commandLine =
            new PosixParser().parse(options, new String[] { "-z", "tail" }, true);

        assertFalse(commandLine.hasOption("a"));
        assertEquals(2, commandLine.getArgs().length);
        assertEquals("-z", commandLine.getArgs()[0]);
        assertEquals("tail", commandLine.getArgs()[1]);
    }

    public void testRecognizedBurstOptionsAreParsed() throws Exception
    {
        Options options = new Options();
        options.addOption("a", false, "first option");
        options.addOption("b", false, "second option");

        CommandLine commandLine =
            new PosixParser().parse(options, new String[] { "-ab" }, false);

        assertTrue(commandLine.hasOption("a"));
        assertTrue(commandLine.hasOption("b"));
        assertEquals(0, commandLine.getArgs().length);
    }

    public void testBurstOptionWithAttachedArgumentIsParsed() throws Exception
    {
        Options options = new Options();
        options.addOption("f", true, "file");

        CommandLine commandLine =
            new PosixParser().parse(options, new String[] { "-fvalue.txt" }, false);

        assertTrue(commandLine.hasOption("f"));
        assertEquals("value.txt", commandLine.getOptionValue("f"));
    }

    public void testNonOptionStopsFurtherOptionProcessingWhenRequested() throws Exception
    {
        Options options = new Options();
        options.addOption("a", false, "known option");

        CommandLine commandLine =
            new PosixParser().parse(options, new String[] { "file", "-a" }, true);

        assertFalse(commandLine.hasOption("a"));
        assertEquals(2, commandLine.getArgs().length);
        assertEquals("file", commandLine.getArgs()[0]);
        assertEquals("-a", commandLine.getArgs()[1]);
    }

    public void testDoubleDashMakesFollowingUnknownOptionAnArgument() throws Exception
    {
        Options options = new Options();
        options.addOption("a", false, "known option");

        CommandLine commandLine =
            new PosixParser().parse(options, new String[] { "--", "-z" }, false);

        assertFalse(commandLine.hasOption("a"));
        assertEquals(1, commandLine.getArgs().length);
        assertEquals("-z", commandLine.getArgs()[0]);
    }
}