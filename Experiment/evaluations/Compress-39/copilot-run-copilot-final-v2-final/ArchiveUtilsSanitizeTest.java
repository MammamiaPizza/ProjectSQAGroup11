package org.apache.commons.compress;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;

 import org.apache.commons.compress.utils.ArchiveUtils;
 import org.junit.Test;

 /**
  * Tests for {@link ArchiveUtils#sanitize(String)} focusing on the documented
  * behaviour that the result must not exceed 255 characters and that non-printable
  * characters are replaced with {@code '?'}.
  */
 public class ArchiveUtilsSanitizeTest {

     private static final int MAX_LENGTH = 255;
     private static final String ELLIPSIS = "...";

     @Test(expected = NullPointerException.class)
     public void testSanitizeNullThrowsNullPointerException() {
         ArchiveUtils.sanitize(null);
     }

     @Test
     public void testSanitizeEmptyStringReturnsEmpty() {
         assertEquals("", ArchiveUtils.sanitize(""));
     }

     @Test
     public void testSanitizeShortPrintableStringIsUnchanged() {
         String input = "hello.txt";
         assertEquals(input, ArchiveUtils.sanitize(input));
     }

     @Test
     public void testSanitizeExactlyAtMaxLengthStaysUnchanged() {
         String input = buildString(MAX_LENGTH, 'a');
         assertEquals(input, ArchiveUtils.sanitize(input));
     }

     @Test
     public void testSanitizeOneOverMaxLengthIsTruncatedWithEllipsis() {
         String input = buildString(MAX_LENGTH + 1, 'b');
         String expected = input.substring(0, MAX_LENGTH - ELLIPSIS.length()) + ELLIPSIS;
         assertEquals(expected, ArchiveUtils.sanitize(input));
     }

     @Test
     public void testSanitizeFarOverMaxLengthIsTruncatedWithEllipsis() {
         String input = buildString(1000, 'c');
         String expected = input.substring(0, MAX_LENGTH - ELLIPSIS.length()) + ELLIPSIS;
         assertEquals(expected, ArchiveUtils.sanitize(input));
     }

     @Test
     public void testSanitizeReplacesIsoControlCharacterWithQuestionMark() {
         String input = "a\nb";
         assertEquals("a?b", ArchiveUtils.sanitize(input));
     }

     @Test
     public void testSanitizeReplacesMultipleControlCharacters() {
         String input = "\0start\tmiddle\r\nend\u007F";
         assertEquals("?start?middle??end?", ArchiveUtils.sanitize(input));
     }

     @Test
     public void testSanitizeReplacesSpecialsUnicodeBlockCharacter() {
         // Characters from the Unicode SPECIALS block, e.g. U+FFF0, are replaced.
         String input = "x\uFFF0y";
         assertEquals("x?y", ArchiveUtils.sanitize(input));
     }

     @Test
     public void testSanitizeTruncationHappensAfterSanitizingControlChars() {
         // Build a string which, after replacing control chars, still exceeds MAX_LENGTH.
         // Insert a control char every few positions so that after replacement the
         // length remains the same (each control becomes '?'). The result must be truncated.
         int before = MAX_LENGTH - ELLIPSIS.length() - 50; // some printable prefix
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < before; i++) {
             sb.append('x');
         }
         sb.append('\n'); // control char, becomes '?'
         // append enough printable chars so total > MAX_LENGTH after replacement
         int remaining = (MAX_LENGTH + 10) - sb.length();
         for (int i = 0; i < remaining; i++) {
             sb.append('y');
         }
         String input = sb.toString();
         String result = ArchiveUtils.sanitize(input);
         assertTrue("Result exceeds max length", result.length() <= MAX_LENGTH);
         assertTrue("Truncated string must end with ellipsis", result.endsWith(ELLIPSIS));
         // The prefix before ellipsis should match the sanitized start of input
         String sanitized = input.replaceAll("\\p{Cntrl}", "?").replaceAll("\\p{InSpecials}", "?");
         String expectedPrefix = sanitized.substring(0, MAX_LENGTH - ELLIPSIS.length());
         assertEquals(expectedPrefix + ELLIPSIS, result);
     }

     @Test
     public void testSanitizeAllControlCharactersStringBecomesEllipsisAfterTruncation() {
         // When every character is a control char, each becomes '?',
         // resulting in a long string of '?' that must be truncated.
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < 300; i++) {
             sb.append('\0');
         }
         String result = ArchiveUtils.sanitize(sb.toString());
         assertEquals(MAX_LENGTH, result.length());
         assertEquals(buildString(MAX_LENGTH - ELLIPSIS.length(), '?') + ELLIPSIS, result);
     }

     @Test
     public void testSanitizeLengthBoundaryWithSpecialCharacters() {
         // Input has exactly MAX_LENGTH printable chars, no truncation, no replacement.
         String input = buildString(MAX_LENGTH, 'd');
         assertEquals(MAX_LENGTH, input.length());
         assertEquals(input, ArchiveUtils.sanitize(input));
     }

     private static String buildString(int length, char ch) {
         StringBuilder sb = new StringBuilder(length);
         for (int i = 0; i < length; i++) {
             sb.append(ch);
         }
         return sb.toString();
     }
 }
