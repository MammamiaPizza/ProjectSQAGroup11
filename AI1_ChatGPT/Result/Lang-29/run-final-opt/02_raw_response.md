package org.apache.commons.lang3;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SystemUtilsJavaVersionIntTest {

    @Test
    public void testJavaVersionIntNullIsIntegerZero() {
        assertEquals("A missing version must be represented by integer zero",
                Integer.valueOf(0), (Object) SystemUtils.toJavaVersionInt(null));
    }

    @Test
    public void testJavaVersionIntSingleComponent() {
        assertEquals(Integer.valueOf(900), (Object) SystemUtils.toJavaVersionInt("9"));
    }

    @Test
    public void testJavaVersionIntTwoComponents() {
        assertEquals(Integer.valueOf(980), (Object) SystemUtils.toJavaVersionInt("9.8"));
    }

    @Test
    public void testJavaVersionIntThreeComponents() {
        assertEquals(Integer.valueOf(987), (Object) SystemUtils.toJavaVersionInt("9.8.7"));
    }

    @Test
    public void testJavaVersionIntIgnoresUpdateComponentAfterTrimLimit() {
        assertEquals(Integer.valueOf(160), (Object) SystemUtils.toJavaVersionInt("1.6.0_23"));
    }

    @Test
    public void testJavaVersionIntArrayParsesAllNumericParts() {
        assertArrayEquals(new int[] { 1, 6, 0, 23 },
                SystemUtils.toJavaVersionIntArray("1.6.0_23"));
    }

    @Test
    public void testJavaVersionIntArrayNullIsEmpty() {
        assertArrayEquals(ArrayUtils.EMPTY_INT_ARRAY, SystemUtils.toJavaVersionIntArray(null));
    }
}