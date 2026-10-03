@org.junit.Test
    public void testCopyDefault() throws java.io.IOException {
        byte[] data = "test".getBytes("US-ASCII");
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        long count = IOUtils.copy(in, out);
        org.junit.Assert.assertEquals(data.length, count);
        org.junit.Assert.assertArrayEquals(data, out.toByteArray());
    }

 @org.junit.Test
 public void testCopyWithSmallBuffer() throws java.io.IOException {
     byte[] data = "abcde".getBytes("US-ASCII");
     java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
     java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
     long count = IOUtils.copy(in, out, 1);
     org.junit.Assert.assertEquals(data.length, count);
     org.junit.Assert.assertArrayEquals(data, out.toByteArray());
 }

 @org.junit.Test
 public void testReadFully() throws java.io.IOException {
     byte[] data = "data".getBytes("US-ASCII");
     java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
     byte[] buf = new byte[4];
     int n = IOUtils.readFully(in, buf);
     org.junit.Assert.assertEquals(4, n);
     org.junit.Assert.assertArrayEquals(data, buf);
 }

 @org.junit.Test
 public void testCloseQuietly() {
     java.io.Closeable throwing = new java.io.Closeable() {
         public void close() throws java.io.IOException {
             throw new java.io.IOException("fail");
         }
     };
     IOUtils.closeQuietly(throwing);
     IOUtils.closeQuietly(null);
 }