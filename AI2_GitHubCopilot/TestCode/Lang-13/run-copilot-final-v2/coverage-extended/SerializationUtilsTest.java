package org.apache.commons.lang3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;

import org.junit.Test;

/**

 - Tests for {@link SerializationUtils}, focusing on LANG-788:
 - primitive type class serialization should not throw ClassNotFoundException.
  */
 public class SerializationUtilsTest {
  @Test
  public void testCloneNull() {
  assertNull(SerializationUtils.clone(null));
  }
  @Test
  public void testCloneNonPrimitiveClass() {
  assertEquals(String.class, SerializationUtils.clone(String.class));
  }
  /**
  - Trigger test for LANG-788: clone of primitive type Class objects
  - must succeed without SerializationException / ClassNotFoundException,
  - and must return the same Class.
    */
   @Test
   public void testPrimitiveTypeClassSerialization() {
   Class<?>[] primitives = {
   int.class, void.class, boolean.class, byte.class,
   char.class, short.class, long.class, float.class, double.class
   };
   for (Class<?> primitive : primitives) {
   Class<?> cloned = SerializationUtils.clone(primitive);
   assertEquals("Clone of " + primitive + " must return the same class",
       primitive, cloned);
   }
   }
  @Test
  public void testDeserializeNullByteArray() {
      try {
          SerializationUtils.deserialize((byte[]) null);
          fail("Expected IllegalArgumentException");
      } catch (IllegalArgumentException expected) {
          // expected
      }
  }
  @Test
  public void testSerializeDeserializePrimitiveClassRoundTrip() {
      byte[] data = SerializationUtils.serialize(int.class);
      Object result = SerializationUtils.deserialize(data);
      assertEquals(int.class, result);
   data = SerializationUtils.serialize(void.class);
   result = SerializationUtils.deserialize(data);
   assertEquals(void.class, result);
  }
  @Test
  public void testClassLoaderAwareObjectInputStreamResolvesPrimitiveClass() throws Exception {
      byte[] data = SerializationUtils.serialize(int.class);
      ByteArrayInputStream bais = new ByteArrayInputStream(data);
      SerializationUtils.ClassLoaderAwareObjectInputStream in =
              new SerializationUtils.ClassLoaderAwareObjectInputStream(
                      bais, getClass().getClassLoader());
      try {
          Object result = in.readObject();
          assertEquals(int.class, result);
      } finally {
          in.close();
      }
  }
  @Test
  public void testClassLoaderAwareObjectInputStreamResolvesVoidClass() throws Exception {
      byte[] data = SerializationUtils.serialize(void.class);
      ByteArrayInputStream bais = new ByteArrayInputStream(data);
      SerializationUtils.ClassLoaderAwareObjectInputStream in =
              new SerializationUtils.ClassLoaderAwareObjectInputStream(
                      bais, getClass().getClassLoader());
      try {
          Object result = in.readObject();
          assertEquals(void.class, result);
      } finally {
          in.close();
      }
  }

@Test(expected = IllegalArgumentException.class)
 public void testSerializeNullOutputStream() {
     SerializationUtils.serialize("test", (java.io.OutputStream) null);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testDeserializeNullInputStream() {
     SerializationUtils.deserialize((java.io.InputStream) null);
 }

 @Test(expected = org.apache.commons.lang3.SerializationException.class)
 public void testDeserializeCorruptStream() {
     SerializationUtils.deserialize(new java.io.ByteArrayInputStream(new byte[]{0, 0, 0, 0}));
 }

 @Test
 public void testConstructor() {
     new SerializationUtils();
 }
}
