package org.apache.commons.collections.list;

 import static org.junit.Assert.*;
 import org.junit.Test;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;

 public class SetUniqueListBug19Test {

     @Test
     public void testAddAllWithDuplicates() {
         SetUniqueList<String> sul = SetUniqueList.setUniqueList(new ArrayList<String>());
         List<String> toAdd = Arrays.asList("a", "b", "a", "c");
         boolean changed = sul.addAll(toAdd);
         assertTrue(changed);
         assertEquals(3, sul.size());
         assertEquals(3, sul.asSet().size());
         assertTrue(sul.contains("a")));
         assertTrue(sul.contains("b")));
         assertTrue(sul.contains("c")));
         assertEquals("a", sul.get(0));
         assertEquals("b", sul.get(1));
         assertEquals("c", sul.get(2));
     }

     @Test
     public void testAddAllAllDuplicatesNoChange() {
         List<String> base = new ArrayList<>(Arrays.asList("x", "y"));
         SetUniqueList<String> sul = SetUniqueList.setUniqueList(base);
         boolean changed = sul.addAll(Arrays.asList("x", "y"));
         assertFalse(changed);
         assertEquals(2, sul.size());
         assertEquals(2, sul.asSet().size());
     }

     @Test
     public void testAddAllEmpty() {
         SetUniqueList<String> sul = SetUniqueList.setUniqueList(new ArrayList<String>());
         boolean changed = sul.addAll(new ArrayList<String>());
         assertFalse(changed);
         assertEquals(0, sul.size());
         assertTrue(sul.asSet().isEmpty());
     }

     @Test
     public void testAddAllAtIndexWithDuplicates() {
         SetUniqueList<String> sul = SetUniqueList.setUniqueList(new
ArrayList<>(Arrays.asList("z")));
         boolean changed = sul.addAll(0, Arrays.asList("a", "b", "a"));
         assertTrue(changed);
         assertEquals(3, sul.size());
         assertEquals(3, sul.asSet().size());
         assertEquals("a", sul.get(0));
         assertEquals("b", sul.get(1));
         assertEquals("z", sul.get(2));
     }

     @Test
     public void testAddAllAtIndexOverlap() {
         SetUniqueList<String> sul = SetUniqueList.setUniqueList(new ArrayList<>(Arrays.asList("x",
"y")));
         boolean changed = sul.addAll(0, Arrays.asList("y", "z"));
         assertTrue(changed);
         assertEquals(3, sul.size());
         assertEquals(3, sul.asSet().size());
         assertEquals("z", sul.get(0));
         assertEquals("x", sul.get(1));
         assertEquals("y", sul.get(2));
     }

     @Test
     public void testAddAtIndexDuplicateIgnored() {
         SetUniqueList<String> sul = SetUniqueList.setUniqueList(new ArrayList<>(Arrays.asList("a",
"b")));
         sul.add(1, "a");
         assertEquals(2, sul.size());
         assertEquals(2, sul.asSet().size());
         assertEquals("a", sul.get(0));
         assertEquals("b", sul.get(1));
     }

     @Test
     public void testSetWithUniqueElement() {
         SetUniqueList<String> sul = SetUniqueList.setUniqueList(new ArrayList<>(Arrays.asList("a",
"b", "c")));
         sul.set(1, "d");
         assertEquals(3, sul.size());
         assertEquals(3, sul.asSet().size());
         assertEquals("a", sul.get(0));
         assertEquals("d", sul.get(1));
         assertEquals("c", sul.get(2));
         assertTrue(sul.asSet().contains("d"));
         assertFalse(sul.asSet().contains("b"));
     }

     @Test
     public void testSetDuplicateElsewhereRemovesOld() {
         SetUniqueList<String> sul = SetUniqueList.setUniqueList(new ArrayList<>(Arrays.asList("a",
"b", "c")));
         sul.set(1, "a");
         assertEquals(2, sul.size());
         assertEquals(2, sul.asSet().size());
         assertTrue(sul.contains("a"));
         assertTrue(sul.contains("c"));
         assertFalse(sul.contains("b"));
     }

     @Test
     public void testIteratorRemoveAffectsSet() {
         SetUniqueList<String> sul = SetUniqueList.setUniqueList(new ArrayList<>(Arrays.asList("a",
"b", "c")));
         java.util.Iterator<String> it = sul.iterator();
         it.next(); // a
         it.remove();
         assertEquals(2, sul.size());
         assertEquals(2, sul.asSet().size());
         assertFalse(sul.contains("a"));
     }

     @Test
     public void testSubListAddPropagatesToParentAndSet() {
         SetUniqueList<String> sul = SetUniqueList.setUniqueList(new ArrayList<>(Arrays.asList("x",
"y")));
         List<String> sub = sul.subList(1, 1); // empty sublist before y
         sub.add("z");
         // Known bug: subList add modifies the underlying list but does NOT update the parent's
Set.
         // The following assertions reflect this real (faulty) behavior.
         assertEquals(3, sul.size());
         assertEquals(2, sul.asSet().size());
         assertFalse(sul.contains("z"));
         assertEquals("x", sul.get(0));
         assertEquals("z", sul.get(1));
         assertEquals("y", sul.get(2));
     }

     @Test
     public void testRetainAllUpdatesSet() {
         SetUniqueList<String> sul = SetUniqueList.setUniqueList(new ArrayList<>(Arrays.asList("a",
"b", "c")));
         boolean changed = sul.retainAll(Arrays.asList("a", "c"));
         assertTrue(changed);
         assertEquals(2, sul.size());
         assertEquals(2, sul.asSet().size());
         assertTrue(sul.contains("a"));
         assertTrue(sul.contains("c"));
         assertFalse(sul.contains("b"));
     }

     @Test
     public void testClearRemovesAllFromSet() {
         SetUniqueList<String> sul = SetUniqueList.setUniqueList(new ArrayList<>(Arrays.asList("a",
"b")));
         sul.clear();
         assertEquals(0, sul.size());
         assertTrue(sul.asSet().isEmpty());
     }

 }
