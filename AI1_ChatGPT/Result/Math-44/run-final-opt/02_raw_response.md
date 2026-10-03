package org.apache.commons.math.ode;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.ode.events.EventHandler;
import org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AbstractIntegratorEventOrderingTest {

    @Test
    public void testBackwardIntegrationProcessesEventsInDecreasingTimeOrder() {
        ClassicalRungeKuttaIntegrator integrator = new ClassicalRungeKuttaIntegrator(20.0);
        List<Double> eventTimes = new ArrayList<Double>();
        List<Double> stepEndTimes = new ArrayList<Double>();

        integrator.addEventHandler(new TimeEvent(10.99, eventTimes),
                                   20.0, 1.0e-12, 100);
        integrator.addEventHandler(new TimeEvent(7.796578226186635, eventTimes),
                                   20.0, 1.0e-12, 100);
        integrator.addStepHandler(new RecordingStepHandler(stepEndTimes));

        double[] result = new double[1];
        double finalTime = integrator.integrate(new UnitRateEquation(),
                                                12.0, new double[] { 12.0 },
                                                0.0, result);

        assertEquals(0.0, finalTime, 0.0);
        assertEquals(0.0, result[0], 1.0e-12);
        assertEquals(2, eventTimes.size());
        assertEquals(10.99, eventTimes.get(0), 1.0e-9);
        assertEquals(7.796578226186635, eventTimes.get(1), 1.0e-9);

        for (int i = 1; i < stepEndTimes.size(); ++i) {
            assertFalse("step handling must not move forward during backward integration",
                        stepEndTimes.get(i) > stepEndTimes.get(i - 1));
        }
    }

    @Test
    public void testForwardIntegrationProcessesEventsInIncreasingTimeOrder() {
        ClassicalRungeKuttaIntegrator integrator = new ClassicalRungeKuttaIntegrator(20.0);
        List<Double> eventTimes = new ArrayList<Double>();
        List<Double> stepEndTimes = new ArrayList<Double>();

        integrator.addEventHandler(new TimeEvent(2.0, eventTimes),
                                   20.0, 1.0e-12, 100);
        integrator.addEventHandler(new TimeEvent(5.0, eventTimes),
                                   20.0, 1.0e-12, 100);
        integrator.addStepHandler(new RecordingStepHandler(stepEndTimes));

        double[] result = new double[1];
        double finalTime = integrator.integrate(new UnitRateEquation(),
                                                0.0, new double[] { 0.0 },
                                                12.0, result);

        assertEquals(12.0, finalTime, 0.0);
        assertEquals(12.0, result[0], 1.0e-12);
        assertEquals(2, eventTimes.size());
        assertEquals(2.0, eventTimes.get(0), 1.0e-9);
        assertEquals(5.0, eventTimes.get(1), 1.0e-9);

        for (int i = 1; i < stepEndTimes.size(); ++i) {
            assertFalse("step handling must not move backward during forward integration",
                        stepEndTimes.get(i) < stepEndTimes.get(i - 1));
        }
    }

    private static class UnitRateEquation implements FirstOrderDifferentialEquations {
        public int getDimension() {
            return 1;
        }

        public void computeDerivatives(double t, double[] y, double[] yDot) {
            yDot[0] = 1.0;
        }
    }

    private static class TimeEvent implements EventHandler {
        private final double eventTime;
        private final List<Double> occurrences;

        TimeEvent(double eventTime, List<Double> occurrences) {
            this.eventTime = eventTime;
            this.occurrences = occurrences;
        }

        public double g(double t, double[] y) {
            return t - eventTime;
        }

        public Action eventOccurred(double t, double[] y, boolean increasing) {
            occurrences.add(t);
            return Action.CONTINUE;
        }

        public void resetState(double t, double[] y) {
        }
    }

    private static class RecordingStepHandler implements StepHandler {
        private final List<Double> stepEndTimes;

        RecordingStepHandler(List<Double> stepEndTimes) {
            this.stepEndTimes = stepEndTimes;
        }

        public void init(double t0, double[] y0, double t) {
        }

        public void handleStep(StepInterpolator interpolator, boolean isLast) {
            stepEndTimes.add(interpolator.getCurrentTime());
        }
    }
}