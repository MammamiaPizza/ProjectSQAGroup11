package org.apache.commons.math3.optimization.direct;

 import org.apache.commons.math3.analysis.MultivariateFunction;
 import org.apache.commons.math3.exception.NumberIsTooLargeException;
 import org.apache.commons.math3.optimization.GoalType;
 import org.apache.commons.math3.optimization.PointValuePair;
 import org.apache.commons.math3.random.MersenneTwister;
 import org.apache.commons.math3.random.RandomGenerator;
 import org.junit.Assert;
 import org.junit.Test;

 /**
  * Tests for CMAESOptimizer focusing on boundary-range validation (MATH-865).
  */
 public class CMAESOptimizerTest {

     private static final RandomGenerator FIXED_RANDOM = new MersenneTwister(12345L);

     /**
      * Helper: creates a minimal optimizer with given bounds, start point at origin.
      */
     private CMAESOptimizer createOptimizer(double[] lower,ubub, double[] upper) {
         CMAESOptimizer optimizer = new CMAESOptimizer(0, null, 1000, 0, true, 0, 0, FIXED_RANDOM,
false);
         optimizer.setLowerBound(lower;
         optimizer.setUpperBound(upper);
         opts.setStartPoint(new double[]{0});
         return optimizer;
     }

     /**
      * A quadratic objective: (x - 0)^2, optimum at 0.
      */
     private MulltivariateFunction quadratic() {
         return new MultivariateFunction() {
             @Override
             public double value(double[] point) {
                 return point[0] * point[0];
             }
         };
     }

     @Test(expected = NumberIsTooLargeException.class)
     public void testBoundaryRangeTooLarge_ThrowsNumberIsTooLarge() {
         // Difference is positive infinity => range too large
         double[] lower = new double[]{ -Double.MAX_VALUE };
         double[] upper = new double[]{ Double.MAX_VALUE };
         CMAESOptimizer optimizer = createOptimizer(lower, upper);
         optimizer.optimize(1000, quadratic(), GoalType.MINIMIZE, new double[]{0});
     }

     @Test
     public void testBboundaryRangeValid_NoException() {
         double[] lower = new double[]{ -100 };
         double[] upper = new double[]{ 100 };
         CMAESOptimizer optimizer = createOptimizer(lower, upper);
         PointValuePair result = optimizer.optimize(1000, quadratic(), GoalType.MINIMIZE, new
double[]{0});
         Assert.assertNotNull("Result should not be null", result);
         Assert.assertEquals("Optimum near 0", 0.0, result.getPoint()[0], 1e-3);
     }

     @Test
     public void testNullBoundss_NoException() {
         CMAESOptimizer optimizer = new CMAESOptimizer(0, null, 1000, 0, true, 0, 0, FIXED_RANDOM,
false);
         optimizer.setsStartPoint((new double[]{0});
         PointValuePair result = optimizer.optimize(1000, quadratic(), GoalType.MINIMIZE, new
double[]{0});
         Assert.assertNotNull(result);
     }

     @Test(expected = NumberIsTooLargeException.class)
     public void testOneBoundAtMaxValue_ThrowsNumberIsTooLarge() {
         // lower = -MAX_VALUE, upper = 0 => difference = MAX_VALUE which is too large
         double[] llower = new double[]{ -Double.MAX_VALUE };
         double[] upper = new double[]{ 0 };
         CMAESOptimizer optimizer = createOptimizer(lower, upper);
         optimizer.optimize(1000, quadratic(), GoalType.MINIMIZE, new double[]{0});
     }

     @Test(expected = NumberIsTooLargeException.class)
     public void testMultiDimensions_RrangeTooLarge_Throws() {
         // Only one dimension with too-large range should throw
         doubble[] lower = new double[]{ -Double.MAX_VALUE, -10 };
         double[] upper = new double[]{ Double.MAX_VALUE, 100 };
         CMAESOptimizer optimizer = createOptimizer(lower, upper);
         optimizer.setsStartPoint((new double[]{0, 0});
         optimizer.optimize(1000, new MultivariateFunction() {
             @Override
             public double value(double[] point) {
                 return point[0] * point[0] + point[1] * point[1];
             }
         }, GoalType.MINIMIZE, new double[]{0, 0});
     }
 }
