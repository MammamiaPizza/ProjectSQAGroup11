package org.apache.commons.math.optimization;

import static org.junit.Assert.*;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.random.JDKRandomGenerator;
import org.junit.Test;

/**

 - Tests for MultiStartUnivariateRealOptimizer revealing bug MATH-393.
  */
 public class MultiStartUnivariateRealOptimizerTest {
  /** Quintic function whose global minimum on [-1, 1] is known. /
  private static final UnivariateRealFunction FF = new UnivariateRealFunction() {
  public double value(double x) { return (x-1)(x-0.5)x(x+0.5)*(x+1); }
  };
  /** Best known minimum x for F on [-1,1], from high-precision Brent.
  */
  private static final double EXACT_MIN_X = -0.27195612846834;
  /** Tolerance for double comparisons
  */
  private static final double EPS = 1e-8;
  /**
  - Test that getResult returns the optimum with smallest function value
  - across multiple random starts, not the last optimizer's result.
    */
   @Test
   public void testGetResultReturnsBestMinimizer() throws Exception {
   int starts = 10;
   JDKRandomGenerator gen = new JDKRandomGenerator();
   gen.setSeed(1234567890L);
   BrentOptimizer brent = new BrentOptimizer();
   MultiStartUnivariateRealOptimizer ms =
   new MultiStartUnivariateRealOptimizer(brent, starts, gen);
   ms.setAbsoluteAccuracy(1e-12);
   ms.setRelativeAccuracy(1e-12);
   ms.setMaximalIterationCount(1000);
   ms.setMaxEvaluations(1000000);
   double result = ms.optimize(FF, GoalType.MINIMIZE, -1.0, 1.0);
   // The result should be the global minimum with x near EXACT_MIN_X.
   assertEquals(EXACT_MIN_X, result, 1e-6);
   // getResult() must be the same as returned optimum.
   assertEquals(result, ms.getResult(), 1e-15);
  }
  /**
  - Test that getOptima() and getOptimaValues() return arrays of correct size,
  - and the first elements correspond to the best optimum.
    */
   @Test
   public void testGetOptimaBestFirst() throws Exception {
   int starts = 8;
   JDKRandomGenerator gen = new JDKRandomGenerator();
   gen.setSeed(9876543210L);
   BrentOptimizer brent = new BrentOptimizer();
   MultiStartUnivariateRealOptimizer ms =
   new MultiStartUnivariateRealOptimizer(brent, starts, gen);
   ms.setAbsoluteAccuracy(1e-12);
   ms.setRelativeAccuracy(1e-12);
   double result = ms.optimize(FF, GoalType.MINIMIZE, -1.0, 1.0);
   double[] optima = ms.getOptima();
   double[] values = ms.getOptimaValues();
   assertEquals("optima length", starts, optima.length);
   assertEquals("values length", starts, values.length);
   // First element should be the best (lowest value) for minimization.
   assertEquals(result, optima[0], 1e-10);
   assertEquals(FF.value(result), values[0], 1e-8);
  }
  /**
  - Test that getFunctionValue() returns the function value at getResult(),
  - not the value at the last evaluated start point (buggy behavior would fail).
    */
   @Test
   public void testGetFunctionValueConsistentWithResult() throws Exception {
   int starts = 6;
   JDKRandomGenerator gen = new JDKRandomGenerator();
   gen.setSeed(55556666L);
   BrentOptimizer brent = new BrentOptimizer();
   MultiStartUnivariateRealOptimizer ms =
   new MultiStartUnivariateRealOptimizer(brent, starts, gen);
   ms.setAbsoluteAccuracy(1e-10);
   ms.setRelativeAccuracy(1e-10);
   ms.optimize(FF, GoalType.MINIMIZE, -1.0, 1.0);
   double result = ms.getResult();
   double funcValue = ms.getFunctionValue();
   // Should be exactly f(result), but buggy code delegates to last optimizer
   assertEquals(FF.value(result), funcValue, 1e-8);
  }
  /**
  - Test that for minimization, the function value at the returned optimum
  - is the smallest among all starts.
    */
   @Test
   public void testResultValueIsMinimumAmongStarts() throws Exception {
   int starts = 7;
   JDKRandomGenerator gen = new JDKRandomGenerator();
   gen.setSeed(11112222L);
   BrentOptimizer brent = new BrentOptimizer();
   MultiStartUnivariateRealOptimizer ms =
   new MultiStartUnivariateRealOptimizer(brent, starts, gen);
   ms.setAbsoluteAccuracy(1e-10);
   ms.setRelativeAccuracy(1e-10);
   ms.optimize(FF, GoalType.MINIMIZE, -1.0, 1.0);
   double bestValue = ms.getOptimaValues()[0];
   double resultValue = FF.value(ms.getResult());
   // They should match: the best value is the value at the best point.
   assertEquals(bestValue, resultValue, 1e-10);
  }
  /**
  - Test that a single start returns the correct optimum and getResult()
  - agrees with the optimizer's own result.
    */
   @Test
   public void testSingleStart() throws Exception {
   JDKRandomGenerator gen = new JDKRandomGenerator();
   gen.setSeed(1L);
   BrentOptimizer brent = new BrentOptimizer();
   MultiStartUnivariateRealOptimizer ms =
   new MultiStartUnivariateRealOptimizer(brent, 1, gen);
   ms.setAbsoluteAccuracy(1e-10);
   ms.setRelativeAccuracy(1e-10);
   double result = ms.optimize(FF, GoalType.MINIMIZE, -1.0, 1.0);
   assertEquals(result, ms.getResult(), 1e-15);
   assertEquals(1, ms.getOptima().length);
   assertEquals(1, ms.getOptimaValues().length);
   assertEquals(result, ms.getOptima()[0], 1e-15);
  }
  /**
  - Zero starts should throw an OptimizationException (or other exception)
  - because no optimum can be computed.
    */
   @Test(expected = IllegalArgumentException.class)
   public void testZeroStartsThrows() throws Exception {
   JDKRandomGenerator gen = new JDKRandomGenerator();
   BrentOptimizer brent = new BrentOptimizer();
   MultiStartUnivariateRealOptimizer ms =
   new MultiStartUnivariateRealOptimizer(brent, 0, gen);
   ms.optimize(FF, GoalType.MINIMIZE, -1.0, 1.0);
   // The current code does not validate starts > 0, leading to
   // ArrayIndexOutOfBoundsException. After fix, it should throw
   // IllegalArgumentException (or similar).
   }
  /**
  - Test that iteration and evaluation counts are accumulated correctly
  - across all starts.
    */
   @Test
   public void testCountersAccumulating() throws Exception {
   int starts = 3;
   JDKRandomGenerator gen = new JDKRandomGenerator();
   gen.setSeed(444555L);
   BrentOptimizer brent = new BrentOptimizer();
   MultiStartUnivariateRealOptimizer ms =
   new MultiStartUnivariateRealOptimizer(brent, starts, gen);
   ms.setAbsoluteAccuracy(1e-10);
   ms.setRelativeAccuracy(1e-10);
   ms.setMaximalIterationCount(500);
   ms.setMaxEvaluations(5000);
   ms.optimize(FF, GoalType.MINIMIZE, -1.0, 1.0);
   assertTrue("some iterations", ms.getIterationCount() > 0);
   assertTrue("some evaluations", ms.getEvaluations() > 0);
   assertTrue("iterations <= max", ms.getIterationCount() <= ms.getMaximalIterationCount());
   assertTrue("evaluations <= max", ms.getEvaluations() <= ms.getMaxEvaluations());
  }
  /**
  - Verify that setAbsoluteAccuracy / setRelativeAccuracy / setMaxEvaluations
  - propagate through to the underlying optimizer or the multi-start wrapper.
    */
   @Test
   public void testConfigurationMethods() {
   BrentOptimizer brent = new BrentOptimizer();
   MultiStartUnivariateRealOptimizer ms =
   new MultiStartUnivariateRealOptimizer(brent, 2, new JDKRandomGenerator());
   ms.setAbsoluteAccuracy(1e-9);
   assertEquals(1e-9, ms.getAbsoluteAccuracy(), 0.);
   ms.resetAbsoluteAccuracy();
   // after reset, it returns the underlying default (Brent default)
   assertTrue(ms.getAbsoluteAccuracy() > 0);
   ms.setRelativeAccuracy(1e-12);
   assertEquals(1e-12, ms.getRelativeAccuracy(), 0.);
   ms.resetRelativeAccuracy();
   assertTrue(ms.getRelativeAccuracy() > 0);
   ms.setMaxEvaluations(1500);
   assertEquals(1500, ms.getMaxEvaluations());
   ms.setMaximalIterationCount(100);
   assertEquals(100, ms.getMaximalIterationCount());
  }

}
