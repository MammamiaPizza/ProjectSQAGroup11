package org.apache.commons.lang3;

 import static org.junit.Assert.*;

 import org.junit.Test;

 /**
  * Tests for LANG-571: {@code add} and {@code addAll} must return an array of the correct
  * component type (e.g. String[]), not Object[].
  */
 public class ArrayUtilsAddReturnTypeTest {

     // --- add(T[], T) ----------------------------------------------------------------------------

     @Test
     public void testAddStringArray_returnsStringArrayNotObjectArray() {
         String[] input = { "a", "b" };
         String[] result = ArrayUtils.add(input, "c");
         // The critical assertion: result class must be String[], not Object[]
         assertSame(String[].class, result.getClass());
         // Content must be correct
         assertArrayEquals(new String[] { "a", "b", "c" }, result);
     }

     @Test
     public void testAddStringArrayWithNullElement_returnsStringArray() {
         String[] input = { "a", null, "b" };
         String[] result = ArrayUtils.add(input, (String) null);
         assertSame(String[].class, result.getClass());
         assertArrayEquals(new String[] { "a", null, "b", null }, result);
     }

     @Test
     public void testAddEmptyStringArray_returnsStringArray() {
         String[] input = new String[0];
         String[] result = ArrayUtils.add(input, "x");
         assertSame(String[].class, result.getClass());
         assertArrayEquals(new String[] { "x" }, result);
     }

     @Test
     public void testAddIntegerArray_returnsIntegerArray() {
         Integer[] input = { 1, 2 };
         Integer[] result = ArrayUtils.add(input, 3);
         assertSame(Integer[].class, result.getClass());
         assertArrayEquals(new Integer[] { 1, 2, 3 }, result);
     }

     @Test
     public void testAddDoesNotModifyOriginalArray() {
         String[] original = { "x" };
         String[] result = ArrayUtils.add(original, "y");
         assertNotSame("Result must be a new array", original, result);
         assertArrayEquals(new String[] { "x" }, original);
         assertArrayEquals(new String[] { "x", "y" }, result);
     }

     // --- addAll(T[], T...) ----------------------------------------------------------------------

     @Test
     public void testAddAllStringArray_returnsStringArrayNotObjectArray() {
         String[] array1 = { "a" };
         String[] array2 = { "b", "c" };
         String[] result = ArrayUtils.addAll(array1, array2);
         assertSame(String[].class, result.getClass());
         assertArrayEquals(new String[] { "a", "b", "c" }, result);
     }

     @Test
     public void testAddAllNullSecondArray_preservesTypeOfFirst() {
         String[] array1 = { "a" };
         String[] result = ArrayUtils.addAll(array1, (String[]) null);
         assertSame(String[].class, result.getClass());
         assertArrayEquals(new String[] { "a" }, result);
     }

     @Test
     public void testAddAllNullFirstArray_preservesTypeOfSecond() {
         String[] array2 = { "a", "b" };
         String[] result = ArrayUtils.addAll(null, array2);
         assertSame(String[].class, result.getClass());
         assertArrayEquals(new String[] { "a", "b" }, result);
     }

     @Test
     public void testAddAllEmptyFirstArray_returnsTypeOfSecond() {
         String[] array1 = new String[0];
         String[] array2 = { "x", "y" };
         String[] result = ArrayUtils.addAll(array1, array2);
         assertSame(String[].class, result.getClass());
         assertArrayEquals(new String[] { "x", "y" }, result);
     }

     @Test
     public void testAddAllEmptySecondArray_returnsTypeOfFirst() {
         String[] array1 = { "p", "q" };
         String[] array2 = new String[0];
         String[] result = ArrayUtils.addAll(array1, array2);
         assertSame(String[].class, result.getClass());
         assertArrayEquals(new String[] { "p", "q" }, result);
     }

     @Test
     public void testAddAllBothNull_returnsNull() {
         // Based on addAll implementation: if both null, should return a clone of array2,
         // but array2 is null, so clone(null) returns null. (Adjust if needed)
         assertNull(ArrayUtils.addAll(null, (String[]) null));
     }

     @Test
     public void testAddAllIncompatibleTypes_throwsIllegalArgumentException() {
         String[] array1 = { "a" };
         Integer[] array2 = { 1, 2 };
         try {
             ArrayUtils.addAll(array1, array2);
             fail("Expected IllegalArgumentException for incompatible types");
         } catch (IllegalArgumentException e) {
             // expected – message should mention incompatible types
             assertTrue(e.getMessage().contains("Cannot store"));
         }
     }

@Test
    public void testAddNullByteArray_returnsByteArray() {
        byte[] result = ArrayUtils.add((byte[]) null, (byte) 42);
        assertSame(byte[].class, result.getClass());
        assertArrayEquals(new byte[]{42}, result);
    }

 @Test
 public void testAddNullCharArray_returnsCharArray() {
     char[] result = ArrayUtils.add((char[]) null, 'x');
     assertSame(char[].class, result.getClass());
     assertArrayEquals(new char[]{'x'}, result);
 }

 @Test
 public void testAddNullByteArrayAtIndexZero_returnsByteArray() {
     byte[] result = ArrayUtils.add((byte[]) null, 0, (byte) 42);
     assertSame(byte[].class, result.getClass());
     assertArrayEquals(new byte[]{42}, result);
 }

 @Test(expected = IndexOutOfBoundsException.class)
 public void testAddNullByteArrayAtIndexNonZero_throwsException() {
     ArrayUtils.add((byte[]) null, 1, (byte) 42);
 }
}
