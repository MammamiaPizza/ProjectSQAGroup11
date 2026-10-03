@Test
public void testSetToExistingElementRemovesTheOtherOccurrence() {
    final SetUniqueList<String> list = SetUniqueList.setUniqueList(
            new java.util.ArrayList<String>(java.util.Arrays.asList("one", "two", "three")));

    org.junit.Assert.assertEquals("one", list.set(0, "three"));

    org.junit.Assert.assertEquals(java.util.Arrays.asList("three", "two"), list);
    org.junit.Assert.assertEquals(
            new java.util.HashSet<String>(java.util.Arrays.asList("three", "two")), list.asSet());
}

@Test
public void testListIteratorSetUpdatesSetMembership() {
    final SetUniqueList<String> list = SetUniqueList.setUniqueList(
            new java.util.ArrayList<String>(java.util.Arrays.asList("one", "two")));
    final java.util.ListIterator<String> iterator = list.listIterator();

    org.junit.Assert.assertEquals("one", iterator.next());
    iterator.set("three");

    org.junit.Assert.assertEquals(java.util.Arrays.asList("three", "two"), list);
    org.junit.Assert.assertEquals(
            new java.util.HashSet<String>(java.util.Arrays.asList("three", "two")), list.asSet());
}

@Test
public void testRemoveAllRemovesOnlyPresentElementsAndReportsChange() {
    final SetUniqueList<String> list = SetUniqueList.setUniqueList(
            new java.util.ArrayList<String>(java.util.Arrays.asList("one", "two", "three")));

    org.junit.Assert.assertTrue(list.removeAll(java.util.Arrays.asList("missing", "two", "three")));
    org.junit.Assert.assertEquals(java.util.Arrays.asList("one"), list);
    org.junit.Assert.assertEquals(
            new java.util.HashSet<String>(java.util.Arrays.asList("one")), list.asSet());
    org.junit.Assert.assertFalse(list.removeAll(java.util.Arrays.asList("missing", "also-missing")));
}