package org.apache.commons.collections.list;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.List;
 import java.util.ListIterator;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link SetUniqueList} targeting bug COLLECTIONS-307.
  */
 public class TestSetUniqueList {

     // Helper to create a SetUniqueList from varargs
     private SetUniqueList createList(Object... elements) {
         List<Object> base = new ArrayList<Object>(Arrays.asList(elements));
         return SetUniqueList.decorate(base);
     }

     @Test
     public void testSetNormal() {
         SetUniqueList list = createList("A", "B", "C");
         Object old = list.set(1, "Z");
         assertEquals("B", old);
         assertEquals(3, list.size());
         assertEquals(3, list.asSet().size());
         assertFalse(list.contains("B"));
         assertTrue(list.contains("Z"));
     }

     @Test
     public void testSetDuplicateReplaceSameIndex() {
         // Setting the same value at its current index should not break set size
         SetUniqueList list = createList("A", "B", "C");
         Object old = list.set(1, "B");
         assertEquals("B", old);
         assertEquals(3, list.size());
         assertEquals("Set size must equal list size after replacing element with itself",
                 3, list.asSet().size());
         assertTrue(list.contains("B"));
     }

     @Test
     public void testSetDuplicateDifferentIndex() {
         // Setting a value that already exists at another index removes the old duplicate
         SetUniqueList list = createList("A", "B", "C", "D");
         Object old = list.set(3, "B"); // insert "B" at last position
         assertEquals("D", old);
         assertEquals(3, list.size());
         assertEquals(3, list.asSet().size());
         // Original "B" at index 1 removed, only one "B" remains
         assertEquals("A", list.get(0));
         assertEquals("C", list.get(1));
         assertEquals("B", list.get(2));
         assertFalse("Duplicate B should not exist", list.indexOf("B") == -1);
         assertEquals(2, list.indexOf("B"));
     }

     @Test
     public void testSetOppositeEnds() {
         SetUniqueList list = createList("X", "Y", "Z");
         list.set(0, "U");
         assertEquals("U", list.get(0));
         assertEquals(3, list.size());
         assertEquals(3, list.asSet().size());

         list.set(list.size() - 1, "V");
         assertEquals("V", list.get(list.size() - 1));
         assertEquals(3, list.size());
         assertEquals(3, list.asSet().size());
     }

     @Test
     public void testAddAllWithDuplicates() {
         SetUniqueList list = createList("A", "B");
         List<Object> toAdd = Arrays.asList("B", "C", "C", "D");
         boolean changed = list.addAll(toAdd);
         assertTrue(changed);
         assertEquals(4, list.size()); // A, B, C, D
         assertEquals(4, list.asSet().size());
         assertTrue(list.containsAll(Arrays.asList("A", "B", "C", "D")));
     }

     @Test
     public void testAddAllAtIndexWithDuplicates() {
         SetUniqueList list = createList("A", "C");
         List<Object> toAdd = Arrays.asList("B", "C", "D", "A");
         boolean changed = list.addAll(1, toAdd);
         assertTrue(changed);
         assertEquals(4, list.size());
         assertEquals(4, list.asSet().size());
         assertEquals("A", list.get(0));
         assertEquals("B", list.get(1));
         assertEquals("C", list.get(2)); // original C shifted
         assertEquals("D", list.get(3));
     }

     @Test
     public void testSubListSetDuplicateAcrossBoundary() {
         // COLLECTIONS-307: subList.set with a value already present outside the view
         // should preserve uniqueness (no duplicates)
         SetUniqueList list = createList("A", "B", "C", "D", "E");
         List<Object> sub = list.subList(1, 4); // indices 1-3: B, C, D
         assertEquals(3, sub.size());

         // Set a value already present at index 0 (outside sublist view)
         sub.set(0, "A"); // replace B with A
         assertEquals("A", list.get(1));

         // The list must not contain duplicates
         assertEquals("Only one A expected", 1, countOccurrences(list, "A"));
         assertEquals("List and set sizes must match", list.size(), list.asSet().size());
     }

     @Test
     public void testSubListAddDuplicate() {
         SetUniqueList list = createList("A", "B", "C");
         List<Object> sub = list.subList(1, 2); // just [B]
         try {
             sub.add("A"); // duplicate of element at index 0
         } catch (Exception e) {
             // may throw or silently ignore
         }
         assertEquals("No duplicates after subList.add", 1, countOccurrences(list, "A"));
         assertEquals(list.size(), list.asSet().size());
     }

     @Test
     public void testIteratorAddDuplicate() {
         SetUniqueList list = createList("A", "B");
         ListIterator<Object> it = list.listIterator();
         it.next(); // A
         it.add("B"); // should be rejected as duplicate
         // list should still be [A, B]
         assertEquals(2, list.size());
         assertEquals(2, list.asSet().size());
         assertEquals("A", list.get(0));
         assertEquals("B", list.get(1));
     }

     @Test
     public void testIteratorRemove() {
         SetUniqueList list = createList("A", "B", "C");
         ListIterator<Object> it = list.listIterator();
         it.next(); // A
         it.next(); // B
         it.remove(); // removes B
         assertEquals(2, list.size());
         assertEquals(2, list.asSet().size());
         assertFalse(list.contains("B"));
     }

     @Test
     public void testRemoveByIndex() {
         SetUniqueList list = createList("X", "Y", "Z");
         Object removed = list.remove(1);
         assertEquals("Y", removed);
         assertEquals(2, list.size());
         assertEquals(2, list.asSet().size());
         assertFalse(list.contains("Y"));
     }

     @Test
     public void testRemoveByObject() {
         SetUniqueList list = createList("X", "Y", "Z");
         assertTrue(list.remove("Y"));
         assertEquals(2, list.size());
         assertEquals(2, list.asSet().size());
         assertFalse(list.contains("Y"));
     }

     // Utility to count occurrences of an object in the decorated list
     private int countOccurrences(SetUniqueList list, Object obj) {
         int count = 0;
         for (Object o : list) {
             if (obj == null ? o == null : obj.equals(o)) {
                 count++;
             }
         }
         return count;
     }
 }
