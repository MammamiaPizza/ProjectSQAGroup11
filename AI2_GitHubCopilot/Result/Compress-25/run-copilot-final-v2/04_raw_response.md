@Test
    public void testDataDescriptorEntryFollowedByStoredEntry() throws Exception {
        final java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        final org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream zos =
            new org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream(baos);
        // first entry: stored, data descriptor
        final org.apache.commons.compress.archivers.zip.ZipArchiveEntry e1 =
            new org.apache.commons.compress.archivers.zip.ZipArchiveEntry("entry1");
        e1.setMethod(org.apache.commons.compress.archivers.zip.ZipEntry.STORED);
        final byte[] data1 = new byte[50];
        for (int i = 0; i < 50; i++) {
            data1[i] = (byte) (i + 1);
        }
        final java.util.zip.CRC32 crc1 = new java.util.zip.CRC32();
        crc1.update(data1);
        e1.setSize(data1.length);
        e1.setCompressedSize(data1.length);
        e1.setCrc(crc1.getValue());
        e1.getGeneralPurposeBit().useDataDescriptor(true);
        zos.putArchiveEntry(e1);
        zos.write(data1);
        zos.closeArchiveEntry();

     // second entry: stored, no data descriptor
     final org.apache.commons.compress.archivers.zip.ZipArchiveEntry e2 =
         new org.apache.commons.compress.archivers.zip.ZipArchiveEntry("entry2");
     e2.setMethod(org.apache.commons.compress.archivers.zip.ZipEntry.STORED);
     final byte[] data2 = new byte[100];
     for (int i = 0; i < 100; i++) {
         data2[i] = (byte) (0xFF - i);
     }
     final java.util.zip.CRC32 crc2 = new java.util.zip.CRC32();
     crc2.update(data2);
     e2.setSize(data2.length);
     e2.setCompressedSize(data2.length);
     e2.setCrc(crc2.getValue());
     zos.putArchiveEntry(e2);
     zos.write(data2);
     zos.closeArchiveEntry();
     zos.finish();
     zos.close();

     final java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
     final org.apache.commons.compress.archivers.zip.ZipArchiveInputStream zis =
         new org.apache.commons.compress.archivers.zip.ZipArchiveInputStream(bais);

     org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry = zis.getNextZipEntry();
     org.junit.Assert.assertNotNull(entry);
     org.junit.Assert.assertEquals("entry1", entry.getName());
     final byte[] read1 = new byte[50];
     int total = 0;
     while (total < 50) {
         final int r = zis.read(read1, total, 50 - total);
         if (r < 0) break;
         total += r;
     }
     org.junit.Assert.assertEquals(50, total);
     org.junit.Assert.assertArrayEquals(data1, read1);

     entry = zis.getNextZipEntry();
     org.junit.Assert.assertNotNull(entry);
     org.junit.Assert.assertEquals("entry2", entry.getName());
     final byte[] read2 = new byte[100];
     total = 0;
     while (total < 100) {
         final int r = zis.read(read2, total, 100 - total);
         if (r < 0) break;
         total += r;
     }
     org.junit.Assert.assertEquals(100, total);
     org.junit.Assert.assertArrayEquals(data2, read2);
 }

 @Test
 public void testPartialReadThenTransitionWithDataDescriptor() throws Exception {
     final java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
     final org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream zos =
         new org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream(baos);

     // entry1: stored with data descriptor
     final org.apache.commons.compress.archivers.zip.ZipArchiveEntry e1 =
         new org.apache.commons.compress.archivers.zip.ZipArchiveEntry("pre");
     e1.setMethod(org.apache.commons.compress.archivers.zip.ZipEntry.STORED);
     final byte[] data1 = new byte[200];
     for (int i = 0; i < 200; i++) { data1[i] = (byte) i; }
     final java.util.zip.CRC32 crc1 = new java.util.zip.CRC32();
     crc1.update(data1);
     e1.setSize(data1.length);
     e1.setCompressedSize(data1.length);
     e1.setCrc(crc1.getValue());
     e1.getGeneralPurposeBit().useDataDescriptor(true);
     zos.putArchiveEntry(e1);
     zos.write(data1);
     zos.closeArchiveEntry();

     // entry2: stored without data descriptor
     final org.apache.commons.compress.archivers.zip.ZipArchiveEntry e2 =
         new org.apache.commons.compress.archivers.zip.ZipArchiveEntry("post");
     e2.setMethod(org.apache.commons.compress.archivers.zip.ZipEntry.STORED);
     final byte[] data2 = new byte[50];
     for (int i = 0; i < 50; i++) { data2[i] = (byte) (0xA0 + i); }
     final java.util.zip.CRC32 crc2 = new java.util.zip.CRC32();
     crc2.update(data2);
     e2.setSize(data2.length);
     e2.setCompressedSize(data2.length);
     e2.setCrc(crc2.getValue());
     zos.putArchiveEntry(e2);
     zos.write(data2);
     zos.closeArchiveEntry();
     zos.finish();
     zos.close();

     final java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
     final org.apache.commons.compress.archivers.zip.ZipArchiveInputStream zis =
         new org.apache.commons.compress.archivers.zip.ZipArchiveInputStream(bais);

     org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry = zis.getNextZipEntry();
     org.junit.Assert.assertNotNull(entry);
     org.junit.Assert.assertEquals("pre", entry.getName());
     // read only part of the data
     final byte[] partial = new byte[100];
     int total = 0;
     while (total < 100) {
         final int r = zis.read(partial, total, 100 - total);
         if (r < 0) break;
         total += r;
     }
     org.junit.Assert.assertEquals(100, total);
     for (int j = 0; j < 100; j++) {
         org.junit.Assert.assertEquals(data1[j], partial[j]);
     }
     // now move to next entry without reading the rest of entry1
     entry = zis.getNextZipEntry();
     org.junit.Assert.assertNotNull(entry);
     org.junit.Assert.assertEquals("post", entry.getName());
     final byte[] read2 = new byte[50];
     total = 0;
     while (total < 50) {
         final int r = zis.read(read2, total, 50 - total);
         if (r < 0) break;
         total += r;
     }
     org.junit.Assert.assertEquals(50, total);
     org.junit.Assert.assertArrayEquals(data2, read2);
 }

 @Test
 public void testSkipDataDescriptorEntryWithoutReading() throws Exception {
     final java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
     final org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream zos =
         new org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream(baos);

     // entry1: stored with data descriptor
     final org.apache.commons.compress.archivers.zip.ZipArchiveEntry e1 =
         new org.apache.commons.compress.archivers.zip.ZipArchiveEntry("skipme");
     e1.setMethod(org.apache.commons.compress.archivers.zip.ZipEntry.STORED);
     final byte[] data1 = new byte[64];
     for (int i = 0; i < 64; i++) { data1[i] = (byte) (i * 3); }
     final java.util.zip.CRC32 crc1 = new java.util.zip.CRC32();
     crc1.update(data1);
     e1.setSize(data1.length);
     e1.setCompressedSize(data1.length);
     e1.setCrc(crc1.getValue());
     e1.getGeneralPurposeBit().useDataDescriptor(true);
     zos.putArchiveEntry(e1);
     zos.write(data1);
     zos.closeArchiveEntry();

     // entry2: stored without data descriptor
     final org.apache.commons.compress.archivers.zip.ZipArchiveEntry e2 =
         new org.apache.commons.compress.archivers.zip.ZipArchiveEntry("target");
     e2.setMethod(org.apache.commons.compress.archivers.zip.ZipEntry.STORED);
     final byte[] data2 = new byte[32];
     for (int i = 0; i < 32; i++) { data2[i] = (byte) 0x42; }
     final java.util.zip.CRC32 crc2 = new java.util.zip.CRC32();
     crc2.update(data2);
     e2.setSize(data2.length);
     e2.setCompressedSize(data2.length);
     e2.setCrc(crc2.getValue());
     zos.putArchiveEntry(e2);
     zos.write(data2);
     zos.closeArchiveEntry();
     zos.finish();
     zos.close();

     final java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
     final org.apache.commons.compress.archivers.zip.ZipArchiveInputStream zis =
         new org.apache.commons.compress.archivers.zip.ZipArchiveInputStream(bais);

     org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry = zis.getNextZipEntry();
     org.junit.Assert.assertNotNull(entry);
     org.junit.Assert.assertEquals("skipme", entry.getName());
     // skip entry entirely
     entry = zis.getNextZipEntry();
     org.junit.Assert.assertNotNull(entry);
     org.junit.Assert.assertEquals("target", entry.getName());
     final byte[] read2 = new byte[32];
     int total = 0;
     while (total < 32) {
         final int r = zis.read(read2, total, 32 - total);
         if (r < 0) break;
         total += r;
     }
     org.junit.Assert.assertEquals(32, total);
     org.junit.Assert.assertArrayEquals(data2, read2);
 }