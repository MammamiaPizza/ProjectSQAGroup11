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

@Test
public void testParseOctalSpaceMidInput() {
    byte[] buffer = new byte[] { 0x31, 0x20, 0x00 };
    assertEquals(1L, TarUtils.parseOctal(buffer, 0, 3));
}

@Test
public void testFormatCheckSumOctalBytes() {
    byte[] buf = new byte[8];
    int result = TarUtils.formatCheckSumOctalBytes(255L, buf, 0, 8);
    assertEquals(8, result);
    assertEquals(0, buf[6]);
    assertEquals((byte) ' ', buf[7]);
    for (int i = 0; i < 6; i++) {
        assertTrue(buf[i] >= '0' && buf[i] <= '7' || buf[i] == ' ');
    }
}

@Test
public void testComputeCheckSum() {
    byte[] buf = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    long expected = 0;
    for (byte b : buf) {
        expected += (b & 0xFF);
    }
    assertEquals(expected, TarUtils.computeCheckSum(buf));
}

@Test
public void testVerifyCheckSumValid() {
    byte[] header = new byte[512];
    for (int i = 0; i < header.length; i++) {
        header[i] = (byte) (i % 256);
    }
    for (int i = 148; i < 156; i++) {
        header[i] = (byte) ' ';
    }
    long sum = TarUtils.computeCheckSum(header);
    TarUtils.formatCheckSumOctalBytes(sum, header, 148, 8);
    assertTrue(TarUtils.verifyCheckSum(header));
}
}
