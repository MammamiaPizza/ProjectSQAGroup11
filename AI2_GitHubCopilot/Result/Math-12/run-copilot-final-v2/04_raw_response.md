@Test
 public void testNextBytesFullWordExhaustion() {
     org.apache.commons.math3.random.BitsStreamGenerator gen = new
org.apache.commons.math3.random.Well19937c(12345L);
     byte[] buf = new byte[5];
     gen.nextBytes(buf);
     assertNotNull(buf);
 }

 @Test
 public void testNextIntPowerOFTwoBound() {
     org.apache.commons.math3.random.BitsStreamGenerator gen = new
org.apache.commons.math3.random.Well19937c(12345L);
     int val = gen.nextInt(32);
     assertTrue(val >=0 && val <32);
 }

 @Test(expected = org.apache.commons.math3.exception.NotStrictlyPositiveException.class)
 public void testNextIntThrowsOnZeroBound() {
     org.apache.commons.math3.random.BitsStreamGenerator gen = new
org.apache.commons.math3.random.Well19937c(12345L);
     gen.nextInt(0);
 }

 @Test
 public void testNextLongDeterminismAfterGaussianAndDouble() {
     org.apache.commons.math3.random.BitsStreamGenerator gen1 = new
org.apache.commons.math3.random.Well19937c(12345L);
     gen1.nextGaussian();
     gen1.nextDouble();
     long val1 = gen1.nextLong();
     org.apache.commons.math3.random.BitsStreamGenerator gen2 = new
org.apache.commons.math3.random.Well19937c(12345L);
     gen2.nextGaussian();
     gen2.nextDouble();
     assertEquals(val1, gen2.nextLong());
 }