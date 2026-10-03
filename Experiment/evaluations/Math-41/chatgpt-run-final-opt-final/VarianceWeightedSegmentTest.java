package org.apache.commons.math.stat.descriptive.moment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class VarianceWeightedSegmentTest {

    @Test
    public void testWeightedSegmentBiasCorrectedUsesOnlySegmentWeights() {
        double[] values = {100.0, 1.0, 3.0, 5.0, 200.0};
        double[] weights = {1000.0, 1.0, 2.0, 1.0, 1000.0};

        Variance variance = new Variance(true);

        assertEquals(8.0 / 3.0, variance.evaluate(values, weights, 1, 3), 0.0);
    }

    @Test
    public void testWeightedSegmentUncorrectedUsesOnlySegmentWeights() {
        double[] values = {100.0, 1.0, 3.0, 5.0, 200.0};
        double[] weights = {1000.0, 1.0, 2.0, 1.0, 1000.0};

        Variance variance = new Variance(false);

        assertEquals(2.0, variance.evaluate(values, weights, 1, 3), 0.0);
    }

    @Test
    public void testWeightedSegmentWithSuppliedMeanUsesOnlySegmentWeightSum() {
        double[] values = {100.0, 1.0, 3.0, 6.0, 200.0};
        double[] weights = {1000.0, 1.0, 2.0, 1.0, 1000.0};

        Variance variance = new Variance(true);

        assertEquals(4.25, variance.evaluate(values, weights, 3.0, 1, 3), 0.0);
    }

    @Test
    public void testWeightedWholeArrayVariance() {
        double[] values = {1.0, 2.0, 4.0};
        double[] weights = {1.0, 2.0, 1.0};

        Variance variance = new Variance(true);

        assertEquals(19.0 / 12.0, variance.evaluate(values, weights), 0.0);
    }

    @Test
    public void testUnweightedSegmentHonorsBiasCorrectionSetting() {
        double[] values = {100.0, 1.0, 3.0, 5.0, 200.0};

        assertEquals(4.0, new Variance(true).evaluate(values, 1, 3), 0.0);
        assertEquals(8.0 / 3.0, new Variance(false).evaluate(values, 1, 3), 0.0);
    }

    @Test
    public void testSingleAndEmptyArrayEvaluation() {
        Variance variance = new Variance();

        assertEquals(0.0, variance.evaluate(new double[] {7.0}), 0.0);
        assertTrue(Double.isNaN(variance.evaluate(new double[0])));
    }

    @Test
    public void testIncrementAndBiasCorrectionToggle() {
        Variance variance = new Variance(true);
        variance.increment(2.0);
        variance.increment(4.0);
        variance.increment(6.0);

        assertEquals(3L, variance.getN());
        assertEquals(4.0, variance.getResult(), 0.0);

        variance.setBiasCorrected(false);
        assertEquals(8.0 / 3.0, variance.getResult(), 0.0);
    }

    @Test
    public void testInvalidInputsAreRejected() {
        Variance variance = new Variance();

        try {
            variance.evaluate((double[]) null);
            fail("Expected null values to be rejected");
        } catch (IllegalArgumentException expected) {
            // expected
        }

        try {
            variance.evaluate(new double[] {1.0, 2.0}, new double[] {1.0});
            fail("Expected mismatched values and weights to be rejected");
        } catch (IllegalArgumentException expected) {
            // expected
        }

        try {
            variance.evaluate(new double[] {1.0, 2.0}, 1, 2);
            fail("Expected an invalid array segment to be rejected");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }
}
