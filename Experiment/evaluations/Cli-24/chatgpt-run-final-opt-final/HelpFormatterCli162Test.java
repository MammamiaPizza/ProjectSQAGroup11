package org.apache.commons.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import junit.framework.TestCase;

public class HelpFormatterCli162Test extends TestCase {

    private static class ExposedHelpFormatter extends HelpFormatter {
        public int wrapPosition(String text, int width, int startPos) {
            return findWrapPos(text, width, startPos);
        }
    }

    public void testLongLineChunkingIgnoresIndentWhenIndentEqualsWidth() {
        String text = "alpha beta gamma delta epsilon";
        String output = printWrapped(10, 10, text);

        assertEquals(compact(text), compact(output));
        assertNoBlankOrOverwideLines(output, 10);
    }

    public void testLongLineChunkingIgnoresIndentWhenIndentExceedsWidth() {
        String text = "one two three four five six";
        String output = printWrapped(8, 12, text);

        assertEquals(compact(text), compact(output));
        assertNoBlankOrOverwideLines(output, 8);
    }

    public void testLongLineChunkingWorksWhenOnlyOneColumnRemainsAfterIndent() {
        String text = "one two three four";
        String output = printWrapped(9, 8, text);

        assertEquals(compact(text), compact(output));
        assertNoBlankOrOverwideLines(output, 9);
    }

    public void testWrappedContinuationUsesConfiguredIndentWhenThereIsRoom() {
        String output = printWrapped(10, 3, "one two three four");

        assertEquals("one two\n   three\n   four\n", normalizeNewLines(output));
    }

    public void testFindWrapPosUsesWhitespaceBeforeWidth() {
        ExposedHelpFormatter formatter = new ExposedHelpFormatter();

        assertEquals(3, formatter.wrapPosition("abc def", 5, 0));
    }

    public void testFindWrapPosReturnsPositionAfterNewline() {
        ExposedHelpFormatter formatter = new ExposedHelpFormatter();

        assertEquals(4, formatter.wrapPosition("abc\ndef", 3, 0));
    }

    public void testPrintHelpRejectsMissingCommandSyntax() {
        HelpFormatter formatter = new HelpFormatter();

        try {
            formatter.printHelp(new PrintWriter(new StringWriter()), 20, null,
                    null, new Options(), 1, 3, null, false);
            fail("Expected IllegalArgumentException for missing command syntax");
        } catch (IllegalArgumentException expected) {
            assertEquals("cmdLineSyntax not provided", expected.getMessage());
        }
    }

    private String printWrapped(int width, int tabStop, String text) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setNewLine("\n");

        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        formatter.printWrapped(printWriter, width, tabStop, text);
        printWriter.flush();

        return normalizeNewLines(writer.toString());
    }

    private void assertNoBlankOrOverwideLines(String output, int width) {
        assertFalse("Wrapping must not create empty continuation lines",
                output.indexOf("\n\n") >= 0);

        StringTokenizer lines = new StringTokenizer(output, "\n");
        while (lines.hasMoreTokens()) {
            String line = lines.nextToken();
            assertTrue("Wrapped line exceeds configured width: [" + line + "]",
                    line.length() <= width);
            assertTrue("Wrapped line must contain text",
                    line.trim().length() > 0);
        }
    }

    private String compact(String text) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (!Character.isWhitespace(character)) {
                result.append(character);
            }
        }
        return result.toString();
    }

    private String normalizeNewLines(String text) {
        return text.replace("\r\n", "\n").replace('\r', '\n');
    }
}
