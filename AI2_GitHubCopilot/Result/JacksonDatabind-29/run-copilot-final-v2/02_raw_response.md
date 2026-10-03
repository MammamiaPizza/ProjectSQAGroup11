package com.fasterxml.jackson.databind.jsontype;

 import static org.junit.Assert.*;

 import org.junit.Test;

 import com.fasterxml.jackson.annotation.JsonSubTypes;
 import com.fasterxml.jackson.annotation.JsonTypeInfo;
 import com.fasterxml.jackson.annotation.JsonTypeName;
 import com.fasterxml.jackson.core.JsonProcessingException;
 import com.fasterxml.jackson.databind.DeserializationFeature;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
 import com.fasterxml.jackson.databind.exc.MismatchedInputException;
 import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

 /**
  * JUnit tests exposing the bug in ExternalTypeHandler related to VALUE_NULL token handling
  * for external type id properties. Tests cover the complete() method paths for null,
  * missing, empty, and valid type id values.
  */
 public class ExternalTypeHandlerTest {

     // -- Test beans with external type id polymorphism --

     @JsonTypeInfo(
             use = JsonTypeInfo.Id.NAME,
             include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
             property = "type",
             defaultImpl = DefaultItem.class
     )
     @JsonSubTypes({
             @JsonSubTypes.Type(value = TextItem.class, name = "text"),
             @JsonSubTypes.Type(value = NumberItem.class, name = "number")
     })
     public static class Item {
         public String name;
     }

     @JsonTypeName("text")
     public static class TextItem extends Item {
         public String content;
     }

     @JsonTypeName("number")
     public static class NumberItem extends Item {
         public int value;
     }

     public static class DefaultItem extends Item {
         public String info;
     }

     // -- Without defaultImpl --

     @JsonTypeInfo(
             use = JsonTypeInfo.Id.NAME,
             include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
             property = "kind"
     )
     @JsonSubTypes({
             @JsonSubTypes.Type(value = Dog.class, name = "dog"),
             @JsonSubTypes.Type(value = Cat.class, name = "cat")
     })
     public static class Pet {
     }

     @JsonTypeName("dog")
     public static class Dog extends Pet {
         public String breed;
     }

     @JsonTypeName("cat")
     public static class Cat extends Pet {
         public boolean indoor;
     }

     // -- Fields for testing property-based creator with external types --

     @JsonTypeInfo(
             use = JsonTypeInfo.Id.NAME,
             include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
             property = "creatType",
             defaultImpl = DefaultCreat.class
     )
     @JsonSubTypes({
             @JsonSubTypes.Type(value = StdCreat.class, name = "std")
     })
     public static class CreatorBean {
         private final int id;
         private final String label;

         public CreatorBean(int id, String label) {
             this.id = id;
             this.label = label;
         }

         public int getId() { return id; }
         public String getLabel() { return label; }
     }

     public static class DefaultCreat extends CreatorBean {
         public DefaultCreat() { super(0, ""); }
     }

     @JsonTypeName("std")
     public static class StdCreat extends CreatorBean {
         public StdCreat() { super(0, ""); }
     }

     private final ObjectMapper mapper = new ObjectMapper()
             .configure(DeserializationFeature.FAIL_ON_UNKOWN_PROPERTIES, false);

     // ------------------------------------------------------------------------
     // 1. null type property with defaultImpl → should deserialize as default type
     //    Buggy version treats JSON null as string "null", causing type resolution failure
     // ------------------------------------------------------------------------
     @Test
     public void testNullTypeIdWithDefaultImpl() throws Exception {
         String json = "{ \"type\": null, \"name\": \"x\", \"info\": \"default\" }";
         Item item = mapper.readValue(json, Item.class);
         assertNotNull(item);
         assertTrue("Expected DefaultItem but got " + item.getClass().getSimpleName(),
                 item instanceof DefaultItem);
         DefaultItem d = (DefaultItem) item;
         assertEquals("x", d.name);
         assertEquals("default", d.info);
     }

     // ------------------------------------------------------------------------
     // 2. null type property without defaultImpl → should fail with meaningful error
     //    (buggy version fails with type resolution error on "null")
     // ------------------------------------------------------------------------
     @Test(expected = JsonProcessingException.class)
     public void testNullTypeIdWithoutDefaultImpl() throws Exception {
         String json = "{ \"kind\": null, \"breed\": \"poodle\" }";
         mapper.readValue(json, Pet.class); // "null" is not a valid subtype id
     }

     // ------------------------------------------------------------------------
     // 3. empty string type id with defaultImpl → should throw (empty string not a valid id)
     // ------------------------------------------------------------------------
     @Test(expected = JsonProcessingException.class)
     public void testEmptyStringTypeId() throws Exception {
         String json = "{ \"type\": \"\", \"name\": \"y\" }";
         mapper.readValue(json, Item.class);
     }

     // ------------------------------------------------------------------------
     // 4. missing type property with value present → use defaultImpl (if available)
     // ------------------------------------------------------------------------
     @Test
     public void testMissingTypePropertyWithDefaultImpl() throws Exception {
         String json = "{ \"name\": \"z\", \"info\": \"auto\" }";
         Item item = mapper.readValue(json, Item.class);
         assertTrue(item instanceof DefaultItem);
         DefaultItem d = (DefaultItem) item;
         assertEquals("z", d.name);
         assertEquals("auto", d.info);
     }

     // ------------------------------------------------------------------------
     // 5. missing type property with value present but no defaultImpl → should fail
     // ------------------------------------------------------------------------
     @Test(expected = JsonProcessingException.class)
     public void testMissingTypePropertyWithoutDefaultImpl() throws Exception {
         String json = "{ \"breed\": \"beagle\" }";
         mapper.readValue(json, Pet.class);
     }

     // ------------------------------------------------------------------------
     // 6. valid string type id → normal deserialization
     // ------------------------------------------------------------------------
     @Test
     public void testValidStringTypeId() throws Exception {
         String json = "{ \"type\": \"text\", \"name\": \"hello\", \"content\": \"world\" }";
         Item item = mapper.readValue(json, Item.class);
         assertTrue(item instanceof TextItem);
         TextItem t = (TextItem) item;
         assertEquals("hello", t.name);
         assertEquals("world", t.content);
     }

     // ------------------------------------------------------------------------
     // 7. numeric type id with defaultImpl → should resolve normally (via NAME)
     // ------------------------------------------------------------------------
     @Test
     public void testNumericTypeId() throws Exception {
         String json = "{ \"type\": \"number\", \"name\": \"n\", \"value\": 42 }";
         Item item = mapper.readValue(json, Item.class);
         assertTrue(item instanceof NumberItem);
         NumberItem n = (NumberItem) item;
         assertEquals("n", n.name);
         assertEquals(42, n.value);
     }

     // ------------------------------------------------------------------------
     // 8. missing value property with type present → should throw (missing property for
     //    external type id)
     // ------------------------------------------------------------------------
     @Test(expected = JsonProcessingException.class)
     public void testMissingValueProperty() throws Exception {
         String json = "{ \"type\": \"text\", \"name\": \"incomplete\" }"; // "content" missing
         mapper.readValue(json, TextItem.class); // Will throw because "content" is missing,
         // but the exception may occur during deserialization.
         // We expect any JsonProcessingException.
     }

     // ------------------------------------------------------------------------
     // 9. both type and value properties missing → should skip property entirely
     //    (f all other fields can be set, result is defaultImpl)
     // ------------------------------------------------------------------------
     @Test
     public void testMissingBothTypeAndValue() throws Exception {
         String json = "{ }";
         Item item = mapper.readValue(json, Item.class);
         assertTrue(item instanceof DefaultItem);
     }

     // ------------------------------------------------------------------------
     // 10. null type property with value missing but defaultImpl → should skip
     //     (buggy: typeId set to "null", but property missing may trigger different path)
     // ------------------------------------------------------------------------
     @Test
     public void testNullTypeWithMissingValueAndDefaultImpl() throws Exception {
         String json = "{ \"type\": null }"; // name missing, info missing
         Item item = mapper.readValue(json, Item.class);
         assertTrue(item instanceof DefaultItem);
     }

     // ------------------------------------------------------------------------
     // 11. valid id with extra unknown property → should ignore unknown and succeed
     // ------------------------------------------------------------------------
     @Test
     public void testExtraUnknownPropertyIgnored() throws Exception {
         String json = "{ \"type\": \"number\", \"name\": \"n2\", \"value\": 99, \"extra\": true }";
         Item item = mapper.readValue(json, Item.class);
         assertTrue(item instanceof NumberItem);
         assertEquals(99, ((NumberItem) item).value);
     }

     // ------------------------------------------------------------------------
     // 12. type property appears after value property → still works
     //      (order independence)
     // ------------------------------------------------------------------------
     @Test
     public void testTypeAfterValueProperty() throws Exception {
         String json = "{ \"name\": \"late\", \"content\": \"text\", \"type\": \"text\" }";
         Item item = mapper.readValue(json, Item.class);
         assertTrue(item instanceof TextItem);
         assertEquals("text", ((TextItem) item).content);
     }
 }