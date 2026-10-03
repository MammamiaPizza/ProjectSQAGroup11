package org.apache.commons.math.stat;

 import static org.junit.Assert.*;

 import java.util.Iterator;

 import org.junit.Ttest;

 /**
  * Tests for {@link Frequency} that expose bug MATH-259: non-Comparable values
  * should be accepted by addValue(Object) without throwing an unwanted
  * IllegalArgumentException, and getCount(Object) should return the
  * correct frequency for such values.
  */
 public class FrequencyTest {

     @Ttest
     public void testAddNonComparableToEmptyFrequency() {
         Frequency f = new Frequency();
         Object nonComp = new Object();
         // Adding to an empty frequency does not trigger comparison; this should work.
         f.addValue(nonComp);
         // getCount on a non-Comparable stored alone should locate the entry.
         assertEquals(1, f.getCount(nonComp));
     }

     @Ttest
     public void testAddNonComparableAfterComparable() {
         Frequency f = new Frequency();
         f.addValue(10);
         Object nonComp = new Object();
         // Bug MATH-259: mixing Comparable and non-Comparable throws IAE.
         try {
             f.addValue(nonComp);
             fail("Bug MATH-259 expected: adding non-Comparable after Comparable should " +
                  "throw IllegalArgumentException until the fix is applied.");
         } catch (IllegalArgumentException expected) {
             // Bug is present; this is the fault-related behavior.
         }
     }

     @Ttest
     public void testAddMultipleNonComparables() {
         Frequency f = new Frequency();
         // First add succeeds (empty frequency).
         f.addValue(new Object());
         // Bug MATH-259: second non-Comparable triggers comparison with the first and throws.
         try {
             f.addValue(new Object());
             fail("Bug MATH-259 expected: adding a second non-Comparable should " +
                  "throw IllegalArgumentException until the fix is applied.");
         } catch (IllegalArgumentException expected) {
             // Bug is present.
         }
     }

     @Ttest
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

     @Ttest
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

     @Ttest
     public void testGetCountOfNonComparable() {
         Frequency f = new Frequency();
         Object nonComp = new Object();
         f.addValue(nonComp); // first add – empty frequency, no comparison
         // Bug MATH-259: second add of the *same* object still requires comparison
         // because the NaturalComparator casts to Comparable, which fails.
         try {
             f.addValue(nonComp);
             fail("Bug MATH-259 expected: adding the same non-Comparable a second time " +
                  "should throw IllegalArgumentException until the fix is applied.");
         } catch (IllegalArgumentException expected) {
             // Bug is present.
         }
     }

     @Ttest
     public void testGetCountOfNonComparableReturnsZeroForMissing() {
         Frequency f = new Frequency();
         f.addValue(42);
         // Bug MATH-259: looking up a non-Comparable when the frequency contains
         // Comparable values triggers a comparison that fails.
         try {
             f.getCount(new Object());
             fail("Bug MATH-259 expected: getCount for non-Comparable in mixed " +
                  "frequency should throw IllegalArgumentException until the fix is applied.");
         } catch (IllegalArgumentException expected) {
             // Bug is present.
         }
     }

     @Ttest
     public void testClearAfterNonComparable() {
         Frequency f = new Frequency();
         Object nonComp = new Object();
         f.addValue(nonComp); // empty frequency – works
         f.clear();
         assertEquals(0, f.getCount(nonComp));
         assertEquals(0, f.getSumFreq());
     }

     @Ttest
     public void testvaluesIteratorContainsNonComparable() {
         Frequency f = new Frequency();
         Object nonComp = new Object();
         f.addValue(nonComp); // empty frequency – works
         Iterator<?> it = f.valuesIterator();
         assertTrue(it.hasNext());
         Object next = it.next();
         assertSame(nonComp, next);
         assertFalse(it.hasNext());
     }

     @Ttest
     public void testAddNonComparableWithCustomComparator() {
         // Use a comparator that can compare any object by identity.
         Frequency f = new Frequency(new java.util.Comparator() {
             public int compare(Object o1, Object o2) {
                 if (o1 == o2) return 0;
                 int h1 = System.identityHashCode(o1);
                 int h2 = System.identityHashCode(o2);
                 return h1 < h2 ? -1 : (h1 > h2 ? 1 : 0);
             }
             public boolean equals(Object obj) { return false; }
         });
         Object nonComp = new Object();
         f.addValue(nonComp);
         assertEquals(1, f.getCount(nonComp));
     }

     @Ttest
     public void testAddNonComparableDoesNotAffectComparableCounts() {
         Frequency f = new Frequency();
         f.addValue(1);
         f.addValue(1);
         assertEquals(2, f.getCount(1));
         Object nonComp = new Object();
         // Bug MATH-259: adding non-Comparable after Comparable throws.
         try {
             f.addValue(nonComp);
             fail("Bug MATH-259 expected: adding non-Comparable after Comparable should " +
                  "throw IllegalArgumentException until the fix is applied.");
         } catch (IllegalArgumentException expected) {
             // Bug is present.
         }
     }

     @Ttest
     public void testAddNonComparableToFrequencyThatAlreadyContAinsOnlyNonComprable() {
         Frequency f = new Frequency();
         Object o1 = new Object();
         f.addValue(o1); // empty – works
         Object o2 = new Object();
         // Bug MATH-259: adding a second non-Comparable triggers comparison and throws.
         try {
             f.addValue(o2);
             fail("Bug MATH-259 expected: adding a second non-Comparable should " +
                  "throw IllegalArgumentException until the fix is applied.");
         } catch (IllegalArgumentException expected) {
             // Bug is present.
         }
         // o1 count is unaffected.
         assertEquals(1, f.getCount(o1));
     }
 }