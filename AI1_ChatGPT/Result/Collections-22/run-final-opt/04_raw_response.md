@Test
public void putReplacingAnExistingNullValueKeepsOnlyOneKeyInInsertionOrder() {
    final ListOrderedMap<String, String> map = new ListOrderedMap<String, String>();
    map.put("first", null);
    map.put("second", "two");

    org.junit.Assert.assertNull(map.put("first", "one"));

    org.junit.Assert.assertEquals(2, map.size());
    org.junit.Assert.assertEquals(2, map.keyList().size());
    org.junit.Assert.assertEquals("first", map.keyList().get(0));
    org.junit.Assert.assertEquals("second", map.keyList().get(1));
    org.junit.Assert.assertEquals("one", map.getValue(0));
    org.junit.Assert.assertEquals("two", map.getValue(1));
}

@Test
public void valueListRemovalOfANullValueRemovesTheCorrespondingEntry() {
    final ListOrderedMap<String, String> map = new ListOrderedMap<String, String>();
    map.put("first", "one");
    map.put("middle", null);
    map.put("last", "three");

    org.junit.Assert.assertNull(map.valueList().remove(1));

    org.junit.Assert.assertEquals(2, map.size());
    org.junit.Assert.assertFalse(map.containsKey("middle"));
    org.junit.Assert.assertEquals("first", map.get(0));
    org.junit.Assert.assertEquals("last", map.get(1));
    org.junit.Assert.assertEquals("one", map.getValue(0));
    org.junit.Assert.assertEquals("three", map.getValue(1));
}