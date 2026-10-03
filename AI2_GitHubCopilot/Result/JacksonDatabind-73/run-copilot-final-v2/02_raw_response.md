package com.fasterxml.jackson.databind.deser;

 import static org.junit.Assert.*;

 import java.io.IOException;

 import org.junit.Test;

 import com.fasterxml.jackson.annotation.JsonProperty;
 import com.fasterxml.jackson.annotation.JsonProperty.Access;
 import com.fasterxml.jackson.annotation.JsonIgnore;
 import com.fasterxml.jackson.core.JsonParseException;
 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.core.JsonProcessingException;
 import com.fasterxml.jackson.databind.DeserializationFeature;
 import com.fasterxml.jackson.databind.JsonMappingException;
 import com.fasterxml.jackson.databind.MapperFeature;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

 public class Bug935ReadWriteOnlyTest {

     private final ObjectMapper MAPPER = new ObjectMapper();

     // --- Read-only property: included in serialization, ignored silently on deserialization ---

     static class ReadOnlyBean {
         @JsonProperty(access = Access.READ_ONLY)
         public String getX() { return "readOnlyX"; }
         // no setter

         public String getY() { return "alwaysY"; }
         public void setY(String y) { }
     }

     @Test
     public void testReadOnlySerialized() throws JsonProcessingException {
         String json = MAPPER.writeValueAsString(new ReadOnlyBean());
         assertTrue("Serialized JSON should contain read-only property", json.contains("\"x\""));
         assertTrue(json.contains("\"y\""));
     }

     @Test
     public void testReadOnlyDeserializedIgnored() throws IOException {
         String json = "{\"x\":\"test\", \"y\":\"value\"}";
         ReadOnlyBean bean = null;
         try {
             bean = MAPPER.readValue(json, ReadOnlyBean.class);
         } catch (UnrecognizedPropertyException e) {
             fail("Read-only property should not cause UnrecognizedPropertyException during
deserialization: " + e.getMessage());
         }
         assertNotNull(bean);
         // The read-only property should be ignored; bean.getX() returns the default from getter
         assertEquals("readOnlyX", bean.getX());
         assertEquals("value", bean.getY());
     }

     // --- Write-only property: accepted on deserialization, omitted on serialization ---

     static class WriteOnlyBean {
         String x;

         public String getX() { return x; }
         @JsonProperty(access = Access.WRITE_ONLY)
         public void setX(String x) { this.x = x; }

         public String getY() { return "alwaysY"; }
         public void setY(String y) { }
     }

     @Test
     public void testWriteOnlyDeserialized() throws IOException {
         String json = "{\"x\":\"writeOnlyValue\", \"y\":\"test\"}";
         WriteOnlyBean bean = null;
         try {
             bean = MAPPER.readValue(json, WriteOnlyBean.class);
         } catch (UnrecognizedPropertyException e) {
             fail("Write-only property on deserialization should be recognized as known property");
         }
         assertNotNull(bean);
         assertEquals("writeOnlyValue", bean.getX());
         assertEquals("test", bean.getY());
     }

     @Test
     public void testWriteOnlySerializedIgnored() throws JsonProcessingException {
         WriteOnlyBean bean = new WriteOnlyBean();
         bean.setX("shouldNotAppear");
         String json = MAPPER.writeValueAsString(bean);
         assertTrue("Serialized JSON must contain normal property", json.contains("\"y\""));
         assertFalse("Serialized JSON must NOT contain write-only property",
json.contains("\"x\""));
     }

     // --- Mixed access on same property name ---

     static class MixedAccessBean {
         private String z;

         @JsonProperty(access = Access.READ_ONLY)
         public String getZ() { return z == null ? "READ_ONLY_DEFAULT" : z; }

         @JsonProperty(access = Access.WRITE_ONLY)
         public void setZ(String z) { this.z = z; }

         public String getY() { return "alwaysY"; }
         public void setY(String y) { }
     }

     @Test
     public void testMixedAccessSameProperty() throws IOException {
         MixedAccessBean bean;

         // Deserialization: should pick up the write-only setter, ignore the read-only getter
         String json = "{\"z\":\"writeValue\", \"y\":\"test\"}";
         try {
             bean = MAPPER.readValue(json, MixedAccessBean.class);
         } catch (UnrecognizedPropertyException e) {
             fail("Mixed access property should be recognized as known: " + e.getMessage());
         }
         assertNotNull(bean);
         assertEquals("writeValue", bean.getZ());
         assertEquals("test", bean.getY());

         // Serialization: should expose the read-only getter's value
         bean.setZ("another"); // new value still WRITE_ONLY on its accessor
         // But the read-only getter returns the field value (latest set)
         String output = MAPPER.writeValueAsString(bean);
         assertTrue(output.contains("\"z\""));
         assertTrue(output.contains("\"y\""));
         MixedAccessBean deserializedBack = MAPPER.readValue(output, MixedAccessBean.class);
         // After re-reading the serialized form (which contains z), the write-only setter should
get called again.
         assertEquals("another", deserializedBack.getZ());
     }

     // --- Truly unknown field must throw UnrecognizedPropertyException ---

     static class SimpleBean {
         public String name;
         public void setName(String name) { this.name = name; }
         public String getName() { return name; }
     }

     @Test
     public void testUnrecognizedFieldCausesException() {
         String json = "{\"unknownField\":\"value\"}";
         try {
             MAPPER.readValue(json, SimpleBean.class);
             fail("Expected UnrecognizedPropertyException for truly unknown field");
         } catch (UnrecognizedPropertyException e) {
             // expected
         } catch (IOException e) {
             fail("Expected UnrecognizedPropertyException but got " + e.getClass().getSimpleName());
         }
     }

     // --- Read-only property coexisting with other legit properties ---

     static class BeanWithReadOnlyAndNormal {
         @JsonProperty(access = Access.READ_ONLY)
         public String getReadOnlyProp() { return "staticValue"; }

         private String normal;
         public String getNormal() { return normal; }
         public void setNormal(String v) { this.normal = v; }
     }

     @Test
     public void testReadOnlyWithNormalProps() throws IOException {
         String json = "{\"readOnlyProp\":\"ignoredVal\", \"normal\":\"realVal\"}";
         BeanWithReadOnlyAndNormal bean = null;
         try {
             bean = MAPPER.readValue(json, BeanWithReadOnlyAndNormal.class);
         } catch (UnrecognizedPropertyException e) {
             fail("Read-only property must not flag UnrecognizedPropertyException for its
presence");
         }
         assertNotNull(bean);
         assertEquals("realVal", bean.getNormal());
         // read-only value unchanged
         assertNotNull(bean.getReadOnlyProp());
     }

     // --- Write-only property should not interfere with serialization of others ---

     static class BeanWithWriteOnlyAndNormal {
         private String secret;

         @JsonProperty(access = Access.WRITE_ONLY)
         public void setSecret(String s) { this.secret = s; }
         public String getSecret() { return secret; }

         private String visible;
         public String getVisible() { return visible; }
         public void setVisible(String v) { this.visible = v; }
     }

     @Test
     public void testWriteOnlySkipsSerialization() throws JsonProcessingException {
         BeanWithWriteOnlyAndNormal bean = new BeanWithWriteOnlyAndNormal();
         bean.setSecret("secretVal");
         bean.setVisible("visibleVal");
         String json = MAPPER.writeValueAsString(bean);
         assertTrue(json.contains("\"visible\""));
         assertFalse("Write-only property secret must not appear in serialized JSON",
json.contains("\"secret\""));
     }

     // --- Interaction with @JsonIgnore (ignore should take priority) ---

     static class IgnoredAndAccessBean {
         @JsonIgnore
         @JsonProperty(access = Access.READ_ONLY)
         public String getIgnored() { return "never"; }
     }

     @Test
     public void testIgnorePredecedesAccess() throws IOException {
         String json = "{\"ignored\":\"value\"}";
         IgnoredAndAccessBean bean = MAPPER.readValue(json, IgnoredAndAccessBean.class);
         // The property is ignored, so it should be skipped; getter returns constant anyway
         assertNotNull(bean);
         assertEquals("never", bean.getIgnored()); // getter still returns constant, no exception
thrown
         assertNull("No unrecognized property exception expected", null); // just ensuring no throw
     }

     // --- Boundary: multiple accessors (field + getter) with differing access ---

     static class MultiAccessorBean {
         @JsonProperty(access = Access.READ_ONLY)
         public String propField = "fieldInit";

         // inherits field's access? Actually field annotation may affect visibility
         public String getPropField() { return propField; }
         @JsonProperty(access = Access.WRITE_ONLY)
         public void setPropField(String v) { this.propField = v; }
     }

     @Test
     public void testMultipleAccessorsMixedAccess() throws IOException {
         // Deserialize: should use WRITE_ONLY setter, ignore READ_ONLY getter/field
         String json = "{\"propField\":\"newVal\"}";
         MultiAccessorBean bean = null;
         try {
             bean = MAPPER.readValue(json, MultiAccessorBean.class);
         } catch (UnrecognizedPropertyException e) {
             fail("Multiple accessors mixed access should still produce known property");
         }
         assertNotNull(bean);
         assertEquals("newVal", bean.propField);

         // Serialize: should expose READ_ONLY field/getter
         String out = MAPPER.writeValueAsString(bean);
         assertTrue(out.contains("\"propField\""));
     }

     // --- Inheritance scenario: base class has read-only property, subclass should still handle it
---

     static class Base {
         @JsonProperty(access = Access.READ_ONLY)
         public String getBaseProp() { return "base"; }
     }

     static class Derived extends Base {
         public String getName() { return "derived"; }
         public void setName(String n) { }
     }

     @Test
     public void testReadOnlyInheritance() throws IOException {
         String json = "{\"baseProp\":\"ignore\", \"name\":\"test\"}";
         Derived bean = null;
         try {
             bean = MAPPER.readValue(json, Derived.class);
         } catch (UnrecognizedPropertyException e) {
             fail("Inherited read-only property must not trigger UnrecognizedPropertyException");
         }
         assertNotNull(bean);
         assertEquals("base", bean.getBaseProp());
     }
 }