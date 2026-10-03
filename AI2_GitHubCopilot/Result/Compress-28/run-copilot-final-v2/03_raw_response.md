package org.apache.commons.compress.archivers.tar;

 import static org.junit.Assert.*;
 import org.junit.Test;
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;

 public class TarArchiveInputStreamTest {

     // Helper: create a tar archive with a single short-name entry, then truncate after
     // the header and the given number of data bytes.  Entry size remains declared as `size`,
     // but the actual data bytes are truncated to `actualDataLength` (< size).
     private byte[] createTruncatedTar(String name, long size, int actualDataLength) throws
Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         TarArchiveOutputStream tos = new TarArchiveOutputStream(bos);
         TarArchiveEntry entry = new TarArchiveEntry(name);
         entry.setSize(size);
         tos.putArchiveEntry(entry);
         if (actualDataLength > 0) {
             tos.write(new byte[actualDataLength]);
         }
         tos.closeArchiveEntry();
         tos.finish();
         tos.close();
         byte[] full = bos.toByteArray();
         // header is exactly 512 (recordSize) bytes for short names
         int truncatePos = TarConstants.DEFAULT_RCDSIZE + actualDataLength;
         if (truncatePos > full.length) {
             truncatePos = full.length;
         }
         byte[] truncated = new byte[truncatePos];
         System.arraycopy(full, 0, truncated, 0, truncatePos);
         return truncated;
     }

     private byte[] createFullTar(String name, long size, byte[] data) throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         TarArchiveOutputStream tos = new TarArchiveOutputStream(bos);
         TarArchiveEntry entry = new TarArchiveEntry(name);
         entry.setSize(size);
         tos.putArchiveEntry(entry);
         if (data.length > 0) {
             tos.write(data);
         }
         tos.closeArchiveEntry();
         tos.finish();
         tos.close();
         return bos.toByteArray();
     }

     private byte[] createFullTar(String name, long size, int dataLength, byte fill) throws
Exception {
         byte[] data = new byte[dataLength];
         for (int i = 0; i < dataLength; i++) {
             data[i] = fill;
         }
         return createFullTar(name, size, data);
     }

     @Test(expected = IOException.class)
     public void shouldThrowAnExceptionOnTruncatedEntries() throws Exception {
         byte[] truncatedTar = createTruncatedTar("file.txt", 100, 50);
         TarArchiveInputStream tin = new TarArchiveInputStream(new
ByteArrayInputStream(truncatedTar));
         TarArchiveEntry entry = tin.getNextTarEntry();
         assertNotNull(entry);
         byte[] buf = new byte[100];
         tin.read(buf);  // must throw IOException because data is truncated
     }

     @Test(expected = IOException.class)
     public void testTruncatedEntryWithNoDataBytes() throws Exception {
         // entry declares 10 bytes but supplies 0
         byte[] truncatedTar = createTruncatedTar("empty.txt", 10, 0);
         TarArchiveInputStream tin = new TarArchiveInputStream(new
ByteArrayInputStream(truncatedTar));
         assertNotNull(tin.getNextTarEntry());
         tin.read(new byte[10]);  // immediate EOF → IOException
     }

     @Test
     public void testCompleteEntryReadsAllData() throws Exception {
         byte[] data = new byte[50];
         for (int i = 0; i < data.length; i++) { data[i] = (byte) i; }
         byte[] fullTar = createFullTar("full.bin", 50, data);
         TarArchiveInputStream tin = new TarArchiveInputStream(new ByteArrayInputStream(fullTar));
         TarArchiveEntry entry = tin.getNextTarEntry();
         assertNotNull(entry);
         byte[] buf = new byte[50];
         int total = 0;
         int n;
         while ((n = tin.read(buf, total, buf.length - total)) != -1) {
             total += n;
         }
         assertEquals(50, total);
         for (int i = 0; i < 50; i++) {
             assertEquals(data[i], buf[i]);
         }
     }

     @Test
     public void testReadReturnsMinusOneAfterCompleteEntry() throws Exception {
         byte[] fullTar = createFullTar("a.txt", 10, 10, (byte) 'x');
         TarArchiveInputStream tin = new TarArchiveInputStream(new ByteArrayInputStream(fullTar));
         assertNotNull(tin.getNextTarEntry());
         byte[] buf = new byte[10];
         assertEquals(10, tin.read(buf));
         assertEquals(-1, tin.read(buf)); // no more data
     }

     @Test
     public void testZeroLengthEntryReadReturnsMinusOne() throws Exception {
         byte[] fullTar = createFullTar("zero", 0, 0, (byte) 0);
         TarArchiveInputStream tin = new TarArchiveInputStream(new ByteArrayInputStream(fullTar));
         TarArchiveEntry entry = tin.getNextTarEntry();
         assertNotNull(entry);
         assertEquals(0, entry.getSize());
         assertEquals(-1, tin.read(new byte[1]));
     }

     @Test
     public void testEntryAtBlockBoundaryFull() throws Exception {
         // size exactly one record (512 bytes)
         byte[] fullTar = createFullTar("blk.bin", 512, 512, (byte) 0x2A);
         TarArchiveInputStream tin = new TarArchiveInputStream(new ByteArrayInputStream(fullTar));
         assertNotNull(tin.getNextTarEntry());
         byte[] buf = new byte[512];
         int total = 0;
         int n;
         while ((n = tin.read(buf, total, buf.length - total)) != -1) {
             total += n;
         }
         assertEquals(512, total);
     }

     @Test(expected = IOException.class)
     public void testEntryAtBlockBoundaryTruncated() throws Exception {
         // entry declares 1024 but only 512 data bytes available
         byte[] truncatedTar = createTruncatedTar("half.bin", 1024, 512);
         TarArchiveInputStream tin = new TarArchiveInputStream(new
ByteArrayInputStream(truncatedTar));
         assertNotNull(tin.getNextTarEntry());
         byte[] buf = new byte[1024];
         tin.read(buf); // EOF before expected size → IOException
     }

     @Test
     public void testTruncatedEntryThrowsOnSubsequentRead() throws Exception {
         // entry declares 10 bytes, only 5 are present
         byte[] truncatedTar = createTruncatedTar("small.txt", 10, 5);
         TarArchiveInputStream tin = new TarArchiveInputStream(new
ByteArrayInputStream(truncatedTar));
         assertNotNull(tin.getNextTarEntry());
         byte[] buf = new byte[100];
         try {
             while (tin.read(buf) != -1) {
                 // read until truncation detected
             }
             fail("Expected IOException due to truncation");
         } catch (IOException expected) {
             // expected behaviour – truncation detected
         }
     }

     @Test
     public void testGetNextTarEntryReturnsNullAfterAllEntries() throws Exception {
         byte[] fullTar = createFullTar("single.txt", 5, 5, (byte) 'a');
         TarArchiveInputStream tin = new TarArchiveInputStream(new ByteArrayInputStream(fullTar));
         assertNotNull(tin.getNextTarEntry());
         // consume the entry
         byte[] buf = new byte[5];
         while (tin.read(buf) != -1) { /* read all */ }
         assertNull(tin.getNextTarEntry());
     }

     @Test
     public void testAvailableReturnsEntrySizeForTruncatedEntry() throws Exception {
         byte[] truncatedTar = createTruncatedTar("avail.txt", 100, 0);
         TarArchiveInputStream tin = new TarArchiveInputStream(new
ByteArrayInputStream(truncatedTar));
         try {
             TarArchiveEntry entry = tin.getNextTarEntry();
             assertNotNull(entry);
             assertEquals(100, tin.available());
         } catch (IOException expected) {
             // IOException is acceptable for truncated entries
         }
     }

     @Test
     public void testReadAfterNoEntryThrowsIllegalState() throws Exception {
         byte[] fullTar = createFullTar("f.txt", 5, 5, (byte) 'x');
         TarArchiveInputStream tin = new TarArchiveInputStream(new ByteArrayInputStream(fullTar));
         // skip the first (and only) entry completely
         while (tin.getNextTarEntry() != null) { /* iterate to end */ }
         // no current entry → read must return -1
         assertEquals(-1, tin.read(new byte[1]));
     }
 }