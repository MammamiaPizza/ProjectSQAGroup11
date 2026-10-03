package com.fasterxml.jackson.databind.ser;

import static org.junit.Assert.*;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.;
import com.fasterxml.jackson.databind.introspect.;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;

public class BeanPropertyWriterTest {

 // ---- helper beans ----

 public static class Bean {
     public Object value;
     public Bean() { }
     public Bean(Object v) { this.value = v; }
 }

 // dummy serializer that writes a fixed string
 @SuppressWarnings("serial")
 public static class DummySerializer extends JsonSerializer<Object> {
     private final String token;
     public DummySerializer(String token) { this.token = token; }
     @Override
     public void serialize(Object value, JsonGenerator g, SerializerProvider p) throws Exception {
         g.writeString(token);
     }
 }

 // ---- helper methods ----

 private BeanPropertyWriter buildWriter(String name, JsonSerializer<?> ser,
                                       JsonSerializer<?> nullSer,
                                       Object suppressableValue) throws Exception {
     // get field accessor
     Field f = Bean.class.getDeclaredField("value");
     AnnotationMap am = new AnnotationMap();
     AnnotatedField af = new AnnotatedField(f, am);

  // context annotations: empty
  Annotations ctxAnnotations = new Annotations() {
      @Override
      public <A extends java.lang.annotation.Annotation> A get(Class<A> cls) { return null; }
  };

  // beandpropertydefinition
  BeanPropertyDefinition propDef = new BeanPropertyDefinition() {
      @Override public String getName() { return name; }
      @Override public PropertyName getWraperName() { return null; }
      @Override public boolean isRequired() { return false; }
      @Override public Class<?>[] findViews() { return null; }
      // remaining methods omitted; default implementation returns null
  };

  JavaType declaredType = TypeFactory.defaultInstance().constructType(Object.class);
  boolean suppresNulls = false; // not used

  BeanPropertyWriter writer = new BeanPropertyWriter(propDef, af, ctxAnnotations,
          declaredType, (JsonSerializer<Object>)ser, null, declaredType,
          suppresNulls, suppresableValue);
  if (nullSer != null) {
      writer.assignNullSerializer((JsonSerializer<Object>)nullSer);
  }
  return writer;

 }

 private String serializeWithColumn(Object bean, BeanPropertyWriter writer) throws Exception {
     StringWriter sw = new StringWriter();
     JsonGenerator gen = new JsonFactory().createJsonGenerator(sw);
     SerializerProvider prov = null; // not used by our dummy serializers
     writer.serializeAsColumn(bean, gen, prov);
     gen.close();
     return sw.toString();
 }

 // ---- tests ----

 @Test
 public void testAssignNullSerializerSetsField() throws Exception {
     BeanPropertyWriter w = buildWriter("foo", null, null, null);
     assertFalse(w.hasNullSerializer());
     w.assignNullSerializer(new DummySerializer("NULL"));
     assertTrue(w.hasNullSerializer());
     assertNotNull(w.getSerializer()); // getter for nullSerializer? Not directly.
 }

 @Test
 public void testHasNullSerializerAfterAssign() throws Exception {
     BeanPropertyWriter w = buildWriter("foo", null, null, null);
     w.assignNullSerializer(new DummySerializer("NULL"));
     assertTrue(w.hasNullSerializer());
 }

 @Test
 public void testSerializeAsColumnNullValueNoNullSerializerWritesSingleNull() throws Exception {
     BeanPropertyWriter w = buildWriter("foo", new DummySerializer("EXTRA"), null, null);
     Bean bean = new Bean(null);
     String out = serializeWithColumn(bean, w);
     // correct: single null token
     assertEquals("null", out.trim());
 }

 @Test
 public void testSerializeAsColumnNullValueWithNullSerializerWritesOnlyNullSerializerOutput() throws
Exception {
     BeanPropertyWriter w = buildWriter("foo", new DummySerializer("EXTRA"),
             new DummySerializer("NULL"), null);
     Bean bean = new Bean(null);
     String out = serializeWithColumn(bean, w);
     // correct: only null serializer output, no extra
     assertEquals(""NULL"", out.trim());
 }

 @Test
 public void testSerializeAsColumnNonNullValueWritesSerializedValue() throws Exception {
     BeanPropertyWriter w = buildWriter("foo", new DummySerializer("HELLO"), null, null);
     Bean bean = new Bean("ignored");
     String out = serializeWithColumn(bean, w);
     assertEquals(""HELLO"", out.trim());
 }

 @Test
 public void testSerializeAsColumnNullValueWithNullSerializerDoesNotCallMainSerializer() throws
Exception {
     // use serializers that record calls, but simple output check is enough:
     // if main serializer writes "EXTRA", then bug would produce "NULL""EXTRA"
     BeanPropertyWriter w = buildWriter("foo", new DummySerializer("EXTRA"),
             new DummySerializer("NULL"), null);
     Bean bean = new Bean(null);
     String out = serializeWithColumn(bean, w);
     assertFalse("should not contain main serializer token", out.contains("EXTRA"));
 }

 @Test
 public void testSerializeAsColumnNullValueNoNullSerializerDoesNotCallMainSerializer() throws
Exception {
     BeanPropertyWriter w = buildWriter("foo", new DummySerializer("EXTRA"), null, null);
     Bean bean = new Bean(null);
     String out = serializeWithColumn(bean, w);
     assertFalse("should not contain main serializer token", out.contains("EXTRA"));
 }

 @Test
 public void testSerializeAsColumnSuppresableValueMarkerEmptyTriggersPlaceholder() throws Exception
{
     // create a writer with MARKER_FOR_EMPTY and a dummy serializer that always isEmpty true
     BeanPropertyWriter w = buildWriter("foo", new JsonSerializer<Object>() {
         @Override
         public void serialize(Object value, JsonGenerator g, SerializerProvider p) throws Exception
{
             // should not be called if placeholder is invoked
             g.writeString("SERI");
         }
         @Override
         public boolean isEmpty(Object value) { return true; }
     }, null, BeanPropertyWriter.MARKER_FOR_EMPTY);
     Bean bean = new Bean("ignored");
     // placeholder behavior is not guaranteed, but at least no main serializer token
     String out = serializeWithColumn(bean, w);
     assertFalse("should not have main serializer token", out.contains("SERI"));
 }

 @Test
 public void testSerializeAsColumnSuppresableValueEquelsTriggersPlaceholder() throws Exception {
     // create a writer with suppresableValue = specific string
     final String suppress = "supp";
     BeanPropertyWriter w = buildWriter("foo", new DummySerializer("WRONG"), null, suppress);
     Bean bean = new Bean(suppress);
     // placeholder should be triggered
     String out = serializeWithColumn(bean, w);
     assertFalse(out.contains("WRONG"));
 }

 @Test
 public void testSerializeAsColumnSelfReferenceHandled() throws Exception {
     // build writer that wraps a bean referencing itself; should throw
     BeanPropertyWriter w = buildWriter("foo", new DummySerializer("SELF"), null, null);
     Bean bean = new Bean();
     bean.value = bean; // self ref
     try {
         serializeWithColumn(bean, w);
         fail("Should have thrown JsonMappingException");
     } catch (JsonMappingException e) {
         // expected
     }
 }

 @Test
 public void testSerializeAsColumnNullValueWithNullSerializerStillReturnsAfterNull() throws
Exception {
     // verify that after null handling, no more writes occur (bug would cause NPE or extra token)
     BeanPropertyWriter w = buildWriter("foo", null, new DummySerializer("NULL"), null);
     Bean bean = new Bean(null);
     String out = serializeWithColumn(bean, w);
     assertEquals(""NULL"", out.trim());
 }

}
