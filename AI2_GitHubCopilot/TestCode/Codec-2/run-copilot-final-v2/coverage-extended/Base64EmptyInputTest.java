package org.apache.commons.codec.binary;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests targeting CODEC-77: encoding an empty byte array must return
  * an empty byte array. The bug caused a spurious line separator (CR/LF)
  * to be emitted for zero-length input when lineLength &gt; 0.
  */
 public class Base64EmptyInputTest {

     // ---- Instance encode / decode ----

     @Test
     public void testEncodeEmptyByteArray() {
         Base64 b64 = new Base64();
         byte[] result = b64.encode(new byte[0]);
         assertNotNull("Encoded result must not be null", result);
         assertEquals("Empty input must produce empty output", 0, result.length);
     }

     @Test
     public void testDecodeEmptyByteArray() {
         Base64 b64 = new Base64();
         byte[] result = b64.decode(new byte[0]);
         assertNotNull("Decoded result must not be null", result);
         assertEquals("Empty base64 must decode to empty", 0, result.length);
     }

     // ---- Instance encode with line-length (streaming path) ----

     @Test
     public void testEncodeEmptyWithChunkSizeLineLength() {
         Base64 b64 = new Base64(76);
         byte[] result = b64.encode(new byte[0]);
         assertNotNull(result);
         assertEquals("Empty input with lineLength=76 must yield empty output", 0, result.length);
     }

     @Test
     public void testEncodeEmptyWithCustomLineSeparator() {
         Base64 b64 = new Base64(76, new byte[]{'\r', '\n'});
         byte[] result = b64.encode(new byte[0]);
         assertNotNull(result);
         assertEquals("Empty input with CRLF separator must yield empty output", 0, result.length);
     }

     @Test
     public void testEncodeEmptyUrlSafe() {
         Base64 b64 = new Base64(true);
         byte[] result = b64.encode(new byte[0]);
         assertNotNull(result);
         assertEquals("Empty input with URL-safe must yield empty output", 0, result.length);
     }

     @Test
     public void testEncodeEmptyWithLineLengthZeroAndUrlSafe() {
         Base64 b64 = new Base64(0, new byte[]{'\r', '\n'}, true);
         byte[] result = b64.encode(new byte[0]);
         assertNotNull(result);
         assertEquals("Empty input with lineLength=0 and urlSafe must yield empty output", 0,
result.length);
     }

     // ---- Static methods ----

     @Test
     public void testStaticEncodeBase64Empty() {
         byte[] result = Base64.encodeBase64(new byte[0]);
         assertNotNull(result);
         assertEquals(0, result.length);
     }

     @Test
     public void testStaticEncodeBase64ChunkedEmpty() {
         byte[] result = Base64.encodeBase64(new byte[0], true);
         assertNotNull(result);
         assertEquals("Chunked encoding of empty must produce empty array", 0, result.length);
     }

     @Test
     public void testStaticEncodeBase64NotChunkedEmpty() {
         byte[] result = Base64.encodeBase64(new byte[0], false);
         assertNotNull(result);
         assertEquals(0, result.length);
     }

     @Test
     public void testStaticEncodeBase64URLSafeEmpty() {
         byte[] result = Base64.encodeBase64URLSafe(new byte[0]);
         assertNotNull(result);
         assertEquals("URL-safe encoding of empty must produce empty array", 0, result.length);
     }

     @Test
     public void testStaticEncodeBase64ChunkedMethodEmpty() {
         byte[] result = Base64.encodeBase64Chunked(new byte[0]);
         assertNotNull(result);
         assertEquals("Chunked convenience method must return empty for empty input", 0,
result.length);
     }

     @Test
     public void testStaticDecodeBase64Empty() {
         byte[] result = Base64.decodeBase64(new byte[0]);
         assertNotNull(result);
         assertEquals("Decoding empty base64 must return empty", 0, result.length);
     }

@org.junit.Test
public void testDecodeObjectWithByteArray() throws org.apache.commons.codec.DecoderException {
    Base64 base64 = new Base64();
    Object result = base64.decode((Object) new byte[0]);
    org.junit.Assert.assertTrue(result instanceof byte[]);
    org.junit.Assert.assertEquals(0, ((byte[]) result).length);
}

@org.junit.Test(expected = org.apache.commons.codec.DecoderException.class)
public void testDecodeObjectWithNonByteArrayThrowsDecoderException() throws
org.apache.commons.codec.DecoderException {
    new Base64().decode(new Object());
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void testConstructorThrowsWhenLineSeparatorContainsBase64Character() {
    new Base64(76, new byte[] {'A'});
}

@org.junit.Test
public void testIsArrayByteBase64WithBase64AndNonBase64Inputs() {
    org.junit.Assert.assertTrue(Base64.isArrayByteBase64(new byte[] {'A', 'B'}));
    org.junit.Assert.assertFalse(Base64.isArrayByteBase64(new byte[] {'A', '!'}));
}
}
