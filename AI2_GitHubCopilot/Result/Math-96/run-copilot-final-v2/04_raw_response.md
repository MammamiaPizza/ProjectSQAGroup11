@Test
public void testAbsNaN() {
    assertTrue(Double.isNaN(new Complex(Double.NaN, 0.0).abs()));
}

@Test
public void testAbsInfinite() {
    assertTrue(Double.isInfinite(new Complex(Double.POSITIVE_INFINITY, 0.0).abs()));
}

@Test
public void testAbsNormalValues() {
    assertEquals(5.0, new Complex(0.0, 5.0).abs(), 0.0);
    assertEquals(3.0, new Complex(3.0, 0.0).abs(), 0.0);
    assertEquals(5.0, new Complex(3.0, 4.0).abs(), 0.0);
    assertEquals(5.0, new Complex(4.0, 3.0).abs(), 0.0);
    assertEquals(5.0, new Complex(-5.0, 0.0).abs(), 0.0);
    assertEquals(2.5, new Complex(0.0, -2.5).abs(), 0.0);
}

@Test
public void testTrigNaN() {
    Complex nan = Complex.NaN;
    assertTrue(nan.acos().isNaN());
    assertTrue(nan.asin().isNaN());
    assertTrue(nan.atan().isNaN());
}