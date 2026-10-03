package org.apache.commons.lang3;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

public class ArrayUtilsLang571Test {

    @Test
    public void testAddToNullStringArrayCreatesStringArray() {
        String[] result = ArrayUtils.add(null, "x");

        assertEquals(String[].class, result.getClass());
        assertArrayEquals(new String[] { "x" }, result);
    }

    @Test
    public void testAddToExistingStringArrayPreservesTypeOrderAndOriginal() {
        String[] original = new String[] { "first", "second" };

        String[] result = ArrayUtils.add(original, "third");

        assertEquals(String[].class, result.getClass());
        assertArrayEquals(new String[] { "first", "second", "third" }, result);
        assertArrayEquals(new String[] { "first", "second" }, original);
        assertNotSame(original, result);
    }
}
