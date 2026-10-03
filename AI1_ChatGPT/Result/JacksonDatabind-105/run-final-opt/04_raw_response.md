@Test
public void testLookupReturnsNullForUnsupportedTypeDespiteKnownJdkName() {
    org.junit.Assert.assertNull(
            com.fasterxml.jackson.databind.deser.std.JdkDeserializers.find(
                    Object.class, java.util.UUID.class.getName()));
}