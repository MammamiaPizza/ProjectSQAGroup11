@Test
public void testSimpleAbstractTypeResolverPreservesCollectionElementType() {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver resolver =
            new com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver();
    resolver.addMapping(java.util.List.class, java.util.ArrayList.class);

    com.fasterxml.jackson.databind.JavaType original = mapper.getTypeFactory()
            .constructCollectionType(java.util.List.class, java.lang.String.class);
    com.fasterxml.jackson.databind.JavaType mapped = resolver.findTypeMapping(
            mapper.getDeserializationConfig(), original);
    com.fasterxml.jackson.databind.JavaType resolved = resolver.resolveAbstractType(
            mapper.getDeserializationConfig(), original);

    assertNotNull(mapped);
    assertSame(java.util.ArrayList.class, mapped.getRawClass());
    assertSame(java.lang.String.class, mapped.getContentType().getRawClass());
    assertNotNull(resolved);
    assertSame(java.util.ArrayList.class, resolved.getRawClass());
    assertSame(java.lang.String.class, resolved.getContentType().getRawClass());
}

@Test
@SuppressWarnings({ "rawtypes", "unchecked" })
public void testSimpleAbstractTypeResolverRejectsUnrelatedMapping() {
    com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver resolver =
            new com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver();

    try {
        resolver.addMapping((Class) java.util.List.class, (Class) java.util.HashMap.class);
        fail("Mappings must require the target type to be a subtype of the source type");
    } catch (IllegalArgumentException e) {
        assertTrue(e.getMessage().contains(java.util.List.class.getName()));
        assertTrue(e.getMessage().contains(java.util.HashMap.class.getName()));
    }
}