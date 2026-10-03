package com.fasterxml.jackson.databind.introspect;

 import static org.junit.Assert.*;

 import java.util.Arrays;
 import java.util.List;

 import org.junit.Test;

 import com.fasterxml.jackson.annotation.JsonProperty;
 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.type.TypeFactory;

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

     private JacksonAnnotationIntrospector introspector = new JacksonAnnotationIntrospector();
     private ObjectMapper mapper = new ObjectMapper();

     private List<String> getEnumNames(Class<? extends Enum<?>> enumClass) {
         JavaType type = TypeFactory.defaultInstance().constructType(enumClass);
         // Use DeserializationConfig to introspect the type
         DeserializationConfig config = mapper.getDeserializationConfig();
         BeanDescription desc = config.introspect(type);
         AnnotatedClass ac = desc.getClassInfo();
         return introspector.findEnumValues(ac);
     }

     // ---- Tests -----------------------------------------------------------

     @Test
     public void testFindEnumValuesWithJsonProperty() {
         List<String> names = getEnumNames(RenamedEnum.class);
         assertEquals("Should return @JsonProperty values instead of constant names",
                 Arrays.asList("b", "a"), names);
     }

     @Test
     public void testFindEnumValuesNoAnnotation() {
         List<String> names = getEnumNames(PlainEnum.class);
         assertEquals("No annotations -> constant names",
                 Arrays.asList("X", "Y"), names);
     }

     @Test
     public void testFindEnumValuesEmptyJsonProperty() {
         List<String> names = getEnumNames(EmptyJsonPropertyEnum.class);
         // Empty string is a valid override
         assertEquals(Arrays.asList("", "regular"), names);
     }

     @Test
     public void testFindEnumValuesDuplicateJsonProperty() {
         List<String> names = getEnumNames(DuplicateJsonPropertyEnum.class);
         // Introspector does not de-duplicate; both entries expected
         assertEquals(Arrays.asList("duplicate", "duplicate"), names);
     }

     @Test
     public void testFindEnumValuesPartialRename() {
         List<String> names = getEnumNames(PartialRenamedEnum.class);
         assertEquals(Arrays.asList("low", "M", "high"), names);
     }

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
