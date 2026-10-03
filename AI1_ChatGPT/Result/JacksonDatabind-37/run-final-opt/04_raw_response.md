@Test
public void protectedCopyAndNarrowingPreserveOrChangeRawTypeAsAppropriate() {
    ExposedSimpleType type = new ExposedSimpleType(
            com.fasterxml.jackson.databind.type.SimpleType.constructUnsafe(String.class));

    org.junit.Assert.assertEquals(String.class, type.getRawClass());
    org.junit.Assert.assertEquals("java.lang.String", type.toCanonical());
    org.junit.Assert.assertSame(type, type.narrowTo(String.class));

    com.fasterxml.jackson.databind.JavaType narrowed = type.narrowTo(Integer.class);
    org.junit.Assert.assertEquals(Integer.class, narrowed.getRawClass());
    org.junit.Assert.assertEquals("java.lang.Integer", narrowed.toCanonical());
}

@Test
public void simpleTypeDoesNotEqualNull() {
    org.junit.Assert.assertFalse(
            com.fasterxml.jackson.databind.type.SimpleType.constructUnsafe(String.class).equals(null));
}

private static final class ExposedSimpleType extends com.fasterxml.jackson.databind.type.SimpleType {
    private ExposedSimpleType(com.fasterxml.jackson.databind.type.TypeBase base) {
        super(base);
    }

    private com.fasterxml.jackson.databind.JavaType narrowTo(Class<?> rawClass) {
        return _narrow(rawClass);
    }
}