@Test
public void fourArgumentConstructorExposesAllKeysInOrder() {
    final MultiKey<Integer> multiKey = new MultiKey<Integer>(1, 2, 3, 4);

    assertEquals(4, multiKey.size());
    assertEquals(Integer.valueOf(1), multiKey.getKey(0));
    assertEquals(Integer.valueOf(2), multiKey.getKey(1));
    assertEquals(Integer.valueOf(3), multiKey.getKey(2));
    assertEquals(Integer.valueOf(4), multiKey.getKey(3));
}

@Test
public void equalsReturnsTrueForSameInstance() {
    final MultiKey<Integer> multiKey = new MultiKey<Integer>(1, 2);

    assertTrue(multiKey.equals(multiKey));
}

@Test
public void toStringIncludesKeys() {
    assertEquals("MultiKey[one, 2]", new MultiKey<Object>("one", Integer.valueOf(2)).toString());
}