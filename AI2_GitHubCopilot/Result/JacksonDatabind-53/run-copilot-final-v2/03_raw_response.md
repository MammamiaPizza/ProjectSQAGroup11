package com.fasterxml.jackson.databind.type;

 import java.util.*;

 import com.fasterxml.jackson.databind.JavaType;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for TypeBindings and TypeFactory focusing on Map type handling
  * (related to Defects4J bug 53 / Jackson-databind issue 1215).
  *
  * Bug: Map type with abstract value type (e.g., Map<String, HasUniqueId>)
  * failed during deserialization because TypeBindings for Map did not properly
  * propagate type variables "K" and "V".
  */
 public class TypeBindingsMapTest {

     // ---- Helpers ----

     private static JavaType stringType() {
         return TypeFactory.defaultInstance().constructType(String.class);
     }

     private static JavaType objectType() {
         return TypeFactory.defaultInstance().constructType(Object.class);
     }

     private static JavaType mapType(JavaType key, JavaType value) {
         return TypeFactory.defaultInstance().constructMapType(
                 (Class<? extends Map>) Map.class, key, value);
     }

     // ---- Normal: Map type bindings contain K and V ----

     @Test
     public void testMapTypeBindingsHasKeyAndValue() {
         JavaType mapT = mapType(stringType(), objectType());
         TypeBindings bindings = mapT.getBindings();

         assertEquals("Should have 2 type parameters", 2, bindings.size());

         // Verify names
         String name0 = bindings.getBoundName(0);
         String name1 = bindings.getBoundName(1);
         // Map has type variables {K, V}
         assertTrue("First type parameter should be K or V",
                 "K".equals(name0) || "V".equals(name0));
         assertTrue("Second type parameter should be K or V",
                 "K".equals(name1) || "V".equals(name1));
         assertNotEquals(name0, name1);
     }

     @Test
     public void testMapTypeBindingsGetBoundTypeForKey() {
         JavaType key = stringType();
         JavaType val = objectType();
         JavaType mapT = mapType(key, val);
         TypeBindings bindings = mapT.getBindings();

         JavaType boundKey = bindings.findBoundType("K");
         assertNotNull("K should be bound", boundKey);
         assertEquals("K should resolve to String", key, boundKey);

         JavaType boundVal = bindings.findBoundType("V");
         assertNotNull("V should be bound", boundVal);
         assertEquals("V should resolve to Object", val, boundVal);
     }

     @Test
     public void testMapTypeBindingsWithAbstractValueType() {
         // This is the bug-reproducing case: Map<String, HasUniqueId>
         // where HasUniqueId is abstract. The bindings should still resolve V correctly.
         JavaType key = stringType();
         // For testing without the exact HasUniqueId class, use a known abstract
         // supertype like Number as a proxy for "abstract value type"
         JavaType val = TypeFactory.defaultInstance().constructType(Number.class);
         JavaType mapT = mapType(key, val);
         TypeBindings bindings = mapT.getBindings();

         assertEquals(2, bindings.size());
         JavaType boundV = bindings.findBoundType("V");
         assertNotNull("V should be bound even for abstract value type", boundV);
         assertEquals(val, boundV);
     }

     // ---- Normal: withUnboundVariable ----

     @Test
     public void testWithUnboundVariablePreservesExistingBindings() {
         JavaType mapT = mapType(stringType(), objectType());
         TypeBindings original = mapT.getBindings();

         TypeBindings augmented = original.withUnboundVariable("X");

         // Original bindings still accessible
         assertNotNull(augmented.findBoundType("K"));
         assertNotNull(augmented.findBoundType("V"));

         // New unbound variable should return null (not bound)
         assertNull(augmented.findBoundType("X"));

         // Size should be larger
         assertTrue(augmented.size() >= original.size());
     }

     @Test
     public void testWithUnboundVariableDoesNotMutateOriginal() {
         JavaType mapT = mapType(stringType(), objectType());
         TypeBindings original = mapT.getBindings();

         TypeBindings augmented = original.withUnboundVariable("Z");

         // Original bindings for K and V must remain valid, even if
         // internal bookkeeping changes. This checks that the critical
         // type information is not lost.
         assertEquals("K should still be bound to String in original",
                 stringType(), original.findBoundType("K"));
         assertEquals("V should still be bound to Object in original",
                 objectType(), original.findBoundType("V"));

         // Augmented should carry the new (unbound) variable
         assertNull("Z should be unbound in augmented", augmented.findBoundType("Z"));
         // Augmented also retains the original bindings
         assertNotNull("Augmented should still have K", augmented.findBoundType("K"));
         assertNotNull("Augmented should still have V", augmented.findBoundType("V"));
     }

     // ---- Normal: TypeBindings.create with Map erased type ----

     @Test
     public void testCreateWithMapClassAndTwoTypeArgs() {
         JavaType key = stringType();
         JavaType val = objectType();

         TypeBindings bindings = TypeBindings.create(Map.class, key, val);

         assertEquals(2, bindings.size());
         assertEquals(key, bindings.findBoundType("K"));
         assertEquals(val, bindings.findBoundType("V"));
     }

     @Test
     public void testCreateWithMapClassAndTypeArray() {
         JavaType key = stringType();
         JavaType val = objectType();

         TypeBindings bindings = TypeBindings.create(Map.class, new JavaType[]{key, val});

         assertEquals(2, bindings.size());
         assertEquals(key, bindings.findBoundType("K"));
         assertEquals(val, bindings.findBoundType("V"));
     }

     @Test
     public void testCreateIfNeededWithMapAndTwoArgs() {
         JavaType key = stringType();
         JavaType val = objectType();

         TypeBindings bindings = TypeBindings.createIfNeeded(Map.class,
                 new JavaType[]{key, val});

         assertEquals(2, bindings.size());
         assertNotNull(bindings.findBoundType("K"));
         assertNotNull(bindings.findBoundType("V"));
     }

     // ---- Boundary: empty / single param / mismatch ----

     @Test
     public void testEmptyBindings() {
         TypeBindings empty = TypeBindings.emptyBindings();
         assertTrue(empty.isEmpty());
         assertEquals(0, empty.size());
         assertNull(empty.findBoundType("K"));
     }

     @Test
     public void testCreateWithSingleParamForMapThrows() {
         try {
             TypeBindings.create(Map.class, stringType());
             fail("Expected IllegalArgumentException for single param on Map (needs 2)");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("class expects"));
         }
     }

     @Test
     public void testCreateWithWrongNumberOfTypeArgsThrows() {
         try {
             TypeBindings.create(Map.class, new JavaType[]{stringType()});
             fail("Expected IllegalArgumentException for 1 param on Map (needs 2)");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("class expects"));
         }
 }