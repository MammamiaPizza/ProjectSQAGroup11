package org.apache.commons.math.ode.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.junit.Test;

public class EventStateGeneratedTest {

    @Test
    public void detectsSecondOfTwoCloseRootsAfterFirstRootHasBeenAccepted()
        throws Exception {

        FunctionHandler handler = new FunctionHandler(new ValueFunction() {
            public double value(double t) {
                return (t - 1.0002) * (t - 1.0052);
            }
        }, EventHandler.CONTINUE);

        EventState state = new EventState(handler, 0.001, 1.0e-9, 100);
        state.reinitializeBegin(0.0, new double[] { 0.0 });

        assertTrue(state.evaluateStep(new LinearTimeInterpolator(0.0, 1.01, true)));
        assertEquals(1.0002, state.getEventTime(), 1.0e-7);

        state.stepAccepted(state.getEventTime(), new double[] { state.getEventTime() });
        assertFalse(state.reset(state.getEventTime(), new double[] { state.getEventTime() }));

        assertTrue(state.evaluateStep(
            new LinearTimeInterpolator(state.getEventTime(), 1.01, true)));
        assertEquals(1.0052, state.getEventTime(), 1.0e-7);
    }

    @Test
    public void findsEventWhenIntegratingBackward() throws Exception {
        FunctionHandler handler = new FunctionHandler(new ValueFunction() {
            public double value(double t) {
                return t - 2.0;
            }
        }, EventHandler.CONTINUE);

        EventState state = new EventState(handler, 0.25, 1.0e-10, 100);
        state.reinitializeBegin(3.0, new double[] { 3.0 });

        assertTrue(state.evaluateStep(new LinearTimeInterpolator(3.0, 0.0, false)));
        assertEquals(2.0, state.getEventTime(), 1.0e-8);

        state.stepAccepted(2.0, new double[] { 2.0 });
        assertEquals(1, handler.eventCount);
        assertTrue(handler.lastIncreasing);
    }

    @Test
    public void noSignChangeProducesNoPendingEvent() throws Exception {
        FunctionHandler handler = new FunctionHandler(new ValueFunction() {
            public double value(double t) {
                return 3.0 + t * t;
            }
        }, EventHandler.CONTINUE);

        EventState state = new EventState(handler, 0.5, 1.0e-8, 50);
        state.reinitializeBegin(0.0, new double[] { 0.0 });

        assertFalse(state.evaluateStep(new LinearTimeInterpolator(0.0, 5.0, true)));
        assertTrue(Double.isNaN(state.getEventTime()));
        assertFalse(state.stop());
        assertFalse(state.reset(5.0, new double[] { 5.0 }));
    }

    @Test
    public void stopActionIsReportedAfterAcceptedEvent() throws Exception {
        FunctionHandler handler = new FunctionHandler(new ValueFunction() {
            public double value(double t) {
                return t - 1.0;
            }
        }, EventHandler.STOP);

        EventState state = new EventState(handler, 0.2, 1.0e-10, 100);
        state.reinitializeBegin(0.0, new double[] { 0.0 });

        assertTrue(state.evaluateStep(new LinearTimeInterpolator(0.0, 2.0, true)));
        state.stepAccepted(state.getEventTime(), new double[] { state.getEventTime() });

        assertTrue(state.stop());
        assertFalse(state.reset(state.getEventTime(), new double[] { state.getEventTime() }));
    }

    @Test
    public void resetStateActionResetsStateAndClearsPendingEvent() throws Exception {
        FunctionHandler handler = new FunctionHandler(new ValueFunction() {
            public double value(double t) {
                return t - 1.0;
            }
        }, EventHandler.RESET_STATE);

        EventState state = new EventState(handler, 0.25, 1.0e-10, 100);
        state.reinitializeBegin(0.0, new double[] { 0.0 });

        assertTrue(state.evaluateStep(new LinearTimeInterpolator(0.0, 2.0, true)));
        double[] y = new double[] { 1.0 };
        state.stepAccepted(state.getEventTime(), y);

        assertTrue(state.reset(state.getEventTime(), y));
        assertEquals(-123.0, y[0], 0.0);
        assertEquals(1, handler.resetCount);
        assertTrue(Double.isNaN(state.getEventTime()));
        assertFalse(state.reset(1.0, y));
    }

    @Test
    public void constructorExposesConfiguredValuesAndNormalizesConvergence() {
        FunctionHandler handler = new FunctionHandler(new ValueFunction() {
            public double value(double t) {
                return t;
            }
        }, EventHandler.CONTINUE);

        EventState state = new EventState(handler, 4.5, -0.125, 17);

        assertSame(handler, state.getEventHandler());
        assertEquals(4.5, state.getMaxCheckInterval(), 0.0);
        assertEquals(0.125, state.getConvergence(), 0.0);
        assertEquals(17, state.getMaxIterationCount());
    }

    private interface ValueFunction {
        double value(double t);
    }

    private static class FunctionHandler implements EventHandler {
        private final ValueFunction function;
        private final int action;
        private int eventCount;
        private int resetCount;
        private boolean lastIncreasing;

        FunctionHandler(ValueFunction function, int action) {
            this.function = function;
            this.action = action;
        }

        public double g(double t, double[] y) {
            return function.value(t);
        }

        public int eventOccurred(double t, double[] y, boolean increasing) {
            eventCount++;
            lastIncreasing = increasing;
            return action;
        }

        public void resetState(double t, double[] y) {
            resetCount++;
            y[0] = -123.0;
        }
    }

    private static class LinearTimeInterpolator implements StepInterpolator {
        private final double previousTime;
        private final double currentTime;
        private final boolean forward;
        private double interpolatedTime;

        LinearTimeInterpolator(double previousTime, double currentTime, boolean forward) {
            this.previousTime = previousTime;
            this.currentTime = currentTime;
            this.forward = forward;
            this.interpolatedTime = previousTime;
        }

        public StepInterpolator copy() {
            LinearTimeInterpolator copy =
                new LinearTimeInterpolator(previousTime, currentTime, forward);
            copy.interpolatedTime = interpolatedTime;
            return copy;
        }

        public double getPreviousTime() {
            return previousTime;
        }

        public double getCurrentTime() {
            return currentTime;
        }

        public double getInterpolatedTime() {
            return interpolatedTime;
        }

        public void setInterpolatedTime(double time) {
            interpolatedTime = time;
        }

        public double[] getInterpolatedState() throws DerivativeException {
            return new double[] { interpolatedTime };
        }

        public double[] getInterpolatedDerivatives() throws DerivativeException {
            return new double[] { 1.0 };
        }

        public boolean isForward() {
            return forward;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeDouble(previousTime);
            out.writeDouble(currentTime);
            out.writeBoolean(forward);
            out.writeDouble(interpolatedTime);
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            throw new IOException("not supported");
        }
    }
}