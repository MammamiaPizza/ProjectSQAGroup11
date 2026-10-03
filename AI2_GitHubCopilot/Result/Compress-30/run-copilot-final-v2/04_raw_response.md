@Test
    public void testSingleByteReadDecompressesCorrectly() throws Exception {
        byte[] original = "Test data for single byte read".getBytes("UTF-8");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream cos =
            new org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream(baos);
        cos.write(original);
        cos.close();
        byte[] compressed = baos.toByteArray();
        BZip2CompressorInputStream in = new BZip2CompressorInputStream(new
ByteArrayInputStream(compressed));
        for (int i = 0; i < original.length; i++) {
            int b = in.read();
            assertTrue("Unexpected EOF at position " + i, b != -1);
            assertEquals(original[i] & 0xff, b);
        }
        assertEquals(-1, in.read());
        in.close();
    }

 @Test
 public void testDecompressConcatenatedTrueWithSingleStream() throws Exception {
     byte[] original = "Test for concatenated flag true".getBytes("UTF-8");
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream cos =
         new org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream(baos);
     cos.write(original);
     cos.close();
     byte[] compressed = baos.toByteArray();
     BZip2CompressorInputStream in = new BZip2CompressorInputStream(
         new ByteArrayInputStream(compressed), true);
     byte[] buf = new byte[1024];
     int total = 0;
     int n;
     while ((n = in.read(buf, total, buf.length - total)) != -1) {
         total += n;
     }
     assertEquals(original.length, total);
     byte[] result = new byte[total];
     System.arraycopy(buf, 0, result, 0, total);
     assertArrayEquals(original, result);
     in.close();
 }

 @Test
 public void testDecompressConcatenatedFalseWithSingleStream() throws Exception {
     byte[] original = "Test for concatenated flag false".getBytes("UTF-8");
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream cos =
         new org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream(baos);
     cos.write(original);
     cos.close();
     byte[] compressed = baos.toByteArray();
     BZip2CompressorInputStream in = new BZip2CompressorInputStream(
         new ByteArrayInputStream(compressed), false);
     ByteArrayOutputStream result = new ByteArrayOutputStream();
     byte[] buf = new byte[1024];
     int n;
     while ((n = in.read(buf)) != -1) {
         result.write(buf, 0, n);
     }
     assertArrayEquals(original, result.toByteArray());
     in.close();
 }

 @Test
 public void testReadBufferExactDecompressedSize() throws Exception {
     byte[] original = "Exact buffer size test".getBytes("UTF-8");
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream cos =
         new org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream(baos);
     cos.write(original);
     cos.close();
     byte[] compressed = baos.toByteArray();
     BZip2CompressorInputStream in = new BZip2CompressorInputStream(new
ByteArrayInputStream(compressed));
     byte[] buf = new byte[original.length];
     int total = 0;
     int n;
     while (total < buf.length && (n = in.read(buf, total, buf.length - total)) != -1) {
         total += n;
     }
     assertEquals(original.length, total);
     assertArrayEquals(original, buf);
     assertEquals(-1, in.read(buf, 0, 1));
     in.close();
 }