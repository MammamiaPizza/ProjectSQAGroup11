package com.fasterxml.jackson.databind.node;

 import static org.junit.Assert.*;

 import java.io.IOException;
 import java.util.HashMap;
 import java.util.Map;

 import org.junit.Test;

 import com.fasterxml.jackson.databind.JsonNode;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.node.JsonNodeFactory;
 import com.fasterxml.jackson.databind.node.ObjectNode;

 /**
  * Tests related to defect #20: conflicting setter definitions for property "all"
  * caused by the two overloaded {@code setAll} methods in {@link ObjectNode}.
  */
 public class TestObjectNodeBug20 {

     /**
      * Core regression: deserializing a JSON object with a property named "all"
      * must not throw {@code JsonMappingException} due to ambiguous setter discovery.
      */
     @Test
     public void testDeserializePropertyAllDoesNotThrowJsonMappingException() throws IOException {
         ObjectMapper mapper = new ObjectMapper();
         String json = "{\"all\":{\"a\":1}}";
         ObjectNode node = mapper.readValue(json, ObjectNode.class);
         assertNotNull("Deserialized node must not be null", node);
         JsonNode allNode = node.get("all");
         assertNotNull("Property 'all' should be present", allNode);
         assertTrue("Property 'all' should be an object", allNode.isObject());
         assertEquals("Nested value 'a' should be 1", 1, allNode.get("a").intValue());
     }

     /**
      * {@code setAll(Map)} with normal entries is applied correctly.
      */
     @Test
     public void testSetAllMapWithValidEntries() {
         ObjectNode node = JsonNodeFactory.instance.objectNode();
         Map<String, JsonNode> props = new HashMap<String, JsonNode>();
         props.put("name", JsonNodeFactory.instance.textNode("test"));
         props.put("value", JsonNodeFactory.instance.numberNode(42));
         node.setAll(props);
         assertEquals("test", node.get("name").textValue());
         assertEquals(42, node.get("value").intValue());
         assertEquals(2, node.size());
     }

     /**
      * {@code setAll(ObjectNode)} copies all properties from the source node.
      */
     @Test
     public void testSetAllObjectNodeWithValidEntries() {
         ObjectNode target = JsonNodeFactory.instance.objectNode();
         ObjectNode source = JsonNodeFactory.instance.objectNode();
         source.put("x", 1);
         source.put("y", 2);
         target.setAll(source);
         assertEquals(1, target.get("x").intValue());
         assertEquals(2, target.get("y").intValue());
         assertEquals(2, target.size());
     }

     /**
      * {@code setAll(Map)} converts null values to a {@code NullNode}.
      */
     @Test
     public void testSetAllMapWithNullValue() {
         ObjectNode node = JsonNodeFactory.instance.objectNode();
         Map<String, JsonNode> props = new HashMap<String, JsonNode>();
         props.put("nullable", null);
         node.setAll(props);
         JsonNode n = node.get("nullable");
         assertTrue("Null entry should be converted to NullNode", n.isNull());
     }

     /**
      * Passing an empty {@code Map} to {@code setAll(Map)} is a no‑op.
      */
     @Test
     public void testSetAllEmptyMap() {
         ObjectNode node = JsonNodeFactory.instance.objectNode();
         node.setAll(new HashMap<String, JsonNode>());
         assertEquals("Node should remain empty", 0, node.size());
     }

     /**
      * Passing an empty {@code ObjectNode} to {@code setAll(ObjectNode)} is a no‑op.
      */
     @Test
     public void testSetAllEmptyObjectNode() {
         ObjectNode target = JsonNodeFactory.instance.objectNode();
         target.setAll(JsonNodeFactory.instance.objectNode());
         assertEquals("Node should remain empty", 0, target.size());
     }

     /**
      * {@code setAll(Map)} with {@code null} throws {@code NullPointerException}.
      */
     @Test(expected = NullPointerException.class)
     public void testSetAllNullMapThrowsNPE() {
         ObjectNode node = JsonNodeFactory.instance.objectNode();
         node.setAll((Map<String, JsonNode>) null);
     }

     /**
      * {@code setAll(ObjectNode)} with {@code null} throws {@code NullPointerException}.
      */
     @Test(expected = NullPointerException.class)
     public void testSetAllNullObjectNodeThrowsNPE() {
         ObjectNode node = JsonNodeFactory.instance.objectNode();
         node.setAll((ObjectNode) null;
     }

     /**
      * {@code setAll(Map)} overwrites existing properties}.
      */
     @Test
     public void testSetAllMapOverwritesExisting() {
         ObjectNode node = JsonNodeFactory.instance.objectNode();
         node.put("key", "old");
         Map<String, JsonNode> props = new HashMap<String, JsonNode>();
         props.put("key", JsonNodeFactory.instance.textNode("new"));
         node.setAll(props);
         assertEquals("new", node.get("key").textValue());
     }

     /**
      * {@code setAll(ObjectNode)} overwrites existing properties.
      */
     @Test
     public void testSetAllObjectNodeOverwritesExisting() {
         ObjectNode target = JsonNodeFactory.instance.objectNode();
         target.put("key", "old");
         ObjectNode source = JsonNodeFactory.instance.objectNode();
         source.put("key", "new");
         target.setAll(source);
         assertEquals("new", target.get("key").textValue());
     }

     /**
      * Serialization / deserialization round-trip after using {@code setAll}.
      */
     @Test
     public void testSerializationRoundtripWithSetAll() throws IOException {
         ObjectMapper mapper = new ObjectMapper();
         ObjectNode node = JsonNodeFactory.instance.objectNode();
         node.put("foo", "bar");
         ObjectNode nested = JsonNodeFactory.instance.objectNode();
         nested.put("inner", 123);
         node.setAll(nested); // merges "inner" into node

         String json = mapper.writeValueAsString(node);
         ObjectNode restored = mapper.readValue(json, ObjectNode.class);
         assertEquals("bar", restored.get("foo").textValue());
         assertEquals(123, restored.get("inner").intValue());
     }
 }