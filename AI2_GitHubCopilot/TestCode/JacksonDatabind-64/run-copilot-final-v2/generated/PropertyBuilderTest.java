package com.fasterxml.jackson.databind.filter;

 import com.fasterxml.jackson.annotation.JsonInclude;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import org.junit.Before;
 import org.junit.Test;

 import java.util.HashMap;
 import java.util.Map;

 import static org.junit.Assert.*;

 /**
  * Tests for {@link com.fasterxml.jackson.databind.ser.PropertyBuilder} default-value
  * and inclusion merging, targeting bug #1417 (null String not excluded by NON_NULL).
  */
 public class PropertyBuilderTest {

     private ObjectMapper mapper;

     @Before
     public void setUp() {
         mapper = new ObjectMapper();
     }

     /* ----- simple beans ----- */

     // No class-level annotation -> default inclusion (ALWAYS)
     public static class PlainBean {
         private String str;
         private int number;

         public String getStr() { return str; }
         public void setStr(String str) { this.str = str; }
         public int getNumber() { return number; }
         public void setNumber(int number) { this.number = number; }
     }

     @JsonInclude(JsonInclude.Include.NON_NULL)
     public static class NonNullBean {
         private String str;
         private int value;

         public String getStr() { return str; }
         public void setStr(String str) { this.str = str; }
         public int getValue() { return value; }
         public void setValue(int value) { this.value = value; }
     }

     @JsonInclude(JsonInclude.Include.ALWAYS)
     public static class AlwaysBean {
         private String str;
         public String getStr() { return str; }
         public void setStr(String str) { this.str = str; }
     }

     // Property-level override: class NON_NULL, but one property ALWAYS
     @JsonInclude(JsonInclude.Include.NON_NULL)
     public static class MixedOverrideBean {
         private String alwaysProp;
         private String nonNullProp;

         @JsonInclude(JsonInclude.Include.ALWAYS)
         public String getAlwaysProp() { return alwaysProp; }
         public void setAlwaysProp(String alwaysProp) { this.alwaysProp = alwaysProp; }

         @JsonInclude(JsonInclude.Include.NON_NULL)
         public String getNonNullProp() { return nonNullProp; }
         public void setNonNullProp(String nonNullProp) { this.nonNullProp = nonNullProp; }
     }

     // Map property bean
     @JsonInclude(JsonInclude.Include.NON_NULL)
     public static class BeanWithMap {
         private Map<String, String> map;

         public Map<String, String> getMap() { return map; }
         public void setMap(Map<String, String> map) { this.map = map; }
     }

     /* ----- Tests ----- */

     // Bug #1417: global NON_NULL should exclude null String (this FAILS on the buggy version)
     @Test
     public void testGlobalNonNullExcludesNull() throws Exception {
         NonNullBean bean = new NonNullBean();
         bean.setStr(null);
         String json = mapper.writeValueAsString(bean);
         assertEquals("{}", json);
     }

     @Test
     public void testGlobalNonNullIncludesNonNull() throws Exception {
         NonNullBean bean = new NonNullBean();
         bean.setStr("hello");
         String json = mapper.writeValueAsString(bean);
         assertTrue(json.contains("\"str\":\"hello\""));
     }

     @Test
     public void testGlobalAlwaysIncludesNull() throws Exception {
         AlwaysBean bean = new AlwaysBean();
         bean.setStr(null);
         String json = mapper.writeValueAsString(bean);
         assertTrue(json.contains("\"str\":null"));
     }

     // Per-property @JsonInclude(ALWAYS) overrides class NON_NULL – null should appear
     @Test
     public void testPropertyAlwaysOverridesGlobalNonNull() throws Exception {
         MixedOverrideBean bean = new MixedOverrideBean();
         bean.setAlwaysProp(null);
         bean.setNonNullProp("present");
         String json = mapper.writeValueAsString(bean);
         assertTrue(json.contains("\"alwaysProp\":null"));
         assertTrue(json.contains("\"nonNullProp\":\"present\""));
         // "nonNullProp" must not be null
         assertFalse(json.contains("\"nonNullProp\":null"));
     }

     // Per-property @JsonInclude(NON_NULL) overrides class ALWAYS – null should be excluded
     @Test
     public void testPropertyNonNullOverridesGlobalAlways() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         // Use a custom bean with class ALWAYS, this property NON_NULL
         @JsonInclude(JsonInclude.Include.ALWAYS)
         class AlwaysAtClassBean {
             private String alwaysNull;

             @JsonInclude(JsonInclude.Include.NON_NULL)
             public String getAlwaysNull() { return alwaysNull; }
             public void setAlwaysNull(String alwaysNull) { this.alwaysNull = alwaysNull; }
         }
         AlwaysAtClassBean bean = new AlwaysAtClassBean();
         bean.setAlwaysNull(null);
         String json = mapper.writeValueAsString(bean);
         assertEquals("{}", json);
     }

     @Test
     public void testDefaultInclusionIncludesNullString() throws Exception {
         PlainBean bean = new PlainBean();
         bean.setStr(null);
         String json = mapper.writeValueAsString(bean);
         assertTrue(json.contains("\"str\":null"));
     }

     @Test
     public void testNonNullExcludesNullMapEntry() throws Exception {
         BeanWithMap bean = new BeanWithMap();
         Map<String, String> m = new HashMap<String, String>();
         m.put("a", null);
         bean.setMap(m);
         String json = mapper.writeValueAsString(bean);
         // Jackson serializes map with non-null values only; empty map expected
         assertEquals("{\"map\":{}}", json);
     }

     @Test
     public void testPrimitiveIntIsSerializedEvenWhenZero() throws Exception {
         NonNullBean bean = new NonNullBean();
         bean.setValue(0);
         String json = mapper.writeValueAsString(bean);
         assertTrue(json.contains("\"value\":0"));
     }

     @Test
     public void testPrimitiveIntWithDefaultInclusion() throws Exception {
         PlainBean bean = new PlainBean();
         bean.setNumber(0);
         String json = mapper.writeValueAsString(bean);
         assertTrue(json.contains("\"number\":0"));
     }

     @Test
     public void testMixedInclusionNoNPE() throws Exception {
         MixedOverrideBean bean = new MixedOverrideBean();
         bean.setAlwaysProp("a");
         bean.setNonNullProp(null);
         String json = mapper.writeValueAsString(bean);
         // nonNullProp is null and annotated NON_NULL -> excluded, alwaysProp appears
         assertEquals("{\"alwaysProp\":\"a\"}", json);
     }
 }
