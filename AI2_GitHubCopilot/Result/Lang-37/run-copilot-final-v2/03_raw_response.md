package org.apache.commons.lang3;

 import static org.junit.Assert.*;

 import java.io.Serializable;
 import org.junit.Test;

 /**
  * Tests for ArrayUtils.add(T[], int, T) and related methods, targeting bug LANG-567.
  * The correct contract requires the new array to use the original array's component type,
  * not the element's type.
  */
 public class ArrayUtilsAddTest {

     // ---- tests for add(T[], int, T) ----

     /**
      * Adding a Double to a Number[] containing diverse subtypes must succeed;
      * bug LANG-567 throws ArrayStoreException because it creates Double[] instead of Number[].
      */
     @Test
     public void testAddDoubleToMixedNumberArray() {
         Number[] arr = {1, 2.5, 3L};
         Number[] result = ArrayUtils.add(arr, 1, 4.2);
         assertNotNull(result);
         assertEquals(Number.class, result.getClass().getComponentType());
         assertEquals(arr.length + 1, result.length);
         assertEquals(arr[0], result[0]);
         assertEquals(4.2, result[1]);
         assertEquals(arr[1], result[2]);
         assertEquals(arr[2], result[3]);
     }

     /**
      * Adding a Float to Number[] with mixed numbers should work.
      */
     @Test
     public void testAddFloatToMixedNumberArray() {
         Number[] arr = {1, 2.5, 3L};
         Number[] result = ArrayUtils.add(arr, 0, 1.1f);
         assertNotNull(result);
         assertEquals(Number.class, result.getClass().getComponentType());
         assertEquals(arr.length + 1, result.length);
         assertEquals(1.1f, result[0]);
         assertEquals(arr[0], result[1]);
         assertEquals(arr[1], result[2]);
         assertEquals(arr[2], result[3]);
     }

     /**
      * Adding null to an Object[] is allowed.
      */
     @Test
     public void testAddNullToObjectArray() {
         Object[] arr = {"a", 1};
         Object[] result = ArrayUtils.add(arr, 1, null);
         assertNotNull(result);
         assertEquals(Object.class, result.getClass().getComponentType());
         assertEquals(3, result.length);
         assertEquals("a", result[0]);
         assertNull(result[1]);
         assertEquals(1, result[2]);
     }

     /**
      * Adding an element to an empty array produces a single-element array with the
      * original component type.
      */
     @Test
     public void testAddToEmptyArray() {
         String[] arr = {};
         String[] result = ArrayUtils.add(arr, 0, "hello");
         assertNotNull(result);
         assertEquals(String.class, result.getClass().getComponentType());
         assertEquals(1, result.length);
         assertEquals("hello", result[0]);
     }

     /**
      * Adding at index 0.
      */
     @Test
     public void testAddAtZeroIndex() {
         String[] arr = {"b", "c"};
         String[] result = ArrayUtils.add(arr, 0, "a");
         assertArrayEquals(new String[]{"a", "b", "c"}, result);
     }

     /**
      * Adding at the end (index == length) appends the element.
      */
     @Test
     public void testAddAtEndIndex() {
         String[] arr = {"a", "b"};
         String[] result = ArrayUtils.add(arr, arr.length, "c");
         assertArrayEquals(new String[]{"a", "b", "c"}, result);
     }

     /**
      * The new array must use the original array's component type, not the element's type.
      */
     @Test
     public void testResultComponentTypeIsOriginalArrayComponentType() {
         Number[] arr = {1, 2};
         Number[] result = ArrayUtils.add(arr, 0, 3.14);
         assertTrue("Result component type must be Number.class, not Double.class",
                 result.getClass().getComponentType().equals(Number.class));
     }

     /**
      * Adding an element that is exactly the component type (no subtype relation).
      */
     @Test
     public void testAddSameTypeElement() {
         String[] arr = {"x", "y"};
         String[] result = ArrayUtils.add(arr, 1, "z");
         assertArrayEquals(new String[]{"x", "z", "y"}, result);
     }

     /**
      * Adding to an interface type array (Serializable) with an implementing element.
      */
     @Test
     public void testAddToInterfaceArray() {
         Serializable[] arr = { "hello", 42 };
         Serializable[] result = ArrayUtils.add(arr, 1, new java.util.Date());
         assertNotNull(result);
         assertEquals(Serializable.class, result.getClass().getComponentType());
         assertEquals(arr.length + 1, result.length);
         assertEquals(arr[0], result[0]);
         assertEquals(new java.util.Date(), result[1]);
         assertEquals(arr[1], result[2]);
     }

     /**
      * Negative index should throw IndexOutOfBoundsException.
      */
     @Test(expected = IndexOutOfBoundsException.class)
     public void testAddWithNegativeIndexThrows() {
         ArrayUtils.add(new String[]{"a"}, -1, "b");
     }

     /**
      * Index greater than length should throw IndexOutOfBoundsException.
      */
     @Test(expected = IndexOutOfBoundsException.class)
     public void testAddWithIndexGreaterThanLengthThrows() {
         ArrayUtils.add(new String[]{"a"}, 2, "b");
     }

     /**
      * A null array argument should throw a NullPointerException (contract check).
      */
     @Test(expected = NullPointerException.class)
     public void testAddNullArrayThrows() {
         ArrayUtils.add((String[]) null, 0, "x");
     }
 }