package com.fasterxml.jackson.databind.type;

 import java.util.*;
 import static org.junit.Assert.*;
 import org.junit.Test;

 public class SimpleTypeTest {

     public static class TestBean {
         public String name;
     }

     // 1. construct() with a normal class returns valid SimpleType
     @Test
     public void testConstructNormalClass() {
         SimpleType type = SimpleType.construct(String.class);
         assertNotNull(type);
         assertEquals(String.class, type.getRawClass());
         assertFalse(type.isContainerType());
         assertEquals("java.lang.String", type.buildCanonicalName());
     }

     // 2. construct() throws IAE for Map, Collection, and array types
     @Test
     public void testConstructThrowsForContainers() {
         try {
             SimpleType.construct(HashMap.class);
             fail("Should throw IAE for Map");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Map"));
         }
         try {
             SimpleType.construct(ArrayList.class);
             fail("Should throw IAE for Collection");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Collection"));
         }
         try {
             SimpleType.construct(int[].class);
             fail("Should throw IAE for array");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("array"));
         }
     }

     // 3. constructUnsafe() bypasses container-type checks
     @Test
     public void testConstructUnsafeBypassesChecks() {
         SimpleType mapType = SimpleType.constructUnsafe(HashMap.class);
         assertNotNull(mapType);
         assertEquals(HashMap.class, mapType.getRawClass());

         SimpleType listType = SimpleType.constructUnsafe(ArrayList.class);
         assertNotNull(listType);
         assertEquals(ArrayList.class, listType.getRawClass());

         SimpleType arrayType = SimpleType.constructUnsafe(int[].class);
         assertNotNull(arrayType);
         assertEquals(int[].class, arrayType.getRawClass());
     }

     // 4. construct() handles inner classes correctly
     @Test
     public void testConstructInnerClass() {
         SimpleType type = SimpleType.construct(TestBean.class);
         assertNotNull(type);
         assertEquals(TestBean.class, type.getRawClass());
         assertFalse(type.isContainerType());
         String canonical = type.buildCanonicalName();
         assertTrue(canonical.contains("SimpleTypeTest$TestBean"));
     }

     // 5. constructUnsafe() with null argument - expect NullPointerException
     @Test(expected = NullPointerException.class)
     public void testConstructUnsafeNullClass() {
         SimpleType.constructUnsafe(null);
     }

     // 6. buildCanonicalName() produces correct output
     @Test
     public void testBuildCanonicalName() {
         assertEquals("java.lang.Integer",
SimpleType.construct(Integer.class).buildCanonicalName());
         assertEquals("java.lang.Double", SimpleType.construct(Double.class).buildCanonicalName());
         // No generics brackets when bindings are empty
         String name = SimpleType.construct(String.class).buildCanonicalName();
         assertFalse(name.contains("<"));
     }

     // 7. toString() follows documented format
     @Test
     public void testToString() {
         String str = SimpleType.construct(Long.class).toString();
         assertTrue(str.startsWith("[simple type, class"));
         assertTrue(str.contains("java.lang.Long"));
         assertTrue(str.endsWith("]"));
     }

     // 8. equals() compares class and bindings
     @Test
     public void testEquals() {
         SimpleType t1 = SimpleType.construct(String.class);
         SimpleType t2 = SimpleType.construct(String.class);
         SimpleType t3 = SimpleType.construct(Integer.class);

         assertEquals(t1, t2);
         assertNotEquals(t1, t3);
         assertEquals(t1, t1);            // reflexivity
         assertNotEquals(t1, null);       // null
         assertNotEquals(t1, new Object()); // different class
     }

     // 9. withTypeHandler() creates new instance or returns this when unchanged
     @Test
     public void testWithTypeHandler() {
         SimpleType type = SimpleType.construct(String.class);
         Object handler = new Object();

         SimpleType with = type.withTypeHandler(handler);
         assertNotSame(type, with);
         assertEquals(String.class, with.getRawClass());

         // same handler returns this
         assertSame(with, with.withTypeHandler(handler));

         // null handler when _typeHandler is null returns this
         assertSame(type, type.withTypeHandler(null));
     }

     // 10. withContentType() and related methods throw IAE for simple types
     @Test(expected = IllegalArgumentException.class)
     public void testWithContentTypeThrows() {
         SimpleType.construct(String.class).withContentType(null);
     }

     // 11. withStaticTyping() toggles the static flag
     @Test
     public void testWithStaticTyping() {
         SimpleType type = SimpleType.construct(Boolean.class);
         SimpleType staticType = type.withStaticTyping();

         assertNotSame(type, staticType);
         assertEquals(Boolean.class, staticType.getRawClass());
         assertFalse(type.isContainerType());

         // calling again returns same instance
         assertSame(staticType, staticType.withStaticTyping());
     }

     // 12. _narrow() returns this for same class, new instance for subclass
     @Test
     public void testNarrow() {
         SimpleType type = SimpleType.construct(Number.class);

         // same class -> same instance
         assertSame(type, type._narrow(Number.class));

         // subclass -> new instance with subclass rawClass
         JavaType narrowed = type._narrow(Integer.class);
         assertNotNull(narrowed);
         assertNotSame(type, narrowed);
         assertEquals(Integer.class, narrowed.getRawClass());
         assertTrue(narrowed instanceof SimpleType);
     }

 }
