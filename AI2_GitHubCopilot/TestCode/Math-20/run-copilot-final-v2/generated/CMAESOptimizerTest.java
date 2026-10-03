package org.apache.commons.math3.optimization.direct;

 import org.apache.commons.math3.analysis.MultivariateFunction;
 import org.apache.commons.math3.optimization.GoalType;
 import org.apache.commons.math3.optimization.PointValuePair;
 import org.apache.commons.math3.optimization.SimpleValueChecker;
 import org.apache.commons.math3.exception.OutOfRangeException;
 import org.junit.Test;
 import org.junit.Assert;

 /**
  * Tests for CMAESOptimizer, focused on the bound-out-of-repair bug reported in MATH-864.
  */
 public class CMAESOptimizerTest {

     /**
      * Simple sphere function: f(x) = sum(x_i^2).
      */
     private static final MultivariateFunction SPHERE = new MultivariateFunction() {
         public double value(double[] x) {
             double sum = 0;
             for (double v : x) {
                 sum += v * v;
             }
             return sum;
         }
     };

     /**
      * Sphere function offset so that optimum is near the upper bound,
      * stressing repair closer to the boundary.
      */
     private static MultivariateFunction offsetSphere(final double[] target) {
         return new MultivariateFunction() {
             public double value(double[] x) {
                 double sum = 0;
                 for (int i = 0; i < x.length; i++) {
                     double diff = x[i] - target[i];
                     sum += diff * diff;
                 }
                 return sum;
             }
         };
     }

     private CMAESOptimizer createOptimizer() {
         return new CMAESOptimizer(10, null, 30000, 0, true, 0, 0,
                 new org.apache.commons.math3.random.MersenneTwister(12345L),
                 false, new SimpleValueChecker(1e-6, 1e-12));
     }

     // --- Core MATH-864 reproduction ---

     @Test
     public void testMath864_BoundsRespected() {
         double[] lower = { -1.0, -1.0 };
         double[] upper = { 0.5, 0.5 };
         double[] start = { 0.0, 0.0 };

         CMAESOptimizer optimizer = createOptimizer();
         PointValuePair result = optimizer.optimize(20000, SPHERE, GoalType.MINIMIZE, start,
                 new org.apache.commons.math3.optimization.SimpleBounds(lower, upper));
         double[] point = result.getPoint();
         for (int i = 0; i < point.length; i++) {
             Assert.assertTrue("Component " + i + " out of upper bound: " + point[i] + " > " +
upper[i],
                     point[i] <= upper[i]);
             Assert.assertTrue("Component " + i + " out of lower bound: " + point[i] + " < " +
lower[i],
                     point[i] >= lower[i]);
         }
     }

     // --- Boundary conditions ---

     @Test
     public void testBoundsExactAtUpperBound() {
         double[] lower = { 1.0, 1.0 };
         double[] upper = { 1.0, 1.0 };
         double[] start = { 1.0, 1.0 };

         CMAESOptimizer optimizer = createOptimizer();
         PointValuePair result = optimizer.optimize(20000, SPHERE, GoalType.MINIMIZE, start,
                 new org.apache.commons.math3.optimization.SimpleBounds(lower, upper));
         double[] point = result.getPoint();
         Assert.assertEquals(1.0, point[0], 1e-10);
         Assert.assertEquals(1.0, point[1], 1e-10);
     }

     @Test
     public void testBoundsExactAtLowerBound() {
         double[] lower = { -2.0, -2.0 };
         double[] upper = { -2.0, -2.0 };
         double[] start = { -2.0, -2.0 };

         CMAESOptimizer optimizer = createOptimizer();
         PointValuePair result = optimizer.optimize(20000, SPHERE, GoalType.MINIMIZE, start,
                 new org.apache.commons.math3.optimization.SimpleBounds(lower, upper));
         double[] point = result.getPoint();
         Assert.assertEquals(-2.0, point[0], 1e-10);
         Assert.assertEquals(-2.0, point[1], 1e-10);
     }

     // --- Shrinking feasible region ---

     @Test
     public void testFeasibleRegionShrinksToPoint() {
         double[] lower = { 0.3, 0.3 };
         double[] upper = { 0.3, 0.3 }; // single feasible point
         double[] start = { 0.3, 0.3 };

         CMAESOptimizer optimizer = createOptimizer();
         PointValuePair result = optimizer.optimize(20000, SPHERE, GoalType.MINIMIZE, start,
                 new org.apache.commons.math3.optimization.SimpleBounds(lower, upper));
         double[] point = result.getPoint();
         Assert.assertEquals(0.3, point[0], 1e-10);
         Assert.assertEquals(0.3, point[1], 1e-10);
     }

     @Test
     public void testFeasibleRegionTinyButNonzero() {
         double[] lower = { 0.4999, 0.4999 };
         double[] upper = { 0.5001, 0.5001 };
         double[] start = { 0.5, 0.5 };

         CMAESOptimizer optimizer = createOptimizer();
         PointValuePair result = optimizer.optimize(20000, SPHERE, GoalType.MINIMIZE, start,
                 new org.apache.commons.math3.optimization.SimpleBounds(lower, upper));
         double[] point = result.getPoint();
         for (int i = 0; i < point.length; i++) {
             Assert.assertTrue(point[i] >= lower[i] && point[i] <= upper[i]);
         }
     }

     // --- Offset optimum near bounds ---

     @Test
     public void testOptimumNearUpperBound() {
         double[] lower = { -5.0, -5.0 };
         double[] upper = { 0.49, 0.49 }; // optimum outside feasible region
         double[] start = { 0.0, 0.0 };
         double[] target = { 1.0, 1.0 }; // sphere optimum is physically outside, should be repaired

         CMAESOptimizer optimizer = createOptimizer();
         PointValuePair result = optimizer.optimize(20000, offsetSphere(target), GoalType.MINIMIZE,
start,
                 new org.apache.commons.math3.optimization.SimpleBounds(lower, upper));
         double[] point = result.getPoint();
         for (int i = 0; i < point.length; i++) {
             Assert.assertTrue("Point out of upper bound", point[i] <= upper[i]);
             Assert.assertTrue("Point out of lower bound", point[i] >= lower[i]);
         }
         // it should be close to the upper bound (the closest feasible point)
         Assert.assertEquals(upper[0], point[0], 1e-6);
         Assert.assertEquals(upper[1], point[1], 1e-6);
     }

     @Test
     public void testOptimumNearLowerBound() {
         double[] lower = { -0.49, -0.49 };
         double[] upper = { 5.0, 5.0 };
         double[] start = { 0.0, 0.0 };
         double[] target = { -1.0, -1.0 };

         CMAESOptimizer optimizer = createOptimizer();
         PointValuePair result = optimizer.optimize(20000, offsetSphere(target), GoalType.MINIMIZE,
start,
                 new org.apache.commons.math3.optimization.SimpleBounds(lower, upper));
         double[] point = result.getPoint();
         for (int i = 0; i < point.length; i++) {
             Assert.assertTrue("Point out of bounds", point[i] >= lower[i] && point[i] <= upper[i]);
         }
         Assert.assertEquals(lower[0], point[0], 1e-6);
         Assert.assertEquals(lower[1], point[1], 1e-6);
     }

     // --- 10-dim high-dimensional sphere ---

     @Test
     public void testHighDimensionSphereWithinBounds() {
         int dim = 10;
         double[] lower = new double[dim];
         double[] upper = new double[dim];
         double[] start = new double[dim];
         for (int i = 0; i < dim; i++) {
             lower[i] = -0.5;
             upper[i] = 0.5;
             start[i] = 0.0;
         }
         CMAESOptimizer optimizer = new CMAESOptimizer(20, null, 30000, 0, true, 0, 0,
                 new org.apache.commons.math3.random.MersenneTwister(42L),
                 false, new SimpleValueChecker(1e-6, 1e-12));
         PointValuePair result = optimizer.optimize(100000, SPHERE, GoalType.MINIMIZE, start,
                 new org.apache.commons.math3.optimization.SimpleBounds(lower, upper));
         double[] point = result.getPoint();
         for (int i = 0; i < dim; i++) {
             Assert.assertTrue("Dimension " + i + " out of bounds", point[i] >= lower[i]
                     && point[i] <= upper[i]);
         }
     }

     // --- Check isFeasible contract indirectly (via repair) ---

     @Test
     public void testRepairConsistentWithBounds() {
         // Use a sphere with optimum far away, so many candidate points fall outside.
         double[] lower = { -0.2, -0.2 };
         double[] upper = { 0.2, 0.2 };
         double[] start = { 0.0, 0.0 };
         double[] target = { 10.0, 10.0 };

         CMAESOptimizer optimizer = createOptimizer();
         PointValuePair result = optimizer.optimize(20000, offsetSphere(target), GoalType.MINIMIZE,
start,
                 new org.apache.commons.math3.optimization.SimpleBounds(lower, upper));
         double[] point = result.getPoint();
         for (int i = 0; i < point.length; i++) {
             Assert.assertTrue(point[i] >= lower[i] && point[i] <= upper[i]);
         }
     }

     // --- Bound tolerance edge ---

     @Test
     public void testAtBoundaryWithNumericalNoise() {
         double[] lower = { -0.5, -0.5 };
         double[] upper = { 0.5, 0.5 };
         double[] start = { 0.49, 0.49 }; // start very close to boundary
         CMAESOptimizer optimizer = createOptimizer();
         PointValuePair result = optimizer.optimize(20000, SPHERE, GoalType.MINIMIZE, start,
                 new org.apache.commons.math3.optimization.SimpleBounds(lower, upper));
         double[] point = result.getPoint();
         for (int i = 0; i < point.length; i++) {
             Assert.assertTrue("Point: " + point[i], point[i] >= lower[i] && point[i] <= upper[i]);
         }
     }

     // --- Negatively-oriented maximization test (flipped objective) ---

     @Test
     public void testMaximizationWithBounds() {
         // Maximize negative sphere = minimize positive sphere, near upper bound.
         double[] lower = { -1.0, -1.0 };
         double[] upper = { 0.3, 0.3 };
         double[] start = { 0.0, 0.0 };
         double[] target = { 5.0, 5.0 }; // to push towards boundary in repaird state
         CMAESOptimizer optimizer = createOptimizer();
         PointValuePair result = optimizer.optimize(20000, offsetSphere(target), GoalType.MAXIMIZE,
start,
                 new org.apache.commons.math3.optimization.SimpleBounds(lower, upper));
         double[] point = result.getPoint();
         for (int i = 0; i < point.length; i++) {
             Assert.assertTrue("Point " + i + " out of bounds", point[i] >= lower[i]
                     && point[i] <= upper[i]);
         }
     }
 }
