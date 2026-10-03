@Test
public void quotedEscapesIncludingUnknownAndTrailingTildeAreDecodedConsistently() {
    JsonPointer pointer = JsonPointer.compile("/a~0b~1c~2d~/x");

    assertEquals("/a~0b~1c~2d~/x", pointer.toString());
    assertEquals("a~b/c~2d~", pointer.getMatchingProperty());

    JsonPointer tail = pointer.tail();
    assertEquals("/x", tail.toString());
    assertSame(tail.tail(), tail.matchProperty("x"));
}

@Test
public void maximumIntegerIndexIsAcceptedButOversizedIndexesAreProperties() {
    JsonPointer maximum = JsonPointer.compile("/2147483647");
    assertEquals(Integer.MAX_VALUE, maximum.getMatchingIndex());
    assertTrue(maximum.mayMatchElement());
    assertSame(maximum.tail(), maximum.matchElement(Integer.MAX_VALUE));

    JsonPointer tooLarge = JsonPointer.compile("/2147483648");
    assertEquals(-1, tooLarge.getMatchingIndex());
    assertFalse(tooLarge.mayMatchElement());

    JsonPointer tooLong = JsonPointer.compile("/12345678901");
    assertEquals(-1, tooLong.getMatchingIndex());
    assertFalse(tooLong.mayMatchElement());
}

@Test
public void equalityAndHashCodeDependOnPointerText() {
    JsonPointer pointer = JsonPointer.compile("/same");
    JsonPointer equalPointer = JsonPointer.valueOf("/same");
    JsonPointer differentPointer = JsonPointer.compile("/different");

    assertTrue(pointer.equals(pointer));
    assertTrue(pointer.equals(equalPointer));
    assertEquals(pointer.hashCode(), equalPointer.hashCode());
    assertFalse(pointer.equals(differentPointer));
    assertFalse(pointer.equals(null));
    assertFalse(pointer.equals("/same"));
}