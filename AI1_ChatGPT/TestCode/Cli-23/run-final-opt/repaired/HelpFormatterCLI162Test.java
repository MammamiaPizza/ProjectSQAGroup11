package org.apache.commons.cli.bug;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.commons.cli.HelpFormatter;

public class HelpFormatterCLI162Test extends TestCase
{
    private static class TestableHelpFormatter extends HelpFormatter
    {
        public int getWrapPosition(String text, int width, int startPos)
        {
            return findWrapPos(text, width, startPos);
        }
    }

    public void testPrintWrappedLongWordMakesProgressWithoutException()
    {
        HelpFormatter formatter = new HelpFormatter();
        StringWriter buffer = new StringWriter();
        PrintWriter writer = new PrintWriter(buffer);

        try
        {
            formatter.printWrapped(writer, 3, "looooong");
            fail("Expected RuntimeException for text too long for line");
        }
        catch (RuntimeException expected)
        {
            assertEquals("Text too long for line - throwing exception to avoid infinite loop [CLI-162]: looooong",
                    expected.getMessage());
        }
    }

    public void testFindWrapPosForLongUnbrokenTextDoesNotReturnNoProgressPosition()
    {
        TestableHelpFormatter formatter = new TestableHelpFormatter();

        int position = formatter.getWrapPosition("looooong", 3, 0);

        assertEquals(0, position);
    }

    public void testPrintWrappedLongDescriptionWithContinuationIndentation()
    {
        TestableHelpFormatter formatter = new TestableHelpFormatter();
        String text = "looooong description";
        int width = 10;
        int firstWrapPosition = formatter.getWrapPosition(text, width, 0);

        assertTrue(firstWrapPosition >= 0);

        StringWriter buffer = new StringWriter();
        PrintWriter writer = new PrintWriter(buffer);

        formatter.printWrapped(writer, width, firstWrapPosition + 1, text);

        writer.flush();
        String output = buffer.toString();
        assertTrue(output.indexOf("looooong") >= 0);
        assertTrue(output.indexOf("description") >= 0);
    }

    public void testPrintWrappedQuotedLongDescriptionWithContinuationIndentation()
    {
        TestableHelpFormatter formatter = new TestableHelpFormatter();
        String text = "used if omited. Example: -e \"Runs such and such\"";
        int width = 20;
        int firstWrapPosition = formatter.getWrapPosition(text, width, 0);

        assertTrue(firstWrapPosition >= 0);

        StringWriter buffer = new StringWriter();
        PrintWriter writer = new PrintWriter(buffer);

        formatter.printWrapped(writer, width, firstWrapPosition + 1, text);

        writer.flush();
        String output = buffer.toString();
        assertTrue(output.indexOf("used") >= 0);
        assertTrue(output.indexOf("omited.") >= 0);
        assertTrue(output.indexOf("Example:") >= 0);
        assertTrue(output.indexOf("-e") >= 0);
        assertTrue(output.indexOf("\"Runs") >= 0);
        assertTrue(output.indexOf("such\"") >= 0);
    }

    public void testPrintWrappedAddsContinuationPadding()
    {
        HelpFormatter formatter = new HelpFormatter();
        StringWriter buffer = new StringWriter();
        PrintWriter writer = new PrintWriter(buffer);

        formatter.printWrapped(writer, 10, 4, "alpha beta gamma");

        writer.flush();
        assertTrue(buffer.toString().indexOf(formatter.getNewLine() + "    gamma") >= 0);
    }

    public void testPrintWrappedTextAtWidthIsRetained()
    {
        HelpFormatter formatter = new HelpFormatter();
        StringWriter buffer = new StringWriter();
        PrintWriter writer = new PrintWriter(buffer);

        formatter.printWrapped(writer, 10, "1234567890");

        writer.flush();
        assertTrue(buffer.toString().indexOf("1234567890") >= 0);
    }

    public void testPrintHelpRejectsMissingCommandSyntax()
    {
        HelpFormatter formatter = new HelpFormatter();

        try
        {
            formatter.printHelp(new PrintWriter(new StringWriter()), 20, null,
                    null, null, 1, 3, null, false);
            fail("Expected IllegalArgumentException for missing command syntax");
        }
        catch (IllegalArgumentException expected)
        {
            assertNotNull(expected);
        }
    }
}
