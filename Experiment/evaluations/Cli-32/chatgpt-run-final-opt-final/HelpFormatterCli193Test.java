package org.apache.commons.cli;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

public class HelpFormatterCli193Test extends TestCase
{
    public void testFindWrapPosReturnsWhitespaceBeforeRequestedWidth()
    {
        HelpFormatter formatter = new HelpFormatter();

        assertEquals(3, formatter.findWrapPos("abc def", 4, 0));
    }

    public void testFindWrapPosReturnsExactWhitespaceBoundary()
    {
        HelpFormatter formatter = new HelpFormatter();

        assertEquals(3, formatter.findWrapPos("abc def", 3, 0));
    }

    public void testFindWrapPosReturnsMinusOneWhenTextExactlyFitsWidth()
    {
        HelpFormatter formatter = new HelpFormatter();

        assertEquals(-1, formatter.findWrapPos("abcdefghijkl", 12, 0));
    }

    public void testPrintWrappedHandlesFinalWordExactlyAsWideAsLine()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setNewLine("\n");
        StringWriter output = new StringWriter();

        formatter.printWrapped(new PrintWriter(output), 12,
                "abcdefghijkl abcdefghijkl");

        assertEquals("abcdefghijkl\nabcdefghijkl"
                + System.getProperty("line.separator"), output.toString());
    }

    public void testPrintWrappedUsesContinuationIndentationAtBoundary()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setNewLine("\n");
        StringWriter output = new StringWriter();

        formatter.printWrapped(new PrintWriter(output), 7, 2, "one two three");

        assertEquals("one two\n  three"
                + System.getProperty("line.separator"), output.toString());
    }
}
