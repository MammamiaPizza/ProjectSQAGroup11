@org.junit.Test
public void determineTypeArgumentsResolvesArrayListElementTypeForListSupertype() throws Exception {
    class ListHolder {
        java.util.List<java.lang.String> value;
    }

    java.lang.reflect.ParameterizedType listOfString =
            (java.lang.reflect.ParameterizedType) ListHolder.class.getDeclaredField("value").getGenericType();

    java.util.Map<java.lang.reflect.TypeVariable<?>, java.lang.reflect.Type> assignments =
            org.apache.commons.lang3.reflect.TypeUtils.determineTypeArguments(
                    java.util.ArrayList.class, listOfString);

    org.junit.Assert.assertNotNull(assignments);
    org.junit.Assert.assertEquals(java.lang.String.class,
            assignments.get(java.util.ArrayList.class.getTypeParameters()[0]));
}

@org.junit.Test
public void determineTypeArgumentsReturnsNullForUnrelatedClass() throws Exception {
    class ListHolder {
        java.util.List<java.lang.String> value;
    }

    java.lang.reflect.ParameterizedType listOfString =
            (java.lang.reflect.ParameterizedType) ListHolder.class.getDeclaredField("value").getGenericType();

    org.junit.Assert.assertNull(org.apache.commons.lang3.reflect.TypeUtils.determineTypeArguments(
            java.util.HashSet.class, listOfString));
}

@org.junit.Test
public void normalizeUpperBoundsRemovesRedundantSupertype() {
    java.lang.reflect.Type[] normalized = org.apache.commons.lang3.reflect.TypeUtils.normalizeUpperBounds(
            new java.lang.reflect.Type[] { Object.class, java.lang.String.class });

    org.junit.Assert.assertEquals(1, normalized.length);
    org.junit.Assert.assertEquals(java.lang.String.class, normalized[0]);
}

@org.junit.Test
public void arrayTypeUtilitiesHandleClassGenericArrayAndNonArrayTypes() throws Exception {
    class ArrayHolder<T> {
        T[] values;
    }

    java.lang.reflect.Type genericArray = ArrayHolder.class.getDeclaredField("values").getGenericType();

    org.junit.Assert.assertTrue(org.apache.commons.lang3.reflect.TypeUtils.isArrayType(java.lang.String[].class));
    org.junit.Assert.assertTrue(org.apache.commons.lang3.reflect.TypeUtils.isArrayType(genericArray));
    org.junit.Assert.assertEquals(java.lang.String.class,
            org.apache.commons.lang3.reflect.TypeUtils.getArrayComponentType(java.lang.String[].class));
    org.junit.Assert.assertEquals(ArrayHolder.class.getTypeParameters()[0],
            org.apache.commons.lang3.reflect.TypeUtils.getArrayComponentType(genericArray));
    org.junit.Assert.assertNull(org.apache.commons.lang3.reflect.TypeUtils.getArrayComponentType(java.lang.String.class));
}