import static org.junit.Assert.*;

 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.Arrays;

 import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
 import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
 import org.junit.Test;

 /**
  * Tests for BZip2CompressorInputStream focusing on COMPRESS-253:
  * truncated bzip2 input must not throw IOException; it must return -1
  * after the last available decompressed byte.
  */
 public class BZip2CompressorInputStreamTest {

     private static byte[] compress(String s) throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         BZip2CompressorOutputStream bzOut = new BZip2CompressorOutputStream(baos);
         try {
             bzOut.write(s.getBytes("UTF-8"));
         } finally {
             bzOut.close();
         }
         return baos.toByteArray();
     }

     private static String generateLongString(int length) {
         StringBuilder sb = new StringBuilder(length);
         for (int i = 0; i < length; i++) {
             sb.append('a');
         }
         return sb.toString();
     }

     // --- correctness baseline ---
     @Test
     public void testFullStreamDecompressesCorrectly() throws IOException {
         String original = "Hello BZip2!";
         byte[] compressed = compress(original);
         BZip2CompressorInputStream in = new BZip2CompressorInputStream(
                 new ByteArrayInputStream(compressed));
         try {
             ByteArrayOutputStream result = new ByteArrayOutputStream();
             byte[] buf = new byte[4096];
             int n;
             while ((n = in.read(buf)) != -1) {
                 result.write(buf, 0, n);
             }
             assertEquals(original, new String(result.toByteArray(), "UTF-8"));
         } finally {
             in.close();
         }
     }

     // --- truncated stream, bulk read ---
     @Test
     public void testTruncatedStreamReadBulk() throws IOException {
         String original = generateLongString(2000);
         byte[] compressed = compress(original);
         // truncate near the end to keep the block header and most data
         byte[] truncated = Arrays.copyOf(compressed, Math.max(0, compressed.length - 20));
         BZip2CompressorInputStream in = new BZip2CompressorInputStream(
                 new ByteArrayInputStream(truncated));
         try {
             byte[] buf = new byte[4096];
             int total = 0;
             int last;
             while ((last = in.read(buf)) != -1) {
                 total += last;
             }
             // after fix we reach here without IOException; must have decoded >0 bytes
             assertTrue("should have decompressed some bytes before EOF", total > 0);
         } finally {
             in.close();
         }
     }

     // --- truncated stream, single-byte read ---
     @Test
     public void testTruncatedStreamReadSingle() throws IOException {
         String original = generateLongString(2000);
         byte[] compressed = compress(original);
         byte[] truncated = Arrays.copyOf(compressed, Math.max(0, compressed.length - 20));
         BZip2CompressorInputStream in = new BZip2CompressorInputStream(
                 new ByteArrayInputStream(truncated));
         try {
             int bytesRead = 0;
             while (in.read() != -1) {
                 bytesRead++;
             }
             assertTrue("should have read at least one byte", bytesRead > 0);
         } finally {
             in.close();
         }
     }

     // --- read partial then continue (no IOException) ---
     @Test
     public void testPartialReadThenContinue() throws IOException {
         String original = generateLongString(2000);
         byte[] compressed = compress(original);
         byte[] truncated = Arrays.copyOf(compressed, Math.max(0, compressed.length - 20));
         BZip2CompressorInputStream in = new BZip2CompressorInputStream(
                 new ByteArrayInputStream(truncated));
         try {
             byte[] buf = new byte[3];
             int r1 = in.read(buf);
             // with enough data the first read will return >0
             if (r1 > 0) {
                 int r2 = in.read(buf);
                 // even if r2 is -1, no IOException must be thrown
                 assertTrue("second read must not throw", r2 >= -1);
             }
         } finally {
             in.close();
         }
     }

     // --- header-only stream (invalid, must throw IOException) ---
     @Test(expected = IOException.class)
     public void testHeaderOnlyNoBlocks() throws IOException {
         byte[] header = new byte[] { 'B', 'Z', 'h', '1' };
         BZip2CompressorInputStream in = new BZip2CompressorInputStream(
                 new ByteArrayInputStream(header));
         byte[] buf = new byte[1024];
         in.read(buf);
         in.close();
     }

     // --- empty stream ---
     @Test(expected = IOException.class)
     public void testEmptyStreamThrowsIOException() throws IOException {
         new BZip2CompressorInputStream(new ByteArrayInputStream(new byte[0]));
     }

     // --- null stream in constructor ---
     @Test(expected = IOException.class)
     public void testNullStreamThrows() throws IOException {
         new BZip2CompressorInputStream(null);
     }

     // --- close and read after close ---
     @Test(expected = IOException.class)
     public void testReadAfterCloseThrows() throws IOException {
         byte[] compressed = compress("data");
         BZip2CompressorInputStream in = new BZip2CompressorInputStream(
                 new ByteArrayInputStream(compressed));
         in.close();
         in.read(); // must throw
     }

     // --- read(byte[], int, int) with invalid offset ---
     @Test(expected = IndexOutOfBoundsException.class)
     public void testReadInvalidOffset() throws IOException {
         byte[] compressed = compress("abc");
         BZip2CompressorInputStream in = new BZip2CompressorInputStream(
                 new ByteArrayInputStream(compressed));
         try {
             byte[] buf = new byte[10];
             in.read(buf, -1, 5);
         } finally {
             in.close();
         }
     }

     // --- read(byte[], int, int) with negative length ---
     @Test(expected = IndexOutOfBoundsException.class)
     public void testReadNegativeLength() throws IOException {
         byte[] compressed = compress("abc");
         BZip2CompressorInputStream in = new BZip2CompressorInputStream(
                 new ByteArrayInputStream(compressed));
         try {
             byte[] buf = new byte[10];
             in.read(buf, 0, -3);
         } finally {
             in.close();
         }
     }

     // --- read(byte[], int, int) with off+len > dest.length ---
     @Test(expected = IndexOutOfBoundsException.class)
     public void testReadBeyondArray() throws IOException {
         byte[] compressed = compress("abc");
         BZip2CompressorInputStream in = new BZip2CompressorInputStream(
                 new ByteArrayInputStream(compressed));
         try {
             byte[] buf = new byte[10];
             in.read(buf, 8, 5);
         } finally {
             in.close();
         }
     }
 }