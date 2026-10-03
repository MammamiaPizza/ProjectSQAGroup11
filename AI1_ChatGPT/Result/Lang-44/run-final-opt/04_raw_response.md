@Test
public void testCompareDoubleHandlesSignedZeroAndNaN() {
    assertEquals(-1, NumberUtils.compare(1.0d, 2.0d));
    assertEquals(1, NumberUtils.compare(2.0d, 1.0d));
    assertEquals(0, NumberUtils.compare(3.0d, 3.0d));
    assertEquals(-1, NumberUtils.compare(-0.0d, 0.0d));
    assertEquals(1, NumberUtils.compare(0.0d, -0.0d));
    assertEquals(0, NumberUtils.compare(Double.NaN, Double.NaN));
    assertEquals(1, NumberUtils.compare(Double.NaN, 0.0d));
    assertEquals(-1, NumberUtils.compare(0.0d, Double.NaN));
}

@Test
public void testCompareFloatHandlesSignedZeroAndNaN() {
    assertEquals(-1, NumberUtils.compare(1.0f, 2.0f));
    assertEquals(1, NumberUtils.compare(2.0f, 1.0f));
    assertEquals(0, NumberUtils.compare(3.0f, 3.0f));
    assertEquals(-1, NumberUtils.compare(-0.0f, 0.0f));
    assertEquals(1, NumberUtils.compare(0.0f, -0.0f));
    assertEquals(0, NumberUtils.compare(Float.NaN, Float.NaN));
    assertEquals(1, NumberUtils.compare(Float.NaN, 0.0f));
    assertEquals(-1, NumberUtils.compare(0.0f, Float.NaN));
}