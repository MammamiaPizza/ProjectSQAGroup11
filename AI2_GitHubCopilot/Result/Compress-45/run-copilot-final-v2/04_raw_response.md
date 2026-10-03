@Test
 public void testComputeCheckSum() {
     byte[] buf = new byte[]{1, 2, 3, 4};
     assertEquals(10L, TarUtils.computeCheckSum(buf));
 }

 @Test
 public void testFormatLongOctalOrBinaryBytesPositive() {
     byte[] buf = new byte[8];
     long value = (1L << 55) - 1;
     TarUtils.formatLongOctalOrBinaryBytes(value, buf, 0, 8);
     assertEquals((byte) 0x80, buf[0]);
 }

 @Test
 public void testFormatOctalBytesNonOverflow() {
     byte[] buf = new byte[8];
     int length = 4;
     int result = TarUtils.formatOctalBytes(0L, buf, 0, length);
     assertEquals(4, result);
     assertEquals(0, buf[length - 2]);
     assertEquals((byte) ' ', buf[length - 1]);
 }

 @Test
 public void testFormatLongOctalBytesLongMinValue() {
     byte[] buf = new byte[8];
     try {
         TarUtils.formatLongOctalBytes(Long.MIN_VALUE, buf, 0, 8);
         fail("Expected IllegalArgumentException");
     } catch (IllegalArgumentException e) {
     }
 }