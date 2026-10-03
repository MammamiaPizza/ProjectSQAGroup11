@org.junit.Test
public void generalizedMapTypeRetainsKeyAndContentBindings() {
    com.fasterxml.jackson.databind.type.TypeFactory factory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
    com.fasterxml.jackson.databind.JavaType base = factory.constructMapType(
            java.util.HashMap.class, CompoundKey.class, String.class);

    com.fasterxml.jackson.databind.JavaType generalized =
            factory.constructGeneralizedType(base, java.util.Map.class);

    org.junit.Assert.assertEquals(java.util.Map.class, generalized.getRawClass());
    org.junit.Assert.assertEquals(CompoundKey.class, generalized.getKeyType().getRawClass());
    org.junit.Assert.assertEquals(String.class, generalized.getContentType().getRawClass());
}

@org.junit.Test
public void mapSuperTypeRetainsResolvedKeyAndContentBindings() {
    com.fasterxml.jackson.databind.type.TypeFactory factory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
    com.fasterxml.jackson.databind.JavaType mapType = factory.constructMapType(
            java.util.LinkedHashMap.class, CompoundKey.class, String.class);

    com.fasterxml.jackson.databind.JavaType superType =
            mapType.findSuperType(java.util.Map.class);

    org.junit.Assert.assertNotNull(superType);
    org.junit.Assert.assertEquals(java.util.Map.class, superType.getRawClass());
    org.junit.Assert.assertEquals(CompoundKey.class, superType.getKeyType().getRawClass());
    org.junit.Assert.assertEquals(String.class, superType.getContentType().getRawClass());
}

@org.junit.Test
public void canonicalMapTypeResolvesBothTypeParameters() {
    com.fasterxml.jackson.databind.JavaType type =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                    .constructFromCanonical("java.util.Map<java.lang.Integer,java.lang.String>");

    org.junit.Assert.assertEquals(java.util.Map.class, type.getRawClass());
    org.junit.Assert.assertEquals(Integer.class, type.getKeyType().getRawClass());
    org.junit.Assert.assertEquals(String.class, type.getContentType().getRawClass());
}

@org.junit.Test
public void specializedCollectionRetainsElementValueHandler() {
    com.fasterxml.jackson.databind.type.TypeFactory factory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
    Object handler = new Object();
    com.fasterxml.jackson.databind.JavaType elementType =
            factory.constructType(String.class).withValueHandler(handler);
    com.fasterxml.jackson.databind.JavaType base =
            factory.constructCollectionLikeType(java.util.Collection.class, elementType);

    com.fasterxml.jackson.databind.JavaType specialized =
            factory.constructSpecializedType(base, java.util.ArrayList.class);

    org.junit.Assert.assertEquals(java.util.ArrayList.class, specialized.getRawClass());
    org.junit.Assert.assertSame(handler, specialized.getContentType().getValueHandler());
}