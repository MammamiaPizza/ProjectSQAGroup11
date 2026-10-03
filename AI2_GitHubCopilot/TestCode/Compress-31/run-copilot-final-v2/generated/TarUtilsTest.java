package org.apache.commons.compress.archivers.tar;

 import static org.junit.Assert.*;
 import org.junit.Test;

 /**
  * Unit tests for {@link TarUtils#parseOctal(byte[], int, int)} focusing on NUL-byte
  * validation and general octal parsing correctness.
  * <p>
  * See COMPRESS-301 and associated test triggers: TarTestCase.testCOMPRESS178
  * and TarUtilsTest.testParseOctalInvalid both expect an exception when an
  * embedded NUL is present inside the parsed octal field.
  * </p>
  */
 public class TarUtilsTest {

     /**
      * Valid octal string with trailing NUL and space – trimming and conversion work.
      */
     @Test
     public void testParseOctalValidWithTrailingNulAndSpace() {
         // "0777\000 " → 0777 octal = 511 decimal
         byte[] buffer = {'0', '7', '7', '7', 0, ' '};
         assertEquals(0777, TarUtils.parseOctal(buffer, 0, 6));
     }

     /**
      * Valid minimal two-char octal string.
      */
     @Test
     public void testParseOctalValidMinimal() {
         // "12" octal = 10 decimal
         byte[] buffer = {'1', '2'};
         assertEquals(10, TarUtils.parseOctal(buffer, 0, 2));
     }

     /**
      * All bytes zero → should return 0 without exception (normal tar "empty field").
      */
     @Test
     public void testParseOctalAllZeros() {
         byte[] buffer = {0, 0, 0, 0};
         assertEquals(0, TarUtils.parseOctal(buffer, 0, 4));
     }

     /**
      * Leading spaces are skipped, trailing NULs/spaces are trimmed.
      */
     @Test
     public void testParseOctalSpacesAndTrailingNuls() {
         // "   644\000\000 " → 0644 octal = 420 decimal
         byte[] buffer = {' ', ' ', ' ', '6', '4', '4', 0, 0, ' '};
         assertEquals(0644, TarUtils.parseOctal(buffer, 0, 9));
     }

     /**
      * Length less than 2 must throw {@link IllegalArgumentException}.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testParseOctalLengthTooSmall() {
         TarUtils.parseOctal(new byte[]{'1'}, 0, 1);
     }

     /**
      * Length = 0 also must throw.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testParseOctalZeroLength() {
         TarUtils.parseOctal(new byte[]{}, 0, 0);
     }

     /**
      * A non‑octal digit (e.g. '8') must throw IllegalArgumentException.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testParseOctalInvalidDigit() {
         byte[] buffer = {'8', 0}; // length >= 2, trailing NUL
         TarUtils.parseOctal(buffer, 0, 2);
     }

     /**
      * Embedded NUL inside the digit sequence (not at the very end) must throw
      * IllegalArgumentException – this is the core bug COMPRESS-301.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testParseOctalEmbeddedNulInsideDigits() {
         // "1\0002 " → NUL is not a valid octal digit
         byte[] buffer = {'1', 0, '2', ' '};
         TarUtils.parseOctal(buffer, 0, 4);
     }

     /**
      * A NUL that appears only at the very end of the range (trailing) is
      * correctly trimmed away and should not cause an exception.
      */
     @Test
     public void testParseOctalTrailingNulTrimmed() {
         // "12\000" → trailing NUL trimmed → parse "12" octal = 10 decimal
         byte[] buffer = {'1', '2', 0};
         assertEquals(10, TarUtils.parseOctal(buffer, 0, 3));
     }

     /**
      * Leading NUL followed by valid digits – the early start‑byte check returns 0
      * (field is treated as empty).  No exception is thrown (current behaviour).
      */
     @Test
     public void testParseOctalLeadingNulFollowedByDigits() {
         byte[] buffer = {0, '1', '2', 0};
         assertEquals(0, TarUtils.parseOctal(buffer, 0, 4));
     }

     /**
      * 0xff byte as the first byte (negative flag) together with trailing NUL
      * triggers an invalid‑byte exception because 0xff is not an octal digit.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testParseOctalNegativeFlagWithTrailingNul() {
         byte[] buffer = {(byte) 0xff, '1', '2', 0};
         TarUtils.parseOctal(buffer, 0, 4);
     }
 }
