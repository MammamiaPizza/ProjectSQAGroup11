package com.fasterxml.jackson.databind.ser.std;

 import com.fasterxml.jackson.annotation.JsonFormat;
 import com.fasterxml.jackson.annotation.JsonProperty;
 import com.fasterxml.jackson.core.JsonProcessingException;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.SerializationFeature;
 import org.junit.Test;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;

 public class EnumSerializerBug75Test {

     enum Color { RED, GREEN, BLUE }

     // Helper POJOs with annotated enum properties
     static class NumberShapeWrapper {
         @JsonFormat(shape = JsonFormat.Shape.NUMBER)
         public Color color;

         NumberShapeWrapper() {}
         NumberShapeWrapper(Color color) { this.color = color; }
     }

     static class StringShapeWrapper {
         @JsonFormat(shape = JsonFormat.Shape.STRING)
         public Color color;

         StringShapeWrapper() {}
         StringShapeWrapper(Color color) { this.color = color; }
     }

     static class DefaultShapeWrapper {
         public Color color;

         DefaultShapeWrapper() {}
         DefaultShapeWrapper(Color color) { this.color = color; }
     }

     @JsonFormat(shape = JsonFormat.Shape.NUMBER)
     enum NumberAnnotatedEnum { X, Y, Z }

     @JsonFormat(shape = JsonFormat.Shape.STRING)
     enum StringAnnotatedEnum { A, B, C }

     static class ArrayNumberShapeWrapper {
         @JsonFormat(shape = JsonFormat.Shape.NUMBER)
         public Color[] colors;

         ArrayNumberShapeWrapper() {}
         ArrayNumberShapeWrapper(Color[] colors) { this.colors = colors; }
     }

     static class ArrayStringShapeWrapper {
         @JsonFormat(shape = JsonFormat.Shape.STRING)
         public Color[] colors;

         ArrayStringShapeWrapper() {}
         ArrayStringShapeWrapper(Color[] colors) { this.colors = colors; }
     }

     static class NullColorWrapper {
         @JsonFormat(shape = JsonFormat.Shape.NUMBER)
         public Color color;

         NullColorWrapper() {}
     }

     private final ObjectMapper mapper = new ObjectMapper();

     // CORE REGRESSION: property annotated with Shape.NUMBER must produce ordinal
     @Test
     public void testEnumPropertyShapeNumber_shouldWriteOrdinal() throws Exception {
         String json = mapper.writeValueAsString(new NumberShapeWrapper(Color.GREEN));
         assertEquals("{\"color\":1}", json);
     }

     // Explicit Shape.STRING should produce name
     @Test
     public void testEnumPropertyShapeString_shouldWriteName() throws Exception {
         String json = mapper.writeValueAsString(new StringShapeWrapper(Color.GREEN));
         assertEquals("{\"color\":\"GREEN\"}", json);
     }

     // No annotation → default to name
     @Test
     public void testEnumPropertyDefault_shouldWriteName() throws Exception {
         String json = mapper.writeValueAsString(new DefaultShapeWrapper(Color.RED));
         assertEquals("{\"color\":\"RED\"}", json);
     }

     // Class-level Shape.NUMBER
     @Test
     public void testEnumClassLevelShapeNumber_shouldWriteOrdinal() throws Exception {
         String json = mapper.writeValueAsString(NumberAnnotatedEnum.Y);
         assertEquals("1", json);
     }

     // Class-level Shape.STRING
     @Test
     public void testEnumClassLevelShapeString_shouldWriteName() throws Exception {
         String json = mapper.writeValueAsString(StringAnnotatedEnum.B);
         assertEquals("\"B\"", json);
     }

     // Feature WRITE_ENUMS_USING_INDEX enabled (no annotation)
     @Test
     public void testEnumIndexFeatureEnabled_shouldWriteOrdinal() throws Exception {
         ObjectMapper indexMapper = new ObjectMapper();
         indexMapper.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);
         String json = indexMapper.writeValueAsString(new DefaultShapeWrapper(Color.BLUE));
         assertEquals("{\"color\":2}", json);
     }

     // Feature WRITE_ENUMS_USING_TO_STRING
     @Test
     public void testEnumToStringFeature_shouldWriteToString() throws Exception {
         ObjectMapper toStringMapper = new ObjectMapper();
         toStringMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
         String json = toStringMapper.writeValueAsString(new DefaultShapeWrapper(Color.RED));
         assertEquals("{\"color\":\"RED\"}", json); // toString() matches name() for simple enums
     }

     // Array of enums with Shape.NUMBER → array of ordinals
     @Test
     public void testArrayOfEnumsShapeNumber_shouldWriteOrdinalArray() throws Exception {
         String json = mapper.writeValueAsString(
                 new ArrayNumberShapeWrapper(new Color[]{Color.RED, Color.GREEN}));
         assertEquals("{\"colors\":[0,1]}", json);
     }

     // Array of enums with Shape.STRING → array of names
     @Test
     public void testArrayOfEnumsShapeString_shouldWriteNameArray() throws Exception {
         String json = mapper.writeValueAsString(
                 new ArrayStringShapeWrapper(new Color[]{Color.BLUE, Color.RED}));
         assertEquals("{\"colors\":[\"BLUE\",\"RED\"]}", json);
     }

     // Null enum value with shape annotation → null literal
     @Test
     public void testNullEnumProperty_shouldSerializeNull() throws Exception {
         String json = mapper.writeValueAsString(new NullColorWrapper());
         assertEquals("{\"color\":null}", json);
     }

     // Ensure property-level Shape.NUMBER takes precedence over class-level default
     @Test
     public void testPropertyOverrideClassLevelShape() throws Exception {
         // A wrapper where property has Shape.STRING even if the enum class had Shape.NUMBER?
         // We'll test opposite: property has NUMBER on a class-level STRING enum.
         // Not directly testable via annotations on class and property simultaneously
         // because `@JsonFormat` on class vs property may behave differently.
         // Instead, we test that feature enablement is overridden by explicit property shape
NUMBER.
         ObjectMapper indexEnabled = new ObjectMapper();
         indexEnabled.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);
         // Even though WRITE_ENUMS_USING_INDEX is enabled, explicit STRING shape should
         // take precedence.
         String json = indexEnabled.writeValueAsString(new StringShapeWrapper(Color.GREEN));
         assertEquals("{\"color\":\"GREEN\"}", json);
     }

     // Negative: unrecognized shape (like OBJECT on property) should throw? Not tested here.
     // Boundary: empty enum? Not testable without changing enum constants.
 }
