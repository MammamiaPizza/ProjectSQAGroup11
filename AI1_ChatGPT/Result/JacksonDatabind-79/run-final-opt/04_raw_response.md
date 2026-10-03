@Test
public void testObjectIdInfoCopyPreservesExplicitResolverAndGenerator() {
    com.fasterxml.jackson.databind.introspect.ObjectIdInfo original =
            new com.fasterxml.jackson.databind.introspect.ObjectIdInfo(
                    new com.fasterxml.jackson.databind.PropertyName("id"),
                    String.class,
                    com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator.class,
                    com.fasterxml.jackson.annotation.SimpleObjectIdResolver.class);

    com.fasterxml.jackson.databind.introspect.ObjectIdInfo asId = original.withAlwaysAsId(true);

    assertNotSame(original, asId);
    assertSame(com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator.class,
            asId.getGeneratorType());
    assertSame(com.fasterxml.jackson.annotation.SimpleObjectIdResolver.class,
            asId.getResolverType());
    assertTrue(asId.getAlwaysAsId());
}