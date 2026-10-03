@Test
public void testListBasedBindingsRetainSingleGenericParameter() {
    com.fasterxml.jackson.databind.JavaType stringType =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                    .constructType(String.class);

    com.fasterxml.jackson.databind.type.TypeBindings bindings =
            com.fasterxml.jackson.databind.type.TypeBindings.create(
                    java.util.List.class,
                    java.util.Collections.singletonList(stringType));

    org.junit.Assert.assertEquals(1, bindings.size());
    org.junit.Assert.assertEquals("E", bindings.getBoundName(0));
    org.junit.Assert.assertEquals(stringType, bindings.getBoundType(0));
}

@Test
public void testCreateIfNeededHandlesGenericAndNonGenericClasses() {
    com.fasterxml.jackson.databind.JavaType stringType =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
                    .constructType(String.class);

    com.fasterxml.jackson.databind.type.TypeBindings nonGeneric =
            com.fasterxml.jackson.databind.type.TypeBindings.createIfNeeded(String.class, stringType);
    com.fasterxml.jackson.databind.type.TypeBindings generic =
            com.fasterxml.jackson.databind.type.TypeBindings.createIfNeeded(
                    java.util.concurrent.atomic.AtomicReference.class, stringType);

    org.junit.Assert.assertTrue(nonGeneric.isEmpty());
    org.junit.Assert.assertEquals(1, generic.size());
    org.junit.Assert.assertEquals("V", generic.getBoundName(0));
    org.junit.Assert.assertEquals(stringType, generic.getBoundType(0));
}

@Test
public void testConstructTypeFromParameterizedMapReferenceRetainsTypes() {
    com.fasterxml.jackson.databind.JavaType mapType =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance().constructType(
                    new com.fasterxml.jackson.core.type.TypeReference<
                            java.util.Map<String, Integer>>() { }.getType());

    org.junit.Assert.assertEquals(java.util.Map.class, mapType.getRawClass());
    org.junit.Assert.assertEquals(String.class, mapType.getKeyType().getRawClass());
    org.junit.Assert.assertEquals(Integer.class, mapType.getContentType().getRawClass());
}