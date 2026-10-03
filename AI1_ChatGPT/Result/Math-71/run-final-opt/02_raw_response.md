package org.apache.commons.math.ode.nonstiff;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.junit.Test;

public class EndTimeEventHandlingTest {

    private static final class UnitRateEquation implements FirstOrderDifferentialEquations {
        public int getDimension() {
            return 1;
        }

        public void computeDerivatives(double t, double[] y, double[] yDot) {
            yDot[0] = 1.0;
        }
    }

    @Test
    public void testClassicalRungeKuttaStopsAtLargeEndTimeInsteadOfAdvancingOneStep()
        throws Exception {

        final double start = 1878250319.9999986;
        final double end = 1878250379.9999986;
        final double[] y = new double[1];

        ClassicalRungeKuttaIntegrator integrator =
            new ClassicalRungeKuttaIntegrator(60.0);

        final double stop = integrator.integrate(
            new UnitRateEquation(), start, new double[] { 0.0 }, end, y);

        assertEquals(end, stop, 1.0e-5);
        assertEquals(end - start, y[0], 1.0e-5);
    }

    @Test
    public void testDormandPrinceStopsAtLargeEndTimeInsteadOfAdvancingPastIt()
        throws Exception {

        final double start = 1878250319.9999986;
        final double end = 1878250379.9999986;
        final double[] y = new double[1];

        DormandPrince853Integrator integrator =
            new DormandPrince853Integrator(1.0e-3, 100.0, 1.0e-10, 1.0e-10);

        final double stop = integrator.integrate(
            new UnitRateEquation(), start, new double[] { 0.0 }, end, y);

        assertEquals(end, stop, 1.0e-5);
        assertEquals(end - start, y[0], 1.0e-5);
    }

    @Test
    public void testClassicalRungeKuttaTruncatesForwardFinalStep()
        throws Exception {

        final double[] y = new double[1];
        ClassicalRungeKuttaIntegrator integrator =
            new ClassicalRungeKuttaIntegrator(3.0);

        final double stop = integrator.integrate(
            new UnitRateEquation(), 0.0, new double[] { 0.0 }, 10.0, y);

        assertEquals(10.0, stop, 0.0);
        assertEquals(10.0, y[0], 1.0e-12);
    }

    @Test
    public void testClassicalRungeKuttaTruncatesBackwardFinalStep()
        throws Exception {

        final double[] y = new double[1];
        ClassicalRungeKuttaIntegrator integrator =
            new ClassicalRungeKuttaIntegrator(3.0);

        final double stop = integrator.integrate(
            new UnitRateEquation(), 10.0, new double[] { 10.0 }, 0.0, y);

        assertEquals(0.0, stop, 0.0);
        assertEquals(0.0, y[0], 1.0e-12);
    }
}