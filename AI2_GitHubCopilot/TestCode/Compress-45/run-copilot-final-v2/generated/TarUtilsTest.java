package org.apache.commons.compress.archivers.tar;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.fail;

 import org.junit.Test;

 /**
  * Tests for {@link TarUtils} focusing on format/parse round-trips and boundary conditions,
  * covering the COMPRESS-411 bug where negative values close to the field capacity
  * were incorrectly rejected.
  */
 public class TarUtilsTest {

     // helpers -----------------------------------------------------------------
     private static byte[] buffer(int length) {
         return new byte[length];
     }

     private static long roundTripLongOctal(long value, int length) {
         byte[] buf = buffer(length);
         TarUtils.formatLongOctalBytes(value, buf, 0, length);
         return TarUtils.parseOctal(buf, 0, length);
     }

     private static long roundTripOctal(long value, int length) {
         byte[] buf = buffer(length);
         TarUtils.formatOctalBytes(value, buf, 0, length);
         return TarUtils.parseOctal(buf, 0, length);
     }

     private static long roundTripLongOctalOrBinary(long value, int length) {
         byte[] buf = buffer(length);
         TarUtils.formatLongOctalOrBinaryBytes(value, buf, 0, length);
         return TarUtils.parseOctal(buf, 0, length);
     }

     // ---------- Positive octal (formatLongOctalBytes) ------------------------
     @Test
     public void testRoundTripPositiveOctalSmall() {
         assertEquals(0L, roundTripLongOctal(0L, 8));
         assertEquals(1L, roundTripLongOctal(1L, 8));
         assertEquals(123456L, roundTripLongOctal(123456L, 8));
     }

     @Test
     public void testRoundTripPositiveOctalMax8Bytes() {
         // for length=8, formatLongOctalBytes allows 7 octal digits -> max 8^7-1 = 2097151
         assertEquals(2097151L, roundTripLongOctal(2097151L, 8));
     }

     @Test(expected = IllegalArgumentException.class)
     public void testFormatLongOctalBytesOverflow8ByteField() {
         // 2097152 = 8^7   -> too large for 7 octal digits
         TarUtils.formatLongOctalBytes(2097152L, buffer(8), 0, 8);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testFormatLongOctalBytesNegativeThrows() {
         // formatLongOctalBytes delegates to formatUnsignedOctalString which cannot
         // represent negative values.
         TarUtils.formatLongOctalBytes(-1L, buffer(8), 0, 8);
     }

     // ---------- Binary round-trips (formatLongOctalOrBinaryBytes) ------------
     @Test
     public void testRoundTripBinaryPositiveLargeValue8ByteField() {
         // 1000000000 is above any reasonable MAXID, so it must use binary encoding.
         assertEquals(1000000000L, roundTripLongOctalOrBinary(1000000000L, 8));
     }

     @Test
     public void testRoundTripBinaryNegativeSmall() {
         assertEquals(-1L, roundTripLongOctalOrBinary(-1L, 8));
         assertEquals(-123456L, roundTripLongOctalOrBinary(-123456L, 8));
     }

     @Test
     public void testRoundTripBinaryNegativeBugValue() {
         // COMPRESS-411: value -72057594037927935 must round-trip for an 8-byte field.
         // abs(value) = 2^56 - 1, which is the maximum representable magnitude.
         final long bugValue = -72057594037927935L;
         assertEquals(bugValue, roundTripLongOctalOrBinary(bugValue, 8));
     }

     @Test(expected = IllegalArgumentException.class)
     public void testBinaryPositiveOverflow8ByteField() {
         // 2^56 is too large (max unsigned is 2^56-1)
         long tooBig = 1L << 56; // 72057594037927936
         TarUtils.formatLongOctalOrBinaryBytes(tooBig, buffer(8), 0, 8);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testBinaryNegativeOverflow8ByteField() {
         // -2^56 has absolute value equal to max, so it is rejected.
         long tooNeg = -(1L << 56); // -72057594037927936
         TarUtils.formatLongOctalOrBinaryBytes(tooNeg, buffer(8), 0, 8);
     }

     // ---------- formatOctalBytes (shorter octal field) -----------------------
     @Test
     public void testRoundTripOctalBytesSmall() {
         // length=8 -> 6 octal digits, max 8^6-1 = 262143
         assertEquals(0L, roundTripOctal(0L, 8));
         assertEquals(1L, roundTripOctal(1L, 8));
         assertEquals(123456L, roundTripOctal(123456L, 8));
     }

     @Test(expected = IllegalArgumentException.class)
     public void testFormatOctalBytesOverflow() {
         // 262144 = 8^6   -> too large for 6 digits
         TarUtils.formatOctalBytes(262144L, buffer(8), 0, 8);
     }

     // ---------- parseOctal direct & error ------------------------------------
     @Test
     public void testParseOctalInvalidCharacterThrows() {
         byte[] buf = new byte[8];
         buf[0] = '0';
         buf[1] = '8';  // invalid octal digit
         buf[2] = ' ';
         buf[3] = 0;
         try {
             TarUtils.parseOctal(buf, 0, 8);
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException e) {
             // expected
         }
     }
 }
