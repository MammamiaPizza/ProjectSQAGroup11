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
         public int getDimension() { return 1; }
         public void computeDerivatives(double t, double[] y, double[] yDot) {
             yDot[0] = -lambda * y[0];
         }
     }

     /** Collects step sizes and first-step size. */
     private static class StepRecorder implements StepHandler {
         final List<Double> sizes = new ArrayList<Double>();
         double firstStep = Double.NaN;
         public void init(double t0, double[] y0, double t) {}
         public void handleStep(StepInterpolator interpolator, boolean isLast) {
             double h = interpolator.getCurrentTime() - interpolator.getPreviousTime();
             if (sizes.isEmpty()) {
                 firstStep = h;
             }
             sizes.add(h);
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

     private static StepRecorder runIntegration(double y0, double start, double end,
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
         assertFalse("no steps", r.sizes.isEmpty());
         double h = r.firstStep;
         assertTrue("first step " + h + " < " + low, h >= low);
         assertTrue("first step " + h + " > " + high, h <= high);
     }

     private static void assertAllStepsAbsBounded(StepRecorder r, double maxAbs) {
         for (double h : r.sizes) {
             assertTrue("step " + h + " exceeds " + maxAbs, Math.abs(h) <= maxAbs);
         }
     }

     /* ----- test methods ----- */

     @Test
     public void testFirstStepForwardMaxStep() {
         StepRecorder r = runIntegration(1.0, 0.0, 5.0, 1e-6, 0.1, 100.0, 100.0, 0.9, 0.2, 10.0);
         assertAllStepsAbsBounded(r, 0.1);
         assertFirstStepBounded(r, -0.1, 0.1);
     }

     @Test
     public void testFirstStepBackwardMaxStep() {
         StepRecorder r = runIntegration(1.0, 5.0, 0.0, 1e-6, 0.1, 100.0, 100.0, 0.9, 0.2, 10.0);
         assertAllStepsAbsBounded(r, 0.1);
         assertTrue("first step must be negative", r.firstStep < 0);
         assertTrue("first step too large", Math.abs(r.firstStep) <= 0.1);
     }

     @Test
     public void testFirstStepForwardFinalTime() {
         StepRecorder r = runIntegration(1.0, 0.0, 0.002, 1e-12, 10.0, 1e6, 1e6, 0.9, 0.2, 10.0);
         assertAllStepsAbsBounded(r, 0.002);
         assertTrue("first step <= end", r.firstStep <= 0.002);
     }

     @Test
     public void testFirstStepBackwardFinalTime() {
         StepRecorder r = runIntegration(1.0, -0.002, -0.004, 1e-12, 10.0, 1e6, 1e6, 0.9, 0.2,
10.0);
         assertAllStepsAbsBounded(r, 0.002);
         assertTrue("first step negative", r.firstStep < 0);
         assertTrue("first step >= end", r.firstStep >= -0.002);
     }

     @Test
     public void testMaxGrowthOneLimitsIncrease() {
         StepRecorder r = runIntegration(1.0, 0.0, 1.0, 1e-12, 10.0, 1e-10, 1e-12, 0.9, 0.2, 1.0);
         assertTrue("needs at least 2 steps", r.sizes.size() >= 2);
         double prev = Math.abs(r.sizes.get(0));
         for (int i = 1; i < r.sizes.size(); i++) {
             double curr = Math.abs(r.sizes.get(i));
             if (curr > prev) {
                 fail("step increased with maxGrowth=1.0: prev=" + prev + " curr=" + curr);
             }
             prev = curr;
         }
     }

     @Test
     public void testMinReductionNearOne() {
         StepRecorder r = runIntegration(1.0, 0.0, 10.0, 1e-12, 10.0, 1e-2, 1e-2, 0.9, 0.99, 10.0);
         assertTrue("should produce some steps", r.sizes.size() > 0);
     }

     @Test
     public void testSafetyExtremelySmallProduceManySteps() {
         StepRecorder r = runIntegration(1.0, 0.0, 1.0, 1e-12, 10.0, 1e-10, 1e-12, 0.001, 0.2,
10.0);
         assertTrue("many small steps expected due to low safety", r.sizes.size() > 20);
     }

     @Test
     public void testStepExactlyAtMaxStepFoward() {
         StepRecorder r = runIntegration(1.0, 0.0, 0.2, 0.005, 0.05, 1e-5, 1e-5, 0.9, 0.2, 10.0);
         assertAllStepsAbsBounded(r, 0.05);
     }

     @Test
     public void testStepExactlyAtMaxStepBacward() {
         StepRecorder r = runIntegration(1.0, 0.2, 0.0, 0.005, 0.05, 1e-5, 1e-5, 0.9, 0.2, 10.0);
         assertAllStepsAbsBounded(r, 0.05);
     }

     @Test
     public void testIntegrateToSameTimeNoSteps() {
         StepRecorder r = runIntegration(1.0, 0.0, 0.0, 1e-6, 1.0, 1e-6, 1e-6, 0.9, 0.2, 10.0);
         assertTrue("no steps expected", r.sizes.isEmpty());
     }

     @Test
     public void testFirstStepBoundedByBothMaxStepAndFinalTime() {
         StepRecorder r = runIntegration(1.0, 0.0, 0.6, 1e-6, 0.5, 100.0, 100.0, 0.9, 0.2, 10.0);
         assertTrue("first step <= maxStep", r.firstStep <= 0.5);
         assertTrue("first step <= end", r.firstStep <= 0.6);
     }
 }