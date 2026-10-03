package org.apache.commons.compress.archivers.zip;

 import static org.junit.Assert.*;

 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.util.Arrays;
 import java.util.zip.CRC32;
 import java.util.zip.ZipEntry;

 import org.junit.Test;

 public class ZipArchiveInputStreamTest {

     /**
      * Create a ZIP with a single stored entry in memory.
      */
     private byte[] createZipWithStoredEntry(String name, byte[] data) throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos);
         ZipArchiveEntry entry = new ZipArchiveEntry(name);
         entry.setMethod(ZipEntry.STORED);
         entry.setSize(data.length);
         entry.setCompressedSize(data.length);
         CRC32 crc = new CRC32();
         crc.update(data);
         entry.setCrc(crc.getValue());
         zos.putArchiveEntry(entry);
         zos.write(data);
         zos.closeArchiveEntry();
         zos.finish();
         zos.close();
         return baos.toByteArray();
     }

     /**
      * Create a ZIP with a single deflated entry in memory.
      */
     private byte[] createZipWithDeflatedEntry(String name, byte[] data) throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos);
         ZipArchiveEntry entry = new ZipArchiveEntry(name);
         entry.setMethod(ZipEntry.DEFLATED);
         zos.putArchiveEntry(entry);
         zos.write(data);
         zos.closeArchiveEntry();
         zos.finish();
         zos.close();
         return baos.toByteArray();
     }

     /**
      * Create a ZIP with multiple entries of given names, data, and methods.
      */
     private byte[] createZipWithMultipleEntries(String[] names, byte[][] data, int[] methods)
throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos);
         for (int i = 0; i < names.length; i++) {
             ZipArchiveEntry entry = new ZipArchiveEntry(names[i]);
             entry.setMethod(methods[i]);
             if (methods[i] == ZipEntry.STORED) {
                 entry.setSize(data[i].length);
                 entry.setCompressedSize(data[i].length);
                 CRC32 crc = new CRC32();
                 crc.update(data[i]);
                 entry.setCrc(crc.getValue());
             }
             zos.putArchiveEntry(entry);
             zos.write(data[i]);
             zos.closeArchiveEntry();
         }
         zos.finish();
         zos.close();
         return baos.toByteArray();
     }

     /**
      * Fully read all bytes from the current entry in the input stream.
      */
     private byte[] readEntryData(ZipArchiveInputStream zis, int expectedSize) throws IOException {
         byte[] buffer = new byte[expectedSize];
         int totalRead = 0;
         while (totalRead < expectedSize) {
             int bytesRead = zis.read(buffer, totalRead, expectedSize - totalRead);
             if (bytesRead < 0) {
                 break;
             }
             totalRead += bytesRead;
         }
         if (totalRead < expectedSize) {
             return Arrays.copyOf(buffer, totalRead);
         }
         return buffer;
     }

     // ====================== Tests ======================

     /**
      * COMPRESS-264: Reading the first stored entry must return the original bytes.
      * The bug caused the first byte read to be 0 instead of the expected value.
      */
     @Test
     public void testReadingOfFirstStoredEntry() throws Exception {
         byte[] original = new byte[100];
         for (int i = 0; i < 100; i++) {
             original[i] = (byte) (i + 1);
         }
         byte[] zipData = createZipWithStoredEntry("entry.bin", original);

         ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipData));
         ZipArchiveEntry entry = zis.getNextZipEntry();
         assertNotNull("Entry must exist", entry);
         assertEquals("Stored method expected", ZipEntry.STORED, entry.getMethod());
         assertEquals("Correct entry size", 100, entry.getSize());

         byte[] result = readEntryData(zis, 100);
         assertEquals("Read all 100 bytes", 100, result.length);
         assertEquals("First byte must be 1", (byte) 1, result[0]);
         assertArrayEquals("All bytes must match original", original, result);

         assertNull("No more entries after first", zis.getNextZipEntry());
         zis.close();
     }

     /**
      * Stored entry with a single byte - boundary case.
      */
     @Test
     public void testReadSingleByteStoredEntry() throws Exception {
         byte[] original = new byte[] { (byte) 0xAB };
         byte[] zipData = createZipWithStoredEntry("one.bin", original);

         ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipData));
         ZipArchiveEntry entry = zis.getNextZipEntry();
         assertNotNull(entry);
         assertEquals(ZipEntry.STORED, entry.getMethod());
         assertEquals(1, entry.getSize());

         int b = zis.read();
         assertNotEquals("Should read a byte", -1, b);
         assertEquals("Byte must match", original[0] & 0xFF, b);
         assertEquals("Next read must return -1", -1, zis.read());

         zis.close();
     }

     /**
      * Stored entry larger than the internal buffer (5000 bytes).
      */
     @Test
     public void testReadLargeStoredEntry() throws Exception {
         int size = 5000;
         byte[] original = new byte[size];
         for (int i = 0; i < size; i++) {
             original[i] = (byte) (i % 256);
         }
         byte[] zipData = createZipWithStoredEntry("large.bin", original);

         ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipData));
         ZipArchiveEntry entry = zis.getNextZipEntry();
         assertNotNull(entry);
         assertEquals(ZipEntry.STORED, entry.getMethod());
         assertEquals(size, entry.getSize());

         byte[] result = readEntryData(zis, size);
         assertEquals(size, result.length);
         assertArrayEquals("All bytes must match", original, result);

         zis.close();
     }

     /**
      * Stored entry exactly at the internal BUFFER_SIZE boundary.
      */
     @Test
     public void testReadStoredEntryExactlyBufferSize() throws Exception {
         int size = ZipArchiveOutputStream.BUFFER_SIZE;
         byte[] original = new byte[size];
         for (int i = 0; i < size; i++) {
             original[i] = (byte) (i % 256);
         }
         byte[] zipData = createZipWithStoredEntry("bufsize.bin", original);

         ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipData));
         ZipArchiveEntry entry = zis.getNextZipEntry();
         assertNotNull(entry);
         assertEquals(ZipEntry.STORED, entry.getMethod());

         byte[] result = readEntryData(zis, size);
         assertEquals(size, result.length);
         assertArrayEquals(original, result);

         zis.close();
     }

     /**
      * A stored entry following a deflated entry - tests transition between methods.
      */
     @Test
     public void testReadStoredAfterDeflated() throws Exception {
         byte[] deflatedData = new byte[200];
         for (int i = 0; i < 200; i++) {
             deflatedData[i] = (byte) (i + 50);
         }
         byte[] storedData = new byte[100];
         for (int i = 0; i < 100; i++) {
             storedData[i] = (byte) (200 - i);
         }

         byte[] zipData = createZipWithMultipleEntries(
             new String[] { "deflated.bin", "stored.bin" },
             new byte[][] { deflatedData, storedData },
             new int[] { ZipEntry.DEFLATED, ZipEntry.STORED }
         );

         ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipData));

         ZipArchiveEntry e1 = zis.getNextZipEntry();
         assertNotNull(e1);
         assertEquals(ZipEntry.DEFLATED, e1.getMethod());
         byte[] r1 = readEntryData(zis, 200);
         assertEquals(200, r1.length);
         assertArrayEquals("Deflated entry data must match", deflatedData, r1);

         ZipArchiveEntry e2 = zis.getNextZipEntry();
         assertNotNull(e2);
         assertEquals(ZipEntry.STORED, e2.getMethod());
         byte[] r2 = readEntryData(zis, 100);
         assertEquals(100, r2.length);
         assertArrayEquals("Stored entry data must match", storedData, r2);

         assertNull(zis.getNextZipEntry());
         zis.close();
     }

     /**
      * Zero-length stored entry should return -1 on first read.
      */
     @Test
     public void testReadZeroLengthStoredEntry() throws Exception {
         byte[] zipData = createZipWithStoredEntry("empty.bin", new byte[0]);

         ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipData));
         ZipArchiveEntry entry = zis.getNextZipEntry();
         assertNotNull(entry);
         assertEquals(ZipEntry.STORED, entry.getMethod());
         assertEquals(0, entry.getSize());

         assertEquals("Read on empty entry must return -1", -1, zis.read(new byte[10]));

         zis.close();
     }

     /**
      * Multiple stored entries read sequentially.
      */
     @Test
     public void testReadMultipleStoredEntries() throws Exception {
         byte[] data1 = new byte[] { 1, 2, 3, 4, 5 };
         byte[] data2 = new byte[] { 10, 20, 30 };
         byte[] data3 = new byte[50];
         for (int i = 0; i < 50; i++) {
             data3[i] = (byte) i;
         }

         byte[] zipData = createZipWithMultipleEntries(
             new String[] { "a.bin", "b.bin", "c.bin" },
             new byte[][] { data1, data2, data3 },
             new int[] { ZipEntry.STORED, ZipEntry.STORED, ZipEntry.STORED }
         );

         ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipData));

         ZipArchiveEntry e1 = zis.getNextZipEntry();
         assertNotNull(e1);
         assertEquals("a.bin", e1.getName());
         byte[] r1 = readEntryData(zis, 5);
         assertArrayEquals("First entry data must match", data1, r1);

         ZipArchiveEntry e2 = zis.getNextZipEntry();
         assertNotNull(e2);
         assertEquals("b.bin", e2.getName());
         byte[] r2 = readEntryData(zis, 3);
         assertArrayEquals("Second entry data must match", data2, r2);

         ZipArchiveEntry e3 = zis.getNextZipEntry();
         assertNotNull(e3);
         assertEquals("c.bin", e3.getName());
         byte[] r3 = readEntryData(zis, 50);
         assertArrayEquals("Third entry data must match", data3, r3);

         assertNull(zis.getNextZipEntry());
         zis.close();
     }

     /**
      * Stored entry read via the 4-arg constructor with data-descriptor-flag enabled.
      */
     @Test
     public void testReadStoredEntryAllowDataDescriptor() throws Exception {
         byte[] original = new byte[75];
         for (int i = 0; i < 75; i++) {
             original[i] = (byte) (i * 3 + 7);
         }
         byte[] zipData = createZipWithStoredEntry("dd.bin", original);

         ZipArchiveInputStream zis = new ZipArchiveInputStream(
             new ByteArrayInputStream(zipData), "UTF-8", true, true);

         ZipArchiveEntry entry = zis.getNextZipEntry();
         assertNotNull(entry);
         assertEquals(ZipEntry.STORED, entry.getMethod());
         assertEquals(75, entry.getSize());

         byte[] result = readEntryData(zis, 75);
         assertEquals(75, result.length);
         assertArrayEquals("Data must match even with data-descriptor flag", original, result);

         zis.close();
     }

     /**
      * Reading after the stream is closed must throw IOException.
      */
     @Test(expected = IOException.class)
     public void testReadAfterClose() throws Exception {
         byte[] original = new byte[10];
         byte[] zipData = createZipWithStoredEntry("test.bin", original);

         ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipData));
         zis.getNextZipEntry();
         zis.close();
         zis.read();
     }

     /**
      * getNextZipEntry must return null after the stream is closed.
      */
     @Test
     public void testGetNextEntryAfterClose() throws Exception {
         byte[] original = new byte[10];
         byte[] zipData = createZipWithStoredEntry("test.bin", original);

         ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipData));
         assertNotNull("First entry must exist", zis.getNextZipEntry());
         zis.close();
         assertNull("getNextZipEntry must return null after close", zis.getNextZipEntry());
     }

     /**
      * Reading beyond the entry boundary returns -1,
      * then the next entry can be obtained correctly.
      */
     @Test
     public void testReadBeyondEntryAndTransition() throws Exception {
         byte[] data1 = new byte[20];
         byte[] data2 = new byte[30];
         for (int i = 0; i < 20; i++) {
             data1[i] = (byte) (i + 10);
         }
         for (int i = 0; i < 30; i++) {
             data2[i] = (byte) (i + 50);
         }

         byte[] zipData = createZipWithMultipleEntries(
             new String[] { "first.bin", "second.bin" },
             new byte[][] { data1, data2 },
             new int[] { ZipEntry.STORED, ZipEntry.STORED }
         );

         ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipData));

         ZipArchiveEntry e1 = zis.getNextZipEntry();
         assertNotNull(e1);
         byte[] buf = new byte[20];
         assertEquals("Read exactly entry size", 20, zis.read(buf));
         assertArrayEquals("First entry data must match", data1, buf);
         assertEquals("Read beyond entry must return -1", -1, zis.read());

         ZipArchiveEntry e2 = zis.getNextZipEntry();
         assertNotNull(e2);
         byte[] buf2 = new byte[30];
         assertEquals("Read second entry", 30, zis.read(buf2));
         assertArrayEquals("Second entry data must match", data2, buf2);

         zis.close();
     }
 }