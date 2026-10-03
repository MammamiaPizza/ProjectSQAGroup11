@Test
public void testCreateMockForReturnsNull() throws Exception {
    SpyAnnotationEngine engine = new SpyAnnotationEngine();
    java.lang.reflect.Field field = getClass().getDeclaredField("f");
    Spy spyAnnotation = field.getAnnotation(Spy.class);
    assertNull(engine.createMockFor(spyAnnotation, field));
}

@Test
public void testProcessOnNonConstructibleTypeRestoresFieldAccessibility() throws Exception {
    SpyAnnotationEngine engine = new SpyAnnotationEngine();
    SpyHolder instance = new SpyHolder();
    java.lang.reflect.Field field = SpyHolder.class.getDeclaredField("target");
    boolean original = field.isAccessible();
    engine.process(SpyHolder.class, instance);
    assertEquals(original, field.isAccessible());
}

private static class NonDefaultConstructible {
    public NonDefaultConstructible(String arg) {}
}

private static class SpyHolder {
    @Spy
    NonDefaultConstructible target;
}