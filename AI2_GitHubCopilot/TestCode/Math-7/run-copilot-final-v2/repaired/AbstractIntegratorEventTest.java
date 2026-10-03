package org.apache.commons.math3.ode;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;

 import java.util.ArrayList;
 import java.util.List;

 import org.apache.commons.math3.ode.events.EventHandler;
 import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
 import org.junit.Test;

 /**
  * Tests for event handling in AbstractIntegrator, focusing on the bug
  * where a non-transitive comparator in the TreeSet could drop events
  * that occur at identical times.
  */
 public class AbstractIntegratorEventTest {

     /** Simple linear ODE for testing: dy/dt = 1, solution y = t. */
     private static class LinearODE implements FirstOrderDifferentialEquations {
         public int getDimension() { return 1; }
         public void computeDerivatives(double t, double[] y, double[] yDot) {
             yDot[0] = 1.0;
         }
     }

     /** An event handler that triggers at a specific time and records the trigger. */
     private static class RecordingEventHandler implements EventHandler {
         private final double triggerTime;
         private final List<Double> eventTimes;
         private final Action action;

         RecordingEventHandler(double triggerTime, List<Double> eventTimes, Action action) {
             this.triggerTime = triggerTime;
             this.eventTimes = eventTimes;
             this.action = action;
         }

         public void init(double t0, double[] y0, double t) {}

         public double g(double t, double[] y) {
             // Root when t == triggerTime
             return t - triggerTime;
         }

         public Action eventOccurred(double t, double[] y, boolean increasing) {
             eventTimes.add(t);
             return action;
         }

         public void resetState(double t, double[] y) {}
     }

     // --- Tests ---------------------------------------------------------------

     @Test
     public void testNoEvents() {
         DormandPrince853Integrator integrator = new DormandPrince853Integrator(1e-3, 1.0, 1e-6,
1e-6);
         List<Double> eventTimes = new ArrayList<Double>();
         // no event handlers added
         double[] y = new double[] { 0.0 };
         double t = integrator.integrate(new LinearODE(), 0.0, y, 10.0, y);
         assertEquals(10.0, t, 1e-6);
         assertTrue("No events expected", eventTimes.isEmpty());
     }

     @Test
     public void testSingleEvent() {
         DormandPrince853Integrator integrator = new DormandPrince853Integrator(1e-3, 1.0, 1e-6,
1e-6);
         List<Double> eventTimes = new ArrayList<Double>();
         integrator.addEventHandler(new RecordingEventHandler(5.0, eventTimes,
EventHandler.Action.CONTINUE),
                                   1e-6, 1e-10, 100);
         double[] y = new double[] { 0.0 };
         integrator.integrate(new LinearODE(), 0.0, y, 10.0, y);
         assertEquals("Single event should be triggered", 1, eventTimes.size());
         assertEquals(5.0, eventTimes.get(0), 1e-3);
     }

     @Test
     public void testTwoEventsDistinctTimes() {
         DormandPrince853Integrator integrator = new DormandPrince853Integrator(1e-3, 1.0, 1e-6,
1e-6);
         List<Double> eventTimes = new ArrayList<Double>();
         integrator.addEventHandler(new RecordingEventHandler(3.0, eventTimes,
EventHandler.Action.CONTINUE),
                                   1e-6, 1e-10, 100);
         integrator.addEventHandler(new RecordingEventHandler(7.0, eventTimes,
EventHandler.Action.CONTINUE),
                                   1e-6, 1e-10, 100);
         double[] y = new double[] { 0.0 };
         integrator.integrate(new LinearODE(), 0.0, y, 10.0, y);
         assertEquals("Two events expected", 2, eventTimes.size());
         // Verify chronological order
         assertTrue("First event at t=3", eventTimes.get(0) <= eventTimes.get(1));
         assertEquals(3.0, eventTimes.get(0), 1e-3);
         assertEquals(7.0, eventTimes.get(1), 1e-3);
     }

     @Test
     public void testTwoEventsSameTime() {
         // This directly tests the bug: TreeSet comparator returning 0 for different objects
         DormandPrince853Integrator integrator = new DormandPrince853Integrator(1e-3, 1.0, 1e-6,
1e-6);
         List<Double> eventTimes = new ArrayList<Double>();
         // Two handlers that both trigger at t=5.0
         integrator.addEventHandler(new RecordingEventHandler(5.0, eventTimes,
EventHandler.Action.CONTINUE),
                                   1e-6, 1e-10, 100);
         integrator.addEventHandler(new RecordingEventHandler(5.0, eventTimes,
EventHandler.Action.CONTINUE),
                                   1e-6, 1e-10, 100);
         double[] y = new double[] { 0.0 };
         integrator.integrate(new LinearODE(), 0.0, y, 10.0, y);
         assertEquals("Two events at same time should both fire", 2, eventTimes.size());
     }

     @Test
     public void testEventAtIntegrationStart() {
         DormandPrince853Integrator integrator = new DormandPrince853Integrator(1e-3, 1.0, 1e-6,
1e-6);
         List<Double> eventTimes = new ArrayList<Double>();
         integrator.addEventHandler(new RecordingEventHandler(0.0, eventTimes,
EventHandler.Action.CONTINUE),
                                   1e-6, 1e-10, 100);
         double[] y = new double[] { 0.0 };
         integrator.integrate(new LinearODE(), 0.0, y, 10.0, y);
         assertEquals("Event at start should trigger", 1, eventTimes.size());
         assertEquals(0.0, eventTimes.get(0), 1e-3);
     }

     @Test
     public void testEventAtIntegrationEnd() {
         DormandPrince853Integrator integrator = new DormandPrince853Integrator(1e-3, 1.0, 1e-6,
1e-6);
         List<Double> eventTimes = new ArrayList<Double>();
         integrator.addEventHandler(new RecordingEventHandler(10.0, eventTimes,
EventHandler.Action.CONTINUE),
                                   1e-6, 1e-10, 100);
         double[] y = new double[] { 0.0 };
         integrator.integrate(new LinearODE(), 0.0, y, 10.0, y);
         assertEquals("Event at end should trigger", 1, eventTimes.size());
         assertEquals(10.0, eventTimes.get(0), 1e-3);
     }

     @Test
     public void testManyEventsMonotonicOrder() {
         DormandPrince853Integrator integrator = new DormandPrince853Integrator(1e-3, 1.0, 1e-6,
1e-6);
         List<Double> eventTimes = new ArrayList<Double>();
         int[] n = {0};
         StepHandler stepRecorder = new StepHandler() {
             public void init(double t0, double[] y0, double t) {}
             public void handleStep(StepInterpolator interpolator, boolean isLast) {
                 // record only at step end to avoid double counting; but we rely on event times
list
             }
         };
         integrator.addStepHandler(stepRecorder);
         // Add many events at 0.1, 0.2, ..., 0.9
         for (double trigger = 0.1; trigger <= 0.9; trigger += 0.1) {
             integrator.addEventHandler(new RecordingEventHandler(trigger, eventTimes,
EventHandler.Action.CONTINUE),
                                       1e-6, 1e-10, 100);
         }
         double[] y = new double[] { 0.0 };
         integrator.integrate(new LinearODE(), 0.0, y, 1.0, y);
         assertEquals("All 9 events should fire", 9, eventTimes.size());
         double previous = Double.NEGATIVE_INFINITY;
         for (double t : eventTimes) {
             assertTrue("Event times must be monotonic increasing", t >= previous);
             previous = t;
         }
     }

     @Test
     public void testEventStopAction() {
         DormandPrince853Integrator integrator = new DormandPrince853Integrator(1e-3, 1.0, 1e-6,
1e-6);
         List<Double> eventTimes = new ArrayList<Double>();
         // Event at t=5 stops integration
         integrator.addEventHandler(new RecordingEventHandler(5.0, eventTimes,
EventHandler.Action.STOP),
                                   1e-6, 1e-10, 100);
         // Another event after stop point should not be reached
         integrator.addEventHandler(new RecordingEventHandler(7.0, eventTimes,
EventHandler.Action.CONTINUE),
                                   1e-6, 1e-10, 100);
         double[] y = new double[] { 0.0 };
         double tEnd = integrator.integrate(new LinearODE(), 0.0, y, 10.0, y);
         assertTrue("Integration stopped before or at stop event time", tEnd <= 5.0 + 1e-3);
         assertEquals("Only stop event should fire", 1, eventTimes.size());
         assertEquals(5.0, eventTimes.get(0), 1e-3);
     }

     @Test
     public void testEventResetAction() {
         DormandPrince853Integrator integrator = new DormandPrince853Integrator(1e-3, 1.0, 1e-6,
1e-6);
         List<Double> eventTimes = new ArrayList<Double>();
         // Event at t=5 resets state (just record, no actual state change needed)
         EventHandler resetHandler = new EventHandler() {
             public void init(double t0, double[] y0, double t) {}
             public double g(double t, double[] y) { return t - 5.0; }
             public Action eventOccurred(double t, double[] y, boolean increasing) {
                 eventTimes.add(t);
                 return Action.RESET_STATE;
             }
             public void resetState(double t, double[] y) {
                 // do nothing, just exercise the path
             }
         };
         integrator.addEventHandler(resetHandler, 1e-6, 1e-10, 100);
         // Another event at t=7 should still fire after reset
         integrator.addEventHandler(new RecordingEventHandler(7.0, eventTimes,
EventHandler.Action.CONTINUE),
                                   1e-6, 1e-10, 100);
         double[] y = new double[] { 0.0 };
         integrator.integrate(new LinearODE(), 0.0, y, 10.0, y);
         assertEquals("Both events should fire even after reset", 2, eventTimes.size());
         assertEquals(5.0, eventTimes.get(0), 1e-3);
         assertEquals(7.0, eventTimes.get(1), 1e-3);
     }

     @Test
     public void testComparatorConsistencyManyIdenticalTimes() {
         // Stress-test: many event handlers with identical g roots.
         // The TreeSet comparator must not drop distinct objects even if compare returns 0.
         DormandPrince853Integrator integrator = new DormandPrince853Integrator(1e-3, 1.0, 1e-6,
1e-6);
         List<Double> eventTimes = new ArrayList<Double>();
         int handlerCount = 5;
         for (int i = 0; i < handlerCount; i++) {
             integrator.addEventHandler(new RecordingEventHandler(5.0, eventTimes,
EventHandler.Action.CONTINUE),
                                       1e-6, 1e-10, 100);
         }
         double[] y = new double[] { 0.0 };
         integrator.integrate(new LinearODE(), 0.0, y, 10.0, y);
         assertEquals("All handlers at same time must fire", handlerCount, eventTimes.size());
     }

     @Test
     public void testStepHandlerCalledAtEventsAndFinal() {
         DormandPrince853Integrator integrator = new DormandPrince853Integrator(1e-3, 1.0, 1e-6,
1e-6);
         List<Double> stepTimes = new ArrayList<Double>();
         StepHandler stepRecorder = new StepHandler() {
             public void init(double t0, double[] y0, double t) {}
             public void handleStep(StepInterpolator interpolator, boolean isLast) {
                 stepTimes.add(interpolator.getGlobalCurrentTime());
             }
         };
         integrator.addStepHandler(stepRecorder);
         // Add an event at t=5.0 that will split the step
         integrator.addEventHandler(new RecordingEventHandler(5.0, new ArrayList<Double>(),
EventHandler.Action.CONTINUE),
                                   1e-6, 1e-10, 100);
         double[] y = new double[] { 0.0 };
         integrator.integrate(new LinearODE(), 0.0, y, 10.0, y);
         // At least 2 step handlings: one at event time, one at final time
         assertTrue("Step handler called at least at event and final time", stepTimes.size() >= 2);
         // Check that event time is present
         boolean foundEvent = false;
         for (double t : stepTimes) {
             if (Math.abs(t - 5.0) < 1e-3) {
                 foundEvent = true;
                 break;
             }
         }
         assertTrue("Event time should appear in step handler calls", foundEvent);
     }
 }
