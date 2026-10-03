package org.apache.commons.compress.archivers.zip;

 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.util.zip.CRC32;
 import java.util.zip.ZipEntry;
 import java.util.zip.ZipOutputStream;
 import org.junit.Test;
 import static org.junit.Assert.*;

 public class ZipArchiveInputStreamBugTest {

  private static byte[] createNestedZipBytes() throws IOException {
      // Inner zip
      ByteArrayOutputStream innerBos = new ByteArrayOutputStream();
      ZipOutputStream innerZos = new ZipOutputStream(innerBos);
      innerZos.putNextEntry(new ZipEntry("inner.txt"));
      innerZos.write("inner".getBytes("UTF-8"));
      innerZos.closeEntry();
      innerZos.finish();
      byte[] innerBytes = innerBos.toByteArray();

      // Outer zip with inner zip as stored entry
      ByteArrayOutputStream outerBos = new ByteArrayOutputStream();
      ZipOutputStream outerZos = new ZipOutputStream(outerBos);
      outerZos.putNextEntry(new ZipEntry("nested.zip"));
      outerZos.write(innerBytes);
      outerZos.closeEntry();
      outerZos.finish();
      return outerBos.toByteArray();
  }

  /**
   * Covers COMPRESS-367: nested zip inside a zip should return exactly 1 entry.
   */
  @Test
  public void testNestedZipReturnsOneEntry() throws Exception {
      byte[] zipData = createNestedZipBytes();
      ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipData));
      int count = 0;
      while (zis.getNextZipEntry() != null) {
          count++;
      }
      zis.close();
      assertEquals("nested zip must yield exactly 1 entry", 1, count);
  }

  /**
   * Covers testThrowOnInvalidEntry: a stream with a non-zip signature
   * must throw IOException rather than silently returning null.
   */
  @Test(expected = IOException.class)
  public void testThrowOnInvalidEntry() throws IOException {
      // 4 bytes that are not LFH, CFH, or AED signatures
      byte[] data = new byte[] { (byte) 0xAB, (byte) 0xCD, (byte) 0xEF, (byte) 0x12 };
      ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(data));
      try {
          zis.getNextZipEntry();
      } finally {
          zis.close();
      }
  }

  @Test
  public void testEmptyStreamReturnsNull() throws IOException {
      ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(new

byte[0]));
         assertNull(zis.getNextZipEntry());
         zis.close();
     }

  @Test
  public void testDeflatedEntry() throws Exception {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ZipOutputStream zos = new ZipOutputStream(bos);
      zos.setMethod(ZipEntry.DEFLATED);
      zos.putNextEntry(new ZipEntry("deflated.txt"));
      byte[] content = "deflated content".getBytes("UTF-8");
      zos.write(content);
      zos.closeEntry();
      zos.finish();

      ZipArchiveInputStream zis = new ZipArchiveInputStream(new

ByteArrayInputStream(bos.toByteArray()));
         ZipArchiveEntry entry = zis.getNextZipEntry();
         assertNotNull(entry);
         assertEquals("deflated.txt", entry.getName());
         byte[] buf = new byte[1024];
         int len = zis.read(buf);
         assertTrue("should read deflated content", len > 0);
         assertEquals("deflated content", new String(buf, 0, len, "UTF-8"));
         assertNull(zis.getNextZipEntry());
         zis.close();
     }

  @Test
  public void testStoredEntry() throws Exception {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ZipOutputStream zos = new ZipOutputStream(bos);
      zos.setMethod(ZipEntry.STORED);
      byte[] content = "stored content".getBytes("UTF-8");
      ZipEntry entry = new ZipEntry("stored.txt");
      entry.setSize(content.length);
      CRC32 crc = new CRC32();
      crc.update(content);
      entry.setCrc(crc.getValue());
      zos.putNextEntry(entry);
      zos.write(content);
      zos.closeEntry();
      zos.finish();

      ZipArchiveInputStream zis = new ZipArchiveInputStream(new

ByteArrayInputStream(bos.toByteArray()));
         ZipArchiveEntry ze = zis.getNextZipEntry();
         assertNotNull(ze);
         assertEquals("stored.txt", ze.getName());
         byte[] buf = new byte[1024];
         int len = zis.read(buf);
         assertTrue(len > 0);
         assertEquals("stored content", new String(buf, 0, len, "UTF-8"));
         zis.close();
     }

  @Test
  public void testMultipleEntries() throws Exception {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ZipOutputStream zos = new ZipOutputStream(bos);
      zos.putNextEntry(new ZipEntry("first.txt"));
      zos.write("first".getBytes("UTF-8"));
      zos.closeEntry();
      zos.putNextEntry(new ZipEntry("second.txt"));
      zos.write("second".getBytes("UTF-8"));
      zos.closeEntry();
      zos.finish();

      ZipArchiveInputStream zis = new ZipArchiveInputStream(new

ByteArrayInputStream(bos.toByteArray()));
         assertEquals("first.txt", zis.getNextZipEntry().getName());
         assertEquals("second.txt", zis.getNextZipEntry().getName());
         assertNull(zis.getNextZipEntry());
         zis.close();
     }

  /**
   * Verifies closeEntry() drain logic: skipping remaining data of the current
   * entry must correctly position the stream at the next LFH header.
   */
  @Test
  public void testSkipEntryDataAdvancesToNextEntry() throws Exception {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ZipOutputStream zos = new ZipOutputStream(bos);
      zos.putNextEntry(new ZipEntry("skip.txt"));
      zos.write("data-to-skip".getBytes("UTF-8"));
      zos.closeEntry();
      zos.putNextEntry(new ZipEntry("read.txt"));
      zos.write("data-to-read".getBytes("UTF-8"));
      zos.closeEntry();
      zos.finish();

      ZipArchiveInputStream zis = new ZipArchiveInputStream(new

ByteArrayInputStream(bos.toByteArray()));
         // Consume first entry but skip its data by calling getNextZipEntry again
         assertNotNull(zis.getNextZipEntry());
         ZipArchiveEntry second = zis.getNextZipEntry();
         assertEquals("read.txt", second.getName());
         byte[] buf = new byte[1024];
         int len = zis.read(buf);
         assertEquals("data-to-read", new String(buf, 0, len, "UTF-8"));
         zis.close();
     }

  @Test
  public void testClosedStreamReturnsNull() throws IOException {
      ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(new

byte[0]));
         zis.close();
         assertNull(zis.getNextZipEntry());
     }

  @Test
  public void testCanReadEntryDataStored() throws Exception {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ZipOutputStream zos = new ZipOutputStream(bos);
      zos.setMethod(ZipEntry.STORED);
      byte[] content = "test".getBytes("UTF-8");
      ZipEntry entry = new ZipEntry("test.txt");
      entry.setSize(content.length);
      CRC32 crc = new CRC32();
      crc.update(content);
      entry.setCrc(crc.getValue());
      zos.putNextEntry(entry);
      zos.write(content);
      zos.closeEntry();
      zos.finish();

      ZipArchiveInputStream zis = new ZipArchiveInputStream(new

ByteArrayInputStream(bos.toByteArray()));
         ZipArchiveEntry ze = zis.getNextZipEntry();
         assertTrue(zis.canReadEntryData(ze));
         byte[] buf = new byte[1024];
         int len = zis.read(buf);
         assertEquals(content.length, len);
         assertArrayEquals(content, java.util.Arrays.copyOf(buf, len));
         zis.close();
     }

  /**
   * Corrupt entry with a valid LFH signature but truncated compressed data
   * must trigger an IOException when the stream ends prematurely.
   */
  @Test(expected = IOException.class)
  public void testTruncatedDeflatedEntryThrowsIOException() throws IOException {
      // Minimal LFH: STORED method, comp-size=100, but stream ends right after header
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      bos.write(new byte[] { 0x50, 0x4b, 0x03, 0x04 }); // LFH sig
      bos.write(new byte[] { 0x14, 0x00 }); // version 2.0
      bos.write(new byte[] { 0x00, 0x00 }); // flags
      bos.write(new byte[] { 0x00, 0x00 }); // method = STORED
      bos.write(new byte[] { 0x00, 0x00, 0x00, 0x00 }); // time + date
      bos.write(new byte[] { 0x00, 0x00, 0x00, 0x00 }); // CRC
      bos.write(new byte[] { 0x64, 0x00, 0x00, 0x00 }); // comp-size = 100
      bos.write(new byte[] { 0x64, 0x00, 0x00, 0x00 }); // uncomp-size = 100
      bos.write(new byte[] { 0x00, 0x00 }); // filename-len = 0
      bos.write(new byte[] { 0x00, 0x00 }); // extra-len = 0
      // No entry data follows!

      ZipArchiveInputStream zis = new ZipArchiveInputStream(new

ByteArrayInputStream(bos.toByteArray()));
         try {
             zis.getNextZipEntry();
             byte[] buf = new byte[200];
             zis.read(buf); // must throw: stream ends before 100 bytes
         } finally {
             zis.close();
         }
     }
 }