package org.apache.commons.collections.map;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.MapIterator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Flat3MapSetValueRegressionTest {

    @Test
    public void mapIteratorSetValueOnSecondFlatEntryReturnsOldValueAndDoesNotChangeOtherEntries() {
        Flat3Map map = new Flat3Map();
        map.put("first", Integer.valueOf(1));
        map.put("target", Integer.valueOf(10));
        map.put("third", Integer.valueOf(3));

        MapIterator iterator = mapIteratorAtKey(map, "target");

        assertEquals(Integer.valueOf(10), iterator.setValue("NewValue"));
        assertEquals("NewValue", iterator.getValue());
        assertEquals("NewValue", map.get("target"));
        assertEquals(Integer.valueOf(1), map.get("first"));
        assertEquals(Integer.valueOf(3), map.get("third"));
    }

    @Test
    public void mapIteratorSetValueOnThirdFlatEntryReturnsOldValueAndDoesNotChangeOtherEntries() {
        Flat3Map map = new Flat3Map();
        map.put("first", Integer.valueOf(1));
        map.put("second", Integer.valueOf(2));
        map.put("target", Integer.valueOf(10));

        MapIterator iterator = mapIteratorAtKey(map, "target");

        assertEquals(Integer.valueOf(10), iterator.setValue("NewValue"));
        assertEquals("NewValue", iterator.getValue());
        assertEquals("NewValue", map.get("target"));
        assertEquals(Integer.valueOf(1), map.get("first"));
        assertEquals(Integer.valueOf(2), map.get("second"));
    }

    @Test
    public void entrySetIteratorSetValueOnSecondFlatEntryReturnsOldValueAndDoesNotChangeOtherEntries() {
        Flat3Map map = new Flat3Map();
        map.put("first", Integer.valueOf(1));
        map.put("target", Integer.valueOf(10));
        map.put("third", Integer.valueOf(3));

        Map.Entry entry = entryAtKey(map, "target");

        assertEquals(Integer.valueOf(10), entry.setValue("NewValue"));
        assertEquals("NewValue", entry.getValue());
        assertEquals("NewValue", map.get("target"));
        assertEquals(Integer.valueOf(1), map.get("first"));
        assertEquals(Integer.valueOf(3), map.get("third"));
    }

    @Test
    public void entrySetIteratorSetValueOnThirdFlatEntryReturnsOldValueAndDoesNotChangeOtherEntries() {
        Flat3Map map = new Flat3Map();
        map.put("first", Integer.valueOf(1));
        map.put("second", Integer.valueOf(2));
        map.put("target", Integer.valueOf(10));

        Map.Entry entry = entryAtKey(map, "target");

        assertEquals(Integer.valueOf(10), entry.setValue("NewValue"));
        assertEquals("NewValue", entry.getValue());
        assertEquals("NewValue", map.get("target"));
        assertEquals(Integer.valueOf(1), map.get("first"));
        assertEquals(Integer.valueOf(2), map.get("second"));
    }

    @Test(expected = IllegalStateException.class)
    public void mapIteratorSetValueBeforeNextIsIllegal() {
        Flat3Map map = new Flat3Map();
        map.put("key", Integer.valueOf(10));

        map.mapIterator().setValue("NewValue");
    }

    private MapIterator mapIteratorAtKey(Flat3Map map, Object key) {
        MapIterator iterator = map.mapIterator();
        while (iterator.hasNext()) {
            Object currentKey = iterator.next();
            if (key.equals(currentKey)) {
                return iterator;
            }
        }
        fail("Expected key was not found: " + key);
        return null;
    }

    private Map.Entry entryAtKey(Flat3Map map, Object key) {
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (key.equals(entry.getKey())) {
                return entry;
            }
        }
        fail("Expected key was not found: " + key);
        return null;
    }

@org.junit.Test
public void constructorCopiesEntriesWhenSourceRequiresDelegateMap() {
    java.util.Map source = new java.util.HashMap();
    source.put("one", Integer.valueOf(1));
    source.put("two", Integer.valueOf(2));
    source.put("three", Integer.valueOf(3));
    source.put("four", Integer.valueOf(4));

    org.apache.commons.collections.map.Flat3Map map =
            new org.apache.commons.collections.map.Flat3Map(source);

    org.junit.Assert.assertEquals(Integer.valueOf(4), Integer.valueOf(map.size()));
    org.junit.Assert.assertEquals(Integer.valueOf(1), map.get("one"));
    org.junit.Assert.assertEquals(Integer.valueOf(4), map.get("four"));
    org.junit.Assert.assertEquals(Boolean.TRUE, Boolean.valueOf(map.containsKey("three")));

    source.put("one", Integer.valueOf(99));
    org.junit.Assert.assertEquals(Integer.valueOf(1), map.get("one"));
}

@org.junit.Test
public void clearRemovesEntriesInFlatAndDelegateModes() {
    org.apache.commons.collections.map.Flat3Map map =
            new org.apache.commons.collections.map.Flat3Map();
    map.put(null, "null");
    map.put("one", Integer.valueOf(1));
    map.put("two", Integer.valueOf(2));

    org.junit.Assert.assertEquals(Boolean.TRUE, Boolean.valueOf(map.containsKey(null)));
    map.clear();

    org.junit.Assert.assertEquals(Integer.valueOf(0), Integer.valueOf(map.size()));
    org.junit.Assert.assertEquals(Boolean.FALSE, Boolean.valueOf(map.containsKey(null)));

    map.put("one", Integer.valueOf(1));
    map.put("two", Integer.valueOf(2));
    map.put("three", Integer.valueOf(3));
    map.put("four", Integer.valueOf(4));
    map.clear();

    org.junit.Assert.assertEquals(Integer.valueOf(0), Integer.valueOf(map.size()));
    org.junit.Assert.assertEquals(null, map.get("one"));
    org.junit.Assert.assertEquals(Boolean.FALSE, Boolean.valueOf(map.containsKey("four")));
}

@org.junit.Test
public void cloneOfDelegateMapCanBeModifiedIndependently() {
    org.apache.commons.collections.map.Flat3Map original =
            new org.apache.commons.collections.map.Flat3Map();
    original.put("one", Integer.valueOf(1));
    original.put("two", Integer.valueOf(2));
    original.put("three", Integer.valueOf(3));
    original.put("four", Integer.valueOf(4));

    org.apache.commons.collections.map.Flat3Map copy =
            (org.apache.commons.collections.map.Flat3Map) original.clone();
    copy.put("copy-only", "value");

    org.junit.Assert.assertEquals(Integer.valueOf(4), Integer.valueOf(original.size()));
    org.junit.Assert.assertEquals(Integer.valueOf(5), Integer.valueOf(copy.size()));
    org.junit.Assert.assertEquals(null, original.get("copy-only"));
    org.junit.Assert.assertEquals("value", copy.get("copy-only"));
}
}
