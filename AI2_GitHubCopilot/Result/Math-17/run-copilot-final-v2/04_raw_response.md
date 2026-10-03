@Test
public void testDoubleConstructorCreatesFinite() {
    Dfp zero = field.newDfp(0.0);
    assertTrue("0.0 should be zero", zero.isZero());
    assertEquals(Dfp.FINITE, zero.nans);
    Dfp one = field.newDfp(1.0);
    assertFalse(one.isZero());
    assertEquals(Dfp.FINITE, one.nans);
}

@Test
public void testNewInstanceWithNansAndSign() {
    Dfp posQNaN = field.getZero().newInstance((byte) 1, Dfp.QNAN);
    assertTrue(posQNaN.isNaN());
    assertEquals(Dfp.QNAN, posQNaN.nans);
    assertEquals(1, posQNaN.sign);
    Dfp negSNaN = field.getZero().newInstance((byte) -1, Dfp.SNAN);
    assertTrue(negSNaN.isNaN());
    assertEquals(Dfp.SNAN, negSNaN.nans);
    assertEquals(-1, negSNaN.sign);
}

@Test
public void testMultiplyOverflowIsInfinite() {
    Dfp a = field.newDfp("1e32760");
    Dfp b = field.newDfp("1e10");
    Dfp product = a.multiply(b);
    assertTrue("overflow product should be infinite", product.isInfinite());
    assertFalse(product.isNaN());
}

@Test
public void testMultiplyNegativeZeroPreservesSign() {
    Dfp zero = field.newDfp("0");
    Dfp negOne = field.newDfp("-1");
    Dfp negZero = Dfp.copysign(zero, negOne);
    assertTrue(negZero.isZero());
    assertEquals(-1, negZero.sign);
    Dfp three = field.newDfp("3");
    Dfp result = negZero.multiply(three);
    assertTrue(result.isZero());
    assertEquals(-1, result.sign);
}