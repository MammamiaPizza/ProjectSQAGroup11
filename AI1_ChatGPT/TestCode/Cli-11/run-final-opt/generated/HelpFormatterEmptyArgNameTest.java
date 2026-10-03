package org.apache.commons.cli;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

public class HelpFormatterEmptyArgNameTest extends TestCase {

    public void testPrintHelpUsageOmitsPlaceholderForEmptyArgumentName() {
        HelpFormatter formatter = new HelpFormatter();
        Options options = new Options();
        Option option = new Option("f", true, "input file");
        option.setArgName("");
        options.addOption(option);

        StringWriter output = new StringWriter();
        PrintWriter writer = new PrintWriter(output);
        formatter.printHelp(writer, 80, "app", null, options, 1, 3, null, true);
        writer.flush();

        assertEquals("usage: app -f", firstLine(output.toString(), formatter.getNewLine()));
    }

    public void testPrintUsageIncludesNonEmptyArgumentName() {
        HelpFormatter formatter = new HelpFormatter();
        Options options = new Options();
        Option option = new Option("f", true, "input file");
        option.setArgName("file");
        options.addOption(option);

        StringWriter output = new StringWriter();
        PrintWriter writer = new PrintWriter(output);
        formatter.printUsage(writer, 80, "app", options);
        writer.flush();

        assertEquals("usage: app -f <file>", firstLine(output.toString(), formatter.getNewLine()));
    }

    public void testPrintHelpRejectsEmptyCommandSyntax() {
        HelpFormatter formatter = new HelpFormatter();

        try {
            formatter.printHelp(new PrintWriter(new StringWriter()), 80, "",
                    null, new Options(), 1, 3, null, true);
            fail("Expected IllegalArgumentException for an empty command syntax");
        } catch (IllegalArgumentException expected) {
            assertEquals("cmdLineSyntax not provided", expected.getMessage());
        }
    }

    private String firstLine(String text, String newline) {
        int index = text.indexOf(newline);
        return index < 0 ? text : text.substring(0, index);
    }
}
