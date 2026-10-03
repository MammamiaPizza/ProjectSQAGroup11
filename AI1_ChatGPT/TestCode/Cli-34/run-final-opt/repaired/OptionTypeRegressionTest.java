package org.apache.commons.cli;

import junit.framework.TestCase;

public class OptionTypeRegressionTest extends TestCase {

    private void resetOptionBuilder() {
        OptionBuilder.withLongOpt("reset-option-builder").create();
    }

    public void testSetTypeIsReturnedByGetType() {
        Option option = new Option("f", true, "file");

        option.setType(String.class);

        assertSame(String.class, option.getType());
    }

    public void testBuilderTransfersTypeToCreatedOption() {
        resetOptionBuilder();

        Option option = OptionBuilder.withLongOpt("file")
                .hasArg()
                .withArgName("name")
                .withType(String.class)
                .withDescription("input file")
                .create("f");

        assertSame(String.class, option.getType());
        assertEquals("file", option.getLongOpt());
        assertTrue(option.hasArg());
    }

    public void testTwoConsecutivelyBuiltTypedOptionsRetainTheirTypes() {
        resetOptionBuilder();

        Option first = OptionBuilder.withLongOpt("first")
                .hasArg()
                .withType(String.class)
                .withDescription("first option")
                .create("a");

        Option second = OptionBuilder.withLongOpt("second")
                .hasArg()
                .withType(String.class)
                .withDescription("second option")
                .create("b");

        assertSame(String.class, first.getType());
        assertSame(String.class, second.getType());
    }

    public void testCreateRetainsTypeForFollowingOption() {
        resetOptionBuilder();

        Option typed = OptionBuilder.withLongOpt("typed")
                .hasArg()
                .withType(String.class)
                .create("t");

        Option following = OptionBuilder.withLongOpt("following")
                .hasArg()
                .create("u");

        assertSame(String.class, typed.getType());
        assertSame(String.class, following.getType());
    }

    public void testCreateWithoutLongOptionDoesNotResetBuilderState() {
        resetOptionBuilder();

        OptionBuilder.withType(Integer.class);
        try {
            OptionBuilder.create();
            fail("Creating an option without a long option should fail");
        } catch (IllegalArgumentException expected) {
            assertEquals("must specify longopt", expected.getMessage());
        }

        Option option = OptionBuilder.withLongOpt("after-failure").create("a");

        assertSame(Integer.class, option.getType());
    }

    public void testParsedStringOptionValueIsReturned() throws Exception {
        Option option = new Option("f", true, "file");
        option.setType(String.class);

        Options options = new Options();
        options.addOption(option);

        CommandLine commandLine = new PosixParser().parse(
                options, new String[] { "-f", "foo" });

        assertEquals("foo", commandLine.getParsedOptionValue("f"));
    }
}
