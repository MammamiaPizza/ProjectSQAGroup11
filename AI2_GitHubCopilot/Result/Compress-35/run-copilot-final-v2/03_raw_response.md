package org.apache.commons.compress.archivers.tar;

 import static org.apache.commons.compress.archivers.tar.TarConstants.CHKSUMLEN;
 import static org.apache.commons.compress.archivers.tar.TarConstants.CHKSUM_OFFSET;
 import static org.junit.Assert.*;

 import org.junit.Test;

 /**
  * Tests for TarUtils, targeting the COMPRESS-335 bug.
  * Focused on parseOctal, verifyCheckSum, parseName, parseBoolean.
  */
 public class TarUtilsTest {
   @Test
   public void testParseOctalNormal() {
   // 8-digit octal: 0100777 = 33279
   byte[] buf = "0100777\u0000".getBytes();
   long result = TarUtils.parseOctal(buf, 0, 8);
   assertEquals(0100777L, result);
   }
   @Test
   public void testParseOctalMaxLength() {
   // 12-digit octal max: 777777777777 = 8^12 - 1 = 68719476735
   byte[] buf = "777777777777".getBytes();
   long result = TarUtils.parseOctal(buf, 0, 12);
   assertEquals(0777777777777L, result);
   }
   @Test
   public void testParseOctalNegative() {
   // 0xff first byte indicates negative binary two's complement.
   // 12-byte field: flag (0xff) + 11 bytes big-endian absolute value.
   // Encoding -1: absolute 1 at LSB.
   byte[] buf = new byte[12];
   buf[0] = (byte) 0xff;
   buf[11] = 1; // rest zeros
   long result = TarUtils.parseOctal(buf, 0, 12);
   assertEquals(-1L, result);
   }
   @Test
   public void testParseOctalTrailingSpacesAndNuls() {
   // 0777 followed by spaces and NULs
   byte[] buf = new byte[8];
   buf[0] = '0';
   buf[1] = '7';
   buf[2] = '7';
   buf[3] = '7';
   buf[4] = ' ';
   buf[5] = 0;
   buf[6] = 0;
   buf[7] = 0;
   long result = TarUtils.parseOctal(buf, 0, 8);
   assertEquals(0777L, result);
   }
   @Test(expected = IllegalArgumentException.class)
   public void testParseOctalNonOctalChar() {
   byte[] buf = "1238".getBytes(); // '8' is invalid
   TarUtils.parseOctal(buf, 0, 4);
   }
   @Test(expected = IllegalArgumentException.class)
   public void testParseOctalLengthTooSmall() {
   byte[] buf = new byte[1];
   TarUtils.parseOctal(buf, 0, 1);
   }
   @Test
   public void testVerifyCheckSumMatching() {
   byte[] header = new byte[512];
   // fill with some recognizable content
   header[0] = 'u';
   header[1] = 's';
   header[2] = 't';
   header[3] = 'a';
   header[4] = 'r';
   // set checksum field to spaces
   for (int i = CHKSUM_OFFSET; i < CHKSUM_OFFSET + CHKSUMLEN; i++) {
       header[i] = ' ';
   }
   long sum = TarUtils.computeCheckSum(header);
   TarUtils.formatCheckSumOctalBytes(sum, header, CHKSUM_OFFSET, CHKSUMLEN);
   assertTrue(TarUtils.verifyCheckSum(header));
   }
   @Test
   public void testVerifyCheckSumMismatch() {
   byte[] header = new byte[512];
   header[0] = 0x01;
   // checksum field spaces
   for (int i = CHKSUM_OFFSET; i < CHKSUM_OFFSET + CHKSUMLEN; i++) {
       header[i] = ' ';
   }
   long sum = TarUtils.computeCheckSum(header);
   TarUtils.formatCheckSumOctalBytes(sum, header, CHKSUM_OFFSET, CHKSUMLEN);
   // corrupt data
   header[100] ^= 0x01;
   assertFalse(TarUtils.verifyCheckSum(header));
   }
   @Test
   public void testVerifyCheckSumAllSpacesReturnsFalse() {
   byte[] header = new byte[512];
   // checksum field spaces, rest is arbitrary non-zero -> sum cannot be zero
   for (int i = 0; i < 512; i++) {
       header[i] = (byte) ((i < CHKSUM_OFFSET || i >= CHKSUM_OFFSET + CHKSUMLEN) ? 1 : ' ');
   }
   assertFalse("VerifyCheckSum should fail when checksum field is spaces and data non-zero",
           TarUtils.verifyCheckSum(header));
   }
   @Test
   public void testParseBooleanTrue() {
   byte[] buf = new byte[] { 1 };
   assertTrue(TarUtils.parseBoolean(buf, 0));
   }
   @Test
   public void testParseBooleanFalse() {
   byte[] buf = new byte[] { 0 };
   assertFalse(TarUtils.parseBoolean(buf, 0));
   }
   @Test
   public void testParseNameTrailingNul() {
   byte[] buf = new byte[] { 'f', 'o', 'o', 0, 0 };
   assertEquals("foo", TarUtils.parseName(buf, 0, 5));
   }
   @Test
   public void testParseNameEmbeddedNul() {
   // "ustar\0extra" – POSIX says parse stops at first NUL
   byte[] buf = new byte[] { 'u', 's', 't', 'a', 'r', 0, 'x' };
   assertEquals("ustar", TarUtils.parseName(buf, 0, 7));
   }

 }