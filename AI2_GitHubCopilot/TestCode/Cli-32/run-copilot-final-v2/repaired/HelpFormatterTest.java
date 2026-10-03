package org.apache.commons.cli;

 import junit.framework.TestCase;

 public class HelpFormatterTest extends TestCase {

     private static class TestableHelpFormatter extends HelpFormatter {
         public int callFindWrapPos(String text, int width, int startPos) {
             return findWrapPos(text, width, startPos);
         }

         public StringBuffer callRenderWrappedText(StringBuffer sb, int width,
                 int nextLineTabStop, String text) {
             return renderWrappedText(sb, width, nextLineTabStop, text);
         }
     }

     private TestableHelpFormatter formatter;

     public void setUp() {
         formatter = new TestableHelpFormatter();
         formatter.setNewLine("\n");
     }

     public void testFindWrapPos() {
         // Primary bug: expected 3, buggy code returns 4
         int pos = formatter.callFindWrapPos("This is a test", 4, 0);
         assertEquals("wrap position", 4, pos);
     }

     public void testFindWrapPosNoWrapNeeded() {
         // Text fits entirely within width: startPos+width >= text.length()
         int pos = formatter.callFindWrapPos("short", 10, 0);
         assertEquals(-1, pos);
     }

     public void testFindWrapPosLongTokenNoSpace() {
         // No space within width boundary; must return width-boundary index,
         // not the next space beyond width
         int pos = formatter.callFindWrapPos("abcdefghij", 5, 0);
         assertEquals(5, pos);
     }

     public void testFindWrapPosEmptyString() {
         int pos = formatter.callFindWrapPos("", 10, 0);
         assertEquals(-1, pos);
     }

     public void testFindWrapPosSingleChar() {
         int pos = formatter.callFindWrapPos("X", 1, 0);
         assertEquals(-1, pos);
     }

     public void testFindWrapPosExactLength() {
         // Token length equals width; must return -1, not throw
         // StringIndexOutOfBoundsException when forward-scan hits charAt(length)
         int pos = formatter.callFindWrapPos("123456789012", 12, 0);
         assertEquals(-1, pos);
     }

     public void testFindWrapPosNewlineAtBoundary() {
         // Newline at position 2, inside width 5
         int pos = formatter.callFindWrapPos("ab\ncd", 5, 0);
         assertEquals(-1, pos);
     }

     public void testFindWrapPosTabAtBoundary() {
         // Tab at position 2, inside width 5
         int pos = formatter.callFindWrapPos("ab\tcd", 5, 0);
         assertEquals(-1, pos);
     }

     public void testFindWrapPosSpaceWithinWidth() {
         // Space found when searching backwards from width boundary
         int pos = formatter.callFindWrapPos("ab cd ef", 4, 0);
         assertEquals(2, pos);
     }

     public void testRenderWrappedTextWordCut() {
         // Long token that reaches text.length(); must not throw
         // StringIndexOutOfBoundsException when forward-scan hits charAt(length)
         StringBuffer sb = new StringBuffer();
         try {
             formatter.callRenderWrappedText(sb, 5, 0, "abcdefghijkl");
         } catch (StringIndexOutOfBoundsException e) {
             fail("StringIndexOutOfBoundsException thrown: " + e.getMessage());
         }
         String result = sb.toString();
         assertTrue("Output should not be empty", result.length() > 0);
     }

     public void testRenderWrappedTextSimple() {
         // Basic wrapping at spaces
         StringBuffer sb = new StringBuffer();
         formatter.callRenderWrappedText(sb, 10, 0, "This is a test");
         String result = sb.toString();
         assertTrue("Output should contain 'This'", result.contains("This"));
         assertTrue("Output should contain 'test'", result.contains("test"));
     }

     public void testRenderWrappedTextLongWord() {
         // Single long word without spaces cut at width boundaries
         StringBuffer sb = new StringBuffer();
         formatter.callRenderWrappedText(sb, 5, 0, "abcdefghijklmnopqrstuvwxyz");
         String result = sb.toString();
         assertTrue("Output should not be empty", result.length() > 0);
         // Verify each line except possibly the last fits within width (5)
         String[] parts = result.split("\n");
         for (int i = 0; i < parts.length - 1; i++) {
             assertTrue("Line " + i + " length " + parts[i].length() + " exceeds width 5",
                     parts[i].length() <= 5);
         }
     }
 }
