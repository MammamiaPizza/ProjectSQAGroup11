package org.apache.commons.cli;

import junit.framework.TestCase;

public class ParserRequiredOptionReuseTest extends TestCase {

    public void testRequiredOptionIsCheckedAfterPreviousSuccessfulParse() throws Exception {
        Options options = new Options();
        Option required = new Option("r", false, "required option");
        required.setRequired(true);
        options.addOption(required);

        Parser parser = new PosixParser();

        CommandLine first = parser.parse(options, new String[] { "-r" });
        assertTrue(first.hasOption("r"));

        try {
            parser.parse(options, new String[0]);
            fail("MissingOptionException should be thrown when the required option is absent on a reused parser");
        } catch (MissingOptionException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    public void testAllRequiredOptionsAreCheckedAfterSuccessfulParse() throws Exception {
        Options options = new Options();

        Option firstRequired = new Option("a", false, "first required option");
        firstRequired.setRequired(true);
        options.addOption(firstRequired);

        Option secondRequired = new Option("b", false, "second required option");
        secondRequired.setRequired(true);
        options.addOption(secondRequired);

        Parser parser = new PosixParser();

        CommandLine first = parser.parse(options, new String[] { "-a", "-b" });
        assertTrue(first.hasOption("a"));
        assertTrue(first.hasOption("b"));

        try {
            parser.parse(options, new String[] { "-a" });
            fail("MissingOptionException should be thrown for the required option omitted on a reused parser");
        } catch (MissingOptionException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    public void testMissingRequiredOptionFailsOnEachIndependentParseAttempt() throws Exception {
        Options options = new Options();
        Option required = new Option("r", false, "required option");
        required.setRequired(true);
        options.addOption(required);

        Parser parser = new PosixParser();

        try {
            parser.parse(options, new String[0]);
            fail("MissingOptionException should be thrown for the first parse");
        } catch (MissingOptionException expected) {
            assertNotNull(expected.getMessage());
        }

        try {
            parser.parse(options, new String[0]);
            fail("MissingOptionException should be thrown again for the second parse");
        } catch (MissingOptionException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    public void testRequiredOptionCanBeParsedNormally() throws Exception {
        Options options = new Options();
        Option required = new Option("r", false, "required option");
        required.setRequired(true);
        options.addOption(required);

        CommandLine commandLine = new PosixParser().parse(options, new String[] { "-r" });

        assertTrue(commandLine.hasOption("r"));
    }

    public void testRequiredOptionWithoutItsArgumentThrowsMissingArgumentException() throws Exception {
        Options options = new Options();
        Option required = new Option("r", true, "required option with argument");
        required.setRequired(true);
        options.addOption(required);

        try {
            new PosixParser().parse(options, new String[] { "-r" });
            fail("MissingArgumentException should be thrown when an option requiring an argument has none");
        } catch (MissingArgumentException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    public void testUnknownOptionThrowsUnrecognizedOptionException() throws Exception {
        Options options = new Options();

        try {
            new PosixParser().parse(options, new String[] { "-unknown" });
            fail("UnrecognizedOptionException should be thrown for an unknown option");
        } catch (UnrecognizedOptionException expected) {
            assertNotNull(expected.getMessage());
        }
    }
}