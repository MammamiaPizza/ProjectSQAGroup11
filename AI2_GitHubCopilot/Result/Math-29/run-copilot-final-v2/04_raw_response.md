@Test
public void testOpenMapRealVectorDefaultConstructor() {
    OpenMapRealVector v = new OpenMapRealVector();
    assertEquals(0, v.getDimension());
}

@Test
public void testOpenMapRealVectorConstructorWithExpectedSizeAndEpsilon() {
    int dim = 5;
    int expectedSize = 3;
    double epsilon = 1e-8;
    OpenMapRealVector v = new OpenMapRealVector(dim, expectedSize, epsilon);
    assertEquals(dim, v.getDimension());
}

@Test
public void testOpenMapRealVectorConstructorFromDoubleArray() {
    Double[] values = {1.0, -2.5, 0.0, 3.14};
    OpenMapRealVector v = new OpenMapRealVector(values);
    assertEquals(values.length, v.getDimension());
    for (int i = 0; i < values.length; i++) {
        assertEquals(values[i], v.getEntry(i), 1e-15);
    }
}

@Test
public void testEbeDivideStoredZeroByDefaultZero() {
    OpenMapRealVector v1 = new OpenMapRealVector(3);
    v1.setEntry(0, 0.0);
    OpenMapRealVector v2 = new OpenMapRealVector(3);
    OpenMapRealVector result = v1.ebeDivide(v2);
    assertTrue(Double.isNaN(result.getEntry(0)));
    assertTrue(Double.isNaN(result.getEntry(1)));
    assertTrue(Double.isNaN(result.getEntry(2)));
}