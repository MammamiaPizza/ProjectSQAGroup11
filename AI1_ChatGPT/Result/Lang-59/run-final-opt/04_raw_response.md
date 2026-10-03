@Test
public void testConstructorsHandleZeroCapacityAndNullString() {
    StrBuilder zeroCapacity = new StrBuilder(0);
    zeroCapacity.append('x');
    assertEquals("x", zeroCapacity.toString());

    StrBuilder nullString = new StrBuilder((java.lang.String) null);
    nullString.append('y');
    assertEquals("y", nullString.toString());
}

@Test
public void testAppendObjectAndStringNullUseConfiguredNullText() {
    StrBuilder builder = new StrBuilder().setNullText("<null>");

    builder.append((java.lang.Object) null);
    builder.append((java.lang.String) null);
    builder.append((java.lang.Object) new java.lang.StringBuilder("value"));

    assertEquals("<null><null>value", builder.toString());
}

@Test
public void testAppendStringSliceAcceptsBoundariesAndRejectsInvalidRanges() {
    StrBuilder builder = new StrBuilder();

    builder.append("ABCDE", 1, 3);
    builder.append("ABCDE", 5, 0);
    assertEquals("BCD", builder.toString());

    try {
        builder.append("ABCDE", -1, 1);
        org.junit.Assert.fail("Expected StringIndexOutOfBoundsException");
    } catch (java.lang.StringIndexOutOfBoundsException expected) {
    }

    try {
        builder.append("ABCDE", 4, 2);
        org.junit.Assert.fail("Expected StringIndexOutOfBoundsException");
    } catch (java.lang.StringIndexOutOfBoundsException expected) {
    }

    assertEquals("BCD", builder.toString());
}

@Test
public void testAppendPrimitiveValuesUsesStringRepresentations() {
    StrBuilder builder = new StrBuilder();

    builder.append('x').append(12).append(34L).append(1.5f).append(2.5d);

    assertEquals("x12341.52.5", builder.toString());
}