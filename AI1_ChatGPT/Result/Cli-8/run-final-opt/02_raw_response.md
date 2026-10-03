package org.apache.commons.cli;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

public class HelpFormatterCli8Test extends TestCase {

    private static class ExposedHelpFormatter extends HelpFormatter {
        public int wrapPosition(String text, int width, int startPos) {
            return findWrapPos(text, width, startPos);
        }
    }

    private String printWrapped(HelpFormatter formatter, int width, String text) {
        StringWriter output = new StringWriter();
        PrintWriter writer = new PrintWriter(output);
        formatter.printWrapped(writer, width, text);
        writer.flush();
        return output.toString();
    }

    private String printWrapped(HelpFormatter formatter, int width,
                                int nextLineTabStop, String text) {
        StringWriter output = new StringWriter();
        PrintWriter writer = new PrintWriter(output);
        formatter.printWrapped(writer, width, nextLineTabStop, text);
        writer.flush();
        return output.toString();
    }

    public void testPrintWrappedWrapsAtSpacesAndPadsFollowingLines() {
        HelpFormatter formatter = new HelpFormatter();
        String nl = System.getProperty("line.separator");

        assertEquals("This is a" + nl + "    test." + nl,
                printWrapped(formatter, 12, 4, "This is a test."));
    }

    public void testPrintWrappedTreatsTabAsBreakOnPaddedLine() {
        HelpFormatter formatter = new HelpFormatter();
        String nl = System.getProperty("line.separator");

        assertEquals("one two" + nl
                        + "    three" + nl
                        + "    four five" + nl,
                printWrapped(formatter, 8, 4, "one two three\tfour five"));
    }

    public void testFindWrapPosRecognizesTabWithinPaddedLineWidth() {
        ExposedHelpFormatter formatter = new ExposedHelpFormatter();

        assertEquals(10, formatter.wrapPosition("    three\tfour five", 8, 4));
    }

    public void testPrintWrappedDoesNotWrapTextThatExactlyFits() {
        HelpFormatter formatter = new HelpFormatter();
        String nl = System.getProperty("line.separator");

        assertEquals("one two" + nl, printWrapped(formatter, 7, "one two"));
    }

    public void testPrintWrappedLeavesLongWordIntactWhenNoBreakExists() {
        HelpFormatter formatter = new HelpFormatter();
        String nl = System.getProperty("line.separator");

        assertEquals("abcdefghijkl" + nl,
                printWrapped(formatter, 4, "abcdefghijkl"));
    }

    public void testPrintWrappedTrimsTrailingSpacesBeforeOutput() {
        HelpFormatter formatter = new HelpFormatter();
        String nl = System.getProperty("line.separator");

        assertEquals("text" + nl, printWrapped(formatter, 20, "text   "));
    }
}