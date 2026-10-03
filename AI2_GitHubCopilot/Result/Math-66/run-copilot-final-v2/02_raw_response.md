package org.apache.commons.math.optimization.univariate;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import org.apache.commons.math.FunctionEvaluationException;
 import org.apache.commons.math.MaxIterationsExceededException;
 import org.apache.commons.math.analysis.UnivariateRealFunction;
 import org.apache.commons.math.exception.NotStrictlyPositiveException;
 import org.apache.commons.math.optimization.GoalType;
 import org.junit.Test;

 /**
  * Tests for {@link BrentOptimizer} covering the MATH-395 bug:
  * incorrect evaluation count statistics and accuracy loss.
  */
 public class BrentOptimizerTest {

     private static final double PI = Math.PI;
     private static final double EXACT_SIN_MIN = 4.71238898038469;
     private static final double EXACT_QUINTIC_MIN = -0.2719561270319131;

     private final UnivariateRealFunction sinFunction = new UnivariateRealFunction() {
         public double value(double x) {
             return Math.sin(x);
         }
     };

     private final UnivariateRealFunction quinticFunction = new UnivariateRealFunction() {
         public double value(double x) {
             return (x - 1) * (x - 0.5) * x * (x + 0.5) * (x + 1);
         }
     };

     @Test
     public void testSinMinNormal() throws Exception {
         BrentOptimizer opt = new BrentOptimizer();
         double result = opt.optimize(sinFunction, GoalType.MINIMIZE, 0, 2 * PI, PI);
         assertEquals(-1.0, sinFunction.value(result), 1e-9);
         assertEquals(EXACT_SIN_MIN, result, 1e-8);
         assertTrue("Too few evaluations", opt.getEvaluations() >= 10);
     }

     @Test
     public void testQuinticMinNormal() throws Exception {
         BrentOptimizer opt = new BrentOptimizer();
         double result = opt.optimize(quinticFunction, GoalType.MINIMIZE, -1, 1, 0);
         assertEquals(quinticFunction.value(EXACT_QUINTIC_MIN), quinticFunction.value(result),
1e-8);
         assertEquals(EXACT_QUINTIC_MIN, result, 1e-7);
         assertTrue(opt.getEvaluations() >= 10);
     }

     @Test
     public void testBoundedIntervalSin() throws Exception {
         BrentOptimizer opt = new BrentOptimizer();
         double result = opt.optimize(sinFunction, GoalType.MINIMIZE, 4.5, 5.0, 4.7);
         assertEquals(EXACT_SIN_MIN, result, 1e-8);
     }

     @Test
     public void testWideIntervalSin() throws Exception {
         BrentOptimizer opt = new BrentOptimizer();
         double result = opt.optimize(sinFunction, GoalType.MINIMIZE, 0, 10 * PI, 5 * PI);
         assertEquals(EXACT_SIN_MIN, result, 1e-7);
     }

     @Test
     public void testMaximizeSin() throws Exception {
         BrentOptimizer opt = new BrentOptimizer();
         double result = opt.optimize(sinFunction, GoalType.MAXIMIZE, 0, PI, PI / 2);
         assertEquals(PI / 2, result, 1e-8);
         assertEquals(1.0, sinFunction.value(result), 1e-9);
     }

     @Test
     public void testQuinticEvaluationsNotTooFew() throws Exception {
         BrentOptimizer opt = new BrentOptimizer();
         opt.optimize(quinticFunction, GoalType.MINIMIZE, -1, 1, 0);
         // With the bug a single run uses only ~18 evaluations instead of many more.
         assertTrue("Evaluation count too small, got " + opt.getEvaluations(),
                    opt.getEvaluations() > 20);
     }

     @Test
     public void testIterationCountAfterOptimization() throws Exception {
         BrentOptimizer opt = new BrentOptimizer();
         opt.optimize(sinFunction, GoalType.MINIMIZE, 0, 2 * PI, PI);
         assertTrue("Iteration count should be positive", opt.getIterationCount() > 0);
     }

     @Test
     public void testAbsoluteAccuracyNotPositive() {
         BrentOptimizer opt = new BrentOptimizer();
         opt.setAbsoluteAccuracy(0.0);
         try {
             opt.optimize(sinFunction, GoalType.MINIMIZE, 0, 2 * PI, PI);
             fail("Expected NotStrictlyPositiveException");
         } catch (NotStrictlyPositiveException e) {
             // expected
         } catch (Exception e) {
             fail("Unexpected exception: " + e);
         }
     }

     @Test
     public void testRelativeAccuracyNotPositive() {
         BrentOptimizer opt = new BrentOptimizer();
         opt.setRelativeAccuracy(0.0);
         try {
             opt.optimize(sinFunction, GoalType.MINIMIZE, 0, 2 * PI, PI);
             fail("Expected NotStrictlyPositiveException");
         } catch (NotStrictlyPositiveException e) {
             // expected
         } catch (Exception e) {
             fail("Unexpected exception: " + e);
         }
     }

     @Test
     public void testMaxIterationsExceeded() {
         BrentOptimizer opt = new BrentOptimizer();
         opt.setMaximalIterationCount(1);
         try {
             opt.optimize(quinticFunction, GoalType.MINIMIZE, -1, 1, 0);
             fail("Expected MaxIterationsExceededException");
         } catch (MaxIterationsExceededException e) {
             // expected
         } catch (Exception e) {
             fail("Unexpected exception: " + e);
         }
     }

     @Test
     public void testStartValueAtEndpoint() throws Exception {
         BrentOptimizer opt = new BrentOptimizer();
         double result = opt.optimize(sinFunction, GoalType.MINIMIZE, 0, 2 * PI, 0);
         assertEquals(EXACT_SIN_MIN, result, 1e-8);
     }

     @Test
     public void testDefaultStartValueConverges() throws Exception {
         BrentOptimizer opt = new BrentOptimizer();
         double result = opt.optimize(sinFunction, GoalType.MINIMIZE, 0, 2 * PI);
         assertEquals(EXACT_SIN_MIN, result, 1e-8);
     }
 }