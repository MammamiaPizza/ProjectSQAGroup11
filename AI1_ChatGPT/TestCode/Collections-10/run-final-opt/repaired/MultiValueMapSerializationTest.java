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
}
