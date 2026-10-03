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
}
