package com.fasterxml.jackson.databind.util;

 import static org.junit.Assert.*;
 import org.junit.Test;

 import java.io.IOException;
 import java.util.Arrays;
 import java.util.List;

 import com.fasterxml.jackson.annotation.JsonIdentityInfo;
 import com.fasterxml.jackson.annotation.JsonTypeInfo;
 import com.fasterxml.jackson.annotation.ObjectIdGenerators;
 import com.fasterxml.jackson.core.*;
 import com.fasterxml.jackson.databind.*;

 public class TokenBufferBug2Test {

     private final ObjectMapper MAPPER = new ObjectMapper();

     @Test
     public void testWriteObjectWithSimpleBean() throws IOException {
         TokenBuffer tb = new TokenBuffer(MAPPER, false);
         tb.writeObject(new SimpleBean("Alice", 30));
         tb.close();

         JsonParser p = tb.asParser();
         assertNextToken(p, JsonToken.START_OBJECT);
         assertNextToken(p, JsonToken.FIELD_NAME);
         assertEquals("name", p.getCurrentName());
         assertNextToken(p, JsonToken.VALUE_STRING);
         assertEquals("Alice", p.getText());
         assertNextToken(p, JsonToken.FIELD_NAME);
         assertEquals("age", p.getCurrentName());
         assertNextToken(p, JsonToken.VALUE_NUMBER_INT);
         assertEquals(30, p.getIntValue());
         assertNextToken(p, JsonToken.END_OBJECT);
         assertNull(p.nextToken());
     }

     @Test
     public void testWriteObjectWithNestedBean() throws IOException {
         TokenBuffer tb = new TokenBuffer(MAPPER, false);
         tb.writeObject(new OutterBean("outer", new InnerBean("nested", 1)));
         tb.close();

         JsonParser p = tb.asParser();
         assertNextToken(p, JsonToken.START_OBJECT);
         assertNextToken(p, JsonToken.FIELD_NAME);
         assertEquals("outerField", p.getCurrentName());
         assertNextToken(p, JsonToken.VALUE_STRING);
         assertEquals("outer", p.getText());
         assertNextToken(p, JsonToken.FIELD_NAME);
         assertEquals("inner", p.getCurrentName());
         assertNextToken(p, JsonToken.START_OBJECT);
         assertNextToken(p, JsonToken.FIELD_NAME);
         assertEquals("innerField", p.getCurrentName());
         assertNextToken(p, JsonToken.VALUE_STRING);
         assertEquals("nested", p.getText());
         assertNextToken(p, JsonToken.FIELD_NAME);
         assertEquals("innerValue", p.getCurrentName());
         assertNextToken(p, JsonToken.VALUE_NUMBER_INT);
         assertEquals(1, p.getIntValue());
         assertNextToken(p, JsonToken.END_OBJECT);
         assertNextToken(p, JsonToken.END_OBJECT);
         assertNull(p.nextToken());
     }

     @Test
     public void testWriteObjectWithListField() throws IOException {
         TokenBuffer tb = new TokenBuffer(MAPPER, false);
         ListBean bean = new ListBean();
         bean.items = Arrays.asList("one", "two", "three");
         tb.writeObject(bean);
         tb.close();

         JsonParser p = tb.asParser();
         assertNextToken(p, JsonToken.START_OBJECT);
         assertNextToken(p, JsonToken.FIELD_NAME);
         assertEquals("items", p.getCurrentName());
         assertNextToken(p, JsonToken.START_ARRAY);
         assertNextToken(p, JsonToken.VALUE_STRING);
         assertEquals("one", p.getText());
         assertNextToken(p, JsonToken.VALUE_STRING);
         assertEquals("two", p.getText());
         assertNextToken(p, JsonToken.VALUE_STRING);
         assertEquals("three", p.getText());
         assertNextToken(p, JsonToken.END_ARRAY);
         assertNextToken(p, JsonToken.END_OBJECT);
         assertNull(p.nextToken());
     }

     @Test
     public void testWriteObjectWithNullProperty() throws IOException {
         TokenBuffer tb = new TokenBuffer(MAPPER, false);
         tb.writeObject(new SimpleBean(null, 42));
         tb.close();

         JsonParser p = tb.asParser();
         assertNextToken(p, JsonToken.START_OBJECT);
         assertNextToken(p, JsonToken.FIELD_NAME);
         assertEquals("name", p.getCurrentName());
         assertNextToken(p, JsonToken.VALUE_NULL);
         assertNextToken(p, JsonToken.FIELD_NAME);
         assertEquals("age", p.getCurrentName());
         assertNextToken(p, JsonToken.VALUE_NUMBER_INT);
         assertEquals(42, p.getIntValue());
         assertNextToken(p, JsonToken.END_OBJECT);
         assertNull(p.nextToken());
     }

     @Test
     public void testWriteObjectWithEmptyBean() throws IOException {
         TokenBuffer tb = new TokenBuffer(MAPPER, false);
         tb.writeObject(new EmptyBean());
         tb.close();

         JsonParser p = tb.asParser();
         assertNextToken(p, JsonToken.VALUE_EMBEDDED_OBJECT);
         assertTrue(p.getEmbeddedObject() instanceof EmptyBean);
         assertNull(p.nextToken());
     }

     @Test
     public void testWriteObjectWithPolymorphicType() throws IOException {
         TokenBuffer tb = new TokenBuffer(MAPPER, false);
         Circle circle = new Circle(5);
         tb.writeObject(circle);
         tb.close();

         JsonParser p = tb.asParser();
         assertNextToken(p, JsonToken.START_OBJECT);
         assertNextToken(p, JsonToken.FIELD_NAME);
         assertEquals("@class", p.getCurrentName());
         assertNextToken(p, JsonToken.VALUE_STRING);
         assertEquals(Circle.class.getName(), p.getText());
         assertNextToken(p, JsonToken.FIELD_NAME);
         assertEquals("radius", p.getCurrentName());
         assertNextToken(p, JsonToken.VALUE_NUMBER_INT);
         assertEquals(5, p.getIntValue());
         assertNextToken(p, JsonToken.END_OBJECT);
         assertNull(p.nextToken());
     }

     @Test
     public void testWriteObjectWithObjectId() throws IOException {
         TokenBuffer tb = new TokenBuffer(MAPPER, false);
         ObjectIdBean bean = new ObjectIdBean(101, "data");
         tb.writeObject(bean);
         tb.close();

         JsonParser p = tb.asParser();
         assertNextToken(p, JsonToken.START_OBJECT);
         assertNextToken(p, JsonToken.FIELD_NAME);
         assertEquals("id", p.getCurrentName());
         assertNextToken(p, JsonToken.VALUE_NUMBER_INT);
         assertEquals(101, p.getIntValue());
         assertNextToken(p, JsonToken.FIELD_NAME);
         assertEquals("value", p.getCurrentName());
         assertNextToken(p, JsonToken.VALUE_STRING);
         assertEquals("data", p.getText());
         assertNextToken(p, JsonToken.END_OBJECT);
         assertNull(p.nextToken());
     }

     @Test
     public void testWriteLargeArray() throws IOException {
         TokenBuffer tb = new TokenBuffer(MAPPER, false);
         tb.writeStartArray();
         final int COUNT = 50;
         for (int i = 0; i < COUNT; i++) {
             tb.writeNumber(i);
         }
         tb.writeEndArray();
         tb.close();

         JsonParser p = tb.asParser();
         assertNextToken(p, JsonToken.START_ARRAY);
         for (int i =0; i < COUNT; i++) {
             assertNextToken(p, JsonToken.VALUE_NUMBER_INT);
             assertEquals(i, p.getIntValue());
         }
         assertNextToken(p, JsonToken.END_ARRAY);
         assertNull(p.nextToken());
     }

     @Test
     public void testWriteObjectDeepNesting() throws IOException {
         TokenBuffer tb = new TokenBuffer(MAPPER, false);
         DeepOuter outer = DeepOuter.create(5);
         tb.writeObject(outer);
         tb.close();

         JsonParser p = tb.asParser();
         assertNextToken(p, JsonToken.VALUE_EMBEDDED_OBJECT);
         Object embedded = p.getEmbeddedObject();
         assertTrue(embedded instanceof DeepOuter);
         DeepOuter result = (DeepOuter) embedded;
         DeepOuter cur = result;
         for (int i = 0; i < 5; i++) {
             assertNotNull(cur.child);
             cur = cur.child;
         }
         assertEquals("leaf", cur.value);
         assertNull(p.nextToken());
     }

     @Test
     public void testWriteNullObjectYieldsNullToken() throws IOException {
         TokenBuffer tb = new TokenBuffer(MAPPER, false);
         tb.writeObject(null);
         tb.close();

         JsonParser p = tb.asParser();
         assertNextToken(p, JsonToken.VALUE_NULL);
         assertNull(p.nextToken());
     }

     @Test
     public void testRoundtripThroughObjectMaper() throws IOException {
         SimpleBean original = new SimpleBean("Bob", 99);
         TokenBuffer tb = new TokenBuffer(MAPPER, false);
         tb.writeObject(original);
         tb.close();

         JsonParser p = tb.asParser();
         ObjectMapper mapper = new ObjectMapper();
         SimpleBean result = mapper.readValue(p, SimpleBean.class);
         assertEquals(original.name, result.name);
         assertEquals(original.age, result.age);
     }

     // Helpers
     private void assertNextToken(JsonParser p, JsonToken expected) throws IOException {
         assertNotNull("Expected token " + expected + " but reached null", p.nextToken());
         assertEquals(expected, p.getCurrentToken());
     }

     // Test data classes
     public static class SimpleBean {
         public String name;
         public int age;
         public SimpleBean() {}
         public SimpleBean(String name, int age) {
             this.name = name;
             this.age = age;
         }
     }

     public static class InnerBean {
         public String innerField;
         public int innerValue;
         public InnerBean() {}
         public InnerBean(String innerField, int innerValue) {
             this.innerField = innerField;
             this.innerValue = innerValue;
         }
     }

     public static class OutterBean {
         public String outerField;
         public InnerBean inner;
         public OutterBean() {}
         public OutterBean(String outerField, InnerBean inner) {
             this.outerField = outerField;
             this.inner = inner;
         }
     }

     public static class ListBean {
         public List<String> items;
     }

     public static class EmptyBean { }

     @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
     public interface Shape { }

     public static class Circle implements Shape {
         public int radius;
         public Circle() {}
         public Circle(int radius) { this.radius = radius; }
     }

     @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
     public static class ObjectIdBean {
         public int id;
         public String value;
         public ObjectIdBean() {}
         public ObjectIdBean(int id, String value) {
             this.id = id;
             this.value = value;
         }
     }

     public static class DeepOuter {
         public DeepOuter child;
         public String value;
         public static DeepOuter create(int depth) {
             DeepOuter root = new DeepOuter();
             DeepOuter cur = root;
             for (int i =0; i < depth; i++) {
                 cur.child = new DeepOuter();
                 cur = cur.child;
             }
             cur.value = "leaf";
             return root;
         }
     }
 }