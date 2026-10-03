public void testPrivateConstructor() throws Exception {
        java.lang.reflect.Constructor c =
org.apache.commons.compress.archivers.tar.TarUtils.class.getDeclaredConstructor(new Class[0]);
        c.setAccessible(true);
        Object obj = c.newInstance(new Object[0]);
        assertNotNull(obj);
    }

 public void testComputeCheckSum() {
     byte[] buf = new byte[] { 1, 2, 3, -1, -2 };
     long sum = org.apache.commons.compress.archivers.tar.TarUtils.computeCheckSum(buf);
     assertEquals(1 + 2 + 3 + 255 + 254, sum);
 }

 public void testFormatOctalBytes() {
     byte[] buf = new byte[20];
     int off = 4;
     int len = 8;
     int ret = org.apache.commons.compress.archivers.tar.TarUtils.formatOctalBytes(0, buf, off,
len);
     assertEquals(off + len, ret);
     assertEquals(' ', buf[off + len - 1]);
     // test with non-zero value to cover loop in formatUnsignedOctalString
     ret = org.apache.commons.compress.archivers.tar.TarUtils.formatOctalBytes(123, buf, off + len,
len);
     assertEquals(off + 2 * len, ret);
     assertEquals(' ', buf[off + 2 * len - 1]);
 }

 public void testFormatCheckSumOctalBytes() {
     byte[] buf = new byte[20];
     int off = 2;
     int len = 10;
     int ret = org.apache.commons.compress.archivers.tar.TarUtils.formatCheckSumOctalBytes(0, buf,
off, len);
     assertEquals(off + len, ret);
     assertEquals(0, buf[off + len - 2]);
     assertEquals(' ', buf[off + len - 1]);
 }