@Test
public void shouldSerializeNestedGenericDeepStubAndRetainItsResolvedReturnType() throws Exception {
    java.util.Map<String, java.util.List<java.util.List<String>>> root =
            org.mockito.Mockito.mock(
                    java.util.Map.class,
                    org.mockito.Mockito.withSettings()
                            .serializable()
                            .defaultAnswer(org.mockito.Mockito.RETURNS_DEEP_STUBS));

    java.util.List<java.util.List<String>> nested = root.get("key");
    org.junit.Assert.assertNotNull(nested);

    java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
    java.io.ObjectOutputStream output = new java.io.ObjectOutputStream(bytes);
    output.writeObject(nested);
    output.close();

    java.io.ObjectInputStream input = new java.io.ObjectInputStream(
            new java.io.ByteArrayInputStream(bytes.toByteArray()));
    java.util.List<?> restored = (java.util.List<?>) input.readObject();
    input.close();

    org.junit.Assert.assertNotNull(restored);
    org.junit.Assert.assertNotNull(restored.get(0));
}