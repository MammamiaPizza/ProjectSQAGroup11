package org.apache.commons.lang;

 import static org.junit.Assert.assertEquals;

 import java.util.Map;

 import org.junit.Test;

 /**
  * Tests for the array-handling fixes in ClassUtils (LANG-535).
  * Covers getShortClassName(Class) and getPackageName(Class) with arrays,
  * plus regression guards for non-aray classes, inner classes, and nulls.
  */
 public class ClassUtilsArrayBugTest {

     // ----------------------------------------------------------------
     // getShortClassName(Class) – array rendering
     // ----------------------------------------------------------------

     @Test
     public void testGetShortClassNameStringArray() {
         assertEquals("String[]", ClassUtils.getShortClassName(String[].class));
     }

     @Test
     public void testGetShortClassNameIntArray() {
         assertEquals("int[]", ClassUtils.getShortClassName(int[].class));
     }

     @Test
     public void testGetShortClassNameBooleanArray() {
         assertEquals("boolean[]", ClassUtils.getShortClassName(boolean[].class));
     }

     @Test
     public void testGetShortClassNameMultiDimArray() {
         assertEquals("String[][]", ClassUtils.getShortClassName(String[][].class));
     }

     @Test
     public void testGetShortClassNameObjectArray() {
         assertEquals("Object[]", ClassUtils.getShortClassName(Object[].class));
     }

     // ----------------------------------------------------------------
     // getPackageName(Class) – array rendering
     // ----------------------------------------------------------------

     @Test
     public void testGetPackageNameStringArray() {
         assertEquals("java.lang", ClassUtils.getPackageName(String[].class));
     }

     @Test
     public void testGetPackageNameIntArray() {
         assertEquals("", ClassUtils.getPackageName(int[].class));
     }

     @Test
     public void testGetPackageNameBooleanArray() {
         assertEquals("", ClassUtils.getPackageName(boolean[].class));
     }

     @Test
     public void testGetPackageNameMultiDimArray() {
         // component is String, so package is java.lang
         assertEquals("java.lang", ClassUtils.getPackageName(String[][].class));
     }

     @Test
     public void testGetPackageNameObjectArray() {
         assertEquals("java.lang", ClassUtils.getPackageName(Object[].class));
     }

     // ----------------------------------------------------------------
     // Regression guards – non-array and inner classes
     // ----------------------------------------------------------------

     @Test
     public void testGetShortClassNameRegularClass() {
         assertEquals("String", ClassUtils.getShortClassName(String.class));
     }

     @Test
     public void testGetShortClassNameInnerClass() {
         // Map.Entry is a public inner interface; its getName() returns "java.util.Map$Entry"
         assertEquals("Map.Entry", ClassUtils.getShortClassName(Map.Entry.class));
     }

     @Test
     public void testGetPackageNameRegularClass() {
         assertEquals("java.lang", ClassUtils.getPackageName(String.class));
     }

     // ----------------------------------------------------------------
     // Null / empty / error-path guards
     // ----------------------------------------------------------------

     @Test
     public void testGetShortClassNameNullClass() {
         assertEquals("", ClassUtils.getShortClassName((Class<?>) null));
     }

     @Test
     public void testGetPackageNameNullClass() {
         assertEquals("", ClassUtils.getPackageName((Class<?>) null));
     }

     @Test
     public void testGetShortClassNameEmptyString() {
         // the String overload
         assertEquals("", ClassUtils.getShortClassName(""));
     }

     @Test
     public void testGetPackageNameEmptyString() {
         assertEquals("", ClassUtils.getPackageName(""));
     }
 }
