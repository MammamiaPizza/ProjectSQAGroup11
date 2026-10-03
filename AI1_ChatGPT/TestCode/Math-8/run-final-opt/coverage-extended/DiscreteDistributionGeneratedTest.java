package org.apache.commons.math3.distribution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.util.Pair;
import org.junit.Test;

public class DiscreteDistributionGeneratedTest {

    @Test
    public void testSampleArraySupportsValuesWithDifferentRuntimeClasses() {
        final Object first = new Object() { };
        final Object second = new Object() { };

        final List<Pair<Object, Double>> samples = new ArrayList<Pair<Object, Double>>();
        samples.add(new Pair<Object, Double>(first, 0.0));
        samples.add(new Pair<Object, Double>(second, 1.0));

        final DiscreteDistribution<Object> distribution =
                new DiscreteDistribution<Object>(samples);

        final Object[] result = distribution.sample(3);

        assertEquals(3, result.length);
        assertSame(second, result[0]);
        assertSame(second, result[1]);
        assertSame(second, result[2]);
    }

    @Test
    public void testSampleReturnsTheValueSelectedByProbabilityMass() {
        final Object first = new Object();
        final Object second = new Object();

        final List<Pair<Object, Double>> samples = new ArrayList<Pair<Object, Double>>();
        samples.add(new Pair<Object, Double>(first, 0.0));
        samples.add(new Pair<Object, Double>(second, 1.0));

        final DiscreteDistribution<Object> distribution =
                new DiscreteDistribution<Object>(samples);

        assertSame(second, distribution.sample());
    }

    @Test
    public void testSingleElementSampleHasRequestedLengthAndValue() {
        final List<Pair<String, Double>> samples = new ArrayList<Pair<String, Double>>();
        samples.add(new Pair<String, Double>("only", 1.0));

        final DiscreteDistribution<String> distribution =
                new DiscreteDistribution<String>(samples);

        final Object[] result = distribution.sample(1);

        assertEquals(1, result.length);
        assertEquals("only", result[0]);
    }

    @Test
    public void testGetSamplesRetainsValuesAndReturnsNormalizedProbabilities() {
        final List<Pair<String, Double>> samples = new ArrayList<Pair<String, Double>>();
        samples.add(new Pair<String, Double>("low", 2.0));
        samples.add(new Pair<String, Double>("high", 6.0));

        final DiscreteDistribution<String> distribution =
                new DiscreteDistribution<String>(samples);

        final List<Pair<String, Double>> returned = distribution.getSamples();

        assertEquals(2, returned.size());
        assertEquals("low", returned.get(0).getKey());
        assertEquals(0.25, returned.get(0).getValue(), 0.0);
        assertEquals("high", returned.get(1).getKey());
        assertEquals(0.75, returned.get(1).getValue(), 0.0);
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void testSampleRejectsZeroSampleSize() {
        final List<Pair<String, Double>> samples = new ArrayList<Pair<String, Double>>();
        samples.add(new Pair<String, Double>("value", 1.0));

        new DiscreteDistribution<String>(samples).sample(0);
    }

    @Test(expected = NotPositiveException.class)
    public void testConstructorRejectsNegativeProbability() {
        final List<Pair<String, Double>> samples = new ArrayList<Pair<String, Double>>();
        samples.add(new Pair<String, Double>("value", -0.1));

        new DiscreteDistribution<String>(samples);
    }

    @Test(expected = MathArithmeticException.class)
    public void testConstructorRejectsZeroTotalProbability() {
        final List<Pair<String, Double>> samples = new ArrayList<Pair<String, Double>>();
        samples.add(new Pair<String, Double>("first", 0.0));
        samples.add(new Pair<String, Double>("second", 0.0));

        new DiscreteDistribution<String>(samples);
    }
}
