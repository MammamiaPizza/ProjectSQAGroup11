@Test
 public void testGetSchema() throws Exception {
     StdKeySerializer serializer = new StdKeySerializer();
     com.fasterxml.jackson.databind.JsonNode schema = serializer.getSchema(null, null);
     assertNotNull(schema);
     assertTrue("Schema node should refer to string type",
         schema.asText().contains("string"));
 }

 @Test
 public void testAcceptJsonFormatVisitor() throws Exception {
     final boolean[] called = { false };
     com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper visitor =
         new com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper(null) {
             @Override
             public com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitable
                     expectStringFormat(com.fasterxml.jackson.databind.JavaType typeHint) {
                 called[0] = true;
                 return null;
             }
             @Override
             public com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
                     expectAnyFormat(com.fasterxml.jackson.databind.JavaType typeHint) {
                 return null;
             }
         };
     new StdKeySerializer().acceptJsonFormatVisitor(visitor, null);
     assertTrue("expectStringFormat should have been called", called[0]);
 }