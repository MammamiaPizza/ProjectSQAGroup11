import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.events.EventHandler;
import org.apache.commons.math.ode.events.EventException;
import org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;

/**

 - Tests that expose the missed-end-event bug in RungeKutta-type integrators.
 - The bug causes the integrator to overshoot the event time because the
 - step size used when accepting the step after an event is not correctly
 - truncated to <code>eventTime - stepStart</code>.
  */
 public class MissedEndEventBugTest {
  /** Absolute tolerance for time comparisons.
  */
  private static final double TIME_TOLERANCE = 1e-9;
  /* --- helper ODE: derivatives are zero, so state never changes --- /
  private static final FirstOrderDifferentialEquations CONSTANT_ODE =
      new FirstOrderDifferentialEquations() {
          public int getDimension() { return 1; }
          public void computeDerivatives(double t, double[] y, double[] yDot) {
              yDot[0] = 0;
          }
      };
  / --- Fixed-step (RungeKuttaIntegrator) tests ---
  */
  /**
  - End event at a time that is not a multiple of the fixed step.
  - The integrator must stop exactly at the event time.
    */
   @Test
   public void testFixedStepEventBetweenSteps() throws Exception {
   ClassicalRungeKuttaIntegrator integrator =
   new ClassicalRungeKuttaIntegrator(1.0);
   double t0 = 0.0, tend = 10.0;
   double eventTime = 2.3; // not a step boundary
   double[] y = new double[] { 1.0 };
   EndTimeEventHandler handler = new EndTimeEventHandler(eventTime);
   integrator.addEventHandler(handler, 1e-12, 1e-9, 100);
   double tFinal = integrator.integrate(CONSTANT_ODE, t0, y, tend, new double[1]);
   assertEquals("fixed-step integrator missed end event",
            eventTime, tFinal, TIME_TOLERANCE);
   assertTrue("event was not triggered", handler.wasTriggered());
  }
  /**
  - Event exactly at the step start should be handled without overshoot
  - and without dividing by zero in the dt calculation.
    */
   @Test
   public void testFixedStepEventAtStepStart() throws Exception {
   ClassicalRungeKuttaIntegrator integrator =
   new ClassicalRungeKuttaIntegrator(1.0);
   double t0 = 0.0, tend = 10.0;
   double eventTime = 3.0; // exact step boundary
   double[] y = new double[] { 1.0 };
   EndTimeEventHandler handler = new EndTimeEventHandler(eventTime);
   integrator.addEventHandler(handler, 1e-12, 1e-9, 100);
   double tFinal = integrator.integrate(CONSTANT_ODE, t0, y, tend, new double[1]);
   assertEquals(eventTime, tFinal, TIME_TOLERANCE);
   assertTrue(handler.wasTriggered());
  }
  /**
  - Event extremely close to step start (within machine epsilon) must be
  - handled gracefully (the buggy code may fail on <code>dt ~ 0</code>).
    */
   @Test
   public void testFixedStepEventNearStepStart() throws Exception {
   ClassicalRungeKuttaIntegrator integrator =
   new ClassicalRungeKuttaIntegrator(1.0);
   double t0 = 0.0, tend = 10.0;
   double eventTime = 3.0 + 1e-14; // infinitesimally after a step boundary
   double[] y = new double[] { 1.0 };
   EndTimeEventHandler handler = new EndTimeEventHandler(eventTime);
   integrator.addEventHandler(handler, 1e-12, 1e-9, 100);
   double tFinal = integrator.integrate(CONSTANT_ODE, t0, y, tend, new double[1]);
   // The event time may be reached or the next step may be taken;
   // the important thing is that the integration does not overshoot
   // far beyond the event time, and does not throw.
   assertTrue("event time not reached reasonably",
          Math.abs(tFinal - eventTime) < 1.0);
  }
  /**
  - Event at the final integration endpoint; the integrator must stop
  - exactly at the endpoint.
    */
   @Test
   public void testFixedStepEventAtFinalTime() throws Exception {
   ClassicalRungeKuttaIntegrator integrator =
   new ClassicalRungeKuttaIntegrator(1.0);
   double t0 = 0.0, tend = 10.0;
   double eventTime = tend;
   double[] y = new double[] { 1.0 };
   EndTimeEventHandler handler = new EndTimeEventHandler(eventTime);
   integrator.addEventHandler(handler, 1e-12, 1e-9, 100);
   double tFinal = integrator.integrate(CONSTANT_ODE, t0, y, tend, new double[1]);
   assertEquals(eventTime, tFinal, TIME_TOLERANCE);
   assertTrue(handler.wasTriggered());
  }
  /* --- Variable-step (EmbeddedRungeKuttaIntegrator) tests ---
  */
  /**
  - Variable-step integrator: end event between steps.
    */
   @Test
   public void testVariableStepEventBetweenSteps() throws Exception {
   DormandPrince853Integrator integrator =
   new DormandPrince853Integrator(1e-3, 10.0, 1e-6, 1e-12);
   double t0 = 0.0, tend = 10.0;
   double eventTime = 4.7;
   double[] y = new double[] { 1.0 };
   EndTimeEventHandler handler = new EndTimeEventHandler(eventTime);
   integrator.addEventHandler(handler, 1e-12, 1e-9, 100);
   double tFinal = integrator.integrate(CONSTANT_ODE, t0, y, tend, new double[1]);
   assertEquals("variable-step integrator missed end event",
            eventTime, tFinal, TIME_TOLERANCE);
   assertTrue(handler.wasTriggered());
  }
  /**
  - Variable-step: event exactly at start.
    */
   @Test
   public void testVariableStepEventAtStart() throws Exception {
   DormandPrince853Integrator integrator =
   new DormandPrince853Integrator(1e-3, 10.0, 1e-6, 1e-12);
   double t0 = 0.0, tend = 10.0;
   double eventTime = 0.0;
   double[] y = new double[] { 1.0 };
   EndTimeEventHandler handler = new EndTimeEventHandler(eventTime);
   integrator.addEventHandler(handler, 1e-12, 1e-9, 100);
   double tFinal = integrator.integrate(CONSTANT_ODE, t0, y, tend, new double[1]);
   assertEquals(eventTime, tFinal, TIME_TOLERANCE);
   assertTrue(handler.wasTriggered());
  }
  /**
  - Variable-step: event at final time.
    */
   @Test
   public void testVariableStepEventAtFinalTime() throws Exception {
   DormandPrince853Integrator integrator =
   new DormandPrince853Integrator(1e-3, 10.0, 1e-6, 1e-12);
   double t0 = 0.0, tend = 10.0;
   double eventTime = 10.0;
   double[] y = new double[] { 1.0 };
   EndTimeEventHandler handler = new EndTimeEventHandler(eventTime);
   integrator.addEventHandler(handler, 1e-12, 1e-9, 100);
   double tFinal = integrator.integrate(CONSTANT_ODE, t0, y, tend, new double[1]);
   assertEquals(eventTime, tFinal, TIME_TOLERANCE);
   assertTrue(handler.wasTriggered());
  }
  /**
  - Variable-step: integration without events should reach final time.
    */
   @Test
   public void testVariableStepNoEventIntegration() throws Exception {
   DormandPrince853Integrator integrator =
   new DormandPrince853Integrator(1e-3, 10.0, 1e-6, 1e-12);
   double t0 = 0.0, tend = 10.0;
   double[] y = new double[] { 1.0 };
   double tFinal = integrator.integrate(CONSTANT_ODE, t0, y, tend, new double[1]);
   assertEquals(tend, tFinal, TIME_TOLERANCE);
   }
  /**
  - Fixed-step: integration without events reaches final time.
    */
   @Test
   public void testFixedStepNoEventIntegration() throws Exception {
   ClassicalRungeKuttaIntegrator integrator =
   new ClassicalRungeKuttaIntegrator(1.0);
   double t0 = 0.0, tend = 10.0;
   double[] y = new double[] { 1.0 };
   double tFinal = integrator.integrate(CONSTANT_ODE, t0, y, tend, new double[1]);
   assertEquals(tend, tFinal, TIME_TOLERANCE);
   }
  /**
  - Event handler that stops integration exactly at a given time.
    */
   private static class EndTimeEventHandler implements EventHandler {
   private final double eventTime;
   private boolean triggered;
   EndTimeEventHandler(double eventTime) {
   this.eventTime = eventTime;
   }
   public int eventOccurred(double t, double[] y, boolean increasing) {
   triggered = true;
   return STOP;
   }
   public double g(double t, double[] y) {
   return t - eventTime;
   }
   public void resetState(double t, double[] y) {
   // nothing to do
   }
   boolean wasTriggered() {
   return triggered;
   }
  }

}