package com.fasterxml.jackson.databind;

  import static org.junit.Assert.*;
  import static org.mockito.Mockito.*;

  import java.io.*;
  import java.lang.reflect.Field;
  import java.lang.reflect.Method;
  import java.util.HashMap;

  import org.junit.Test;
  import org.junit.runner.RunWith;
  import org.mockito.MockSettings;
  import org.mockito.stubbing.Answer;
  import org.powermock.modules.junit4.PowerMockRunner;

  import com.fasterxml.jackson.core.JsonGenerator;
  import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
  import com.fasterxml.jackson.databind.introspect.AnnotationMap;
  import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
  import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
  import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
  import com.fasterxml.jackson.databind.type.TypeFactory;
  import com.fasterxml.jackson.databind.util.Annotations;
  import com.fasterxml.jackson.databind.util.NameTransformer;
  import com.fasterxml.jackson.annotation.JsonInclude;
  import com.fasterxml.jackson.core.SerializableString;
  import com.fasterxml.jackson.core.io.SerializedString;
  import com.fasterxml.jackson.databind.PropertyMetadata;
  import com.fasterxml.jackson.databind.PropertyName;
  import com.fasterxml.jackson.databind.JavaType;

  @RunWith(PowerMockRunner.class)
  public class BeanPropertyWriterSerializationTest {

      // Serializable custom serializer for testing
      public static class SerializableSerializer extends
 com.fasterxml.jackson.databind.JsonSerializer<Object> implements Serializable {
          private static final long serialVersionUID = 1L;
          @Override
          public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers)
 throws IOException {
              gen.writeString("serializable");
          }
      }

      // Helper to perform roundtrip serialization
      private BeanPropertyWriter roundtrip(BeanPropertyWriter writer) throws Exception {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ObjectOutputStream oos = new ObjectOutputStream(baos);
          oos.writeObject(writer);
          oos.close();
          byte[] bytes = baos.toByteArray();
          ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
          ObjectInputStream ois = new ObjectInputStream(bais);
          BeanPropertyWriter result = (BeanPropertyWriter) ois.readObject();
          ois.close();
          return result;
      }

      // Creates a basic writer with minimal, serializable fields
      private BeanPropertyWriter createBaseWriter() throws Exception {
          MockSettings serializable = withSettings().serializable();

          BeanPropertyDefinition propDef = mock(BeanPropertyDefinition.class, serializable);
          when(propDef.getName()).thenReturn("testProp");
          when(propDef.getWrapperName()).thenReturn(null);
          PropertyMetadata metadata = mock(PropertyMetadata.class, serializable);
          when(metadata.isRequired()).thenReturn(false);
          when(propDef.getMetadata()).thenReturn(metadata);
          when(propDef.findViews()).thenReturn(null);

          AnnotatedMember member = mock(AnnotatedMember.class, serializable);
          // ensure it is not AnnotatedField or AnnotatedMethod so _accessorMethod and _field stay
null
          JavaType stringType = TypeFactory.defaultInstance().constructType(String.class);
          Annotations annotations = mock(Annotations.class, serializable);

          return new BeanPropertyWriter(propDef, member, annotations,
                  stringType, null, null, stringType, false, null);
      }

      // Utility: set a protected/private field via reflection
      private void setField(Object target, String fieldName, Object value) throws Exception {
          Field field = BeanPropertyWriter.class.getDeclaredField(fieldName);
          field.setAccessible(true);
          field.set(target, value);
      }

      // Utility: read a protected/private field via reflection
      private Object getField(Object target, String fieldName) throws Exception {
          Field field = BeanPropertyWriter.class.getDeclaredField(fieldName);
          field.setAccessible(true);
          return field.get(target);
      }

      @Test
      public void testMinimalWriterCanBeSerialized() throws Exception {
          BeanPropertyWriter writer = createBaseWriter();
          BeanPropertyWriter result = roundtrip(writer);
          assertNotNull("Deserialized writer must not be null", result);
          assertEquals("testProp", result.getName());
      }

      @Test
      public void testRoundtripPreservesNameAndType() throws Exception {
          BeanPropertyWriter writer = createBaseWriter();
          // set a known declared type (already set in creation)
          JavaType originalType = writer.getType();
          BeanPropertyWriter result = roundtrip(writer);
          assertEquals("testProp", result.getName());
          assertNotNull("Type must survive roundtrip", result.getType());
          assertEquals("Raw class mismatch", originalType.getRawClass(),
 result.getType().getRawClass());
      }

      @Test
      public void testSerializerSurvivesRoundtrip() throws Exception {
          BeanPropertyWriter writer = createBaseWriter();
          SerializableSerializer ser = new SerializableSerializer();
          setField(writer, "_serializer", ser);
          BeanPropertyWriter result = roundtrip(writer);
          assertNotNull("Serializer should be non-null after deserialization",
 result.getSerializer());
          assertTrue("Serializer should be of type SerializableSerializer",
                  result.getSerializer() instanceof SerializableSerializer);
      }

      @Test
      public void testNullSerializerSurvivesRoundtrip() throws Exception {
          BeanPropertyWriter writer = createBaseWriter();
          SerializableSerializer nullSer = new SerializableSerializer();
          setField(witer, "_nullSerializer", nullSer);
          BeanPropertyWriter result = roundtrip(writer);
          JsonSerializer<Object> deserNullSer = (JsonSerializer<Object>) getField(result,
 "_nullSerializer");
          assertNotNull("Null serializer should survive roundtrip", deserNullSer);
          assertTrue("Should be SerializableSerializer", deserNullSer instanceof
 SerializableSerializer);
      }

      @Test
      public void testWrapperNameSurvivesRoundtrip() throws Exception {
          BeanPropertyWriter writer = createBaseWriter();
          PropertyName wrapper = PropertyName.construct("myWrapper");
          setField(witer, "_wrapperName", wrapper);
          BeanPropertyWriter result = roundtrip(writer);
          PropertyName resultWrapper = result.getWrapperName();
          assertNotNull(resultWrapper);
          assertEquals("myWrapper", resultWrapper.getSimpleName());
      }

      @Test
      public void testIncludeInViewsSurvivesRoundtrip() throws Exception {
          BeanPropertyWriter writer = createBaseWriter();
          Class<?>[] views = new Class<?>[]{ String.class, Integer.class };
          setField(writer, "_includeInViews", views);
          BeanPropertyWriter result = roundtrip(writer);
          assertArrayEquals("Views should be preserved", views, result.getViews());
      }

      @Test
      public void testTypeSerializerSurvivesRoundtrip() throws Exception {
          BeanPropertyWriter writer = createBaseWriter();
          TypeSerializer typeSer = mock(TypeSerializer.class, withSettings().serializable());
          setField(writer, "_typeSerializer", typeSer);
          BeanPropertyWriter result = roundtrip(writer);
          TypeSerializer resultTypeSer = (TypeSerializer) getField(result, "_typeSerializr");
          assertNotNull("TypeSerializer should be non-null after deserialization", resultTypeSer);
      }

      @Test
      public void testSuppressableValueSurvivesRoundtrip() throws Exception {
          BeanPropertyWriter writer = createBaseWriter();
          String suppressable = "suppressMe";
          setField(writer, "_suppressableValue", suppressable);
          BeanPropertyWriter result = roundtrip(writer);
          Object value = getField(result, "_suppressableValue");
          assertEquals("Suppressable value should be preserved", suppressable, value);
      }

      @Test
      public void testNonTrivialBaseTypeSurvivesRoundtrip() throws Exception {
          BeanPropertyWriter writer = createBaseWriter();
          JavaType baseType = TypeFactory.defaultInstance().constructType(Number.class);
          setField(writer, "_nonTrivialBaseType", baseType);
          BeanPropertyWriter result = roundtrip(writer);
          JavaType resultType = (JavaType) getField(result, "_nonTrivialBaseType");
          assertNotNull(resultType);
          assertEquals(baseType.getRawClass(), resultType.getRawClass());
      }

      @Test
      public void testTransientInternalSettingsLostAfterDeserialization() throws Exception {
          BeanPropertyWriter writer = createBaseWriter();
          HashMap<Object, Object> settings = new HashMap<Object, Object>();
          settings.put("key", "value");
          setField(writer, "_internalSettings", settings);
          BeanPropertyWriter result = roundtrip(writer);
          Object internal = getField(result, "_internalSettings");
          assertNull("Transient _internalSettings should be null after deserialization", internal);
      }

      @Test
      public void testMetadataSurvivesRoundtrip() throws Exception {
          BeanPropertyWriter writer = createBaseWriter();
          assertNotNull("Original metadata should not be null", writer.getMetadata());
          BeanPropertyWriter result = roundtrip(writer);
          assertNotNull("Metadata should be non-null after deserialization", result.getMetadata());
      }

      @Test
      public void testMultipleFieldsRoundtrip() throws Exception {
          BeanPropertyWriter writer = createBaseWriter();
          SerializableSerializer ser = new SerializableSerializer();
          setField(writer, "_serializer", ser);
          setField(writer, "_wrapperName", PropertyName.construct("wrap"));
          setField(writer, "_suppressableValue", "block");
          setField(writer, "_includeInViews", new Class<?>[]{ Boolean.class });
          BeanPropertyWriter result = roundtrip(writer);
          assertNotNull(result.getSerializer());
          assertTrue(result.getSerializer() instanceof SerializableSerializer);
          assertEquals("wrap", result.getWrapperName().getSimpleName());
          assertEquals("block", getField(result, "_suppressableValue"));
          assertArrayEquals(new Class<?>[]{ Boolean.class }, result.getViews());
      }
  }
