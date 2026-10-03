package org.apache.commons.cli;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

public class HelpFormatterDefaultArgNameRegressionTest extends TestCase {

    public void testConfiguredDefaultArgNameIsUsedForOptionWithoutExplicitName() {
        Option option = new Option("f", true, "file");
        assertNull(option.getArgName());

        assertEquals("usage: app -f <argument>" + System.getProperty("line.separator"),
                renderUsage(option, "argument"));
    }

    public void testExplicitOptionArgNameOverridesFormatterDefault() {
        Option option = new Option("f", true, "file");
        option.setArgName("file");

        assertEquals("file", option.getArgName());
        assertEquals("usage: app -f <file>" + System.getProperty("line.separator"),
                renderUsage(option, "argument"));
    }

    public void testBuilderHasArgLeavesArgNameUnsetForFormatterDefault() {
        OptionBuilder.hasArg();
        Option option = OptionBuilder.create("f");

        assertNull(option.getArgName());
        assertEquals("usage: app -f <argument>" + System.getProperty("line.separator"),
                renderUsage(option, "argument"));
    }

    public void testBuilderWithArgNameTransfersExplicitName() {
        OptionBuilder.withArgName("file");
        OptionBuilder.hasArg();
        Option option = OptionBuilder.create("f");

        assertEquals("file", option.getArgName());
        assertEquals("usage: app -f <file>" + System.getProperty("line.separator"),
                renderUsage(option, "argument"));
    }

    public void testBuilderResetDoesNotRetainArgNameAfterCreate() {
        OptionBuilder.withArgName("first");
        OptionBuilder.hasArg();
        OptionBuilder.create("a");

        OptionBuilder.hasArg();
        Option option = OptionBuilder.create("b");

        assertNull(option.getArgName());
        assertEquals("usage: app -b <argument>" + System.getProperty("line.separator"),
                renderUsage(option, "argument"));
    }

    public void testNoArgumentOptionDoesNotRenderArgumentPlaceholder() {
        Option option = new Option("v", false, "verbose");

        assertFalse(option.hasArg());
        assertEquals("usage: app -v" + System.getProperty("line.separator"),
                renderUsage(option, "argument"));
    }

    public void testInvalidEmptyOptionNameIsRejected() {
        try {
            new Option("", true, "invalid");
            fail("An empty option name must be rejected");
        } catch (IllegalArgumentException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    private String renderUsage(Option option, String defaultArgName) {
        option.setRequired(true);

        Options options = new Options();
        options.addOption(option);

        HelpFormatter formatter = new HelpFormatter();
        formatter.defaultArgName = defaultArgName;

        StringWriter output = new StringWriter();
        PrintWriter writer = new PrintWriter(output);
        formatter.printUsage(writer, 80, "app", options);
        writer.flush();

        return output.toString();
    }
}