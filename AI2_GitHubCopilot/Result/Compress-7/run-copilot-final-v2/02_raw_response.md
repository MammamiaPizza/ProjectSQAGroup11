package org.apache.commons.compress.archivers.tar;

 import junit.framework.TestCase;

 /**
  * Tests for {@link TarUtils} focusing on parseName and formatNameBytes,
  * especially the non‑ASCII 8‑bit character bug (COMPRESS‑114).
  */
 public class TarUtilsTest extends TestCase {

     // ---- parseName tests ----

     public void testParseNameUnsignedByteHandling() {
         // Bug: sign-extension turned 0xB1 into U+FFB1 instead of U+00B1
         byte[] buf = new byte[] { (byte) 0xB1 };
         String result = TarUtils.parseName(buf, 0, 1);
         assertEquals("Single high-byte must become Unicode U+00B1",
                      (char) 0xB1, result.charAt(0));
     }

     public void testParseNameStopsAtNUL() {
         byte[] buf = new byte[] { 'A', 'B', 0, 'C', 'D' };
         String result = TarUtils.parseName(buf, 0, 5);
         assertEquals("AB", result);
     }

     public void testParseNameAllNULsReturnsEmpty() {
         byte[] buf = new byte[] { 0, 0, 0 };
         String result = TarUtils.parseName(buf, 0, 3);
         assertEquals("", result);
     }

     public void testParseNameEmptyBufferReturnsEmpty() {
         byte[] buf = new byte[0];
         String result = TarUtils.parseName(buf, 0, 0);
         assertEquals("", result);
     }

     public void testParseNameNullBufferThrowsNPE() {
         try {
             TarUtils.parseName(null, 0, 1);
             fail("Should have thrown NullPointerException");
         } catch (NullPointerException expected) {
             // pass
         }
     }

     // ---- formatNameBytes tests ----

     public void testFormatNameBytesSimpleAscii() {
         byte[] buf = new byte[6];
         TarUtils.formatNameBytes("hello", buf, 0, 6);
         assertEquals('h', buf[0]);
         assertEquals('e', buf[1]);
         assertEquals('l', buf[2]);
         assertEquals('l', buf[3]);
         assertEquals('o', buf[4]);
         assertEquals(0, buf[5]);
     }

     public void testFormatNameBytesPaddingWithNULs() {
         byte[] buf = new byte[5];
         TarUtils.formatNameBytes("ab", buf, 0, 5);
         assertEquals('a', buf[0]);
         assertEquals('b', buf[1]);
         assertEquals(0, buf[2]);
         assertEquals(0, buf[3]);
         assertEquals(0, buf[4]);
     }

     public void testFormatNameBytesTruncation() {
         byte[] buf = new byte[3];
         TarUtils.formatNameBytes("abcdef", buf, 0, 3);
         assertEquals('a', buf[0]);
         assertEquals('b', buf[1]);
         assertEquals('c', buf[2]);
     }

     public void testFormatNameBytesEmptyNameIsAllNULs() {
         byte[] buf = new byte[3];
         TarUtils.formatNameBytes("", buf, 0, 3);
         assertEquals(0, buf[0]);
         assertEquals(0, buf[1]);
         assertEquals(0, buf[2]);
     }

     public void testFormatNameBytesNullNameThrowsNPE() {
         try {
             TarUtils.formatNameBytes(null, new byte[10], 0, 10);
             fail("Should have thrown NullPointerException");
         } catch (NullPointerException expected) {
             // pass
         }
     }

     public void testFormatNameBytesNullBufferThrowsNPE() {
         try {
             TarUtils.formatNameBytes("test", null, 0, 10);
             fail("Should have thrown NullPointerException");
         } catch (NullPointerException expected) {
             // pass
         }
     }

     public void testFormatNameBytesReturnsOffsetPlusLength() {
         byte[] buf = new byte[20];
         int ret = TarUtils.formatNameBytes("foo", buf, 7, 5);
         assertEquals(12, ret);
         assertEquals('f', buf[7]);
         assertEquals('o', buf[8]);
         assertEquals('o', buf[9]);
         assertEquals(0, buf[10]);
         assertEquals(0, buf[11]);
     }

     // ---- round-trip with high-byte Latin-1 characters (the trigger) ----

     public void testRoundTripWithHighBytesExposesBug() {
         // This string contains U+00B1 (±) which is the trigger character
         String original = "0302-0601-3\u00B1\u00B1\u00B1F06\u00B1W220\u00B1ZB"
                          + "\u00B1LALALA\u00B1\u00B1\u00B1\u00B1\u00B1\u00B1\u00B1\u00B1"
                          + "\u00B1\u00B1CAN\u00B1\u00B1DC\u00B1\u00B1\u00B104\u00B1060302\u00B1"
                          + "MOE.model";
         byte[] buf = new byte[100];
         TarUtils.formatNameBytes(original, buf, 0, 100);
         String recovered = TarUtils.parseName(buf, 0, 100);
         // With correct masking parseName gives back the original string,
         // the bug produces mojibake (U+FFB1 etc.)
         assertEquals("Round-trip must preserve Latin-1 high-byte characters",
                      original, recovered);
     }
 }