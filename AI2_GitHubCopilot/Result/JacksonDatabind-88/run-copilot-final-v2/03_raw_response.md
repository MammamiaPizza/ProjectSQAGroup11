package com.fasterxml.jackson.databind.jsontype.impl;

 import static org.junit.Assert.*;
 import static org.hamcrest.CoreMatchers.*;

 import com.fasterxml.jackson.annotation.JsonTypeInfo;
 import com.fasterxml.jackson.databind.*;
 import org.junit.Rule;
 import org.junit.Test;
 import org.junit.rules.ExpectedException;

 public class ClassNameIdResolverTest {

     @Rule
     public ExpectedException thrown = ExpectedException.none();

     // ---- Helper types for subtype checks ----

     static class Base { }
     static class Sub extends Base { }
     static class Unrelated { }

     static class BaseWrapper {
         @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
         public Base value;
     }

     static class NumberWrapper {
         @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
         public Number value;
     }

     static class CharSequenceWrapper {
         @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
         public CharSequence value;
     }

     // custom Number subclass with public property for valid-subtype tests
     static class Num extends Number {
         private static final long serialVersionUID = 1L;
         public int value;
         @Override public int intValue() { return value; }
         @Override public long longValue() { return value; }
         @Override public float floatValue() { return value; }
         @Override public double doubleValue() { return value; }
     }

     // custom CharSequence implementation with public property
     static class MyCharSequence implements CharSequence {
         public String value;
         @Override public int length() { return value == null ? 0 : value.length(); }
         @Override public char charAt(int index) { return value.charAt(index); }
         @Override public CharSequence subSequence(int start, int end) { return
value.subSequence(start, end); }
         @Override public String toString() { return value; }
     }

     // ---- Tests: non-subtype class ids ----

     @Test
     public void testNonSubtypeClassId() throws Exception {
         thrown.expect(JsonMappingException.class);
         thrown.expectMessage(containsString("not subtype of"));
         ObjectMapper mapper = new ObjectMapper();
         String json = "{\"value\":{\"@class\":\"java.util.HashMap\"}}";
         mapper.readValue(json, BaseWrapper.class);
     }

     @Test
     public void testUnrelatedClassId() throws Exception {
         thrown.expect(JsonMappingException.class);
         thrown.expectMessage(containsString("not subtype of"));
         ObjectMapper mapper = new ObjectMapper();
         String json = "{\"value\":{\"@class\":\"" + Unrelated.class.getName() + "\"}}";
         mapper.readValue(json, BaseWrapper.class);
     }

     @Test
     public void testNonSubtypeWithNumberBase() throws Exception {
         thrown.expect(JsonMappingException.class);
         ObjectMapper mapper = new ObjectMapper();
         String json = "{\"value\":{\"@class\":\"java.lang.String\"}}";
         mapper.readValue(json, NumberWrapper.class);
     }

     @Test
     public void testNonSubtypeGenericId() throws Exception {
         thrown.expect(JsonMappingException.class);
         ObjectMapper mapper = new ObjectMapper();
         String json = "{\"value\":{\"@class\":\"java.util.ArrayList<java.util.HashMap>\"}}";
         mapper.readValue(json, BaseWrapper.class);
     }

     // ---- Tests: valid subtype class ids ----

     @Test
     public void testValidSubtypeClassId() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         String json = "{\"value\":{\"@class\":\"" + Sub.class.getName() + "\"}}";
         BaseWrapper result = mapper.readValue(json, BaseWrapper.class);
         assertNotNull("value should not be null", result.value);
         assertTrue("value should be instance of Sub", result.value instanceof Sub);
     }

     @Test
     public void testBaseTypeItselfAsId() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         String json = "{\"value\":{\"@class\":\"" + Base.class.getName() + "\"}}";
         BaseWrapper result = mapper.readValue(json, BaseWrapper.class);
         assertNotNull("value should not be null", result.value);
         assertTrue("value should be instance of Base", result.value instanceof Base);
     }

     @Test
     public void testValidSubtypeForNumber() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         String json = "{\"value\":{\"@class\":\"" + Num.class.getName() + "\",\"value\":42}}";
         NumberWrapper result = mapper.readValue(json, NumberWrapper.class);
         assertNotNull("value should not be null", result.value);
         assertTrue(result.value instanceof Num);
     }

     @Test
     public void testValidSubtypeForInterface() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         String json = "{\"value\":{\"@class\":\"" + MyCharSequence.class.getName() +
"\",\"value\":\"hello\"}}";
         CharSequenceWrapper result = mapper.readValue(json, CharSequenceWrapper.class);
         assertNotNull("value should not be null", result.value);
         assertTrue(result.value instanceof MyCharSequence);
     }

     // ---- Tests: unknown / invalid class names ----

     @Test
     public void testUnknownClassId() throws Exception {
         thrown.expect(JsonMappingException.class);
         thrown.expectMessage(containsString("no such class found"));
         ObjectMapper mapper = new ObjectMapper();
         String json = "{\"value\":{\"@class\":\"com.example.NonExistent\"}}";
         mapper.readValue(json, BaseWrapper.class);
     }

     @Test
     public void testEmptyClassId() throws Exception {
         thrown.expect(JsonMappingException.class);
         ObjectMapper mapper = new ObjectMapper();
         String json = "{\"value\":{\"@class\":\"\"}}";
         mapper.readValue(json, BaseWrapper.class);
     }

     @Test
     public void testInvalidGenericSyntax() throws Exception {
         thrown.expect(JsonMappingException.class);
         ObjectMapper mapper = new ObjectMapper();
         String json = "{\"value\":{\"@class\":\"java.util.ArrayList<\"}}";
         mapper.readValue(json, BaseWrapper.class);
     }
  }