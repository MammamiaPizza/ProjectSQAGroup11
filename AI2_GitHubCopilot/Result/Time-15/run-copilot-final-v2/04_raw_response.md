@Test
public void testSafeAddOverflows() {
    try {
        FieldUtils.safeAdd(Integer.MAX_VALUE, 1);
        fail("Expected ArithmeticException for int overflow");
    } catch (ArithmeticException e) {
    }
    try {
        FieldUtils.safeAdd(Integer.MIN_VALUE, -1);
        fail("Expected ArithmeticException for int overflow");
    } catch (ArithmeticException e) {
    }
    try {
        FieldUtils.safeAdd(Long.MAX_VALUE, 1L);
        fail("Expected ArithmeticException for long overflow");
    } catch (ArithmeticException e) {
    }
    try {
        FieldUtils.safeAdd(Long.MIN_VALUE, -1L);
        fail("Expected ArithmeticException for long overflow");
    } catch (ArithmeticException e) {
    }
    assertEquals(0, FieldUtils.safeAdd(0, 0));
    assertEquals(100, FieldUtils.safeAdd(50, 50));
    assertEquals(Long.MAX_VALUE - 1, FieldUtils.safeAdd(Long.MAX_VALUE - 1, 0L));
}

@Test
public void testSafeMultiplyToInt() {
    assertEquals(200, FieldUtils.safeMultiplyToInt(10L, 20L));
    assertEquals(0, FieldUtils.safeMultiplyToInt(0L, 999L));
    assertEquals(-10, FieldUtils.safeMultiplyToInt(1L, -10L));
    assertEquals(Integer.MAX_VALUE, FieldUtils.safeMultiplyToInt(Integer.MAX_VALUE, 1L));
    assertEquals(Integer.MIN_VALUE, FieldUtils.safeMultiplyToInt(Integer.MIN_VALUE, 1L));
    try {
        FieldUtils.safeMultiplyToInt((long) Integer.MAX_VALUE + 1, 2L);
        fail("Expected ArithmeticException for overflow");
    } catch (ArithmeticException e) {
    }
    try {
        FieldUtils.safeMultiplyToInt((long) Integer.MIN_VALUE, 2L);
        fail("Expected ArithmeticException for overflow");
    } catch (ArithmeticException e) {
    }
}

@Test
public void testGetWrappedValue() {
    assertEquals(0, FieldUtils.getWrappedValue(6, 0, 5));
    assertEquals(1, FieldUtils.getWrappedValue(7, 0, 5));
    assertEquals(0, FieldUtils.getWrappedValue(-6, 0, 5));
    assertEquals(4, FieldUtils.getWrappedValue(-2, 0, 5));
    assertEquals(3, FieldUtils.getWrappedValue(3, 0, 5));
    assertEquals(0, FieldUtils.getWrappedValue(0, 0, 5));
    assertEquals(5, FieldUtils.getWrappedValue(5, 0, 5));
}

@Test
public void testEquals() {
    assertTrue(FieldUtils.equals(null, null));
    Object obj = new Integer(5);
    assertTrue(FieldUtils.equals(obj, obj));
    assertFalse(FieldUtils.equals(obj, null));
    assertFalse(FieldUtils.equals(null, obj));
    assertTrue(FieldUtils.equals(new Integer(5), new Integer(5)));
    assertFalse(FieldUtils.equals(new Integer(5), new Integer(6)));
}