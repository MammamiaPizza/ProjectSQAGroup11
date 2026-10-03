@org.junit.Test
public void constructFromCanonicalResolvesPrimitiveNames() {
    com.fasterxml.jackson.databind.type.TypeFactory factory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
    String[] names = { "int", "long", "double", "boolean", "byte", "char", "short", "void" };
    java.lang.Class<?>[] types = {
            Integer.TYPE, Long.TYPE, Double.TYPE, Boolean.TYPE,
            Byte.TYPE, Character.TYPE, Short.TYPE, Void.TYPE
    };

    for (int i = 0; i < names.length; ++i) {
        com.fasterxml.jackson.databind.JavaType type = factory.constructFromCanonical(names[i]);
        org.junit.Assert.assertEquals(types[i], type.getRawClass());
    }
}

@org.junit.Test
public void constructFromCanonicalResolvesMapTypeArguments() {
    com.fasterxml.jackson.databind.JavaType type =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                    .constructFromCanonical("java.util.Map<java.lang.String,java.lang.Integer>");

    org.junit.Assert.assertEquals(java.util.Map.class, type.getRawClass());
    org.junit.Assert.assertEquals(String.class, type.getKeyType().getRawClass());
    org.junit.Assert.assertEquals(Integer.class, type.getContentType().getRawClass());
}

@org.junit.Test
public void constructTypeResolvesWildcardUpperBound() {
    java.lang.reflect.Type reflectedType =
            new com.fasterxml.jackson.core.type.TypeReference<java.util.List<? extends Number>>() { }.getType();

    com.fasterxml.jackson.databind.JavaType type =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance().constructType(reflectedType);

    org.junit.Assert.assertEquals(java.util.List.class, type.getRawClass());
    org.junit.Assert.assertEquals(Number.class, type.getContentType().getRawClass());
}

@org.junit.Test
public void constructTypeResolvesGenericArrayVariableBound() {
    com.fasterxml.jackson.databind.JavaType type =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                    .constructType(genericArrayType());

    org.junit.Assert.assertTrue(type.isArrayType());
    org.junit.Assert.assertEquals(Object[].class, type.getRawClass());
    org.junit.Assert.assertEquals(Object.class, type.getContentType().getRawClass());
}

private <T> java.lang.reflect.Type genericArrayType() {
    return new com.fasterxml.jackson.core.type.TypeReference<T[]>() { }.getType();
}