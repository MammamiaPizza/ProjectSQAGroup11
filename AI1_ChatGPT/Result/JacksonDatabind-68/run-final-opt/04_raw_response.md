@Test
public void chainedArrayCreatorsAcceptMixedScalarAndArrayElements() throws Exception {
    Messages result = new com.fasterxml.jackson.databind.ObjectMapper().readValue(
            "{\"messages\":[\"first\",[\"second\",\"third\"]]}",
            Messages.class);

    assertNotNull(result);
    assertEquals(2, result.messages.length);
    assertArrayEquals(new String[] { "first" }, result.messages[0].values);
    assertArrayEquals(new String[] { "second", "third" }, result.messages[1].values);
}