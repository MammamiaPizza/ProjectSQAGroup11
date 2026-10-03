package com.fasterxml.jackson.databind.filter;

 import static org.junit.Assert.*;

 import java.util.HashMap;
 import java.util.Map;

 import org.junit.Test;

 import com.fasterxml.jackson.annotation.JsonInclude;
 import com.fasterxml.jackson.core.JsonProcessingException;
 import com.fasterxml.jackson.databind.ObjectMapper;

 public class PropertyBuilderInclusionTest {

     private final ObjectMapper MAPPER = new ObjectMapper();

     @JsonInclude(JsonInclude.Include.NON_NULL)
     public static class NonNullBean {
         public String str;
     }

     @JsonInclude(JsonInclude.Include.ALWAYS)
     public static class AlwaysBean {
         public String str;
     }

     @JsonInclude(JsonInclude.Include.ALWAYS)
     public static class GlobalAlwaysPropNonNull {
         @JsonInclude(JsonInclude.Include.NON_NULL)
         public String str;
     }

     @JsonInclude(JsonInclude.Include.NON_NULL)
     public static class GlobalNonNullPropAlways {
         @JsonInclude(JsonInclude.Include.ALWAYS)
         public String str;
     }

     @JsonInclude(JsonInclude.Include.NON_NULL)
     public static class MapBean {
         public Map<String, String> map;
     }

     @JsonInclude(JsonInclude.Include.NON_DEFAULT)
     public static class NonDefaultBean {
         public String str;
         public int num;
     }

     @JsonInclude(JsonInclude.Include.NON_EMPTY)
     public static class NonEmptyBean {
         public String str;
     }

     @JsonInclude(JsonInclude.Include.NON_DEFAULT)
     public static class ThrowingGetterBean {
         private String str;
         public String getStr() {
             if (str == null) throw new RuntimeException("null disallowed");
             return str;
         }
         public void setStr(String s) { this.str = s; }
     }

     @JsonInclude(JsonInclude.Include.NON_DEFAULT)
     public static class NoDefaultConstructorBean {
         public String str;
         public NoDefaultConstructorBean(String s) { this.str = s; }
     }

     @Test
     public void testNON_NULLExcludesNullString() throws Exception {
         NonNullBean bean = new NonNullBean();
         bean.str = null;
         String json = MAPPER.writeValueAsString(bean);
         assertEquals("{}", json);
     }

     @Test
     public void testNON_NULLIncludesNonNullString() throws Exception {
         NonNullBean bean = new NonNullBean();
         bean.str = "hello";
         String json = MAPPER.writeValueAsString(bean);
         assertEquals("{\"str\":\"hello\"}", json);
     }

     @Test
     public void testALWAYSIncludesNullString() throws Exception {
         AlwaysBean bean = new AlwaysBean();
         bean.str = null;
         String json = MAPPER.writeValueAsString(bean);
         assertEquals("{\"str\":null}", json);
     }

     @Test
     public void testPropertyOverrideGlobalAlwaysWithPropNonNull() throws Exception {
         GlobalAlwaysPropNonNull bean = new GlobalAlwaysPropNonNull();
         bean.str = null;
         String json = MAPPER.writeValueAsString(bean);
         assertEquals("{}", json);
     }

     @Test
     public void testPropertyOverrideGlobalNonNullWithPropAlways() throws Exception {
         GlobalNonNullPropAlways bean = new GlobalNonNullPropAlways();
         bean.str = null;
         String json = MAPPER.writeValueAsString(bean);
         assertEquals("{\"str\":null}", json);
     }

     @Test
     public void testNON_NULLExcludesNullMapProperty() throws Exception {
         MapBean bean = new MapBean();
         bean.map = null;
         String json = MAPPER.writeValueAsString(bean);
         assertEquals("{}", json);
     }

     @Test
     public void testNON_DEFAULTExcludesDefaultValues() throws Exception {
         NonDefaultBean bean = new NonDefaultBean();
         bean.str = null;
         bean.num = 0;
         String json = MAPPER.writeValueAsString(bean);
         assertEquals("{}", json);
     }

     @Test
     public void testNON_DEFAULTIncludesNonDefaultString() throws Exception {
         NonDefaultBean bean = new NonDefaultBean();
         bean.str = "abc";
         bean.num = 0;
         String json = MAPPER.writeValueAsString(bean);
         assertEquals("{\"str\":\"abc\"}", json);
     }

     @Test
     public void testNON_EMPTYExcludesEmptyString() throws Exception {
         NonEmptyBean bean = new NonEmptyBean();
         bean.str = "";
         String json = MAPPER.writeValueAsString(bean);
         assertEquals("{}", json);
     }

     @Test
     public void testNON_EMPTYIncludesNonEmptyString() throws Exception {
         NonEmptyBean bean = new NonEmptyBean();
         bean.str = "x";
         String json = MAPPER.writeValueAsString(bean);
         assertEquals("{\"str\":\"x\"}", json);
     }

     @Test
     public void testThrowingGetterPropagatesException() {
         ThrowingGetterBean bean = new ThrowingGetterBean();
         bean.setStr(null);
         try {
             MAPPER.writeValueAsString(bean);
             fail("Expected exception");
         } catch (JsonProcessingException e) {
             assertNotNull(e.getCause());
             assertTrue(e.getCause() instanceof RuntimeException);
         }
     }

     @Test
     public void testNullStringWithNON_DEFAULTNoDefaultConstructor() throws Exception {
         NoDefaultConstructorBean bean = new NoDefaultConstructorBean(null);
         String json = MAPPER.writeValueAsString(bean);
         assertEquals("{}", json);
     }
 }