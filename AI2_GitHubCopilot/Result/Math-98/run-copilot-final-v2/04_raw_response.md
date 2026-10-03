@Test(expected = IllegalArgumentException.class)
public void testBigMatrixImplAddIncompatibleDimensions() {
    BigMatrixImpl a = new BigMatrixImpl(new double[][]{{1.0, 2.0}, {3.0, 4.0}});
    BigMatrixImpl b = new BigMatrixImpl(new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0,
9.0}});
    a.add(b);
}

@Test(expected = IllegalArgumentException.class)
public void testRealMatrixImplAddIncompatibleDimensions() {
    RealMatrixImpl a = new RealMatrixImpl(new double[][]{{1.0, 2.0}, {3.0, 4.0}});
    RealMatrixImpl b = new RealMatrixImpl(new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0,
8.0, 9.0}});
    a.add(b);
}

@Test
public void testBigMatrixImplColumnVectorConstructor() {
    BigDecimal[] v = new BigDecimal[]{new BigDecimal("1.0"), new BigDecimal("2.0")};
    BigMatrixImpl m = new BigMatrixImpl(v);
    assertEquals(2, m.getRowDimension());
    assertEquals(1, m.getColumnDimension());
    assertEquals(new BigDecimal("1.0"), m.getEntry(0, 0));
    assertEquals(new BigDecimal("2.0"), m.getEntry(1, 0));
}

@Test(expected = IllegalArgumentException.class)
public void testBigMatrixImplConstructorZeroRows() {
    new BigMatrixImpl(new double[0][2]);
}