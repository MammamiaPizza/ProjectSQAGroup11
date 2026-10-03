import static org.junit.Assert.*;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;

 import org.apache.commons.collections.set.ListOrderedSet;
 import org.junit.Test;

 /**
  * JUnit tests for ListOrderedSet.retainAll() focusing on COLLECTIONS-426.
  * Verifies that the wrapped Set and setOrder List remain synchronized.
  */
 public class ListOrderedSetRetainAllTest {

     /**
      * retainAll with a mixed subset: survivors kept in original order,
      * all others removed from both the set and the order list.
      */
     @Test
     public void testRetainAllWithMixedSubset() {
         ListOrderedSet<String> los = new ListOrderedSet<String>();
         los.add("A");
         los.add("B");
         los.add("C");
         los.add("D");

         Collection<String> retain = Arrays.asList("B", "D");
         boolean changed = los.retainAll(retain);

         assertTrue("retainAll should report a change", changed);
         assertEquals("set size mismatch", 2, los.size());
         List<String> order = los.asList();
         assertEquals("order list size should match set", 2, order.size());
         assertEquals("first survivor should be B", "B", order.get(0));
         assertEquals("second survivor should be D", "D", order.get(1));
         assertFalse("A should be removed", los.contains("A"));
         assertFalse("C should be removed", los.contains("C"));
     }

     /**
      * Empty argument collection clears both the set and the order list.
      */
     @Test
     public void testRetainAllWithEmptyCollection() {
         ListOrderedSet<String> los = new ListOrderedSet<String>();
         los.add("X");
         los.add("Y");

         los.retainAll(new ArrayList<String>());

         assertTrue("set should be empty", los.isEmpty());
         assertEquals("order list should be empty", 0, los.asList().size());
     }

     /**
      * retainAll with the same elements leaves everything untouched.
      */
     @Test
     public void testRetainAllWithEqualCollection() {
         ListOrderedSet<String> los = new ListOrderedSet<String>();
         los.add("A");
         los.add("B");
         los.add("C");

         Collection<String> same = Arrays.asList("A", "B", "C");
         boolean changed = los.retainAll(same);

         assertFalse("retainAll should report no change", changed);
         assertEquals(3, los.size());
         assertEquals("A", los.get(0));
         assertEquals("B", los.get(1));
         assertEquals("C", los.get(2));
     }

     /**
      * A superset argument retains all current elements and does not alter order.
      */
     @Test
     public void testRetainAllWithSuperset() {
         ListOrderedSet<String> los = new ListOrderedSet<String>();
         los.add("A");
         los.add("B");

         Collection<String> superset = Arrays.asList("A", "B", "C");
         boolean changed = los.retainAll(superset);

         assertFalse("retainAll should report no change", changed);
         assertEquals(2, los.size());
         assertEquals("A", los.get(0));
         assertEquals("B", los.get(1));
     }

     /**
      * Duplicate entries in the argument collection must not create duplicate
      * elements in setOrder.
      */
     @Test
     public void testRetainAllWithDuplicatesInArg() {
         ListOrderedSet<String> los = new ListOrderedSet<String>();
         los.add("A");
         los.add("B");
         los.add("C");

         Collection<String> withDup = Arrays.asList("A", "A", "B");
         los.retainAll(withDup);

         assertEquals("set size", 2, los.size());
         List<String> order = los.asList();
         assertEquals("order list size must equal set size", 2, order.size());
         assertEquals("first element", "A", order.get(0));
         assertEquals("second element", "B", order.get(1));
     }

     /**
      * retainAll returns true when elements are removed, false otherwise.
      */
     @Test
     public void testRetainAllReturnValue() {
         ListOrderedSet<String> los = new ListOrderedSet<String>();
         los.add("A");
         los.add("B");

         boolean changed = los.retainAll(Arrays.asList("B"));  // A removed
         assertTrue("should report change", changed);

         changed = los.retainAll(Arrays.asList("B"));          // unchanged
         assertFalse("should report no change", changed);
     }

     /**
      * retainAll on an already empty set should do nothing.
      */
     @Test
     public void testRetainAllOnEmptySet() {
         ListOrderedSet<String> los = new ListOrderedSet<String>();
         boolean changed = los.retainAll(Arrays.asList("A"));
         assertFalse("no change on empty set", changed);
         assertTrue(los.isEmpty());
     }

     /**
      * After retainAll the asList() and iterator() must reflect survivors
      * in original insertion order.
      */
     @Test
     public void testRetainAllMaintainsOrder() {
         ListOrderedSet<String> los = new ListOrderedSet<String>();
         los.add("first");
         los.add("second");
         los.add("third");
         los.add("fourth");

         los.retainAll(Arrays.asList("fourth", "second"));

         List<String> list = los.asList();
         assertEquals(2, list.size());
         assertEquals("second", list.get(0));
         assertEquals("fourth", list.get(1));

         // iterator follows same order
         java.util.Iterator<String> it = los.iterator();
         assertTrue(it.hasNext());
         assertEquals("second", it.next());
         assertTrue(it.hasNext());
         assertEquals("fourth", it.next());
         assertFalse(it.hasNext());
     }

     /**
      * get(int) after retainAll must return elements at the correct positions
      * in the surviving order.
      */
     @Test
     public void testGetAfterRetainAll() {
         ListOrderedSet<String> los = new ListOrderedSet<String>();
         los.add("A");
         los.add("B");
         los.add("C");
         los.add("D");

         los.retainAll(Arrays.asList("A", "C"));

         assertEquals("first survivor", "A", los.get(0));
         assertEquals("second survivor", "C", los.get(1));
         try {
             los.get(2);
             fail("IndexOutOfBoundsException expected");
         } catch (IndexOutOfBoundsException e) {
             // expected
         }
     }

     /**
      * remove(int) after retainAll removes the element and keeps set / order
      * consistent.
      */
     @Test
     public void testRemoveByIndexAfterRetainAll() {
         ListOrderedSet<String> los = new ListOrderedSet<String>();
         los.add("X");
         los.add("Y");
         los.add("Z");

         los.retainAll(Arrays.asList("X", "Z")); // survivors: X, Z

         Object removed = los.remove(0);         // remove X
         assertEquals("removed element", "X", removed);
         assertEquals(1, los.size());
         assertFalse(los.contains("X"));
         assertTrue(los.contains("Z"));
         assertEquals("remaining element", "Z", los.get(0));
     }

     /**
      * indexOf(Object) after retainAll returns the correct index of survivors.
      */
     @Test
     public void testIndexOfAfterRetainAll() {
         ListOrderedSet<String> los = new ListOrderedSet<String>();
         los.add("A");
         los.add("B");
         los.add("C");
         los.add("D");

         los.retainAll(Arrays.asList("B", "D"));

         assertEquals("index of B", 0, los.indexOf("B"));
         assertEquals("index of D", 1, los.indexOf("D"));
         assertEquals("missing element", -1, los.indexOf("A"));
     }

     /**
      * retainAll with null argument should throw NullPointerException (consistent
      * with the underlying Set contract).
      */
     @Test(expected = NullPointerException.class)
     public void testRetainAllNullArgument() {
         ListOrderedSet<String> los = new ListOrderedSet<String>();
         los.add("a");
         los.retainAll(null);
     }
 }