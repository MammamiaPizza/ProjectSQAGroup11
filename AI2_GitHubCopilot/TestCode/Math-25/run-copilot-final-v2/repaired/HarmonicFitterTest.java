package org.apache.commons.math3.optimization.fitting;

 import static org.junit.Assert.*;
 import org.junit.Before;
 import org.junit.Test;

 import org.apache.commons.math3.exception.MathIllegalStateException;
 import org.apache.commons.math3.exception.NumberIsTooSmallException;
 import org.apache.commons.math3.exception.ZeroException;
 import org.apache.commons.math3.optimization.DifferentiableMultivariateVectorOptimizer;
 import org.apache.commons.math3.optimization.general.LevenbergMarquardtOptimizer;
 import org.apache.commons.math3.analysis.function.HarmonicOscillator;
 import org.apache.commons.math3.util.FastMath;

 public class HarmonicFitterTest {

     private HarmonicFitter fitter;
     private DifferentiableMultivariateVectorOptimizer optimizer;

     @Before
     public void setUp() {
         optimizer = new LevenbergMarquardtOptimizer();
         fitter = new HarmonicFitter(optimizer);
     }

     @Test(expected = MathIllegalStateException.class)
     public void testMath844() {
         fitter.addObservedPoint(0.0, 0.0, 1.0);
         fitter.addObservedPoint(0.0, 1.0, 1.0);
         fitter.addObservedPoint(0.0, 2.0, 1.0);
         fitter.addObservedPoint(0.0, 3.0, 1.0);
         fitter.addObservedPoint(0.0, 4.0, 1.0);
         fitter.fit();
     }

     @Test(expected = NumberIsTooSmallException.class)
     public void testParameterGuesserRejectsZeroPoints() {
         new HarmonicFitter.ParameterGuesser(new WeightedObservedPoint[] {});
     }

     @Test(expected = NumberIsTooSmallException.class)
     public void testParameterGuesserRejectsOnePoint() {
         WeightedObservedPoint[] points = {
             new WeightedObservedPoint(1.0, 0.0, 0.0)
         };
         new HarmonicFitter.ParameterGuesser(points);
     }

     @Test(expected = NumberIsTooSmallException.class)
     public void testParameterGuesserRejectsTwoPoints() {
         WeightedObservedPoint[] points = {
             new WeightedObservedPoint(1.0, 0.0, 0.0),
             new WeightedObservedPoint(1.0, 1.0, 1.0)
         };
         new HarmonicFitter.ParameterGuesser(points);
     }

     @Test(expected = NumberIsTooSmallException.class)
     public void testParameterGuesserRejectsThreePoints() {
         WeightedObservedPoint[] points = {
             new WeightedObservedPoint(1.0, 0.0, 0.0),
             new WeightedObservedPoint(1.0, 1.0, 1.0),
             new WeightedObservedPoint(1.0, 2.0, 2.0)
         };
         new HarmonicFitter.ParameterGuesser(points);
     }

     @Test
     public void testParameterGuesserWithFourPoints() {
         WeightedObservedPoint[] points = {
             new WeightedObservedPoint(1.0, 0.0, 0.0),
             new WeightedObservedPoint(1.0, Math.PI / 2, 1.0),
             new WeightedObservedPoint(1.0, Math.PI, 0.0),
             new WeightedObservedPoint(1.0, 3 * Math.PI / 2, -1.0)
         };
         HarmonicFitter.ParameterGuesser guesser = new HarmonicFitter.ParameterGuesser(points);
         double[] guess = guesser.guess();
         assertNotNull(guess);
         assertEquals(3, guess.length);
         for (double v : guess) {
             assertFalse(Double.isNaN(v));
             assertFalse(Double.isInfinite(v));
         }
     }

     @Test
     public void testFitWithExactSine() {
         double a = 2.0;
         double omega = 2 * Math.PI * 0.5; // pi
         double phi = Math.PI / 4;
         for (double t = 0.0; t <= 6.0; t += 0.3) {
             double y = a * Math.sin(omega * t + phi);
             fitter.addObservedPoint(t, y, 1.0);
         }
         double[] fitted = fitter.fit(new double[] {2.0, Math.PI, Math.PI / 4});
         assertEquals("Amplitude", a, fitted[0], 0.3);
         assertEquals("Omega", omega, fitted[1], 0.3);
     }

     @Test
     public void testFitWithConstantY() {
         for (double t = 0.0; t <= 5.0; t += 0.5) {
             fitter.addObservedPoint(t, 3.0, 1.0);
         }
         double[] fitted = fitter.fit(new double[] {0.1, 1.0, 0.0});
         assertTrue("Amplitude near 0 for constant data", Math.abs(fitted[0]) < 0.5);
     }

     @Test
     public void testFitWithInitialGuess() {
         double a = 1.5;
         double omega = 2.0;
         double phi = 0.5;
         for (double t = 0.0; t <= 6.0; t += 0.3) {
             double y = a * Math.sin(omega * t + phi);
             fitter.addObservedPoint(t, y, 1.0);
         }
         double[] initialGuess = { 1.0, 1.5, 0.0 };
         double[] fitted = fitter.fit(initialGuess);
         assertNotNull(fitted);
         assertEquals(3, fitted.length);
     }

     @Test
     public void testFitNoObservationsThrows() {
         try {
             fitter.fit();
             fail("Expected exception for empty sample");
         } catch (Exception e) {
             // pass – any exception is acceptable here
         }
     }

     @Test
     public void testGuessWithUnsortedPoints() {
         fitter.addObservedPoint(3 * Math.PI / 2, -1.0, 1.0);
         fitter.addObservedPoint(Math.PI / 2, 1.0, 1.0);
         fitter.addObservedPoint(Math.PI, 0.0, 1.0);
         fitter.addObservedPoint(0.0, 0.0, 1.0);
         double[] guess = fitter.fit();
         assertNotNull(guess);
         assertEquals(3, guess.length);
     }
 }
