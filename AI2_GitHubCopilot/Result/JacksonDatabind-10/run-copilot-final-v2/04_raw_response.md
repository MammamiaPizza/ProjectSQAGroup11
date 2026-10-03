@Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
    public void testAnyGetterNonMapThrows() throws Exception {
        new com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(new AnyGetterNonMapBean());
    }

 public static class AnyGetterNonMapBean {
     @com.fasterxml.jackson.annotation.JsonAnyGetter
     public String getNonMap() {
         return "not-a-map";
     }
 }

 @Test
 public void testAnyGetterWithFilterAndMapSerializer() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     com.fasterxml.jackson.databind.ser.FilterProvider filters = new
com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider()
             .addFilter("filter1",
com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.serializeAll());
     mapper.setFilterProvider(filters);
     String json = mapper.writer(filters).writeValueAsString(new FilteredAnyGetterBean());
     org.junit.Assert.assertEquals("{\"stuff\":\"key/value\"}", json);
 }

 @com.fasterxml.jackson.annotation.JsonFilter("filter1")
 public static class FilteredAnyGetterBean {
     @com.fasterxml.jackson.annotation.JsonAnyGetter
     public java.util.Map<String, Object> getStuff() {
         java.util.Map<String, Object> m = new java.util.HashMap<>();
         m.put("key", "value");
         return m;
     }
 }