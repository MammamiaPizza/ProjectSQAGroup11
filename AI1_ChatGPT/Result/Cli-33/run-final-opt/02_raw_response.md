package org.apache.commons.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

public class HelpFormatterCli33Test extends TestCase
{
    private static class ExposedHelpFormatter extends HelpFormatter
    {
        public int wrapPosition(String text, int width, int startPos)
        {
            return findWrapPos(text, width, startPos);
        }
    }

    private String eol()
    {
        return System.getProperty("line.separator");
    }

    public void testIndentedHeaderAndFooterArePreservedByPublicPrintHelpOverload()
    {
        HelpFormatter formatter = new HelpFormatter();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream original = System.out;

        try
        {
            System.setOut(new PrintStream(bytes));
            formatter.printHelp("foobar", "   header", new Options(), "   footer");
        }
        finally
        {
            System.out.flush();
            System.setOut(original);
        }

        assertEquals("usage: foobar" + eol()
                + "   header" + eol()
                + eol()
                + "   footer" + eol(), bytes.toString());
    }

    public void testPrintWrappedKeepsInitialAndContinuationIndentation()
    {
        HelpFormatter formatter = new HelpFormatter();
        StringWriter output = new StringWriter();
        PrintWriter writer = new PrintWriter(output);

        formatter.printWrapped(writer, 12, 3, "   alpha beta gamma");
        writer.flush();

        assertEquals("   alpha" + eol()
                + "   beta" + eol()
                + "   gamma" + eol(), output.toString());
    }

    public void testPrintWrappedUsesConfiguredNewLineBetweenWrappedLines()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setNewLine("|");
        StringWriter output = new StringWriter();
        PrintWriter writer = new PrintWriter(output);

        formatter.printWrapped(writer, 7, 2, "one two three");
        writer.flush();

        assertEquals("one two|  three" + eol(), output.toString());
    }

    public void testFindWrapPosFindsWhitespaceAndRecognizesTextAtBoundary()
    {
        ExposedHelpFormatter formatter = new ExposedHelpFormatter();

        assertEquals(3, formatter.wrapPosition("abc def", 4, 0));
        assertEquals(-1, formatter.wrapPosition("abc def", 7, 0));
    }

    public void testPrintHelpRejectsMissingCommandSyntax()
    {
        HelpFormatter formatter = new HelpFormatter();
        PrintWriter writer = new PrintWriter(new StringWriter());

        try
        {
            formatter.printHelp(writer, 80, "", null, new Options(), 1, 3, null);
            fail("An empty command syntax must be rejected");
        }
        catch (IllegalArgumentException expected)
        {
            assertEquals("cmdLineSyntax not provided", expected.getMessage());
        }
    }

    public void testAutoUsageIncludesDeclaredOptionAndDescription()
    {
        HelpFormatter formatter = new HelpFormatter();
        Options options = new Options();
        options.addOption(new Option("a", "all", false, "include all files"));
        StringWriter output = new StringWriter();
        PrintWriter writer = new PrintWriter(output);

        formatter.printHelp(writer, 80, "app", null, options, 1, 3, null, true);
        writer.flush();

        String help = output.toString();
        assertTrue(help.indexOf("usage: app") >= 0);
        assertTrue(help.indexOf("-a") >= 0);
        assertTrue(help.indexOf("include all files") >= 0);
    }
}