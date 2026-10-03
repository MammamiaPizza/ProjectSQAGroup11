package org.apache.commons.cli;

import junit.framework.TestCase;

public class PosixParserCli22Test extends TestCase {

    private static class ExposedPosixParser extends PosixParser {
        public String[] flattenTokens(Options options, String[] arguments, boolean stopAtNonOption) {
            return flatten(options, arguments, stopAtNonOption);
        }
    }

    private void assertTokens(String[] expected, String[] actual) {
        assertEquals("token count", expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals("token at index " + i, expected[i], actual[i]);
        }
    }

    public void testStopAtNonOptionDoesNotReplaceArgumentWithTerminator() {
        Options options = new Options();
        options.addOption("b", false, "enable b");

        String[] tokens = new ExposedPosixParser().flattenTokens(
                options, new String[] { "-b", "foo" }, true);

        assertTokens(new String[] { "-b", "--", "foo" }, tokens);
    }

    public void testStopAtNonOptionPreservesCommandTextAndRemainingArguments() {
        Options options = new Options();
        options.addOption("b", false, "enable b");

        String command = "println 'hello'";
        String[] tokens = new ExposedPosixParser().flattenTokens(
                options, new String[] { "-b", command, "-x" }, true);

        assertTokens(new String[] { "-b", "--", command, "-x" }, tokens);
    }

    public void testParseStopAtNonOptionExposesFirstArgument() throws Exception {
        Options options = new Options();
        options.addOption("b", false, "enable b");

        CommandLine line = new PosixParser().parse(
                options, new String[] { "-b", "foo" }, true);

        assertTrue("the recognized option should be present", line.hasOption("b"));
        assertEquals("foo", line.getArgs()[0]);
    }

    public void testExplicitTerminatorStopsOptionProcessingAndPreservesFollowingArguments()
            throws Exception {
        Options options = new Options();
        options.addOption("b", false, "enable b");
        options.addOption("a", false, "enable a");

        CommandLine line = new PosixParser().parse(
                options, new String[] { "-b", "--", "-a", "value" }, true);

        assertTrue(line.hasOption("b"));
        assertFalse("options after -- must be arguments", line.hasOption("a"));
        assertTokens(new String[] { "-a", "value" }, line.getArgs());
    }

    public void testBurstGroupedShortOptions() throws Exception {
        Options options = new Options();
        options.addOption("a", false, "enable a");
        options.addOption("b", false, "enable b");

        CommandLine line = new PosixParser().parse(
                options, new String[] { "-ab" }, false);

        assertTrue(line.hasOption("a"));
        assertTrue(line.hasOption("b"));
        assertEquals(0, line.getArgs().length);
    }

    public void testBurstOptionWithAttachedArgument() throws Exception {
        Options options = new Options();
        options.addOption("o", true, "output");

        CommandLine line = new PosixParser().parse(
                options, new String[] { "-ovalue" }, false);

        assertTrue(line.hasOption("o"));
        assertEquals("value", line.getOptionValue("o"));
    }

    public void testNormalParsingContinuesPastNonOptionWhenStopIsDisabled() throws Exception {
        Options options = new Options();
        options.addOption("a", false, "enable a");
        options.addOption("b", false, "enable b");

        CommandLine line = new PosixParser().parse(
                options, new String[] { "-b", "file", "-a" }, false);

        assertTrue(line.hasOption("a"));
        assertTrue(line.hasOption("b"));
        assertTokens(new String[] { "file" }, line.getArgs());
    }
}
