package org.apache.commons.math.ode.nonstiff;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.events.EventException;
import org.apache.commons.math.ode.events.EventHandler;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EmbeddedRungeKuttaIntegratorGeneratedTest {

    private static final double ACCURACY = 1.0e-10;

    @Test
    public void testDefaultAndConfiguredStepControlParameters() {
        DormandPrince54Integrator integrator =
                new DormandPrince54Integrator(1.0e-8, 1.0, 1.0e-12, 1.0e-12);

        assertEquals(0.9, integrator.getSafety(), 0.0);
        assertEquals(0.2, integrator.getMinReduction(), 0.0);
        assertEquals(10.0, integrator.getMaxGrowth(), 0.0);

        integrator.setSafety(0.73);
        integrator.setMinReduction(0.17);
        integrator.setMaxGrowth(4.5);

        assertEquals(0.73, integrator.getSafety(), 0.0);
        assertEquals(0.17, integrator.getMinReduction(), 0.0);
        assertEquals(4.5, integrator.getMaxGrowth(), 0.0);
    }

    @Test
    public void testPolynomialIntegrationForward() throws Exception {
        DormandPrince54Integrator integrator =
                new DormandPrince54Integrator(1.0e-8, 1.0, 1.0e-12, 1.0e-12);
        double[] result = new double[1];

        double stop = integrator.integrate(new CubicPolynomialEquation(),
                                           0.0, new double[] { 0.0 }, 1.0, result);

        assertEquals(1.0, stop, 0.0);
        assertEquals(1.0, result[0], ACCURACY);
    }

    @Test
    public void testPolynomialIntegrationBackward() throws Exception {
        DormandPrince54Integrator integrator =
                new DormandPrince54Integrator(1.0e-8, 1.0, 1.0e-12, 1.0e-12);
        double[] result = new double[1];

        double stop = integrator.integrate(new CubicPolynomialEquation(),
                                           1.0, new double[] { 1.0 }, 0.0, result);

        assertEquals(0.0, stop, 0.0);
        assertEquals(0.0, result[0], ACCURACY);
    }

    @Test
    public void testForwardStopEventTruncatesStepAtEventTime() throws Exception {
        DormandPrince54Integrator integrator =
                new DormandPrince54Integrator(1.0e-8, 1.0, 1.0e-12, 1.0e-12);
        StoppingTimeEvent event = new StoppingTimeEvent(0.5);
        integrator.addEventHandler(event, 0.1, 1.0e-12, 100);

        double[] result = new double[1];
        double stop = integrator.integrate(new CubicPolynomialEquation(),
                                           0.0, new double[] { 0.0 }, 1.0, result);

        assertEquals(0.5, stop, ACCURACY);
        assertEquals(0.5, event.eventTime, ACCURACY);
        assertEquals(0.125, result[0], ACCURACY);
    }

    @Test
    public void testBackwardStopEventTruncatesStepAtEventTime() throws Exception {
        DormandPrince54Integrator integrator =
                new DormandPrince54Integrator(1.0e-8, 1.0, 1.0e-12, 1.0e-12);
        StoppingTimeEvent event = new StoppingTimeEvent(0.5);
        integrator.addEventHandler(event, 0.1, 1.0e-12, 100);

        double[] result = new double[1];
        double stop = integrator.integrate(new CubicPolynomialEquation(),
                                           1.0, new double[] { 1.0 }, 0.0, result);

        assertEquals(0.5, stop, ACCURACY);
        assertEquals(0.5, event.eventTime, ACCURACY);
        assertEquals(0.125, result[0], ACCURACY);
    }

    @Test
    public void testResetStateEventContinuesWithResetValue() throws Exception {
        DormandPrince54Integrator integrator =
                new DormandPrince54Integrator(1.0e-8, 1.0, 1.0e-12, 1.0e-12);
        ResettingTimeEvent event = new ResettingTimeEvent(0.5, 2.0);
        integrator.addEventHandler(event, 0.1, 1.0e-12, 100);

        double[] result = new double[1];
        double stop = integrator.integrate(new CubicPolynomialEquation(),
                                           0.0, new double[] { 0.0 }, 1.0, result);

        assertEquals(1.0, stop, 0.0);
        assertEquals(0.5, event.eventTime, ACCURACY);
        assertTrue(event.resetCalled);
        assertEquals(2.875, result[0], ACCURACY);
    }

    private static class CubicPolynomialEquation implements FirstOrderDifferentialEquations {
        public int getDimension() {
            return 1;
        }

        public void computeDerivatives(double t, double[] y, double[] yDot)
            throws DerivativeException {
            yDot[0] = 3.0 * t * t;
        }
    }

    private static class StoppingTimeEvent implements EventHandler {
        private final double time;
        private double eventTime = Double.NaN;

        StoppingTimeEvent(double time) {
            this.time = time;
        }

        public double g(double t, double[] y) throws EventException {
            return t - time;
        }

        public int eventOccurred(double t, double[] y, boolean increasing)
            throws EventException {
            eventTime = t;
            return STOP;
        }

        public void resetState(double t, double[] y) throws EventException {
        }
    }

    private static class ResettingTimeEvent implements EventHandler {
        private final double time;
        private final double resetValue;
        private double eventTime = Double.NaN;
        private boolean resetCalled;

        ResettingTimeEvent(double time, double resetValue) {
            this.time = time;
            this.resetValue = resetValue;
        }

        public double g(double t, double[] y) throws EventException {
            return t - time;
        }

        public int eventOccurred(double t, double[] y, boolean increasing)
            throws EventException {
            eventTime = t;
            return RESET_STATE;
        }

        public void resetState(double t, double[] y) throws EventException {
            resetCalled = true;
            y[0] = resetValue;
        }
    }
}