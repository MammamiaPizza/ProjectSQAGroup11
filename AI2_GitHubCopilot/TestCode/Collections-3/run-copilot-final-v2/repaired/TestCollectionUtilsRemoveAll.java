package org.apache.commons.collections;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.List;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for CollectionUtils.removeAll with cardinality-aware removal semantics.
  * Bug COLLECTIONS-219: removeAll incorrectly delegates to ListUtils.retainAll
  * instead of performing cardinality-based subtraction.
  */
 public class TestCollectionUtilsRemoveAll {

     // --- removeAll cardinality tests ---

     @Test
     public void testRemoveAllWithDuplicatesInSourceOnly() {
         // Source has duplicates of "A" remove has only one "A".
         // Expected: one "A" remains after cardinality subtraction (2 - 1 = 1).
         List<String> source = new ArrayList<String>(Arrays.asList("A", "A", "B"));
         List<String> remove = new ArrayList<String>(Arrays.asList("A"));

         Collection result = CollectionUtils.removeAll(source, remove);

         assertEquals("Should retain one A and one B", 2, result.size());
         assertEquals("Cardinality of A should be 1", 1,
                 CollectionUtils.cardinality("A", result));
         assertEquals("Cardinality of B should be 1", 1,
                 CollectionUtils.cardinality("B", result));
     }

     @Test
     public void testRemoveAllWithRemoveHasExtraCopies() {
         // Remove has more copies than source; source should be fully depleted of that element.
         List<String> source = new ArrayList<String>(Arrays.asList("A", "B"));
         List<String> remove = new ArrayList<String>(Arrays.asList("A", "A", "A"));

         Collection result = CollectionUtils.removeAll(source, remove);

         assertEquals("Only B should remain", 1, result.size());
         assertTrue("Result should contain B", result.contains("B"));
         assertFalse("Result should not contain A", result.contains("A"));
     }

     @Test
     public void testRemoveAllDisjointCollections() {
         // No elements in common.
         List<String> source = new ArrayList<String>(Arrays.asList("A", "B"));
         List<String> remove = new ArrayList<String>(Arrays.asList("C", "D"));

         Collection result = CollectionUtils.removeAll(source, remove);

         assertEquals("All source elements should remain", 2, result.size());
         assertTrue(result.contains("A"));
         assertTrue(result.contains("B"));
     }

     @Test
     public void testRemoveAllIdenticalCollections() {
         // Removing the exact same multiset.
         List<String> source = new ArrayList<String>(Arrays.asList("A", "B", "A"));
         List<String> remove = new ArrayList<String>(Arrays.asList("A", "B", "A"));

         Collection result = CollectionUtils.removeAll(source, remove);

         assertTrue("Result should be empty when remove matches source exactly",
                 result.isEmpty());
     }

     @Test
     public void testRemoveAllRemoveSupersetOfElements() {
         // Remove contains all distinct elements from source, each with high cardinality.
         List<String> source = new ArrayList<String>(Arrays.asList("A", "A", "B"));
         List<String> remove = new ArrayList<String>(Arrays.asList("A", "A", "A", "B", "B"));

         Collection result = CollectionUtils.removeAll(source, remove);

         assertTrue("Result should be empty when remove covers all source multiplicities",
                 result.isEmpty());
     }

     @Test
     public void testRemoveAllEmptySource() {
         List<String> source = new ArrayList<String>();
         List<String> remove = new ArrayList<String>(Arrays.asList("A", "B"));

         Collection result = CollectionUtils.removeAll(source, remove);

         assertTrue("Removing from empty source should yield empty collection",
                 result.isEmpty());
     }

     @Test
     public void testRemoveAllEmptyRemove() {
         List<String> source = new ArrayList<String>(Arrays.asList("A", "B", "C"));
         List<String> remove = new ArrayList<String>();

         Collection result = CollectionUtils.removeAll(source, remove);

         assertEquals("Source should be unchanged when remove is empty", 3, result.size());
         assertTrue(result.contains("A"));
         assertTrue(result.contains("B"));
         assertTrue(result.contains("C"));
     }

     @Test
     public void testRemoveAllBothEmpty() {
         List<String> source = new ArrayList<String>();
         List<String> remove = new ArrayList<String>();

         Collection result = CollectionUtils.removeAll(source, remove);

         assertTrue("Removing empty from empty should yield empty", result.isEmpty());
     }

     @Test
     public void testRemoveAllWithNullElementsInSource() {
         List<String> source = new ArrayList<String>();
         source.add(null);
         source.add(null);
         source.add("A");
         List<String> remove = new ArrayList<String>();
         remove.add(null);

         Collection result = CollectionUtils.removeAll(source, remove);

         assertEquals("One null and one A should remain", 2, result.size());
         assertEquals("Cardinality of null should be 1", 1,
                 CollectionUtils.cardinality(null, result));
         assertEquals("Cardinality of A should be 1", 1,
                 CollectionUtils.cardinality("A", result));
     }

     @Test
     public void testRemoveAllSourceUnchanged() {
         // removeAll returns a new collection; the source collection should not be modified.
         List<String> source = new ArrayList<String>(Arrays.asList("A", "B", "A"));
         List<String> remove = new ArrayList<String>(Arrays.asList("A"));
         int originalSize = source.size();

         CollectionUtils.removeAll(source, remove);

         assertEquals("Source collection should remain unchanged", originalSize, source.size());
         assertEquals("Source should still have cardinality 2 for A", 2,
                 CollectionUtils.cardinality("A", source));
     }

     @Test
     public void testRemoveAllResultIsIndependent() {
         // Mutating the returned collection should not affect the original source.
         List<String> source = new ArrayList<String>(Arrays.asList("A", "B"));
         List<String> remove = new ArrayList<String>(Arrays.asList("A"));

         Collection result = CollectionUtils.removeAll(source, remove);
         result.clear();

         assertEquals("Clearing result should not affect source", 2, source.size());
     }

     @Test
     public void testRemoveAllCardinalityAcrossMultipleElements() {
         // Comprehensive test: multiple elements with varying cardinalities.
         List<String> source = new ArrayList<String>(Arrays.asList(
                 "A", "A", "A", "B", "B", "C"));
         List<String> remove = new ArrayList<String>(Arrays.asList(
                 "A", "A", "B", "C", "C"));

         Collection result = CollectionUtils.removeAll(source, remove);

         // Expected: A: 3-2=1, B: 2-1=1, C: 1-1=0, extra C in remove ignored
         assertEquals("Total size should be 2", 2, result.size());
         assertEquals("Cardinality of A", 1, CollectionUtils.cardinality("A", result));
         assertEquals("Cardinality of B", 1, CollectionUtils.cardinality("B", result));
         assertEquals("Cardinality of C", 0, CollectionUtils.cardinality("C", result));
     }
 }
