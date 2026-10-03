package org.apache.commons.cli;

import junit.framework.TestCase;

public class PosixParserStopAtNonOptionTest extends TestCase
{
    private static class ExposedPosixParser extends PosixParser
    {
        public String[] flattenArguments(Options options, String[] arguments, boolean stopAtNonOption)
        {
            return flatten(options, arguments, stopAtNonOption);
        }
    }

    private void assertTokens(String[] expected, String[] actual)
    {
        assertEquals("token count", expected.length, actual.length);

        for (int i = 0; i < expected.length; i++)
        {
            assertEquals("token at index " + i, expected[i], actual[i]);
        }
    }

    public void testStopAtNonOptionRetainsFollowingOptionLikeArguments() throws Exception
    {
        Options options = new Options();
        options.addOption("a", false, "option a");
        options.addOption("b", false, "option b");

        CommandLine commandLine = new PosixParser().parse(
                options,
                new String[] { "-a", "file.txt", "-b", "-ab" },
                true);

        assertTrue("option before operand must be parsed", commandLine.hasOption("a"));
        assertFalse("option after operand must be an argument", commandLine.hasOption("b"));
        assertTokens(new String[] { "file.txt", "-b", "-ab" }, commandLine.getArgs());
    }

    public void testFirstNonOptionStopsBurstingOfLaterTokens() throws Exception
    {
        Options options = new Options();
        options.addOption("a", false, "option a");
        options.addOption("b", false, "option b");

        CommandLine commandLine = new PosixParser().parse(
                options,
                new String[] { "input", "-ab", "-a" },
                true);

        assertFalse(commandLine.hasOption("a"));
        assertFalse(commandLine.hasOption("b"));
        assertTokens(new String[] { "input", "-ab", "-a" }, commandLine.getArgs());
    }

    public void testFlattenAddsEndOfOptionsBeforeFirstOrdinaryOperand()
    {
        Options options = new Options();
        options.addOption("a", false, "option a");

        String[] flattened = new ExposedPosixParser().flattenArguments(
                options,
                new String[] { "-a", "operand", "-a", "--unknown=value" },
                true);

        assertTokens(
                new String[] { "-a", "--", "operand", "-a", "--unknown=value" },
                flattened);
    }

    public void testRecognizedOptionsBeforeFirstOperandAreTokenized()
    {
        Options options = new Options();
        options.addOption("a", false, "option a");
        options.addOption("b", false, "option b");

        String[] flattened = new ExposedPosixParser().flattenArguments(
                options,
                new String[] { "-ab", "operand", "-b" },
                true);

        assertTokens(new String[] { "-a", "-b", "--", "operand", "-b" }, flattened);
    }

    public void testUnknownCharacterInBurstStopsAndPreservesRemainder()
    {
        Options options = new Options();
        options.addOption("a", false, "option a");
        options.addOption("b", false, "option b");

        String[] flattened = new ExposedPosixParser().flattenArguments(
                options,
                new String[] { "-abx", "-a", "tail" },
                true);

        assertTokens(new String[] { "-a", "-b", "--", "x", "-a", "tail" }, flattened);
    }

    public void testOptionArgumentIsConsumedBeforeStoppingAtNextOperand()
    {
        Options options = new Options();
        options.addOption("f", true, "file");
        options.addOption("a", false, "option a");

        String[] flattened = new ExposedPosixParser().flattenArguments(
                options,
                new String[] { "-f", "value", "operand", "-a" },
                true);

        assertTokens(new String[] { "-f", "value", "--", "operand", "-a" }, flattened);
    }
}
