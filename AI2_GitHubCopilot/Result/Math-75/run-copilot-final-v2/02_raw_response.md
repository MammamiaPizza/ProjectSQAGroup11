package org.apache.commons.math.stat;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 import org.junit.Test;

 public class FrequencyTest {

     @Test
     public void testGetPctObjectBug() {
         Frequency f = new Frequency();
         // Add three distinct strings, each once
         f.addValue("A");
         f.addValue("B");
         f.addValue("C");
         // getPct(Object) is buggy: returns cumulative percentage instead of individual percentage
         // For "B", expected pct = count("B")/sumFreq = 1/3
         // Buggy returns cumPct("B") = 2/3
         assertEquals("getPct(Object) should return individual percentage",
                 1.0 / 3.0, f.getPct((Object) "B"), 1e-9);
     }

     @Test
     public void testGetPctComparableCorrect() {
         Frequency f = new Frequency();
         f.addValue("A");
         f.addValue("B");
         f.addValue("C");
         // getPct(Comparable) should be correct even in buggy version
         assertEquals("getPct(Comparable) for 'B'",
                 1.0 / 3.0, f.getPct("B"), 1e-9);
     }

     @Test
     public void testGetPctIntIsCorrect() {
         Frequency f = new Frequency();
         f.addValue(10);
         f.addValue(20);
         f.addValue(30);
         // getPct(int) uses getPct(Long.valueOf) which calls getPct(Comparable) – correct
         assertEquals("getPct(int) for 20 with 3 distinct values",
                 1.0 / 3.0, f.getPct(20), 1e-9);
     }

     @Test
     public void testGetPctObjectOnEmptyTable() {
         Frequency f = new Frequency();
         // When table is empty, getPct(Object) should return NaN (buggy version returns NaN too)
         assertTrue("getPct(Object) on empty frequency should be NaN",
                 Double.isNaN(f.getPct((Object) "X")));
     }

     @Test
     public void testGetPctObjectSingleEntry() {
         Frequency f = new Frequency();
         f.addValue("only");
         // Single entry -> pct = 1.0
         assertEquals("Single entry getPct(Object)", 1.0, f.getPct((Object) "only"), 1e-9);
     }

     @Test
     public void testMixedAddAndGetPctConsistency() {
         Frequency f = new Frequency();
         // Add same integer via int, Integer, and Object
         f.addValue(5);           // int
         f.addValue(Integer.valueOf(5)); // Integer
         f.addValue((Object) Integer.valueOf(5)); // Object
         f.addValue(7);           // another distinct int
         // Total sum = 4, count for 5 = 3, pct = 3/4 = 0.75
         assertEquals("getPct(int) for 5", 0.75, f.getPct(5), 1e-9);
         assertEquals("getPct(Object) for 5", 0.75, f.getPct((Object) Integer.valueOf(5)), 1e-9);
     }

     @Test
     public void testCumPctObjectCorrect() {
         Frequency f = new Frequency();
         f.addValue(1);
         f.addValue(2);
         f.addValue(3);
         // getCumPct(Object 2) should be (count(1)+count(2))/sumFreq = 2/3
         // Note: getCumPct(Object) delegates correctly to getCumPct(Comparable)
         assertEquals("getCumPct(Object) for 2", 2.0 / 3.0, f.getCumPct((Object)
Integer.valueOf(2)), 1e-9);
     }

     @Test
     public void testCumPctComparableCorrect() {
         Frequency f = new Frequency();
         f.addValue(1);
         f.addValue(2);
         f.addValue(3);
         assertEquals("getCumPct(Comparable) for 2", 2.0 / 3.0, f.getCumPct(Integer.valueOf(2)),
1e-9);
     }

     @Test
     public void testGetPctWithMultipleEqualFrequencies() {
         Frequency f = new Frequency();
         f.addValue("X");
         f.addValue("X");
         f.addValue("Y");
         f.addValue("Y");
         f.addValue("Z");
         f.addValue("Z");
         // Each count=2, sum=6, individual pct = 2/6 = 1/3
         assertEquals("pct(Comparable) for X", 1.0 / 3.0, f.getPct("X"), 1e-9);
         assertEquals("pct(Comparable) for Y", 1.0 / 3.0, f.getPct("Y"), 1e-9);
         assertEquals("pct(Comparable) for Z", 1.0 / 3.0, f.getPct("Z"), 1e-9);
         // Object method should give the same (but buggy will fail for non-last keys)
         assertEquals("pct(Object) for X", 1.0 / 3.0, f.getPct((Object) "X"), 1e-9);
     }

     @Test
     public void testGetCountAndSumFreqConsistency() {
         Frequency f = new Frequency();
         f.addValue("apple");
         f.addValue("apple");
         f.addValue("banana");
         assertEquals("count 'apple'", 2, f.getCount("apple"));
         assertEquals("count 'banana'", 1, f.getCount("banana"));
         assertEquals("sum freq", 3, f.getSumFreq());
     }

     @Test
     public void testGetPctBoundaryWithZeroCount() {
         Frequency f = new Frequency();
         f.addValue("A");
         f.addValue("B");
         // getPct for a value never added should be 0.0
         assertEquals("getPct for nonexistent Comparable", 0.0, f.getPct("Z"), 1e-9);
         assertEquals("getPct for nonexistent Object", 0.0, f.getPct((Object) "Z"), 1e-9);
     }
 }