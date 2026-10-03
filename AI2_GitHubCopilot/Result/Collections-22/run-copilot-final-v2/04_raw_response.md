@org.junit.Test
public void testFirstKeyAndLastKeyOnEmptyMapThrowNoSuchElementException() {
    final org.apache.commons.collections4.map.ListOrderedMap<String, String> map =
            new org.apache.commons.collections4.map.ListOrderedMap<String, String>();
    boolean firstKeyThrew = false;
    try {
        map.firstKey();
    } catch (final java.util.NoSuchElementException expected) {
        firstKeyThrew = true;
    }
    boolean lastKeyThrew = false;
    try {
        map.lastKey();
    } catch (final java.util.NoSuchElementException expected) {
        lastKeyThrew = true;
    }
    org.junit.Assert.assertTrue("firstKey() should throw for an empty map", firstKeyThrew);
    org.junit.Assert.assertTrue("lastKey() should throw for an empty map", lastKeyThrew);
}

@org.junit.Test
public void testNextKeyAndPreviousKeyAtBoundariesAndMissingKey() {
    final org.apache.commons.collections4.map.ListOrderedMap<String, String> map =
            new org.apache.commons.collections4.map.ListOrderedMap<String, String>();
    map.put("a", "1");
    map.put("b", "2");
    map.put("c", "3");
    org.junit.Assert.assertEquals("b", map.nextKey("a"));
    org.junit.Assert.assertEquals("c", map.nextKey("b"));
    org.junit.Assert.assertNull(map.nextKey("c"));
    org.junit.Assert.assertNull(map.nextKey("missing"));
    org.junit.Assert.assertEquals("b", map.previousKey("c"));
    org.junit.Assert.assertEquals("a", map.previousKey("b"));
    org.junit.Assert.assertNull(map.previousKey("a"));
}

@org.junit.Test
public void testListOrderedMapFactoryPreservesMapIterationOrderAndClear() {
    final java.util.LinkedHashMap<String, String> base = new java.util.LinkedHashMap<String,
String>();
    base.put("a", "1");
    base.put("b", "2");
    base.put("c", "3");
    final org.apache.commons.collections4.map.ListOrderedMap<String, String> map =
            org.apache.commons.collections4.map.ListOrderedMap.listOrderedMap(base);
    org.junit.Assert.assertEquals("a", map.get(0));
    org.junit.Assert.assertEquals("b", map.get(1));
    org.junit.Assert.assertEquals("c", map.get(2));
    org.junit.Assert.assertEquals(3, map.keyList().size());
    map.clear();
    org.junit.Assert.assertTrue(map.keyList().isEmpty());
}

@org.junit.Test
public void testMapIteratorTraversal() {
    final org.apache.commons.collections4.map.ListOrderedMap<String, String> map =
            new org.apache.commons.collections4.map.ListOrderedMap<String, String>();
    map.put("a", "1");
    map.put("b", "2");
    map.put("c", "3");
    final org.apache.commons.collections4.OrderedMapIterator<String, String> it = map.mapIterator();
    org.junit.Assert.assertTrue(it.hasNext());
    org.junit.Assert.assertEquals("a", it.next());
    org.junit.Assert.assertEquals("a", it.getKey());
    org.junit.Assert.assertEquals("b", it.next());
    org.junit.Assert.assertEquals("c", it.next());
    org.junit.Assert.assertFalse(it.hasNext());
}