@Test
public void testNonPositiveInitialCapacityUsesDefaultCapacity() {
    StrBuilder defaultBuilder = new StrBuilder();
    assertEquals(defaultBuilder.capacity(), new StrBuilder(0).capacity());
    assertEquals(defaultBuilder.capacity(), new StrBuilder(-1).capacity());
}

@Test
public void testNullStringConstructorCreatesEmptyBuilder() {
    StrBuilder builder = new StrBuilder((String) null);
    assertEquals(0, builder.length());
    assertEquals("", builder.toString());
}

@Test
public void testAppendNullTextAndPrimitiveOverloads() {
    StrBuilder builder = new StrBuilder();
    builder.setNullText("<null>");
    builder.append((Object) null);
    builder.append((String) null);
    builder.append((String) null, 0, 0);
    builder.append('!');
    builder.append(12);
    builder.append(34L);
    builder.append(1.5f);
    builder.append(2.5d);

    assertEquals("<null><null><null>!12341.52.5", builder.toString());
}

@Test
public void testIndexOfDoesNotFindStaleContentAfterClearAndAppend() {
    StrBuilder builder = new StrBuilder("prefixthree");
    builder.clear();
    builder.append("prefix");

    assertEquals(-1, builder.indexOf("three"));
}