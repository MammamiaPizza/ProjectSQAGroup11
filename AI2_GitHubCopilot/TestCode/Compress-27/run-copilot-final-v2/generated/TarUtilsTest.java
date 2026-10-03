package org.apache.commons.compress.archivers.tar;

 import org.junit.Test;
 import static org.junit.Assert.*;

 public class TarUtilsTest {

     /**
      * The reported bug: a field containing space then NUL (e.g., " \0")
      * should be treated as an empty octal field and return 0.
      * Buggy code throws IllegalArgumentException on this benign input.
      */
     @Test
     public void testParseOctal() {
         byte[] buffer = new byte[] { ' ', 0 };
         assertEquals(0L, TarUtils.parseOctal(buffer, 0, 2));
     }

     @Test
     public void testParseOctalAllNul() {
         byte[] buffer = new byte[] { 0, 0, 0, 0 };
         // First byte is NUL → immediate return 0 (existing behavior)
         assertEquals(0L, TarUtils.parseOctal(buffer, 0, 4));
     }

     @Test
     public void testParseOctalAllSpace() {
         // All-space field should be treated as empty → return 0
         byte[] buffer = new byte[] { ' ', ' ', ' ' };
         assertEquals(0L, TarUtils.parseOctal(buffer, 0, 3));
     }

     @Test
     public void testParseOctalSpaceNulSequence() {
         // Several spaces followed by NULs → empty field → 0
         byte[] buffer = new byte[] { ' ', ' ', ' ', 0, 0 };
         assertEquals(0L, TarUtils.parseOctal(buffer, 0, 5));
     }

     @Test
     public void testParseOctalLeadingSpaces() {
         // Leading spaces before octal digits
         byte[] buffer = new byte[] { ' ', ' ', '1', '0', 0 };
         // Octal "10" = 8
         assertEquals(8L, TarUtils.parseOctal(buffer, 0, 5));
     }

     @Test
     public void testParseOctalTrailingSpaces() {
         // Trailing spaces after octal digits
         byte[] buffer = new byte[] { '1', '2', ' ', ' ', 0 };
         // Octal "12" = 10
         assertEquals(10L, TarUtils.parseOctal(buffer, 0, 5));
     }

     @Test
     public void testParseOctalTrailingNuls() {
         // Trailing NULs after octal digits (standard tar format)
         byte[] buffer = new byte[] { '7', '7', '7', 0, 0, 0 };
         // Octal "777" = 511
         assertEquals(511L, TarUtils.parseOctal(buffer, 0, 6));
     }

     @Test
     public void testParseOctalMixedPadding() {
         // Mixed leading spaces and trailing NULs around octal value
         byte[] buffer = new byte[] { ' ', ' ', '3', '7', '7', 0, 0 };
         // Octal "377" = 3*64 + 7*8 + 7 = 192 + 56 + 7 = 255
         assertEquals(255L, TarUtils.parseOctal(buffer, 0, 7));
     }

     @Test
     public void testParseOctalZeroPadded() {
         // Zero value with surrounding spaces and NUL
         byte[] buffer = new byte[] { ' ', '0', 0, ' ' };
         // Octal "0" = 0
         assertEquals(0L, TarUtils.parseOctal(buffer, 0, 4));
     }

     @Test
     public void testParseOctalLargeValue() {
         // Large octal value near max that fits in a 12-byte tar field
         // Octal "777777777777" = 8^12 - 1 = 68719476735
         byte[] buffer = new byte[] { '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7' };
         assertEquals(68719476735L, TarUtils.parseOctal(buffer, 0, 12));
     }

     @Test(expected = IllegalArgumentException.class)
     public void testParseOctalLengthTooSmall() {
         // Length less than 2 must throw IllegalArgumentException
         TarUtils.parseOctal(new byte[] { '0' }, 0, 1);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testParseOctalInvalidCharacter() {
         // Non-octal character (not 0-7) after trimming padding must throw
         byte[] buffer = new byte[] { '1', '8', ' ', 0 };
         TarUtils.parseOctal(buffer, 0, 4);
     }
 }
