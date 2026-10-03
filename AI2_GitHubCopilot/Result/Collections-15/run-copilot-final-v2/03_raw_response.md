package org.apache.commons.collections.list;

 import static org.junit.Assert.*;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.List;

 import org.junit.Before;
 import org.junit.Test;

 /**
  * Tests for {@link SetUniqueList} focusing on the COLLECTIONS-304 bug
  * where addAll(int, Collection) incorrectly counts duplicates within
  * the provided collection when inserting at an index.
  */
 public class TestSetUniqueList {

     private SetUniqueList list;

     @Before
     public void setUp() {
         list = SetUniqueList.decorate(new ArrayList());
     }

     // ---- addAll(int,Collection) tests (COLLECTIONS-304) ----

     @Test
     public void testAddAllAtIndexWithDuplicatesOfExisting() {
         list.add("A");
         list.add("B");
         list.add("C");
         // size = 3

         Collection<String> toAdd = Arrays.asList("A", "D", "B", "E");
         boolean changed = list.addAll(1, toAdd);

         assertTrue("list should have changed", changed);
         assertEquals("size should reflect unique elements only", 5, list.size());
         // A, D, E, B, C  (or A, D, B, E, C — but size must be 5)
         assertEquals("asSet size must match list size", list.size(), list.asSet().size());
     }

     @Test
     public void testAddAllAtIndexWithDuplicatesInCollectionItself() {
         list.add("X");
         list.add("Y");
         // size = 2

         // collection has internal duplicate "Z" plus "X" already present
         Collection<String> toAdd = Arrays.asList("Z", "X", "Z", "W");
         boolean changed = list.addAll(0, toAdd);

         assertTrue("list should have changed", changed);
         assertEquals("size should be 4 (X, Y, Z, W)", 4, list.size());
         assertEquals("asSet size must match list size", list.size(), list.asSet().size());
     }

     @Test
     public void testAddAllAtIndexAllElementsAlreadyPresent() {
         list.add("P");
         list.add("Q");
         list.add("R");
         // size = 3

         Collection<String> toAdd = Arrays.asList("P", "Q", "R");
         boolean changed = list.addAll(1, toAdd);

         assertFalse("list should not change when all elements already present", changed);
         assertEquals("size should remain 3", 3, list.size());
         assertEquals("asSet size must match list size", list.size(), list.asSet().size());
     }

     @Test
     public void testAddAllAtEndWithDuplicates() {
         list.add("1");
         list.add("2");
         list.add("3");

         Collection<String> toAdd = Arrays.asList("4", "1", "5", "2");
         boolean changed = list.addAll(toAdd); // addAll(Collection) delegates to addAll(size(),
coll)

         assertTrue("list should have changed", changed);
         assertEquals("size should be 5", 5, list.size());
         assertEquals("asSet size must match list size", list.size(), list.asSet().size());
     }

     @Test
     public void testAddAllAtIndexZeroWithNewAndDuplicates() {
         list.add("alpha");
         list.add("beta");

         Collection<String> toAdd = Arrays.asList("gamma", "alpha", "delta", "beta", "epsilon");
         boolean changed = list.addAll(0, toAdd);

         assertTrue("list should have changed", changed);
         assertEquals("size should be 5", 5, list.size());
         assertEquals("asSet size must match list size", list.size(), list.asSet().size());
         // verify distinct elements are present
         assertTrue(list.contains("gamma"));
         assertTrue(list.contains("delta"));
         assertTrue(list.contains("epsilon"));
     }

     // ---- add(Object) duplicate ----

     @Test
     public void testAddDuplicateReturnsFalseAndSizeUnchanged() {
         list.add("foo");
         list.add("bar");
         assertEquals(2, list.size());

         boolean result = list.add("foo"); // duplicate

         assertFalse("add should return false for duplicate", result);
         assertEquals("size should remain 2", 2, list.size());
     }

     // ---- add(int, Object) duplicate ----

     @Test
     public void testAddAtIndexDuplicateDoesNotIncreaseSize() {
         list.add("X");
         list.add("Y");
         assertEquals(2, list.size());

         list.add(0, "X"); // duplicate at index

         assertEquals("size should remain 2 after duplicate add at index", 2, list.size());
         assertEquals("Y should still be at index 1", "Y", list.get(1));
     }

     // ---- set(int, Object) ----

     @Test
     public void testSetReplacesAndRemovesOldDuplicate() {
         list.add("A");
         list.add("B");
         list.add("C");
         assertEquals(3, list.size());

         // "B" exists at index 1; set index 0 to "B" — old "A" removed, "B" moves to index 0
         Object previous = list.set(0, "B");

         assertEquals("previous object should be A", "A", previous);
         assertEquals("size should be 2 (A removed)", 2, list.size());
         assertFalse("A should no longer be in list", list.contains("A"));
         assertEquals("B should be at index 0", "B", list.get(0));
         assertEquals("C should be at index 1", "C", list.get(1));
         assertEquals("asSet size must match list size", list.size(), list.asSet().size());
     }

     @Test
     public void testSetWithNewElement() {
         list.add("A");
         list.add("B");

         Object previous = list.set(1, "C");

         assertEquals("previous object should be B", "B", previous);
         assertEquals("size should remain 2", 2, list.size());
         assertFalse("B should no longer be in list", list.contains("B"));
         assertTrue("C should be in list", list.contains("C"));
         assertEquals("C should be at index 1", "C", list.get(1));
     }

     @Test
     public void testSetElementAtSameIndexAlreadyExists() {
         list.add("A");
         list.add("B");
         list.add("A"); // duplicate — not added
         list.add("C");
         // list: A, B, C (size 3)

         // set index 0 to "A" — it's the same element at the same position
         Object previous = list.set(0, "A");

         assertEquals("previous object should be A", "A", previous);
         assertEquals("size should remain 3", 3, list.size());
         assertTrue("A should still be at index 0", "A".equals(list.get(0)));
     }

     // ---- Boundary / corner cases ----

     @Test
     public void testAddAllAtIndexWithEmptyCollection() {
         list.add("apple");
         list.add("banana");

         boolean changed = list.addAll(1, Collections.emptyList());

         assertFalse("list should not change with empty collection", changed);
         assertEquals("size should remain 2", 2, list.size());
     }

     @Test
     public void testAddAllWithAllNewElementsAtStart() {
         list.add("old1");
         list.add("old2");

         Collection<String> toAdd = Arrays.asList("new1", "new2", "new3");
         boolean changed = list.addAll(0, toAdd);

         assertTrue("list should have changed", changed);
         assertEquals("size should be 5", 5, list.size());
         assertEquals("first element should be new1", "new1", list.get(0));
         assertEquals("asSet size must match list size", list.size(), list.asSet().size());
     }

 }