package com.fasterxml.jackson.databind.ser.std;

 import static org.junit.Assert.*;

 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Set;

 import org.junit.Test;

 import com.fasterxml.jackson.core.JsonGenerator;
 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.databind.JavaType;
 import com.fasterxml.jackson.databind.JsonNode;
 import com.fasterxml.jackson.databind.jsonFormatVisitors.*;

 public class NumberSerializerTest {

     @Test
     public void testGetSchemaBigDecimalIsNumber() {
         JsonNode node = new NumberSerializer(BigDecimal.class).getSchema(null, null);
         assertNotNull(node);
         assertEquals("number", node.get("type").asText());
     }

     @Test
     public void testGetSchemaBigIntegerIsInteger() {
         JsonNode node = new NumberSerializer(BigInteger.class).getSchema(null, null);
         assertNotNull(node);
         assertEquals("integer", node.get("type").asText());
     }

     @Test
     public void testGetSchemaLongIsNumber() {
         JsonNode node = new NumberSerializer(Long.class).getSchema(null, null);
         assertNotNull(node);
         assertEquals("number", node.get("type").asText());
     }

     @Test
     public void testGetSchemaIntegerIsNumber() {
         JsonNode node = new NumberSerializer(Integer.class).getSchema(null, null);
         assertNotNull(node);
         assertEquals("number", node.get("type").asText());
     }

     @Test
     public void testGetSchemaDoubleIsNumber() {
         JsonNode node = new NumberSerializer(Double.class).getSchema(null, null);
         assertNotNull(node);
         assertEquals("number", node.get("type").asText());
     }

     @Test
     public void testGetSchemaNumberInstanceIsNumber() {
         JsonNode node = NumberSerializer.instance.getSchema(null, null);
         assertNotNull(node);
         assertEquals("number", node.get("type").asText());
     }

     @Test
     public void testVisitorBigDecimalNumberTypeBigDecimal() throws Exception {
         RecordingWrapper wrapper = new RecordingWrapper();
         new NumberSerializer(BigDecimal.class).acceptJsonFormatVisitor(wrapper, null);
         assertTrue("expectNumberFormat should be called",
wrapper.calls.contains("expectNumberFormat"));
         assertEquals("numberType", JsonParser.NumberType.BIG_DECIMAL, wrapper.numberType);
     }

     @Test
     public void testVisitorBigIntegerNumberTypeBigInteger() throws Exception {
         RecordingWrapper wrapper = new RecordingWrapper();
         new NumberSerializer(BigInteger.class).acceptJsonFormatVisitor(wrapper, null);
         assertTrue("expectIntegerFormat should be called",
wrapper.calls.contains("expectIntegerFormat"));
         assertEquals("numberType", JsonParser.NumberType.BIG_INTEGER, wrapper.numberType);
     }

     @Test
     public void testVisitorLongCallsExpectNumberFormat() throws Exception {
         RecordingWrapper wrapper = new RecordingWrapper();
         new NumberSerializer(Long.class).acceptJsonFormatVisitor(wrapper, null);
         assertTrue("expectNumberFormat expected", wrapper.calls.contains("expectNumberFormat"));
         assertFalse("expectIntegerFormat unexpected",
wrapper.calls.contains("expectIntegerFormat"));
         assertNull("numberType should not be set", wrapper.numberType);
     }

     @Test
     public void testVisitorIntegerCallsExpectNumberFormat() throws Exception {
         RecordingWrapper wrapper = new RecordingWrapper();
         new NumberSerializer(Integer.class).acceptJsonFormatVisitor(wrapper, null);
         assertTrue("expectNumberFormat expected", wrapper.calls.contains("expectNumberFormat"));
         assertFalse("expectIntegerFormat unexpected",
wrapper.calls.contains("expectIntegerFormat"));
         assertNull("numberType should not be set", wrapper.numberType);
     }

     @Test
     public void testVisitorDoubleCallsExpectNumberFormat() throws Exception {
         RecordingWrapper wrapper = new RecordingWrapper();
         new NumberSerializer(Double.class).acceptJsonFormatVisitor(wrapper, null);
         assertTrue("expectNumberFormat expected", wrapper.calls.contains("expectNumberFormat"));
         assertFalse("expectIntegerFormat unexpected",
wrapper.calls.contains("expectIntegerFormat"));
         assertNull("numberType should not be set", wrapper.numberType);
     }

     @Test
     public void testVisitorNumberInstanceCallsExpectNumberFormat() throws Exception {
         RecordingWrapper wrapper = new RecordingWrapper();
         NumberSerializer.instance.acceptJsonFormatVisitor(wrapper, null);
         assertTrue("expectNumberFormat expected", wrapper.calls.contains("expectNumberFormat"));
         assertFalse("expectIntegerFormat unexpected",
wrapper.calls.contains("expectIntegerFormat"));
         assertNull("numberType should not be set", wrapper.numberType);
     }

     // ---------- helper classes ----------

     private static class RecordingWrapper extends JsonFormatVisitorWrapper.Base {
         final List<String> calls = new ArrayList<String>();
         JsonParser.NumberType numberType;

         RecordingWrapper() {
             super(null);
         }

         @Override
         public JsonNumberFormatVisitor expectNumberFormat(JavaType type) {
             calls.add("expectNumberFormat");
             return new JsonNumberFormatVisitor() {
                 @Override
                 public void numberType(JsonParser.NumberType nType) { numberType = nType; }

                 @Override
                 public void format(JsonGenerator.Feature... features) { /* no-op */ }
             };
         }

         @Override
         public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) {
             calls.add("expectIntegerFormat");
             return new JsonIntegerFormatVisitor() {
                 @Override
                 public void numberType(JsonParser.NumberType nType) { numberType = nType; }

                 @Override
                 public void format(JsonGenerator.Feature... features) { /* no-op */ }

                 @Override
                 public void enumTypes(Set<String> enums) { /* no-op */ }
             };
         }
     }
 }