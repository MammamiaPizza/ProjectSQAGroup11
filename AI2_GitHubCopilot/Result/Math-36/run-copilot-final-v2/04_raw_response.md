@Test(expected = org.apache.commons.math.exception.MathIllegalArgumentException.class)
public void testConstructorDoubleNaN() {
    new BigFraction(Double.NaN);
}

@Test(expected = org.apache.commons.math.exception.MathIllegalArgumentException.class)
public void testConstructorDoublePositiveInfinity() {
    new BigFraction(Double.POSITIVE_INFINITY);
}

@Test(expected = org.apache.commons.math.exception.MathIllegalArgumentException.class)
public void testConstructorDoubleNegativeInfinity() {
    new BigFraction(Double.NEGATIVE_INFINITY);
}

@Test
public void testDoubleConstructorBranches() {
    // k < 0 branch: sub-unity magnitude invokes denominator = BigInteger.ZERO.flipBit(-k)
    BigFraction f = new BigFraction(0.5);
    Assert.assertEquals(0.5, f.doubleValue(), 0.0);
    // k >= 0 branch: magnitude >= 1 invokes numerator multiply and denominator = ONE
    f = new BigFraction(2.0);
    Assert.assertEquals(2.0, f.doubleValue(), 0.0);
    Assert.assertFalse("Must not be NaN", Double.isNaN(f.doubleValue()));
}