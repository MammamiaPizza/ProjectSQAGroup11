@Test
public void testSingleArgumentConstructorAndAdditionHandleFiniteAndNaNValues() {
    Complex value = new Complex(2.5);
    org.junit.Assert.assertEquals(2.5, value.getReal(), 0.0);
    org.junit.Assert.assertEquals(0.0, value.getImaginary(), 0.0);

    Complex scalarSum = value.add(1.5);
    org.junit.Assert.assertEquals(4.0, scalarSum.getReal(), 0.0);
    org.junit.Assert.assertEquals(0.0, scalarSum.getImaginary(), 0.0);

    Complex complexSum = new Complex(1.0, 2.0).add(new Complex(3.0, -5.0));
    org.junit.Assert.assertEquals(4.0, complexSum.getReal(), 0.0);
    org.junit.Assert.assertEquals(-3.0, complexSum.getImaginary(), 0.0);

    org.junit.Assert.assertTrue(value.add(Double.NaN).isNaN());
    org.junit.Assert.assertTrue(Complex.NaN.add(1.0).isNaN());
    org.junit.Assert.assertTrue(Complex.ONE.add(Complex.NaN).isNaN());
}

@Test
public void testAbsHandlesNaNInfinityAxesAndScaledFiniteValues() {
    org.junit.Assert.assertTrue(Double.isNaN(new Complex(Double.NaN, 1.0).abs()));
    org.junit.Assert.assertEquals(Double.POSITIVE_INFINITY,
            new Complex(Double.POSITIVE_INFINITY, 1.0).abs(), 0.0);
    org.junit.Assert.assertEquals(5.0, new Complex(3.0, 4.0).abs(), 0.0);
    org.junit.Assert.assertEquals(5.0, new Complex(4.0, 3.0).abs(), 0.0);
    org.junit.Assert.assertEquals(7.0, new Complex(0.0, -7.0).abs(), 0.0);
    org.junit.Assert.assertEquals(0.0, Complex.ZERO.abs(), 0.0);
}

@Test
public void testAcosHandlesNaNAndZero() {
    org.junit.Assert.assertTrue(Complex.NaN.acos().isNaN());

    Complex result = Complex.ZERO.acos();
    org.junit.Assert.assertEquals(Math.PI / 2.0, result.getReal(), 1.0e-15);
    org.junit.Assert.assertEquals(0.0, result.getImaginary(), 1.0e-15);
}