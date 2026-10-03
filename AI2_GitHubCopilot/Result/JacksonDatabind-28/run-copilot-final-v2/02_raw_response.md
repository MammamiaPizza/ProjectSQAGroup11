package com.fasterxml.jackson.databind.deser.std;

 import static org.junit.Assert.*;

 import org.junit.Test;

 import com.fasterxml.jackson.core.JsonFactory;
 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.core.JsonToken;
 import com.fasterxml.jackson.databind.JsonMappingException;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.ObjectReader;
 import com.fasterxml.jackson.databind.node.ObjectNode;
 import com.fasterxml.jackson.databind.node.JsonNodeFactory;

 public class JsonNodeDeserializerTest {

     private final ObjectMapper mapper = new ObjectMapper();
     private final ObjectReader objectNodeReader = mapper.reader(ObjectNode.class);
     private final JsonFactory jsonFactory = new JsonFactory();

     /* ------------------------------------------------------------------ */
     /*  Tests directly targeting the bug: END_OBJECT token handling        */
     /* ------------------------------------------------------------------ */

     /**
      * This is the exact trigger of issue 941: when the parser is positioned
      * at END_OBJECT (after an empty object), deserialization must produce
      * an empty ObjectNode instead of throwing a mapping exception.
      */
     @Test
     public void testEmptyObjectAtEndObjectToken() throws Exception {
         JsonParser p = jsonFactory.createParser("{}");
         p.nextToken(); // move past START_OBJECT
         // Now current token is END_OBJECT
         ObjectNode node = objectNodeReader.readValue(p);
         assertNotNull("Empty object should not deserialize to null", node);
         assertEquals("Size must be zero for empty object", 0, node.size());
         p.close();
     }

     /**
      * Normal case: parser is at START_OBJECT. This must work regardless.
      */
     @Test
     public void testEmptyObjectAtStartObjectToken() throws Exception {
         JsonParser p = jsonFactory.createParser("{}");
         ObjectNode node = objectNodeReader.readValue(p);
         assertNotNull(node);
         assertEquals(0, node.size());
         p.close();
     }

     /**
      * Parser positioned at FIELD_NAME of an object with a single field.
      * The buggy code explicitly handles FIELD_NAME; we verify it still works.
      */
     @Test
     public void testObjectAtFieldNameToken() throws Exception {
         JsonParser p = jsonFactory.createParser("{\"a\":42}");
         p.nextToken(); // skip START_OBJECT, now at FIELD_NAME
         ObjectNode node = objectNodeReader.readValue(p);
         assertNotNull(node);
         assertEquals(1, node.size());
         assertEquals(42, node.get("a").intValue());
         p.close();
     }

     /* ------------------------------------------------------------------ */
     /*  Normal / happy-path tests using typical deserialization flow       */
     /* ------------------------------------------------------------------ */

     @Test
     public void testObjectWithMultipleFields() throws Exception {
         JsonParser p = jsonFactory.createParser("{\"s\":\"text\",\"b\":true,\"n\":null}");
         ObjectNode node = objectNodeReader.readValue(p);
         assertNotNull(node);
         assertEquals(3, node.size());
         assertEquals("text", node.get("s").textValue());
         assertTrue(node.get("b").booleanValue());
         assertTrue(node.get("n").isNull());
         p.close();
     }

     @Test
     public void testNestedObjects() throws Exception {
         JsonParser p = jsonFactory.createParser("{\"inner\":{\"key\":\"val\"}}");
         ObjectNode node = objectNodeReader.readValue(p);
         assertNotNull(node);
         assertEquals(1, node.size());
         ObjectNode inner = (ObjectNode) node.get("inner");
         assertNotNull(inner);
         assertEquals(1, inner.size());
         assertEquals("val", inner.get("key").textValue());
         p.close();
     }

     @Test
     public void testObjectWithArrayField() throws Exception {
         JsonParser p = jsonFactory.createParser("{\"arr\":[1,2,3]}");
         ObjectNode node = objectNodeReader.readValue(p);
         assertNotNull(node);
         assertEquals(3, node.get("arr").size());
         p.close();
     }

     @Test
     public void testObjectWithNumberField() throws Exception {
         JsonParser p = jsonFactory.createParser("{\"int\":1,\"float\":2.5}");
         ObjectNode node = objectNodeReader.readValue(p);
         assertNotNull(node);
         assertEquals(1, node.get("int").intValue());
         assertEquals(2.5, node.get("float").doubleValue(), 0.0);
         p.close();
     }

     /* ------------------------------------------------------------------ */
     /*  Boundary / edge-case tests                                         */
     /* ------------------------------------------------------------------ */

     @Test
     public void testObjectWithNullFieldAndFalseField() throws Exception {
         JsonParser p = jsonFactory.createParser("{\"flag\":false,\"nothing\":null}");
         ObjectNode node = objectNodeReader.readValue(p);
         assertFalse(node.get("flag").booleanValue());
         assertTrue(node.get("nothing").isNull());
         p.close();
     }

     @Test
     public void testObjectWithBooleanField() throws Exception {
         JsonParser p = jsonFactory.createParser("{\"yes\":true,\"no\":false}");
         ObjectNode node = objectNodeReader.readValue(p);
         assertTrue(node.get("yes").booleanValue());
         assertFalse(node.get("no").booleanValue());
         p.close();
     }

     /* ------------------------------------------------------------------ */
     /*  Invalid / exception tests – ensures proper error when token is    */
     /*  not START_OBJECT, FIELD_NAME, or END_OBJECT.                      */
     /* ------------------------------------------------------------------ */

     @Test(expected = JsonMappingException.class)
     public void testInvalidCurrentTokenStartArray() throws Exception {
         JsonParser p = jsonFactory.createParser("[]");
         objectNodeReader.readValue(p); // START_ARRAY is not valid
         p.close();
     }

     @Test(expected = JsonMappingException.class)
     public void testInvalidCurrentTokenValueString() throws Exception {
         JsonParser p = jsonFactory.createParser("\"oops\"");
         objectNodeReader.readValue(p); // VALUE_STRING is not valid
         p.close();
     }

     @Test
     public void testReadNullLiteralReturnsNull() throws Exception {
         JsonParser p = jsonFactory.createParser("null");
         ObjectNode node = objectNodeReader.readValue(p);
         // Jackson's default behavior for root-level JSON null is to return Java null
         assertNull("Root null maps to Java null for ObjectNode", node);
         p.close();
     }
 }