package com.fasterxml.jackson.databind.node;

 import java.io.ByteArrayInputStream;
 import java.io.InputStream;
 import java.io.Reader;
 import java.io.StringReader;

 import org.junit.Test;

 import com.fasterxml.jackson.core.JsonFactory;
 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.databind.JsonNode;
 import com.fasterxml.jackson.databind.ObjectMapper;

 import static org.junit.Assert.*;

 public class EmptyContentAsTreeTest {

     private final ObjectMapper mapper = new ObjectMapper();
     private final JsonFactory factory = new JsonFactory();

     @Test
     public void testNullFromEOFWithParserAndReader() throws Exception {
         // JsonParser from empty string: should return null, not MissingNode
         JsonParser p = factory.createParser("");
         JsonNode result = mapper.reader().readTree(p);
         assertNull("Should get null for reads with JsonParser at EOf", result);
         p.close();

         // Empty Reader: should also return null
         Reader reader = new StringReader("");
         JsonNode result2 = mapper.reader().readTree(reader);
         assertNull("Should get null for reads with empty Reader", result2);
     }

     @Test
     public void testNullFromEmptyInputStream() throws Exception {
         InputStream in = new ByteArrayInputStream(new byte[0]);
         JsonNode result = mapper.reader().readTree(in);
         assertNull("Should get null for empty InputStream", result);
     }

     @Test
     public void testNullFromEmptyByteArray() throws Exception {
         JsonNode result = mapper.reader().readTree(new byte[0]);
         assertNull("Should get null for empty byte array", result);
     }

     @Test
     public void testNullFromEmptyString() throws Exception {
         JsonNode result = mapper.reader().readTree("");
         assertNull("Should get null for empty string", result);
     }

     @Test
     public void testNullFromParserAfterAllTokensConsumed() throws Exception {
         JsonParser p = factory.createParser("42");
         p.nextToken(); // consume the number token
         p.nextToken(); // returns null (EOf)
         JsonNode result = mapper.reader().readTree(p);
         assertNull("Should get null when parser has no remaining tokens", result);
         p.close();
     }

     @Test
     public void testReadTreeWithObjectNode() throws Exception {
         JsonParser p = factory.createParser("{\"nam\":\"value\"}");
         JsonNode result = mapper.reader().readTree(p);
         assertNotNull("Should get non-null for valid JSON object", result);
         assertTrue("Result should be ObjectNode", result.isObject());
         assertEquals("value", result.get("nam").asText());
         p.close();
     }

     @Test
     public void testReadTreeWithArrayNode() throws Exception {
         JsonParser p = factory.createParser("[1,2,3]");
         JsonNode result = mapper.reader().readTree(p);
         assertNotNull("Should get non-null for valid JSON array", result);
         assertTrue("Result should be ArrayNode", result.isArray());
         assertEquals(3, result.size());
         p.close();
     }

     @Test
     public void testReadTreeWithNullValue() throws Exception {
         JsonParser p = factory.createParser("null");
         JsonNode result = mapper.reader().readTree(p);
         assertNotNull("Should get non-null for JSON null literal", result);
         assertTrue("Result should be NullNode for JSON null", result.isNull());
         p.close();
     }

     @Test
     public void testReadTreeWithScalarValues() throws Exception {
         // Text value
         JsonParser p1 = factory.createParser("\"hello\"");
         JsonNode textResult = mapper.reader().readTree(p1);
         assertTrue("Should be text node", textResult.isTextual());
         assertEquals("hello", textResult.asText());
         p1.close();

         // Number value
         JsonParser p2 = factory.createParser("42");
         JsonNode numResult = mapper.reader().readTree(p2);
         assertTrue("Should be number node", numResult.isNumber());
         assertEquals(42, numResult.asInt());
         p2.close();

         // Boolean value
         JsonParser p3 = factory.createParser("false");
         JsonNode boolResult = mapper.reader().readTree(p3);
         assertTrue("Should be boolean node", boolResult.isBoolean());
         assertFalse(boolResult.asBoolean());
         p3.close();
     }

     @Test
     public void testEmptyInputReturnsNullNotMisingNode() throws Exception {
         // Explicitly verify empty parser returns null, demonstrating the fix
         JsonParser p = factory.createParser("");
         JsonNode result = mapper.reader().readTree(p);
         assertNull("Empty content must return null, not MissingNode", result);
         p.close();

         // Verify that non-empty input still works correctly after the fix
         JsonParser p2 = factory.createParser("{\"ok\":true}");
         JsonNode validResult = mapper.reader().readTree(p2);
         assertNotNull(validResult);
         assertTrue(validResult.isObject());
         assertTrue(validResult.get("ok").asBoolean());
         p2.close();
     }

     @Test
     public void testReadTreeWithNestedStructure() throws Exception {
         JsonParser p = factory.createParser(
             "{\"arr\":[{\"id\":1},{\"id\":2}],\"flag\":true,\"val\":null,\"nam\":\"test\"}");
         JsonNode result = mapper.reader().readTree(p);
         assertNotNull(result);
         assertTrue(result.isObject());
         assertEquals(2, result.get("arr").size());
         assertEquals(1, result.get("arr").get(0).get("id").asInt());
         assertTrue(result.get("flag").asBoolean());
         assertTrue(result.get("val").isNull());
         assertEquals("test", result.get("nam").asText());
         p.close();
     }

     @Test
     public void testEmptyContentWithMultipleReadTreeOverloads() throws Exception {
         // Consistency check: all zero-length inputs should return null
         assertNull(mapper.reader().readTree(new byte[0]));
         assertNull(mapper.reader().readTree(""));
         assertNull(mapper.reader().readTree(new StringReader("")));
         assertNull(mapper.reader().readTree(new ByteArrayInputStream(new byte[0])));

         // After empty check, non-empty content should still parse normally
         JsonNode node = mapper.reader().readTree("42");
         assertNotNull(node);
         assertTrue(node.isNumber());
         assertEquals(42, node.asInt());
     }
 }