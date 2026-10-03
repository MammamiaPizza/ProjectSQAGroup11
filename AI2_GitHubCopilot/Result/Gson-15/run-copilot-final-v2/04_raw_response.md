@Test(expected = NullPointerException.class)
 public void testConstructorNullWriter() {
     new JsonWriter(null);
 }

 @Test(expected = NullPointerException.class)
 public void testNameNullThrowsNullPointerException() throws IOException {
     JsonWriter writer = new JsonWriter(new StringWriter());
     writer.beginObject();
     writer.name(null);
 }

 @Test
 public void testJsonValueNullCallsNullValue() throws IOException {
     StringWriter sw = new StringWriter();
     JsonWriter writer = new JsonWriter(sw);
     writer.beginArray();
     writer.jsonValue(null);
     writer.endArray();
     writer.close();
     assertTrue("null missing", sw.toString().contains("null"));
 }

 @Test(expected = IOException.class)
 public void testCloseIncompleteDocument() throws IOException {
     JsonWriter writer = new JsonWriter(new StringWriter());
     writer.beginArray();
     writer.close();
 }