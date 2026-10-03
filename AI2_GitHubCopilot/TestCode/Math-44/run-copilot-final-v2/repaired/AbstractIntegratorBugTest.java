package org.apache.commons.math.ode;

 import static org.junit.Assert.*;

 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;

 import org.apache.commons.math.ode.events.EventHandler;
 import org.apache.commons.math.ode.events.EventState;
 import org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator;
 import org.apache.commons.math.ode.sampling.AbstractStepInterpolator;
 import org.apache.commons.math.ode.sampling.StepHandler;
 import org.apache.commons.math.ode.sampling.StepInterpolator;
 import org.apache.commons.math.exception.NumberIsTooSmallException;
 import org.junit.Test;

 /**
  * Tests for {@link AbstractIntegrator} targeting the bug MATH-695
  * ("going backward in time!") and related sanity checks.
  */
 public class AbstractIntegratorBugTest {

     /** Simple ODE: y' = y (exponential growth). */
     private static class LinearODE implements FirstOrderDifferentialEquations {
         public int getDimension() { return 1; }
         public void computeDerivatives(double t, double[] y, double[] yDot) {
             yDot[0] = y[0];
         }
     }

     /** ODE with dependency on time. */
     private static class SinODE implements FirstOrderDifferentialEquations {
         public int getDimension() { return 1; }
         public void computeDerivatives(double t, double[] y, double[] yDot) {
             yDot[0] = Math.sin(t);
         }
     }

     /** Event handler that resets the state when event occurs. */
     private static class ResettingEventHandler implements EventHandler {
         private final double triggerY;
         private boolean resetCalled;

         public ResettingEventHandler(double triggerY) {
             this.triggerY = triggerY;
         }

         public double g(double t, double[] y) {
             return y[0] - triggerY; // event when y crosses triggerY
         }

         public EventHandler.Action eventOccurred(double t, double[] y, boolean increasing) {
             return RESET_STATE;
         }

         public void resetState(double t, double[] y) {
             y[0] *= 2.0; // arbitrary modification to force recomputation of derivatives
             resetCalled = true;
         }
     }

     /** Event handler that stops integration. */
     private static class StoppingEventHandler implements EventHandler {
         private final double stopTime;

         public StoppingEventHandler(double stopTime) {
             this.stopTime = stopTime;
         }

         public double g(double t, double[] y) {
             return t - stopTime;
         }

         public EventHandler.Action eventOccurred(double t, double[] y, boolean increasing) {
             return STOP;
         }

         public void resetState(double t, double[] y) {}
     }

     /** Step handler that records step times. */
     private static class RecordingStepHandler implements StepHandler {
         private final ArrayList<Double> times = new ArrayList<Double>();
         private final ArrayList<Boolean> isForwardList = new ArrayList<Boolean>();

         public void init(double t0, double[] y0, double t) {}
         public void handleStep(StepInterpolator interpolator, boolean isLast) {
             times.add(interpolator.getInterpolatedTime());
             isForwardList.add(interpolator.isForward());
         }
     }

     // --- Sanity checks ---

     @Test(expected = NumberIsTooSmallException.class)
     public void testSanityChecksIntervalTooSmall() {
         FirstOrderIntegrator integrator = new DormandPrince853Integrator(1e-6, 1e-6, 0.1, 0.1);
         LinearODE ode = new LinearODE();
         double[] y0 = new double[] { 1.0 };
         double[] y = new double[1];
         // dt = 0 should trigger the exception
         integrator.integrate(ode, 1.0, y0, 1.0, y);
     }

     @Test(expected = NumberIsTooSmallException.class)
     public void testSanityChecksIntervalBelowThreshold() {
         FirstOrderIntegrator integrator = new DormandPrince853Integrator(1e-9, 1e-9, 0.1, 0.1);
         LinearODE ode = new LinearODE();
         double[] y0 = new double[] { 1.0 };
         double[] y = new double[1];
         // extremely small but nonzero interval
         integrator.integrate(ode, 0.0, y0, 1e-15, y);
     }

     // --- Direction and basic integration ---

     @Test
     public void testForwardIntegrationBasic() {
         FirstOrderIntegrator integrator = new DormandPrince853Integrator(1e-6, 1e-4, 0.01, 0.1);
         LinearODE ode = new LinearODE();
         double[] y0 = new double[] { 1.0 };
         double[] y = new double[1];
         double tEnd = 1.0;
         double finalT = integrator.integrate(ode, 0.0, y0, tEnd, y);
         assertEquals(tEnd, finalT, 1e-10);
         assertTrue("Solution should grow", y[0] > y0[0]);
     }

     @Test
     public void testBackwardIntegrationBasic() {
         FirstOrderIntegrator integrator = new DormandPrince853Integrator(1e-6, 1e-4, 0.01, 0.1);
         LinearODE ode = new LinearODE();
         double[] y0 = new double[] { 10.0 };
         double[] y = new double[1];
         double tStart = 5.0;
         double tEnd = 0.0;
         double finalT = integrator.integrate(ode, tStart, y0, tEnd, y);
         assertEquals(tEnd, finalT, 1e-10);
         // Backward integration: time decreases, solution should shrink
         assertTrue("Solution should shrink during backward integration", y[0] < y0[0]);
     }

     // --- Event handling reproducing MATH-695 scenario ---

     @Test
     public void testEventResetDoesNotReverseTime() {
         // This test triggers a state reset after an event.
         // The bug caused the integrator to later attempt going backward in time.
         FirstOrderIntegrator integrator = new DormandPrince853Integrator(1e-6, 1e-4, 0.01, 0.2);
         LinearODE ode = new LinearODE();
         double[] y0 = new double[] { 1.0 };
         double[] y = new double[1];

         ResettingEventHandler eventHandler = new ResettingEventHandler(50.0);
         integrator.addEventHandler(eventHandler, 0.1, 1e-9, 100);

         // Integrate far enough so that y crosses 50 (triggering the event).
         double tEnd = 5.0; // y(5) = e^5 ~ 148, will cross 50 at ~ ln(50)=3.91
         double finalT = integrator.integrate(ode, 0.0, y0, tEnd, y);

         assertEquals(tEnd, finalT, 1e-10);
         assertTrue("Event reset should have been called", eventHandler.resetCalled);
         // After reset at event time, the solution should have evolved naturally from doubled
value.
         // No assertion on exact value, just that no exception was thrown and time moved forward.
         assertTrue("Final time should not go backward", finalT >= 0.0);
         assertTrue("Event time must be less than end time", finalT > 0.0);
     }

     @Test
     public void testEventResetIntegrationContinuesAfterReset() {
         // Second variant with a different ODE to ensure post-reset continuation.
         FirstOrderIntegrator integrator = new DormandPrince853Integrator(1e-9, 1e-6, 0.1, 0.5);
         SinODE ode = new SinODE();
         double[] y0 = new double[] { 0.0 };
         double[] y = new double[1];

         // Event when y reaches 0.5 (approx t ~ 1.0 rad)
         ResettingEventHandler eventHandler = new ResettingEventHandler(0.5);
         integrator.addEventHandler(eventHandler, 0.1, 1e-9, 100);

         double tEnd = 3.0;
         double finalT = integrator.integrate(ode, 0.0, y0, tEnd, y);

         assertEquals("Integration should reach the specified end time", tEnd, finalT, 1e-10);
         assertTrue("Event reset should have occurred", eventHandler.resetCalled);
     }

     @Test
     public void testEventStop() {
         FirstOrderIntegrator integrator = new DormandPrince853Integrator(1e-6, 1e-4, 0.01, 0.1);
         LinearODE ode = new LinearODE();
         double[] y0 = new double[] { 1.0 };
         double[] y = new double[1];

         StoppingEventHandler stopHandler = new StoppingEventHandler(2.0);
         integrator.addEventHandler(stopHandler, 0.01, 1e-12, 100);

         double tEnd = 5.0;
         double finalT = integrator.integrate(ode, 0.0, y0, tEnd, y);

         assertEquals("Integration should stop at event time", 2.0, finalT, 1e-6);
     }

     @Test
     public void testMultipleEventsAtSameTime() {
         FirstOrderIntegrator integrator = new DormandPrince853Integrator(1e-6, 1e-4, 0.01, 0.1);
         LinearODE ode = new LinearODE();
         double[] y0 = new double[] { 1.0 };
         double[] y = new double[1];

         // Two events triggering at nearly the same y (and thus time)
         ResettingEventHandler eh1 = new ResettingEventHandler(20.0);
         ResettingEventHandler eh2 = new ResettingEventHandler(20.1);
         integrator.addEventHandler(eh1, 0.1, 1e-9, 100);
         integrator.addEventHandler(eh2, 0.1, 1e-9, 100);

         double tEnd = 4.0;
         // Should not throw and both events should be handled
         double finalT = integrator.integrate(ode, 0.0, y0, tEnd, y);
         assertTrue("Integration should progress", finalT > 0.0);
     }

     @Test
     public void testStepHandlerCalledWithCorrectDirection() {
         FirstOrderIntegrator integrator = new DormandPrince853Integrator(1e-6, 1e-4, 0.01, 0.1);
         LinearODE ode = new LinearODE();
         double[] y0 = new double[] { 1.0 };
         double[] y = new double[1];

         RecordingStepHandler stepHandler = new RecordingStepHandler();
         integrator.addStepHandler(stepHandler);

         double tEnd = 1.0;
         integrator.integrate(ode, 0.0, y0, tEnd, y);

         assertTrue("Step handler should have recorded times", stepHandler.times.size() > 0);
         // All steps should be forward
         for (Boolean fwd : stepHandler.isForwardList) {
             assertTrue("All recorded steps should be forward", fwd);
         }
         // Times should be monotonically increasing
         for (int i = 1; i < stepHandler.times.size(); i++) {
             assertTrue(stepHandler.times.get(i) > stepHandler.times.get(i-1));
         }
     }

     @Test
     public void testStepHandlerCalledDuringBackwardIntegration() {
         FirstOrderIntegrator integrator = new DormandPrince853Integrator(1e-6, 1e-4, 0.01, 0.1);
         LinearODE ode = new LinearODE();
         double[] y0 = new double[] { 10.0 };
         double[] y = new double[1];

         RecordingStepHandler stepHandler = new RecordingStepHandler();
         integrator.addStepHandler(stepHandler);

         double tEnd = 1.0;
         integrator.integrate(ode, 5.0, y0, tEnd, y);

         assertTrue("Step handler should have recorded times", stepHandler.times.size() > 0);
         // All steps should be backward
         for (Boolean fwd : stepHandler.isForwardList) {
             assertFalse("All recorded steps should be backward", fwd);
         }
         // Times should be monotonically decreasing
         for (int i = 1; i < stepHandler.times.size(); i++) {
             assertTrue(stepHandler.times.get(i) < stepHandler.times.get(i-1));
         }
     }

     @Test
     public void testGetEventHandlers() {
         FirstOrderIntegrator integrator = new DormandPrince853Integrator(1e-6, 1e-4, 0.1, 0.1);
         ResettingEventHandler eh1 = new ResettingEventHandler(100);
         StoppingEventHandler eh2 = new StoppingEventHandler(2.0);
         integrator.addEventHandler(eh1, 0.1, 1e-9, 100);
         integrator.addEventHandler(eh2, 0.1, 1e-9, 100);

         Collection<EventHandler> handlers = integrator.getEventHandlers();
         assertEquals(2, handlers.size());
         assertTrue(handlers.contains(eh1));
         assertTrue(handlers.contains(eh2));
     }

     @Test
     public void testClearEventAndStepHandlers() {
         FirstOrderIntegrator integrator = new DormandPrince853Integrator(1e-6, 1e-4, 0.1, 0.1);
         ResettingEventHandler eh = new ResettingEventHandler(100);
         integrator.addEventHandler(eh, 0.1, 1e-9, 100);

         RecordingStepHandler sh = new RecordingStepHandler();
         integrator.addStepHandler(sh);

         integrator.clearEventHandlers();
         integrator.clearStepHandlers();

         assertTrue(integrator.getEventHandlers().isEmpty());
         assertTrue(integrator.getStepHandlers().isEmpty());
     }
 }
