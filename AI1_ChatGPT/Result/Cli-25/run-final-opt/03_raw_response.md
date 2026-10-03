package org.apache.commons.cli.bug;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.commons.cli.HelpFormatter;

public class HelpFormatterCLI162Test extends TestCase {

    public void testLongUnbrokenTextIsChunkedAndContinuationIsIndented() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setNewLine("\n");

        StringWriter output = new StringWriter();
        PrintWriter writer = new PrintWriter(output);

        formatter.printWrapped(writer, 10, 3, "word abcdefghijklmnopqrst");
        writer.flush();

        assertEquals("word\n   abcdefg\n   hijklmn\n   opqrst"
                + System.getProperty("line.separator"), output.toString());
    }

    public void testLongArgumentAfterInitialWordUsesWidthIncludingIndentation() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setNewLine("\n");

        StringWriter output = new StringWriter();
        PrintWriter writer = new PrintWriter(output);

        formatter.printWrapped(writer, 10, 3, "word 1234567890123 end");
        writer.flush();

        assertEquals("word\n   1234567\n   890123\n   end"
                + System.getProperty("line.separator"), output.toString());
    }

    public void testExactWidthAndOneCharacterOverWidthHaveExpectedChunks() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setNewLine("\n");

        StringWriter exactOutput = new StringWriter();
        formatter.printWrapped(new PrintWriter(exactOutput), 10, 2, "0123456789");

        StringWriter overOutput = new StringWriter();
        formatter.printWrapped(new PrintWriter(overOutput), 10, 2, "word 0123456789X");

        assertEquals("0123456789" + System.getProperty("line.separator"),
                exactOutput.toString());
        assertEquals("word\n  01234567\n  89X" + System.getProperty("line.separator"),
                overOutput.toString());
    }

    public void testWhitespaceAtWrapBoundaryIsPreferredOverSplittingWord() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setNewLine("\n");

        StringWriter output = new StringWriter();
        PrintWriter writer = new PrintWriter(output);

        formatter.printWrapped(writer, 10, 0, "alpha beta gamma");
        writer.flush();

        assertEquals("alpha beta\ngamma" + System.getProperty("line.separator"),
                output.toString());
    }
}