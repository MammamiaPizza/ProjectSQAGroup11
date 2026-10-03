@Test
public void compareDoubleOrdersValuesSignedZeroAndNaN() {
    assertEquals(-1, NumberUtils.compare(1.0d, 2.0d));
    assertEquals(1, NumberUtils.compare(2.0d, 1.0d));
    assertEquals(0, NumberUtils.compare(1.0d, 1.0d));
    assertEquals(-1, NumberUtils.compare(-0.0d, 0.0d));
    assertEquals(1, NumberUtils.compare(0.0d, -0.0d));
    assertEquals(0, NumberUtils.compare(Double.NaN, Double.NaN));
}

@Test
public void compareFloatOrdersValuesSignedZeroAndNaN() {
    assertEquals(-1, NumberUtils.compare(1.0f, 2.0f));
    assertEquals(1, NumberUtils.compare(2.0f, 1.0f));
    assertEquals(0, NumberUtils.compare(1.0f, 1.0f));
    assertEquals(-1, NumberUtils.compare(-0.0f, 0.0f));
    assertEquals(1, NumberUtils.compare(0.0f, -0.0f));
    assertEquals(0, NumberUtils.compare(Float.NaN, Float.NaN));
}