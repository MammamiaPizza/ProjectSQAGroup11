package org.apache.commons.math.linear;

 import static org.junit.Assert.*;
 import org.junit.Test;
 import java.util.Iterator;
 import org.apache.commons.math.linear.OpenMapRealVector;
 import org.apache.commons.math.linear.RealVector.Entry;

 /**
  * Tests targeting the sparseIterator() concurrent modification bug (MATH-645).
  * Verifies that setValue() on entries during iteration does not throw
  * MathRuntimeException and that the vector state is correctly updated.
  */
 public class OpenMapRealVectorSparseIteratorTest {

     /**
      * For a vector with all zero entries, the sparse iterator hasNext()
      * must return false immediately.
      */
     @Test
     public void testZeroVectorHasNextFalse() {
         OpenMapRealVector v = new OpenMapRealVector(5);
         Iterator<Entry> it = v.sparseIterator();
         assertFalse("hasNext should be false for zero vector", it.hasNext());
     }

     /**
      * Single non-zero entry: iterate, setValue, then hasNext() must be false
      * and getEntry() must return the new value.
      */
     @Test
     public void testSingleEntrySetValueAndIterate() {
         OpenMapRealVector v = new OpenMapRealVector(new double[]{0, 3.5, 0, 0, 0});
         Iterator<Entry> it = v.sparseIterator();
         assertTrue("should have at least one entry", it.hasNext());
         Entry e = it.next();
         assertEquals("index", 1, e.getIndex());
         assertEquals("initial value", 3.5, e.getValue(), 0.0);
         e.setValue(7.2);
         // After the single entry is processed the iterator must be exhausted.
         assertFalse("no more entries expected", it.hasNext());
         // The vector must reflect the updated value.
         assertEquals("updated entry value", 7.2, v.getEntry(1), 0.0);
         assertEquals("unchanged entry", 0.0, v.getEntry(0), 0.0);
     }

     /**
      * Setting values on multiple entries during a single iteration must not
      * throw a concurrent modification exception and all entries must be
      * visited exactly once.
      */
     @Test
     public void testMultipleEntriesSetValueNoException() {
         OpenMapRealVector v = new OpenMapRealVector(new double[]{1.0, 0, 2.0, 3.0, 0});
         Iterator<Entry> it = v.sparseIterator();
         int count = 0;
         while (it.hasNext()) {
             Entry e = it.next();
             if (e.getIndex() == 0) {
                 e.setValue(10.0);
             } else if (e.getIndex() == 2) {
                 e.setValue(20.0);
             }
             count++;
         }
         assertEquals("should iterate over three non-zero entries", 3, count);
         assertEquals("vector entry 0", 10.0, v.getEntry(0), 0.0);
         assertEquals("vector entry 2", 20.0, v.getEntry(2), 0.0);
         assertEquals("vector entry 3 unchanged", 3.0, v.getEntry(3), 0.0);
     }

     /**
      * setValue() must update the underlying vector; getEntry() must
      * immediately return the new value.
      */
     @Test
     public void testSetValueUpdatesVectorEntry() {
         OpenMapRealVector v = new OpenMapRealVector(new double[]{4.0, 0, 0, 8.0});
         Iterator<Entry> it = v.sparseIterator();
         Entry e = it.next();
         assertEquals(0, e.getIndex());
         assertEquals(4.0, e.getValue(), 0.0);
         e.setValue(5.0);
         assertEquals("getEntry after setValue", 5.0, v.getEntry(0), 0.0);
     }

     /**
      * An empty (dimension 0) vector must yield an iterator with no elements.
      */
     @Test
     public void testEmptyVectorIterator() {
         OpenMapRealVector v = new OpenMapRealVector(); // dimension 0
         Iterator<Entry> it = v.sparseIterator();
         assertFalse("hasNext must be false for empty vector", it.hasNext());
     }

     /**
      * Normal iteration without any modifications visits every non-zero entry
      * in the correct index order.
      */
     @Test
     public void testNormalIterationOrderAndValues() {
         double[] values = {0, 1, 0, 2, 3};
         OpenMapRealVector v = new OpenMapRealVector(values);
         Iterator<Entry> it = v.sparseIterator();
         int[] expectedIdx = {1, 3, 4};
         double[] expectedVal = {1, 2, 3};
         int i = 0;
         while (it.hasNext()) {
             Entry e = it.next();
             assertEquals("index", expectedIdx[i], e.getIndex());
             assertEquals("value", expectedVal[i], e.getValue(), 0.0);
             i++;
         }
         assertEquals("visited all non-zero entries", 3, i);
     }

     /**
      * The remove() method of the sparse iterator must throw
      * UnsupportedOperationException.
      */
     @Test(expected = UnsupportedOperationException.class)
     public void testRemoveThrowsException() {
         OpenMapRealVector v = new OpenMapRealVector(new double[]{0, 5});
         Iterator<Entry> it = v.sparseIterator();
         it.next();
         it.remove();
     }

     /**
      * Calling setValue() on every entry during iteration must succeed without
      * exception and all values must be updated in the vector.
      */
     @Test
     public void testSetValueAllEntriesDuringIteration() {
         OpenMapRealVector v = new OpenMapRealVector(new double[]{2, 0, 3, 0, 4});
         Iterator<Entry> it = v.sparseIterator();
         while (it.hasNext()) {
             Entry e = it.next();
             e.setValue(e.getValue() * 10);
         }
         assertEquals(20.0, v.getEntry(0), 0.0);
         assertEquals(30.0, v.getEntry(2), 0.0);
         assertEquals(40.0, v.getEntry(4), 0.0);
     }

     /**
      * Two independently obtained iterators must be able to traverse the
      * vector without interfering with each other, even when one performs
      * setValue.
      */
     @Test
     public void testTwoIteratorsIndependence() {
         OpenMapRealVector v = new OpenMapRealVector(new double[]{1, 0, 2});
         Iterator<Entry> it1 = v.sparseIterator();
         Iterator<Entry> it2 = v.sparseIterator();

         Entry e1 = it1.next();
         Entry e2 = it2.next();
         assertEquals("it1 first index", 0, e1.getIndex());
         assertEquals("it2 first index", 0, e2.getIndex());

         e1.setValue(10.0);
         assertEquals("vector updated by it1", 10.0, v.getEntry(0), 0.0);

         // it2 must still be able to continue.
         assertTrue("it2 must have more elements", it2.hasNext());
         Entry e2next = it2.next();
         assertEquals("it2 next index", 2, e2next.getIndex());
         assertEquals("it2 next value", 2.0, e2next.getValue(), 0.0);
     }

     /**
      * Setting an entry to the default value (0.0 within epsilon tolerance)
      * removes it from the sparse map. The iterator must handle the resulting
      * structural modification without throwing a concurrent modification
      * exception.
      */
     @Test
     public void testSetValueToDefaultRemovesEntry() {
         OpenMapRealVector v = new OpenMapRealVector(new double[]{0, 3.0, 0});
         Iterator<Entry> it = v.sparseIterator();
         assertTrue(it.hasNext());
         Entry e = it.next();
         assertEquals(1, e.getIndex());
         e.setValue(0.0);  // default, should remove entry
         // After the sole non-zero entry became zero the iterator must be empty.
         assertFalse("hasNext must be false after entry removed", it.hasNext());
         assertEquals("entry should be zero", 0.0, v.getEntry(1), 1e-12);
     }

     /**
      * Iterates over a vector with many non-zero entries and performs
      * multiple setValue calls interspersed with hasNext/next. Verifies that
      * the iterator never throws a concurrent modification exception and the
      * final entry count is correct.
      */
     @Test
     public void testSetValueAndHasNextConsistency() {
         double[] data = new double[100];
         for (int i = 0; i < data.length; i++) {
             data[i] = (i % 3 == 0) ? i + 1.0 : 0.0; // about 33 non-zero
         }
         OpenMapRealVector v = new OpenMapRealVector(data);
         Iterator<Entry> it = v.sparseIterator();
         int count = 0;
         while (it.hasNext()) {
             // call hasNext, which might trigger the bug
             assertTrue(it.hasNext()); // should already be true, but safe
             Entry e = it.next();
             if (count % 2 == 0) {
                 e.setValue(e.getValue() + 1.0);
             }
             count++;
         }
         // count must equal the number of initially non-zero entries
         int expectedNonZero = 0;
         for (double d : data) {
             if (!v.isDefaultValue(d)) expectedNonZero++;
         }
         // Since all non-zero entries were kept non-zero (we only added 1),
         // the count should match the original non-zero count.
         assertEquals("all non-zero entries visited", expectedNonZero, count);
     }

     /**
      * Setting the value to the current value (no-op update) must not cause
      * any exception and the iteration must complete normally.
      */
     @Test
     public void testSetValueNoChangeDoesNotBreakIteration() {
         OpenMapRealVector v = new OpenMapRealVector(new double[]{1, 0, 2, 0, 3});
         Iterator<Entry> it = v.sparseIterator();
         int count = 0;
         while (it.hasNext()) {
             Entry e = it.next();
             e.setValue(e.getValue()); // set same value
             count++;
         }
         assertEquals(3, count);
         assertEquals(1.0, v.getEntry(0), 0.0);
         assertEquals(2.0, v.getEntry(2), 0.0);
         assertEquals(3.0, v.getEntry(4), 0.0);
     }
 }
