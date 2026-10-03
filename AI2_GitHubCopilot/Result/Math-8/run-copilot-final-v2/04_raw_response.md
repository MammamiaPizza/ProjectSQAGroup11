@Test(expected = org.apache.commons.math3.exception.NotPositiveException.class)
    public void testNegativeProbabilityThrows() {
        java.util.List<org.apache.commons.math3.util.Pair<Integer, Double>> items =
                new java.util.ArrayList<org.apache.commons.math3.util.Pair<Integer, Double>>();
        items.add(new org.apache.commons.math3.util.Pair<Integer, Double>(1, -1.0));
        new org.apache.commons.math3.distribution.DiscreteDistribution<Integer>(items);
    }

 @Test
 public void testGetSamples() {
     java.util.List<org.apache.commons.math3.util.Pair<Integer, Double>> input =
             new java.util.ArrayList<org.apache.commons.math3.util.Pair<Integer, Double>>();
     input.add(new org.apache.commons.math3.util.Pair<Integer, Double>(1, 0.7));
     input.add(new org.apache.commons.math3.util.Pair<Integer, Double>(2, 0.3));
     org.apache.commons.math3.distribution.DiscreteDistribution<Integer> dist =
             new org.apache.commons.math3.distribution.DiscreteDistribution<Integer>(input);
     java.util.List<org.apache.commons.math3.util.Pair<Integer, Double>> samples =
dist.getSamples();
     org.junit.Assert.assertEquals(2, samples.size());
     org.junit.Assert.assertEquals(Integer.valueOf(1), samples.get(0).getKey());
     org.junit.Assert.assertEquals(0.7, samples.get(0).getValue().doubleValue(), 0.0);
     org.junit.Assert.assertEqual(Integer.valueOf(2), samples.get(1).getKey());
     org.junit.Assert.assertEqual(0.3, samples.get(1).getValue().doubleValue(), 0.0);
 }

 @Test
 public void testProbabilityMethod() {
     java.util.List<org.apache.commons.math3.util.Pair<String, Double>> input =
             new java.util.ArrayList<org.apache.commons.math3.util.Pair<String, Double>>();
     input.add(new org.apache.commons.math3.util.Pair<String, Double>("a", 0.2));
     input.add(new org.apache.commons.math3.util.Pair<String, Double>("b", 0.3));
     input.add(new org.apache.commons.math3.util.Pair<String, Double>(null, 0.5));
     org.apache.commons.math3.distribution.DiscreteDistribution<String> dist =
             new org.apache.commons.math3.distribution.DiscreteDistribution<String>(input);
     org.junit.Assert.assertEqual(0.2, dist.probability("a"), 1e-9);
     org.junit.Assert.assertEqual(0.0, dist.probability("c"), 1e-9);
     org.junit.Assert.assertEqual(0.5, dist.probability(null), 1e-9);
 }