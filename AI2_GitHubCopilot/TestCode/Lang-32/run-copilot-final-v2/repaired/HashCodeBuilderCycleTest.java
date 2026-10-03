package org.apache.commons.lang3.builder;

 import static org.junit.Assert.*;

 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.Set;

 import org.junit.Test;

 /**
  * Tests for HashCodeBuilder focusing on cycle detection and ThreadLocal registry bug (LANG-586).
  */
 public class HashCodeBuilderCycleTest {

     // --- normal cases ---
     @Test
     public void testReflectionHashCodeSimplePojo() {
         Pojo a = new Pojo("test", 42);
         Pojo b = new Pojo("test", 42);
         assertEquals(HashCodeBuilder.reflectionHashCode(a), HashCodeBuilder.reflectionHashCode(b));
     }

     @Test
     public void testReflectionHashCodeEmptyObject() {
         EmptyObj obj = new EmptyObj();
         int hash = HashCodeBuilder.reflectionHashCode(obj);
         assertEquals(17, hash);
     }

     // --- cycle detection ---
     @Test(timeout = 2000)
     public void testReflectionHashCodeCycleDoesNotStackOverflow() {
         SelfRefCycle obj = new SelfRefCycle();
         obj.self = obj;
         int hash = HashCodeBuilder.reflectionHashCode(obj);
         assertTrue(hash >= 0);
     }

     /**
      * The bug: after processing a cycle and unregistering the object, the ThreadLocal's Set
      * should be cleaned up when empty. The buggy code leaves an empty HashSet, so getRegistry()
      * returns [] instead of null. This test directly reproduces the trigger failure.
      */
     @Test
     public void testRegistryCleanupAfterCycleReflection() {
         // Clear any left-over registry state
         Set<IDKey> registry = HashCodeBuilder.getRegistry();
         registry.clear();
         SelfRefCycle obj = new SelfRefCycle();
         obj.self = obj;
         HashCodeBuilder.reflectionHashCode(obj);
         // After processing, the object has been unregistered; the registry should be null
         assertNull("Registry should be null/cleared after all objects unregistered",
                 HashCodeBuilder.getRegistry());
     }

     // --- null object input ---
     @Test(expected = IllegalArgumentException.class)
     public void testReflectionHashCodeNullThrows() {
         HashCodeBuilder.reflectionHashCode(null);
     }

     // --- transient field handling ---
     @Test
     public void testTransientFieldsExcludedByDefault() {
         TransientPojo obj = new TransientPojo();
         obj.normalField = 5;
         obj.transientField = 10;
         int without = HashCodeBuilder.reflectionHashCode(obj, false);
         int with = HashCodeBuilder.reflectionHashCode(obj, true);
         assertTrue("Transient field should affect hash when testTransients=true, but not when
false",
                 without != with);
     }

     // --- exclude fields ---
     @Test
     public void testExcludeFieldsArray() {
         TwoFieldPojo obj = new TwoFieldPojo();
         obj.a = 1;
         obj.b = 2;
         int full = HashCodeBuilder.reflectionHashCode(obj);
         int exclA = HashCodeBuilder.reflectionHashCode(obj, new String[]{"a"});
         int exclB = HashCodeBuilder.reflectionHashCode(obj, new String[]{"b"});

         assertTrue("Excluding a changes hash", full != exclA);
         assertTrue("Excluding b changes hash", full != exclB);
         assertTrue("Excluding different fields gives different hashes", exclA != exclB);
     }

     @Test
     public void testExcludeFieldsCollection() {
         TwoFieldPojo obj = new TwoFieldPojo();
         obj.a = 1;
         obj.b = 2;
         Collection<String> exclude = new HashSet<String>(Arrays.asList("a"));
         int full = HashCodeBuilder.reflectionHashCode(obj);
         int excl = HashCodeBuilder.reflectionHashCode(obj, exclude);
         assertTrue(full != excl);
     }

     // --- manual builder: append(Object) with null and arrays ---
     @Test
     public void testAppendNullObject() {
         HashCodeBuilder builder = new HashCodeBuilder();
         builder.append((Object) null);
         assertEquals(629, builder.toHashCode());
     }

     @Test
     public void testAppendNullArray() {
         HashCodeBuilder builder = new HashCodeBuilder();
         builder.append((int[]) null);
         assertEquals(629, builder.toHashCode());
     }

     @Test
     public void testAppendSuper() {
         HashCodeBuilder builder = new HashCodeBuilder();
         builder.appendSuper(100);
         assertEquals(729, builder.toHashCode());
     }

     // --- constructor validation ---
     @Test(expected = IllegalArgumentException.class)
     public void testConstructorZeroInitial() {
         new HashCodeBuilder(0, 37);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testConstructorEvenInitial() {
         new HashCodeBuilder(2, 37);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testConstructorZeroMultiplier() {
         new HashCodeBuilder(17, 0);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testConstructorEvenMultiplier() {
         new HashCodeBuilder(17, 2);
     }

     // --- direct registry operations ---
     @Test
     public void testRegisterIsRegisteredUnregister() {
         Set<IDKey> reg = HashCodeBuilder.getRegistry();
         reg.clear();
         Object obj = new Object();
         assertFalse(HashCodeBuilder.isRegistered(obj));
         HashCodeBuilder.register(obj);
         assertTrue(HashCodeBuilder.isRegistered(obj));
         HashCodeBuilder.unregister(obj);
         assertFalse(HashCodeBuilder.isRegistered(obj));
     }

     // --- helper POJOs ---
     public static class Pojo {
         public String name;
         public int value;

         public Pojo(String name, int value) {
             this.name = name;
             this.value = value;
         }
     }

     public static class EmptyObj {
         // no fields
     }

     public static class SelfRefCycle {
         public SelfRefCycle self;
     }

     public static class TransientPojo {
         public int normalField;
         public transient int transientField;
     }

     public static class TwoFieldPojo {
         public int a;
         public int b;
     }
 }
