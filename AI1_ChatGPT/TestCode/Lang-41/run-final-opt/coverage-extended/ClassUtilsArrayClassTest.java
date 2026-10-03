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

@org.junit.Test
public void multidimensionalReferenceArrayUsesReadableSyntaxAndComponentPackage() {
    assertEquals("String[][]", ClassUtils.getShortClassName(String[][].class));
    assertEquals("java.lang", ClassUtils.getPackageName(String[][].class));
}

@org.junit.Test
public void multidimensionalPrimitiveArrayUsesReadableSyntaxAndNoPackage() {
    assertEquals("int[][]", ClassUtils.getShortClassName(int[][].class));
    assertEquals("", ClassUtils.getPackageName(int[][].class));
}

@org.junit.Test
public void innerClassArrayUsesReadableInnerClassNameAndComponentPackage() {
    assertEquals("Map.Entry[]", ClassUtils.getShortClassName(java.util.Map.Entry[].class));
    assertEquals("java.util", ClassUtils.getPackageName(java.util.Map.Entry[].class));
}
}
