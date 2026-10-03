package org.apache.commons.cli;

import junit.framework.TestCase;

public class PosixParserStopAtNonOptionTest extends TestCase {

    public void testUnknownOptionStopsBeforeFollowingKnownOption() throws Exception {
        Options options = new Options();
        options.addOption("a", false, "option a");

        CommandLine commandLine = new PosixParser().parse(
                options, new String[] { "-z", "-a" }, true);

        assertFalse("Confirm -a is not set", commandLine.hasOption("a"));
        assertEquals("Confirm arg of -z", "-z", commandLine.getArgs()[0]);
        assertEquals("Confirm arg of -a", "-a", commandLine.getArgs()[1]);
    }

    public void testOrdinaryNonOptionStopsBeforeFollowingKnownOption() throws Exception {
        Options options = new Options();
        options.addOption("a", false, "option a");

        CommandLine commandLine = new PosixParser().parse(
                options, new String[] { "value", "-a" }, true);

        assertFalse(commandLine.hasOption("a"));
        assertEquals("value", commandLine.getArgs()[0]);
        assertEquals("-a", commandLine.getArgs()[1]);
    }

    public void testRecognizedOptionBeforeNonOptionIsStillParsed() throws Exception {
        Options options = new Options();
        options.addOption("a", false, "option a");
        options.addOption("b", false, "option b");

        CommandLine commandLine = new PosixParser().parse(
                options, new String[] { "-a", "value", "-b" }, true);

        assertTrue(commandLine.hasOption("a"));
        assertFalse(commandLine.hasOption("b"));
        assertEquals("value", commandLine.getArgs()[0]);
        assertEquals("-b", commandLine.getArgs()[1]);
    }

    public void testStopAtNonOptionFalseContinuesAfterUnknownOption() throws Exception {
        Options options = new Options();
        options.addOption("a", false, "option a");

        CommandLine commandLine = new PosixParser().parse(
                options, new String[] { "-z", "-a" }, false);

        assertTrue(commandLine.hasOption("a"));
    }

    public void testOptionArgumentDoesNotCauseStopping() throws Exception {
        Options options = new Options();
        options.addOption("f", true, "file");
        options.addOption("a", false, "option a");

        CommandLine commandLine = new PosixParser().parse(
                options, new String[] { "-f", "file.txt", "-a" }, true);

        assertEquals("file.txt", commandLine.getOptionValue("f"));
        assertTrue(commandLine.hasOption("a"));
    }

    public void testRecognizedClusterIsBurstIntoOptions() throws Exception {
        Options options = new Options();
        options.addOption("a", false, "option a");
        options.addOption("b", false, "option b");

        CommandLine commandLine = new PosixParser().parse(
                options, new String[] { "-ab" }, true);

        assertTrue(commandLine.hasOption("a"));
        assertTrue(commandLine.hasOption("b"));
    }
}