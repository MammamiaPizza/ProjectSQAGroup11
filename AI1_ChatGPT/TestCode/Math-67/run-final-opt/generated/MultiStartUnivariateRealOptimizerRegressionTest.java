package org.apache.commons.math.optimization;

import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.random.JDKRandomGenerator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MultiStartUnivariateRealOptimizerRegressionTest {

    private UnivariateRealFunction quinticFunction() {
        return new UnivariateRealFunction() {
            public double value(double x) {
                return x * (x * x - 1.0) * (x * x - 0.25);
            }
        };
    }

    private MultiStartUnivariateRealOptimizer newQuinticOptimizer() {
        JDKRandomGenerator generator = new JDKRandomGenerator();
        generator.setSeed(4312000053L);
        return new MultiStartUnivariateRealOptimizer(new BrentOptimizer(), 10, generator);
    }

    @Test
    public void testQuinticMinWithRandomStartsUsesOrderedGeneratedBounds()
        throws Exception {
        MultiStartUnivariateRealOptimizer optimizer = newQuinticOptimizer();

        double optimum = optimizer.optimize(quinticFunction(), GoalType.MINIMIZE,
                                            -0.3, -0.2);

        assertEquals(-0.27195612846834, optimum, 1.0e-13);
        assertEquals(-0.27195612846834, optimizer.getResult(), 1.0e-13);
        assertEquals(-0.04433426954946637, optimizer.getFunctionValue(), 1.0e-13);
    }

    @Test
    public void testOptimaAndValuesAreSortedAndDefensivelyCopied()
        throws Exception {
        JDKRandomGenerator generator = new JDKRandomGenerator();
        generator.setSeed(123456789L);
        MultiStartUnivariateRealOptimizer optimizer =
            new MultiStartUnivariateRealOptimizer(new BrentOptimizer(), 4, generator);

        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                double delta = x - 0.25;
                return -(delta * delta);
            }
        };

        double result = optimizer.optimize(function, GoalType.MAXIMIZE, -1.0, 1.0);
        double[] optima = optimizer.getOptima();
        double[] values = optimizer.getOptimaValues();

        assertEquals(4, optima.length);
        assertEquals(4, values.length);
        assertEquals(0.25, result, 1.0e-6);
        assertEquals(result, optima[0], 1.0e-6);
        assertEquals(0.0, values[0], 1.0e-12);

        for (int i = 1; i < values.length; i++) {
            assertEquals(function.value(optima[i]), values[i], 1.0e-12);
            assertEquals(true, values[i - 1] >= values[i]);
        }

        optima[0] = Double.NaN;
        values[0] = Double.NaN;
        assertEquals(result, optimizer.getOptima()[0], 1.0e-6);
        assertEquals(0.0, optimizer.getOptimaValues()[0], 1.0e-12);
    }

    @Test
    public void testOptimaAreUnavailableBeforeOptimization() {
        MultiStartUnivariateRealOptimizer optimizer = newQuinticOptimizer();

        try {
            optimizer.getOptima();
            fail("getOptima should reject access before optimization");
        } catch (IllegalStateException expected) {
            // expected
        }

        try {
            optimizer.getOptimaValues();
            fail("getOptimaValues should reject access before optimization");
        } catch (IllegalStateException expected) {
            // expected
        }
    }
}
