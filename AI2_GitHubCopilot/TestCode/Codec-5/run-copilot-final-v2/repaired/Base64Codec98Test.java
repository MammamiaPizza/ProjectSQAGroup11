import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

/**

 - JUnit test for the Base64 bug CODEC-98 (NullPointerException)
 - covering edge cases, roundtrip, null/empty inputs, and constructor variations.
  */
 public class Base64Codec98Test {
  /**
  - Roundtrip encode/decode for various lengths ensures no data corruption.
    */
   @Test
   public void testRoundtripVariousLengths() {
   int[] lengths = {0, 1, 2, 3, 4, 76, 77, 100, 1000};
   for (int len : lengths) {
   byte[] original = new byte[len];
   new Random(42).nextBytes(original);
   // Instance encode/decode
   Base64 b64 = new Base64();
   byte[] encoded = b64.encode(original);
   byte[] decoded = b64.decode(encoded);
   Assert.assertArrayEquals("Roundtrip failed for length " + len, original, decoded);
   // Static encodeBase64 / decodeBase64
   byte[] staticEncoded = Base64.encodeBase64(original);
   byte[] staticDecoded = Base64.decodeBase64(staticEncoded);
   Assert.assertArrayEquals("Static roundtrip failed for length " + len, original, staticDecoded);
   }
  }
  /**
  - Encoding/decoding empty byte array should return empty result, never null.
    */
   @Test
   public void testEmptyInput() {
   byte[] empty = new byte[0];
   Base64 b64 = new Base64();
   Assert.assertNotNull("Encode empty should not be null", b64.encode(empty));
   Assert.assertEquals(0, b64.encode(empty).length);
   Assert.assertNotNull("Decode empty should not be null", b64.decode(empty));
   Assert.assertEquals(0, b64.decode(empty).length);
   Assert.assertNotNull("encodeBase64 empty", Base64.encodeBase64(empty));
   Assert.assertEquals(0, Base64.encodeBase64(empty).length);
   Assert.assertNotNull("decodeBase64 empty", Base64.decodeBase64(empty));
   Assert.assertEquals(0, Base64.decodeBase64(empty).length);
   Assert.assertNotNull("encodeToString empty", b64.encodeToString(empty));
   Assert.assertEquals("", b64.encodeToString(empty));
  }
  /**
  - Null inputs to encode/decode must not throw NPE; expected to return null.
    */
   @Test
   public void testNullInputs() {
   Base64 b64 = new Base64();
   Assert.assertNull(b64.encode((byte[]) null));
   Assert.assertNull(b64.decode((byte[]) null));
   Assert.assertNull(b64.decode((String) null));
   Assert.assertNull(Base64.encodeBase64((byte[]) null));
   Assert.assertNull(Base64.decodeBase64((byte[]) null));
   Assert.assertNull(Base64.decodeBase64((String) null));
   }
  /**
  - Constructor with null lineSeparator – in the fixed version this should not throw NPE.
  - In the buggy CODEC-98 version this will throw a NullPointerException.
    */
   @Test
   public void testConstructorWithNullLineSeparator() {
   // Should not throw; expect graceful fallback (chunking disabled)
   Base64 b64a = new Base64(76, (byte[]) null);
   byte[] data = "data".getBytes();
   Assert.assertNotNull(b64a.encode(data));
   // Three-argument constructor with null separator
   Base64 b64b = new Base64(76, null, false);
   Assert.assertNotNull(b64b.encode(data));
   // URL-safe variant with null separator
   Base64 b64c = new Base64(76, null, true);
   Assert.assertNotNull(b64c.encode(data));
  }
  /**
  - Negative lineLength must not cause exceptions; effective line-length becomes 0.
    */
   @Test
   public void testNegativeLineLength() {
   Base64 b64 = new Base64(-1);
   byte[] data = "Hello".getBytes();
   byte[] encoded = b64.encode(data);
   // With lineLength=0 the encode is unchunked, should decode back exactly
   byte[] decoded = b64.decode(encoded);
   Assert.assertArrayEquals(data, decoded);
   // Further negative values
   Base64 b642 = new Base64(-76);
   byte[] encoded2 = b642.encode(data);
   Assert.assertEquals(0, encoded2.length % 4); // base64 multiple of 4
  }
  /**
  - encodeToString and decode using String should roundtrip correctly.
    */
   @Test
   public void testEncodeToStringAndDecodeString() throws UnsupportedEncodingException {
   byte[] original = "Hello World!".getBytes("UTF-8");
   Base64 b64 = new Base64();
   String b64Str = b64.encodeToString(original);
   Assert.assertNotNull(b64Str);
   byte[] decoded = b64.decode(b64Str);
   Assert.assertArrayEquals(original, decoded);
   // static versions
   String staticStr = Base64.encodeBase64String(original);
   byte[] staticDecoded = Base64.decodeBase64(staticStr);
   Assert.assertArrayEquals(original, staticDecoded);
  }
  /**
  - Chunked encoding with a custom single-byte separator verifies correct line insertion.
    */
   @Test
   public void testChunkedEncoding() {
   byte lineSep = (byte) '\n';
   int lineLength = 4; // one base64 character per 3 bytes? Actually 4 base64 chars per 3 bytes
   Base64 b64 = new Base64(lineLength, new byte[]{lineSep});
   // 3 bytes produce exactly 4 base64 characters (one chunk) + separator
   byte[] input = new byte[]{0, 1, 2};
   byte[] encoded = b64.encode(input);
   Assert.assertEquals(5, encoded.length);
   Assert.assertEquals(lineSep, encoded[4]);
   // 6 bytes produce two chunks, should have two separators
   byte[] input2 = new byte[]{0, 1, 2, 3, 4, 5};
   byte[] encoded2 = b64.encode(input2);
   Assert.assertTrue(encoded2.length > 8); // at least 8 chars + 2 separators
   long sepCount = 0;
   for (byte b : encoded2) { if (b == lineSep) sepCount++; }
   Assert.assertEquals(2, sepCount);
  }
  /**
  - URL-safe encoding omits padding characters and should roundtrip through
  - a url-safe decode instance.
    */
   @Test
   public void testUrlSafeEncoding() {
   byte[] data = "url safe test".getBytes();
   byte[] urlEncoded = Base64.encodeBase64URLSafe(data);
   // Verify no padding '='
   for (byte b : urlEncoded) {
   Assert.assertFalse("URL-safe output must not contain padding", b == '=');
   }
   // Roundtrip using url-safe instance
   Base64 urlSafeBase64 = new Base64(0, null, true); // null handled gracefully
   byte[] decoded = urlSafeBase64.decode(urlEncoded);
   Assert.assertArrayEquals(data, decoded);
   // Static URL-safe string
   String urlSafeStr = Base64.encodeBase64URLSafeString(data);
   Assert.assertFalse(urlSafeStr.contains("="));
   byte[] decoded2 = urlSafeBase64.decode(urlSafeStr);
   Assert.assertArrayEquals(data, decoded2);
  }
  /**
  - decode(Object) with an invalid type must throw DecoderException.
    */
   @Test(expected = DecoderException.class)
   public void testDecodeObjectWithInvalidType() throws DecoderException {
   new Base64().decode(new Object());
   }
  /**
  - encode(Object) with a non-byte[] argument must throw EncoderException.
    */
   @Test(expected = EncoderException.class)
   public void testEncodeObjectWithInvalidType() throws EncoderException {
   new Base64().encode("not a byte array");
   }
  /**
  - isBase64 sanity check for valid and invalid octets.
    */
   @Test
   public void testIsBase64() {
   Assert.assertTrue(Base64.isBase64((byte) 'A'));
   Assert.assertTrue(Base64.isBase64((byte) '='));
   Assert.assertFalse(Base64.isBase64((byte) '\n'));
   Assert.assertFalse(Base64.isBase64((byte) '@'));
   }
  /**
  - High-level static encodeBase64 methods should behave as expected.
    */
   @Test
   public void testStaticMethods() {
   byte[] data = "Some static data".getBytes();
   // encodeBase64(byte[])
   byte[] enc1 = Base64.encodeBase64(data);
   Assert.assertNotNull(enc1);
   // encodeBase64String
   String encStr = Base64.encodeBase64String(data);
   Assert.assertNotNull(encStr);
   Assert.assertEquals(new String(enc1, java.nio.charset.StandardCharsets.UTF_8), encStr);
   // encodeBase64Chunked
   byte[] encChunk = Base64.encodeBase64Chunked(data);
   Assert.assertNotNull(encChunk);
   Assert.assertTrue(encChunk.length >= enc1.length); // chunked adds separators
   }

}
