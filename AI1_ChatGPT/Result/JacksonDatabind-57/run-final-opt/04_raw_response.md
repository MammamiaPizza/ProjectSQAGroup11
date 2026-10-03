@Test
public void readsSequentialTreeValuesFromUtf32BigEndianStream() throws Exception {
    byte[] input = utf32Ascii("{\"a\":5} {\"a\":6}", true);
    com.fasterxml.jackson.databind.MappingIterator<com.fasterxml.jackson.databind.JsonNode> values =
            new com.fasterxml.jackson.databind.ObjectMapper()
                    .readerFor(com.fasterxml.jackson.databind.JsonNode.class)
                    .readValues(new java.io.ByteArrayInputStream(input));

    assertTrue(values.hasNext());
    assertEquals(5, values.next().get("a").asInt());
    assertTrue(values.hasNext());
    assertEquals(6, values.next().get("a").asInt());
    assertFalse(values.hasNext());
}

@Test
public void readsSequentialTreeValuesFromUtf32LittleEndianBytes() throws Exception {
    byte[] input = utf32Ascii("{\"a\":7} {\"a\":8}", false);
    com.fasterxml.jackson.databind.MappingIterator<com.fasterxml.jackson.databind.JsonNode> values =
            new com.fasterxml.jackson.databind.ObjectMapper()
                    .readerFor(com.fasterxml.jackson.databind.JsonNode.class)
                    .readValues(input);

    assertTrue(values.hasNext());
    assertEquals(7, values.next().get("a").asInt());
    assertTrue(values.hasNext());
    assertEquals(8, values.next().get("a").asInt());
    assertFalse(values.hasNext());
}

@Test
public void rejectsArrayValueToUpdate() {
    boolean thrown = false;
    try {
        new com.fasterxml.jackson.databind.ObjectMapper()
                .readerFor(int[].class)
                .withValueToUpdate(new int[1]);
    } catch (IllegalArgumentException e) {
        thrown = true;
        assertEquals("Can not update an array value", e.getMessage());
    }
    assertTrue(thrown);
}

private byte[] utf32Ascii(String text, boolean bigEndian) {
    byte[] result = new byte[text.length() * 4];
    for (int i = 0; i < text.length(); ++i) {
        int offset = i * 4;
        int ch = text.charAt(i);
        if (bigEndian) {
            result[offset + 3] = (byte) ch;
        } else {
            result[offset] = (byte) ch;
        }
    }
    return result;
}