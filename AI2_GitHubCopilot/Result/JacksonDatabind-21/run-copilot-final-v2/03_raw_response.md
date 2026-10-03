package com.fasterxml.jackson.databind.introspect;

 import static org.junit.Assert.*;

 import org.junit.Test;

 import com.fasterxml.jackson.annotation.JsonProperty;
 import com.fasterxml.jackson.databind.*;

 /**
  * Tests for {@link JacksonAnnotationIntrospector} related to enum value resolution
  * when {@link JsonProperty} is used on enum constants (bug 677 / defect 21).
  */
 public class JacksonAnnotationIntrospectorEnumTest {

     // ---- Enums under test ------------------------------------------------
     public enum RenamedEnum {
         @JsonProperty("b") B,
         @JsonProperty("a") A
     }

     public enum PlainEnum {
         X, Y
     }

     public enum PartialRenamedEnum {
         @JsonProperty("low") L,
         M,
         @JsonProperty("high") H
     }

     public enum EmptyJsonPropertyEnum {
         @JsonProperty("") EMPTY,
         @JsonProperty("regular") REGULAR
     }

     public enum DuplicateJsonPropertyEnum {
         @JsonProperty("duplicate") FIRST,
         @JsonProperty("duplicate") SECOND
     }

     // ---- Helper ---------------------------------------------------------

     private ObjectMapper mapper = new ObjectMapper();

     // ---- Tests -----------------------------------------------------------

     @Test
     public void testDeserializationSingleRenamedValue() throws Exception {
         assertEquals(RenamedEnum.B, mapper.readValue("\"b\"", RenamedEnum.class));
         assertEquals(RenamedEnum.A, mapper.readValue("\"a\"", RenamedEnum.class));
     }

     @Test
     public void testDeserializationArrayOfRenamed() throws Exception {
         RenamedEnum[] result = mapper.readValue("[\"b\",\"a\"]", RenamedEnum[].class);
         assertArrayEquals(new RenamedEnum[]{RenamedEnum.B, RenamedEnum.A}, result);
     }

     @Test
     public void testDeserializationDefaultNamesStillWork() throws Exception {
         assertEquals(PlainEnum.X, mapper.readValue("\"X\"", PlainEnum.class));
         assertEquals(PlainEnum.Y, mapper.readValue("\"Y\"", PlainEnum.class));
     }

     @Test
     public void testDeserializationRenamedOnlyEffectiveForAnnotated() throws Exception {
         assertEquals(PartialRenamedEnum.L, mapper.readValue("\"low\"", PartialRenamedEnum.class));
         assertEquals(PartialRenamedEnum.M, mapper.readValue("\"M\"", PartialRenamedEnum.class));
         assertEquals(PartialRenamedEnum.H, mapper.readValue("\"high\"", PartialRenamedEnum.class));
     }

     @Test
     public void testDeserializationInvalidEnumValueThrows() {
         try {
             mapper.readValue("\"z\"", RenamedEnum.class);
             fail("Expected JsonMappingException for unknown enum value");
         } catch (JsonMappingException e) {
             // expected
         } catch (Exception e) {
             fail("Unexpected exception: " + e);
         }
     }

     @Test
     public void testDeserializationEnumWithEmptyJsonProperty() throws Exception {
         // empty string must be quoted as JSON string
         assertEquals(EmptyJsonPropertyEnum.EMPTY, mapper.readValue("\"\"",
                 EmptyJsonPropertyEnum.class));
     }

     @Test
     public void testSerializationRenamedEnumUsesPropertyAnnotation() throws Exception {
         // Serialization should also output @JsonProperty value
         String json = mapper.writeValueAsString(RenamedEnum.B);
         assertEquals("\"b\"", json);
         json = mapper.writeValueAsString(RenamedEnum.A);
         assertEquals("\"a\"", json);
     }
 }