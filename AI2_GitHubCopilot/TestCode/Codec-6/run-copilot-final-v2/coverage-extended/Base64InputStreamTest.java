package org.apache.commons.codec.binary;

 import static org.junit.Assert.assertArrayEquals;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.Arrays;

 import org.junit.Test;

 /**
  * Tests for {@link Base64InputStream}.
  */
 public class Base64InputStreamTest {

     private static final String UTF_8 = "UTF-8";

     @Test
     public void testEmptyDecodeRead() throws IOException {
         // empty input stream decoding should return -1 immediately (CODEC-101 regression)
         Base64InputStream stream = new Base64InputStream(new ByteArrayInputStream(new byte[0]),
false);
         assertEquals(-1, stream.read());
         assertEquals(-1, stream.read());
     }

     @Test
     public void testEmptyDecodeReadByteArray() throws IOException {
         // read(byte[]) on empty decode stream must return -1
         Base64InputStream stream = new Base64InputStream(new ByteArrayInputStream(new byte[0]),
false);
         byte[] buf = new byte[10];
         assertEquals(-1, stream.read(buf, 0, 10));
     }

     @Test
     public void testEmptyEncodeRead() throws IOException {
         // empty input stream encoding should also return -1
         Base64InputStream stream = new Base64InputStream(new ByteArrayInputStream(new byte[0]),
true);
         assertEquals(-1, stream.read());
     }

     @Test
     public void testDecodeSingleByte() throws IOException {
         // encoded version of 'f' (102) is "Zg==" in standard base64
         byte[] encoded = "Zg==".getBytes(UTF_8);
         Base64InputStream stream = new Base64InputStream(new ByteArrayInputStream(encoded), false);
         int c = stream.read();
         assertTrue("expected a byte but got -1", c >= 0);
         assertEquals('f', c);
         assertEquals(-1, stream.read());
     }

     @Test
     public void testDecodeMultiByte() throws IOException {
         String original = "Hello World!";
         byte[] raw = original.getBytes(UTF_8);
         byte[] encoded = java.util.Base64.getEncoder().encode(raw); // ensure identical encoding
         Base64InputStream stream = new Base64InputStream(new ByteArrayInputStream(encoded), false);
         byte[] decoded = readAllBytes(stream);
         assertArrayEquals(raw, decoded);
     }

     @Test
     public void testEncodeThenDecodeRoundTrip() throws IOException {
         byte[] original = "Round-trip test data with some binary".getBytes(UTF_8);
         // encode via Base64InputStream
         Base64InputStream encoder =
                 new Base64InputStream(new ByteArrayInputStream(original), true);
         byte[] encoded = readAllBytes(encoder);
         // decode back
         Base64InputStream decoder =
                 new Base64InputStream(new ByteArrayInputStream(encoded), false);
         byte[] decoded = readAllBytes(decoder);
         assertArrayEquals(original, decoded);
     }

     @Test
     public void testReadLenZeroReturnsZero() throws IOException {
         byte[] buf = new byte[4];
         // any input, len=0 must return 0 per InputStream contract
         Base64InputStream decodeStream = new Base64InputStream(new
ByteArrayInputStream("Zg==".getBytes(UTF_8)),
                 false);
         assertEquals(0, decodeStream.read(buf, 0, 0));
         // also test with encode path
         Base64InputStream encodeStream = new Base64InputStream(new ByteArrayInputStream(new
byte[5]), true);
         assertEquals(0, encodeStream.read(buf, 0, 0));
     }

     @Test
     public void testReadNullArrayThrowsNPE() throws IOException {
         Base64InputStream stream = new Base64InputStream(new ByteArrayInputStream(new byte[1]),
false);
         try {
             stream.read(null, 0, 1);
             fail("expected NullPointerException");
         } catch (NullPointerException e) {
             // expected
         }
     }

     @Test
     public void testReadInvalidOffsetOrLenThrowsIOOB() throws IOException {
         Base64InputStream stream = new Base64InputStream(new ByteArrayInputStream(new byte[1]),
false);
         byte[] buf = new byte[4];

         // negative offset
         try {
             stream.read(buf, -1, 2);
             fail("expected IndexOutOfBoundsException");
         } catch (IndexOutOfBoundsException e) { }

         // negative length
         try {
             stream.read(buf, 0, -1);
             fail("expected IndexOutOfBoundsException");
         } catch (IndexOutOfBoundsException e) { }

         // offset beyond length
         try {
             stream.read(buf, buf.length + 1, 0);
             fail("expected IndexOutOfBoundsException");
         } catch (IndexOutOfBoundsException e) { }

         // offset+len beyond length
         try {
             stream.read(buf, 0, buf.length + 1);
             fail("expected IndexOutOfBoundsException");
         } catch (IndexOutOfBoundsException e) { }
     }

     @Test
     public void testReadAfterEOFAlwaysReturnsMinusOne() throws IOException {
         Base64InputStream stream = new Base64InputStream(new ByteArrayInputStream(new byte[0]),
false);
         assertEquals(-1, stream.read());
         assertEquals(-1, stream.read());
         byte[] buf = new byte[4];
         assertEquals(-1, stream.read(buf, 0, 4));
     }

     @Test
     public void testMarkSupportedReturnsFalse() {
         Base64InputStream stream = new Base64InputStream(new ByteArrayInputStream(new byte[1]),
false);
         assertFalse(stream.markSupported());
     }

     @Test
     public void testEncodeWithLineLengthConstructor() throws IOException {
         // line length 4, separator "`" for a deterministic, simple test
         byte[] original = "Hello World!".getBytes(UTF_8);
         byte[] sep = "`".getBytes(UTF_8);
         Base64InputStream encoder =
                 new Base64InputStream(new ByteArrayInputStream(original), true, 4, sep);
         byte[] encoded = readAllBytes(encoder);
         assertNotNull(encoded);
         assertTrue("encoded output should not be empty", encoded.length > 0);

         // ensure decoding back yields original
         Base64InputStream decoder = new Base64InputStream(new ByteArrayInputStream(encoded),
false);
         byte[] decoded = readAllBytes(decoder);
         assertArrayEquals(original, decoded);
     }

     // helper to consume all bytes from an input stream
     private static byte[] readAllBytes(InputStream in) throws IOException {
         byte[] buffer = new byte[1024];
         int total = 0;
         int read;
         while ((read = in.read(buffer, total, buffer.length - total)) > 0) {
             total += read;
             if (total == buffer.length) {
                 buffer = Arrays.copyOf(buffer, buffer.length * 2);
             }
         }
         return Arrays.copyOf(buffer, total);
     }

@Test
public void testDefaultConstructorDecode() throws Exception {
    // "dGVzdA==" is base64 for "test"
    byte[] encoded = "dGVzdA==".getBytes("US-ASCII");
    java.io.InputStream in = new java.io.ByteArrayInputStream(encoded);
    Base64InputStream stream = new Base64InputStream(in);
    byte[] buf = new byte[100];
    int len = stream.read(buf);
    String result = new String(buf, 0, len, "US-ASCII");
    assertEquals("test", result);
}

@Test
public void testReadHighByteConversion() throws Exception {
    // "gA==" is base64 for byte 0x80 (128)
    byte[] encoded = "gA==".getBytes("US-ASCII");
    java.io.InputStream in = new java.io.ByteArrayInputStream(encoded);
    Base64InputStream stream = new Base64InputStream(in, false);
    int c = stream.read();
    assertTrue("Expected a non-EOF high byte", c >= 0);
    assertEquals(128, c);
    assertEquals(-1, stream.read());
}

@Test
public void testReadAfterEOFByteArray() throws Exception {
    // "SGVsbG8=" is base64 for "Hello"
    byte[] encoded = "SGVsbG8=".getBytes("US-ASCII");
    java.io.InputStream in = new java.io.ByteArrayInputStream(encoded);
    Base64InputStream stream = new Base64InputStream(in, false);
    byte[] buf = new byte[100];
    int total = stream.read(buf);
    assertEquals(5, total);
    // read past EOF
    int after = stream.read(buf);
    assertEquals(-1, after);
}

@Test
public void testReadNonEqualBufferLen() throws Exception {
    // "SGVsbG8=" decodes to "Hello" (5 bytes)
    byte[] encoded = "SGVsbG8=".getBytes("US-ASCII");
    java.io.InputStream in = new java.io.ByteArrayInputStream(encoded);
    Base64InputStream stream = new Base64InputStream(in, false);
    byte[] buf = new byte[10];   // length 10
    int len1 = stream.read(buf, 0, 3);  // len=3 < buf.length
    assertEquals(3, len1);
    assertEquals('H', buf[0]);
    assertEquals('e', buf[1]);
    assertEquals('l', buf[2]);
    int len2 = stream.read(buf, 0, 10);
    assertEquals(2, len2);
    assertEquals('l', buf[0]);
    assertEquals('o', buf[1]);
    assertEquals(-1, stream.read());
}
}
