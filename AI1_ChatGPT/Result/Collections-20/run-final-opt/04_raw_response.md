@Test
public void collectionConstructorPreservesOrderAndIteratorStartsAtFirstElement() {
    final TreeList<String> list = new TreeList<String>(java.util.Arrays.asList("A", "B", "C"));

    assertEquals(3, list.size());
    assertArrayEquals(new Object[] { "A", "B", "C" }, list.toArray());

    final java.util.Iterator<String> iterator = list.iterator();
    assertEquals("A", iterator.next());
}

@Test
public void setReturnsPreviousValueAndReplacesIndexedElement() {
    final TreeList<String> list = new TreeList<String>();
    list.add("A");
    list.add("B");
    list.add("C");

    assertEquals("B", list.set(1, "X"));
    assertArrayEquals(new Object[] { "A", "X", "C" }, list.toArray());
}

@Test
public void clearEmptiesListAndAllowsReuse() {
    final TreeList<String> list = new TreeList<String>();
    list.add("A");
    list.add("B");

    list.clear();

    assertEquals(0, list.size());
    assertEquals(-1, list.indexOf("A"));
    assertEquals(Boolean.FALSE, Boolean.valueOf(list.contains("A")));

    list.add("C");
    assertEquals("C", list.get(0));
}