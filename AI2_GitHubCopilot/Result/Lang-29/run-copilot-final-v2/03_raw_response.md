package org.apache.commons.lang3;

 import static org.junit.Assert.*;
 import org.junit.Test;

 public class SystemUtilsTest {

     @Test
     public void testToJavaVersionIntValid() {
         assertEquals(160, (int) SystemUtils.toJavaVersionInt("1.6.0_20"));
         assertEquals(170, (int) SystemUtils.toJavaVersionInt("1.7.0"));
         assertEquals(150, (int) SystemUtils.toJavaVersionInt("1.5"));
         assertEquals(120, (int) SystemUtils.toJavaVersionInt("1.2"));
         assertEquals(100, (int) SystemUtils.toJavaVersionInt("1.0"));
         assertEquals(100, (int) SystemUtils.toJavaVersionInt("1.0.0"));
         assertEquals(234, (int) SystemUtils.toJavaVersionInt("2.3.4"));
         assertEquals(123, (int) SystemUtils.toJavaVersionInt("1.2.3.4"));
     }

     @Test
     public void testToJavaVersionIntBoundary() {
         assertEquals(0, (int) SystemUtils.toJavaVersionInt(null));
         assertEquals(0, (int) SystemUtils.toJavaVersionInt(""));
         assertEquals(0, (int) SystemUtils.toJavaVersionInt("Java"));
     }

     @Test
     public void testToJavaVersionIntReturnTypeBug() {
         Object result = SystemUtils.toJavaVersionInt("1.5");
         assertFalse("toJavaVersionInt should return int, not float", result instanceof Float);
     }

     @Test
     public void testToJavaVersionIntNullEqualsZero() {
         assertEquals(Integer.valueOf(0), SystemUtils.toJavaVersionInt(null));
     }

     @Test
     public void testToJavaVersionFloatValid() {
         assertEquals(1.6f, SystemUtils.toJavaVersionFloat("1.6.0_20"), 0.001f);
         assertEquals(1.7f, SystemUtils.toJavaVersionFloat("1.7.0"), 0.001f);
         assertEquals(1.5f, SystemUtils.toJavaVersionFloat("1.5"), 0.001f);
     }

     @Test
     public void testToJavaVersionFloatNull() {
         assertEquals(0.0f, SystemUtils.toJavaVersionFloat(null), 0.001f);
     }

     @Test
     public void testToJavaVersionFloatInvalid() {
         assertEquals(0.0f, SystemUtils.toJavaVersionFloat(""), 0.001f);
         assertEquals(0.0f, SystemUtils.toJavaVersionFloat("NoVersionHere"), 0.001f);
     }

     @Test
     public void testToJavaVersionIntArrayValid() {
         assertArrayEquals(new int[]{1, 6, 0, 20}, SystemUtils.toJavaVersionIntArray("1.6.0_20"));
         assertArrayEquals(new int[]{1, 7, 0}, SystemUtils.toJavaVersionIntArray("1.7.0"));
         assertArrayEquals(new int[]{1, 5}, SystemUtils.toJavaVersionIntArray("1.5"));
     }

     @Test
     public void testToJavaVersionIntArrayNull() {
         assertArrayEquals(new int[0], SystemUtils.toJavaVersionIntArray(null));
     }

     @Test
     public void testIsJavaVersionAtLeastInt() {
         assertTrue(SystemUtils.isJavaVersionAtLeast(0));
         assertTrue(SystemUtils.isJavaVersionAtLeast(1));
         assertFalse(SystemUtils.isJavaVersionAtLeast(99999));
     }

     @Test
     public void testIsJavaVersionAtLeastFloat() {
         assertTrue(SystemUtils.isJavaVersionAtLeast(0.0f));
         assertFalse(SystemUtils.isJavaVersionAtLeast(999.9f));
     }

     @Test
     public void testIsJavaAwtHeadless() {
         boolean result = SystemUtils.isJavaAwtHeadless();
     }
 }