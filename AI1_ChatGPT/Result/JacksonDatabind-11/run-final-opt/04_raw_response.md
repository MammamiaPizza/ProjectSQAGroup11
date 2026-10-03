@Test
public void testConstructTypeFromGenericArrayUsesVariableBound() throws Exception {
    class GenericArrayHolder<T extends CharSequence> {
        public T[] values;
    }

    java.lang.reflect.Field field = GenericArrayHolder.class.getField("values");
    JavaType resolved = TypeFactory.defaultInstance().constructType(field.getGenericType());

    assertEquals(CharSequence[].class, resolved.getRawClass());
    assertNotNull(resolved.containedType(0));
    assertEquals(CharSequence.class, resolved.containedType(0).getRawClass());
}

@Test
public void testConstructTypeFromWildcardUsesUpperBound() throws Exception {
    class WildcardHolder {
        public java.util.List<? extends Number> values;
    }

    java.lang.reflect.Field field = WildcardHolder.class.getField("values");
    JavaType resolved = TypeFactory.defaultInstance().constructType(field.getGenericType());

    assertEquals(java.util.List.class, resolved.getRawClass());
    assertNotNull(resolved.containedType(0));
    assertEquals(Number.class, resolved.containedType(0).getRawClass());
}

@Test
public void testConstructTypeReturnsJavaTypeInputUnchanged() {
    TypeFactory factory = TypeFactory.defaultInstance();
    JavaType type = factory.constructType(String.class);

    assertEquals(type, factory.constructType(type));
}