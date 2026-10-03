package org.apache.commons.collections.map;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MultiValueMapSerializationTest {

    @Test
    public void testEmptyMapSerializationRoundTripRemainsEmptyAndUsable() throws Exception {
        MultiValueMap original = new MultiValueMap();

        MultiValueMap restored = deserialize(serialize(original));

        assertTrue(restored.isEmpty());
        assertEquals(0, restored.totalSize());
        assertNull(restored.getCollection("missing"));

        assertEquals("value", restored.put("key", "value"));
        assertFalse(restored.isEmpty());
        assertEquals(1, restored.totalSize());
        assertEquals(1, restored.size("key"));
        assertTrue(restored.containsValue("key", "value"));
    }

    @Test
    public void testPopulatedMapSerializationRoundTripPreservesAllMappings() throws Exception {
        MultiValueMap original = new MultiValueMap();
        original.put("fruit", "apple");
        original.put("fruit", "banana");
        original.put("color", "blue");

        MultiValueMap restored = deserialize(serialize(original));

        assertEquals(3, restored.totalSize());
        assertEquals(2, restored.size());
        assertEquals(2, restored.keySet().size());
        assertEquals(2, restored.size("fruit"));
        assertEquals(1, restored.size("color"));
        assertEquals(Arrays.asList(new String[] { "apple", "banana" }),
                restored.getCollection("fruit"));
        assertEquals(Arrays.asList(new String[] { "blue" }),
                restored.getCollection("color"));
        assertTrue(restored.containsValue("apple"));
        assertTrue(restored.containsValue("fruit", "banana"));
        assertTrue(restored.containsValue("color", "blue"));
    }

    @Test
    public void testDeserializedMapCollectionsRemainMutableAndMaintainTotalSize() throws Exception {
        MultiValueMap original = new MultiValueMap();
        original.put("letters", "a");
        original.put("letters", "b");

        MultiValueMap restored = deserialize(serialize(original));
        Collection letters = restored.getCollection("letters");

        assertNotNull(letters);
        assertTrue(letters.remove("a"));
        assertEquals(1, restored.size("letters"));
        assertEquals(1, restored.totalSize());
        assertEquals("b", restored.removeMapping("letters", "b"));
        assertEquals(0, restored.totalSize());
        assertFalse(restored.containsKey("letters"));
    }

    private byte[] serialize(MultiValueMap map) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(map);
        output.close();
        return bytes.toByteArray();
    }

    private MultiValueMap deserialize(byte[] bytes) throws Exception {
        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes));
        MultiValueMap result = (MultiValueMap) input.readObject();
        input.close();
        return result;
    }

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
}
