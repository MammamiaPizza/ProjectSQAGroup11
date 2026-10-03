package org.apache.commons.collections4.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class ListOrderedMapNullValuesTest {

    @Test
    public void valuesIteratorTraversesAllNullValuesInInsertionOrder() {
        final ListOrderedMap<String, String> map = new ListOrderedMap<String, String>();
        map.put("first", null);
        map.put("second", null);
        map.put("third", null);

        final Iterator<String> iterator = map.values().iterator();

        assertNull(iterator.next());
        assertNull(iterator.next());
        assertNull(iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(3, map.values().size());
    }

    @Test
    public void valueListGetAndValuesIteratorPreserveMixedNullValueOrder() {
        final ListOrderedMap<String, String> map = new ListOrderedMap<String, String>();
        map.put("first", null);
        map.put("second", "two");
        map.put("third", null);
        map.put("fourth", "four");

        final List<String> valueList = map.valueList();

        assertNull(valueList.get(0));
        assertEquals("two", valueList.get(1));
        assertNull(valueList.get(2));
        assertEquals("four", valueList.get(3));
        assertEquals(Arrays.asList((String) null, "two", null, "four"),
                new ArrayList<String>(map.values()));
    }

    @Test
    public void valuesViewRemainsSynchronizedAfterRemovingEntryBetweenNullValues() {
        final ListOrderedMap<String, String> map = new ListOrderedMap<String, String>();
        map.put("first", null);
        map.put("middle", "value");
        map.put("last", null);

        assertEquals("value", map.remove("middle"));

        assertEquals(Arrays.asList((String) null, null), new ArrayList<String>(map.values()));
        assertEquals(Arrays.asList((String) null, null), map.valueList());
        assertEquals(2, map.size());
    }

    @Test
    public void valueListSetUpdatesNullValueWithoutChangingOrder() {
        final ListOrderedMap<String, String> map = new ListOrderedMap<String, String>();
        map.put("first", "one");
        map.put("second", "two");
        map.put("third", "three");

        assertEquals("two", map.valueList().set(1, null));

        assertNull(map.get("second"));
        assertEquals(Arrays.asList("one", null, "three"),
                new ArrayList<String>(map.values()));
        assertEquals(Arrays.asList("first", "second", "third"), map.keyList());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void valueListRejectsIndexEqualToSize() {
        final ListOrderedMap<String, String> map = new ListOrderedMap<String, String>();
        map.put("only", null);

        map.valueList().get(1);
    }
}
