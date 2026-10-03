package com.fasterxml.jackson.databind.deser.jdk;

 import java.util.concurrent.atomic.AtomicReference;

 import com.fasterxml.jackson.annotation.JsonSetter;
 import com.fasterxml.jackson.annotation.Nulls;
 import com.fasterxml.jackson.databind.ObjectMapper;

 import org.junit.Test;
 import static org.junit.Assert.*;

 public class NullWithinNestedAtomicReferenceTest
 {
     public static class InnerBean {
         public AtomicReference<String> ref;
     }

     public static class OuterBean {
         public InnerBean inner;
     }

     public static class BeanWithAtomicRef {
         public AtomicReference<String> ref;
     }

     public static class BeanWithAtomicRefSetter {
         private AtomicReference<String> ref;
         public AtomicReference<String> getRef() { return ref; }
         public void setRef(AtomicReference<String> ref) { this.ref = ref; }
     }

     public static class BeanWithSkipNulls {
         private String value = "default";
         @JsonSetter(nulls = Nulls.SKIP)
         public void setValue(String value) { this.value = value; }
         public String getValue() { return value; }
     }

     public static class BeanWithSkipNullsAtomicRef {
         private AtomicReference<String> ref = new AtomicReference<String>("initial");
         @JsonSetter(nulls = Nulls.SKIP)
         public void setRef(AtomicReference<String> ref) { this.ref = ref; }
         public AtomicReference<String> getRef() { return ref; }
     }

     private final ObjectMapper MAPPER = new ObjectMapper();

     // 1. Trigger scenario: nested null AtomicReference
     @Test
     public void testNullWithinNested() throws Exception {
         String json = "{\"inner\":{\"ref\":null}}";
         OuterBean result = MAPPER.readValue(json, OuterBean.class);
         assertNotNull(result);
         assertNotNull(result.inner);
         assertNotNull("AtomicReference container must be non-null even when JSON value is null",
result.inner.ref);
         assertNull("Contents must be null", result.inner.ref.get());
     }

     // 2. Normal: nested non-null AtomicReference
     @Test
     public void testNonNullWithinNested() throws Exception {
         String json = "{\"inner\":{\"ref\":\"hello\"}}";
         OuterBean result = MAPPER.readValue(json, OuterBean.class);
         assertNotNull(result.inner);
         assertNotNull(result.inner.ref);
         assertEquals("hello", result.inner.ref.get());
     }

     // 3. Top-level null for AtomicReference public field
     @Test
     public void testTopLevelNullAtomicRefField() throws Exception {
         String json = "{\"ref\":null}";
         BeanWithAtomicRef result = MAPPER.readValue(json, BeanWithAtomicRef.class);
         assertNotNull(result.ref);
         assertNull(result.ref.get());
     }

     // 4. Top-level null for AtomicReference via setter
     @Test
     public void testTopLevelNullAtomicRefSetter() throws Exception {
         String json = "{\"ref\":null}";
         BeanWithAtomicRefSetter result = MAPPER.readValue(json, BeanWithAtomicRefSetter.class);
         assertNotNull(result.getRef());
         assertNull(result.getRef().get());
     }

     // 5. Missing field
     @Test
     public void testMissingAtomicRefField() throws Exception {
         String json = "{}";
         BeanWithAtomicRef result = MAPPER.readValue(json, BeanWithAtomicRef.class);
         assertNotNull(result);
         assertNull(result.ref);
     }

     // 6. @JsonSetter(nulls=SKIP) preserves default String value
     @Test
     public void testSkipNullsPreservesDefault() throws Exception {
         String json = "{\"value\":null}";
         BeanWithSkipNulls result = MAPPER.readValue(json, BeanWithSkipNulls.class);
         assertEquals("default", result.getValue());
     }

     // 7. @JsonSetter(nulls=SKIP) preserves default AtomicReference
     @Test
     public void testSkipNullsPreservesAtomicRefDefault() throws Exception {
         String json = "{\"ref\":null}";
         BeanWithSkipNullsAtomicRef result = MAPPER.readValue(json,
BeanWithSkipNullsAtomicRef.class);
         assertNotNull(result.getRef());
         assertEquals("initial", result.getRef().get());
     }

     // 8. Non-null value through setter
     @Test
     public void testValidValueThroughSetter() throws Exception {
         String json = "{\"ref\":\"test-value\"}";
         BeanWithAtomicRefSetter result = MAPPER.readValue(json, BeanWithAtomicRefSetter.class);
         assertNotNull(result.getRef());
         assertEquals("test-value", result.getRef().get());
     }

     // 9. Valid value through public field
     @Test
     public void testValidValueThroughField() throws Exception {
         String json = "{\"ref\":\"field-value\"}";
         BeanWithAtomicRef result = MAPPER.readValue(json, BeanWithAtomicRef.class);
         assertNotNull(result.ref);
         assertEquals("field-value", result.ref.get());
     }

     // 10. Top-level null for nested object
     @Test
     public void testOuterNullInnerMissing() throws Exception {
         String json = "{\"inner\":null}";
         OuterBean result = MAPPER.readValue(json, OuterBean.class);
         assertNotNull(result);
         assertNull(result.inner);
     }

     // 11. Nested missing field (inner present, ref missing)
     @Test
     public void testNestedMissingRef() throws Exception {
         String json = "{\"inner\":{}}";
         OuterBean result = MAPPER.readValue(json, OuterBean.class);
         assertNotNull(result.inner);
         assertNull(result.inner.ref);
     }

     // 12. Deserialization does not throw on valid null within AtomicReference
     @Test
     public void testNoExceptionOnNullAtomicRef() throws Exception {
         String json = "{\"ref\":null}";
         try {
             BeanWithAtomicRef result = MAPPER.readValue(json, BeanWithAtomicRef.class);
             assertNotNull(result);
         } catch (Exception e) {
             fail("Deserialization should not throw: " + e.getClass().getName() + ": " +
e.getMessage());
         }
     }
 }
