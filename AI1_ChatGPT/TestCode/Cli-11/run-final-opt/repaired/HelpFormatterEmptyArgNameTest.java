package org.apache.commons.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

public class HelpFormatterEmptyArgNameTest extends TestCase {

    public void testPrintHelpUsageOmitsPlaceholderForEmptyArgumentName() {
        HelpFormatter formatter = new HelpFormatter();
        Options options = new Options();
        Option option = new Option("f", true, "input file");
        option.setRequired(true);
        option.setArgName("");
        options.addOption(option);

        assertEquals("usage: app -f",
                firstLine(printHelp(formatter, "app", options), formatter.getNewLine()));
    }

    public void testPrintUsageIncludesNonEmptyArgumentName() {
        HelpFormatter formatter = new HelpFormatter();
        Options options = new Options();
        Option option = new Option("f", true, "input file");
        option.setRequired(true);
        option.setArgName("file");
        options.addOption(option);

        assertEquals("usage: app -f <file>",
                firstLine(printHelp(formatter, "app", options), formatter.getNewLine()));
    }

    public void testPrintHelpRejectsEmptyCommandSyntax() {
        HelpFormatter formatter = new HelpFormatter();

        try {
            formatter.printHelp("", new Options());
            fail("Expected IllegalArgumentException for an empty command syntax");
        } catch (IllegalArgumentException expected) {
            assertEquals("cmdLineSyntax not provided", expected.getMessage());
        }
    }

    private String printHelp(HelpFormatter formatter, String cmdLineSyntax, Options options) {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream redirected = new PrintStream(output);

        try {
            System.setOut(redirected);
            formatter.printHelp(cmdLineSyntax, options);
        } finally {
            redirected.flush();
            System.setOut(original);
            redirected.close();
        }

        return output.toString();
    }

    private String firstLine(String text, String newline) {
        int index = text.indexOf(newline);
        return index < 0 ? text : text.substring(0, index);
    }
}
