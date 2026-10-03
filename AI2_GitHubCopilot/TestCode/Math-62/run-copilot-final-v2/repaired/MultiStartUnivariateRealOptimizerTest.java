package org.apache.commons.math.optimization.univariate;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import org.apache.commons.math.FunctionEvaluationException;
 import org.apache.commons.math.analysis.UnivariateRealFunction;
 import org.apache.commons.math.exception.ConvergenceException;
 import org.apache.commons.math.exception.MathIllegalStateException;
 import org.apache.commons.math.optimization.GoalType;
 import org.apache.commons.math.random.JDKRandomGenerator;
 import org.apache.commons.math.random.RandomGenerator;
 import org.junit.Test;

 /** Tests for {@link MultiStartUnivaariateRealOptimizer}.
  *
  * @version $Revision$ $Date$
  */
 public class MultiStartUnivariateRealOptimizerTest {

     // ---------- helper functions ----------

     private static class QuinticFunction implements UnivariateRealFunction {
         public double value(double x) {
             return (x - 1) * (x - 0.5) * x * (x + 0.5) * (x + 1);
         }
     }

     private static class QuadraticFunction implements UnivariateRealFunction {
         private final double a, b; // a*(x-b)^2

         QuadraticFunction(double a, double b) {
             this.a = a;
             this.b = b;
         }

         public double value(double x) {
             double d = x - b;
             return a * d * d;
         }
     }

     private static class SinFunction implements UnivariateRealFunction {
         public double value(double x) {
             return Math.sin(100 * x);
         }
     }

     // ---------- tests ----------

     @Test(expected = MathIllegalStateException.class)
     public void testGetOptimaBeforeOptimizeThrowsException() {
         BaseUnivariateRealOptimizer<UnivariateRealFunction> base =
             new BrentOptimizer(1e-6, 1e-6);
MultiStartUnivariateRealOptimizer<UnivariateRealFunction> optimizer =
             new MultiStartUnivariateRealOptimizer<UnivariateRealFunction>(
                 base, 3, new JDKRandomGenerator());        optimizer.getOptima(); // not called
optimize yet
     }

     /**
      * Zero starts means no attempt to optimize -> ConvergenceException.
      */
     @Test(expected = ConvergenceException.class)
     public void testZeroStartsThrowsException() throws FunctionEvaluationException {
         BaseUnivariateRealOptimizer<UnivariateRealFunction> base =
             new BrentOptimizer(1e-6, 1e-6);
         MultiStartUnivariateRealOptimizer<UnivariateRealFunction> optimizer =
             new MultiStartUnivariateRealOptimizer<UnivariateRealFunction>(
                 base, 0, new JDKRandomGenerator());
         optimizer.optimize(new QuadraticFunction(1, 0), GoalType.MINIMIZE, -10, 10);    }

     /**
      * With a single start the result should be consistent with the underlying
      * optimizer.
  */
  @Test
  public void testSingleStart() throws FunctionEvaluationException {
         QuadraticFunction f = new QuadraticFunction2.0, 3.0); // min at x=3, value 0
         BaseUnivariateRealOptimizer<QuadraticFunction> base = new BrentOptimizer(1e-6, 1e-6);
         base.setMaxEvaluations(100);
         // underlying optimizer alone
         UnivariateRealPointValuePair expected = base.optimize(f, GoalType.MINIMIZE, -50, 50);

         MultiStartUnivariateRealOptimizer<QuadraticFunction> multi =
             new MultiStartUnivariateRealOptimizer<QuadraticFunction>(
                 base, 1, new JDKRandomGenerator());        multi.setMaxEvaluations(200);
         UnivariateRealPointValuePair result = multi.optimize(f, GoalType.MINIMIZE, -50, 50);

         assertEquals(expected.getValue(), result.getValue(), 1e-12);
         assertEquals(expected.getPoint(), result.getPoint(), 1e-12);
         assertEquals(1, multi.getOptima().length);    }

     /**
      * Multiple independent starts on a quintic – should converge to the known
      * analytical minimum (value ≈ -0.2719561293).  This is the trigger condition
      * for MATH-413.
      */
     @Test
     public void testQuinticMin() throws FunctionEvaluationException {
         QuinticFunction f = new QuinticFunction();
         BrentOptimizer base = new BrentOptimizer(1e-6, 1e-6);
         base.setMaxEvaluations(200);
         // fixed seed for determinism
         RandomGenerator rng = new JDKRandomGenerator();        rng.setSeed(123456L);

         MultiStartUnivariateRealOptimizer<QuinticFunction> multi =
             new MultiStartUnivariateRealOptimizer<QuinticFunction>(base,7, rng);
multi.setMaxEvaluations(2000);

         UnivariateRealPointValuePair optimum =
             multi.optimize(f, GoalType.MINIMIZE, -1.0, 1.0);

         // The known global minimum of (x-1)(x-0.5)x(x+0.5)(x+1)
         double expectedValue = -0.2719561293;
         assertEquals(expectedValue, optimum.getValue(), 1e-8);

         UnivariateRealPointValuePair[] optima = multi.getOptima();
         assertEquals(7, optima.length);
         // best point is first element
         assertEquals(expectedValue, optima[0].getValue(), 1e-8);
         // all non-null entries must be ordered (minimization → ascending values)
         checkSortedByValue(optima, GoalType.MINIMIZE);    }

     /**
      * getOptima() sorting correctness: all non-null converged runs must be
      * ordered from best to worst, followed by null entries for non-converged
      * starts.
      */
     @Test
     public void testOptimaSorting() throws FunctionEvaluationException {
         // use a function with several local minima within the bounds
         // sin(100*x) has many local minima; narrow intervals give different values
         SinFunction f = new SinFunction();
         BrentOptimizer base = new BrentOptimizer(1e-6, 1e-6);
         base.setMaxEvaluations(200);

         RandomGenerator rng = new JDKRandomGenerator();        rng.setSeed(999L);
         MultiStartUnivariateRealOptimizer<SinFunction> multi =
             new MultiStartUnivariateRealOptimizer<SinFunction>(base,5, rng);
multi.setMaxEvaluations(1000);

         multi.optimize(f, GoalType.MINIMIZE, -2.0, 2.0);
         UnivariateRealPointValuePair[] optima = multi.getOptima();
         assertEquals(5, optima.length);
         checkSortedByValue(optima, GoalType.MINIMIZE);    }

     /**
      * When min == max the optimizer must return that single point
      * (boundary case).
  */
  @Test
  public void testBoundsEqual() throws FunctionEvaluationException {
         QuadraticFunction f = new QuadraticFunction(1, 5);
         BrentOptimizer base = new BrentOptimizer(1e-6,1e-6);

         MultiStartUnivariateRealOptimizer<QuadraticFunction> multi =
             new MultiStartUnivariateRealOptimizer<QuadraticFunction>(
                 base, 3, new JDKRandomGenerator());        multi.setMaxEvaluations(1000);

         // bounds equal
         UnivariateRealPointValuePair result =
             multi.optimize(f, GoalType.MINIMIZE, 2.0, 2.0);
         assertEquals(2.0, result.getPoint(), 1e-12);
         assertEquals(f.value(2.0), result.getValue(), 1e-12);    }

     /**
      * If maxEvaluations is too small to even finish one start, a
      * ConvergenceException must be thrown.
      */
     @Test(expected = ConvergenceException.class)
     public void testInsufficientEvaluations() throws FunctionEvaluationException {
         QuinticFunction f = new QuinticFunction();
         BrentOptimizer base = new BrentOptimizer(1e-6, 1e-6);
         // this optimizer normally needs at least 10–15 evaluations for convergence
         base.setMaxEvaluations(3);

         MultiStartUnivariateRealOptimizer<QuinticFunction> multi =
             new MultiStartUnivariateRealOptimizer<QuinticFunction>(
                 base,2, new JDKRandomGenerator());        multi.setMaxEvaluations(6);
         multi.optimize(f, GoalType.MINIMIZE, -1, 1);    }

     /**
      * Negative number of starts causes NegativeArraySizeException at construction
      * time (because new UnivariateRealPointValuePair[negative]).
  */
  @Test(expected = NegativeArraySizeException.class)
  public void testNegativeStartsThrowsException() {
         new MultiStartUnivariateRealOptimizer<QuinticFunction>(
             new BrentOptimizer(1e-6,1e-6), -1, new JDKRandomGenerator());    }

     /**
      * Invalid bounds (min > max) must be rejected with an appropriate
      * IllegalArgumentException.
  */
     @Test(expected = IllegalArgumentException.class)
     public void testInvalidBounds() throws FunctionEvaluationException {
         QuadraticFunction f = new QuadraticFunction(1, 0);
         BrentOptimizer base = new BrentOptimizer(1e-6,1e-6);
         MultiStartUnivariateRealOptimizer<QuadraticFunction> multi =
             new MultiStartUnivariateRealOptimizer<QuadraticFunction>(
                 base, 2, new JDKRandomGenerator());        multi.setMaxEvaluations(200);
         // min > max
         multi.optimize(f, GoalType.MINIMIZE, 5, -5);    }

     /**
      * Determinism: two runs with the same seed must produce identical results.
      */
     @Test
     public void testRandomSeedDeterminism() throws FunctionEvaluationException {
         QuinticFunction f = new QuinticFunction();
         long seed = 87654321L;

         BrentOptimizer base1 = new BrentOptimizer(1e-6, 1e-6);
         base1.setMaxEvaluations(200);
         RandomGenerator rng1 = new JDKRandomGenerator();        rng1.setSeed(seed);
         MultiStartUnivariateRealOptimizer<QuinticFunction> multi1 =
             new MultiStartUnivariateRealOptimizer<QuinticFunction>(base1, 8, rng1);
multi1.setMaxEvaluations(2000);
         UnivariateRealPointValuePair result1 =
             multi1.optimize(f, GoalType.MINIMIZE, -1, 1);

         BrentOptimizer base2 = new BrentOptimizer(1e-6, 1e-6);
         base2.setMaxEvaluations(200);
         RandomGenerator rng2 = new JDKRandomGenerator();        rng2.setSeed(seed);
         MultiStartUnivariateRealOptimizer<QuinticFunction> multi2 =
             new MultiStartUnivariateRealOptimizer<QuinticFunction>(base2, 8, rng2);
multi2.setMaxEvaluations(2000);
         UnivariateRealPointValuePair result2 =
             multi2.optimize(f, GoalType.MINIMIZE, -1, 1);

         assertEquals(result1.getValue(), result2.getValue(), 1e-15);
assertEquals(result1.getPoint(), result2.getPoint(), 1e-15);    }

     /**
      * When some starts fail to converge (e.g. because their random interval does
      * not contain the optimum) the optimizer must still return the best result
      * from those that did converge, or throw a ConvergenceException if none
      * converged.
      */
     @Test
     public void testPartialConvergence() throws FunctionEvaluationException {
         QuinticFunction f = new QuinticFunction();
         BrentOptimizer base = new BrentOptimizer(1e-6, 1e-6);
         base.setMaxEvaluations(100);

         // give each start enough evaluations to converge
         MultiStartUnivariateRealOptimizer<QuinticFunction> multi =
             new MultiStartUnivariateRealOptimizer<QuinticFunction>(
                 base, 10, new JDKRandomGenerator());        multi.setMaxEvaluations(1000);

         UnivariateRealPointValuePair result =
             multi.optimize(f, GoalType.MINIMIZE, -1, 1);

         assertNotNull(result);
         // at least some runs converged; the best must be close to global min
         assertEquals(-0.2719561293, result.getValue(), 1e-6);    }

     // ---------- private helpers ----------

     /**
      * Verifies that the array is sorted from best to worst for the given goal,
      * and that null entries (non-converged starts) appear at the end.
      */
     private static void checkSortedByValue(UnivariateRealPointValuePair[] optima,
                                             GoalType goal) {
         int lastNonNull = -1;
         for (int i = 0; i < optima.length; i++) {
             if (optima[i] != null) {
                 if (lastNonNull >= 0) {
                     double prev = optima[lastNonNull].getValue();
                     double cur = optima[i].getValue();
                     if (goal == GoalType.MINIMIZE) {
                         assertTrue("Values not sorted (minimization) at index " + i,
                                    prev <= cur);
                     } else {
                         assertTrue("Values not sorted (maximization) at index " + i,
                                    prev >= cur);
                     }
                 }
                 lastNonNull = i;
             }
         }
         // after first null all remaining must be null
         boolean seenNull = false;
         for (UnivariateRealPointValuePair p : optima) {
             if (p == null) {
                 seenNull = true;
             } else {
                 if (seenNull) {
                     fail("null entries are not grouped at the end");
                 }
             }
         }
     }
 }
