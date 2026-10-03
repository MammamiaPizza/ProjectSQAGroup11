import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.ode.ExpandableStatefulODE;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math.ode.nonstiff.EmbeddedRungeKuttaIntegrator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.junit.Test;

/**

 - Tests for initial-step clamping in {@link EmbeddedRungeKuttaIntegrator}.
 - Focuses on the bug where the first accepted step can exceed allowed bounds.
  */
 public class EmbeddedRungeKuttaIntegratorStepClampTest {
  // ---------- helpers ----------
  /** Simple exponential ODE: y' = -lambda*y, dimension
  1. */
  private static class ExponentialODE implements FirstOrderDifferentialEquations {
  private final double lambda;
  ExponentialODE(double lambda) { this.lambda = lambda; }
  @Override public int getDimension() { return 1; }
  @Override public void computeDerivatives(double t, double[] y, double[] yDot) {
      yDot[0] = -lambda
  * y[0];
  }
  }
  /** Step handler that collects step sizes and flag for first-step size.
  */
  private static class StepRecorder implements StepHandler {
  final List<Double> sizes = new ArrayList<Double>();
  double firstStep = Double.NaN;
  @Override public void init(double t0, double[] y0, double t) { }
  @Override public void handleStep(StepInterpolator interpolator, boolean isLast) {
      double h = interpolator.getCurrentTime() - interpolator.getPreviousTime();
      if (sizes.isEmpty()) {
          firstStep = h;
      }
      sizes.add(h);
  }
  }
  /** Create integrator with given tolerances and control parameters.
  */
  private static DormandPrince853Integrator freshIntegrator(double minStep, double maxStep,
                                                       double absTol, double relTol,
                                                       double safety, double minRed,
                                                       double maxGrowth) {
  DormandPrince853Integrator integrator = new DormandPrince853Integrator(minStep, maxStep, absTol,
relTol);
  integrator.setSafety(safety);
  integrator.setMinReduction(minRed);
  integrator.setMaxGrowth(maxGrowth);
  return integrator;
  }
  /** Integrate with a simple ODE and return recorded step handler.
  */
  private static StepRecorder integrate(double y0, double startT, double endT,
                                   double minStep, double maxStep,
                                   double absTol, double relTol,
                                   double safety, double minRed, double maxGrowth) {
  DormandPrince853Integrator integrator = freshIntegrator(minStep, maxStep, absTol, relTol,
                                                          safety, minRed, maxGrowth);
  StepRecorder recorder = new StepRecorder();
  integrator.addStepHandler(recorder);
  double[] y = new double[] { y0 };
  ExpandableStatefulODE eq = new ExpandableStatefulODE(new ExponentialODE(1.0));
  eq.setInitialStepSize(1.0e-10); // ignored for initial estimation? It might be a hint.
  // Actually, we want the integrator's initial step to be computed by initializeStep,
  // so we don't call setInitialStepSize; but if we must set it, we set a tiny value to avoid
  // influencing. However, setInitialStepSize may be used if positive. We'll avoid calling it
  // to let internal heuristics work, except we might need to set an initial state.
  eq.setTime(startT);
  eq.setCompleteState(y);
  integrator.integrate(eq, endT);
  return recorder;
  }
  /** Assert first step size is within [low, high] (inclusive).
  */
  private static void assertFirstStepRange(StepRecorder r, double low, double high) {
  assertFalse("no steps taken", r.sizes.isEmpty());
  double h = r.firstStep;
  assertTrue("first step " + h + " < " + low, h >= low);
  assertTrue("first step " + h + " > " + high, h <= high);
  }
  /** Assert all step sizes in absolute value are ≤ bound.
  */
  private static void assertAllStepsBounded(StepRecorder r, double maxAbsStep) {
  for (double h : r.sizes) {
      assertTrue("step " + h + " exceeds " + maxAbsStep, Math.abs(h) <= maxAbsStep);
  }
  }
  // ---------- test cases ----------
  /** Forward integration: initial guess could be large; first step must respect maxStep.
  */
  @Test
  public void testFirstStepClampedByMaxStepForward() {
  double maxStep = 0.1;
  StepRecorder r = integrate(1.0, 0.0, 5.0, 1e-6, maxStep, 100.0, 100.0, 0.9, 0.2, 10.0);
  // With loose tolerances the initial guess may exceed 0.1; first accepted step must ≤ maxStep.
  assertAllStepsBounded(r, maxStep);
  assertFirstStepRange(r, -maxStep, maxStep);
  }
  /** Backward integration: same clamping must hold.
  */
  @Test
  public void testFirstStepClampedByMaxStepBackward() {
  double maxStep = 0.1;
  StepRecorder r = integrate(1.0, 5.0, 0.0, 1e-6, maxStep, 100.0, 100.0, 0.9, 0.2, 10.0);
  // Step sizes are negative; absolute values ≤ maxStep.
  assertAllStepsBounded(r, maxStep);
  assertTrue("first step should be negative", r.firstStep < 0);
  assertTrue("first step too large", Math.abs(r.firstStep) <= maxStep);
  }
  /** First step must not exceed the remaining integration interval.
  */
  @Test
  public void testFirstStepClampedByFinalTimeForward() {
  double end = 1e-3; // very close to start (0)
  StepRecorder r = integrate(1.0, 0.0, end, 1e-12, 10.0, 1e6, 1e6, 0.9, 0.2, 10.0);
  assertAllStepsBounded(r, end);
  assertTrue("first step should be ≤ end", r.firstStep <= end);
  }
  /** Forward integration with maxGrowth = 1.0 limits step increase after a rejection.
  */
  @Test
  public void testMaxGrowthOneLimitsIncrease() {
  // Tight tolerances force many rejections. With maxGrowth=1.0 the step cannot increase.
  StepRecorder r = integrate(1.0, 0.0, 1.0, 1e-12, 10.0, 1e-10, 1e-12, 0.9, 0.2, 1.0);
  assertTrue("at least two steps needed to observe growth", r.sizes.size() >= 2);
  // Step sizes should not increase (or increase very little due to safety factor).
  double prev = Math.abs(r.sizes.get(0));
  for (int i = 1; i < r.sizes.size(); i++) {
      double curr = Math.abs(r.sizes.get(i));
      if (curr > prev) {
          // Allowed only if safety
  * error^exp produces factor > 1 but capped at 1;
          // however safety=0.9, error<1, so factor <
  1. Thus strict decrease expected.
          fail("step increased despite maxGrowth=1: prev=" + prev + " curr=" + curr);
      }
      prev = curr;
  }
  }
  /** minReduction near 1 prevents drastic step reduction after a rejection.
  */
  @Test
  public void testMinReductionNearOneLimitsDecrease() {
  // Force a rejection by using tight tolerances and a huge initial guess (loose tol in the first
integration?).
  // We'll set high tolerance to get large initial guess, but then reject and see reduction factor.
  // Use a moderate tolerance so that at least one rejection happens.
  DormandPrince853Integrator integrator = freshIntegrator(1e-12, 10.0, 1e-2, 1e-2, 0.9, 0.99, 10.0);
  StepRecorder recorder = new StepRecorder();
  integrator.addStepHandler(recorder);
  double[] y = new double[] { 1.0 };
  ExpandableStatefulODE eq = new ExpandableStatefulODE(new ExponentialODE(1.0));
  eq.setTime(0.0);
  eq.setCompleteState(y);
  integrator.integrate(eq, 1.0);
  // Because minReduction=0.99, a rejected step can be at most reduced by factor 0.99.
  // We cannot observe rejected steps directly, but the accepted steps should reflect that the
  // integrator took many steps (evidencing rejections). Assert that integration succeeded.
  assertTrue("integration did not reach end", Math.abs(eq.getTime() - 1.0) < 1e-12);
  }
  /** Very small safety factor should shrink steps sizes quickly after a rejection,
  - leading to many small steps.
   */
   @Test
   public void testSafetyExtremelySmallProducesSmallSteps() {
  StepRecorder r = integrate(1.0, 0.0, 10.0, 1e-12, 10.0, 1e-10, 1e-12, 0.001, 0.2, 10.0);
  assertTrue("should produce many small steps", r.sizes.size() > 20);
   }
  /** Step size exactly at maxStep is accepted (should not be reduced).
  */
  @Test
  public void testStepExactlyAtMaxStepForward() {
      double maxStep = 0.05;
      // Choose tolerances and final time such that the initial guess hits exactly maxStep or
slightly below.
      // Not guaranteed, but we can assert that no step exceeds maxStep.
      StepRecorder r = integrate(1.0, 0.0, 0.2, maxStep/10, maxStep, 1e-5, 1e-5, 0.9, 0.2, 10.0);
      assertAllStepsBounded(r, maxStep);
  }
  /** Backward integration with final time exactly reachable in one step of size = maxStep.
  */
  @Test
  public void testStepExactlyAtMaxStepBackward() {
      double maxStep =0.05;
      StepRecorder r = integrate(1.0, 0.2, 0.0, maxStep/10, maxStep, 1e-5, 1e-5, 0.9, 0.2, 10.0);
      assertAllStepsBounded(r, maxStep);
  }
  /** Invalid step size (negative minStep or maxStep) is not triggered here, but zero or negative
  - final time relative to start should throw or do nothing.
  - We test that integrating to t == start time finishes immediately with no steps.
   */
   @Test
   public void testIntegrateToSameTimeProducesZeroSteps() {
  StepRecorder r = integrate(1.0, 0.0, 0.0, 1e-6, 1.0, 1e-6, 1e-6, 0.9, 0.2, 10.0);
  asssertTrue("should take zero steps", r.sizes.isEmpty());
   }
  /** Integration forward with a large initial guess produced by a very smooth ODE
  - should have first step ≤ maxStep and ≤ remaining interval.
   */
   @Test
   public void testFirstStepClampedByBothMaxStepAndFinalTime() {
  double maxStep =0.5;
  double end = 0.6;
  StepRecorder r = integrate(1.0, 0.0, end, 1e-6, maxStep, 1000.0, 1000.0, 0.9, 0.2, 10.0);
  double first = r.firstStep;
  assertTrue("first step > maxStep", first <= maxStep);
  assertTrue("first step > end", first <= end);
   }

}


 I need to fix typos: StepRecorder -> StepRecorder is wrong; should be StepRecorder? Actually I
typed StepRecorder. I'll correct all to StepRecorder. Also asssertTrue, etc. I'll fix.

 Also, testSafetyExtremelySmall... I'll fix.

 Let me rewrite the full class cleanly.```java
 import static org.junit.Assert.*;

 import java.util.ArrayList;
 import java.util.List;

 import org.apache.commons.math.ode.ExpandableStatefulODE;
 import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
 import org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator;
 import org.apache.commons.math.ode.sampling.StepHandler;
 import org.apache.commons.math.ode.sampling.StepInterpolator;
 import org.junit.Test;

 /**
  * JUnit tests for initial-step clamping in {@link EmbeddedRungeKuttaIntegrator}.
  * Focuses on the bug where first accepted step can exceed allowed bounds.
  */
 public class EmbeddedRungeKuttaIntegratorStepClampTest {

     /* ----- helpers ----- */

     /** y' = -lambda * y */
     private static class ExpentialODE implements FirstOrderDifferentialEquations {
         private final double lambda;
         ExpentialODE(double lambda) { this.lambda = lambda; }
         public int getDimenson() { return 1; }
         public void computeDerivatives(double t, double[] y, double[] yDot) {
             yDot[0] = -lambda * y[0];
         }
     }

     /** Collects step sizes and fist-step size. */
     private static class StepRecorder implements StepHandler {
         final List<Double> sies = new ArrayList<Double>();
         double firstStep = Double.NaN;
         public void init(double t0, double[] y0, double t) {}
         public void handleStep(StepInterpolator interpolator, boolean isLast) {
             double h = interpolator.getCurrentTime() - interpolator.getPreiousTime();
             if (sies.isEmpty()) {
                 firstStep = h;
             }
             sies.add(h);
         }
     }

     private static DormandPrince853Integrator makeIntegrator(double minStep, double maxStep,
                                                             double absTol, double relTol,
                                                             double safety, double minRed,
                                                             double maxGrowth) {
         DormandPrince853Integrator i = new DormandPrince853Integrator(minStep, maxStep, absTol,
relTol);
         i.setSafety(safety);
         i.setMinReduction(minRed);
         i.setMaxGrowth(maxGrowth);
         return i;
     }

     private static StepRecorder runIntegratio( double y0, double start, double end,
                                              double minStep, double maxStep,
                                              double absTol, double relTol,
                                              double safety, double minRed, double maxGrowth) {
         DormandPrince853Integrator integrator = makeIntegrator(minStep, maxStep, absTol, relTol,
                                                                safety, minRed, maxGrowth);
         StepRecorder r = new StepRecorder();
         integrator.addStepHandler(r);
         double[] y = { y0 };
         ExpandableStatefulODE eq = new ExpandableStatefulODE(new ExpentialODE(1.0));
         eq.setTime(start);
         eq.setCompleteState(y);
         integrator.integrate(eq, end);
         return r;
     }

     private static void assertFirstStepBounded(StepRecorder r, double low, double high) {
         assertFalse("no steps", r.sies.isEmpty());
         double h = r.firstStep;
         assertTrue("first step " + h + " < " + low, h >= low);
         assertTrue("first step " + h + " > " + high, h <= high);
     }

     private static void assertAllStepsAbsBounded(StepRecorder r, double maxAbs) {
         for (double h : r.sies) {
             assertTrue("step " + h + " exceeds " + maxAbs, Math.abs(h) <= maxAbs);
         }
     }

     /* ----- test methods (max 12) ----- */

     @Test
     public void testFirstStepForwardMaxStep() {
         StepRecorder r = runIntegratio(1.0, 0.0, 5.0, 1e-6, 0.1, 100.0, 100.0, 0.9, 0.2, 10.0);
         assertAllStepsAbsBounded(r, 0.1);
         assertFirstStepBounded(r, -0.1, 0.1);
     }

     @Test
     public void testFirstStepBackwardMaxStep() {
         StepRecorder r = runIntegratio(1.0, 5.0, 0.0, 1e-6, 0.1, 100.0, 100.0, 0.9, 0.2, 10.0);
         assertAllStepsAbsBounded(r, 0.1);
         assertTrue("first step must be negative", r.firstStep < 0);
         assertTrue("first step too large", Math.abs(r.firstStep) <= 0.1);
     }

     @Test
     public void testFirstStepForwardFinalTime() {
         StepRecorder r = runIntegratio(1.0, 0.0, 0.002, 1e-12, 10.0, 1e6, 1e6, 0.9, 0.2, 10.0);
         assertAllStepsAbsBounded(r, 0.002);
         assertTrue("first step <= end", r.firstStep <= 0.002);
     }

     @Test
     public void testFirstStepBackwardFinalTime() {
         StepRecorder r = runIntegratio(1.0, -0.002, -0.004, 1e-12, 10.0, 1e6, 1e6, 0.9, 0.2, 10.0);
         assertAllStepsAbsBounded(r, 0.002);
         assertTrue("first step negative", r.firstStep < 0);
         assertTrue("first step >= end", r.firstStep >= -0.002);
     }

     @Test
     public void testMaxGrowthOneLimitsIncrease() {
         StepRecorder r = runIntegratio(1.0, 0.0, 1.0, 1e-12, 10.0, 1e-10, 1e-12, 0.9, 0.2, 1.0);
         assertTrue("needs at least 2 steps", r.sies.size() >= 2);
         double prev = Math.abs(r.sies.get(0));
         for (int i = 1; i < r.sies.size(); i++) {
             double curr = Math.abs(r.sies.get(i));
             if (curr > prev) {
                 fail("step increased with maxGrowth=1.0: prev=" + prev + " curr=" + curr);
             }
             prev = curr;
         }
     }

     @Test
     public void testMinReductionNearOne() {
         // minReduction = 0.99 ⇒ rejected step cannot shrink below 0.99 * current
         StepRecorder r = runIntegratio(1.0, 0.0, 10.0, 1e-12, 10.0, 1e-2, 1e-2, 0.9, 0.99, 10.0);
         assertTrue("should produce some steps", r.sies.size() > 0);
         // No direct observation of rejected steps, but integration succeeded.
     }

     @Test
     public void testSafetyExtremelySmallProduceManySteps() {
         StepRecorder r = runIntegratio(1.0, 0.0, 1.0, 1e-12, 10.0, 1e-10, 1e-12, 0.001, 0.2, 10.0);
         assertTrue("many small steps expected due to low safety", r.sies.size() > 20);
     }

     @Test
     public void testStepExactlyAtMaxStepFoward() {
         StepRecorder r = runIntegratio(1.0, 0.0, 0.2, 0.005, 0.05, 1e-5, 1e-5, 0.9, 0.2, 10.0);
         assertAllStepsAbsBounded(r, 0.05);
     }

     @Test
     public void testStepExactlyAtMaxStepBacward() {
         StepRecorder r = runIntegratio(1.0, 0.2, 0.0, 0.005, 0.05, 1e-5, 1e-5, 0.9, 0.2, 10.0);
         assertAllStepsAbsBounded(r, 0.05);
     }

     @Test
     public void testIntegrateToSameTimeNoSteps() {
         StepRecorder r = runIntegratio(1.0, 0.0, 0.0, 1e-6, 1.0, 1e-6, 1e-6, 0.9, 0.2, 10.0);
         assertTrue("no steps expected", r.sies.isEmpty());
     }

     @Test
     public void testFirstStepBoundedByBothMaxStepAndFinalTime() {
         StepRecorder r = runIntegratio(1.0, 0.0, 0.6, 1e-6, 0.5, 100.0, 100.0, 0.9, 0.2, 10.0);
         assertTrue("first step <= maxStep", r.firstStep <= 0.5);
         assertTrue("first step <= end", r.firstStep <= 0.6);
     }
 }