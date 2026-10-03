@Test(expected = NullPointerException.class)
 public void testReadByteArrayWithNullBufferThrowsNullPointerException() throws Exception {
     java.util.zip.Checksum checksum = new java.util.zip.CRC32();
     java.io.ByteArrayInputStream is = new java.io.ByteArrayInputStream(new byte[]{1});
     ChecksumCalculatingInputStream cis = new ChecksumCalculatingInputStream(checksum, is);
     cis.read((byte[]) null);
 }

 @Test(expected = NullPointerException.class)
 public void testReadByteArrayOffsetWithNullBufferThrowsNullPointerException() throws Exception {
     java.util.zip.Checksum checksum = new java.util.zip.CRC32();
     java.io.ByteArrayInputStream is = new java.io.ByteArrayInputStream(new byte[]{1});
     ChecksumCalculatingInputStream cis = new ChecksumCalculatingInputStream(checksum, is);
     cis.read(null, 0, 1);
 }

 @Test
 public void testReadByteArrayFromEmptyStreamReturnsMinusOne() throws Exception {
     java.util.zip.Checksum checksum = new java.util.zip.CRC32();
     java.io.ByteArrayInputStream is = new java.io.ByteArrayInputStream(new byte[0]);
     ChecksumCalculatingInputStream cis = new ChecksumCalculatingInputStream(checksum, is);
     byte[] buf = new byte[10];
     assertEquals(-1, cis.read(buf));
     assertEquals(0, cis.getValue());
 }

 @Test
 public void testReadByteArrayOffsetFromEmptyStreamReturnsMinusOne() throws Exception {
     java.util.zip.Checksum checksum = new java.util.zip.CRC32();
     java.io.ByteArrayInputStream is = new java.io.ByteArrayInputStream(new byte[0]);
     ChecksumCalculatingInputStream cis = new ChecksumCalculatingInputStream(checksum, is);
     byte[] buf = new byte[10];
     assertEquals(-1, cis.read(buf, 0, 10));
     assertEquals(0, cis.getValue());
 }