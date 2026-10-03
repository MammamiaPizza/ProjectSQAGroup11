package org.apache.commons.math.optimization.direct;

 import org.apache.commons.math.MaxIterationsExceededException;
 import org.apache.commons.math.analysis.MultivariateRealFunction;
 import org.apache.commons.math.optimization.GoalType;
 import org.apache.commons.math.optimization.OptimizationException;
 import org.apache.commons.math.optimization.RealConvergenceChecker;
 import org.apache.commons.math.optimization.RealPointValuePair;
 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link MultiDirectional} direct search optimizer.
  * Targets the iterateSimplex convergence loop, evaluateNewSimplex
  * transformation, comparator-based simplex ranking, and constructor
  * coefficient handling.
  */
 public class MultiDirectionalTest {

     // --- Helpers ------------------------------------------------------------

     /** Sphere f(x)=sum(x_i^2), minimum at origin with value 0. */
     private static MultivariateRealFunction sphere(final int dim) {
         return new MultivariateRealFunction() {
             public double value(double[] x) {
                 double s = 0.0;
                 for (double v : x) s += v * v;
                 return s;
             }
         };
     }

     /** Rosenbrock f(x,y)=(1-x)^2+100(y-x^2)^2, minimum at (1,1) value 0. */
     private static MultivariateRealFunction rosenbrock() {
         return new MultivariateRealFunction() {
             public double value(double[] x) {
                 double a = 1.0 - x[0];
                 double b = x[1] - x[0] * x[0];
                 return a * a + 100.0 * b * b;
             }
         };
     }

     // --- Normal / correctness -----------------------------------------------

     @Test
     public void testMinimizeSphere2D() throws Exception {
         MultiDirectional opt = new MultiDirectional();
         opt.setMaxIterations(500);
         RealPointValuePair r = opt.optimize(sphere(2), GoalType.MINIMIZE,
                                             new double[] { 3.0, 4.0 });
         assertEquals("f-value near zero", 0.0, r.getValue(), 1e-8);
         assertEquals("x0 near zero", 0.0, r.getPoint()[0], 1e-6);
         assertEquals("x1 near zero", 0.0, r.getPoint()[1], 1e-6);
     }

     @Test
     public void testMinimizeSphere3D() throws Exception {
         MultiDirectional opt = new MultiDirectional();
         opt.setMaxIterations(500);
         RealPointValuePair r = opt.optimize(sphere(3), GoalType.MINIMIZE,
                                             new double[] { 1.0, 2.0, 3.0 });
         assertEquals(0.0, r.getValue(), 1e-8);
     }

     @Test
     public void testMaximizeNegSphere() throws Exception {
         // Maximizing -sphere is equivalent to minimizing sphere.
         MultiDirectional opt = new MultiDirectional();
         opt.setMaxIterations(500);
         MultivariateRealFunction neg = new MultivariateRealFunction() {
             public double value(double[] x) {
                 return -(x[0] * x[0] + x[1] * x[1]);
             }
         };
         RealPointValuePair r = opt.optimize(neg, GoalType.MAXIMIZE,
                                             new double[] { 2.0, 3.0 });
         assertEquals("max at origin value 0", 0.0, r.getValue(), 1e-6);
         assertTrue("x0 near zero", Math.abs(r.getPoint()[0]) < 1e-4);
         assertTrue("x1 near zero", Math.abs(r.getPoint()[1]) < 1e-4);
     }

     @Test
     public void testRosenbrockImproves() throws Exception {
         MultiDirectional opt = new MultiDirectional();
         opt.setMaxIterations(2000);
         RealPointValuePair r = opt.optimize(rosenbrock(), GoalType.MINIMIZE,
                                             new double[] { -1.2, 1.0 });
         // Rosenbrock minimum is 0.0 at (1,1); buggy may stall, but value must
         // drop significantly from the start (f(-1.2,1) ~ 24.2).
         assertTrue("value improved from initial", r.getValue() < 15.0);
     }

     // --- Constructor and coefficients ---------------------------------------

     @Test
     public void testDefaultConstructorConverges() throws Exception {
         MultiDirectional opt = new MultiDirectional();
         opt.setMaxIterations(100);
         RealPointValuePair r = opt.optimize(sphere(2), GoalType.MINIMIZE,
                                             new double[] { 1.0, 2.0 });
         assertTrue("converged with default coefficients", r.getValue() < 1e-4);
     }

     @Test
     public void testCustomKhiGamma() throws Exception {
         MultiDirectional opt = new MultiDirectional(1.5, 0.3);
         opt.setMaxIterations(500);
         RealPointValuePair r = opt.optimize(sphere(2), GoalType.MINIMIZE,
                                             new double[] { 5.0, -3.0 });
         assertEquals(0.0, r.getValue(), 1e-8);
     }

     @Test
     public void testBoundaryCoefficientsSmallKhi() throws Exception {
         // khi < 1.0 disables expansion beyond reflection; contraction gamma near 1.0.
         MultiDirectional opt = new MultiDirectional(0.5, 0.9);
         opt.setMaxIterations(500);
         RealPointValuePair r = opt.optimize(sphere(2), GoalType.MINIMIZE,
                                             new double[] { 3.0, 4.0 });
         assertEquals(0.0, r.getValue(), 1e-6);
     }

     // --- Already at optimum -------------------------------------------------

     @Test
     public void testStartAtOptimum() throws Exception {
         MultiDirectional opt = new MultiDirectional();
         opt.setMaxIterations(100);
         RealPointValuePair r = opt.optimize(sphere(2), GoalType.MINIMIZE,
                                             new double[] { 0.0, 0.0 });
         assertEquals(0.0, r.getValue(), 1e-12);
         assertEquals(0.0, r.getPoint()[0], 1e-12);
         assertEquals(0.0, r.getPoint()[1], 1e-12);
     }

     // --- Iteration / evaluation limits --------------------------------------

     @Test(expected = MaxIterationsExceededException.class)
     public void testMaxIterationsExceededTightLimit() throws Exception {
         MultiDirectional opt = new MultiDirectional();
         opt.setMaxIterations(2);
         opt.optimize(sphere(2), GoalType.MINIMIZE,
                      new double[] { 100.0, 200.0 });
     }

     @Test
     public void testIterationAndEvaluationCounters() throws Exception {
         MultiDirectional opt = new MultiDirectional();
         opt.setMaxIterations(200);
         opt.optimize(sphere(2), GoalType.MINIMIZE, new double[] { 5.0, 5.0 });
         assertTrue("iterations > 0", opt.getIterations() > 0);
         assertTrue("evaluations > 0", opt.getEvaluations() > 0);
     }

     // --- Convergence checker integration ------------------------------------

     @Test
     public void testConvergenceCheckerInvoked() throws Exception {
         MultiDirectional opt = new MultiDirectional();
         opt.setMaxIterations(500);
         final boolean[] invoked = new boolean[1];
         opt.setConvergenceChecker(new RealConvergenceChecker() {
             public boolean converged(int iteration,
                                      RealPointValuePair previous,
                                      RealPointValuePair current) {
                 invoked[0] = true;
                 return current != null && Math.abs(current.getValue()) < 0.2;
             }
         });
         RealPointValuePair r = opt.optimize(sphere(2), GoalType.MINIMIZE,
                                             new double[] { 5.0, 5.0 });
         assertTrue("checker was called", invoked[0]);
         assertTrue("result satisfies checker tolerance", r.getValue() < 0.2);
     }

     // --- Fault-related: stuck simplex exhausts iterations -------------------

     @Test(expected = MaxIterationsExceededException.class)
     public void testStuckSimplexExceedsMaxIter() throws Exception {
         // When neither reflection nor contraction improves the best point
         // the simplex is not updated and the inner loop spins, exhausting
         // the iteration budget.  This is the root cause of MATH-283.
         MultiDirectional opt = new MultiDirectional();
         opt.setMaxIterations(10);

         // Function whose gradient near the start simplex yields little
         // directional improvement with default khi/gamma.
         MultivariateRealFunction tricky = new MultivariateRealFunction() {
             public double value(double[] x) {
                 // Narrow diagonal valley; hard for axis-aligned simplex moves.
                 double u = x[0] + x[1];
                 double v = x[0] - x[1];
                 return u * u + 0.001 * v * v;
             }
         };
         opt.optimize(tricky, GoalType.MINIMIZE, new double[] { 10.0, -10.0 });
     }
 }