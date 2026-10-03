@Test
public void testAddAllAddsOnlyNewElementsAndReportsWhetherChanged() {
    final ListOrderedSet<String> set = new ListOrderedSet<String>();

    assertTrue(set.addAll(java.util.Arrays.asList("alpha", "beta", "alpha")));
    assertEquals(java.util.Arrays.asList("alpha", "beta"), set.asList());

    assertFalse(set.addAll(java.util.Arrays.asList("beta", "alpha")));
    assertEquals(2, set.size());
    assertEquals(java.util.Arrays.asList("alpha", "beta"), set.asList());
}

@Test
public void testAddAllAtIndexInsertsOnlyNewElementsInEncounterOrder() {
    final ListOrderedSet<String> set = new ListOrderedSet<String>();
    set.add("one");
    set.add("four");

    assertTrue(set.addAll(1, java.util.Arrays.asList("two", "one", "three", "two")));
    assertEquals(java.util.Arrays.asList("one", "two", "three", "four"), set.asList());

    assertFalse(set.addAll(2, java.util.Arrays.asList("one", "three")));
    assertEquals(java.util.Arrays.asList("one", "two", "three", "four"), set.asList());
}

@Test
public void testOrderedIteratorPreviousAndRemoveKeepOrderAndSetInSync() {
    final ListOrderedSet<String> set = new ListOrderedSet<String>();
    set.addAll(java.util.Arrays.asList("alpha", "beta", "gamma"));

    final org.apache.commons.collections.OrderedIterator<String> iterator = set.iterator();
    assertFalse(iterator.hasPrevious());
    assertEquals("alpha", iterator.next());
    assertTrue(iterator.hasPrevious());
    assertEquals("beta", iterator.next());
    assertEquals("beta", iterator.previous());

    iterator.remove();

    assertEquals(java.util.Arrays.asList("alpha", "gamma"), set.asList());
    assertFalse(set.contains("beta"));
    assertEquals(2, set.size());
}

@Test
public void testListOrderedSetFactoryWithSetCopiesInitialOrderAndUsesBackingSet() {
    final java.util.Set<String> backingSet = new java.util.LinkedHashSet<String>();
    backingSet.add("first");
    backingSet.add("second");

    final ListOrderedSet<String> set = ListOrderedSet.listOrderedSet(backingSet);

    assertEquals(java.util.Arrays.asList("first", "second"), set.asList());
    assertTrue(set.add("third"));
    assertTrue(backingSet.contains("third"));
    assertEquals(java.util.Arrays.asList("first", "second", "third"), set.asList());
}