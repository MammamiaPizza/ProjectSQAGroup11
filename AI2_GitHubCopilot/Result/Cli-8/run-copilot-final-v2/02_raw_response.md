package org.apache.commons.cli;

 import junit.framework.TestCase;
 import java.io.PrintWriter;
 import java.io.StringWriter;

 /**
  * Tests for HelpFormatter wrapping behavior, targeting the CLI-151 bug
  * where words (like "DATE") could be incorrectly split at wrap boundaries.
  */
 public class HelpFormatterTest extends TestCase {

     private HelpFormatter formatter;

     public void setUp() {
         formatter = new HelpFormatter();
     }

     // Helper to capture printWrapped output
     private String getWrappedOutput(int width, int nextLineTabStop, String text) {
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         formatter.printWrapped(pw, width, nextLineTabStop, text);
         pw.flush();
         return sw.toString();
     }

     private String getWrappedOutput(int width, String text) {
         return getWrappedOutput(width, 0, text);
     }

     // --- printWrapped tests ---

     public void testPrintWrappedShortText() {
         String result = getWrappedOutput(80, "short text");
         assertEquals("short text" + formatter.getNewLine(), result);
     }

     public void testPrintWrappedExactWidth() {
         String text = "1234567890";
         String result = getWrappedOutput(text.length(), text);
         // Should fit exactly, no line break
         assertEquals(text + formatter.getNewLine(), result);
     }

     public void testPrintWrappedWordAtBoundary() {
         // The word "DATE" must not be split when it occurs exactly at the wrap boundary.
         // Width is chosen so that the space before "DATE" is just beyond the width,
         // forcing a wrap before "DATE" (not inside it).
         String text = "This is a test: -DATE where DATE is important";
         int width = 18;
         String result = getWrappedOutput(width, text);
         // Verify that "DATE" appears intact on a new line
         String[] lines = result.split(formatter.getNewLine());
         for (int i = 0; i < lines.length; i++) {
             // Each line should not have a partial "DATE" fragment
             if (lines[i].contains("DATE")) {
                 assertTrue("Line " + i + " should contain full DATE, not split: " + lines[i],
                         lines[i].contains("DATE"));
             }
             // "DATE" must not be split across lines; that would mean "DA" on one line and "TE" on
another.
         }
         // Additional explicit check: no line ends with "DA" followed by a line starting with "TE"
         for (int i = 0; i < lines.length - 1; i++) {
             String current = lines[i].trim();
             String next = lines[i+1].trim();
             if (current.endsWith("DA") && next.startsWith("TE")) {
                 fail("Word DATE was split: '" + current + "' / '" + next + "'");
             }
         }
     }

     public void testPrintWrappedWithPadding() {
         // Padding should not cause word splitting. The test case from the bug report.
         String text = "This is a test: -DATE where DATE is the date";
         int width = 20;
         int nextLineTabStop = 3;
         String result = getWrappedOutput(width, nextLineTabStop, text);
         String[] lines = result.split(formatter.getNewLine());
         // Check that "-DATE" is never broken
         for (String line : lines) {
             if (line.contains("-")) {
                 assertTrue("Hyphenated token -DATE should stay intact: " + line,
                         line.contains("-DATE"));
             }
         }
         // Explicitly ensure no line contains a split of DATE
         for (int i = 0; i < lines.length - 1; i++) {
             String current = lines[i].trim();
             String next = lines[i+1].trim();
             if ((current.endsWith("-") || current.endsWith("-D")) && next.startsWith("DATE")) {
                 fail("Hyphenated token was split: '" + current + "' / '" + next + "'");
             }
         }
     }

     public void testPrintWrappedVeryLongToken() {
         // Token longer than width cannot be split; it should be printed on its own line.
         String longToken = "SuperCaliFragilisticExpialiDocious";
         String prefix = "Before ";
         String text = prefix + longToken + " after";
         int width = 15;
         String result = getWrappedOutput(width, text);
         String[] lines = result.split(formatter.getNewLine());
         boolean foundLongTokenLine = false;
         for (String line : lines) {
             if (line.trim().equals(longToken)) {
                 foundLongTokenLine = true;
             }
             // The long token should not be split into pieces
             if (line.trim().startsWith("Super") && line.trim().length() < longToken.length()) {
                 fail("Long token was split: " + line);
             }
         }
         assertTrue("Long token should appear on its own line", foundLongTokenLine);
     }

     public void testPrintWrappedMultipleSpaces() {
         // Multiple consecutive spaces should be handled without breaking words
         String text = "word1     word2    word3";
         int width = 10;
         String result = getWrappedOutput(width, text);
         String[] lines = result.split(formatter.getNewLine());
         for (String line : lines) {
             assertFalse("Line should not contain multiple spaces (trimmed): " + line,
                     line.contains("  "));
         }
         // All words should be intact
         assertTrue(result.contains("word1"));
         assertTrue(result.contains("word2"));
         assertTrue(result.contains("word3"));
     }

     public void testPrintWrappedEmptyText() {
         String result = getWrappedOutput(10, "");
         assertEquals(formatter.getNewLine(), result);
     }

     // --- findWrapPos tests (via subclass to access protected method) ---

     private class AccessibleHelpFormatter extends HelpFormatter {
         public int publicFindWrapPos(String text, int width, int startPos) {
             return findWrapPos(text, width, startPos);
         }
     }

     private AccessibleHelpFormatter accessible = new AccessibleHelpFormatter();

     public void testFindWrapPosNoWrapNeeded() {
         // text fits entirely within width, should return -1
         int pos = accessible.publicFindWrapPos("short", 80, 0);
         assertEquals(-1, pos);
     }

     public void testFindWrapPosAtSpaceBeforeWord() {
         // width exactly at the space before a word; wrap should happen at that space
         String text = "hello world";
         int width = 6; // covers "hello " (space at index 5)
         int pos = accessible.publicFindWrapPos(text, width, 0);
         assertEquals(5, pos); // the space position
     }

     public void testFindWrapPosWithHyphen() {
         // Hyphen should not be a wrap point; findWrapPos must not split at hyphen.
         // If the hyphen falls at width and no space is available before it,
         // the algorithm should look for space after the hyphen.
         String text = "abc-def ghi";
         int width = 6; // covers "abc-de" – hyphen at index 3
         int pos = accessible.publicFindWrapPos(text, width, 0);
         // The hyphen is not a space, so the backward search should continue.
         // Since there's no space before width (except maybe at 0?), it should go forward.
         // Expected: space at index 7 (after "def")? Actually text is "abc-def ghi": indices:
         // 0 a,1 b,2 c,3 -,4 d,5 e,6 f,7 space,8 g,9 h,10 i
         // width=6 -> startPos+width=6, charAt(6)='f', backward: 6 f,5 e,4 d,3 -,2 c,1 b,0 a -> no
space.
         // Then forward from 6: f(6), space(7) -> returns 7.
         assertEquals(7, pos);
     }

     public void testFindWrapPosWithStartPos() {
         // When startPos > 0 (simulating padding), the window is shifted.
         String text = "     hello world"; // 5 spaces padding + "hello world"
         int width = 10;
         int startPos = 5; // skip padding
         int pos = accessible.publicFindWrapPos(text, width, startPos);
         // The window is positions 5 to 15. "hello world" from index 5: h(5)e(6)l(7)l(8)o(9)
(10)w(11)...
         // width=10 -> pos=15, charAt(15) is out of bounds? text length 5+11=16 -> charAt(15) = 'd'
(last char "world" -> w(11)o(12)r(13)l(14)d(15))
         // Backward from 15: d(15), l(14), r(13), o(12), w(11), space(10) -> returns 10.
         assertEquals(10, pos);
     }

     // --- rtrim test ---

     public void testRtrim() {
         assertEquals("hello", formatter.rtrim("hello   "));
         assertEquals("hello", formatter.rtrim("hello"));
         assertEquals("", formatter.rtrim("   "));
         assertNull(formatter.rtrim(null));
     }
 }