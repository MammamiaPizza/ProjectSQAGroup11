package org.apache.commons.math.stat;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import java.util.Iterator;

 import org.junit.Test;

 /**
  * Test class for {@link Frequency} targeting bug MATH-259:
  * addValue(Object) should handle non-Comparable arguments by throwing
  * IllegalArgumentException instead of a ClassCastException.
  */
 public class FrequencyTest {

     @Test
     public void testEmptyFrequency() {
         Frequency f = new Frequency();
         assertEquals("Empty frequency should have sum freq 0", 0, f.getSumFreq());
         assertEquals("getCount on non-existent object should be 0", 0, f.getCount(new Object()));
         assertEquals("getCount(Integer) on empty should be 0", 0, f.getCount(42));
         assertTrue("getPct on empty should be NaN", Double.isNaN(f.getPct("test")));
         assertTrue("getCumPct on empty should be NaN", Double.isNaN(f.getCumPct('a')));
     }

     @Test
     public void testAddComparableString() {
         Frequency f = new Frequency();
         f.addValue("hello");
         assertEquals("Count of 'hello' after one addition should be 1", 1, f.getCount("hello"));
         f.addValue("hello");
         assertEquals("Count of 'hello' after second addition should be 2", 2, f.getCount("hello"));
         assertEquals("Sum freq should be 2", 2, f.getSumFreq());
     }

     @Test
     public void testAddComparableInteger() {
         Frequency f = new Frequency();
         f.addValue(Integer.valueOf(5));
         assertEquals("Count of 5 should be 1", 1, f.getCount(5));
         f.addValue(5);   // primitive int overload
         assertEquals("Count of 5 after adding primitive int should be 2", 2, f.getCount(5));
     }

     @Test
     public void testAddPrimitiveTypes() {
         Frequency f = new Frequency();
         f.addValue(10L);         // long
         f.addValue(10);          // int
         f.addValue('A');         // char
         assertEquals("Count of Long 10 should combine int and long additions", 2, f.getCount(10L));
         assertEquals("Count of char 'A' should be 1", 1, f.getCount('A'));
         assertEquals("Sum freq should be 3", 3, f.getSumFreq());
     }

     @Test
     public void testAddNonComparableThrowsIllegalArgumentException() {
         Frequency f = new Frequency();
         // make freqTable contain a Comparable so that the ClassCastException
         // detection path inside addValue(Comparable) is exercised
         f.addValue("initial");
         try {
             f.addValue(new Object()); // non-Comparable via deprecated Object method
             fail("Expected IllegalArgumentException for non-Comparable argument");
         } catch (IllegalArgumentException e) {
             // Expected after fix: bug MATH-259 ensures no ClassCastException here
         } catch (ClassCastException e) {
             // Buggy behaviour: the deprecated addValue(Object) performs an invalid cast
             fail("ClassCastException should not be thrown; IllegalArgumentException is expected");
         }
     }

     @Test
     public void testAddNullObjectDeprecated() {
         Frequency f = new Frequency();
         try {
             f.addValue((Object) null);
             fail("Expected NullPointerException for null argument via deprecated method");
         } catch (NullPointerException expected) {
             // the underlying TreeMap forbids null keys
         }
     }

     @Test
     public void testGetCountForNonExistent() {
         Frequency f = new Frequency();
         f.addValue("foo");
         assertEquals("Count of non-existent should be 0", 0, f.getCount("bar"));
         assertEquals("Count of non-existent integer should be 0", 0, f.getCount(99));
     }

     @Test
     public void testGetCumFreqOrdering() {
         Frequency f = new Frequency();
         f.addValue(1);
         f.addValue(2);
         f.addValue(3);
         f.addValue(2); // frequencies: 1->1, 2->2, 3->1
         // cumulative: 1->1, 2->3, 3->4
         assertEquals("Cum freq of value 1", 1, f.getCumFreq(1));
         assertEquals("Cum freq of value 2", 3, f.getCumFreq(2));
         assertEquals("Cum freq of value 3", 4, f.getCumFreq(3));
         assertEquals("Cum freq below lowest", 0, f.getCumFreq(0));
         assertEquals("Cum freq above highest", 4, f.getCumFreq(4));
     }

     @Test
     public void testGetPctAndCumPct() {
         Frequency f = new Frequency();
         f.addValue("a");
         f.addValue("b");
         f.addValue("b");
         // a: count=1 (33.3…%), b: count=2 (66.6…%)
         assertEquals("Pct of 'a'", 1.0 / 3.0, f.getPct("a"), 1e-9);
         assertEquals("Pct of 'b'", 2.0 / 3.0, f.getPct("b"), 1e-9);
         // cumulative: a->0.333, b->1.0
         assertEquals("Cum Pct of 'a'", 1.0 / 3.0, f.getCumPct("a"), 1e-9);
         assertEquals("Cum Pct of 'b'", 1.0, f.getCumPct("b"), 1e-9);
     }

     @Test
     public void testClear() {
         Frequency f = new Frequency();
         f.addValue("x");
         f.addValue(42);
         assertEquals("Sum freq before clear", 2, f.getSumFreq());
         f.clear();
         assertEquals("Sum freq after clear", 0, f.getSumFreq());
         assertEquals("Count after clear", 0, f.getCount("x"));
     }

     @Test
     public void testValuesIterator() {
         Frequency f = new Frequency();
         f.addValue(10);
         f.addValue(20);
         f.addValue(10);
         Iterator<?> it = f.valuesIterator();
         assertTrue("Iterator should have next", it.hasNext());
         Object first = it.next();
         assertTrue("First value should be 10 or 20 (depends on comparator)",
                 first.equals(Long.valueOf(10)) || first.equals(Long.valueOf(20)));
         assertTrue("Should have second element", it.hasNext());
         Object second = it.next();
         assertFalse("Should have no third element (only two distinct values)", it.hasNext());
         assertNotNull(first);
         assertNotNull(second);
     }

     @Test
     public void testToStringContainsData() {
         Frequency f = new Frequency();
         f.addValue("score");
         String out = f.toString();
         assertTrue("toString should contain the value", out.contains("score"));
         assertTrue("toString should contain frequency", out.contains("1"));
     }
 }