package org.apache.commons.math3.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.junit.Test;

public class ContinuedFractionRegressionTest {

    @Test
    public void testAllEvaluationOverloadsForTerminatingFraction() {
        ContinuedFraction fraction = new ContinuedFraction() {
            @Override
            protected double getA(int n, double x) {
                return n == 0 ? 2.0 * x : 1.0;
            }

            @Override
            protected double getB(int n, double x) {
                return 0.0;
            }
        };

        assertEquals(1.0, fraction.evaluate(0.5), 0.0);
        assertEquals(1.0, fraction.evaluate(0.5, 1.0e-12), 0.0);
        assertEquals(1.0, fraction.evaluate(0.5, 2), 0.0);
        assertEquals(1.0, fraction.evaluate(0.5, 1.0e-12, 2), 0.0);
    }

    @Test
    public void testConvergesForSimpleContinuedFraction() {
        ContinuedFraction fraction = new ContinuedFraction() {
            @Override
            protected double getA(int n, double x) {
                return 1.0;
            }

            @Override
            protected double getB(int n, double x) {
                return 1.0;
            }
        };

        assertEquals((1.0 + Math.sqrt(5.0)) / 2.0,
                     fraction.evaluate(0.5, 1.0e-12, 1000),
                     1.0e-11);
    }

    @Test
    public void testNegativeOverflowCoefficientsAreScaledRatherThanRejected() {
        ContinuedFraction fraction = new ContinuedFraction() {
            @Override
            protected double getA(int n, double x) {
                return n == 0 ? 1.0e200 : -1.0e200;
            }

            @Override
            protected double getB(int n, double x) {
                return -1.0e200;
            }
        };

        double value = fraction.evaluate(0.5, 1.0e-12, 10);

        assertFalse(Double.isInfinite(value));
        assertFalse(Double.isNaN(value));
        assertEquals(1.0e200, value, 0.0);
    }

    @Test
    public void testPositiveOverflowCoefficientsAreScaled() {
        ContinuedFraction fraction = new ContinuedFraction() {
            @Override
            protected double getA(int n, double x) {
                return 1.0e200;
            }

            @Override
            protected double getB(int n, double x) {
                return 0.0;
            }
        };

        assertEquals(1.0e200, fraction.evaluate(0.5, 1.0e-12, 10), 0.0);
    }

    @Test
    public void testIterationLimitIsReportedForNonConvergentFraction() {
        ContinuedFraction fraction = new ContinuedFraction() {
            @Override
            protected double getA(int n, double x) {
                return 1.0;
            }

            @Override
            protected double getB(int n, double x) {
                return 1.0;
            }
        };

        try {
            fraction.evaluate(0.5, 1);
            fail("Expected the iteration limit to be reported");
        } catch (MaxCountExceededException expected) {
            assertEquals(Integer.valueOf(1), expected.getMax());
        }
    }

    @Test
    public void testNaNConvergentIsReportedAsDivergence() {
        ContinuedFraction fraction = new ContinuedFraction() {
            @Override
            protected double getA(int n, double x) {
                return n == 0 ? 1.0 : Double.NaN;
            }

            @Override
            protected double getB(int n, double x) {
                return 0.0;
            }
        };

        try {
            fraction.evaluate(0.5, 1.0e-12, 10);
            fail("Expected NaN divergence to be reported");
        } catch (ConvergenceException expected) {
            assertFalse(expected.getMessage().length() == 0);
        }
    }
}
