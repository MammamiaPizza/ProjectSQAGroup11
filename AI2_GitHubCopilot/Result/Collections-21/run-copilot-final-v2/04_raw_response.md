@org.junit.Test(expected = IllegalArgumentException.class)
public void testConstructorRejectsNullSet() {
    new SetUniqueList<String>(new java.util.ArrayList<String>(), null);
}

@org.junit.Test
public void testAddAndRemoveKeepListAndSetConsistent() {
    final SetUniqueList<String> list = SetUniqueList.setUniqueList(new
java.util.ArrayList<String>());
    org.junit.Assert.assertTrue(list.add("a"));
    org.junit.Assert.assertFalse(list.add("a"));
    list.add(0, "b");
    list.add(0, "a");
    org.junit.Assert.assertEquals(java.util.Arrays.asList("b", "a"), list);
    org.junit.Assert.assertEquals("b", list.remove(0));
    org.junit.Assert.assertFalse(list.contains("b"));
    org.junit.Assert.assertEquals(java.util.Arrays.asList("a"), list);
    org.junit.Assert.assertFalse(list.remove("b"));
}

@org.junit.Test
public void testAsSetContainsAndClearUseBackingSet() {
    final SetUniqueList<String> list = SetUniqueList.setUniqueList(new
java.util.ArrayList<String>(java.util.Arrays.asList("a", "b")));
    org.junit.Assert.assertTrue(list.contains("a"));
    org.junit.Assert.assertFalse(list.contains("c"));
    org.junit.Assert.assertTrue(list.containsAll(java.util.Arrays.asList("a", "b")));
    org.junit.Assert.assertFalse(list.containsAll(java.util.Arrays.asList("a", "c")));
    final java.util.Set<String> view = list.asSet();
    org.junit.Assert.assertTrue(view.containsAll(java.util.Arrays.asList("a", "b")));
    list.clear();
    org.junit.Assert.assertTrue(list.isEmpty());
    org.junit.Assert.assertTrue(view.isEmpty());
    org.junit.Assert.assertFalse(list.contains("a"));
}

@org.junit.Test
public void testSubListWithNonHashSetBackingSetAndIteratorIndex() {
    final java.util.Set<String> backing = java.util.Collections.unmodifiableSet(new
java.util.HashSet<String>(java.util.Arrays.asList("a", "b")));
    final SetUniqueList<String> list = new SetUniqueList<String>(new
java.util.ArrayList<String>(java.util.Arrays.asList("a", "b")), backing);
    org.junit.Assert.assertEquals(java.util.Arrays.asList("a"), list.subList(0, 1));
    org.junit.Assert.assertEquals(java.util.Arrays.asList("b"), list.subList(1, 2));
    final java.util.ListIterator<String> li = list.listIterator(1);
    org.junit.Assert.assertEquals("b", li.next());
    org.junit.Assert.assertEquals("a", li.previous());
    final java.util.Iterator<String> it = list.iterator();
    org.junit.Assert.assertTrue(it.hasNext());
    org.junit.Assert.assertEquals("a", it.next());
    org.junit.Assert.assertEquals("b", it.next());
    org.junit.Assert.assertFalse(it.hasNext());
}