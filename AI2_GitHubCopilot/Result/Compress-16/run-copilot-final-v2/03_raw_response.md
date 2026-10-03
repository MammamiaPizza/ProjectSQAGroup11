package org.apache.commons.compress.archivers;

 import static org.junit.Assert.*;

 import java.io.ByteArrayInputStream;
 import java.io.InputStream;

 import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
 import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
 import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
 import org.junit.Test;

 /**
  * Tests for TAR detection and edge cases in ArchiveStreamFactory,
  * targeting the bug where AIFF/WAV/IFF "FORM" headers and other
  * non-archives with a valid ustar magic but incorrect checksum
  * are wrongly identified as TAR archives.
  */
 public class ArchiveStreamFactoryTest {

     // ---- helpers ----

     private byte[] createUstarTarHeader() {
         byte[] h = new byte[512];
         h[0] = 'a';                       // name
         // mode "000644\0"
         System.arraycopy("000644\000".getBytes(), 0, h, 100, 7);
         // uid / gid "000000\0"
         System.arraycopy("000000\000".getBytes(), 0, h, 108, 7);
         System.arraycopy("000000\000".getBytes(), 0, h, 116, 7);
         // size "00000000000\0"
         System.arraycopy("00000000000\000".getBytes(), 0, h, 124, 12);
         // mtime "00000000000\0"
         System.arraycopy("00000000000\000".getBytes(), 0, h, 136, 12);
         // chksum – fill with spaces temporarily
         for (int i = 148; i < 156; i++) h[i] = ' ';
         // typeflag regular file
         h[156] = '0';
         // magic "ustar\0"
         h[257] = 'u'; h[258] = 's'; h[259] = 't'; h[260] = 'a'; h[261] = 'r'; h[262] = 0;
         // version "00"
         h[263] = '0'; h[264] = '0';
         // compute unsigned checksum
         long sum = 0;
         for (int i = 0; i < 512; i++) sum += (h[i] & 0xFF);
         String chk = String.format("%06o\000 ", sum);
         System.arraycopy(chk.getBytes(), 0, h, 148, 8);
         return h;
     }

     /** Creates a 512-byte block that starts with a "FORM" AIFF header
      *  and contains the string "ustar\0" at the TAR magic offset (257). */
     private byte[] createAiffWithMagic() {
         byte[] b = new byte[512];
         // "FORM"
         b[0] = 'F'; b[1] = 'O'; b[2] = 'R'; b[3] = 'M';
         // chunk size = 504 (0x1F8) big-endian
         b[4] = 0; b[5] = 0; b[6] = 1; b[7] = (byte) 0xF8;
         // form type "AIFF"
         b[8] = 'A'; b[9] = 'I'; b[10] = 'F'; b[11] = 'F';
         // place "ustar\0" at offset 257
         b[257] = 'u'; b[258] = 's'; b[259] = 't'; b[260] = 'a'; b[261] = 'r'; b[262] = 0;
         b[263] = '0'; b[264] = '0'; // version – make matches() return true
         // remainder stays zero (including a wrong checksum)
         return b;
     }

     /** Creates a 512-byte block with ustar magic but everything else zero
      *  (definitely not a valid TAR header). */
     private byte[] createMagicOnlyBlock() {
         byte[] b = new byte[512];
         b[257] = 'u'; b[258] = 's'; b[259] = 't'; b[260] = 'a'; b[261] = 'r'; b[262] = 0;
         b[263] = '0'; b[264] = '0';
         return b;
     }

     // ---- tests ----

     /** Valid ustar TAR must be recognised as TarArchiveInputStream. */
     @Test
     public void testDetectValidUstarTar() throws Exception {
         InputStream in = new ByteArrayInputStream(createUstarTarHeader());
         ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(in);
         assertNotNull(ais);
         assertTrue("expected TarArchiveInputStream", ais instanceof TarArchiveInputStream);
     }

     /** AIFF file starting with "FORM" must NOT be detected as a TAR archive. */
     @Test(expected = ArchiveException.class)
     public void testAiffFilesAreNoTARs() throws Exception {
         InputStream in = new ByteArrayInputStream(createAiffWithMagic());
         new ArchiveStreamFactory().createArchiveInputStream(in);
     }

     /** Another "FORM" block (e.g. IFF) with ustar magic must also be rejected. */
     @Test(expected = ArchiveException.class)
     public void testIffFormHeaderThrows() throws Exception {
         byte[] b = new byte[512];
         b[0] = 'F'; b[1] = 'O'; b[2] = 'R'; b[3] = 'M';
         // chunk size 504
         b[4] = 0; b[5] = 0; b[6] = 1; b[7] = (byte) 0xF8;
         // form type "IFF "
         b[8] = 'I'; b[9] = 'F'; b[10] = 'F'; b[11] = ' ';
         b[257] = 'u'; b[258] = 's'; b[259] = 't'; b[260] = 'a'; b[261] = 'r'; b[262] = 0;
         b[263] = '0'; b[264] = '0';
         InputStream in = new ByteArrayInputStream(b);
         new ArchiveStreamFactory().createArchiveInputStream(in);
     }

     /** A block that contains ustar magic but no valid checksum must not be accepted. */
     @Test(expected = ArchiveException.class)
     public void testMagicWithoutValidChecksumThrows() throws Exception {
         InputStream in = new ByteArrayInputStream(createMagicOnlyBlock());
         new ArchiveStreamFactory().createArchiveInputStream(in);
     }

     /** Stream shorter than 512 bytes cannot contain a valid TAR header. */
     @Test(expected = ArchiveException.class)
     public void testShortStreamThrows() throws Exception {
         byte[] data = new byte[200];
         for (int i = 0; i < data.length; i++) data[i] = (byte) i;
         InputStream in = new ByteArrayInputStream(data);
         new ArchiveStreamFactory().createArchiveInputStream(in);
     }

     /** Zero-length stream cannot be detected as any archive. */
     @Test(expected = ArchiveException.class)
     public void testZeroLengthStreamThrows() throws Exception {
         InputStream in = new ByteArrayInputStream(new byte[0]);
         new ArchiveStreamFactory().createArchiveInputStream(in);
     }

     /** Null InputStream must cause IllegalArgumentException. */
     @Test(expected = IllegalArgumentException.class)
     public void testNullInputStreamThrows() throws Exception {
         new ArchiveStreamFactory().createArchiveInputStream((InputStream) null);
     }

     /** An InputStream that does not support mark is illegal. */
     @Test(expected = IllegalArgumentException.class)
     public void testMarkNotSupportedThrows() throws Exception {
         InputStream in = new InputStream() {
             public int read() { return -1; }
             public boolean markSupported() { return false; }
         };
         new ArchiveStreamFactory().createArchiveInputStream(in);
     }

     /** None of the known signatures must match completely unrelated data. */
     @Test(expected = ArchiveException.class)
     public void testNonMatchingDataThrows() throws Exception {
         byte[] data = new byte[512]; // all zeros
         InputStream in = new ByteArrayInputStream(data);
         new ArchiveStreamFactory().createArchiveInputStream(in);
     }

     /** Valid ZIP signature should still be detected correctly (regression guard). */
     @Test
     public void testDetectZipArchive() throws Exception {
         byte[] data = new byte[32];
         data[0] = 0x50; data[1] = 0x4B; data[2] = 0x03; data[3] = 0x04; // PK\003\004
         InputStream in = new ByteArrayInputStream(data);
         ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(in);
         assertTrue("expected ZipArchiveInputStream", ais instanceof ZipArchiveInputStream);
     }

     /** Valid AR signature should still be detected correctly (regression guard). */
     @Test
     public void testDetectArArchive() throws Exception {
         byte[] data = new byte[32];
         System.arraycopy("!<arch>\n".getBytes(), 0, data, 0, 8);
         InputStream in = new ByteArrayInputStream(data);
         ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(in);
         assertTrue("expected ArArchiveInputStream", ais instanceof ArArchiveInputStream);
     }
 }