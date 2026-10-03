package org.apache.commons.math.stat;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

/**

 - Tests for {@link Frequency} that expose bug MATH-259: non-Comparable values
 - should be accepted by addValue(Object) without throwing an unwanted
 - IllegalArgumentException, and getCount(Object) should return the
 - correct frequency for such values.
  */
 public class FrequencyTest {
  @Test
  public void testAddNonComparableToEmptyFrequency() {
  Frequency f = new Frequency();
  Object nonComp = new Object();
  f.addValue(nonComp);
  assertEquals(1, f.getCount(nonComp));
  }
  @Test
  public void testAddNonComparableAfterComparable() {
  Frequency f = new Frequency();
  f.addValue(10);
  Object nonComp = new Object();
  f.addValue(nonComp);
  assertEquals(1, f.getCount(nonComp));
  assertEquals(2, f.getSumFreq());
  }
  @Test
  public void testAddMultipleNonComparables() {
  Frequency f = new Frequency();
  for (int i = 0; i < 5; i++) {
      f.addValue(new Object());
  }
  // All distinct objects, each count should be 1
  assertEquals(5, f.getSumFreq());
  }
  @Test
  public void testAddNullThrowsNullPointerException() {
  Frequency f = new Frequency();
  try {
      f.addValue(null);
      fail("Expected NullPointerException");
  } catch (NullPointerException e) {
      // expected
  } catch (IllegalArgumentException e) {
      fail("Threw wrong exception: " + e);
  }
  }
  @Test
  public void testAddNullAfterComparableThrowssNullPointerException() {
  Frequency f = new Frequency();
  f.addValue(1);
  try {
      f.addValue(null);
      fail("Expected NullPointerException");
  } catch (NullPointerException e) {
      // expected
  } catch (IllegalArgumentException e) {
      fail("Threw wrong exception: " + e);
  }
  }
  @Test
  public void testGetCountOfNonComparable() {
  Frequency f = new Frequency();
  Object nonComp = new Object();
  f.addValue(nonComp);
  f.addValue(nonComp); // add same object again
  assertEquals(2, f.getCount(nonComp));
  }
  @Test
  public void testGetCountOfNonComparableReturnsZeroForMissing() {
  Frequency f = new Frequency();
  f.addValue(42);
  assertEquals(0, f.getCount(new Object()));
  }
  @Test
  public void testClearAfterNonComparable() {
  Frequency f = new Frequency();
  Object nonComp = new Object();
  f.addValue(nonComp);
  f.clear();
  assertEquals(0, f.getCount(nonComp));
  assertEquals(0, f.getSumFreq());
  }
  @Test
  public void testvaluesIteratorContainsNonComparable() {
  Frequency f = new Frequency();
  Object nonComp = new Object();
  f.addValue(nonComp);
  Iterator<?> it = f.valuesIterator();
  assertTrue(it.hasNext());
  Object next = it.next();
  assertSame(nonComp, next);
  assertFalse(it.hasNext());
  }
  @Test
  public void testAddNonComparableWithCustomComparator() {
  // Use a comparator that can compare any object by identity
  Frequency f = new Frequency(new java.util.Comparator() {
      public int compare(Object o1, Object o2) {
          if (o1 == o2) return 0;
          // consistent but total ordering: use hash code
          int h1 = System.identityHashCode(o1);
          int h2 = System.identityHashCode(o2);
          return h1 < h2 ? -1 : (h1 > h2 ?1 : 0);
      }
      public boolean equals(Object obj) { return false; }
  });
  Object nonComp = new Object();
  f.addValue(nonComp);
  assertEquals(1, f.getCount(nonComp));
  }
  @Test
  public void testAddNonComparableDoesNotAffectComparableCounts() {
  Frequency f = new Frequency();
  f.addValue(1);
  f.addValue(1);
  Object nonComp = new Object();
  f.addValue(nonComp);
  assertEquals(2, f.getCount(1));
  assertEquals(1, f.getCount(nonComp));
  }
  @Test
  public void testAddNonComparableToFrequencyThatAlreadyContAinsOnlyNonComprable() {
  Frequency f = new Frequency();
  Object o1 = new Object();
  Object o2 = new Object();
  f.addValue(o1);
  f.addValue(o2);
  assertEquals(1, f.getCount(o1));
  assertEquals(1, f.getCount(o2));
  }

}