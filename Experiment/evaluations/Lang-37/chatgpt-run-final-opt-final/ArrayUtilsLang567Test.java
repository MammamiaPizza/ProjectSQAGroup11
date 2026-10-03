package org.apache.commons.lang3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class ArrayUtilsLang567Test {

    @Test
    public void testAddAtStartPreservesDeclaredNumberArrayType() {
        Number[] original = new Number[] { Integer.valueOf(1), Double.valueOf(2.0) };

        Number[] result = ArrayUtils.add(original, 0, Long.valueOf(3L));

        assertEquals(Number[].class, result.getClass());
        assertEquals(3, result.length);
        assertEquals(Long.valueOf(3L), result[0]);
        assertEquals(Integer.valueOf(1), result[1]);
        assertEquals(Double.valueOf(2.0), result[2]);
        assertEquals(Integer.valueOf(1), original[0]);
    }

    @Test
    public void testAddInMiddlePreservesDeclaredNumberArrayType() {
        Number[] original = new Number[] { Integer.valueOf(1), Double.valueOf(2.0) };

        Number[] result = ArrayUtils.add(original, 1, Long.valueOf(3L));

        assertEquals(Number[].class, result.getClass());
        assertEquals(3, result.length);
        assertEquals(Integer.valueOf(1), result[0]);
        assertEquals(Long.valueOf(3L), result[1]);
        assertEquals(Double.valueOf(2.0), result[2]);
    }

    @Test
    public void testAddAtEndPreservesDeclaredNumberArrayType() {
        Number[] original = new Number[] { Integer.valueOf(1), Double.valueOf(2.0) };

        Number[] result = ArrayUtils.add(original, 2, Long.valueOf(3L));

        assertEquals(Number[].class, result.getClass());
        assertEquals(3, result.length);
        assertEquals(Integer.valueOf(1), result[0]);
        assertEquals(Double.valueOf(2.0), result[1]);
        assertEquals(Long.valueOf(3L), result[2]);
    }

    @Test
    public void testAddToEmptyTypedArrayPreservesArrayType() {
        Number[] result = ArrayUtils.add(new Number[0], 0, Long.valueOf(7L));

        assertEquals(Number[].class, result.getClass());
        assertEquals(1, result.length);
        assertEquals(Long.valueOf(7L), result[0]);
    }

    @Test
    public void testAddToNullTypedArrayCreatesElementTypeArray() {
        String[] result = ArrayUtils.add((String[]) null, 0, "value");

        assertEquals(String[].class, result.getClass());
        assertEquals(1, result.length);
        assertEquals("value", result[0]);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testAddRejectsNegativeIndex() {
        ArrayUtils.add(new Number[] { Integer.valueOf(1) }, -1, Long.valueOf(2L));
    }

@org.junit.Test(expected = IndexOutOfBoundsException.class)
public void testAddRejectsIndexPastEnd() {
    ArrayUtils.add(new Number[] { Integer.valueOf(1) }, 2, Long.valueOf(2L));
}
}
