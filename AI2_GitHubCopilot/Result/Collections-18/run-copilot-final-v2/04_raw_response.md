@Test
public void testConstructorFromSet() {
    java.util.Set<String> set = new java.util.LinkedHashSet<String>();
    set.add("A");
    set.add("B");
    set.add("C");
    ListOrderedSet<String> los = ListOrderedSet.listOrderedSet(set);
    assertEquals(3, los.size());
    assertEquals("A", los.get(0));
    assertEquals("B", los.get(1));
    assertEquals("C", los.get(2));
}

@Test
public void testConstructorFromSetAndList() {
    java.util.Set<String> set = new java.util.HashSet<String>();
    set.add("A");
    set.add("B");
    java.util.List<String> order = new java.util.ArrayList<String>();
    order.add("B");
    order.add("A");
    ListOrderedSet<String> los = ListOrderedSet.listOrderedSet(set, order);
    assertEquals(2, los.size());
    assertEquals("B", los.get(0));
    assertEquals("A", los.get(1));
    assertSame(order, los.asList());
}

@Test(expected = IllegalArgumentException.class)
public void testConstructorFromSetAndListNullList() {
    java.util.Set<String> set = new java.util.HashSet<String>();
    ListOrderedSet.listOrderedSet(set, null);
}

@Test
public void testAddAllAtIndex() {
    ListOrderedSet<String> los = new ListOrderedSet<String>();
    los.add("A");
    los.add("B");
    los.add("C");
    java.util.Collection<String> toAdd = java.util.Arrays.asList("D", "E");
    boolean changed = los.addAll(1, toAdd);
    assertTrue(changed);
    assertEquals(5, los.size());
    assertEquals("A", los.get(0));
    assertEquals("D", los.get(1));
    assertEquals("E", los.get(2));
    assertEquals("B", los.get(3));
    assertEquals("C", los.get(4));
    changed = los.addAll(2, java.util.Arrays.asList("A", "D"));
    assertFalse(changed);
    assertEquals(5, los.size());
}