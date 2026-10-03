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
}