package org.apache.commons.collections4;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Comparator;
 import java.util.Iterator;
 import java.util.List;
 import java.util.NoSuchElementException;

 import static org.junit.Assert.*;
 import org.junit.Test;

 public class IteratorUtilsTest {

     @Test
     public void testCollatedIteratorMergeTwoSorted() {
         Iterator<Integer> it1 = Arrays.asList(1, 3, 5).iterator();
         Iterator<Integer> it2 = Arrays.asList(2, 4, 6).iterator();
         Iterator<Integer> collated =
                 IteratorUtils.collatedIterator(Comparator.<Integer>naturalOrder(), it1, it2);

         List<Integer> result = new ArrayList<>();
         while (collated.hasNext()) {
             result.add(collated.next());
         }
         assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), result);
     }

     @Test
     public void testCollatedIteratorOneEmptyIterator() {
         Iterator<Integer> it1 = Arrays.asList(1, 2, 3).iterator();
         Iterator<Integer> it2 = Arrays.<Integer>asList().iterator();
         Iterator<Integer> collated =
                 IteratorUtils.collatedIterator(Comparator.<Integer>naturalOrder(), it1, it2);

         List<Integer> result = new ArrayList<>();
         while (collated.hasNext()) {
             result.add(collated.next());
         }
         assertEquals(Arrays.asList(1, 2, 3), result);
     }

     @Test
     public void testCollatedIteratorBothEmpty() {
         Iterator<Integer> it1 = Arrays.<Integer>asList().iterator();
         Iterator<Integer> it2 = Arrays.<Integer>asList().iterator();
         Iterator<Integer> collated =
                 IteratorUtils.collatedIterator(Comparator.<Integer>naturalOrder(), it1, it2);

         assertFalse(collated.hasNext());
     }

     @Test(expected = NoSuchElementException.class)
     public void testCollatedIteratorNextWhenExhausted() {
         Iterator<Integer> it1 = Arrays.asList(1).iterator();
         Iterator<Integer> it2 = Arrays.<Integer>asList().iterator();
         Iterator<Integer> collated =
                 IteratorUtils.collatedIterator(Comparator.<Integer>naturalOrder(), it1, it2);

         collated.next(); // consume the single element
         collated.next(); // should throw NoSuchElementException
     }

     @Test
     public void testCollatedIteratorDuplicateValues() {
         Iterator<Integer> it1 = Arrays.asList(1, 2, 3).iterator();
         Iterator<Integer> it2 = Arrays.asList(2, 3, 4).iterator();
         Iterator<Integer> collated =
                 IteratorUtils.collatedIterator(Comparator.<Integer>naturalOrder(), it1, it2);

         List<Integer> result = new ArrayList<>();
         while (collated.hasNext()) {
             result.add(collated.next());
         }
         assertEquals(Arrays.asList(1, 2, 2, 3, 3, 4), result);
     }

     @Test
     public void testCollatedIteratorReverseComparator() {
         Iterator<Integer> it1 = Arrays.asList(5, 3, 1).iterator();
         Iterator<Integer> it2 = Arrays.asList(6, 4, 2).iterator();
         Iterator<Integer> collated =
                 IteratorUtils.collatedIterator(Comparator.<Integer>reverseOrder(), it1, it2);

         List<Integer> result = new ArrayList<>();
         while (collated.hasNext()) {
             result.add(collated.next());
         }
         assertEquals(Arrays.asList(6, 5, 4, 3, 2, 1), result);
     }

     @Test
     public void testCollatedIteratorVarargsNullComparatorMessage() {
         Iterator<Integer> it1 = Arrays.asList(1, 2).iterator();
         Iterator<Integer> it2 = Arrays.asList(3, 4).iterator();
         try {
             Iterator<Integer> collated = IteratorUtils.collatedIterator(null, it1, it2);
             collated.hasNext(); // bug: throws NPE about missing setComparator
             fail("Expected NullPointerException");
         } catch (NullPointerException e) {
             assertTrue("Expected message about setComparator",
                     e.getMessage().contains("setComparator"));
         }
     }

     @Test
     public void testCollatedIteratorTwoNullComparatorMessage() {
         Iterator<Integer> it1 = Arrays.asList(1, 2).iterator();
         Iterator<Integer> it2 = Arrays.asList(3, 4).iterator();
         try {
             Iterator<Integer> collated = IteratorUtils.collatedIterator(null, it1, it2);
             collated.hasNext();
             fail("Expected NullPointerException");
         } catch (NullPointerException e) {
             assertTrue("Expected message about setComparator",
                     e.getMessage().contains("setComparator"));
         }
     }

     @Test
     public void testCollatedIteratorCollectionNullComparatorMessage() {
         Iterator<Integer> it1 = Arrays.asList(1, 2).iterator();
         Iterator<Integer> it2 = Arrays.asList(3, 4).iterator();
         List<Iterator<Integer>> iterators = Arrays.<Iterator<Integer>>asList(it1, it2);
         try {
             Iterator<Integer> collated = IteratorUtils.collatedIterator((Comparator<Integer>) null,
iterators);
             collated.hasNext();
             fail("Expected NullPointerException");
         } catch (NullPointerException e) {
             assertTrue("Expected message about setComparator",
                     e.getMessage().contains("setComparator"));
         }
     }

     @Test
     public void testCollatedIteratorSingleElementEach() {
         Iterator<Integer> it1 = Arrays.asList(1).iterator();
         Iterator<Integer> it2 = Arrays.asList(2).iterator();
         Iterator<Integer> collated =
                 IteratorUtils.collatedIterator(Comparator.<Integer>naturalOrder(), it1, it2);

         assertTrue(collated.hasNext());
         assertEquals(Integer.valueOf(1), collated.next());
         assertTrue(collated.hasNext());
         assertEquals(Integer.valueOf(2), collated.next());
         assertFalse(collated.hasNext());
     }

     @Test
     public void testCollatedIteratorDifferentLengths() {
         Iterator<Integer> it1 = Arrays.asList(1, 3).iterator();
         Iterator<Integer> it2 = Arrays.asList(2).iterator();
         Iterator<Integer> collated =
                 IteratorUtils.collatedIterator(Comparator.<Integer>naturalOrder(), it1, it2);

         List<Integer> result = new ArrayList<>();
         while (collated.hasNext()) {
             result.add(collated.next());
         }
         assertEquals(Arrays.asList(1, 2, 3), result);
     }
 }
