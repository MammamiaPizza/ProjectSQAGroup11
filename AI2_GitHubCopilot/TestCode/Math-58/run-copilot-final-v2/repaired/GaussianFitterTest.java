package org.apache.commons.math.optimization.fitting;

import static org.junit.Assert.*;
import org.junit.Test;
import org.apache.commons.math.analysis.function.Gaussian;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.NumberIsTooSmallException;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;
import org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction;
import org.apache.commons.math.optimization.VectorialPointValuePair;
import org.apache.commons.math.optimization.OptimizationException;

public class GaussianFitterTest {

 private static class MockOptimizer implements DifferentiableMultivariateVectorialOptimizer {
     public VectorialPointValuePair optimize(int maxEvaluations,
                                             DifferentiableMultivariateVectorialFunction objective,
                                             double[] target,
                                             double[] weight,
                                             double[] startPoint)
             throws OptimizationException {
         double[] values = objective.value(startPoint);
         return new VectorialPointValuePair(startPoint, values);
     }
 }

 @Test(expected = NullArgumentException.class)
 public void testParameterGuesserNullObservations() {
     new GaussianFitter.ParameterGuesser(null);
 }

 @Test(expected = NumberIsTooSmallException.class)
 public void testParameterGuesserInsufficientPoints() {
     WeightedObservedPoint[] points = new WeightedObservedPoint[] {
         new WeightedObservedPoint(1.0, 0.0, 1.0),
         new WeightedObservedPoint(1.0, 1.0, 2.0)
     };
     new GaussianFitter.ParameterGuesser(points);
 }

 @Test
 public void testParameterGuesserValidData() {
     WeightedObservedPoint[] points = new WeightedObservedPoint[] {
         new WeightedObservedPoint(1.0, 0.0, 0.1),
         new WeightedObservedPoint(1.0, 1.0, 0.5),
         new WeightedObservedPoint(1.0, 2.0, 1.0),
         new WeightedObservedPoint(1.0, 3.0, 0.5),
         new WeightedObservedPoint(1.0, 4.0, 0.1)
     };
     double[] guess = new GaussianFitter.ParameterGuesser(points).guess();
     assertEquals(3, guess.length);
     assertTrue("amplitude should be non-negative", guess[0] >= 0.0);
     assertTrue("sigma should be positive", guess[2] > 0.0);
 }

 @Test
 public void testParameterGuesserAllSameX() {
     // sigma can be non-positive due to the bug
     WeightedObservedPoint[] points = new WeightedObservedPoint[] {
         new WeightedObservedPoint(1.0, 0.0, 1.0),
         new WeightedObservedPoint(1.0, 0.0, 5.0),
         new WeightedObservedPoint(1.0, 0.0, 2.0)
     };
     double[] guess = new GaussianFitter.ParameterGuesser(points).guess();
     assertTrue("sigma must be strictly positive, bug if <=0", guess[2] > 0.0);
 }

 @Test
 public void testParameterGuesserMonotonicIncreasing() {
     WeightedObservedPoint[] points = new WeightedObservedPoint[] {
         new WeightedObservedPoint(1.0, 0.0, 1.0),
         new WeightedObservedPoint(1.0, 1.0, 2.0),
         new WeightedObservedPoint(1.0, 2.0, 3.0)
     };
     double[] guess = new GaussianFitter.ParameterGuesser(points).guess();
     assertTrue("sigma should be positive", guess[2] > 0.0);
 }

 @Test
 public void testParameterGuesserMinimalThreePoints() {
     WeightedObservedPoint[] points = new WeightedObservedPoint[] {
         new WeightedObservedPoint(1.0, 0.0, 0.0),
         new WeightedObservedPoint(1.0, 1.0, 10.0),
         new WeightedObservedPoint(1.0, 2.0, 0.0)
     };
     double[] guess = new GaussianFitter.ParameterGuesser(points).guess();
     assertTrue("sigma should be positive", guess[2] > 0.0);
 }

 @Test
 public void testFitWithValidData() throws Exception {
     GaussianFitter fitter = new GaussianFitter(new MockOptimizer());
     fitter.addObservedPoint(new WeightedObservedPoint(1.0, 0.0, 0.1));
     fitter.addObservedPoint(new WeightedObservedPoint(1.0, 1.0, 0.5));
     fitter.addObservedPoint(new WeightedObservedPoint(1.0, 2.0, 1.0));
     fitter.addObservedPoint(new WeightedObservedPoint(1.0, 3.0, 0.5));
     fitter.addObservedPoint(new WeightedObservedPoint(1.0, 4.0, 0.1));
     double[] fitted = fitter.fit();
     assertNotNull(fitted);
     assertEquals(3, fitted.length);
     assertTrue("fitted amplitude should be non-negative", fitted[0] >=0.0);
     assertTrue("fitted sigma should be positive", fitted[2] >0.0);
 }

 @Test(expected = NotStrictlyPositiveException.class)
 public void testFitAllSameXThrows() throws Exception {
     GaussianFitter fitter = new GaussianFitter(new MockOptimizer());
     fitter.addObservedPoint(new WeightedObservedPoint(1.0, 0.0, 1.0));
     fitter.addObservedPoint(new WeightedObservedPoint(1.0, 0.0, 5.0));
     fitter.addObservedPoint(new WeightedObservedPoint(1.0, 0.0,2.0));
     fitter.fit(); // basicGuess produces sigma = 0 => evaluation throws
 }

 @Test
 public void testFitWithInitialGuessNegativeSigma() throws Exception {
     GaussianFitter fitter = new GaussianFitter(new MockOptimizer());
     fitter.addObservedPoint(new WeightedObservedPoint(1.0, 0.0, 1.0));
     fitter.addObservedPoint(new WeightedObservedPoint(1.0, 1.0, 2.0));
     fitter.addObservedPoint(new WeightedObservedPoint(1.0, 2.0, 1.0));
     double[] guess = new double[] { 1.0, 1.0, -1.0 };
     fitter.fit(guess); // wrapper should catch NotStrictlyPositiveException internally
 }

 @Test(expected = NotStrictlyPositiveException.class)
 public void testValueNegativeSigmaThrows() {
     Gaussian.Parametric g = new Gaussian.Parametric();
     g.value(0.0, new double[] { 1.0, 0.0, -1.0 });
 }

 @Test
 public void testGradientMatchesAnalytic() {
     Gaussian.Parametric g = new Gaussian.Parametric();
     double a = 2.5, b = 1.0, c = 3.0, x = 2.0;
     double[] p = new double[] { a, b, c };
     double f = a * Math.exp(-((x - b) * (x - b)) / (2.0 * c * c));
     double[] expected = new double[] {
         f / a,
         f * (x - b) / (c * c),
         f * (x - b) * (x - b) / (c * c * c)
     };
     double[] actual = g.gradient(x, p);
     assertArrayEquals("gradient differs", expected, actual, 1e-12);
 }

 @Test(expected = NotStrictlyPositiveException.class)
 public void testGradientNegativeSigmaThrows() {
     Gaussian.Parametric g = new Gaussian.Parametric();
     g.gradient(0.0, new double[] { 1.0, 0.0, -0.5 });
 }

 private static void assertArrayEquals(String message, double[] expected, double[] actual,

double tol) {
        assertEquals(message + ": array lengths differ", expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(message + " at index " + i, expected[i], actual[i], tol);
        }
    }
}
