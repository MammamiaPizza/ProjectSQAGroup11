package com.fasterxml.jackson.databind.deser;

 import com.fasterxml.jackson.annotation.JsonCreator;
 import com.fasterxml.jackson.annotation.JsonProperty;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.PropertyNamingStrategy;
 import org.junit.Test;

 import static org.junit.Assert.*;

 /**
  * Tests for {@link BasicDeserializerFactory} handling of naming strategy
  * with {@link JsonCreator} annotated constructor parameters.
  * Bug: naming strategy was not applied to creator property names,
  * causing {@link com.fasterxml.jackson.databind.exc.InvalidDefinitionException}.
  */
 public class BasicDeserializerFactoryTest {

     // ---- Single-argument creator with SNAKE_CASE (the main bug trigger) ----

     @Test
     public void testSnakeCaseWithSingleArgCreator() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

         // Snake-case JSON key: "param_name0" -> should map to paramName0 via strategy
         SingleArgCreator bean = mapper.readValue("{\"param_name0\":42}",
                 SingleArgCreator.class);

         assertNotNull(bean);
         assertEquals(42, bean.getParamName0());
     }

     @Test
     public void testSnakeCaseWithMultiArgCreator() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

         // Snake-case keys for both params
         String json = "{\"first_param\":\"hello\",\"second_param\":\"world\"}";
         MultiArgCreator bean = mapper.readValue(json, MultiArgCreator.class);

         assertNotNull(bean);
         assertEquals("hello", bean.getFirstParam());
         assertEquals("world", bean.getSecondParam());
     }

     // ---- No naming strategy: default (implicit) names must be used ----

     @Test
     public void testDefaultNamingWithSingleArgCreator() throws Exception {
         ObjectMapper mapper = new ObjectMapper();

         // No strategy: JSON key must equal the implicit parameter name
         String json = "{\"value\":123}";
         SingleStringArgCreator bean = mapper.readValue(json, SingleStringArgCreator.class);

         assertNotNull(bean);
         assertEquals("123", bean.getValue());
     }

     // ---- @JsonProperty should override any naming strategy ----

     @Test
     public void testJsonPropertyOverridesStrategy() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

         // @JsonProperty("explicitName") used; strategy must not modify it
         String json = "{\"explicitName\":\"overridden\"}";
         JsonPropertyOverride bean = mapper.readValue(json, JsonPropertyOverride.class);

         assertNotNull(bean);
         assertEquals("overridden", bean.getName());
     }

     // ---- Boundary: creator with no parameters (empty JSON object) ----
     @Test
     public void testNoArgCreatorWithStrategy() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE);

         // No properties to map, just ensure no exception
         NoArgCreator bean = mapper.readValue("{}", NoArgCreator.class);
         assertNotNull(bean);
     }

     // ---- Error case: naming strategy ignored -> leads to InvalidDefinitionException ----
     @Test(expected = com.fasterxml.jackson.databind.exc.InvalidDefinitionException.class)
     public void testMissingPropertyDueToStrategyMismatch() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

         // This JSON uses the original (non-snake) name – it should fail because
         // after renaming the property is not found (strategy renames paramName0 -> param_name0).
         // When the fix is in place, this fails because "paramName0" is not a known property.
         // With the bug, the property-name lookup ignored the strategy, so this would have matched,
         // but we expect it to fail in either case to illustrate the misalignment.
         String json = "{\"paramName0\":42}";
         mapper.readValue(json, SingleArgCreator.class);
     }

     // ---- Helper value classes -----------------------------------------

     public static class SingleArgCreator {
         private final int paramName0;

         @JsonCreator
         public SingleArgCreator(int paramName0) {
             this.paramName0 = paramName0;
         }

         public int getParamName0() {
             return paramName0;
         }
     }

     public static class MultiArgCreator {
         private final String firstParam;
         private final String secondParam;

         @JsonCreator
         public MultiArgCreator(
                 @JsonProperty("firstParam") String firstParam,
                 @JsonProperty("secondParam") String secondParam) {
             this.firstParam = firstParam;
             this.secondParam = secondParam;
         }

         public String getFirstParam() { return firstParam; }
         public String getSecondParam() { return secondParam; }
     }

     public static class SingleStringArgCreator {
         private final String value;

         @JsonCreator
         public SingleStringArgCreator(String value) {
             this.value = value;
         }

         public String getValue() {
             return value;
         }
     }

     public static class JsonPropertyOverride {
         private final String name;

         @JsonCreator
         public JsonPropertyOverride(@JsonProperty("explicitName") String name) {
             this.name = name;
         }

         public String getName() {
             return name;
         }
     }

     public static class NoArgCreator {
         @JsonCreator
         public NoArgCreator() { }
     }
 }