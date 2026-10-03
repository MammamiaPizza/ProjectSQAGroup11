@Test
 public void testNextBoolean_FilledJsonObject() throws IOException {
     JsonObject obj = new JsonObject();
     obj.addProperty("flag", true);
     JsonTreeReader reader = new JsonTreeReader(obj);
     reader.beginObject();
     reader.nextName();
     assertTrue(reader.nextBoolean());
     reader.endObject();
     assertEquals(JsonToken.END_DOCUMENT, reader.peek());
 }

 @Test
 public void testNextBoolean_RootPrimitive() throws IOException {
     JsonTreeReader reader = new JsonTreeReader(new JsonPrimitive(false));
     assertFalse(reader.nextBoolean());
     assertEquals(JsonToken.END_DOCUMENT, reader.peek());
 }

 @Test
 public void testClose_ThenPeek() throws IOException {
     JsonTreeReader reader = new JsonTreeReader(new JsonObject());
     reader.close();
     assertEquals(JsonToken.END_DOCUMENT, reader.peek());
 }

 @Test
 public void testExpect_Failure() throws IOException {
     JsonTreeReader reader = new JsonTreeReader(new JsonPrimitive("hello"));
     try {
         reader.beginObject();
         fail("Expected IllegalStateException");
     } catch (IllegalStateException expected) {
         assertTrue(expected.getMessage().contains("Expected BEGIN_OBJECT but was"));
     }
 }