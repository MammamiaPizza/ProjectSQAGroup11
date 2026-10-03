@Test
public void testAbsHandlesSpecialValuesAndFiniteComponentOrderings() {
    assertTrue(Double.isNaN(Complex.NaN.abs()));
    assertEquals(Double.POSITIVE_INFINITY,
            new Complex(Double.POSITIVE_INFINITY, 1.0).abs(), 0.0);
    assertEquals(5.0, new Complex(3.0, 4.0).abs(), 0.0);
    assertEquals(5.0, new Complex(4.0, 3.0).abs(), 0.0);
    assertEquals(2.0, new Complex(0.0, -2.0).abs(), 0.0);
}

@Test
public void testInverseTrigonometricFunctionsAtZeroAndNaN() {
    Complex acos = Complex.ZERO.acos();
    assertEquals(Math.PI / 2.0, acos.getReal(), 1.0e-12);
    assertEquals(0.0, acos.getImaginary(), 1.0e-12);

    Complex asin = Complex.ZERO.asin();
    assertEquals(0.0, asin.getReal(), 1.0e-12);
    assertEquals(0.0, asin.getImaginary(), 1.0e-12);

    Complex atan = Complex.ZERO.atan();
    assertEquals(0.0, atan.getReal(), 1.0e-12);
    assertEquals(0.0, atan.getImaginary(), 1.0e-12);

    assertTrue(Complex.NaN.acos().isNaN());
    assertTrue(Complex.NaN.asin().isNaN());
    assertTrue(Complex.NaN.atan().isNaN());
}