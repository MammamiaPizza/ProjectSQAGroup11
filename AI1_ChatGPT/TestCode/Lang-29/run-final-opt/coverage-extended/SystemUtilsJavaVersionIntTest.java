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

@Test
public void testJavaVersionMatchHandlesNullAndPrefixes() {
    org.junit.Assert.assertFalse(SystemUtils.isJavaVersionMatch(null, "1.6"));
    org.junit.Assert.assertTrue(SystemUtils.isJavaVersionMatch("1.6.0_23", "1.6"));
    org.junit.Assert.assertFalse(SystemUtils.isJavaVersionMatch("1.5.0_22", "1.6"));
}

@Test
public void testOSNameMatchHandlesNullAndPrefixes() {
    org.junit.Assert.assertFalse(SystemUtils.isOSNameMatch(null, "Windows"));
    org.junit.Assert.assertTrue(SystemUtils.isOSNameMatch("Windows 7", "Windows"));
    org.junit.Assert.assertFalse(SystemUtils.isOSNameMatch("Linux", "Windows"));
}

@Test
public void testJavaVersionFloatHandlesNullAndSingleComponentVersions() {
    org.junit.Assert.assertEquals(0.0f, SystemUtils.toJavaVersionFloat(null), 0.0f);
    org.junit.Assert.assertEquals(9.0f, SystemUtils.toJavaVersionFloat("9"), 0.0f);
}

@Test
public void testDirectoryAccessorsReflectSystemProperties() {
    org.junit.Assert.assertEquals(new java.io.File(System.getProperty("java.home")), SystemUtils.getJavaHome());
    org.junit.Assert.assertEquals(new java.io.File(System.getProperty("java.io.tmpdir")), SystemUtils.getJavaIoTmpDir());
    org.junit.Assert.assertEquals(new java.io.File(System.getProperty("user.dir")), SystemUtils.getUserDir());
    org.junit.Assert.assertEquals(new java.io.File(System.getProperty("user.home")), SystemUtils.getUserHome());
}
}
