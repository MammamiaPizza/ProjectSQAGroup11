@org.junit.Test
public void testProbabilityAccumulatesDuplicateValuesAndHandlesNull() {
    final java.util.List<org.apache.commons.math3.util.Pair<String, Double>> samples =
        new java.util.ArrayList<org.apache.commons.math3.util.Pair<String, Double>>();
    samples.add(new org.apache.commons.math3.util.Pair<String, Double>("repeated", 1.0));
    samples.add(new org.apache.commons.math3.util.Pair<String, Double>("other", 2.0));
    samples.add(new org.apache.commons.math3.util.Pair<String, Double>("repeated", 3.0));
    samples.add(new org.apache.commons.math3.util.Pair<String, Double>(null, 4.0));

    final org.apache.commons.math3.distribution.DiscreteDistribution<String> distribution =
        new org.apache.commons.math3.distribution.DiscreteDistribution<String>(samples);

    org.junit.Assert.assertEquals(0.4, distribution.probability("repeated"), 0.0);
    org.junit.Assert.assertEquals(0.2, distribution.probability("other"), 0.0);
    org.junit.Assert.assertEquals(0.4, distribution.probability(null), 0.0);
    org.junit.Assert.assertEquals(0.0, distribution.probability("missing"), 0.0);
}

@org.junit.Test
public void testReseedRandomGeneratorRepeatsSampleSequence() throws Exception {
    final java.util.List<org.apache.commons.math3.util.Pair<String, Double>> samples =
        new java.util.ArrayList<org.apache.commons.math3.util.Pair<String, Double>>();
    samples.add(new org.apache.commons.math3.util.Pair<String, Double>("low", 1.0));
    samples.add(new org.apache.commons.math3.util.Pair<String, Double>("middle", 2.0));
    samples.add(new org.apache.commons.math3.util.Pair<String, Double>("high", 3.0));

    final org.apache.commons.math3.distribution.DiscreteDistribution<String> distribution =
        new org.apache.commons.math3.distribution.DiscreteDistribution<String>(samples);

    distribution.reseedRandomGenerator(123456789L);
    final String[] first = distribution.sample(32);
    distribution.reseedRandomGenerator(123456789L);
    final String[] second = distribution.sample(32);

    org.junit.Assert.assertArrayEquals(first, second);
}