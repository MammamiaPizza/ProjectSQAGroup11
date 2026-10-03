public void testNextIntReadsLenientUnquotedStringWithPlusSign() throws Exception {
  com.google.gson.stream.JsonReader reader =
      new com.google.gson.stream.JsonReader(new java.io.StringReader("[+123]"));
  reader.setLenient(true);

  reader.beginArray();
  assertEquals(com.google.gson.stream.JsonToken.STRING, reader.peek());
  assertEquals(123, reader.nextInt());
  reader.endArray();
}

public void testNextLongReadsLenientUnquotedStringWithPlusSign() throws Exception {
  com.google.gson.stream.JsonReader reader =
      new com.google.gson.stream.JsonReader(new java.io.StringReader("[+9223372036854775807]"));
  reader.setLenient(true);

  reader.beginArray();
  assertEquals(com.google.gson.stream.JsonToken.STRING, reader.peek());
  assertEquals(9223372036854775807L, reader.nextLong());
  reader.endArray();
}