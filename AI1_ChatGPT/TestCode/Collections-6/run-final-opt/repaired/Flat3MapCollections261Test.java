package org.apache.commons.collections.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.AbstractMap;
import java.util.Iterator;

import org.apache.commons.collections.MapIterator;
import org.junit.Test;

public class Flat3MapCollections261Test {

    @Test
    public void entrySetRemoveMustNotRemoveEntryWhenValueDoesNotMatch() {
        Flat3Map map = new Flat3Map();
        map.put("key", "actual");

        boolean removed = map.entrySet().remove(
                new AbstractMap.SimpleEntry("key", "different"));

        assertFalse(removed);
        assertEquals(1, map.size());
        assertEquals("actual", map.get("key"));
    }

    @Test
    public void entrySetRemoveRemovesMatchingEntry() {
        Flat3Map map = new Flat3Map();
        map.put("key", "value");

        boolean removed = map.entrySet().remove(
                new AbstractMap.SimpleEntry("key", "value"));

        assertTrue(removed);
        assertTrue(map.isEmpty());
        assertFalse(map.containsKey("key"));
    }

    @Test
    public void removeReturnsValueAssociatedWithFirstStoredKey() {
        Flat3Map map = new Flat3Map();
        map.put("first", "one");
        map.put("second", "two");
        map.put("third", "three");

        assertEquals("one", map.remove("first"));
        assertEquals(2, map.size());
        assertFalse(map.containsKey("first"));
        assertEquals("two", map.get("second"));
        assertEquals("three", map.get("third"));
    }

    @Test
    public void removeReturnsValueAssociatedWithMiddleStoredKey() {
        Flat3Map map = new Flat3Map();
        map.put("first", "one");
        map.put("second", "two");
        map.put("third", "three");

        assertEquals("two", map.remove("second"));
        assertEquals(2, map.size());
        assertEquals("one", map.get("first"));
        assertFalse(map.containsKey("second"));
        assertEquals("three", map.get("third"));
    }

    @Test
    public void removeNullKeyReturnsItsOwnValueAndPreservesOtherMappings() {
        Flat3Map map = new Flat3Map();
        map.put(null, "null-value");
        map.put("second", "two");
        map.put("third", "three");

        assertEquals("null-value", map.remove(null));
        assertFalse(map.containsKey(null));
        assertEquals(2, map.size());
        assertEquals("two", map.get("second"));
        assertEquals("three", map.get("third"));
    }

    @Test
    public void collectionViewsReflectRemovalsAndContainment() {
        Flat3Map map = new Flat3Map();
        map.put("a", "one");
        map.put("b", "two");
        map.put("c", "three");

        assertEquals(3, map.entrySet().size());
        assertEquals(3, map.keySet().size());
        assertEquals(3, map.values().size());
        assertTrue(map.entrySet().contains(new AbstractMap.SimpleEntry("b", "two")));
        assertTrue(map.keySet().contains("c"));
        assertTrue(map.values().contains("one"));

        assertTrue(map.keySet().remove("b"));
        assertTrue(map.values().remove("three"));

        assertEquals(1, map.size());
        assertTrue(map.containsKey("a"));
        assertFalse(map.containsKey("b"));
        assertFalse(map.containsKey("c"));
    }

    @Test
    public void valuesViewCanRemoveNullValue() {
        Flat3Map map = new Flat3Map();
        map.put(null, null);
        map.put("other", "value");

        assertTrue(map.values().remove(null));

        assertEquals(1, map.size());
        assertFalse(map.containsKey(null));
        assertEquals("value", map.get("other"));
    }

    @Test
    public void mapIteratorCanUpdateAndRemoveCurrentMapping() {
        Flat3Map map = new Flat3Map();
        map.put("first", "one");
        map.put("second", "two");
        map.put("third", "three");

        MapIterator iterator = map.mapIterator();
        assertEquals("first", iterator.next());
        assertEquals("one", iterator.setValue("ONE"));
        assertEquals("ONE", map.get("first"));

        iterator.remove();

        assertFalse(map.containsKey("first"));
        assertEquals(2, map.size());

        int remaining = 0;
        while (iterator.hasNext()) {
            iterator.next();
            remaining++;
        }
        assertEquals(2, remaining);
    }

    @Test
    public void mapIteratorRemoveBeforeNextIsInvalid() {
        Flat3Map map = new Flat3Map();
        map.put("key", "value");

        try {
            map.mapIterator().remove();
            fail("remove before next must throw IllegalStateException");
        } catch (IllegalStateException expected) {
            assertEquals(1, map.size());
        }
    }

    @Test
    public void fourthEntryUsesMapViewsAndClearReturnsToEmptyFlatMap() {
        Flat3Map map = new Flat3Map();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.put("four", 4);

        assertEquals(4, map.size());
        assertTrue(map.keySet().contains("four"));
        assertTrue(map.values().contains(4));

        Iterator iterator = map.entrySet().iterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        iterator.remove();

        assertEquals(3, map.size());

        map.clear();

        assertTrue(map.isEmpty());
        assertEquals(0, map.entrySet().size());
        assertFalse(map.keySet().iterator().hasNext());

        map.put("new", "value");
        assertEquals("value", map.get("new"));
        assertNull(map.remove("missing"));
    }
}
