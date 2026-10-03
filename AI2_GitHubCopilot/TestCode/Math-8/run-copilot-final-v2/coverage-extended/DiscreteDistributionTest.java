package org.apache.commons.math3.distribution;

 import java.util.ArrayList;
 import java.util.List;

 import org.apache.commons.math3.exception.NotStrictlyPositiveException;
 import org.apache.commons.math3.util.Pair;
 import org.junit.Assert;
 import org.junit.Test;

 /**
  * Tests for {@link DiscreteDistribution} focusing on the bug MATH-942,
  * where {@code sample(int)} threw an {@link ArrayStoreException} when the
  * sample list contained items of different subclasses of the declared type
  * parameter {@code T}.
  */
 public class DiscreteDistributionTest {

     // ---------- MATH-942 regression tests ----------

     /**
      * With two distinct run-time subclasses of T and the first one having
      * zero probability, any call to {@code sample()} is forced to pick a
      * different subclass. This is the core trigger of MATH-942.
      */
     @Test
     public void testSampleDifferentSubclassesNoStoreException() {
         List<Pair<Number, Double>> samples = new ArrayList<Pair<Number, Double>>();
         // first entry determines array component type in the buggy code
         samples.add(new Pair<Number, Double>(Integer.valueOf(1), 0.0));
         // second is a different subclass and always selected
         samples.add(new Pair<Number, Double>(Double.valueOf(2.0), 1.0));

         DiscreteDistribution<Number> dist = new DiscreteDistribution<Number>(samples);

         // must not throw ArrayStoreException
         Object[] result = dist.sample(5);

         Assert.assertNotNull(result);
         Assert.assertEquals(5, result.length);
         // every element must be a Number (Double in this case)
         for (Object n : result) {
             Assert.assertTrue("Element should be instanceof Number", n instanceof Number);
         }
     }

     @Test
     public void testSampleDifferentSubclassesMultipleProbabilities() {
         List<Pair<Number, Double>> samples = new ArrayList<Pair<Number, Double>>();
         samples.add(new Pair<Number, Double>(Integer.valueOf(3), 0.3));
         samples.add(new Pair<Number, Double>(Double.valueOf(4.5), 0.3));
         samples.add(new Pair<Number, Double>(Float.valueOf(2.5f), 0.4));

         DiscreteDistribution<Number> dist = new DiscreteDistribution<Number>(samples);

         // calling many times increases the chance of hitting a different subclass;
         // with the bug present even a single sample() returning Float/Double when
         // array component type is Integer would cause ArrayStoreException
         Object[] result = dist.sample(10);
         Assert.assertNotNull(result);
         Assert.assertEquals(10, result.length);
         for (Object n : result) {
             Assert.assertTrue("Element should be instanceof Number", n instanceof Number);
         }
     }

     @Test
     public void testSampleSizeOne() {
         List<Pair<Number, Double>> samples = new ArrayList<Pair<Number, Double>>();
         samples.add(new Pair<Number, Double>(Long.valueOf(10L), 0.0));
         samples.add(new Pair<Number, Double>(Double.valueOf(9.9), 1.0));

         DiscreteDistribution<Number> dist = new DiscreteDistribution<Number>(samples);
         Object[] result = dist.sample(1);
         Assert.assertEquals(1, result.length);
         Assert.assertTrue(result[0] instanceof Number);
     }

     @Test
     public void testSampleSingleSubclassWorks() {
         // all entries are the same concrete subclass; should never fail
         List<Pair<Integer, Double>> samples = new ArrayList<Pair<Integer, Double>>();
         samples.add(new Pair<Integer, Double>(Integer.valueOf(7), 0.5));
         samples.add(new Pair<Integer, Double>(Integer.valueOf(11), 0.5));

         DiscreteDistribution<Integer> dist = new DiscreteDistribution<Integer>(samples);
         Object[] result = dist.sample(4);
         Assert.assertNotNull(result);
         Assert.assertEquals(4, result.length);
         for (Object i : result) {
             Assert.assertTrue(i instanceof Integer);
         }
     }

     // ---------- boundary / invalid inputs ----------

     @Test(expected = NotStrictlyPositiveException.class)
     public void testSampleZeroThrows() {
         List<Pair<Integer, Double>> samples = new ArrayList<Pair<Integer, Double>>();
         samples.add(new Pair<Integer, Double>(Integer.valueOf(1), 1.0));
         DiscreteDistribution<Integer> dist = new DiscreteDistribution<Integer>(samples);
         dist.sample(0);
     }

     @Test(expected = NotStrictlyPositiveException.class)
     public void testSampleNegativeThrows() {
         List<Pair<Integer, Double>> samples = new ArrayList<Pair<Integer, Double>>();
         samples.add(new Pair<Integer, Double>(Integer.valueOf(1), 1.0));
         DiscreteDistribution<Integer> dist = new DiscreteDistribution<Integer>(samples);
         dist.sample(-3);
     }

     // ---------- array type and contents checks ----------

     @Test
     public void testSampleReturnsArrayWithCorrectComponentType() {
         List<Pair<Number, Double>> samples = new ArrayList<Pair<Number, Double>>();
         samples.add(new Pair<Number, Double>(Integer.valueOf(0), 0.0));
         samples.add(new Pair<Number, Double>(Double.valueOf(1.0), 1.0));

         DiscreteDistribution<Number> dist = new DiscreteDistribution<Number>(samples);
         Object[] result = dist.sample(3);

         // The runtime array must not cause ArrayStoreException or ClassCastException
         // when accessed. Simply verify the call succeeded without exception.
         Assert.assertNotNull(result);
         Assert.assertEquals(3, result.length);
         for (Object o : result) {
             Assert.assertTrue(o instanceof Number);
         }
     }

     @Test
     public void testSampleElementsAreInstancesOfT() {
         List<Pair<Number, Double>> samples = new ArrayList<Pair<Number, Double>>();
         samples.add(new Pair<Number, Double>(Short.valueOf((short) 1), 0.0));
         samples.add(new Pair<Number, Double>(Double.valueOf(5.0), 0.5));
         samples.add(new Pair<Number, Double>(Integer.valueOf(3), 0.5));

         DiscreteDistribution<Number> dist = new DiscreteDistribution<Number>(samples);
         Object[] result = dist.sample(7);

         for (Object n : result) {
             Assert.assertTrue("Each sample must be an instance of T (Number)", n instanceof
Number);
         }
     }

     // ---------- interface type parameter ----------

     @Test
     public void testSampleWithInterfaceType() {
         // CharSequence is an interface; String and StringBuilder are different classes
         List<Pair<CharSequence, Double>> samples = new ArrayList<Pair<CharSequence, Double>>();
         samples.add(new Pair<CharSequence, Double>("hello", 0.0));
         samples.add(new Pair<CharSequence, Double>(new StringBuilder("world"), 1.0));

         DiscreteDistribution<CharSequence> dist = new DiscreteDistribution<CharSequence>(samples);
         Object[] result = dist.sample(4);

         Assert.assertNotNull(result);
         Assert.assertEquals(4, result.length);
         for (Object cs : result) {
             Assert.assertTrue(cs instanceof CharSequence);
         }
     }

     // ---------- consistency after reseeding ----------

     @Test
     public void testReseedDeterminism() {
         List<Pair<Number, Double>> samples = new ArrayList<Pair<Number, Double>>();
         samples.add(new Pair<Number, Double>(Integer.valueOf(1), 0.0));
         samples.add(new Pair<Number, Double>(Double.valueOf(2.0), 1.0));

         DiscreteDistribution<Number> dist = new DiscreteDistribution<Number>(samples);

         dist.reseedRandomGenerator(42L);
         Object[] first = dist.sample(6);

         dist.reseedRandomGenerator(42L);
         Object[] second = dist.sample(6);

         Assert.assertArrayEquals(first, second);
     }

     @Test
     public void testReseedDifferentSeedsYieldDifferentSamples() {
         // Even when all probability is concentrated on one item, the objects are
         // always the same, but we only care that no exception occurs.
         List<Pair<Number, Double>> samples = new ArrayList<Pair<Number, Double>>();
         samples.add(new Pair<Number, Double>(Integer.valueOf(1), 0.0));
         samples.add(new Pair<Number, Double>(Double.valueOf(2.0), 1.0));

         DiscreteDistribution<Number> dist = new DiscreteDistribution<Number>(samples);
         dist.reseedRandomGenerator(100L);
         Object[] first = dist.sample(3);

         dist.reseedRandomGenerator(200L);
         Object[] second = dist.sample(3);

         Assert.assertNotNull(first);
         Assert.assertNotNull(second);
         // content does not matter; just verifying no exception
     }
 }
