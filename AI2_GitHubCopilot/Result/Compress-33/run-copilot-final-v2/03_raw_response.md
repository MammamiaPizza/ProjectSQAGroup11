package org.apache.commons.compress.compressors;

 import static org.junit.Assert.*;

 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.zip.Deflater;
 import java.util.zip.Inflater;

 import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
 import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
 import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
 import org.junit.Test;

 /**
  * Tests for {@link CompressorStreamFactory} auto-detection of raw deflate streams
  * and {@link DeflateCompressorInputStream} behavior.
  *
  * Bug COMPRESS-316: CompressorStreamFactory.createCompressorInputStream(InputStream)
  * fails to detect raw deflate streams, throwing CompressorException.
  */
 public class CompressorStreamFactoryDeflateDetectTest {

     // Magic byte signatures for various compression formats
     private static final byte[] GZIP_SIGNATURE = { (byte) 0x1f, (byte) 0x8b };
     private static final byte[] BZIP2_SIGNATURE = { 'B', 'Z' };

     /**
      * Creates raw deflate-compressed bytes from the given input bytes.
      * Raw deflate has no zlib/gzip header — just the deflate stream.
      */
     private static byte[] rawDeflate(byte[] input) throws Exception {
         Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
         deflater.setInput(input);
         deflater.finish();
         byte[] buf = new byte[1024];
         int len = deflater.deflate(buf);
         deflater.end();
         byte[] result = new byte[len];
         System.arraycopy(buf, 0, result, 0, len);
         return result;
     }

     /**
      * Creates a raw deflate-compressed byte array from a string.
      */
     private static byte[] rawDeflateString(String s) throws Exception {
         return rawDeflate(s.getBytes("UTF-8"));
     }

     /**
      * Creates an InputStream that supports mark/reset with the given bytes.
      */
     private static InputStream markedStream(byte[] data) {
         ByteArrayInputStream bais = new ByteArrayInputStream(data);
         return bais;
     }

     /**
      * Decompresses raw deflate bytes using a standard Inflater (no header).
      */
     private static byte[] decompressRawDeflate(byte[] compressed) throws Exception {
         Inflater inflater = new Inflater(true);
         inflater.setInput(compressed);
         byte[] buf = new byte[4096];
         int len = inflater.inflate(buf);
         inflater.end();
         byte[] result = new byte[len];
         System.arraycopy(buf, 0, result, 0, len);
         return result;
     }

     // ---- Test helper that asserts full cycle ----

     private void assertReadsDecompressedData(byte[] expected, CompressorInputStream cis) throws
IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         byte[] buf = new byte[1024];
         int n;
         while ((n = cis.read(buf)) != -1) {
             baos.write(buf, 0, n);
         }
         cis.close();
         assertArrayEquals(expected, baos.toByteArray());
     }

     // ===================== Tests =====================

     /**
      * Auto-detection of a raw deflate stream must return a DeflateCompressorInputStream.
      */
     @Test
     public void testDetectRawDeflateStream() throws Exception {
         byte[] original = "Hello, raw deflate world!".getBytes("UTF-8");
         byte[] compressed = rawDeflate(original);
         InputStream in = markedStream(compressed);
         CompressorStreamFactory factory = new CompressorStreamFactory();
         CompressorInputStream cis = factory.createCompressorInputStream(in);
         assertTrue("Expected DeflateCompressorInputStream for raw deflate",
                 cis instanceof DeflateCompressorInputStream);
         assertReadsDecompressedData(original, cis);
     }

     /**
      * Factory with decompressUntilEOF=true must still detect raw deflate.
      */
     @Test
     public void testDetectRawDeflateWithDecompressUntilEOF() throws Exception {
         byte[] original = "Decompress-until-eof test".getBytes("UTF-8");
         byte[] compressed = rawDeflate(original);
         InputStream in = markedStream(compressed);
         CompressorStreamFactory factory = new CompressorStreamFactory(true);
         CompressorInputStream cis = factory.createCompressorInputStream(in);
         assertTrue("Expected DeflateCompressorInputStream", cis instanceof
DeflateCompressorInputStream);
         assertReadsDecompressedData(original, cis);
     }

     /**
      * Explicitly named "deflate" must work.
      */
     @Test
     public void testExplicitDeflateByName() throws Exception {
         byte[] original = "Explicit deflate name".getBytes("UTF-8");
         byte[] compressed = rawDeflate(original);
         CompressorStreamFactory factory = new CompressorStreamFactory();
         CompressorInputStream cis = factory.createCompressorInputStream("deflate",
markedStream(compressed));
         assertTrue(cis instanceof DeflateCompressorInputStream);
         assertReadsDecompressedData(original, cis);
     }

     /**
      * Empty stream: auto-detection should fail with CompressorException (no signature).
      */
     @Test(expected = CompressorException.class)
     public void testEmptyStreamThrowsCompressorException() throws Exception {
         CompressorStreamFactory factory = new CompressorStreamFactory();
         factory.createCompressorInputStream(markedStream(new byte[0]));
     }

     /**
      * One-byte stream: not enough to identify any format.
      */
     @Test(expected = CompressorException.class)
     public void testOneByteStreamThrowsCompressorException() throws Exception {
         CompressorStreamFactory factory = new CompressorStreamFactory();
         factory.createCompressorInputStream(markedStream(new byte[] { 0x00 }));
     }

     /**
      * Unknown magic bytes must throw CompressorException with appropriate message.
      */
     @Test
     public void testUnknownMagicThrowsWithMessage() throws Exception {
         byte[] unknown = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                 (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                 (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
         CompressorStreamFactory factory = new CompressorStreamFactory();
         try {
             factory.createCompressorInputStream(markedStream(unknown));
             fail("Expected CompressorException");
         } catch (CompressorException e) {
             assertTrue("Message should indicate no compressor found",
                     e.getMessage().contains("No Compressor found"));
         }
     }

     /**
      * Truncated raw deflate data: single byte — should not auto-detect as any format.
      */
     @Test(expected = CompressorException.class)
     public void testTruncatedRawDeflateSingleByte() throws Exception {
         byte[] fullCompressed = rawDeflateString("truncated test data");
         byte[] singleByte = new byte[] { fullCompressed[0] };
         CompressorStreamFactory factory = new CompressorStreamFactory();
         factory.createCompressorInputStream(markedStream(singleByte));
     }

     /**
      * Raw deflate of a small payload (3 bytes): deflate may produce only a few bytes;
      * factory should still detect and decompress.
      */
     @Test
     public void testSmallRawDeflatePayload() throws Exception {
         byte[] original = new byte[] { 1, 2, 3 };
         byte[] compressed = rawDeflate(original);
         CompressorStreamFactory factory = new CompressorStreamFactory();
         CompressorInputStream cis = factory.createCompressorInputStream(markedStream(compressed));
         assertTrue(cis instanceof DeflateCompressorInputStream);
         assertReadsDecompressedData(original, cis);
     }

     /**
      * Known gzip signature must still be detected as gzip (not deflate).
      */
     @Test
     public void testGzipSignatureStillDetectedAsGzip() throws Exception {
         byte[] gzipData = new byte[12];
         System.arraycopy(GZIP_SIGNATURE, 0, gzipData, 0, GZIP_SIGNATURE.length);
         CompressorStreamFactory factory = new CompressorStreamFactory();
         CompressorInputStream cis = factory.createCompressorInputStream(markedStream(gzipData));
         assertTrue("Expected GzipCompressorInputStream", cis instanceof GzipCompressorInputStream);
         cis.close();
     }

     /**
      * Known bzip2 signature must still be detected as bzip2 (not deflate).
      */
     @Test
     public void testBzip2SignatureStillDetectedAsBzip2() throws Exception {
         byte[] bzip2Data = new byte[12];
         System.arraycopy(BZIP2_SIGNATURE, 0, bzip2Data, 0, BZIP2_SIGNATURE.length);
         bzip2Data[3] = 'h'; // BZh — standard bzip2 magic
         CompressorStreamFactory factory = new CompressorStreamFactory();
         CompressorInputStream cis = factory.createCompressorInputStream(markedStream(bzip2Data));
         assertTrue("Expected BZip2CompressorInputStream", cis instanceof
BZip2CompressorInputStream);
         cis.close();
     }

     /**
      * Null InputStream must throw IllegalArgumentException.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testNullInputStreamThrows() throws Exception {
         new CompressorStreamFactory().createCompressorInputStream((InputStream) null);
     }

     /**
      * DeflateCompressorInputStream single-byte read correctness.
      */
     @Test
     public void testDeflateCompressorInputStreamByteByByte() throws Exception {
         byte[] original = "byte-by-byte deflate read".getBytes("UTF-8");
         byte[] compressed = rawDeflate(original);
         DeflateCompressorInputStream dcis = new DeflateCompressorInputStream(
                 new ByteArrayInputStream(compressed));
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         int b;
         while ((b = dcis.read()) != -1) {
             baos.write(b);
         }
         dcis.close();
         assertArrayEquals(original, baos.toByteArray());
     }
 }