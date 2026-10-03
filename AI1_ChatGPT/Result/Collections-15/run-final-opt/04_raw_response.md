@Test
public void testEmptyDecorateAddAndContainsAll() {
    SetUniqueList list = SetUniqueList.decorate(new java.util.ArrayList());

    assertTrue(list.add("one"));
    assertFalse(list.add("one"));
    assertTrue(list.containsAll(java.util.Arrays.asList(new String[] { "one" })));
    assertFalse(list.containsAll(java.util.Arrays.asList(new String[] { "two" })));
}

@Test
public void testRemoveAllAndRetainAllSynchronizeSet() {
    SetUniqueList list = SetUniqueList.decorate(new java.util.ArrayList(
            java.util.Arrays.asList(new String[] { "a", "b", "c" })));

    assertTrue(list.removeAll(java.util.Arrays.asList(new String[] { "a", "missing" })));
    assertEquals(java.util.Arrays.asList(new String[] { "b", "c" }), list);
    assertEquals(new java.util.HashSet(java.util.Arrays.asList(new String[] { "b", "c" })),
            list.asSet());
    assertFalse(list.removeAll(java.util.Arrays.asList(new String[] { "missing" })));

    assertTrue(list.retainAll(java.util.Arrays.asList(new String[] { "b", "missing" })));
    assertEquals(java.util.Arrays.asList(new String[] { "b" }), list);
    assertEquals(new java.util.HashSet(java.util.Arrays.asList(new String[] { "b" })),
            list.asSet());
}

@Test
public void testIteratorRemovalsKeepSetSynchronized() {
    SetUniqueList list = SetUniqueList.decorate(new java.util.ArrayList(
            java.util.Arrays.asList(new String[] { "a", "b", "c" })));

    java.util.Iterator iterator = list.iterator();
    assertEquals("a", iterator.next());
    iterator.remove();
    assertFalse(list.asSet().contains("a"));
    assertTrue(list.add("a"));

    java.util.ListIterator listIterator = list.listIterator(1);
    assertEquals("b", listIterator.previous());
    listIterator.remove();
    assertFalse(list.asSet().contains("b"));
    assertTrue(list.add("b"));
    assertEquals(new java.util.HashSet(java.util.Arrays.asList(new String[] { "a", "b", "c" })),
            list.asSet());
}

@Test
public void testSubListClearRetainsMembershipOfParentElements() {
    SetUniqueList list = SetUniqueList.decorate(new java.util.ArrayList(
            java.util.Arrays.asList(new String[] { "a", "b", "c" })));

    list.subList(0, 1).clear();

    assertEquals(java.util.Arrays.asList(new String[] { "b", "c" }), list);
    assertTrue(list.asSet().contains("b"));
    assertFalse(list.add("b"));
    assertEquals(2, list.size());
}