@Test
    public void testAbsNaN() {
        assertTrue(Double.isNaN(Complex.NaN.abs()));
    }

 @Test
 public void testAbsInfinite() {
     assertEquals(Double.POSITIVE_INFINITY, Complex.INF.abs(), 0.0);
 }

 @Test
 public void testAbs() {
     assertEquals(5.0, new Complex(3.0, 4.0).abs(), 1.0e-12);
     assertEquals(5.0, new Complex(4.0, 3.0).abs(), 1.0e-12);
     assertEquals(5.0, new Complex(0.0, 5.0).abs(), 1.0e-12);
     assertEquals(5.0, new Complex(5.0, 0.0).abs(), 1.0e-12);
     assertEquals(0.0, Complex.ZERO.abs(), 0.0);
 }

 @Test
 public void testAtan() {
     assertTrue(Complex.NaN.atan().isNaN());
     Complex result = new Complex(1.0, 1.0).atan();
     assertFalse(result.isNaN());
     assertFalse(result.isInfinite());
 }