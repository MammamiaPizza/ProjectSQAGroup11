package org.apache.commons.math3.ode;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.ode.events.EventHandler;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AbstractIntegratorTest {

    private static final double EPS = 1.0e-10;

    @Test
    public void testForwardEventsAreReportedInChronologicalOrder() throws Exception {
        final List<Double> events = new ArrayList<Double>();
        final DormandPrince853Integrator integrator = newIntegrator();

        integrator.addEventHandler(new TimeEvent(0.5, EventHandler.Action.CONTINUE, events),
                                   0.1, 1.0e-12, 100);
        integrator.addEventHandler(new TimeEvent(1.0, EventHandler.Action.CONTINUE, events),
                                   0.1, 1.0e-12, 100);
        integrator.addEventHandler(new TimeEvent(1.5, EventHandler.Action.CONTINUE, events),
                                   0.1, 1.0e-12, 100);

        final double[] result = new double[1];
        final double end = integrator.integrate(new UnitRateEquation(), 0.0,
                                                new double[] { 0.0 }, 2.0, result);

        assertEquals(2.0, end, EPS);
        assertEquals(2.0, result[0], EPS);
        assertEquals(3, events.size());
        assertEquals(0.5, events.get(0), EPS);
        assertEquals(1.0, events.get(1), EPS);
        assertEquals(1.5, events.get(2), EPS);
    }

    @Test
    public void testCoincidentEventsAreBothReported() throws Exception {
        final DormandPrince853Integrator integrator = newIntegrator();
        final TimeEvent first = new TimeEvent(1.0, EventHandler.Action.CONTINUE, null);
        final TimeEvent second = new TimeEvent(1.0, EventHandler.Action.CONTINUE, null);

        integrator.addEventHandler(first, 0.1, 1.0e-12, 100);
        integrator.addEventHandler(second, 0.1, 1.0e-12, 100);

        final double[] result = new double[1];
        final double end = integrator.integrate(new UnitRateEquation(), 0.0,
                                                new double[] { 0.0 }, 2.0, result);

        assertEquals(2.0, end, EPS);
        assertEquals(2.0, result[0], EPS);
        assertEquals(1, first.occurrences);
        assertEquals(1, second.occurrences);
        assertEquals(1.0, first.lastEventTime, EPS);
        assertEquals(1.0, second.lastEventTime, EPS);
    }

    @Test
    public void testBackwardEventsAreReportedInReverseChronologicalOrder() throws Exception {
        final List<Double> events = new ArrayList<Double>();
        final DormandPrince853Integrator integrator = newIntegrator();

        integrator.addEventHandler(new TimeEvent(0.5, EventHandler.Action.CONTINUE, events),
                                   0.1, 1.0e-12, 100);
        integrator.addEventHandler(new TimeEvent(1.0, EventHandler.Action.CONTINUE, events),
                                   0.1, 1.0e-12, 100);
        integrator.addEventHandler(new TimeEvent(1.5, EventHandler.Action.CONTINUE, events),
                                   0.1, 1.0e-12, 100);

        final double[] result = new double[1];
        final double end = integrator.integrate(new UnitRateEquation(), 2.0,
                                                new double[] { 2.0 }, 0.0, result);

        assertEquals(0.0, end, EPS);
        assertEquals(0.0, result[0], EPS);
        assertEquals(3, events.size());
        assertEquals(1.5, events.get(0), EPS);
        assertEquals(1.0, events.get(1), EPS);
        assertEquals(0.5, events.get(2), EPS);
    }

    @Test
    public void testResetEventChangesStateAndLaterEventsStillOccur() throws Exception {
        final List<Double> events = new ArrayList<Double>();
        final DormandPrince853Integrator integrator = newIntegrator();
        final ResettingTimeEvent reset = new ResettingTimeEvent(1.0, events);
        final TimeEvent later = new TimeEvent(1.5, EventHandler.Action.CONTINUE, events);

        integrator.addEventHandler(reset, 0.1, 1.0e-12, 100);
        integrator.addEventHandler(later, 0.1, 1.0e-12, 100);

        final double[] result = new double[1];
        final double end = integrator.integrate(new UnitRateEquation(), 0.0,
                                                new double[] { 0.0 }, 2.0, result);

        assertEquals(2.0, end, EPS);
        assertEquals(11.0, result[0], EPS);
        assertEquals(1, reset.occurrences);
        assertEquals(1, reset.resetCalls);
        assertEquals(1, later.occurrences);
        assertEquals(2, events.size());
        assertEquals(1.0, events.get(0), EPS);
        assertEquals(1.5, events.get(1), EPS);
    }

    @Test
    public void testStopEventEndsIntegrationAndMarksLastStep() throws Exception {
        final DormandPrince853Integrator integrator = newIntegrator();
        final TimeEvent stop = new TimeEvent(1.0, EventHandler.Action.STOP, null);
        final LastStepRecorder steps = new LastStepRecorder();

        integrator.addEventHandler(stop, 0.1, 1.0e-12, 100);
        integrator.addStepHandler(steps);

        final double[] result = new double[1];
        final double end = integrator.integrate(new UnitRateEquation(), 0.0,
                                                new double[] { 0.0 }, 2.0, result);

        assertEquals(1.0, end, EPS);
        assertEquals(1.0, result[0], EPS);
        assertEquals(1, stop.occurrences);
        assertEquals(1, steps.lastStepCalls);
        assertEquals(1.0, steps.lastStepTime, EPS);
        assertTrue(steps.stepCalls >= 1);
    }

    private static DormandPrince853Integrator newIntegrator() {
        return new DormandPrince853Integrator(1.0e-8, 5.0, 1.0e-12, 1.0e-12);
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
        private final Action action;
        private final List<Double> recordedEvents;
        private int occurrences;
        private double lastEventTime = Double.NaN;

        TimeEvent(double eventTime, Action action, List<Double> recordedEvents) {
            this.eventTime = eventTime;
            this.action = action;
            this.recordedEvents = recordedEvents;
        }

        public void init(double t0, double[] y0, double t) {
        }

        public double g(double t, double[] y) {
            return t - eventTime;
        }

        public Action eventOccurred(double t, double[] y, boolean increasing) {
            ++occurrences;
            lastEventTime = t;
            if (recordedEvents != null) {
                recordedEvents.add(t);
            }
            return action;
        }

        public void resetState(double t, double[] y) {
        }
    }

    private static class ResettingTimeEvent extends TimeEvent {
        private int resetCalls;

        ResettingTimeEvent(double eventTime, List<Double> recordedEvents) {
            super(eventTime, Action.RESET_STATE, recordedEvents);
        }

        @Override
        public void resetState(double t, double[] y) {
            ++resetCalls;
            y[0] = 10.0;
        }
    }

    private static class LastStepRecorder implements StepHandler {
        private int stepCalls;
        private int lastStepCalls;
        private double lastStepTime = Double.NaN;

        public void init(double t0, double[] y0, double t) {
        }

        public void handleStep(StepInterpolator interpolator, boolean isLast) {
            ++stepCalls;
            if (isLast) {
                ++lastStepCalls;
                lastStepTime = interpolator.getCurrentTime();
            }
        }
    }
}