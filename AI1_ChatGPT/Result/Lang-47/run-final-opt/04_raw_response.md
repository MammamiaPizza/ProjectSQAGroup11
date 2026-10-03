@Test
public void testEmptyConstructorsCreateUsableBuilders() {
    StrBuilder zeroCapacity = new StrBuilder(0);
    assertEquals(0, zeroCapacity.length());
    assertSame(zeroCapacity, zeroCapacity.append('x'));
    assertEquals("x", zeroCapacity.toString());

    StrBuilder nullString = new StrBuilder((String) null);
    assertEquals(0, nullString.length());
    assertSame(nullString, nullString.append('y'));
    assertEquals("y", nullString.toString());
}

@Test
public void testAppendCharacterAndNumericValues() {
    StrBuilder builder = new StrBuilder();

    assertSame(builder, builder.append('a'));
    assertSame(builder, builder.append(7));
    assertSame(builder, builder.append(8L));
    assertSame(builder, builder.append(1.5f));
    assertSame(builder, builder.append(2.5d));

    assertEquals("a781.52.5", builder.toString());
}

@Test
public void testAppendObjectAndStringRangesWithConfiguredNullText() {
    StrBuilder builder = new StrBuilder();
    builder.setNullText("<null>");

    assertSame(builder, builder.append((Object) null));
    assertSame(builder, builder.append((Object) "X"));
    assertSame(builder, builder.append("abcdef", 2, 3));
    assertSame(builder, builder.append("ignored", 1, 0));
    assertSame(builder, builder.append((String) null));
    assertSame(builder, builder.append((String) null, 0, 0));

    assertEquals("<null>Xcde<null><null>", builder.toString());
}

@Test(expected = StringIndexOutOfBoundsException.class)
public void testAppendStringRangeRejectsNegativeStartIndex() {
    new StrBuilder().append("abc", -1, 1);
}