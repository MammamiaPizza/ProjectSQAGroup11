package org.apache.commons.codec.binary;

 import static org.junit.Assert.*;

 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.util.Arrays;

 import org.junit.Test;

 /**
  * Tests for CODEC-105: ArrayIndexOutOfBoundsException in Base64InputStream
  * when using line separators with various data sizes.
  */
 public class Base64InputStreamTest {

     /**
      * Direct reproduction of CODEC-105: decode with line separator at various
      * data sizes that may trigger the ArrayIndexOutOfBoundsException.
      */
     @Test
     public void testCodec105DecodeWithLineSeparator() throws IOException {
         byte[] lineSeparator = new byte[] { '\r', '\n' };
         int[] sizes = { 1, 2, 3, 63, 64, 65, 75, 76, 77, 127, 128, 255, 256 };
         for (int dataLen : sizes) {
             byte[] original = new byte[dataLen];
             for (int i = 0; i < dataLen; i++) {
                 original[i] = (byte) (i & 0xFF);
             }
             Base64 encoder = new Base64(64, lineSeparator);
             byte[] encoded = encoder.encode(original);

             ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
             Base64InputStream decoder = new Base64InputStream(bais, false, 64, lineSeparator);

             byte[] result = new byte[dataLen + 10];
             int total = 0;
             int r;
             while ((r = decoder.read(result, total, result.length - total)) != -1) {
                 total += r;
             }
             decoder.close();
             assertArrayEquals("Decode mismatch at length " + dataLen, original,
                     Arrays.copyOf(result, total));
         }
     }

     /**
      * Encode path through the stream with line separator — this exercises the
      * other direction that shares the same buffer management code paths.
      * The encoder output is verified by a full decode roundtrip.
      */
     @Test
     public void testCodec105EncodeWithLineSeparator() throws IOException {
         byte[] lineSeparator = new byte[] { '\r', '\n' };
         int[] sizes = { 1,63, 64, 65, 128, 256 };
         for (int dataLen : sizes) {
             byte[] original = new byte[dataLen];
             for (int i = 0; i < dataLen; i++) {
                 original[i] = (byte) ((i + 42) & 0xFF);
             }

             ByteArrayInputStream bais = new ByteArrayInputStream(original);
             Base64InputStream encoder = new Base64InputStream(bais, true, 64, lineSeparator);

             // Encode using the stream
             byte[] encBuf = new byte[4096];
             int encTotal = 0;
             int r;
             while ((r = encoder.read(encBuf, encTotal, encBuf.length - encTotal)) != -1) {
                 encTotal += r;
             }
             encoder.close();
             byte[] encoded = Arrays.copyOf(encBuf, encTotal);

             // Decode back using a stream to verify correctness
             ByteArrayInputStream decIn = new ByteArrayInputStream(encoded);
             Base64InputStream decoder = new Base64InputStream(decIn, false, 64, lineSeparator);
             byte[] decBuf = new byte[original.length + 10];
             int decTotal = 0;
             while ((r = decoder.read(decBuf, decTotal, decBuf.length - decTotal)) != -1) {
                 decTotal += r;
             }
             decoder.close();
             assertArrayEquals("Encode roundtrip mismatch at length " + dataLen, original,
                     Arrays.copyOf(decBuf, decTotal));
         }
     }

     /**
      * Decode via single-byte read() — exercises read(byte[],0,1) where
      * b.length==len, causing setInitialBuffer to be called with a 1-byte array.
      */
     @Test
     public void testSingleByteReadDecode() throws IOException {
         byte[] original = new byte[] { 0, 42, 127, -1, -128, (byte) 255, 100 };
         byte[] encoded = Base64.encodeBase64(original);

         ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
         Base64InputStream stream = new Base64InputStream(bais, false);

         for (int i = 0; i < original.length; i++) {
             int r = stream.read();
             assertTrue("Premature EOF at index " + i, r >= 0);
             assertEquals("Mismatch at index " + i, original[i] & 0xFF, r);
         }
         assertEquals(-1, stream.read());
     }

     /**
      * Encode via single-byte read() — collect bytes and compare to expected.
      */
     @Test
     public void testSingleByteReadEncode() throws IOException {
         byte[] original = new byte[] { 1, 2, 3, 4, 5 };
         byte[] expected = Base64.encodeBase64(original);

         ByteArrayInputStream bais = new ByteArrayInputStream(original);
         Base64InputStream stream = new Base64InputStream(bais, true);

         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         int b;
         while ((b = stream.read()) != -1) {
             baos.write(b);
         }
         byte[] actual = baos.toByteArray();
         assertArrayEquals(expected, actual);
     }

     /**
      * Empty stream in both encode and decode modes.
      */
     @Test
     public void testEmptyStream() throws IOException {
         // Encode
         Base64InputStream enc = new Base64InputStream(
                 new ByteArrayInputStream(new byte[0]), true);
         assertEquals(-1, enc.read());
         assertEquals(-1, enc.read(new byte[10], 0, 10));

         // Decode
         Base64InputStream dec = new Base64InputStream(
                 new ByteArrayInputStream(new byte[0]), false);
         assertEquals(-1, dec.read());
         assertEquals(-1, dec.read(new byte[10], 0, 10));
     }

     /**
      * read(byte[],int,int) with offset+len reaching the array edge.
      */
     @Test
     public void testReadOffsetAtArrayEdge() throws IOException {
         byte[] original = new byte[50];
         for (int i = 0; i < 50; i++) {
             original[i] = (byte) (i + 10);
         }
         byte[] encoded = Base64.encodeBase64(original);

         ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
         Base64InputStream stream = new Base64InputStream(bais, false);

         byte[] buf = new byte[100];
         // Read exactly into positions 10..59
         int r = stream.read(buf, 10, 50);
         assertEquals(50, r);

         byte[] expectedBuf = new byte[100];
         System.arraycopy(original, 0, expectedBuf, 10, 50);
         assertArrayEquals(expectedBuf, buf);
     }

     /**
      * len=0 must return 0 without consuming input.
      */
     @Test
     public void testReadLenZero() throws IOException {
         byte[] data = "Hello".getBytes();
         Base64InputStream stream = new Base64InputStream(
                 new ByteArrayInputStream(data), true);
         assertEquals(0, stream.read(new byte[10], 0, 0));
         assertEquals(0, stream.read(new byte[10], 5, 0));
         // Stream should still be readable afterwards
         assertTrue(stream.read() >= 0);
     }

     /**
      * null array must throw NullPointerException.
      */
     @Test(expected = NullPointerException.class)
     public void testReadNullArray() throws IOException {
         Base64InputStream stream = new Base64InputStream(
                 new ByteArrayInputStream(new byte[0]), true);
         stream.read(null, 0, 10);
     }

     /**
      * Decode with single-byte line separator (\n only) to exercise different
      * encodeSize/decodeSize values.
      */
     @Test
     public void testDecodeWithNewlineSeparator() throws IOException {
         byte[] lineSep = new byte[] { '\n' };
         for (int size : new int[] { 32, 64, 96, 128 }) {
             byte[] original = new byte[size];
             Arrays.fill(original, (byte) 'A');
             Base64 enc = new Base64(64, lineSep);
             byte[] encoded = enc.encode(original);

             ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
             Base64InputStream dec = new Base64InputStream(bais, false, 64, lineSep);

             byte[] result = new byte[size + 10];
             int total = 0;
             int r;
             while ((r = dec.read(result, total, result.length - total)) != -1) {
                 total += r;
             }
             dec.close();
             assertArrayEquals("Size " + size, original, Arrays.copyOf(result, total));
         }
     }

     /**
      * Full roundtrip encode-then-decode through streams without line separator.
      */
     @Test
     public void testFullRoundtripNoLineSeparator() throws IOException {
         byte[] original = new byte[200];
         for (int i = 0; i < 200; i++) {
             original[i] = (byte) (i & 0xFF);
         }

         // Encode
         ByteArrayInputStream encIn = new ByteArrayInputStream(original);
         Base64InputStream encStream = new Base64InputStream(encIn, true);
         int encSize = ((original.length + 2) / 3) * 4;
         byte[] encBuf = new byte[encSize + 10];
         int encTotal = 0;
         int r;
         while ((r = encStream.read(encBuf, encTotal, encBuf.length - encTotal)) != -1) {
             encTotal += r;
         }
         encStream.close();
         byte[] encoded = Arrays.copyOf(encBuf, encTotal);

         // Decode
         ByteArrayInputStream decIn = new ByteArrayInputStream(encoded);
         Base64InputStream decStream = new Base64InputStream(decIn, false);
         byte[] decBuf = new byte[original.length + 10];
         int decTotal = 0;
         while ((r = decStream.read(decBuf, decTotal, decBuf.length - decTotal)) != -1) {
             decTotal += r;
         }
         decStream.close();
         assertArrayEquals(original, Arrays.copyOf(decBuf, decTotal));
     }

     /**
      * urlSafe encoding/decoding via Base64 API (internally used by the stream).
      */
     @Test
     public void testUrlSafeEncoding() {
         byte[] original = new byte[64];
         for (int i = 0; i < 64; i++) {
             original[i] = (byte) (i + 1);
         }
         byte[] urlSafeEncoded = Base64.encodeBase64URLSafe(original);
         String urlSafeStr = Base64.encodeBase64URLSafeString(original);
         assertEquals(urlSafeStr, new String(urlSafeEncoded,
                 java.nio.charset.StandardCharsets.UTF_8));

         // Decode roundtrip
         Base64 b64 = new Base64(true);
         byte[] decoded = b64.decode(urlSafeEncoded);
         assertArrayEquals(original, decoded);

         // Verify no '+' or '/' in urlSafe output
         for (byte b : urlSafeEncoded) {
             if (b != '=') {
                 assertTrue("URL-safe output contains non-URL-safe char: " + (char) b,
                         b == '-' || b == '_' || Character.isLetterOrDigit(b));
             }
         }
     }

     /**
      * maxResultSize limit in encodeBase64 overloads.
      */
     @Test
     public void testMaxResultSize() {
         byte[] data = new byte[100];
         Arrays.fill(data, (byte) 'x');
         // Too-small limit must throw
         try {
             Base64.encodeBase64(data, false, false, 10);
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("maxium")); // note: typo in original
         }
         // Adequate limit must succeed
         byte[] result = Base64.encodeBase64(data, false, false, Integer.MAX_VALUE);
         assertNotNull(result);
         assertTrue(result.length > 100);
         // Verify by decoding
         assertArrayEquals(data, Base64.decodeBase64(result));
     }
 }
