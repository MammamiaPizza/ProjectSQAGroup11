package org.mockito.internal.matchers;

 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;

 import org.junit.Test;

 /**
  * Tests for {@link Equality} exposing Bug 484: {@code Equality.areEqual}
  * throws a {@link RuntimeException} when both arguments are arrays.
  * The tests cover the public contract: no exceptions, correct equality
  * for nulls, non-arrays, and arrays (primitive, object, nested).
  */
 public class EqualityTest {

     // ---- null handling --------------------------------------------------------

     @Test
     public void shouldKnowIfObjectsAreEqual_bothNull() {
         assertTrue(Equality.areEqual(null, null));
     }

     @Test
     public void shouldKnowIfObjectsAreEqual_oneNullOtherNonNull() {
         assertFalse(Equality.areEqual(null, new Object()));
         assertFalse(Equality.areEqual(new Object(), null));
     }

     // ---- non‑array objects ----------------------------------------------------

     @Test
     public void shouldKnowIfObjectsAreEqual_sameObjectReference() {
         Object obj = new Object();
         assertTrue(Equality.areEqual(obj, obj));
     }

     @Test
     public void shouldKnowIfObjectsAreEqual_differentTypes() {
         assertFalse(Equality.areEqual("hello", 42));
         assertFalse(Equality.areEqual(42, "hello"));
     }

     @Test
     public void shouldKnowIfObjectsAreEqual_objectMeetsEqualsContract() {
         assertTrue(Equality.areEqual(100, 100));
         assertFalse(Equality.areEqual(100, 200));
     }

     // ---- primitive‑array paths (Bug‑484 trigger) -----------------------------

     @Test
     public void shouldKnowIfObjectsAreEqual_primitiveArraysEqual() {
         assertTrue(Equality.areEqual(new int[] {1, 2, 3}, new int[] {1, 2, 3}));
         assertTrue(Equality.areEqual(new byte[] {1, 2}, new byte[] {1, 2}));
         assertTrue(Equality.areEqual(new char[] {'a', 'b'}, new char[] {'a', 'b'}));
     }

     @Test
     public void shouldKnowIfObjectsAreEqual_primitiveArraysDifferentLength() {
         assertFalse(Equality.areEqual(new int[] {1, 2}, new int[] {1, 2, 3}));
         assertFalse(Equality.areEqual(new float[] {1.0f}, new float[] {1.0f, 2.0f}));
     }

     @Test
     public void shouldKnowIfObjectsAreEqual_primitiveArraysDifferentElements() {
         assertFalse(Equality.areEqual(new int[] {1, 2, 3}, new int[] {1, 2, 4}));
         assertFalse(Equality.areEqual(new double[] {1.0}, new double[] {2.0}));
     }

     @Test
     public void shouldKnowIfObjectsAreEqual_emptyPrimitiveArrays() {
         assertTrue(Equality.areEqual(new int[] {}, new int[] {}));
         assertFalse(Equality.areEqual(new int[] {}, new int[] {1}));
         assertFalse(Equality.areEqual(new int[] {1}, new int[] {}));
     }

     // ---- object‑array paths --------------------------------------------------

     @Test
     public void shouldKnowIfObjectsAreEqual_stringArrays() {
         assertTrue(Equality.areEqual(new String[] {"a", "b"}, new String[] {"a", "b"}));
         assertFalse(Equality.areEqual(new String[] {"a"}, new String[] {"a", "b"}));
         assertFalse(Equality.areEqual(new String[] {"a", "b"}, new String[] {"a", "c"}));
     }

     @Test
     public void shouldKnowIfObjectsAreEqual_nestedObjectArrays() {
         Object[] a = {new int[] {1, 2}, "x"};
         Object[] b = {new int[] {1, 2}, "x"};
         assertTrue(Equality.areEqual(a, b));

         Object[] c = {new int[] {1, 2}, "y"};
         assertFalse(Equality.areEqual(a, c));
     }

     @Test
     public void shouldKnowIfObjectsAreEqual_boxedPrimitiveArrays() {
         assertTrue(Equality.areEqual(new Integer[] {1, 2}, new Integer[] {1, 2}));
         assertFalse(Equality.areEqual(new Integer[] {1, 2}, new Integer[] {1, 3}));
     }

     @Test
     public void shouldKnowIfObjectsAreEqual_arrayOfNullElements() {
         String[] a = {null, "b"};
         String[] b = {null, "b"};
         assertTrue(Equality.areEqual(a, b));

         String[] c = {null, "d"};
         assertFalse(Equality.areEqual(a, c));
     }

     @Test
     public void shouldKnowIfObjectsAreEqual_arrayVersusNonArray() {
         assertFalse(Equality.areEqual(new int[] {1}, "not an array"));
         assertFalse(Equality.areEqual("not an array", new String[] {"x"}));
     }

     // ---- isArray helper -------------------------------------------------------

     @Test
     public void isArray_classifiesArraysCorrectly() {
         assertTrue(Equality.isArray(new int[] {}));
         assertTrue(Equality.isArray(new Object[] {}));
         assertFalse(Equality.isArray("string"));
         assertFalse(Equality.isArray(42));
         assertFalse(Equality.isArray(new Object()));
     }
 }
