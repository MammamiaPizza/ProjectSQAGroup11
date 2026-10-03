package org.apache.commons.collections4.keyvalue;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

public class MultiKeyGeneratedTest {

    private static final class DerivedMultiKey extends MultiKey<Integer> {
        private static final long serialVersionUID = 1L;

        private final int marker;

        private DerivedMultiKey(final int marker, final Integer key1,
                final Integer key2, final Integer key3) {
            super(key1, key2, key3);
            this.marker = marker;
        }

        private int getMarker() {
            return marker;
        }
    }

    @Test
    public void derivedMultiKeyRetainsHashAndKeysAfterSerialization() throws Exception {
        final DerivedMultiKey original = new DerivedMultiKey(3, 1, 2, 4);

        final DerivedMultiKey restored = roundTrip(original);

        assertEquals(3, restored.getMarker());
        assertEquals(3, restored.size());
        assertEquals(Integer.valueOf(1), restored.getKey(0));
        assertEquals(Integer.valueOf(2), restored.getKey(1));
        assertEquals(Integer.valueOf(4), restored.getKey(2));
        assertEquals(original, restored);
        assertEquals(restored, original);
        assertEquals(original.hashCode(), restored.hashCode());
    }

    @Test
    public void baseMultiKeyRecalculatesHashAfterSerialization() throws Exception {
        final MultiKey<Integer> original = new MultiKey<Integer>(1, 2, 4);

        final MultiKey<Integer> restored = roundTrip(original);

        assertEquals(original, restored);
        assertEquals(original.hashCode(), restored.hashCode());
    }

    @Test
    public void equalKeysHaveEqualHashCodesAndDifferentKeysAreNotEqual() {
        final MultiKey<String> first = new MultiKey<String>("one", null, "three");
        final MultiKey<String> same = new MultiKey<String>("one", null, "three");
        final MultiKey<String> different = new MultiKey<String>("one", "two", "three");

        assertEquals(first, same);
        assertEquals(first.hashCode(), same.hashCode());
        assertFalse(first.equals(different));
        assertFalse(first.equals("not a MultiKey"));
    }

    @Test
    public void arrayConstructorClonesInputAndGetKeysReturnsClone() {
        final String[] source = new String[] { "first", "second" };
        final MultiKey<String> key = new MultiKey<String>(source);

        source[0] = "changed";
        assertEquals("first", key.getKey(0));

        final String[] returned = key.getKeys();
        assertNotSame(returned, key.getKeys());
        assertArrayEquals(new String[] { "first", "second" }, returned);

        returned[1] = "changed-again";
        assertEquals("second", key.getKey(1));
    }

    @Test
    public void fixedArityConstructorsExposeAllKeysInOrder() {
        final MultiKey<String> twoKeys = new MultiKey<String>("a", "b");
        final MultiKey<String> fiveKeys = new MultiKey<String>("a", "b", "c", "d", "e");

        assertEquals(2, twoKeys.size());
        assertArrayEquals(new String[] { "a", "b" }, twoKeys.getKeys());
        assertEquals(5, fiveKeys.size());
        assertArrayEquals(new String[] { "a", "b", "c", "d", "e" }, fiveKeys.getKeys());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullKeyArrayIsRejected() {
        new MultiKey<String>((String[]) null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getKeyRejectsNegativeIndex() {
        new MultiKey<String>("a", "b").getKey(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getKeyRejectsIndexEqualToSize() {
        new MultiKey<String>("a", "b").getKey(2);
    }

    @SuppressWarnings("unchecked")
    private static <T> T roundTrip(final T value) throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(value);
        }

        try (ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()))) {
            return (T) input.readObject();
        }
    }
}
