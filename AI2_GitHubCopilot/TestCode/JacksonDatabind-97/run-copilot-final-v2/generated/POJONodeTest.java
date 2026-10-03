package com.fasterxml.jackson.databind.node;

import static org.junit.Assert.*;
import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class POJONodeTest {

 // -- inner helpers for custom serializer tests --

 static class MyPojo {
     public String name;
     public MyPojo(String n) { this.name = n; }
     @Override public String toString() { return "Hello!"; }
 }

 static class MyPojoSerializer extends JsonSerializer<MyPojo> {
     @Override
     public void serialize(MyPojo value, JsonGenerator gen, SerializerProvider serializers) throws
IOException {
         gen.writeString("The value is: " + value.toString());
     }
 }

 static class Wrapper {
     public Object str;
     public Wrapper(Object s) { this.str = s; }
 }

 static class MyIntVal {
     public int v;
     public MyIntVal(int v) { this.v = v; }
     @Override public String toString() { return String.valueOf(v); }
 }

 static class MyIntValSerializer extends JsonSerializer<MyIntVal> {
     @Override
     public void serialize(MyIntVal value, JsonGenerator gen, SerializerProvider serializers) throws
IOException {
         gen.writeString("Number: " + value.v);
     }
 }

 static class MyBase { public String base; }
 static class MyDerivedA extends MyBase { public int aVal; }
 static class MyDerivedB extends MyBase { public String bVal; }

 static class MyBaseSerializer extends JsonSerializer<MyBase> {
     @Override
     public void serialize(MyBase value, JsonGenerator gen, SerializerProvider serializers) throws
IOException {
         gen.writeString("base-serialized");
     }
 }

 // ===== test methods =====

 @Test
 public void testPOJONodeCustomSer() throws Exception {
     // Bug: gen.writeObject(_value) bypasses custom serializer registered via module.
     ObjectMapper mapper = new ObjectMapper();
     SimpleModule mod = new SimpleModule();
     mod.addSerializer(MyPojo.class, new MyPojoSerializer());
     mapper.registerModule(mod);

     Wrapper w = new Wrapper(new POJONode(new MyPojo("Hello!")));
     String json = mapper.writeValueAsString(w);

     // In the buggy version the custom serializer is not used, so "NULL" appears instead of the
expected string.
     // The fixed version emits "The value is: Hello!".
     assertTrue("Expected custom serialization result", json.contains("\"str\":\"The value is:
Hello!\""));
 }

 @Test
 public void testNullPOJONodeSerialization() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     String json = mapper.writeValueAsString(new POJONode(null));
     assertEquals("null", json); // default null serialization
 }

 @Test
 public void testPOJONodeIntegerCustomSer() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     SimpleModule mod = new SimpleModule();
     mod.addSerializer(MyIntVal.class, new MyIntValSerializer());
     mapper.registerModule(mod);

     String json = mapper.writeValueAsString(new POJONode(new MyIntVal(42)));
     // The bug prevents custom serializer from firing; on fix it should contain "Number: 42"
     assertTrue("Expected custom integer serialization", json.contains("Number: 42"));
 }

 @Test
 public void testPOJONodeSameSerializerForMultipleTypes() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     SimpleModule mod = new SimpleModule();
     mod.addSerializer(MyBase.class, new MyBaseSerializer());
     mapper.registerModule(mod);

     String jsonA = mapper.writeValueAsString(new POJONode(new MyDerivedA()));
     String jsonB = mapper.writeValueAsString(new POJONode(new MyDerivedB()));
     // Both should use the same registered serializer for the base type.
     assertTrue(jsonA.contains("base-serialized"));
     assertTrue(jsonB.contains("base-serialized"));
 }

 @Test
 public void testAsText() {
     POJONode node = new POJONode(new MyPojo("Hi"));
     // Default asText uses _value.toString()
     assertEquals("Hello!", node.asText());
 }

 @Test
 public void testAsTextNull() {
     POJONode node = new POJONode(null);
     assertEquals("null", node.asText());
 }

 @Test
 public void testAsTextDefaultValue() {
     POJONode node = new POJONode(null);
     assertEquals("default", node.asText("default"));
 }

 @Test
 public void testAsBoolean() {
     POJONode trueNode = new POJONode(Boolean.TRUE);
     POJONode falseNode = new POJONode(Boolean.FALSE);
     POJONode nonBool = new POJONode("xyz");

     assertTrue(trueNode.asBoolean(false));
     assertFalse(falseNode.asBoolean(true));
     // default value used when _value not Boolean
     assertTrue(nonBool.asBoolean(true));
     assertFalse(nonBool.asBoolean(false));
 }

 @Test
 public void testAsInt() {
     POJONode intNode = new POJONode(Integer.valueOf(42));
     POJONode doubleNode = new POJONode(Double.valueOf(3.14));
     POJONode nonNum = new POJONode("abc");

     assertEquals(42, intNode.asInt(-1));
     assertEquals(3, doubleNode.asInt(-1)); // truncation
     assertEquals(-1, nonNum.asInt(-1)); // default
 }

 @Test
 public void testAsLong() {
     POJONode longNode = new POJONode(Long.valueOf(123456789L));
     POJONode nonNum = new POJONode(null);

     assertEquals(123456789L, longNode.asLong(-1L));
     assertEquals(-1L, nonNum.asLong(-1L));
 }

 @Test
 public void testAsDouble() {
     POJONode doubleNode = new POJONode(Double.valueOf(2.5));
     POJONode nonNum = new POJONode(null);

     assertEquals(2.5, doubleNode.asDouble(-1.0), 0.0);
     assertEquals(-1.0, nonNum.asDouble(-1.0), 0.0);
 }

 @Test
 public void testBinaryValue() throws IOException {
     byte[] data = new byte[] { 1, 2, 3 };
     POJONode node = new POJONode(data);
     assertArrayEquals(data, node.binaryValue());
 }

 @Test
 public void testEqualsHashCode() {
     POJONode a = new POJONode("test");
     POJONode b = new POJONode("test");
     POJONode c = new POJONode("other");

     assertEquals(a, b);
     assertNotEquals(a, c);
     assertEquals(a.hashCode(), b.hashCode());
 }

}
