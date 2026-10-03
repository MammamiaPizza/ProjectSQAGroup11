package org.apache.commons.compress.compressors.bzip2;

 import static org.junit.Assert.*;

 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import org.junit.Test;

 public class BZip2CompressorInputStreamTest {

     private byte[] compress(byte[] data) throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try (BZip2CompressorOutputStream out = new BZip2CompressorOutputStream(baos)) {
             out.write(data);
         }
         return baos.toByteArray();
     }

     private BZip2CompressorInputStream createInputStream(byte[] data) throws IOException {
         byte[] compressed = compress(data);
         return new BZip2CompressorInputStream(new ByteArrayInputStream(compressed));
     }

     private byte[] readAll(BZip2CompressorInputStream in) throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         byte[] buf = new byte[512];
         int n;
         while ((n = in.read(buf)) != -1) {
             baos.write(buf, 0, n);
         }
         return baos.toByteArray();
     }

     // Trigger: len=0 must return 0 even when the stream has data (InputStream contract)
     @Test
     public void readOfLength0ShouldReturn0() throws IOException {
         byte[] data = "test".getBytes();
         BZip2CompressorInputStream in = createInputStream(data);
         try {
             byte[] dest = new byte[10];
             assertEquals(0, in.read(dest, 0, 0));
         } finally {
             in.close();
         }
     }

     @Test
     public void testReadZeroLengthAtEOFReturnsZero() throws IOException {
         byte[] data = "abc".getBytes();
         BZip2CompressorInputStream in = createInputStream(data);
         readAll(in); // exhaust the stream
         try {
             byte[] dest = new byte[5];
             assertEquals(0, in.read(dest, 0, 0));
         } finally {
             in.close();
         }
     }

     @Test(expected = IOException.class)
     public void testReadZeroLengthAfterCloseThrowsIOException() throws IOException {
         byte[] data = "data".getBytes();
         BZip2CompressorInputStream in = createInputStream(data);
         in.close();
         in.read(new byte[1], 0, 0);
     }

     @Test(expected = IndexOutOfBoundsException.class)
     public void testReadNegativeLengthThrowsIOOB() throws IOException {
         byte[] data = "x".getBytes();
         BZip2CompressorInputStream in = createInputStream(data);
         try {
             in.read(new byte[5], 0, -1);
         } finally {
             in.close();
         }
     }

     @Test(expected = IndexOutOfBoundsException.class)
     public void testReadNegativeOffsetThrowsIOOB() throws IOException {
         byte[] data = "x".getBytes();
         BZip2CompressorInputStream in = createInputStream(data);
         try {
             in.read(new byte[5], -1, 1);
         } finally {
             in.close();
         }
     }

     @Test(expected = IndexOutOfBoundsException.class)
     public void testReadOffsetPlusLengthExceedLength() throws IOException {
         byte[] data = "data".getBytes();
         BZip2CompressorInputStream in = createInputStream(data);
         try {
             in.read(new byte[5], 3, 3); // offs + len = 6 > array length 5
         } finally {
             in.close();
         }
     }

     @Test
     public void testReadReturnsDataAndEOF() throws IOException {
         byte[] original = "Hello BZip2!".getBytes();
         BZip2CompressorInputStream in = createInputStream(original);
         try {
             ByteArrayOutputStream result = new ByteArrayOutputStream();
             int b;
             while ((b = in.read()) != -1) {
                 result.write(b);
             }
             assertArrayEquals(original, result.toByteArray());
         } finally {
             in.close();
         }
     }

     @Test
     public void testReadByteArrayReturnsData() throws IOException {
         byte[] original = "compression test".getBytes();
         BZip2CompressorInputStream in = createInputStream(original);
         try {
             byte[] buf = new byte[100];
             int total = 0;
             int n;
             while ((n = in.read(buf)) != -1) {
                 total += n;
             }
             assertEquals(original.length, total);
         } finally {
             in.close();
         }
     }

     @Test
     public void testReadZeroLengthWithNonZeroOffsetReturnsZero() throws IOException {
         byte[] data = "test".getBytes();
         BZip2CompressorInputStream in = createInputStream(data);
         try {
             byte[] dest = new byte[10];
             assertEquals(0, in.read(dest, 5, 0));
         } finally {
             in.close();
         }
     }

     @Test(expected = IOException.class)
     public void testReadAfterCloseThrowsIOException() throws IOException {
         byte[] data = "x".getBytes();
         BZip2CompressorInputStream in = createInputStream(data);
         in.close();
         in.read();
     }

     @Test
     public void testCloseMultipleTimes() throws IOException {
         byte[] data = "x".getBytes();
         BZip2CompressorInputStream in = createInputStream(data);
         in.close();
         in.close(); // must not throw
     }

     @Test
     public void testReadZeroLengthAfterPartialReadReturnsZero() throws IOException {
         byte[] data = "more data than one byte".getBytes();
         BZip2CompressorInputStream in = createInputStream(data);
         try {
             in.read(); // consume one byte
             byte[] dest = new byte[5];
             assertEquals(0, in.read(dest, 0, 0));
         } finally {
             in.close();
         }
     }
 }
