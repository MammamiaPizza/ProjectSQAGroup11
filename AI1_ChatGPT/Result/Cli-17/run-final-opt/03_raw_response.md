package org.apache.commons.cli;

import junit.framework.TestCase;

public class PosixParserCli17Test extends TestCase
{
    private static class ExposedPosixParser extends PosixParser
    {
        public String[] flattenArguments(Options options, String[] arguments,
                                         boolean stopAtNonOption)
        {
            return flatten(options, arguments, stopAtNonOption);
        }
    }

    private void assertTokens(String[] expected, String[] actual)
    {
        assertEquals("token count", expected.length, actual.length);

        for (int i = 0; i < expected.length; i++)
        {
            assertEquals("token " + i, expected[i], actual[i]);
        }
    }

    public void testBurstAttachedArgumentStopsAtFollowingNonOption()
    {
        Options options = new Options();
        options.addOption(new Option("a", false, "flag a"));
        options.addOption(new Option("b", true, "option b"));

        String[] flattened = new ExposedPosixParser().flattenArguments(
            options,
            new String[] { "-abvalue", "first", "second" },
            true);

        assertTokens(
            new String[] { "-a", "-b", "value", "first", "--", "second" },
            flattened);
    }

    public void testBurstAttachedArgumentDoesNotStopWhenDisabled()
    {
        Options options = new Options();
        options.addOption(new Option("a", false, "flag a"));
        options.addOption(new Option("b", true, "option b"));

        String[] flattened = new ExposedPosixParser().flattenArguments(
            options,
            new String[] { "-abvalue", "first", "second" },
            false);

        assertTokens(
            new String[] { "-a", "-b", "value", "first", "second" },
            flattened);
    }

    public void testBurstArgumentWithoutAttachedValueConsumesNextTokenOnly()
    {
        Options options = new Options();
        options.addOption(new Option("a", false, "flag a"));
        options.addOption(new Option("b", true, "option b"));

        String[] flattened = new ExposedPosixParser().flattenArguments(
            options,
            new String[] { "-ab", "value", "tail" },
            true);

        assertTokens(
            new String[] { "-a", "-b", "value", "--", "tail" },
            flattened);
    }

    public void testUnknownCharacterInBurstStopsAndPreservesRemainder()
    {
        Options options = new Options();
        options.addOption(new Option("a", false, "flag a"));

        String[] flattened = new ExposedPosixParser().flattenArguments(
            options,
            new String[] { "-ax", "tail" },
            true);

        assertTokens(
            new String[] { "-a", "--", "x", "tail" },
            flattened);
    }

    public void testKnownShortOptionsAreBurstIndividually()
    {
        Options options = new Options();
        options.addOption(new Option("a", false, "flag a"));
        options.addOption(new Option("b", false, "flag b"));
        options.addOption(new Option("c", false, "flag c"));

        String[] flattened = new ExposedPosixParser().flattenArguments(
            options,
            new String[] { "-abc" },
            true);

        assertTokens(new String[] { "-a", "-b", "-c" }, flattened);
    }

    public void testSingleAndDoubleHyphenRemainArgumentBoundaries()
        throws Exception
    {
        Options options = new Options();
        options.addOption(new Option("a", false, "flag a"));

        CommandLine commandLine = new PosixParser().parse(
            options,
            new String[] { "-a", "-", "--", "-a", "plain" },
            true);

        assertTrue("option before boundaries should be recognized",
                   commandLine.hasOption("a"));
        assertTokens(new String[] { "-", "--", "-a", "plain" },
                     commandLine.getArgs());
    }
}