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
         list.add(0, "A");
         list.add(1, "B");
         list.add(2, "C");
         list.add(1, "X");
         list.add(0, "Z");
         assertEquals("first element", "Z", list.get(0));
         assertEquals("last element", "C", list.get(4));
     }

     @Test
     public void testAddAtZeroRepeatedlyThenGet() {
         String[] letters = {"D", "C", "B", "A"};
         for (String s : letters) {
             list.add(0, s);
         }
         assertEquals("A", list.get(0));
         assertEquals("D", list.get(3));
     }

     @Test
     public void testRotationsDoNotCorruptOffsets() {
         int count = 32;
         for (int i = 0; i < count; i++) {
             list.add(i / 2, String.valueOf(i));
         }
         for (int i = 0; i < count; i++) {
             assertEquals("element " + i, String.valueOf(i), list.get(i));
         }
     }

     @Test
     public void testGetAfterInterleavedInserts() {
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
         list.remove(1);
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
         assertFalse(lit.hasNext());
         assertTrue(lit.hasPrevious());
         assertEquals("two", lit.previous());
         assertEquals("one", lit.previous());
         assertFalse(lit.hasPrevious());
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
         list.add(1, "b");
         it.next();
     }

     @Test(expected = NoSuchElementException.class)
     public void testIteratorExhaustedThrows() {
         list.add(0, "sole");
         Iterator<String> it = list.iterator();
         it.next();
         it.next();
     }
 }