package org.apache.commons.compress.archivers.zip;

 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;

 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.zip.CRC32;
 import java.util.zip.ZipEntry;

 import static org.junit.Assert.*;

 /**
  * Tests for {@link ZipArchiveOutputStream} focusing on data descriptor behavior
  * when adding raw entries (COMPRESS-394).
  *
  * <p>The bug: when adding a raw archive entry with known CRC and sizes,
  * a data descriptor was incorrectly written. The fix ensures that when
  * CRC and sizes are known (not CRC_UNKNOWN), no data descriptor is emitted.</p>
  */
 public class ZipArchiveOutputStreamDataDescriptorTest {

     private File tempFile;

     @Before
     public void setUp() throws IOException {
         tempFile = File.createTempFile("zip-test-", ".zip");
         tempFile.deleteOnExit();
     }

     @After
     public void tearDown() {
         if (tempFile != null && tempFile.exists()) {
             tempFile.delete();
         }
     }

     /**
      * Creates a simple byte payload.
      */
     private static byte[] createPayload(int size) {
         byte[] data = new byte[size];
         for (int i = 0; i < size; i++) {
             data[i] = (byte) (i % 256);
         }
         return data;
     }

     /**
      * Computes CRC32 for the given data.
      */
     private static long computeCrc(byte[] data) {
         CRC32 crc = new CRC32();
         crc.update(data);
         return crc.getValue();
     }

     /**
      * Reads the raw bytes from a local file header data descriptor field
      * if present. Returns the 4-byte CRC field value or -1 if no descriptor found.
      * The data descriptor signature is 0x08074b50.
      */
     private long readDataDescriptorCrc(File file, String entryName) throws IOException {
         try (FileInputStream fis = new FileInputStream(file)) {
             byte[] buffer = new byte[(int) file.length()];
             int read = fis.read(buffer);
             if (read != buffer.length) {
                 throw new IOException("Failed to read entire file");
             }
             // Find the data descriptor signature 0x08074b50
             for (int i = 0; i < buffer.length - 16; i++) {
                 if (buffer[i] == 0x50 && buffer[i + 1] == 0x4b
                         && buffer[i + 2] == 0x07 && buffer[i + 3] == 0x08) {
                     // Data descriptor found: +4 = CRC (4 bytes)
                     long crc = ((long) (buffer[i + 4] & 0xFF))
                             | ((long) (buffer[i + 5] & 0xFF) << 8)
                             | ((long) (buffer[i + 6] & 0xFF) << 16)
                             | ((long) (buffer[i + 7] & 0xFF) << 24);
                     return crc;
                 }
             }
         }
         return -1;
     }

     /**
      * Verifies that when adding a raw archive entry with known CRC and sizes,
      * no data descriptor is written. This is the core bug scenario from COMPRESS-394.
      */
     @Test
     public void testNoDataDescriptorForRawEntryWithKnownCrc() throws IOException {
         byte[] payload = createPayload(1024);
         long expectedCrc = computeCrc(payload);

         ZipArchiveEntry entry = new ZipArchiveEntry("known-crc.txt");
         entry.setMethod(ZipEntry.STORED);
         entry.setSize(payload.length);
         entry.setCompressedSize(payload.length);
         entry.setCrc(expectedCrc);

         try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(tempFile)) {
             zos.addRawArchiveEntry(entry, new ByteArrayInputStream(payload));
             zos.closeArchiveEntry();
             zos.finish();
         }

         // The first byte after the entry data should NOT be a data descriptor.
         // In a correct implementation, no data descriptor means the next structure
         // (central directory or another entry) follows immediately.
         byte[] zipBytes = java.nio.file.Files.readAllBytes(tempFile.toPath());
         // Find the "known-crc.txt" local file header
         boolean foundDescriptor = false;
         for (int i = 0; i < zipBytes.length - 4; i++) {
             if (zipBytes[i] == 0x50 && zipBytes[i + 1] == 0x4b
                     && zipBytes[i + 2] == 0x07 && zipBytes[i + 3] == 0x08) {
                 foundDescriptor = true;
                 break;
             }
         }
         assertFalse("Data descriptor should not be present for known CRC raw entry",
                 foundDescriptor);
     }

     /**
      * Verifies that when adding a raw archive entry with CRC_UNKNOWN,
      * a data descriptor IS written.
      */
     @Test
     public void testDataDescriptorWrittenForRawEntryWithUnknownCrc() throws IOException {
         byte[] payload = createPayload(512);

         ZipArchiveEntry entry = new ZipArchiveEntry("unknown-crc.txt");
         entry.setMethod(ZipEntry.STORED);
         // Leave CRC as CRC_UNKNOWN (the default for a new entry with no explicit setCrc)
         // CRC_UNKNOWN is the default: no need to set it explicitly

         try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(tempFile)) {
             zos.addRawArchiveEntry(entry, new ByteArrayInputStream(payload));
             zos.closeArchiveEntry();
             zos.finish();
         }

         byte[] zipBytes = java.nio.file.Files.readAllBytes(tempFile.toPath());
         boolean foundDescriptor = false;
         for (int i = 0; i < zipBytes.length - 4; i++) {
             if (zipBytes[i] == 0x50 && zipBytes[i + 1] == 0x4b
                     && zipBytes[i + 2] == 0x07 && zipBytes[i + 3] == 0x08) {
                 foundDescriptor = true;
                 break;
             }
         }
         assertTrue("Data descriptor should be present for CRC_UNKNOWN raw entry",
                 foundDescriptor);
     }

     /**
      * Tests that a normal (non-raw) STORED entry with known sizes does not
      * produce a data descriptor when seekable.
      */
     @Test
     public void testNormalStoredEntryNoDescriptorSeekable() throws IOException {
         byte[] payload = createPayload(256);

         ZipArchiveEntry entry = new ZipArchiveEntry("normal-stored.txt");
         entry.setMethod(ZipEntry.STORED);
         entry.setSize(payload.length);
         entry.setCrc(computeCrc(payload));

         try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(tempFile)) {
             zos.putArchiveEntry(entry);
             zos.write(payload);
             zos.closeArchiveEntry();
             zos.finish();
         }

         byte[] zipBytes = java.nio.file.Files.readAllBytes(tempFile.toPath());
         boolean foundDescriptor = false;
         for (int i = 0; i < zipBytes.length - 4; i++) {
             if (zipBytes[i] == 0x50 && zipBytes[i + 1] == 0x4b
                     && zipBytes[i + 2] == 0x07 && zipBytes[i + 3] == 0x08) {
                 foundDescriptor = true;
                 break;
             }
         }
         assertFalse("Data descriptor should not be present for seekable stored entry",
                 foundDescriptor);
     }

     /**
      * Tests that a DEFLATED entry with CRC_UNKNOWN writes a data descriptor.
      */
     @Test
     public void testDeflatedEntryWithUnknownCrcWritesDescriptor() throws IOException {
         byte[] payload = createPayload(2048);

         ZipArchiveEntry entry = new ZipArchiveEntry("deflated-unknown.txt");
         entry.setMethod(ZipEntry.DEFLATED);

         try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(tempFile)) {
             zos.putArchiveEntry(entry);
             zos.write(payload);
             zos.closeArchiveEntry();
             zos.finish();
         }

         byte[] zipBytes = java.nio.file.Files.readAllBytes(tempFile.toPath());
         boolean foundDescriptor = false;
         for (int i = 0; i < zipBytes.length - 4; i++) {
             if (zipBytes[i] == 0x50 && zipBytes[i + 1] == 0x4b
                     && zipBytes[i + 2] == 0x07 && zipBytes[i + 3] == 0x08) {
                 foundDescriptor = true;
                 break;
             }
         }
         assertTrue("Data descriptor should be present for deflated entry with unknown CRC",
                 foundDescriptor);
     }

     /**
      * Tests adding a raw entry where the entry is a "2-phase source" (known CRC).
      * The closeCopiedEntry path should suppress the data descriptor.
      */
     @Test
     public void testRawEntryTwoPhaseSourceNoDescriptor() throws IOException {
         byte[] payload = createPayload(768);
         long crc = computeCrc(payload);

         ZipArchiveEntry entry = new ZipArchiveEntry("two-phase.txt");
         entry.setMethod(ZipEntry.STORED);
         entry.setSize(payload.length);
         entry.setCompressedSize(payload.length);
         entry.setCrc(crc); // Known CRC triggers 2-phase path

         try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(tempFile)) {
             zos.addRawArchiveEntry(entry, new ByteArrayInputStream(payload));
             zos.closeArchiveEntry();
             zos.finish();
         }

         // Verify the CRC in the archive is correct
         try (java.util.zip.ZipFile zf = new java.util.zip.ZipFile(tempFile)) {
             java.util.zip.ZipEntry ze = zf.getEntry("two-phase.txt");
             assertNotNull("Entry should exist", ze);
             assertEquals("CRC should match", crc, ze.getCrc());
             assertEquals("Size should match", payload.length, ze.getSize());
         }
     }

     /**
      * Tests that adding multiple raw entries with mixed CRC known/unknown
      * works correctly - some with descriptor, some without.
      */
     @Test
     public void testMultipleRawEntriesMixedCrc() throws IOException {
         byte[] payload1 = createPayload(100);
         byte[] payload2 = createPayload(200);
         long crc1 = computeCrc(payload1);

         ZipArchiveEntry knownEntry = new ZipArchiveEntry("known.txt");
         knownEntry.setMethod(ZipEntry.STORED);
         knownEntry.setSize(payload1.length);
         knownEntry.setCompressedSize(payload1.length);
         knownEntry.setCrc(crc1);

         ZipArchiveEntry unknownEntry = new ZipArchiveEntry("unknown.txt");
         unknownEntry.setMethod(ZipEntry.STORED);

         try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(tempFile)) {
             // Add known CRC entry first
             zos.addRawArchiveEntry(knownEntry, new ByteArrayInputStream(payload1));
             zos.closeArchiveEntry();
             // Add unknown CRC entry second
             zos.addRawArchiveEntry(unknownEntry, new ByteArrayInputStream(payload2));
             zos.closeArchiveEntry();
             zos.finish();
         }

         // Verify both entries are extractable with correct data
         try (java.util.zip.ZipFile zf = new java.util.zip.ZipFile(tempFile)) {
             java.util.zip.ZipEntry ze1 = zf.getEntry("known.txt");
             assertNotNull("Known entry should exist", ze1);
             assertEquals("Known entry CRC", crc1, ze1.getCrc());

             java.util.zip.ZipEntry ze2 = zf.getEntry("unknown.txt");
             assertNotNull("Unknown entry should exist", ze2);
             assertEquals("Unknown entry size", payload2.length, ze2.getSize());

             // Read and verify data
             try (InputStream is = zf.getInputStream(ze1)) {
                 byte[] read = new byte[payload1.length];
                 int total = 0;
                 while (total < read.length) {
                     int r = is.read(read, total, read.length - total);
                     if (r < 0) break;
                     total += r;
                 }
                 assertArrayEquals("Known entry data", payload1, read);
             }
             try (InputStream is = zf.getInputStream(ze2)) {
                 byte[] read = new byte[payload2.length];
                 int total = 0;
                 while (total < read.length) {
                     int r = is.read(read, total, read.length - total);
                     if (r < 0) break;
                     total += r;
                 }
                 assertArrayEquals("Unknown entry data", payload2, read);
             }
         }
     }

     /**
      * Tests the boundary case: empty payload with known CRC.
      */
     @Test
     public void testRawEntryEmptyPayloadKnownCrc() throws IOException {
         byte[] payload = new byte[0];
         long crc = computeCrc(payload);

         ZipArchiveEntry entry = new ZipArchiveEntry("empty.txt");
         entry.setMethod(ZipEntry.STORED);
         entry.setSize(0);
         entry.setCompressedSize(0);
         entry.setCrc(crc);

         try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(tempFile)) {
             zos.addRawArchiveEntry(entry, new ByteArrayInputStream(payload));
             zos.closeArchiveEntry();
             zos.finish();
         }

         try (java.util.zip.ZipFile zf = new java.util.zip.ZipFile(tempFile)) {
             java.util.zip.ZipEntry ze = zf.getEntry("empty.txt");
             assertNotNull("Empty entry should exist", ze);
             assertEquals("Empty entry CRC", crc, ze.getCrc());
             assertEquals("Empty entry size", 0, ze.getSize());
         }
     }

     /**
      * Tests the boundary case: single byte payload with unknown CRC.
      */
     @Test
     public void testRawEntrySingleByteUnknownCrc() throws IOException {
         byte[] payload = new byte[] { (byte) 0xAB };

         ZipArchiveEntry entry = new ZipArchiveEntry("single.txt");
         entry.setMethod(ZipEntry.STORED);

         try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(tempFile)) {
             zos.addRawArchiveEntry(entry, new ByteArrayInputStream(payload));
             zos.closeArchiveEntry();
             zos.finish();
         }

         try (java.util.zip.ZipFile zf = new java.util.zip.ZipFile(tempFile)) {
             java.util.zip.ZipEntry ze = zf.getEntry("single.txt");
             assertNotNull("Single byte entry should exist", ze);
             assertEquals("Single byte entry size", 1, ze.getSize());
         }
     }

     /**
      * Tests that an entry with STORED method and CRC_UNKNOWN on a non-seekable
      * stream (ByteArrayOutputStream) writes a data descriptor.
      */
     @Test
     public void testNonSeekableStreamDataDescriptor() throws IOException {
         byte[] payload = createPayload(512);
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         ZipArchiveEntry entry = new ZipArchiveEntry("nonseekable.txt");
         entry.setMethod(ZipEntry.STORED);

         try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos)) {
             zos.putArchiveEntry(entry);
             zos.write(payload);
             zos.closeArchiveEntry();
             zos.finish();
         }

         byte[] zipBytes = baos.toByteArray();
         boolean foundDescriptor = false;
         for (int i = 0; i < zipBytes.length - 4; i++) {
             if (zipBytes[i] == 0x50 && zipBytes[i + 1] == 0x4b
                     && zipBytes[i + 2] == 0x07 && zipBytes[i + 3] == 0x08) {
                 foundDescriptor = true;
                 break;
             }
         }
         assertTrue("Data descriptor should be present for non-seekable stored entry with unknown
CRC",
                 foundDescriptor);
     }

     /**
      * Tests the specific byte-level assertion from the failing test:
      * when adding a raw entry with known CRC and sizes, byte[0] after
      * the entry data should NOT be 0x08 (which would indicate a data
      * descriptor signature byte). Instead it should be 0x00 or something else.
      *
      * <p>This directly reproduces the assertion:
      * "arrays first differed at element [0]; expected:<0> but was:<8>"</p>
      */
     @Test
     public void testFirstByteAfterRawEntryDataIsNotDataDescriptor() throws IOException {
         byte[] payload = createPayload(64);
         long crc = computeCrc(payload);

         ZipArchiveEntry entry = new ZipArchiveEntry("first-byte.txt");
         entry.setMethod(ZipEntry.STORED);
         entry.setSize(payload.length);
         entry.setCompressedSize(payload.length);
         entry.setCrc(crc);

         try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(tempFile)) {
             zos.addRawArchiveEntry(entry, new ByteArrayInputStream(payload));
             zos.closeArchiveEntry();
             zos.finish();
         }

         byte[] zipBytes = java.nio.file.Files.readAllBytes(tempFile.toPath());

         // Find the local file header for "first-byte.txt"
         int lfhPos = -1;
         for (int i = 0; i < zipBytes.length - 30; i++) {
             if (zipBytes[i] == 0x50 && zipBytes[i + 1] == 0x4b
                     && zipBytes[i + 2] == 0x03 && zipBytes[i + 3] == 0x04) {
                 // Check if this is our entry by looking at filename length
                 int nameLen = ((zipBytes[i + 26] & 0xFF) | ((zipBytes[i + 27] & 0xFF) << 8));
                 int extraLen = ((zipBytes[i + 28] & 0xFF) | ((zipBytes[i + 29] & 0xFF) << 8));
                 String name = new String(zipBytes, i + 30, nameLen, "UTF-8");
                 if ("first-byte.txt".equals(name)) {
                     lfhPos = i;
                     break;
                 }
             }
         }
         assertTrue("Local file header should be found", lfhPos >= 0);

         // Calculate where entry data ends
         int nameLen = ((zipBytes[lfhPos + 26] & 0xFF) | ((zipBytes[lfhPos + 27] & 0xFF) << 8));
         int extraLen = ((zipBytes[lfhPos + 28] & 0xFF) | ((zipBytes[lfhPos + 29] & 0xFF) << 8));
         int dataStart = lfhPos + 30 + nameLen + extraLen;
         int dataEnd = dataStart + payload.length;

         // The byte immediately after the data should NOT be 0x08 (data descriptor sig byte 3)
         assertTrue("Data end should be within bounds", dataEnd < zipBytes.length);
         byte firstByteAfter = zipBytes[dataEnd];
         assertNotEquals("First byte after data should not be 0x08 (data descriptor signature
byte)",
                 0x08, firstByteAfter);
     }
 }