import static org.junit.Assert.*;

 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.util.Date;
 import java.util.zip.CRC32;
 import java.util.zip.ZipEntry;
 import java.util.zip.ZipOutputStream;

 import org.apache.commons.compress.archivers.ArchiveEntry;
 import org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException;
 import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
 import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
 import org.junit.Test;

 /**
  * Tests for ZipArchiveInputStream focusing on canReadEntryData when
  * uncompressed size is unknown (COMPRESS-436).
  */
 public class ZipArchiveInputStreamTest {

     // --- helper ----------------------------------------------------------------

     private byte[] createZipWithEntry(ZipEntry entry, byte[] data) throws IOException {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         ZipOutputStream zos = new ZipOutputStream(bos);
         zos.putNextEntry(entry);
         if (data != null) {
             zos.write(data);
         }
         zos.closeEntry();
         zos.close();
         return bos.toByteArray();
     }

     private ZipEntry createStoredEntry(String name, byte[] data) {
         ZipEntry entry = new ZipEntry(name);
         entry.setMethod(ZipEntry.STORED);
         CRC32 crc = new CRC32();
         crc.update(data);
         entry.setSize(data.length);
         entry.setCompressedSize(data.length);
         entry.setCrc(crc.getValue());
         return entry;
     }

     // --- canReadEntryData ------------------------------------------------------

     @Test
     public void testCanReadEntryData_DEFLATED_UnknownSize_ReturnsFalse() throws IOException {
         // Entry with data descriptor (GP bit 3), DEFLATED method, size = 0
         ZipEntry ze = new ZipEntry("unknown.bin");
         ze.setMethod(ZipEntry.DEFLATED);
         byte[] data = new byte[10];
         for (int i = 0; i < data.length; i++) data[i] = (byte) i;
         byte[] zipBytes = createZipWithEntry(ze, data);

         ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipBytes));
         ArchiveEntry entry = zis.getNextEntry();
         assertNotNull(entry);
         assertFalse(zis.canReadEntryData(entry));
         zis.close();
     }

     @Test
     public void testCanReadEntryData_DEFLATED_KnownSize_ReturnsTrue() throws IOException {
         ZipEntry ze = new ZipEntry("known.bin");
         ze.setMethod(ZipEntry.DEFLATED);
         byte[] data = new byte[100];
         for (int i = 0; i < data.length; i++) data[i] = (byte) i;
         // Set known size (required for DEFLATED entries to produce known size)
         ze.setSize(data.length);
         byte[] zipBytes = createZipWithEntry(ze, data);

         ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipBytes));
         ArchiveEntry entry = zis.getNextEntry();
         assertNotNull(entry);
         assertTrue(zis.canReadEntryData(entry));
         zis.close();
     }

     @Test
     public void testCanReadEntryData_STORED_KnownSize_ReturnsTrue() throws IOException {
         byte[] data = "Hello, World!".getBytes("UTF-8");
         ZipEntry ze = createStoredEntry("stored.txt", data);
         byte[] zipBytes = createZipWithEntry(ze, data);

         ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipBytes));
         ArchiveEntry entry = zis.getNextEntry();
         assertNotNull(entry);
         assertTrue(zis.canReadEntryData(entry));
         zis.close();
     }

     @Test
     public void testCanReadEntryData_NonZipArchiveEntry_ReturnsFalse() throws IOException {
         // any ArchiveEntry that is not ZipArchiveEntry must return false
         ArchiveEntry unknown = new ArchiveEntry() {
             @Override public String getName() { return "x"; }
             @Override public long getSize() { return 0; }
             @Override public boolean isDirectory() { return false; }
             @Override public Date getLastModifiedDate() { return null; }
         };
         try (ZipArchiveInputStream zis = new ZipArchiveInputStream(
                 new ByteArrayInputStream(new byte[0]))) {
             assertFalse(zis.canReadEntryData(unknown));
         }
     }

     // --- reading behaviour -----------------------------------------------------

     @Test
     public void testRead_ReadableEntry_ReadsCorrectData() throws IOException {
         byte[] original = new byte[256];
         for (int i = 0; i < original.length; i++) original[i] = (byte) (i & 0xFF);

         ZipEntry ze = new ZipEntry("readable.bin");
         ze.setMethod(ZipEntry.DEFLATED);
         ze.setSize(original.length);
         byte[] zipBytes = createZipWithEntry(ze, original);

         ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipBytes));
         ArchiveEntry entry = zis.getNextEntry();
         assertNotNull(entry);
         assertTrue(zis.canReadEntryData(entry));

         byte[] buffer = new byte[512];
         int read = zis.read(buffer);
         assertTrue(read > 0);
         // verify first bytes match
         for (int i = 0; i < read && i < original.length; i++) {
             assertEquals("Mismatch at byte " + i, original[i], buffer[i]);
         }
         int total = read;
         while (total < original.length) {
             read = zis.read(buffer);
             if (read < 0) break;
             total += read;
         }
         assertEquals(original.length, total);
         zis.close();
     }

     @Test(expected = UnsupportedZipFeatureException.class)
     public void testRead_UnreadableEntry_ThrowsUnsupportedZipFeatureException() throws IOException
{
         // entry with data descriptor -> canReadEntryData false -> read must throw
         ZipEntry ze = new ZipEntry("unreadable.bin");
         ze.setMethod(ZipEntry.DEFLATED);
         byte[] data = new byte[50];
         for (int i = 0; i < data.length; i++) data[i] = (byte) i;
         byte[] zipBytes = createZipWithEntry(ze, data);

         ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipBytes));
         ArchiveEntry entry = zis.getNextEntry();
         assertNotNull(entry);
         assertFalse(zis.canReadEntryData(entry));
         // reading must throw
         zis.read(new byte[32]);
     }

     @Test
     public void testGetNextEntry_DataDescriptorEntry_SetsCorrectState() throws IOException {
         ZipEntry ze = new ZipEntry("ddesc.bin");
         ze.setMethod(ZipEntry.DEFLATED);
         byte[] data = new byte[10];
         byte[] zipBytes = createZipWithEntry(ze, data);

         ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipBytes));
         ZipArchiveEntry entry = zis.getNextZipEntry();
         assertNotNull(entry);
         assertTrue("Entry should have data descriptor flag",
                 entry.getGeneralPurposeBit().usesDataDescriptor());
         // size from local header should be 0 (unknown)
         assertEquals(0, entry.getSize());
         // canRead must be false
         assertFalse(zis.canReadEntryData(entry));
         zis.close();
     }

     @Test
     public void testClose_ClosesUnderlyingStream() throws IOException {
         ByteArrayInputStream bin = new ByteArrayInputStream(new byte[0]);
         ZipArchiveInputStream zis = new ZipArchiveInputStream(bin);
         zis.close();
         assertTrue(true); // no exception
     }

     @Test
     public void testSkip_ReadableEntry_SkipsData() throws IOException {
         byte[] data = new byte[300];
         for (int i = 0; i < data.length; i++) data[i] = (byte) i;
         ZipEntry ze = new ZipEntry("skip.bin");
         ze.setMethod(ZipEntry.DEFLATED);
         ze.setSize(data.length);
         byte[] zipBytes = createZipWithEntry(ze, data);

         ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipBytes));
         ArchiveEntry entry = zis.getNextEntry();
         assertNotNull(entry);
         assertTrue(zis.canReadEntryData(entry));

         long skipped = zis.skip(100);
         assertTrue(skipped > 0);
         byte[] buf = new byte[200];
         int read = zis.read(buf);
         assertTrue(read > 0);
         zis.close();
     }
 }
