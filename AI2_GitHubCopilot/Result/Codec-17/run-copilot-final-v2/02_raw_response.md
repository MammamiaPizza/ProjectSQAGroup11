package org.apache.commons.codec.binary;

 import static org.junit.Assert.*;

 import org.junit.Test;

 /**
  * Tests for {@link StringUtils} focusing on CODEC-229: all public newString*
  * methods must return null for a null byte[] input instead of throwing
  * {@link NullPointerException}.
  */
 public class StringUtilsTest {

     private static final byte[] NULL_BYTES = null;
     private static final byte[] EMPTY_BYTES = new byte[0];
     private static final String TEST_STR = "aBc 123 \u00e4\u00f6\u00fc"; // includes ISO-8859-1
characters

     // --- null byte[] must return null (CODEC-229) ---

     @Test
     public void testNewStringNullWithCharsetName() {
         assertNull(StringUtils.newString(NULL_BYTES, "UTF-8"));
     }

     @Test
     public void testNewStringIso8859_1Null() {
         // This call exposes CODEC-229: current implementation uses new String(bytes, charset)
         // which throws NullPointerException instead of returning null.
         assertNull(StringUtils.newStringIso8859_1(NULL_BYTES));
     }

     @Test
     public void testNewStringUsAsciiNull() {
         assertNull(StringUtils.newStringUsAscii(NULL_BYTES));
     }

     @Test
     public void testNewStringUtf8Null() {
         assertNull(StringUtils.newStringUtf8(NULL_BYTES));
     }

     @Test
     public void testNewStringUtf16Null() {
         assertNull(StringUtils.newStringUtf16(NULL_BYTES));
     }

     @Test
     public void testNewStringUtf16BeNull() {
         assertNull(StringUtils.newStringUtf16Be(NULL_BYTES));
     }

     @Test
     public void testNewStringUtf16LeNull() {
         assertNull(StringUtils.newStringUtf16Le(NULL_BYTES));
     }

     // --- empty byte[] must produce empty string ---

     @Test
     public void testNewStringEmptyBytesIso8859_1() {
         assertEquals("", StringUtils.newStringIso8859_1(EMPTY_BYTES));
     }

     @Test
     public void testNewStringEmptyBytesUtf8() {
         assertEquals("", StringUtils.newStringUtf8(EMPTY_BYTES));
     }

     // --- round-trip: encode then decode reproduces the original string ---

     @Test
     public void testRoundTripIso8859_1() {
         byte[] encoded = StringUtils.getBytesIso8859_1(TEST_STR);
         String decoded = StringUtils.newStringIso8859_1(encoded);
         assertEquals(TEST_STR, decoded);
     }

     @Test
     public void testRoundTripUtf8() {
         byte[] encoded = StringUtils.getBytesUtf8(TEST_STR);
         String decoded = StringUtils.newStringUtf8(encoded);
         assertEquals(TEST_STR, decoded);
     }

     @Test
     public void testRoundTripUsAscii() {
         String asciiOnly = "Hello 123";
         byte[] encoded = StringUtils.getBytesUsAscii(asciiOnly);
         String decoded = StringUtils.newStringUsAscii(encoded);
         assertEquals(asciiOnly, decoded);
     }
 }