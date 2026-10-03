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

@org.junit.Test
public void testAddAtIndexToNullStringArrayCreatesStringArray() {
    String[] result = org.apache.commons.lang3.ArrayUtils.add((String[]) null, 0, "value");

    org.junit.Assert.assertEquals(String[].class, result.getClass());
    org.junit.Assert.assertArrayEquals(new String[] { "value" }, result);
}

@org.junit.Test(expected = IndexOutOfBoundsException.class)
public void testAddAtInvalidIndexToNullStringArrayThrowsException() {
    org.apache.commons.lang3.ArrayUtils.add((String[]) null, 1, "value");
}

@org.junit.Test
public void testAddByteElementsAppendsAndInsertsInOrder() {
    byte[] appended = org.apache.commons.lang3.ArrayUtils.add((byte[]) null, (byte) 4);
    byte[] inserted = org.apache.commons.lang3.ArrayUtils.add(new byte[] { 1, 3 }, 1, (byte) 2);

    org.junit.Assert.assertArrayEquals(new byte[] { 4 }, appended);
    org.junit.Assert.assertArrayEquals(new byte[] { 1, 2, 3 }, inserted);
}

@org.junit.Test
public void testAddCharToNullArrayCreatesSingleElementArray() {
    char[] result = org.apache.commons.lang3.ArrayUtils.add((char[]) null, 'x');

    org.junit.Assert.assertArrayEquals(new char[] { 'x' }, result);
}
}
