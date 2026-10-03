import static org.junit.Assert.*;

 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;

 import org.apache.commons.codec.binary.Base32;
 import org.apache.commons.codec.binary.Base32InputStream;
 import org.junit.Test;

 /**
  * Tests for {@link BaseNCodecInputStream} via its concrete subclass {@link Base32InputStream}.
  * The tests target the bugs described in CODEC-130.
  */
 public class BaseNCodecInputStreamTest {

     // ------------------------------------------------------------------- helpers

     private Base32InputStream createBase32InputStream(final String raw) throws IOException {
         final byte[] rawBytes = raw.getBytes("UTF-8");
         final String encoded = Base32.encodeBase32String(rawBytes);
         return new Base32InputStream(
                 new ByteArrayInputStream(encoded.getBytes("US-ASCII")), false);
     }

     private static byte[] readAll(final InputStream is) throws IOException {
         final ByteArrayOutputStream bos = new ByteArrayOutputStream();
         int n;
         final byte[] buf = new byte[4096];
         while ((n = is.read(buf)) != -1) {
             bos.write(buf, 0, n);
         }
         return bos.toByteArray();
     }

     // ------------------------------------------------------------------- tests

     @Test
     public void testMarkSupported() throws IOException {
         final Base32InputStream in = createBase32InputStream("test");
         assertFalse(in.markSupported());
         in.close();
     }

     @Test
     public void testAvailableInitial() throws IOException {
         final Base32InputStream in = createBase32InputStream("Hello");
         assertTrue("available() should be > 0 for non-empty stream", in.available() > 0);
         in.close();
     }

     @Test
     public void testAvailableAfterReadingAlmostAll() throws IOException {
         final String raw = "Hello";
         // total decoded length obtained from a fresh stream
         final int totalLen = readAll(createBase32InputStream(raw)).length;
         assertTrue(totalLen > 1);

         final Base32InputStream in = createBase32InputStream(raw);
         final byte[] buf = new byte[totalLen - 1];
         final int n = in.read(buf);
         assertEquals("read count", totalLen - 1, n);
         assertEquals("available should be 1 after reading until 1 byte remains", 1,
in.available());
         in.close();
     }

     @Test
     public void testSkipZero() throws IOException {
         final Base32InputStream in = createBase32InputStream("test");
         assertEquals(0, in.skip(0));
         in.close();
     }

     @Test(expected = IllegalArgumentException.class)
     public void testSkipNegative() throws IOException {
         final Base32InputStream in = createBase32InputStream("test");
         try {
             in.skip(-1L);
         } finally {
             in.close();
         }
     }

     @Test
     public void testSkipToEnd() throws IOException {
         final String raw = "Hello";
         final int totalLen = readAll(createBase32InputStream(raw)).length;
         final Base32InputStream in = createBase32InputStream(raw);
         final long skipped = in.skip(totalLen);
         assertEquals("skip should return the number of decoded bytes skipped", totalLen, skipped);
         assertEquals("read after skipping to end must return -1", -1, in.read());
         in.close();
     }

     @Test
     public void testSkipPastEnd() throws IOException {
         final String raw = "Hello";
         final int totalLen = readAll(createBase32InputStream(raw)).length;
         final Base32InputStream in = createBase32InputStream(raw);
         final long skipped = in.skip(totalLen + 100L);
         assertEquals("skip beyond end must return only available bytes", totalLen, skipped);
         assertEquals("after skip past end read must return -1", -1, in.read());
         in.close();
     }

     @Test
     public void testSkipBig() throws IOException {
         final String raw = "Hello";
         final int totalLen = readAll(createBase32InputStream(raw)).length;
         final Base32InputStream in = createBase32InputStream(raw);
         final long skipped = in.skip(Long.MAX_VALUE);
         assertEquals("skip(Long.MAX_VALUE) must return only the available bytes", totalLen,
skipped);
         assertEquals(-1, in.read());
         in.close();
     }

     @Test
     public void testReadAfterSkipDataIntegrity() throws IOException {
         // CODEC-130: skip + read must return correct remaining decoded bytes
         final String raw = "Hello World";
         final byte[] fullRaw = raw.getBytes("UTF-8");
         final Base32InputStream in = createBase32InputStream(raw);
         // skip first byte
         final long skipped = in.skip(1);
         assertEquals("should have skipped 1 decoded byte", 1, skipped);
         final byte[] remaining = readAll(in);
         final byte[] expected = new byte[fullRaw.length - 1];
         System.arraycopy(fullRaw, 1, expected, 0, expected.length);
         assertArrayEquals("after skipping 1 byte, remaining data must match expected suffix",
expected, remaining);
         in.close();
     }

     @Test
     public void testReadSingleByte() throws IOException {
         final Base32InputStream in = createBase32InputStream("A");
         assertEquals((int) 'A', in.read());
         assertEquals(-1, in.read());
         in.close();
     }

     @Test
     public void testReadByteArray() throws IOException {
         final Base32InputStream in = createBase32InputStream("AB");
         final byte[] buf = new byte[10];
         final int n = in.read(buf);
         assertEquals(2, n);
         assertEquals('A', buf[0]);
         assertEquals('B', buf[1]);
         assertEquals(-1, in.read());
         in.close();
     }

     @Test
     public void testReadAll() throws IOException {
         final String raw = "Hello World";
         final byte[] expected = raw.getBytes("UTF-8");
         final Base32InputStream in = createBase32InputStream(raw);
         final byte[] actual = readAll(in);
         assertArrayEquals(expected, actual);
         in.close();
     }
 }
