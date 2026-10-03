@Test
public void nullInputCompilesToEmptyPointer()
{
    assertTrue(JsonPointer.compile(null).matches());
}

@Test
public void nonZeroLeadingZeroIndexIsNotAnArrayIndex()
{
    JsonPointer pointer = JsonPointer.compile("/01");

    assertEquals("01", pointer.getMatchingProperty());
    assertEquals(-1, pointer.getMatchingIndex());
    assertFalse(pointer.mayMatchElement());
    assertNull(pointer.matchElement(1));
    assertNotNull(pointer.matchProperty("01"));
}

@Test
public void escapedSegmentWithLiteralCharactersAndTailIsDecoded()
{
    JsonPointer pointer = JsonPointer.compile("/a~0b~1c/d");

    assertEquals("a~b/c", pointer.getMatchingProperty());
    assertNotNull(pointer.matchProperty("a~b/c"));
    assertEquals("d", pointer.tail().getMatchingProperty());
}

@Test
public void equalityAndHashCodeUsePointerString()
{
    JsonPointer pointer = JsonPointer.compile("/value");
    JsonPointer samePointer = JsonPointer.compile("/value");

    assertTrue(pointer.equals(pointer));
    assertTrue(pointer.equals(samePointer));
    assertEquals(pointer.hashCode(), samePointer.hashCode());
    assertFalse(pointer.equals(null));
    assertFalse(pointer.equals("value"));
}