package org.apache.commons.collections.list;

 import static org.junit.Assert.*;
 import static org.hamcrest.CoreMatchers.*;

 import java.util.Arrays;
 import java.util.ConcurrentModificationException;
 import java.util.Iterator;
 import java.util.ListIterator;
 import java.util.NoSuchElementException;

 import org.junit.Before;
 import org.junit.Test;

 /**
  * Tests for {@link TreeList} concentrating on the COLLECTIONS-447 bug
  * (rotation-offset miscalculation causing wrong get results) and
  * general list contract compliance.
  */
 public class TreeListTest {

     private TreeList<String> list;

     @Before
     public void setUp() {
         list = new TreeList<String>();
     }

     // --- COLLECTIONS-447 reproduction attempts ---

     @Test
     public void testFirstElementAfterMixedInserts() {
         // After multiple inserts that cause AVL rotations the root
         // offset may become stale; get(0) must still return the first
         // element according to insertion order.
         list.add(0, "A");
         list.add(1, "B");
         list.add(2, "C");
         list.add(1, "X");   // shifts B and C right
         list.add(0, "Z");   // shifts all right
         // Expected order: Z, A, X, B, C
         assertEquals("first element", "Z", list.get(0));
         assertEquals("last element", "C", list.get(4));
     }

     @Test
     public void testAddAtZeroRepeatedlyThenGet() {
         // Repeated inserts at index 0 exercise balance and rotations heavily.
         String[] letters = {"D", "C", "B", "A"};
         for (String s : letters) {
             list.add(0, s);
         }
         // After inserting at 0, the last one becomes the first element.
         assertEquals("A", list.get(0));
         assertEquals("D", list.get(3));
     }

     @Test
     public void testRotationsDoNotCorruptOffsets() {
         // Insert a large number of elements at the beginning and middle
         // to force many AVL rotations; verify correctness via iterator.
         int count = 32;
         for (int i = 0; i < count; i++) {
             list.add(i / 2, String.valueOf(i));
         }
         Iterator<String> it = list.iterator();
         int idx = 0;
         while (it.hasNext()) {
             assertEquals("element " + idx, String.valueOf(idx), it.next());
             idx++;
         }
     }

     @Test
     public void testGetAfterInterleavedInserts() {
         // Inserts that cause left and right rotations in quick succession.
         // The previous bug gave wrong results for get(0) with a sequence
         // similar to the one that triggered COLLECTIONS-447.
         list.add(0, "C");
         list.add(0, "B");
         list.add(0, "A");
         assertEquals("A", list.get(0));
         assertEquals("B", list.get(1));
         assertEquals("C", list.get(2));
     }

     // --- Standard list contract ---

     @Test
     public void testAddAtIndexEndAndMiddle() {
         list.add(0, "first");
         list.add(1, "second");
         list.add(1, "middle");
         assertEquals(3, list.size());
         assertEquals("first", list.get(0));
         assertEquals("middle", list.get(1));
         assertEquals("second", list.get(2));
     }

     @Test
     public void testAddThenRemoveThenReadd() {
         list.add(0, "X");
         list.add(1, "Y");
         list.add(2, "Z");
         list.remove(1);               // removes "Y"
         assertEquals(2, list.size());
         list.add(1, "Y2");
         assertEquals(Arrays.asList("X", "Y2", "Z"),
                      Arrays.asList(list.toArray()));
     }

     @Test
     public void testSetDoesNotChangeOrder() {
         list.add(0, "a");
         list.add(1, "b");
         assertEquals("a", list.set(0, "A"));
         assertEquals("A", list.get(0));
     }

     @Test
     public void testIndexOfAndContains() {
         list.add(0, "alpha");
         list.add(1, "beta");
         assertEquals(0, list.indexOf("alpha"));
         assertEquals(1, list.indexOf("beta"));
         assertEquals(-1, list.indexOf("gamma"));
         assertTrue(list.contains("beta"));
         assertFalse(list.contains("omega"));
     }

     @Test
     public void testIteratorNextAndPrevious() {
         list.add(0, "one");
         list.add(1, "two");
         ListIterator<String> lit = list.listIterator();
         assertTrue(lit.hasNext());
         assertEquals("one", lit.next());
         assertEquals("two", lit.next());
         assertTrue(lit.hasPrevious());
         assertEquals("two", lit.previous());
         assertEquals(0, lit.nextIndex());
     }

     @Test
     public void testIteratorAdd() {
         list.add(0, "1");
         list.add(1, "3");
         ListIterator<String> lit = list.listIterator(1);
         lit.add("2");
         assertEquals(Arrays.asList("1", "2", "3"),
                      Arrays.asList(list.toArray()));
     }

     @Test(expected = IndexOutOfBoundsException.class)
     public void testGetOutOfBounds() {
         list.add(0, "only");
         list.get(1);
     }

     @Test(expected = ConcurrentModificationException.class)
     public void testConcurrentModificationDetected() {
         list.add(0, "a");
         Iterator<String> it = list.iterator();
         list.add(1, "b");          // modify
         it.next();                 // should throw
     }

     @Test(expected = NoSuchElementException.class)
     public void testIteratorExhaustedThrows() {
         list.add(0, "sole");
         Iterator<String> it = list.iterator();
         it.next();
         it.next();
     }
 }