package org.apache.commons.collections.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MultiValueMapCollections228Test {

    @Test
    public void testPutWithListBackedMapReturnsAddedValueAndStoresIt() {
        MultiValueMap map = MultiValueMap.decorate(new HashMap(), ArrayList.class);

        assertEquals("a", map.put("key", "a"));
        assertTrue(map.containsValue("key", "a"));
        assertEquals(1, map.size("key"));
    }

    @Test
    public void testPutWithSetBackedMapReturnsAddedValueAndStoresIt() {
        MultiValueMap map = MultiValueMap.decorate(new HashMap(), HashSet.class);

        assertEquals("a", map.put("key", "a"));
        assertTrue(map.containsValue("key", "a"));
        assertEquals(1, map.size("key"));
    }

    @Test
    public void testPutAllForAbsentKeyReturnsTrueAndStoresAllValues() {
        MultiValueMap map = MultiValueMap.decorate(new HashMap(), ArrayList.class);

        assertTrue(map.putAll("key", Arrays.asList(new String[] { "a", "b" })));
        assertTrue(map.containsValue("key", "a"));
        assertTrue(map.containsValue("key", "b"));
        assertEquals(2, map.size("key"));
        assertEquals(2, map.totalSize());
    }

@org.junit.Test
    public void testDefaultConstructorContainsValuesAndClear() {
        org.apache.commons.collections.map.MultiValueMap map =
                new org.apache.commons.collections.map.MultiValueMap();

        org.junit.Assert.assertFalse(map.containsValue("missing"));
        map.put("first", "a");
        map.put("second", "b");

        org.junit.Assert.assertTrue(map.containsValue("a"));
        org.junit.Assert.assertTrue(map.containsValue("b"));
        org.junit.Assert.assertFalse(map.containsValue("missing"));

        map.clear();

        org.junit.Assert.assertEquals(0, map.totalSize());
        org.junit.Assert.assertFalse(map.containsValue("a"));
    }

    @org.junit.Test
    public void testPutAllForExistingKeyAndEmptyCollection() {
        org.apache.commons.collections.map.MultiValueMap map =
                new org.apache.commons.collections.map.MultiValueMap();
        map.put("key", "a");

        org.junit.Assert.assertTrue(map.putAll("key",
                java.util.Arrays.asList(new String[] { "b", "c" })));
        org.junit.Assert.assertEquals(3, map.size("key"));
        org.junit.Assert.assertEquals(3, map.totalSize());
        org.junit.Assert.assertTrue(map.containsValue("key", "a"));
        org.junit.Assert.assertTrue(map.containsValue("key", "b"));
        org.junit.Assert.assertTrue(map.containsValue("key", "c"));

        org.junit.Assert.assertFalse(map.putAll("key", java.util.Collections.EMPTY_LIST));
        org.junit.Assert.assertEquals(3, map.size("key"));
    }

    @org.junit.Test
    public void testIteratorForAbsentAndPresentKey() {
        org.apache.commons.collections.map.MultiValueMap map =
                new org.apache.commons.collections.map.MultiValueMap();

        org.junit.Assert.assertFalse(map.iterator("missing").hasNext());

        map.put("key", "a");
        java.util.Iterator iterator = map.iterator("key");
        org.junit.Assert.assertTrue(iterator.hasNext());
        org.junit.Assert.assertEquals("a", iterator.next());
        org.junit.Assert.assertFalse(iterator.hasNext());
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testDecorateRejectsNullCollectionFactory() {
        org.apache.commons.collections.map.MultiValueMap.decorate(
                new java.util.HashMap(),
                (org.apache.commons.collections.Factory) null);
    }
}
