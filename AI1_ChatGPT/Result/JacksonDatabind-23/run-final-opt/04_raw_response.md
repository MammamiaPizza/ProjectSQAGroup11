@Test
public void testProtectedConstructorCanBeUsedBySubclass() {
    assertNotNull(new com.fasterxml.jackson.databind.ser.std.NumberSerializers() { });
}