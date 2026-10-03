package com.fasterxml.jackson.databind.ser;

 import static org.junit.Assert.*;

 import org.junit.Test;

 import com.fasterxml.jackson.annotation.JsonAnyGetter;
 import com.fasterxml.jackson.core.JsonProcessingException;
 import com.fasterxml.jackson.databind.JsonMappingException;
 import com.fasterxml.jackson.databind.ObjectMapper;

 import java.util.*;

 /**
  * Tests for Jackson-databind bug #705 (AnyGetterWriter not resolving / serializing map
incorrectly).
  */
 public class TestAnyGetterBug705 {

     private final ObjectMapper MAPPER = new ObjectMapper();

     private String toJson(Object value) throws JsonProcessingException {
         return MAPPER.writeValueAsString(value);
     }

     // ---- Test beans ----

     public static class SingleEntryBean {
         @JsonAnyGetter
         public Map<String, Object> getAny() {
             Map<String, Object> m = new LinkedHashMap<>();
             m.put("stuff", "key/value");
             return m;
         }
     }

     public static class MultiEntryBean {
         @JsonAnyGetter
         public Map<String, Object> getAny() {
             Map<String, Object> m = new LinkedHashMap<>();
             m.put("a", 1);
             m.put("b", "two");
             m.put("c", true);
             return m;
         }
     }

     public static class EmptyMapBean {
         @JsonAnyGetter
         public Map<String, Object> getAny() {
             return Collections.emptyMap();
         }
     }

     public static class NullValueBean {
         @JsonAnyGetter
         public Map<String, Object> getAny() {
             Map<String, Object> m = new LinkedHashMap<>();
             m.put("present", 42);
             m.put("notThere", null);
             return m;
         }
     }

     public static class NonStringKeyBean {
         @JsonAnyGetter
         public Map<Object, Object> getAny() {
             Map<Object, Object> m = new LinkedHashMap<>();
             m.put(123, "numberKey");
             m.put(true, "boolKey");
             return m;
         }
     }

     public static class MixedAccessorBean {
         private final String name;

         public MixedAccessorBean(String name) {
             this.name = name;
         }

         public String getName() {
             return name;
         }

         @JsonAnyGetter
         public Map<String, Object> getAdditional() {
             Map<String, Object> m = new LinkedHashMap<>();
             m.put("extra", "info");
             return m;
         }
     }

     public static class NullReturnBean {
         @JsonAnyGetter
         public Map<String, Object> getAny() {
             return null;
         }
     }

     // Bug reproduction: expected "stuff":"key/value", buggy produces "key":"value"
     @Test
     public void testIssue705() throws Exception {
         String json = toJson(new SingleEntryBean());
         assertEquals("{\"stuff\":\"key/value\"}", json);
     }

     @Test
     public void testMultipleEntries() throws Exception {
         String json = toJson(new MultiEntryBean());
         assertEquals("{\"a\":1,\"b\":\"two\",\"c\":true}", json);
     }

     @Test
     public void testEmptyMap() throws Exception {
         String json = toJson(new EmptyMapBean());
         assertEquals("{}", json);
     }

     @Test
     public void testNullValueSerialized() throws Exception {
         String json = toJson(new NullValueBean());
         // Jackson by default writes nulls
         assertEquals("{\"present\":42,\"notThere\":null}", json);
     }

     @Test
     public void testNonStringKeys() throws Exception {
         String json = toJson(new NonStringKeyBean());
         assertEquals("{\"123\":\"numberKey\",\"true\":\"boolKey\"}", json);
     }

     @Test
     public void testCombinedWithRegularProperty() throws Exception {
         MixedAccessorBean bean = new MixedAccessorBean("tester");
         String json = toJson(bean);
         assertEquals("{\"name\":\"tester\",\"extra\":\"info\"}", json);
     }

     @Test
     public void testAnyGetterReturnsNull() throws Exception {
         String json = toJson(new NullReturnBean());
         assertEquals("{}", json);
     }

     @Test(expected = JsonMappingException.class)
     public void testAnyGetterReturnsNonMap() throws Exception {
         class BadBean {
             @JsonAnyGetter
             public Object getAny() {
                 return "not_a_map";
             }
         }
         toJson(new BadBean());
     }
 }
