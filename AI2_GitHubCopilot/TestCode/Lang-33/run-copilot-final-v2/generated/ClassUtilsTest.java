package org.apache.commons.lang3;

 import static org.junit.Assert.*;

 import org.junit.Test;

 /**
  * Tests for {@link ClassUtils#toClass(Object[])} targeting LANG-587 (NullPointerException on null
elements).
  */
 public class ClassUtilsTest {

     @Test
     public void testToClass_nullInput() {
         assertNull("null input should return null", ClassUtils.toClass(null));
     }

     @Test
     public void testToClass_emptyArray() {
         Class<?>[] result = ClassUtils.toClass(new Object[0]);
         assertNotNull("empty array should not return null", result);
         assertEquals("empty array should produce zero-length result", 0, result.length);
     }

     @Test
     public void testToClass_allNullElements() {
         Class<?>[] result = ClassUtils.toClass(new Object[]{null, null, null});
         assertNotNull(result);
         assertEquals(3, result.length);
         assertNull(result[0]);
         assertNull(result[1]);
         assertNull(result[2]);
     }

     @Test
     public void testToClass_singleNullElement() {
         Class<?>[] result = ClassUtils.toClass(new Object[]{null});
         assertNotNull(result);
         assertEquals(1, result.length);
         assertNull(result[0]);
     }

     @Test
     public void testToClass_classObjects() {
         Class<?>[] result = ClassUtils.toClass(new Object[]{String.class, Integer.class,
int.class});
         assertEquals(3, result.length);
         assertSame("Class objects should be returned as-is", String.class, result[0]);
         assertSame("Class objects should be returned as-is", Integer.class, result[1]);
         assertSame("Primitive class objects should be returned as-is", int.class, result[2]);
     }

     @Test
     public void testToClass_objectInstances() {
         Class<?>[] result = ClassUtils.toClass(new Object[]{
             "hello",
             Integer.valueOf(42),
             Double.valueOf(3.14)
         });
         assertEquals(3, result.length);
         assertEquals(String.class, result[0]);
         assertEquals(Integer.class, result[1]);
         assertEquals(Double.class, result[2]);
     }

     @Test
     public void testToClass_singleClassElement() {
         Class<?>[] result = ClassUtils.toClass(new Object[]{String.class});
         assertEquals(1, result.length);
         assertSame(String.class, result[0]);
     }

     @Test
     public void testToClass_singleObjectElement() {
         Class<?>[] result = ClassUtils.toClass(new Object[]{"test"});
         assertEquals(1, result.length);
         assertEquals(String.class, result[0]);
     }

     @Test
     public void testToClass_mixedNullClassAndObject() {
         Class<?>[] result = ClassUtils.toClass(new Object[]{
             null,
             String.class,
             "hello",
             null,
             Integer.class,
             int.class
         });
         assertEquals(6, result.length);
         assertNull(result[0]);
         assertSame(String.class, result[1]);
         assertEquals(String.class, result[2]);
         assertNull(result[3]);
         assertSame(Integer.class, result[4]);
         assertSame(int.class, result[5]);
     }

     @Test
     public void testToClass_nullElementsBeforeAndAfterValid() {
         Class<?>[] result = ClassUtils.toClass(new Object[]{null, "valid", null});
         assertEquals(3, result.length);
         assertNull(result[0]);
         assertEquals(String.class, result[1]);
         assertNull(result[2]);
     }

     @Test
     public void testToClass_variousPrimitiveClasses() {
         Class<?>[] result = ClassUtils.toClass(new Object[]{
             boolean.class, byte.class, char.class, short.class,
             int.class, long.class, float.class, double.class, void.class
         });
         assertEquals(9, result.length);
         assertSame(boolean.class, result[0]);
         assertSame(byte.class, result[1]);
         assertSame(char.class, result[2]);
         assertSame(short.class, result[3]);
         assertSame(int.class, result[4]);
         assertSame(long.class, result[5]);
         assertSame(float.class, result[6]);
         assertSame(double.class, result[7]);
         assertSame(void.class, result[8]);
     }

     @Test
     public void testToClass_onlyClassObjectsNoNulls() {
         Class<?>[] input = new Class<?>[]{String.class, Object.class, Number.class};
         Class<?>[] result = ClassUtils.toClass(input);
         assertArrayEquals("Array of pure Class objects should be returned unchanged",
             new Class<?>[]{String.class, Object.class, Number.class}, result);
     }
 }
