package org.apache.commons.cli;

import junit.framework.TestCase;

public class ParserRequiredOptionTest extends TestCase {

    public void testMissingSingleRequiredOptionIncludesSingularPrefix() throws Exception {
        Options options = new Options();
        Option option = new Option("f", false, "flag");
        option.setRequired(true);
        options.addOption(option);

        try {
            new PosixParser().parse(options, new String[0]);
            fail("Expected MissingOptionException");
        } catch (MissingOptionException expected) {
            assertEquals("Missing required option: f", expected.getMessage());
        }
    }

    public void testMissingMultipleRequiredOptionsIncludesPluralPrefix() throws Exception {
        Options options = new Options();

        Option first = new Option("f", false, "first");
        first.setRequired(true);
        options.addOption(first);

        Option second = new Option("x", false, "second");
        second.setRequired(true);
        options.addOption(second);

        try {
            new PosixParser().parse(options, new String[0]);
            fail("Expected MissingOptionException");
        } catch (MissingOptionException expected) {
            assertEquals("Missing required options: fx", expected.getMessage());
        }
    }

    public void testMissingOneOfMultipleRequiredOptionsUsesSingularPrefix() throws Exception {
        Options options = new Options();

        Option first = new Option("f", false, "first");
        first.setRequired(true);
        options.addOption(first);

        Option second = new Option("x", false, "second");
        second.setRequired(true);
        options.addOption(second);

        try {
            new PosixParser().parse(options, new String[] { "-f" });
            fail("Expected MissingOptionException");
        } catch (MissingOptionException expected) {
            assertEquals("Missing required option: x", expected.getMessage());
        }
    }

    public void testRequiredOptionIsAcceptedWhenPresent() throws Exception {
        Options options = new Options();
        Option option = new Option("f", false, "flag");
        option.setRequired(true);
        options.addOption(option);

        CommandLine commandLine = new PosixParser().parse(options, new String[] { "-f" });

        assertTrue(commandLine.hasOption("f"));
    }

    public void testNoArgumentsAreAcceptedWhenThereAreNoRequiredOptions() throws Exception {
        Options options = new Options();

        CommandLine commandLine = new PosixParser().parse(options, null);

        assertEquals(0, commandLine.getArgs().length);
    }

    public void testRequiredOptionWithoutRequiredArgumentThrowsMissingArgumentException() throws Exception {
        Options options = new Options();
        Option option = new Option("f", true, "file");
        option.setRequired(true);
        options.addOption(option);

        try {
            new PosixParser().parse(options, new String[] { "-f" });
            fail("Expected MissingArgumentException");
        } catch (MissingArgumentException expected) {
            assertEquals("Missing argument for option:f", expected.getMessage());
        }
    }

    public void testUnrecognizedOptionThrowsUnrecognizedOptionException() throws Exception {
        Options options = new Options();

        try {
            new PosixParser().parse(options, new String[] { "-z" });
            fail("Expected UnrecognizedOptionException");
        } catch (UnrecognizedOptionException expected) {
            assertEquals("Unrecognized option: -z", expected.getMessage());
        }
    }
}