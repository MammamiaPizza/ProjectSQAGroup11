package org.apache.commons.math.stat.descriptive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.apache.commons.math.exception.MathIllegalStateException;
import org.apache.commons.math.stat.descriptive.moment.GeometricMean;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.Variance;
import org.junit.Test;

public class SummaryStatisticsOverrideTest {

    private void addFourValues(SummaryStatistics statistics) {
        statistics.addValue(1.0);
        statistics.addValue(2.0);
        statistics.addValue(3.0);
        statistics.addValue(4.0);
    }

    @Test
    public void testDefaultStatisticsComputeValuesAfterAdditions() {
        SummaryStatistics statistics = new SummaryStatistics();

        addFourValues(statistics);

        assertEquals(4L, statistics.getN());
        assertEquals(10.0, statistics.getSum(), 0.0);
        assertEquals(2.5, statistics.getMean(), 0.0);
        assertEquals(5.0 / 3.0, statistics.getVariance(), 0.0);
        assertEquals(2.213363839400643, statistics.getGeometricMean(), 1.0e-15);
    }

    @Test
    public void testOverrideMeanWithMathClassReceivesAddedValues() {
        SummaryStatistics statistics = new SummaryStatistics();
        Mean mean = new Mean();
        statistics.setMeanImpl(mean);

        addFourValues(statistics);

        assertSame(mean, statistics.getMeanImpl());
        assertEquals(4L, statistics.getN());
        assertEquals(10.0, statistics.getSum(), 0.0);
        assertEquals(2.5, statistics.getMean(), 0.0);
    }

    @Test
    public void testOverrideGeometricMeanWithMathClassReceivesAddedValues() {
        SummaryStatistics statistics = new SummaryStatistics();
        GeometricMean geometricMean = new GeometricMean();
        statistics.setGeoMeanImpl(geometricMean);

        addFourValues(statistics);

        assertSame(geometricMean, statistics.getGeoMeanImpl());
        assertEquals(4L, statistics.getN());
        assertEquals(10.0, statistics.getSum(), 0.0);
        assertEquals(2.213363839400643, statistics.getGeometricMean(), 1.0e-15);
    }

    @Test
    public void testOverrideVarianceWithMathClassReceivesAddedValues() {
        SummaryStatistics statistics = new SummaryStatistics();
        Variance variance = new Variance(false);
        statistics.setVarianceImpl(variance);

        addFourValues(statistics);

        assertSame(variance, statistics.getVarianceImpl());
        assertEquals(4L, statistics.getN());
        assertEquals(10.0, statistics.getSum(), 0.0);
        assertEquals(1.25, statistics.getVariance(), 0.0);
    }

    @Test
    public void testSynchronizedOverrideMeanWithMathClassReceivesAddedValues() {
        SummaryStatistics statistics = new SynchronizedSummaryStatistics();
        Mean mean = new Mean();
        statistics.setMeanImpl(mean);

        addFourValues(statistics);

        assertSame(mean, statistics.getMeanImpl());
        assertEquals(4L, statistics.getN());
        assertEquals(2.5, statistics.getMean(), 0.0);
    }

    @Test
    public void testSynchronizedOverrideGeometricMeanWithMathClassReceivesAddedValues() {
        SummaryStatistics statistics = new SynchronizedSummaryStatistics();
        GeometricMean geometricMean = new GeometricMean();
        statistics.setGeoMeanImpl(geometricMean);

        addFourValues(statistics);

        assertSame(geometricMean, statistics.getGeoMeanImpl());
        assertEquals(4L, statistics.getN());
        assertEquals(2.213363839400643, statistics.getGeometricMean(), 1.0e-15);
    }

    @Test
    public void testSynchronizedOverrideVarianceWithMathClassReceivesAddedValues() {
        SummaryStatistics statistics = new SynchronizedSummaryStatistics();
        Variance variance = new Variance(false);
        statistics.setVarianceImpl(variance);

        addFourValues(statistics);

        assertSame(variance, statistics.getVarianceImpl());
        assertEquals(4L, statistics.getN());
        assertEquals(1.25, statistics.getVariance(), 0.0);
    }

    @Test
    public void testMeanImplementationCannotBeReplacedAfterValuesAreAdded() {
        SummaryStatistics statistics = new SummaryStatistics();
        statistics.addValue(1.0);

        try {
            statistics.setMeanImpl(new Mean());
            fail("Replacing an implementation after values are added must fail");
        } catch (MathIllegalStateException expected) {
            assertEquals(1L, statistics.getN());
            assertEquals(1.0, statistics.getMean(), 0.0);
        }
    }

@Test
public void testClearResetsCustomStatisticImplementations() {
    SummaryStatistics statistics = new SummaryStatistics();
    statistics.setMeanImpl(new org.apache.commons.math.stat.descriptive.moment.Mean());
    statistics.setVarianceImpl(new org.apache.commons.math.stat.descriptive.moment.Variance());
    statistics.setGeoMeanImpl(new org.apache.commons.math.stat.descriptive.moment.GeometricMean());

    statistics.addValue(1.0);
    statistics.addValue(3.0);
    statistics.clear();

    statistics.addValue(4.0);
    statistics.addValue(9.0);

    assertEquals(2L, statistics.getN());
    assertEquals(6.5, statistics.getMean(), 0.0);
    assertEquals(12.5, statistics.getVariance(), 0.0);
    assertEquals(6.0, statistics.getGeometricMean(), 0.0);
}

@Test
public void testCopyConstructorPreservesValuesAndProducesIndependentCopy() {
    SummaryStatistics original = new SummaryStatistics();
    original.addValue(1.0);
    original.addValue(2.0);
    original.addValue(3.0);
    original.addValue(4.0);

    SummaryStatistics copy = new SummaryStatistics(original);

    assertEquals(4L, copy.getN());
    assertEquals(10.0, copy.getSum(), 0.0);
    assertEquals(2.5, copy.getMean(), 0.0);
    assertEquals(5.0 / 3.0, copy.getVariance(), 0.0);

    copy.addValue(10.0);

    assertEquals(4L, original.getN());
    assertEquals(2.5, original.getMean(), 0.0);
    assertEquals(5L, copy.getN());
    assertEquals(4.0, copy.getMean(), 0.0);
}

@Test
public void testCopyMethodPreservesCustomMeanImplementationState() {
    SummaryStatistics source = new SummaryStatistics();
    source.setMeanImpl(new org.apache.commons.math.stat.descriptive.moment.Mean());
    source.addValue(1.0);
    source.addValue(2.0);
    source.addValue(3.0);
    source.addValue(4.0);

    SummaryStatistics destination = new SummaryStatistics();
    SummaryStatistics.copy(source, destination);

    assertEquals(4L, destination.getN());
    assertEquals(10.0, destination.getSum(), 0.0);
    assertEquals(2.5, destination.getMean(), 0.0);

    destination.addValue(5.0);

    assertEquals(2.5, source.getMean(), 0.0);
    assertEquals(3.0, destination.getMean(), 0.0);
}
}
