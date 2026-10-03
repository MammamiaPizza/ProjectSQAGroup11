@Test
public void testDecorateRejectsNullAndSupportsAnEmptyList() {
    boolean rejected = false;
    try {
        SetUniqueList.decorate(null);
    } catch (IllegalArgumentException expected) {
        rejected = true;
    }
    assertTrue(rejected);

    SetUniqueList list = SetUniqueList.decorate(new java.util.ArrayList());
    assertTrue(list.isEmpty());
    assertTrue(list.add("one"));
    assertFalse(list.add("one"));
    assertEquals(java.util.Arrays.asList("one"), list);
}

@Test
public void testRemovalOperationsKeepTheBackingSetInSync() {
    SetUniqueList list = SetUniqueList.decorate(new java.util.ArrayList(
            java.util.Arrays.asList("one", "two", "three")));

    assertEquals("two", list.remove(1));
    assertFalse(list.contains("two"));
    assertTrue(list.add("two"));

    assertTrue(list.remove("one"));
    assertFalse(list.contains("one"));
    assertTrue(list.add("one"));

    assertTrue(list.removeAll(java.util.Arrays.asList("two", "missing")));
    assertFalse(list.contains("two"));

    assertTrue(list.retainAll(java.util.Arrays.asList("three")));
    assertEquals(java.util.Arrays.asList("three"), list);

    list.clear();
    assertTrue(list.isEmpty());
    assertTrue(list.add("one"));
}

@Test
public void testIteratorsMaintainUniquenessAfterMutations() {
    SetUniqueList list = SetUniqueList.decorate(new java.util.ArrayList(
            java.util.Arrays.asList("one", "two")));

    java.util.Iterator iterator = list.iterator();
    assertEquals("one", iterator.next());
    iterator.remove();
    assertFalse(list.contains("one"));
    assertTrue(list.add("one"));

    java.util.ListIterator listIterator = list.listIterator();
    assertEquals("two", listIterator.next());
    listIterator.set("three");
    assertFalse(list.contains("two"));
    assertTrue(list.add("two"));

    listIterator.add("four");
    assertEquals(java.util.Arrays.asList("three", "four", "one", "two"), list);

    java.util.ListIterator reverseIterator = list.listIterator(list.size());
    assertEquals("two", reverseIterator.previous());
}

@Test
public void testAsSetIsUnmodifiableAndSubListRemovalsAllowReaddingElement() {
    SetUniqueList list = SetUniqueList.decorate(new java.util.ArrayList(
            java.util.Arrays.asList("one", "two", "three")));

    assertTrue(list.contains("two"));
    assertTrue(list.containsAll(java.util.Arrays.asList("one", "three")));

    java.util.Set values = list.asSet();
    boolean unsupported = false;
    try {
        values.add("four");
    } catch (UnsupportedOperationException expected) {
        unsupported = true;
    }
    assertTrue(unsupported);

    java.util.List subList = list.subList(1, 3);
    assertEquals("two", subList.remove(0));
    assertFalse(list.contains("two"));
    assertTrue(list.add("two"));
    assertEquals(java.util.Arrays.asList("one", "three", "two"), list);
}