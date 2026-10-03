package org.apache.commons.cli;

import junit.framework.TestCase;

public class GnuParserEqualsOptionTest extends TestCase
{
    public void testShortOptionWithEqualsUsesValueWithoutEquals() throws Exception
    {
        Options options = new Options();
        options.addOption(new Option("f", true, "file"));

        CommandLine commandLine = new GnuParser().parse(options, new String[] { "-f=bar" });

        assertEquals("bar", commandLine.getOptionValue("f"));
    }

    public void testSingleDashLongOptionWithEqualsUsesLongOptionValue() throws Exception
    {
        Options options = new Options();
        options.addOption(new Option("f", "foo", true, "file"));

        CommandLine commandLine = new GnuParser().parse(options, new String[] { "-foo=bar" });

        assertTrue(commandLine.hasOption("foo"));
        assertEquals("bar", commandLine.getOptionValue("foo"));
    }

    public void testDoubleDashLongOptionWithEqualsIsRecognized() throws Exception
    {
        Options options = new Options();
        options.addOption(new Option("f", "foo", true, "file"));

        CommandLine commandLine = new GnuParser().parse(options, new String[] { "--foo=bar" });

        assertTrue(commandLine.hasOption("foo"));
        assertEquals("bar", commandLine.getOptionValue("foo"));
    }

    public void testShortOptionWithoutEqualsStillAcceptsFollowingValue() throws Exception
    {
        Options options = new Options();
        options.addOption(new Option("f", true, "file"));

        CommandLine commandLine = new GnuParser().parse(options, new String[] { "-f", "bar" });

        assertEquals("bar", commandLine.getOptionValue("f"));
    }

    public void testLongOptionWithoutEqualsStillAcceptsFollowingValue() throws Exception
    {
        Options options = new Options();
        options.addOption(new Option("f", "foo", true, "file"));

        CommandLine commandLine = new GnuParser().parse(options, new String[] { "--foo", "bar" });

        assertTrue(commandLine.hasOption("foo"));
        assertEquals("bar", commandLine.getOptionValue("foo"));
    }

    public void testStopAtNonOptionLeavesFollowingTokensAsArguments() throws Exception
    {
        Options options = new Options();
        options.addOption(new Option("f", true, "file"));

        CommandLine commandLine = new GnuParser().parse(
            options,
            new String[] { "input.txt", "-f", "bar" },
            true);

        assertFalse(commandLine.hasOption("f"));
        assertEquals(3, commandLine.getArgs().length);
        assertEquals("input.txt", commandLine.getArgs()[0]);
        assertEquals("-f", commandLine.getArgs()[1]);
        assertEquals("bar", commandLine.getArgs()[2]);
    }
}
