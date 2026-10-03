package org.apache.commons.math.optimization.univariate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.MathIllegalStateException;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.random.JDKRandomGenerator;
import org.junit.Test;

public class MultiStartUnivariateRealOptimizerGeneratedTest {

    @Test
    public void testQuinticMinimumHasRequiredPrecisionAcrossStarts()
        throws FunctionEvaluationException {
        JDKRandomGenerator generator = new JDKRandomGenerator();
        generator.setSeed(123456789L);

        MultiStartUnivariateRealOptimizer<UnivariateRealFunction> optimizer =
            new MultiStartUnivariateRealOptimizer<UnivariateRealFunction>(
                new BrentOptimizer(1.0e-10, 1.0e-14), 10, generator);
        optimizer.setMaxEvaluations(300);

        UnivariateRealPointValuePair optimum = optimizer.optimize(
            new QuinticFunction(), GoalType.MINIMIZE, -0.3, -0.2);

        assertEquals(-0.2719561279, optimum.getPoint(), 1.0e-9);
        assertEquals(-0.0443342695, optimum.getValue(), 1.0e-9);
        assertTrue(optimizer.getEvaluations() > 0);
        assertTrue(optimizer.getEvaluations() <= 300);
    }

    @Test
    public void testOptimaAreSortedForMinimizationAndReturnedArrayIsDefensive()
        throws FunctionEvaluationException {
        JDKRandomGenerator generator = new JDKRandomGenerator();
        generator.setSeed(987654321L);

        MultiStartUnivariateRealOptimizer<UnivariateRealFunction> optimizer =
            new MultiStartUnivariateRealOptimizer<UnivariateRealFunction>(
                new BrentOptimizer(1.0e-10, 1.0e-14), 4, generator);
        optimizer.setMaxEvaluations(300);

        UnivariateRealPointValuePair best = optimizer.optimize(
            new UnivariateRealFunction() {
                public double value(double x) {
                    return x * x;
                }
            },
            GoalType.MINIMIZE, -1.0, 1.0);

        UnivariateRealPointValuePair[] optima = optimizer.getOptima();
        assertEquals(4, optima.length);
        assertNotNull(optima[0]);
        assertEquals(best.getValue(), optima[0].getValue(), 0.0);
        for (int i = 1; i < optima.length; ++i) {
            if (optima[i] != null) {
                assertTrue(optima[i - 1].getValue() <= optima[i].getValue());
            }
        }

        optima[0] = null;
        assertNotNull(optimizer.getOptima()[0]);
    }

    @Test
    public void testOptimaAreSortedForMaximization()
        throws FunctionEvaluationException {
        JDKRandomGenerator generator = new JDKRandomGenerator();
        generator.setSeed(246813579L);

        MultiStartUnivariateRealOptimizer<UnivariateRealFunction> optimizer =
            new MultiStartUnivariateRealOptimizer<UnivariateRealFunction>(
                new BrentOptimizer(1.0e-10, 1.0e-14), 4, generator);
        optimizer.setMaxEvaluations(300);

        UnivariateRealPointValuePair best = optimizer.optimize(
            new UnivariateRealFunction() {
                public double value(double x) {
                    return x;
                }
            },
            GoalType.MAXIMIZE, -1.0, 1.0);

        UnivariateRealPointValuePair[] optima = optimizer.getOptima();
        assertEquals(1.0, best.getPoint(), 1.0e-8);
        assertEquals(1.0, optima[0].getValue(), 1.0e-8);
        for (int i = 1; i < optima.length; ++i) {
            if (optima[i] != null) {
                assertTrue(optima[i - 1].getValue() >= optima[i].getValue());
            }
        }
    }

    @Test(expected = MathIllegalStateException.class)
    public void testGetOptimaBeforeOptimizationThrows() {
        MultiStartUnivariateRealOptimizer<UnivariateRealFunction> optimizer =
            new MultiStartUnivariateRealOptimizer<UnivariateRealFunction>(
                new BrentOptimizer(1.0e-10, 1.0e-14), 2, new JDKRandomGenerator());

        optimizer.getOptima();
    }

    private static class QuinticFunction implements UnivariateRealFunction {
        public double value(double x) {
            return (x - 1.0) * (x - 0.5) * x * (x + 0.5) * (x + 1.0);
        }
    }
}
