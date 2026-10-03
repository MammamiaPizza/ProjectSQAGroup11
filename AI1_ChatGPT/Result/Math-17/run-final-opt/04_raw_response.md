@Test
public void testSignalingNaNMultiplySignalsInvalidAndReturnsQuietNaN() {
    Dfp signalingNaN = field.getZero().newInstance((byte) 1, Dfp.SNAN);

    Dfp result = field.newDfp(2).multiply(signalingNaN);

    assertEquals(Dfp.QNAN, result.classify());
    assertTrue((field.getIEEEFlags() & 1) != 0);
}

@Test
public void testNegativeZeroDoubleConstructionPreservesSign() {
    Dfp negativeZero = field.newDfp(-0.0d);

    assertTrue(negativeZero.isZero());
    assertEquals(Double.doubleToLongBits(-0.0d),
                 Double.doubleToLongBits(negativeZero.toDouble()));
}

@Test
public void testSmallestDoubleSubnormalConstructsAsPositiveFiniteValue() {
    Dfp smallestSubnormal = field.newDfp(Double.MIN_VALUE);

    assertEquals(Dfp.FINITE, smallestSubnormal.classify());
    assertTrue(smallestSubnormal.greaterThan(field.getZero()));
}