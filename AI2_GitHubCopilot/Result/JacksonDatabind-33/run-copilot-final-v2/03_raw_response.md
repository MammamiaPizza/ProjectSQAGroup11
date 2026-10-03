package com.fasterxml.jackson.databind.introspect;

import static org.junit.Assert.*;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.util.NameTransformer;

public class JacksonAnnotationIntrospectorTest {

 // Helper ObjectMapper with default configuration (FAIL_ON_EMPTY_BEANS enabled)
 private static final ObjectMapper MAPPER = new ObjectMapper();

 // ---------- dummy beans for introspection and serialization ----------

 static class InnerBean {
     public String a = "val";
     public int b = 42;
 }

 static class BeanWithUnwrappedField {
     @JsonUnwrapped
     public InnerBean inner = new InnerBean();
 }

 static class BeanWithUnwrappedFieldNullDefault {
     @JsonUnwrapped
     public InnerBean inner = null;
 }

 static class BeanWithUnwrappedGetter {
     private InnerBean inner = new InnerBean();

     @JsonUnwrapped
     public InnerBean getInner() { return inner; }
 }

 static class BeanWithUnwrappedGetterAndOtherProp {
     @JsonUnwrapped
     public InnerBean getInner() { return new InnerBean(); }

     public String other = "prop";
 }

 static class BeanWithStaticUnwrapped {
     public String field = "data";

     @JsonUnwrapped
     public static InnerBean getInnerStatic() { return new InnerBean(); }
 }

 // ---------- Unit tests for findUnwrappingNameTransformer ----------

 @Test
 public void testFindUnwrappingNameTransformerForField() throws Exception {
     JacksonAnnotationIntrospector intro = new JacksonAnnotationIntrospector();
     Field field = BeanWithUnwrappedField.class.getDeclaredField("inner");
     AnnotationMap annotations = AnnotationMap.of(field.getAnnotations());
     AnnotatedField af = new AnnotatedField(null, field, annotations);

     NameTransformer transformer = intro.findUnwrappingNameTransformer(af);
     assertNotNull("NameTransformer should not be null for @JsonUnwrapped field", transformer);
 }

 @Test
 public void testFindUnwrappingNameTransformerForGetter() throws Exception {
     JacksonAnnotationIntrospector intro = new JacksonAnnotationIntrospector();
     Method method = BeanWithUnwrappedGetter.class.getDeclaredMethod("getInner");
     AnnotationMap annotations = AnnotationMap.of(method.getAnnotations());
     AnnotatedMethod am = new AnnotatedMethod(null, method, annotations, null);

     NameTransformer transformer = intro.findUnwrappingNameTransformer(am);
     assertNotNull("NameTransformer should not be null for @JsonUnwrapped getter", transformer);
 }

 @Test
 public void testFindUnwrappingNameTransformerNonAnnotated() throws Exception {
     JacksonAnnotationIntrospector intro = new JacksonAnnotationIntrospector();

     // member without any Jackson annotations
     class UnannotatedBean { public String x; }
     Field field = UnannotatedBean.class.getDeclaredField("x");
     AnnotatedField af = new AnnotatedField(null, field, AnnotationMap.of(new Annotation[]{}));

     assertNull("NameTransformer should be null for non-annotated member",
             intro.findUnwrappingNameTransformer(af));
 }

 // ---------- Unit tests for hasIgnoreMarker ----------

 @Test
 public void testHasIgnoreMarkerWithUnwrappedField() throws Exception {
     JacksonAnnotationIntrospector intro = new JacksonAnnotationIntrospector();
     Field field = BeanWithUnwrappedField.class.getDeclaredField("inner");
     AnnotatedField af = new AnnotatedField(null, field,
             AnnotationMap.of(field.getAnnotations()));

     assertFalse("hasIgnoreMarker must be false for @JsonUnwrapped field",
             intro.hasIgnoreMarker(af));
 }

 @Test
 public void testHasIgnoreMarkerWithUnwrappedGetter() throws Exception {
     JacksonAnnotationIntrospector intro = new JacksonAnnotationIntrospector();
     Method method = BeanWithUnwrappedGetter.class.getDeclaredMethod("getInner");
     AnnotatedMethod am = new AnnotatedMethod(null, method,
             AnnotationMap.of(method.getAnnotations()), null);

     assertFalse("hasIgnoreMarker must be false for @JsonUnwrapped getter",
             intro.hasIgnoreMarker(am));
 }

 // ---------- Serialisation tests (absence of FAIL_ON_EMPTY_BEANS) ----------

 @Test
 public void testSerializationWithUnwrappedGetter() throws Exception {
     String jason = MAPPER.writeValueAsString(new BeanWithUnwrappedGetter());
     assertNotNull(jason);
     // verify that inner properties are unwrapped and visible
     assertTrue("Should contain property 'a' from unwrapped bean", jason.contains("\"val\""));
     assertTrue("Should contain property 'b'", jason.contains("42"));
 }

 @Test
 public void testSerializationWithNullUnwrappedField() throws Exception {
     // just ensure no exception is thrown
     String jason = MAPPER.writeValueAsString(new BeanWithUnwrappedFieldNullDefault());
     assertNotNull(jason);
 }

 @Test
 public void testSerializationUnwrappedStaticMethod() throws Exception {
     // static method should be ignored; serialisation must succeed
     String jason = MAPPER.writeValueAsString(new BeanWithStaticUnwrapped());
     assertNotNull(jason);
     assertTrue("Should contain regular field 'data'", jason.contains("\"data\""));
 }

 @Test
 public void testSerializationBeanWithOnlyUnwrappedMember() throws Exception {
     // bean that has *only* an @JsonUnwrapped member - must still be recognised as having
properties
     class OnlyUnwrapped {
         @JsonUnwrapped
         public InnerBean inner = new InnerBean();
     }
     String jason = MAPPER.writeValueAsString(new OnlyUnwrapped());
     assertNotNull(jason);
     assertTrue(jason.contains("\"val\""));
 }

}