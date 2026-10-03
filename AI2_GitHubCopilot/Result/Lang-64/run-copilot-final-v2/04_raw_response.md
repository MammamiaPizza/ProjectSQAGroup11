@Test
public void testGetEnum_Valid() {
    Enum result = ValuedEnum.getEnum(ColorValuedEnum.class, ColorValuedEnum.RED.getValue());
    assertNotNull(result);
    assertEquals(ColorValuedEnum.RED, result);
}

@Test
public void testGetEnum_NotFound() {
    Enum result = ValuedEnum.getEnum(ColorValuedEnum.class, 999);
    assertNull(result);
}

@Test
public void testGetEnum_NullClass() {
    try {
        ValuedEnum.getEnum(null, 0);
        fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
        // expected
    }
}

@Test
public void testToString_Caching() {
    String s1 = ColorValuedEnum.RED.toString();
    assertNotNull(s1);
    String s2 = ColorValuedEnum.RED.toString();
    assertSame(s1, s2);
}