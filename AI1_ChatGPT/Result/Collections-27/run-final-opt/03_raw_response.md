package org.apache.commons.collections4.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MultiValueMapSerializationSafetyTest {

    @Test
    public void testDefaultCollectionFactoryRoundTripsThroughSerialization() throws Exception {
        final MultiValueMap<String, String> source = new MultiValueMap<String, String>();
        source.put("letters", "a");
        source.put("letters", "b");
        source.put("numbers", "1");

        final MultiValueMap<String, String> copy = deserialize(serialize(source));

        assertEquals(2, copy.size("letters"));
        assertEquals(3, copy.totalSize());
        assertTrue(copy.containsValue("letters", "a"));
        assertTrue(copy.containsValue("letters", "b"));
        assertTrue(copy.containsValue("numbers", "1"));
        assertFalse(copy.containsValue("numbers", "2"));
    }

    @Test
    public void testCustomCollectionClassRoundTripsThroughSerialization() throws Exception {
        final Map<String, UnsafeCollection> backing =
                new HashMap<String, UnsafeCollection>();
        final MultiValueMap<String, String> source =
                MultiValueMap.<String, String, UnsafeCollection>multiValueMap(
                        backing, UnsafeCollection.class);
        source.put("key", "value");

        final MultiValueMap<String, String> copy = deserialize(serialize(source));

        assertTrue(copy.containsValue("key", "value"));
        copy.put("other", "value2");
        assertTrue(copy.containsValue("other", "value2"));
        assertEquals(2, copy.totalSize());
    }

    private static byte[] serialize(final Object value) throws Exception {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(value);
        output.close();
        return bytes.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private static MultiValueMap<String, String> deserialize(final byte[] bytes) throws Exception {
        final ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes));
        final MultiValueMap<String, String> result =
                (MultiValueMap<String, String>) input.readObject();
        input.close();
        return result;
    }

    public static class UnsafeCollection extends ArrayList<String> {
        private static final long serialVersionUID = 1L;
    }
}