@Test
public void findResolvesFloatPrimitiveAndWrapperDeserializers() {
    assertNotNull(com.fasterxml.jackson.databind.deser.std.NumberDeserializers.find(
            Float.TYPE, Float.TYPE.getName()));
    assertNotNull(com.fasterxml.jackson.databind.deser.std.NumberDeserializers.find(
            Float.class, Float.class.getName()));
}

@Test
public void findResolvesGeneralAndBigNumberDeserializers() {
    assertNotNull(com.fasterxml.jackson.databind.deser.std.NumberDeserializers.find(
            Number.class, Number.class.getName()));
    assertNotNull(com.fasterxml.jackson.databind.deser.std.NumberDeserializers.find(
            java.math.BigDecimal.class, java.math.BigDecimal.class.getName()));
    assertNotNull(com.fasterxml.jackson.databind.deser.std.NumberDeserializers.find(
            java.math.BigInteger.class, java.math.BigInteger.class.getName()));
}

@Test
public void findRejectsUnsupportedNumberTypes() {
    assertNull(com.fasterxml.jackson.databind.deser.std.NumberDeserializers.find(
            String.class, String.class.getName()));

    try {
        com.fasterxml.jackson.databind.deser.std.NumberDeserializers.find(
                Void.TYPE, Void.TYPE.getName());
        fail("Void is not a supported numeric primitive type");
    } catch (IllegalArgumentException e) {
        assertNotNull(e.getMessage());
    }
}