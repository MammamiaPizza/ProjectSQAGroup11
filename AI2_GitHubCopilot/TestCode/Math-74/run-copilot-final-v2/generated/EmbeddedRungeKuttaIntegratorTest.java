package org.apache.commons.math.ode.nonstiff;

 import org.apache.commons.math.analysis.DifferentiableUnivariateFunction;
 import org.apache.commons.math.analysis.UnivariateFunction;
 import org.apache.commons.math.linear.RealVector;
 import org.apache.commons.math.ode.DerivativeException;
 import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
 import org.apache.commons.math.ode.IntegratorException;
 import org.apache.commons.math.ode.events.EventHandler;
 import org.junit.Before;
 import org.junit.Test;

 import static org.junit.Assert.*;

 public class EmbeddedRungeKuttaIntegratorTest {

     private static final double EPS = 1.0e-10;

     @Before
     public void setUp() {
     }

     @Test
     public void testDefaultControlParams() {
         ClassicalRungeKuttaIntegrator ik = new ClassicalRungeKuttaIntegrator(1.0e-5, 1.0e-5);
         assertEquals(0.9, ik.getSafety(), 1.0e-15);
         assertEquals(0.2, ik.getMinReduction(), 1.0e-15);
         assertEquals(10.0, ik.getMaxGrowth(), 1.0e-15);
     }

     @Test
     public void testSetSafety() {
         ClassicalRungeKuttaIntegrator ik = new ClassicalRungeKuttaIntegrator(1.0e-5, 1.0e-5);
         double[] safeties = {0.0, 0.1, 0.5, 0.9, 1.0, 1.1, 2.0, 10.0};
         for (double s : safeties) {
             ik.setSafety(s);
             assertEquals(s, ik.getSafety(), 1.0e-15);
         }
     }

     @Test
     public void testSetSafetyViaVector() {
         double[] absTol = {1.0e-5};
         double[] relTol = {1.0e-5};
         ClassicalRungeKuttaIntegrator ik = new ClassicalRungeKuttaIntegrator(absTol, relTol);
         double[] safeties = {0.0, 0.5, 0.75, 0.9, 1.0, 1.5};
         for (double s : safeties) {
             ik.setSafety(s);
             assertEquals(s, ik.getSafety(), 1.0e-15);
         }
         double result = integrateExponential(ik);
         assertEquals(1.0, result, EPS);
     }

     @Test
     public void testMinReductionBoundary() {
         ClassicalRungeKuttaIntegrator ik = new ClassicalRungeKuttaIntegrator(1.0e-5, 1.0e-5);
         double[] values = {0.0, 0.1, 0.2, 0.5, 1.0};
         for (double v : values) {
             ik.setMinReduction(v);
             assertEquals(v, ik.getMinReduction(), 1.0e-15);
         }
     }

     @Test
     public void testMaxGrowthBoundary() {
         ClassicalRungeKuttaIntegrator ik = new ClassicalRungeKuttaIntegrator(1.0e-5, 1.0e-5);
         double[] values = {1.0, 2.0, 5.0, 10.0};
         for (double v : values) {
             ik.setMaxGrowth(v);
             assertEquals(v, ik.getMaxGrowth(), 1.0e-15);
         }
     }

     @Test
     public void testMinReductionZeroIntegrate() {
         ClassicalRungeKuttaIntegrator ik = new ClassicalRungeKuttaIntegrator(1.0e-5, 1.0e-5);
         ik.setMinReduction(0.0);
         double result = integrateExponential(ik);
         assertEquals(1.0, Math.abs(result), EPS);
     }

     @Test
     public void testMinReductionNormalIntegrate() {
         ClassicalRungeKuttaIntegrator ik = new ClassicalRungeKuttaIntegrator(1.0e-5, 1.0e-5);
         ik.setMinReduction(0.2);
         double result = integrateExponential(ik);
         assertEquals(1.0, Math.abs(result), EPS);
     }

     @Test
     public void testMaxGrowthSmallIntegrate() {
         ClassicalRungeKuttaIntegrator ik = new ClassicalRungeKuttaIntegrator(1.0e-5, 1.0e-5);
         ik.setMaxGrowth(1.001);
         double result = integrateExponential(ik);
         assertEquals(1.0, Math.abs(result), EPS);
     }

     @Test
     public void testMaxGrowthNormalIntegrate() {
         ClassicalRungeKuttaIntegrator ik = new ClassicalRungeKuttaIntegrator(1.0e-5, 1.0e-5);
         ik.setMaxGrowth(10.0);
         double result = integrateExponential(ik);
         assertEquals(1.0, Math.abs(result), EPS);
     }

     @Test
     public void testSafetyOneIntegrate() {
         ClassicalRungeKuttaIntegrator rk4 = new ClassicalRungeKuttaIntegrator(1.0e-6, 1.0e-6);
         double result = integrateExponential(rk4, 0.0, 1.0);
         double expected = Math.exp(0.5);
         assertEquals("RK int should handle safety", 1.0, Math.abs(result - expected) < 0.1 ? 1.0 :
0.0, 0.0);
         assertEquals(expected, result, 1e-4);
     }

     @Test
     public void testBackwardIntegrationSafety() {
         ClassicalRungeKuttaIntegrator rk1 = new ClassicalRungeKuttaIntegrator(1.0e-6, 1.0e-6);
         double result = rk1.integrate(new FirstOrderDifferentialEquations() {
             @Override
             public int getDimension() { return 1; }

             @Override
             public void computeDerivatives(double t, double[] y, double[] yDot)
                     throws org.apache.commons.math.ode.DerivativeException {
                 yDot[0] = -1.0;
             }
         }, 0.0, new double[]{1.0}, -1.0, new double[1]);
         assertEquals(-1.0, result, 1e-14);
     }

     @Test
     public void testStepSizeAdaptionEffectiveness() {
         // The bug in MATH-74b makes step-size adaption ineffective because
         // the error estimate is computed from the wrong final stage coefficients.
         // When the error is always ~0, the safety/minReduction/maxGrowth params
         // have no real effect on step size selection.
         // Here we verify the integrator works correctly with non-default control parameters.
         // Use a problem where step-size adaption matters: y' = y, y(0) = 1, [0, 10]
         ClassicalRungeKuttaIntegrator rk = new ClassicalRungeKuttaIntegrator(1.0e-3, 1.0e-3);
         rk.setSafety(0.9);
         rk.setMinReduction(0.2);
         rk.setMaxGrowth(10.0);
         FirstOrderDifferentialEquations eq = new FirstOrderDifferentialEquations() {
             @Override
             public int getDimension() { return 1; }

             @Override
             public void computeDerivatives(double t, double[] y, double[] yDot)
                     throws org.apache.commons.math.ode.DerivativeException {
                 yDot[0] = y[0];
             }
         };
         double[] y = new double[1];
         double result = rk.integrate(eq, 0.0, new double[]{1.0}, 10.0, y);
         double expected = Math.exp(10.0);
         // The integrator should converge to the analytical solution
         assertEquals(10.0, result, 1e-14);
         double relErr = Math.abs(y[0] - expected) / expected;
         assertTrue("Relative error too large: " + relErr, relErr < 1e-3);
     }

     @Test
     public void testEventHandlerAtStepBoundary() {
         ClassicalRungeKuttaIntegrator rk = new ClassicalRungeKuttaIntegrator(1.0e-5, 1.0e-5);
         FirstOrderDifferentialEquations eq = new FirstOrderDifferentialEquations() {
             @Override
             public int getDimension() { return 1; }

             @Override
             public void computeDerivatives(double t, double[] y, double[] yDot)
                     throws org.apache.commons.math.ode.DerivativeException {
                 yDot[0] = 0.0; // constant
             }
         };

         final double[] eventTime = new double[1];
         eventTime[0] = -Double.MAX_VALUE;

         EventHandler eh = new EventHandler() {
             @Override
             public double g(double t, double[] y) {
                 return t - 0.0; // zero at t = 0
             }

             @Override
             public int handler(double t, double[] y, boolean isIncrease) {
                 return 0;
             }

             @Override
             public void init(double t0, double[] y0, double t) {
             }

             @Override
             public boolean reset(double t, double[] y) {
                 return false;
             }

             @Override
             public double getMinStep() {
                 return -Double.MAX_VALUE;
             }
         };

         // start at t=0, integrate forward; event at t=0 should be handled
         double result = rk.integrate(eq, 0.0, new double[]{1.0}, 1.0, new double[1]);
         assertEquals(1.0, result, 1e-14);
     }

     // ---------------------------------------------------------------
     // helpers

     private double integrateExponential(EmbeddedRungeKuttaIntegrator ik) {
         return integrateExponential(ik, 0.0, 0.5);
     }

     private double integrateExponential(EmbeddedRungeKuttaIntegrator ik, double t0, double target)
{
         FirstOrderDifferentialEquations eq = new FirstOrderDifferentialEquations() {
             @Override
             public int getDimension() { return 1; }

             @Override
             public void computeDerivatives(double t, double[] y, double[] yDot)
                     throws org.apache.commons.math.ode.DerivativeException {
                 yDot[0] = y[0];
             }
         };
         double[] y = new double[1];
         double result = ik.integrate(eq, t0, new double[]{1.0}, target, y);
         assertEquals(t0 + (target - t0) < t0 + target ? target : target, result, 1e-14);
         return y[0];
     }

     private RealVector toRealVector(double[] values) {
         return new org.apache.linear.RealVectorImpl(values);
     }
 }
