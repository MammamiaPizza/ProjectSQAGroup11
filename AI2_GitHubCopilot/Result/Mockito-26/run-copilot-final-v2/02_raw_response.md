package org.mockito.internal.util;

 import static org.junit.Assert.*;
 import org.junit.Test;

 public class PrimitivesTest {

     @Test
     public void testPrimitiveValueOrNullForBoolean() {
         Object result = Primitives.primitiveValueOrNullFor(boolean.class);
         assertNotNull(result);
         assertTrue("Expected Boolean but got " + result.getClass().getName(), result instanceof
Boolean);
         assertFalse(((Boolean) result).booleanValue());
     }

     @Test
     public void testPrimitiveValueOrNullForChar() {
         Object result = Primitives.primitiveValueOrNullFor(char.class);
         assertNotNull(result);
         assertTrue("Expected Character but got " + result.getClass().getName(), result instanceof
Character);
         assertEquals('\u0000', ((Character) result).charValue());
     }

     @Test
     public void testPrimitiveValueOrNullForByte() {
         Object result = Primitives.primitiveValueOrNullFor(byte.class);
         assertNotNull(result);
         assertTrue("Expected Byte but got " + result.getClass().getName(), result instanceof Byte);
         assertEquals((byte) 0, ((Byte) result).byteValue());
     }

     @Test
     public void testPrimitiveValueOrNullForShort() {
         Object result = Primitives.primitiveValueOrNullFor(short.class);
         assertNotNull(result);
         assertTrue("Expected Short but got " + result.getClass().getName(), result instanceof
Short);
         assertEquals((short) 0, ((Short) result).shortValue());
     }

     @Test
     public void testPrimitiveValueOrNullForInt() {
         Object result = Primitives.primitiveValueOrNullFor(int.class);
         assertNotNull(result);
         assertTrue("Expected Integer but got " + result.getClass().getName(), result instanceof
Integer);
         assertEquals(0, ((Integer) result).intValue());
     }

     @Test
     public void testPrimitiveValueOrNullForLong() {
         Object result = Primitives.primitiveValueOrNullFor(long.class);
         assertNotNull(result);
         assertTrue("Expected Long but got " + result.getClass().getName(), result instanceof Long);
         assertEquals(0L, ((Long) result).longValue());
     }

     @Test
     public void testPrimitiveValueOrNullForFloat() {
         Object result = Primitives.primitiveValueOrNullFor(float.class);
         assertNotNull(result);
         assertTrue("Expected Float but got " + result.getClass().getName(), result instanceof
Float);
         assertEquals(0.0f, ((Float) result).floatValue(), 0.0f);
     }

     @Test
     public void testPrimitiveValueOrNullForDouble() {
         Object result = Primitives.primitiveValueOrNullFor(double.class);
         assertNotNull(result);
         assertTrue("Expected Double but got " + result.getClass().getName(), result instanceof
Double);
         assertEquals(0.0, ((Double) result).doubleValue(), 0.0);
     }

     @Test
     public void testPrimitiveValueOrNullForNullClass() {
         Object result = Primitives.primitiveValueOrNullFor(null);
         assertNull(result);
     }

     @Test
     public void testPrimitiveValueOrNullForNonPrimitiveClass() {
         Object result = Primitives.primitiveValueOrNullFor(String.class);
         assertNull(result);
     }

     @Test
     public void testPrimitiveWrapperOf() {
         assertFalse(Primitives.primitiveWrapperOf(Boolean.class));
         assertEquals('\u0000', Primitives.primitiveWrapperOf(Character.class).charValue());
         assertEquals((byte) 0, Primitives.primitiveWrapperOf(Byte.class).byteValue());
         assertEquals((short) 0, Primitives.primitiveWrapperOf(Short.class).shortValue());
         assertEquals(0, Primitives.primitiveWrapperOf(Integer.class).intValue());
         assertEquals(0L, Primitives.primitiveWrapperOf(Long.class).longValue());
         assertEquals(0.0f, Primitives.primitiveWrapperOf(Float.class).floatValue(), 0.0f);
         assertEquals(0.0, Primitives.primitiveWrapperOf(Double.class).doubleValue(), 0.0);
         assertNull(Primitives.primitiveWrapperOf(null));
     }

     @Test
     public void testPrimitiveTypeOfAndIsPrimitiveWrapper() {
         assertEquals(boolean.class, Primitives.primitiveTypeOf(Boolean.class));
         assertEquals(char.class, Primitives.primitiveTypeOf(Character.class));
         assertEquals(byte.class, Primitives.primitiveTypeOf(Byte.class));
         assertEquals(short.class, Primitives.primitiveTypeOf(Short.class));
         assertEquals(int.class, Primitives.primitiveTypeOf(Integer.class));
         assertEquals(long.class, Primitives.primitiveTypeOf(Long.class));
         assertEquals(float.class, Primitives.primitiveTypeOf(Float.class));
         assertEquals(double.class, Primitives.primitiveTypeOf(Double.class));

         assertEquals(boolean.class, Primitives.primitiveTypeOf(boolean.class));
         assertEquals(int.class, Primitives.primitiveTypeOf(int.class));

         assertTrue(Primitives.isPrimitiveWrapper(Boolean.class));
         assertTrue(Primitives.isPrimitiveWrapper(Integer.class));
         assertTrue(Primitives.isPrimitiveWrapper(Double.class));
         assertFalse(Primitives.isPrimitiveWrapper(String.class));
         assertFalse(Primitives.isPrimitiveWrapper(int.class));
     }
 }