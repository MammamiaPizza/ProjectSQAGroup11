package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.SourceExcerptProvider;

 import junit.framework.TestCase;

 /**
  * Tests for {@link LightweightMessageFormatter} focusing on the bug where
  * trailing spaces in source excerpt lines were lost (Issue 487).
  */
 public class LightweightMessageFormatterTest extends TestCase {

   private static final String TEST_SOURCE_NAME = "test.js";

   /**
    * Creates a LightweightMessageFormatter backed by a mock provider that
    * returns the given sourceLine for any source/line combination.
    */
   private LightweightMessageFormatter createFormatterWithLine(final String sourceLine) {
     SourceExcerptProvider mockSource = new SourceExcerptProvider() {
       @Override
       public String getSourceLine(String source, int lineNumber) {
         return sourceLine;
       }

       @Override
       public Region getSourceRegion(String source, int lineNumber, int length) {
         return null;
       }
     };
     return new LightweightMessageFormatter(mockSource);
   }

   private JSError createError(String sourceName, int lineNumber, int charno, String description) {
     return new JSError(sourceName, lineNumber, charno, description);
   }

   // ================== Trailing space tests ==================

   /**
    * Single trailing space at end of line, charno points to that space.
    * Bug: trailing space should appear in source line and pointer line.
    */
   public void testFormatErrorSpaceEndOfLine1() {
     // Source line: "desc here " (trailing space)
     String sourceLine = "desc here ";
     int charno = 9; // index of the trailing space
     LightweightMessageFormatter formatter = createFormatterWithLine(sourceLine);
     JSError error = createError(TEST_SOURCE_NAME, 1, charno, "some error");

     String result = formatter.formatError(error);
     assertTrue("Output should contain source line with trailing space",
         result.contains("desc here \n"));
     // Pointer line: spaces until column 9, then ^
     // With trailing space preserved, pointer line should also have a trailing space
     assertTrue("Pointer line should contain trailing space",
         result.contains(" ^\n")); // but needs spaces before ^
     // More precisely, after the source line, there should be a line with 9 spaces and ^
     assertTrue(result.contains("         ^\n")); // 9 spaces then ^
     // But we want to verify the trailing space is part of source line, not stripped.
     // Check full expected pattern
     String expected = "test.js:1: ERROR - some error\n" +
                       "desc here \n" +
                       "         ^\n";
     assertEquals(expected, result);
   }

   public void testFormatErrorSpaceEndOfLine2() {
     // Source line: "desc here  " (two trailing spaces)
     String sourceLine = "desc here  ";
     int charno = 10; // second trailing space
     LightweightMessageFormatter formatter = createFormatterWithLine(sourceLine);
     JSError error = createError(TEST_SOURCE_NAME, 2, charno, "another error");

     String result = formatter.formatError(error);
     String expected = "test.js:2: ERROR - another error\n" +
                       "desc here  \n" +
                       "          ^\n";
     assertEquals(expected, result);
   }

   /**
    * Source line consists entirely of spaces; charno inside them.
    * Trailing spaces (which are all characters) must be preserved.
    */
   public void testOnlySpacesSourceLine() {
     String sourceLine = "   "; // 3 spaces
     int charno = 1;
     LightweightMessageFormatter formatter = createFormatterWithLine(sourceLine);
     JSError error = createError(TEST_SOURCE_NAME, 3, charno, "space error");

     String result = formatter.formatError(error);
     String expected = "test.js:3: ERROR - space error\n" +
                       "   \n" +
                       " ^\n"; // after one space
     assertEquals(expected, result);
   }

   /**
    * Source line with no trailing spaces - baseline to ensure no false positives.
    */
   public void testNoTrailingSpaces() {
     String sourceLine = "abc";
     int charno = 1;
     LightweightMessageFormatter formatter = createFormatterWithLine(sourceLine);
     JSError error = createError(TEST_SOURCE_NAME, 4, charno, "no trailing");

     String result = formatter.formatError(error);
     String expected = "test.js:4: ERROR - no trailing\n" +
                       "abc\n" +
                       " ^\n";
     assertEquals(expected, result);
   }

   // ================== Edge cases ==================

   public void testEmptySourceLine() {
     String sourceLine = "";
     int charno = 0; // charno == length, so no pointer line
     LightweightMessageFormatter formatter = createFormatterWithLine(sourceLine);
     JSError error = createError(TEST_SOURCE_NAME, 5, charno, "empty source");

     String result = formatter.formatError(error);
     // Source line is empty, arrow not printed because charno >= length
     String expected = "test.js:5: ERROR - empty source\n\n";
     assertEquals(expected, result);
   }

   public void testNegativeCharno() {
     String sourceLine = "some code";
     int charno = -1;
     LightweightMessageFormatter formatter = createFormatterWithLine(sourceLine);
     JSError error = createError(TEST_SOURCE_NAME, 6, charno, "negative charno");

     String result = formatter.formatError(error);
     // No arrow because charno < 0
     assertFalse(result.contains("^"));
     assertTrue(result.contains("some code\n"));
   }

   public void testCharnoOutOfRange() {
     String sourceLine = "abc";
     int charno = 10; // >= length
     LightweightMessageFormatter formatter = createFormatterWithLine(sourceLine);
     JSError error = createError(TEST_SOURCE_NAME, 7, charno, "out of range");

     String result = formatter.formatError(error);
     // No arrow
     assertFalse(result.contains("^"));
     assertTrue(result.contains("abc\n"));
   }

   /**
    * Null source name and zero/negative line number: no file prefix in output.
    */
   public void testNullSourceNameAndNoLine() {
     String sourceLine = "x";
     LightweightMessageFormatter formatter = createFormatterWithLine(sourceLine);
     JSError error = createError(null, -1, 0, "anonymous");

     String result = formatter.formatError(error);
     String expected = "ERROR - anonymous\nx\n^\n";
     assertEquals(expected, result);
   }

   /**
    * formatWarning should also preserve trailing spaces.
    */
   public void testFormatWarningTrailingSpace() {
     String sourceLine = "warn here ";
     int charno = 9;
     LightweightMessageFormatter formatter = createFormatterWithLine(sourceLine);
     JSError error = createError(TEST_SOURCE_NAME, 8, charno, "a warning");

     String result = formatter.formatWarning(error);
     String expected = "test.js:8: WARNING - a warning\n" +
                       "warn here \n" +
                       "         ^\n";
     assertEquals(expected, result);
   }

   /**
    * Tab character at end of line: tab should be preserved as-is in both
    * source line and pointer line (since it's whitespace and Character.isWhitespace returns true).
    */
   public void testTabAtEnd() {
     String sourceLine = "code\t";
     int charno = 4; // pointing to the tab
     LightweightMessageFormatter formatter = createFormatterWithLine(sourceLine);
     JSError error = createError(TEST_SOURCE_NAME, 9, charno, "tab end");

     String result = formatter.formatError(error);
     // The pointer line will place the tab character, so the caret appears after it
     String expected = "test.js:9: ERROR - tab end\n" +
                       "code\t\n" +
                       "    \t^\n";
     assertEquals(expected, result);
   }

   /**
    * When no source provider is used (created via withoutSource()),
    * the excerpt is null and no source details are appended.
    */
   public void testWithoutSource() {
     LightweightMessageFormatter formatter = LightweightMessageFormatter.withoutSource();
     JSError error = createError("ignored.js", 1, 0, "no source");

     String result = formatter.formatError(error);
     assertEquals("ignored.js:1: ERROR - no source\n", result);
   }

   /**
    * Source provider returns null for excerpt: no source line and no pointer.
    */
   public void testSourceExcerptReturnsNull() {
     SourceExcerptProvider nullProvider = new SourceExcerptProvider() {
       @Override
       public String getSourceLine(String source, int lineNumber) {
         return null;
       }

       @Override
       public Region getSourceRegion(String source, int lineNumber, int length) {
         return null;
       }
     };
     LightweightMessageFormatter formatter = new LightweightMessageFormatter(nullProvider);
     JSError error = createError(TEST_SOURCE_NAME, 10, 5, "null excerpt");

     String result = formatter.formatError(error);
     assertFalse(result.contains("^"));
     assertEquals("test.js:10: ERROR - null excerpt\n", result);
   }
 }