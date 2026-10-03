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

@Test
public void testToClassWithNullArrayReturnsNull() {
    org.junit.Assert.assertNull(ClassUtils.toClass((Object[]) null));
}

@Test
public void testConvertClassNamesToClassesHandlesNullValidAndInvalidNames() {
    org.junit.Assert.assertNull(ClassUtils.convertClassNamesToClasses((java.util.List<String>) null));

    java.util.List<Class<?>> classes = ClassUtils.convertClassNamesToClasses(
            java.util.Arrays.asList("java.lang.String", "not.a.RealClass"));
    org.junit.Assert.assertEquals(String.class, classes.get(0));
    org.junit.Assert.assertNull(classes.get(1));
}

@Test
public void testConvertClassesToClassNamesHandlesNullAndNullElements() {
    org.junit.Assert.assertNull(ClassUtils.convertClassesToClassNames((java.util.List<Class<?>>) null));

    java.util.List<String> classNames = ClassUtils.convertClassesToClassNames(
            java.util.Arrays.<Class<?>>asList(String.class, null));
    org.junit.Assert.assertEquals("java.lang.String", classNames.get(0));
    org.junit.Assert.assertNull(classNames.get(1));
}

@Test
public void testGetAllInterfacesHandlesNullAndInheritedInterfaces() {
    org.junit.Assert.assertNull(ClassUtils.getAllInterfaces(null));

    java.util.List<Class<?>> interfaces = ClassUtils.getAllInterfaces(java.util.ArrayList.class);
    org.junit.Assert.assertTrue(interfaces.contains(java.util.List.class));
    org.junit.Assert.assertTrue(interfaces.contains(java.util.Collection.class));
    org.junit.Assert.assertTrue(interfaces.contains(java.util.RandomAccess.class));
}
}
