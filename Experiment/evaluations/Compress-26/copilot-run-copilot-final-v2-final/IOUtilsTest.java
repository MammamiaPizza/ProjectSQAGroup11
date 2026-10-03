package org.apache.commons.compress.utils;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;

 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.io.InputStream;

 import org.junit.Test;

 public class IOUtilsTest {

     /**
      * When skip() always returns 0, the method must fall back to read().
      * Bug: breaks immediately and returns 0 instead of reading.
      */
     @Test
     public void skipUsingRead() throws IOException {
         InputStream in = new InputStream() {
             int pos = 0;
             @Override
             public int read() throws IOException {
                 if (pos >= 20) return -1;
                 pos++;
                 return 0;
             }
             @Override
             public long skip(long n) throws IOException {
                 return 0;
             }
         };
         assertEquals(10, IOUtils.skip(in, 10));
     }

     /**
      * When skip() returns partial then 0, method must combine skip+read.
      * Bug: returns only the skipped amount (5), not full amount (10).
      */
     @Test
     public void skipUsingSkipAndRead() throws IOException {
         InputStream in = new InputStream() {
             int pos = 0;
             int skipCalls = 0;
             @Override
             public int read() throws IOException {
                 if (pos >= 20) return -1;
                 pos++;
                 return 0;
             }
             @Override
             public long skip(long n) throws IOException {
                 skipCalls++;
                 if (skipCalls == 1) {
                     pos += 5;
                     return 5;
                 }
                 return 0;
             }
         };
         assertEquals(10, IOUtils.skip(in, 10));
     }

     /**
      * Normal case: skip() returns all requested bytes at once.
      */
     @Test
     public void skipFullAmount() throws IOException {
         InputStream in = new ByteArrayInputStream(new byte[100]);
         assertEquals(50, IOUtils.skip(in, 50));
     }

     /**
      * Skip returns 1 byte at a time; should accumulate correctly.
      */
     @Test
     public void skipOneByteAtATime() throws IOException {
         InputStream in = new InputStream() {
             int pos = 0;
             @Override
             public int read() throws IOException {
                 return pos++ < 20 ? 0 : -1;
             }
             @Override
             public long skip(long n) throws IOException {
                 return n > 0 ? 1 : 0;
             }
         };
         assertEquals(10, IOUtils.skip(in, 10));
     }

     /**
      * Skip zero bytes: should return 0 immediately.
      */
     @Test
     public void skipZeroBytes() throws IOException {
         InputStream in = new ByteArrayInputStream(new byte[10]);
         assertEquals(0, IOUtils.skip(in, 0));
     }

     /**
      * Negative numToSkip: while-loop condition never true, returns 0.
      */
     @Test
     public void skipNegativeBytes() throws IOException {
         InputStream in = new ByteArrayInputStream(new byte[10]);
         assertEquals(0, IOUtils.skip(in, -5));
     }

     /**
      * Empty stream: requested bytes exceed available, skip returns 0.
      */
     @Test
     public void skipEmptyStream() throws IOException {
         InputStream in = new ByteArrayInputStream(new byte[0]);
         assertEquals(0, IOUtils.skip(in, 10));
     }

     /**
      * Stream has exactly the number of bytes requested.
      */
     @Test
     public void skipExactBytesAvailable() throws IOException {
         InputStream in = new ByteArrayInputStream(new byte[10]);
         assertEquals(10, IOUtils.skip(in, 10));
     }

     /**
      * Read fallback encounters EOF before requested bytes are skipped.
      * Bug: breaks on first 0-skip and returns 0 instead of available bytes.
      */
     @Test
     public void skipReadFallbackWithEOF() throws IOException {
         InputStream in = new InputStream() {
             int pos = 0;
             @Override
             public int read() throws IOException {
                 if (pos >= 5) return -1;
                 pos++;
                 return 0;
             }
             @Override
             public long skip(long n) throws IOException {
                 return 0;
             }
         };
         assertEquals(5, IOUtils.skip(in, 20));
     }

     /**
      * Mixed skip and read fallback hitting EOF during the read phase.
      * Bug: returns only 3 (from skip) instead of 8 (3 skip +5 read).
      */
     @Test
     public void skipMixedThenEOFInRead() throws IOException {
         InputStream in = new InputStream() {
             int pos = 0;
             int skipCalls = 0;
             @Override
             public int read() throws IOException {
                 if (pos >= 8) return -1;
                 pos++;
                 return 0;
             }
             @Override
             public long skip(long n) throws IOException {
                 skipCalls++;
                 if (skipCalls == 1) {
                     pos += 3;
                     return 3;
                 }
                 return 0;
             }
         };
         assertEquals(8, IOUtils.skip(in, 20));
     }

     /**
      * Verify stream position is correct after skip by reading remaining bytes.
      */
     @Test
     public void verifyStreamPositionAfterSkip() throws IOException {
         byte[] data = new byte[20];
         for (int i = 0; i < 20; i++) data[i] = (byte) i;
         ByteArrayInputStream in = new ByteArrayInputStream(data);
         IOUtils.skip(in, 7);
         assertEquals(7, in.read());
     }

@org.junit.Test
    public void testCopyDefault() throws java.io.IOException {
        byte[] data = "test".getBytes("US-ASCII");
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        long count = IOUtils.copy(in, out);
        org.junit.Assert.assertEquals(data.length, count);
        org.junit.Assert.assertArrayEquals(data, out.toByteArray());
    }

 @org.junit.Test
 public void testCopyWithSmallBuffer() throws java.io.IOException {
     byte[] data = "abcde".getBytes("US-ASCII");
     java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
     java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
     long count = IOUtils.copy(in, out, 1);
     org.junit.Assert.assertEquals(data.length, count);
     org.junit.Assert.assertArrayEquals(data, out.toByteArray());
 }

 @org.junit.Test
 public void testReadFully() throws java.io.IOException {
     byte[] data = "data".getBytes("US-ASCII");
     java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
     byte[] buf = new byte[4];
     int n = IOUtils.readFully(in, buf);
     org.junit.Assert.assertEquals(4, n);
     org.junit.Assert.assertArrayEquals(data, buf);
 }

 @org.junit.Test
 public void testCloseQuietly() {
     java.io.Closeable throwing = new java.io.Closeable() {
         public void close() throws java.io.IOException {
             throw new java.io.IOException("fail");
         }
     };
     IOUtils.closeQuietly(throwing);
     IOUtils.closeQuietly(null);
 }
}
