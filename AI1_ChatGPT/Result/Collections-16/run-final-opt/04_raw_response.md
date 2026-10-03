@Test
public void rejectsNullListAndNullSetDuringConstruction() {
    boolean listRejected = false;
    try {
        SetUniqueList.decorate(null);
    } catch (IllegalArgumentException expected) {
        listRejected = true;
    }
    assertTrue(listRejected);

    boolean setRejected = false;
    try {
        new SetUniqueList(new java.util.ArrayList(), null);
    } catch (IllegalArgumentException expected) {
        setRejected = true;
    }
    assertTrue(setRejected);
}

@Test
public void addRejectsDuplicatesAndContainsUsesUniqueSet() {
    SetUniqueList list = SetUniqueList.decorate(new java.util.ArrayList());

    assertTrue(list.add("a"));
    assertFalse(list.add("a"));
    assertTrue(list.add("b"));

    assertEquals(java.util.Arrays.asList("a", "b"), list);
    assertTrue(list.contains("a"));
    assertFalse(list.contains("c"));
    assertTrue(list.containsAll(java.util.Arrays.asList("a", "b")));
    assertFalse(list.containsAll(java.util.Arrays.asList("a", "c")));
    assertEquals(new java.util.HashSet(java.util.Arrays.asList("a", "b")), list.asSet());
}

@Test
public void bulkRemovalsAndClearKeepSetViewInSync() {
    SetUniqueList list = SetUniqueList.decorate(
            new java.util.ArrayList(java.util.Arrays.asList("a", "b", "c")));

    assertFalse(list.remove("missing"));
    assertTrue(list.removeAll(java.util.Arrays.asList("a", "missing")));
    assertEquals(java.util.Arrays.asList("b", "c"), list);
    assertEquals(new java.util.HashSet(java.util.Arrays.asList("b", "c")), list.asSet());

    assertTrue(list.retainAll(java.util.Arrays.asList("b")));
    assertEquals(java.util.Arrays.asList("b"), list);
    assertEquals(new java.util.HashSet(java.util.Arrays.asList("b")), list.asSet());

    list.clear();
    assertTrue(list.isEmpty());
    assertTrue(list.asSet().isEmpty());
}

@Test
public void listIteratorPreviousRemovalKeepsSetViewInSync() {
    SetUniqueList list = SetUniqueList.decorate(
            new java.util.ArrayList(java.util.Arrays.asList("a", "b", "c")));

    java.util.ListIterator iterator = list.listIterator(list.size());
    assertEquals("c", iterator.previous());
    iterator.remove();

    assertEquals(java.util.Arrays.asList("a", "b"), list);
    assertEquals(new java.util.HashSet(java.util.Arrays.asList("a", "b")), list.asSet());
}