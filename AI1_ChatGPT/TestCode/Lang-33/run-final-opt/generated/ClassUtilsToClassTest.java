package org.apache.commons.lang3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ClassUtilsToClassTest {

    @Test
    public void testToClassPreservesTypesOrderAndNullElements() {
        Object[] values = new Object[] { new Object(), null, "x" };

        Class<?>[] result = ClassUtils.toClass(values);

        assertEquals(3, result.length);
        assertEquals(Object.class, result[0]);
        assertNull(result[1]);
        assertEquals(String.class, result[2]);
    }

    @Test
    public void testToClassWithEmptyArrayReturnsEmptyClassArray() {
        Class<?>[] result = ClassUtils.toClass(new Object[0]);

        assertEquals(0, result.length);
    }
}
