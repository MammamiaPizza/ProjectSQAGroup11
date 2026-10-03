package com.fasterxml.jackson.databind.deser;

 import static org.junit.Assert.*;
 import org.junit.Test;
 import com.fasterxml.jackson.annotation.*;
 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.exc.*;
 import com.fasterxml.jackson.core.*;

 /**
  * Tests for {@link BeanDeserializer} with external type id and property-based creator.
  * Covers bug #928 where CreatorProperty without fallback setter caused IllegalStateException.
  */
 public class TestBeanDeserializerCreatorExternalId {

     // ---------- polymorphic base types ---------
     @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property
= "type")
     interface Animal {}

     static class Dog implements Animal {
         public String name;
     }

     static class Cat implements Animal {
         public int lives;
     }

     // ---------- bean with creator that also has the type id property ---------
     static class BeanWithTypeIdInCreator {
         private final String type;
         private final Animal animal;

         @JsonCreator
         public BeanWithTypeIdInCreator(@JsonProperty("type") String type,
                                        @JsonProperty("animal") Animal animal) {
             this.type = type;
             this.animal = animal;
         }

         public String getType() { return type; }
         public Animal getAnimal() { return animal; }
     }

     // ---------- bean where the polymorphic value is a creator property but type id is NOT
---------
     static class BeanWithValueInCreator {
         private final Animal animal;
         private String type; // regular setter

         @JsonCreator
         public BeanWithValueInCreator(@JsonProperty("animal") Animal animal) {
             this.animal = animal;
         }

         public Animal getAnimal() { return animal; }

         public void setType(String type) { this.type = type; }
         public String getType() { return type; }
     }

     private ObjectMapper mapper() {
         ObjectMapper m = new ObjectMapper();
         m.registerSubtypes(Dog.class, Cat.class);
         return m;
     }

     // 1) normal: external type id + property-based creator, type id property is part of creator
     // This is the scenario that triggers bug #928.
     @Test
     public void testExternalIdWithCreatorTypeId() throws Exception {
         ObjectMapper mapper = mapper();
         String json = "{\"type\":\"Dog\",\"animal\":{\"name\":\"Fido\"}}";
         BeanWithTypeIdInCreator bean = mapper.readValue(json, BeanWithTypeIdInCreator.class);
         assertNotNull(bean);
         assertEquals("Dog", bean.getType());
         assertTrue("Expected Dog instance", bean.getAnimal() instanceof Dog);
         assertEquals("Fido", ((Dog) bean.getAnimal()).name);
     }

     // 2) external type id with property-based creator where value is creator param, type id is
setter
     @Test
     public void testExternalIdWithCreatorValue() throws Exception {
         ObjectMapper mapper = mapper();
         String json = "{\"type\":\"Cat\",\"animal\":{\"lives\":9}}";
         BeanWithValueInCreator bean = mapper.readValue(json, BeanWithValueInCreator.class);
         assertNotNull(bean);
         assertEquals("Cat", bean.getType());
         assertTrue(bean.getAnimal() instanceof Cat);
         assertEquals(9, ((Cat) bean.getAnimal()).lives);
     }

     // 3) order: type id appears AFTER the external value property
     @Test
     public void testExternalIdTypeAfterValue() throws Exception {
         ObjectMapper mapper = mapper();
         String json = "{\"animal\":{\"name\":\"Rex\"},\"type\":\"Dog\"}";
         BeanWithTypeIdInCreator bean = mapper.readValue(json, BeanWithTypeIdInCreator.class);
         assertNotNull(bean);
         assertEquals("Dog", bean.getType());
         assertTrue(bean.getAnimal() instanceof Dog);
         assertEquals("Rex", ((Dog) bean.getAnimal()).name);
     }

     // 4) missing type id property should fail
     @Test(expected = JsonMappingException.class)
     public void testExternalIdMissingType() throws Exception {
         ObjectMapper mapper = mapper();
         String json = "{\"animal\":{\"name\":\"Fido\"}}";
         mapper.readValue(json, BeanWithTypeIdInCreator.class);
     }

     // 5) all creator arguments provided, including external-type-id handled property
     @Test
     public void testAllCreatorPropsProvided() throws Exception {
         ObjectMapper mapper = mapper();
         String json = "{\"type\":\"Dog\",\"animal\":{\"name\":\"Bolt\"}}";
         BeanWithTypeIdInCreator bean = mapper.readValue(json, BeanWithTypeIdInCreator.class);
         assertEquals("Dog", bean.getType());
         assertEquals("Bolt", ((Dog) bean.getAnimal()).name);
     }

     // 6) type id property is a creator property but there is also a regular setter for it
(fallback exists)
     // (This should work even without the fix, but validates no regression.)
     @Test
     public void testExternalIdCreatorWithFallbackSetter() throws Exception {
         ObjectMapper mapper = mapper();
         // use a variant of BeanWithTypeIdInCreator but with setter? We'll add inner class below
     }

     // Helper: bean with creator AND setter (fallback) for type
     static class BeanWithFallback {
         private String type;
         private final Animal animal;

         @JsonCreator
         public BeanWithFallback(@JsonProperty("type") String type,
                                 @JsonProperty("animal") Animal animal) {
             this.type = type;
             this.animal = animal;
         }

         public void setType(String type) { this.type = type; }
         public String getType() { return type; }
         public Animal getAnimal() { return animal; }
     }

     @Test
     public void testExternalIdCreatorWithFallbackSetter() throws Exception {
         ObjectMapper mapper = mapper();
         String json = "{\"type\":\"Cat\",\"animal\":{\"lives\":5}}";
         BeanWithFallback bean = mapper.readValue(json, BeanWithFallback.class);
         assertNotNull(bean);
         assertEquals("Cat", bean.getType());
         assertTrue(bean.getAnimal() instanceof Cat);
         assertEquals(5, ((Cat) bean.getAnimal()).lives);
     }
 }