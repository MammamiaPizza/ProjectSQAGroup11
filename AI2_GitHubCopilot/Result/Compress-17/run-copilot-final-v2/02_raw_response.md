package org.apache.commons.compress.archivers.tar;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link TarUtils} targeting COMPRESS-197 and related edge cases.
  */
 public class TarUtilsTest {

     // ------------ parseOctal ------------

     @Test
     public void testParseOctal_ValidOctal() {
         // "755\0" should parse to 493 (7*8^2 + 5*8 + 5)
         byte[] buffer = new byte[] { '7', '5', '5', 0 };
         assertEquals(493, TarUtils.parseOctal(buffer, 0, 4));
     }

     @Test
     public void testParseOctal_LeadingSpacesAndTrailingNulls() {
         // Spaces then octal digits then two NUL bytes
         byte[] buffer = new byte[] { ' ', ' ', '7', '5', '5', 0, 0 };
         assertEquals(493, TarUtils.parseOctal(buffer, 0, buffer.length));
     }

     @Test
     public void testParseOctal_AllZeros() {
         byte[] buffer = new byte[] { '0', '0', '0', '0', '0', '0', '0', '0', 0 };
         assertEquals(0, TarUtils.parseOctal(buffer, 0, buffer.length));
     }

     @Test(expected = IllegalArgumentException.class)
     public void testParseOctal_LengthTooSmall() {
         TarUtils.parseOctal(new byte[] { '1', '2' }, 0, 1);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testParseOctal_InvalidOctalDigit() {
         // '8' is not an octal digit
         byte[] buffer = new byte[] { '1', '2', '8', 0 };
         TarUtils.parseOctal(buffer, 0, buffer.length);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testParseOctal_LeadingFFByte() {
         // buffer with leading 0xff is invalid for the current parseOctal
         byte[] buffer = new byte[] { (byte) 0xff, '7', '5', '5', 0 };
         TarUtils.parseOctal(buffer, 0, buffer.length);
     }

     @Test
     public void testParseOctal_Max12DigitValue() {
         // 12-digit max octal value 777777777777 = 8^12 - 1 = 68719476735
         byte[] buffer = new byte[] { '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', 0
};
         assertEquals(68719476735L, TarUtils.parseOctal(buffer, 0, buffer.length));
     }

     // ------------ parseName ------------

     @Test
     public void testParseName_NormalName() {
         byte[] buffer = new byte[100];
         byte[] nameBytes = "file.txt".getBytes();
         System.arraycopy(nameBytes, 0, buffer, 0, nameBytes.length);
         // remaining bytes are 0 (NUL)
         assertEquals("file.txt", TarUtils.parseName(buffer, 0, 100));
     }

     @Test
     public void testParseName_TrailingNullsOnly() {
         byte[] buffer = new byte[100]; // all zeros
         assertEquals("", TarUtils.parseName(buffer, 0, 100));
     }

     @Test
     public void testParseName_EmbeddedNullActsAsTerminator() {
         byte[] buffer = new byte[100];
         byte[] nameBytes = new byte[] { 'a', 'b', 0, 'c', 'd' };
         System.arraycopy(nameBytes, 0, buffer, 0, nameBytes.length);
         // Trailing NULs are stripped, but the decode stops at the first NUL
         assertEquals("ab", TarUtils.parseName(buffer, 0, 100));
     }

     // ------------ verifyCheckSum ------------

     @Test
     public void testVerifyCheckSum_ValidHeader() {
         byte[] header = new byte[512];
         // Fill with recognisable data
         for (int i = 0; i < header.length; i++) {
             header[i] = (byte) ('a' + i % 26);
         }
         // Set checksum area to spaces, compute sum, format checksum, then verify
         for (int i = TarConstants.CHKSUM_OFFSET; i < TarConstants.CHKSUM_OFFSET +
TarConstants.CHKSUMLEN; i++) {
             header[i] = ' ';
         }
         long sum = TarUtils.computeCheckSum(header);
         TarUtils.formatCheckSumOctalBytes(sum, header, TarConstants.CHKSUM_OFFSET,
TarConstants.CHKSUMLEN);
         assertTrue(TarUtils.verifyCheckSum(header));
     }

     @Test
     public void testVerifyCheckSum_WrongStoredSum() {
         byte[] header = new byte[512];
         for (int i = 0; i < header.length; i++) {
             header[i] = (byte) i;
         }
         for (int i = TarConstants.CHKSUM_OFFSET; i < TarConstants.CHKSUM_OFFSET +
TarConstants.CHKSUMLEN; i++) {
             header[i] = ' ';
         }
         long sum = TarUtils.computeCheckSum(header);
         // Write an incorrect checksum (off by 1)
         TarUtils.formatCheckSumOctalBytes(sum + 1, header, TarConstants.CHKSUM_OFFSET,
TarConstants.CHKSUMLEN);
         assertFalse(TarUtils.verifyCheckSum(header));
     }
 }