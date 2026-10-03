@Test
public void testAbsNanAndInfinite() {
    assertTrue(Double.isNaN(Complex.NaN.abs()));
    assertEquals(Double.POSITIVE_INFINITY, Complex.INF.abs(), 0.0);
    assertEquals(5.0, new Complex(3.0, 4.0).abs(), 0.0);
    assertEquals(0.0, new Complex(0.0, 0.0).abs(), 0.0);
}

@Test
public void testAddDoubleNaN() {
    Complex z = new Complex(1.0, 2.0);
    assertTrue(z.add(Double.NaN).isNaN());
    assertTrue(Complex.NaN.add(3.0).isNaN());
}

@Test
public void testAddComplexNaN() {
    assertTrue(Complex.NaN.add(Complex.ONE).isNaN());
    assertTrue(Complex.ONE.add(Complex.NaN).isNaN());
}

@Test
public void testAcosNaN() {
    assertTrue(Complex.NaN.acos().isNaN());
}