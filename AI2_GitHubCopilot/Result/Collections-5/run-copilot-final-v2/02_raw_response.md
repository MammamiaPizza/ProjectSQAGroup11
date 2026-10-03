package org.apache.commons.collections.list;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;

 import junit.framework.TestCase;

 import org.apache.commons.collections.set.UnmodifiableSet;

 public class TestSetUniqueListBug5 extends TestCase {

     private SetUniqueList list;

     @Override
     protected void setUp() throws Exception {
         super.setUp();
         list = SetUniqueList.decorate(new ArrayList());
     }

     // --- addAll(int, Collection) index/order regression tests ---

     /**
      * COLLECTIONS-249: addAll(0, coll) on empty list must place the first new element at index 0.
      */
     public void testAddAllAtIndexZeroOnEmptyList() {
         List coll = Arrays.asList("a", "b", "c");
         list.addAll(0, coll);
         assertEquals("Size must be 3", 3, list.size());
         assertEquals("First element must be at index 0", "a", list.get(0));
         assertEquals("Second element must be at index 1", "b", list.get(1));
         assertEquals("Third element must be at index 2", "c", list.get(2));
     }

     /**
      * COLLECTIONS-249: addAll(0, coll) on a non-empty list must insert new unique
      * elements at the front, preserving their relative order and skipping duplicates.
      */
     public void testAddAllAtIndexZeroWithExistingElements() {
         list.add("x");
         list.add("y");
         List coll = Arrays.asList("a", "b", "a", "x"); // internal dup + existing dup
         list.addAll(0, coll);

         assertEquals("Size must be 4 (x already present, a deduped)", 4, list.size());
         assertEquals(0, list.get(0));
         assertEquals(1, list.get(1));
         assertEquals(2, list.get(2));
         assertEquals(3, list.get(3));
         assertEquals("First new element must be at index 0", "a", list.get(0));
         assertEquals("Second new element must follow", "b", list.get(1));
         assertEquals("Existing elements must shift right", "x", list.get(2));
         assertEquals("y", list.get(3));
     }

     /**
      * addAll(index, coll) with index == size() must behave like addAll(coll),
      * appending unique elements at the end.
      */
     public void testAddAllAtSizeAppendsAtEnd() {
         list.add("x");
         int idx = list.size(); // idx = 1
         List coll = Arrays.asList("d", "e");
         list.addAll(idx, coll);

         assertEquals(3, list.size());
         assertEquals("x", list.get(0));
         assertEquals("d", list.get(1));
         assertEquals("e", list.get(2));
     }

     /**
      * addAll(1, coll) on a list of size 2 must insert new unique elements starting
      * at position 1.
      */
     public void testAddAllAtMiddleIndex() {
         list.add("first");
         list.add("last");
         List coll = Arrays.asList("mid1", "mid2");
         list.addAll(1, coll);

         assertEquals(4, list.size());
         assertEquals("first", list.get(0));
         assertEquals("mid1", list.get(1));
         assertEquals("mid2", list.get(2));
         assertEquals("last", list.get(3));
     }

     /**
      * Internal duplicates within the supplied collection must be ignored; only the
      * first occurrence of each unique element is added, preserving relative order.
      */
     public void testAddAllSkipsInternalDuplicates() {
         List coll = Arrays.asList("a", "b", "a", "c", "b");
         list.addAll(0, coll);

         assertEquals(3, list.size());
         assertEquals("a", list.get(0));
         assertEquals("b", list.get(1));
         assertEquals("c", list.get(2));
     }

     /**
      * Elements already present in the list must be skipped, and must not advance
      * the insertion point for subsequent unique elements.
      */
     public void testAddAllSkipsExistingDuplicatesWithoutIndexShift() {
         list.add("existing");
         List coll = Arrays.asList("existing", "new1", "new2");
         list.addAll(0, coll);

         assertEquals(3, list.size());
         // new1, new2 inserted at front, existing at end; "existing" was skipped
         assertEquals("new1", list.get(0));
         assertEquals("new2", list.get(1));
         assertEquals("existing", list.get(2));
     }

     /**
      * addAll with an empty collection must not change the list and return false.
      */
     public void testAddAllEmptyCollection() {
         list.add("a");
         boolean changed = list.addAll(0, Collections.EMPTY_LIST);
         assertFalse("Must return false when nothing is added", changed);
         assertEquals(1, list.size());
         assertEquals("a", list.get(0));
     }

     /**
      * addAll with a collection where every element is a duplicate must return false
      * and preserve the list exactly as before.
      */
     public void testAddAllAllDuplicatesReturnsFalse() {
         list.add("a");
         list.add("b");
         List coll = Arrays.asList("a", "b");
         boolean changed = list.addAll(0, coll);
         assertFalse(changed);
         assertEquals(2, list.size());
         assertEquals("a", list.get(0));
         assertEquals("b", list.get(1));
     }

     /**
      * addAll must throw IndexOutOfBoundsException for invalid indices.
      */
     public void testAddAllInvalidIndexThrows() {
         list.add("x");
         try {
             list.addAll(-1, Collections.singletonList("bad"));
             fail("Expected IndexOutOfBoundsException for negative index");
         } catch (IndexOutOfBoundsException expected) {
             // expected
         }
         try {
             list.addAll(list.size() + 1, Collections.singletonList("bad"));
             fail("Expected IndexOutOfBoundsException for index > size()");
         } catch (IndexOutOfBoundsException expected) {
             // expected
         }
     }

     /**
      * Passing a null collection must throw NullPointerException.
      */
     public void testAddAllNullCollectionThrows() {
         try {
             list.addAll(0, null);
             fail("Expected NullPointerException");
         } catch (NullPointerException expected) {
             // expected
         }
     }

     /**
      * After bulk insertion the internal set must reflect exactly the unique elements.
      */
     public void testAddAllSetConsistency() {
         List coll = Arrays.asList("a", "b", "a", "c");
         list.addAll(0, coll);

         Set set = list.asSet();
         assertEquals(3, set.size());
         assertTrue(set.contains("a"));
         assertTrue(set.contains("b"));
         assertTrue(set.contains("c"));
     }

     /**
      * A more complex scenario: mixing pre-existing elements, internal duplicates,
      * and existing duplicates, then verifying final order, size, and set contents.
      */
     public void testAddAllComplexOrderAndDuplicates() {
         list.add("p"); // index 0
         list.add("q"); // index 1
         // coll: new unique at front, dups, existing dups
         List coll = Arrays.asList("x", "p", "y", "x", "z", "q");
         list.addAll(1, coll); // insert at index 1

         // Expected order: p, x, y, z, q
         assertEquals(5, list.size());
         assertEquals("p", list.get(0));
         assertEquals("x", list.get(1));
         assertEquals("y", list.get(2));
         assertEquals("z", list.get(3));
         assertEquals("q", list.get(4));

         Set set = list.asSet();
         assertEquals(5, set.size());
         assertTrue(set.contains("p"));
         assertTrue(set.contains("q"));
         assertTrue(set.contains("x"));
         assertTrue(set.contains("y"));
         assertTrue(set.contains("z"));
     }
 }