@org.junit.Test
public void testDecorateUsesSuppliedBackingMapAndCollectionClass() {
    java.util.Map backing = new java.util.HashMap();
    org.apache.commons.collections.map.MultiValueMap map =
            org.apache.commons.collections.map.MultiValueMap.decorate(backing, java.util.LinkedList.class);

    map.put("key", "value");

    org.junit.Assert.assertTrue(backing.containsKey("key"));
    org.junit.Assert.assertTrue(map.getCollection("key") instanceof java.util.LinkedList);
    org.junit.Assert.assertTrue(map.getCollection("key").contains("value"));
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void testDecorateRejectsNullCollectionFactory() {
    org.apache.commons.collections.map.MultiValueMap.decorate(
            new java.util.HashMap(), (org.apache.commons.collections.Factory) null);
}

@org.junit.Test
public void testPutAllCollectionHandlesEmptyNewAndExistingMappings() {
    org.apache.commons.collections.map.MultiValueMap map =
            new org.apache.commons.collections.map.MultiValueMap();

    org.junit.Assert.assertFalse(map.putAll("letters", null));
    org.junit.Assert.assertFalse(map.putAll("letters", java.util.Collections.EMPTY_LIST));
    org.junit.Assert.assertFalse(map.containsKey("letters"));

    org.junit.Assert.assertTrue(map.putAll("letters",
            java.util.Arrays.asList(new Object[] { "a", "b" })));
    org.junit.Assert.assertEquals(2, map.size("letters"));
    org.junit.Assert.assertEquals(2, map.totalSize());

    org.junit.Assert.assertTrue(map.putAll("letters",
            java.util.Collections.singletonList("c")));
    org.junit.Assert.assertEquals(3, map.size("letters"));
    org.junit.Assert.assertEquals(3, map.totalSize());
}

@org.junit.Test
public void testIteratorForMissingAndPresentKeysAndClear() {
    org.apache.commons.collections.map.MultiValueMap map =
            new org.apache.commons.collections.map.MultiValueMap();

    java.util.Iterator missing = map.iterator("missing");
    org.junit.Assert.assertFalse(missing.hasNext());
    org.junit.Assert.assertFalse(map.containsValue("missing", "value"));

    map.put("key", "value");
    java.util.Iterator iterator = map.iterator("key");
    org.junit.Assert.assertTrue(iterator.hasNext());
    org.junit.Assert.assertEquals("value", iterator.next());
    org.junit.Assert.assertTrue(map.containsValue("key", "value"));

    map.clear();
    org.junit.Assert.assertTrue(map.isEmpty());
    org.junit.Assert.assertEquals(0, map.totalSize());
}