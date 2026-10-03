package com.fasterxml.jackson.databind.introspect;

 import static org.junit.Assert.*;
 import java.lang.annotation.*;
 import java.util.*;
 import org.junit.Test;

 public class AnnotationMapTest {

     @Retention(RetentionPolicy.RUNTIME)
     public @interface A { String value() default ""; }

     @Retention(RetentionPolicy.RUNTIME)
     public @interface B { String value() default ""; }

     @A("primary")
     static final class PrimaryA {}

     @A("secondary")
     static final class SecondaryA {}

     @B("baz")
     static final class WithB {}

     @Test
     public void testMergeSecondaryOverridesPrimary() {
         AnnotationMap primary = new AnnotationMap();
         primary.add(PrimaryA.class.getAnnotation(A.class));
         AnnotationMap secondary = new AnnotationMap();
         secondary.add(SecondaryA.class.getAnnotation(A.class));

         AnnotationMap merged = AnnotationMap.merge(primary, secondary);

         assertNotNull(merged);
         A a = merged.get(A.class);
         assertNotNull(a);
         assertEquals("secondary", a.value());
     }

     @Test
     public void testMergeNullPrimary() {
         AnnotationMap secondary = new AnnotationMap();
         secondary.add(SecondaryA.class.getAnnotation(A.class));

         AnnotationMap merged = AnnotationMap.merge(null, secondary);

         assertSame(secondary, merged);
     }

     @Test
     public void testMergeNullSecondary() {
         AnnotationMap primary = new AnnotationMap();
         primary.add(PrimaryA.class.getAnnotation(A.class));

         AnnotationMap merged = AnnotationMap.merge(primary, null);

         assertSame(primary, merged);
     }

     @Test
     public void testMergeEmptyPrimary() {
         AnnotationMap primary = new AnnotationMap(); // empty
         AnnotationMap secondary = new AnnotationMap();
         secondary.add(SecondaryA.class.getAnnotation(A.class));

         AnnotationMap merged = AnnotationMap.merge(primary, secondary);

         assertSame(secondary, merged);
     }

     @Test
     public void testMergeEmptySecondary() {
         AnnotationMap primary = new AnnotationMap();
         primary.add(PrimaryA.class.getAnnotation(A.class));
         AnnotationMap secondary = new AnnotationMap();

         AnnotationMap merged = AnnotationMap.merge(primary, secondary);

         assertSame(primary, merged);
     }

     @Test
     public void testMergeDisjointAnnotations() {
         AnnotationMap primary = new AnnotationMap();
         primary.add(PrimaryA.class.getAnnotation(A.class));
         AnnotationMap secondary = new AnnotationMap();
         secondary.add(WithB.class.getAnnotation(B.class));

         AnnotationMap merged = AnnotationMap.merge(primary, secondary);

         assertEquals(2, merged.size());
         assertNotNull(merged.get(A.class));
         assertNotNull(merged.get(B.class));
     }

     @Test
     public void testMergeBothNull() {
         assertNull(AnnotationMap.merge(null, null));
     }

     @Test
     public void testAddNewAnnotationReturnsTrue() {
         AnnotationMap map = new AnnotationMap();

         boolean result = map.add(PrimaryA.class.getAnnotation(A.class));

         assertTrue(result);
         assertNotNull(map.get(A.class));
     }

     @Test
     public void testAddExistingAnnotationReturnsFalse() {
         AnnotationMap map = new AnnotationMap();
         Annotation ann = PrimaryA.class.getAnnotation(A.class);
         map.add(ann);

         boolean result = map.add(ann);

         assertFalse(result);
         assertEquals(1, map.size());
         assertEquals(ann, map.get(A.class));
     }

     @Test
     public void testAddIfNotPresent() {
         AnnotationMap map = new AnnotationMap();
         Annotation ann = PrimaryA.class.getAnnotation(A.class);

         assertTrue(map.addIfNotPresent(ann));
         assertFalse(map.addIfNotPresent(ann));
         assertEquals(ann, map.get(A.class));
     }

     @Test
     public void testSize() {
         AnnotationMap map = new AnnotationMap();
         assertEquals(0, map.size());
         map.add(PrimaryA.class.getAnnotation(A.class));
         assertEquals(1, map.size());
         map.add(PrimaryA.class.getAnnotation(A.class));
         assertEquals(1, map.size());
     }

     @Test
     public void testGetReturnsNullWhenEmpty() {
         AnnotationMap map = new AnnotationMap();
         assertNull(map.get(A.class));
     }

     @Test
     public void testAnnotationsIterable() {
         AnnotationMap map = new AnnotationMap();
         assertFalse(map.annotations().iterator().hasNext());
         map.add(PrimaryA.class.getAnnotation(A.class));
         map.add(WithB.class.getAnnotation(B.class));
         Iterator<Annotation> it = map.annotations().iterator();
         assertTrue(it.hasNext());
         Set<Class<?>> types = new HashSet<Class<?>>();
         while (it.hasNext()) {
             types.add(it.next().annotationType());
         }
         assertEquals(2, types.size());
         assertTrue(types.contains(A.class));
         assertTrue(types.contains(B.class));
     }
 }