package org.apache.commons.lang;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ClassUtilsArrayClassTest {

    @Test
    public void getShortClassNameForReferenceArrayUsesReadableArraySyntax() {
        assertEquals("String[]", ClassUtils.getShortClassName(String[].class));
    }

    @Test
    public void getPackageNameForReferenceArrayUsesComponentPackage() {
        assertEquals("java.lang", ClassUtils.getPackageName(String[].class));
    }

    @Test
    public void primitiveArrayHasReadableShortNameAndNoPackage() {
        assertEquals("int[]", ClassUtils.getShortClassName(int[].class));
        assertEquals("", ClassUtils.getPackageName(int[].class));
    }

    @Test
    public void nonArrayClassNameAndPackageRemainUnchanged() {
        assertEquals("String", ClassUtils.getShortClassName(String.class));
        assertEquals("java.lang", ClassUtils.getPackageName(String.class));
    }
}
