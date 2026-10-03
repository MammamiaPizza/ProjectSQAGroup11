package org.apache.commons.compress.archivers.tar;

 import static org.junit.Assert.*;
 import org.junit.Test;

 /**
  * Tests for {@link TarUtils} focusing on {@code parseOctal} and related numeric
  * field parsing, covering the behavior required for COMPRESS-181 (robust
  * handling of broken/loosely formatted time headers).
  */
 public class TarUtilsTest {

     // -----------------------------------------------------------------------
     // parseOctal - valid inputs
     // -----------------------------------------------------------------------

     @Test
     public void testParseOctalValidSmallValue() {
         byte[] buf = "0000007\0".getBytes();
         assertEquals(7L, TarUtils.parseOctal(buf, 0, buf.length));
     }

     @Test
     public void testParseOctalLeadingSpaces() {
         byte[] buf = "   7\0".getBytes();
         assertEquals(7L, TarUtils.parseOctal(buf, 0, buf.length));
     }

     @Test
     public void testParseOctalTrailingSpaces() {
         byte[] buf = "7   \0".getBytes();
         // The method chops one trailing NUL/space, then a second; here we have
         // chars: '7',' ',' ',' ', NUL. After stripping, should parse "7".
         assertEquals(7L, TarUtils.parseOctal(buf, 0, buf.length));
     }

     @Test
     public void testParseOctalTrailingNuls() {
         // field: '7', NUL, NUL, NUL → valid, strips trailing NULs
         byte[] buf = new byte[] { '7', 0, 0, 0 };
         assertEquals(7L, TarUtils.parseOctal(buf, 0, buf.length));
     }

     @Test
     public void testParseOctalAllSpaces() {
         // Posix: all spaces means zero
         byte[] buf = "   \0".getBytes();  // length 4
         assertEquals(0L, TarUtils.parseOctal(buf, 0, buf.length));
     }

     @Test
     public void testParseOctalAllNuls() {
         byte[] buf = new byte[] { 0, 0, 0, 0 };
         assertEquals(0L, TarUtils.parseOctal(buf, 0, buf.length));
     }

     @Test
     public void testParseOctalLargeValueNearMaxLong() {
         // 777777777777777777777 (octal) fits in long
         byte[] buf = "777777777777777777777\0".getBytes();
         long result = TarUtils.parseOctal(buf, 0, buf.length);
         // verify it is positive and doesn't overflow
         assertTrue(result > 0);
         // round-trip sanity: value should equal its octal representation
         assertEquals(Long.parseLong("777777777777777777777", 8), result);
     }

     // -----------------------------------------------------------------------
     // parseOctal - invalid / exception cases
     // -----------------------------------------------------------------------

     @Test(expected = IllegalArgumentException.class)
     public void testParseOctalEmbeddedNonOctal() {
         byte[] buf = "701A3\0\0".getBytes(); // 'A' is not octal
         TarUtils.parseOctal(buf, 0, buf.length);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testParseOctalLengthTooShort() {
         TarUtils.parseOctal(new byte[] { '0' }, 0, 1); // length < 2
     }

     @Test(expected = IllegalArgumentException.class)
     public void testParseOctalZeroLength() {
         TarUtils.parseOctal(new byte[4], 0, 0);
     }

     // -----------------------------------------------------------------------
     // parseBoolean
     // -----------------------------------------------------------------------

     @Test
     public void testParseBooleanTrue() {
         assertTrue(TarUtils.parseBoolean(new byte[] { 1 }, 0));
     }

     @Test
     public void testParseBooleanFalse() {
         assertFalse(TarUtils.parseBoolean(new byte[] { 0 }, 0));
         // non-1 should be false per implementation
         assertFalse(TarUtils.parseBoolean(new byte[] { 2, 0 }, 0));
     }
 }