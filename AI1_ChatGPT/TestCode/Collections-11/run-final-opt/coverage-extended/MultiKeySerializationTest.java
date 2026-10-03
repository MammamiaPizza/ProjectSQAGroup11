package org.apache.commons.collections.keyvalue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MultiKeySerializationTest {

    @Test
    public void serializedMultiKeyRetainsHashCodeAndEquality() throws Exception {
        MultiKey original = new MultiKey(Integer.valueOf(1), Integer.valueOf(2));
        MultiKey restored = serializeAndDeserialize(original);

        assertEquals(original, restored);
        assertEquals(original.hashCode(), restored.hashCode());
    }

    @Test
    public void deserializedMultiKeyCanRetrieveValueStoredWithOriginalKey() throws Exception {
        MultiKey original = new MultiKey(Integer.valueOf(1), Integer.valueOf(2));
        Map values = new HashMap();
        values.put(original, Integer.valueOf(2));

        MultiKey restored = serializeAndDeserialize(original);

        assertEquals(Integer.valueOf(2), values.get(restored));
    }

    @Test
    public void fixedArityConstructorsExposeAllKeysInOrder() {
        MultiKey two = new MultiKey("a", "b");
        MultiKey three = new MultiKey("a", "b", "c");
        MultiKey four = new MultiKey("a", "b", "c", "d");
        MultiKey five = new MultiKey("a", "b", "c", "d", "e");

        assertEquals(2, two.size());
        assertEquals("b", two.getKey(1));
        assertEquals(3, three.size());
        assertEquals("c", three.getKey(2));
        assertEquals(4, four.size());
        assertEquals("d", four.getKey(3));
        assertEquals(5, five.size());
        assertEquals("e", five.getKey(4));
    }

    @Test
    public void arrayConstructorClonesInputAndGetKeysReturnsClone() {
        Object[] input = new Object[] {"first", "second"};
        MultiKey key = new MultiKey(input);

        input[0] = "changed";
        Object[] returnedKeys = key.getKeys();
        returnedKeys[1] = "changed again";

        assertEquals("first", key.getKey(0));
        assertEquals("second", key.getKey(1));
        assertNotSame(returnedKeys, key.getKeys());
    }

    @Test
    public void equalsRequiresSameOrderedKeysAndHashCodesMatchForEqualKeys() {
        MultiKey first = new MultiKey("one", null, "three");
        MultiKey equal = new MultiKey("one", null, "three");
        MultiKey differentOrder = new MultiKey(null, "one", "three");
        MultiKey differentSize = new MultiKey("one", null);

        assertTrue(first.equals(equal));
        assertEquals(first.hashCode(), equal.hashCode());
        assertFalse(first.equals(differentOrder));
        assertFalse(first.equals(differentSize));
        assertFalse(first.equals("not a MultiKey"));
        assertFalse(first.equals(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void arrayConstructorRejectsNullArray() {
        new MultiKey((Object[]) null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getKeyRejectsIndexBeyondLastKey() {
        new MultiKey("first", "second").getKey(2);
    }

    @Test
    public void emptyArrayCreatesEmptyKey() {
        MultiKey key = new MultiKey(new Object[0]);

        assertEquals(0, key.size());
        assertEquals(0, key.hashCode());
        assertEquals(new MultiKey(new Object[0]), key);
        assertNull(key.getKeys().length == 0 ? null : key.getKeys()[0]);
    }

    private static MultiKey serializeAndDeserialize(MultiKey key) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(key);
        output.close();

        ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()));
        MultiKey restored = (MultiKey) input.readObject();
        input.close();
        return restored;
    }

@org.junit.Test
public void equalsReturnsTrueForSameInstance() {
    org.apache.commons.collections.keyvalue.MultiKey key =
            new org.apache.commons.collections.keyvalue.MultiKey("a", "b");

    org.junit.Assert.assertTrue(key.equals(key));
}

@org.junit.Test
public void toStringListsKeysInOrder() {
    org.apache.commons.collections.keyvalue.MultiKey key =
            new org.apache.commons.collections.keyvalue.MultiKey("a", null, "c");

    org.junit.Assert.assertEquals("MultiKey[a, null, c]", key.toString());
}
}
