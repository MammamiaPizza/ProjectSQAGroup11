import java.util.Arrays;
 import java.util.Random;

 import org.apache.commons.math3.analysis.MultivariateFunction;
 import org.apache.commons.math3.analysis.MultivariateVectorFunction;
 import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
 import org.apache.commons.math3.exception.TooManyEvaluationsException;
 import org.apache.commons.math3.linear.ArrayRealVector;
 import org.apache.commons.math3.linear.RealMatrix;
 import org.apache.commons.math3.linear.RealVector;
 import org.apache.commons.math3.optim.InitialGuess;
 import org.apache.commons.math3.optim.MaxEval;
 import org.apache.commons.math3.optim.MaxIter;
 import org.apache.commons.math3.optim.PointValuePair;
 import org.apache.commons.math3.optim.PointVectorValuePair;
 import org.apache.commons.math3.optim.SimpleValueChecker;
 import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
 import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
 import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
 import
org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
 import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer;
 import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
 import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer;
 import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
 import org.apache.commons.math3.optim.nonlinear.vector.ModelFunction;
 import org.apache.commons.math3.optim.nonlinear.vector.ModelFunctionJacobian;
 import org.apache.commons.math3.optim.nonlinear.vector.Target;
 import org.apache.commons.math3.optim.nonlinear.vector.Weight;
 import org.apache.commons.math3.optim.nonlinear.vector.jacobian.GaussNewtonOptimizer;
 import org.apache.commons.math3.optim.nonlinear.vector.jacobian.LevenbergMarquardtOptimizer;
 import org.apache.commons.math3.random.MersenneTwister;
 import org.junit.Assert;
 import org.junit.Test;

 /**
  * Tests targeting BaseOptimizer.incrementIterationCount() / incrementEvaluationCount()
  * through various optimizers (MATH-949 regression).
  */
 public class OptimizerIncrementTest {

     // ----- NonLinearConjugateGradient --------------------------------------------------
     @Test
     public void testNonLinearConjugateGradientTrivial() {
         NonLinearConjugateGradientOptimizer optimizer =
             new NonLinearConjugateGradientOptimizer(
                 NonLinearConjugateGradientOptimizer.Formula.FLETCHER_REEVES,
                 new SimpleValueChecker(1e-10, 1e-10));

         // f(x) = (x-1)^2, minimum at x=1, value 0
         MultivariateFunction func = new MultivariateFunction() {
             public double value(double[] x) { return (x[0] - 1) * (x[0] - 1); }
         };
         MultivariateFunction grad = new MultivariateFunction() {
             public double[] value(double[] x) { return new double[] { 2 * (x[0] - 1) }; }
         };

         PointValuePair result = optimizer.optimize(
             new ObjectiveFunction(func),
             new ObjectiveFunctionGradient(grad),
             GoalType.MINIMIZE,
             new InitialGuess(new double[] { 2.0 }),
             new MaxEval(1000),
             new MaxIter(1000));

         Assert.assertEquals(0.0, result.getValue(), 1e-6);
         Assert.assertArrayEquals(new double[] { 1.0 }, result.getPoint(), 1e-6);
         Assert.assertTrue("iterations must be > 0", optimizer.getIterations() > 0);
         Assert.assertTrue("evaluations must be > 0", optimizer.getEvaluations() > 0);
     }

     // ----- CMA-ES --------------------------------------------------------------------
     @Test
     public void testCMAESRosenbrock() {
         CMAESOptimizer optimizer = createCMAES();

         // Rosenbrock: f = 100*(y-x^2)^2 + (1-x)^2, optimum [1,1] value 0
         MultivariateFunction rosen = new MultivariateFunction() {
             public double value(double[] x) {
                 double a = x[1] - x[0] * x[0];
                 double b = 1 - x[0];
                 return 100 * a * a + b * b;
             }
         };

         PointValuePair result = optimizer.optimize(
             new ObjectiveFunction(rosen),
             GoalType.MINIMIZE,
             new InitialGuess(new double[] { -1.2, 1.0 }),
             new CMAESOptimizer.Sigma(new double[] { 0.3, 0.3 }),
             new MaxEval(100000),
             new MaxIter(100000));

         Assert.assertTrue("value should be near zero", Math.abs(result.getValue()) < 1e-4);
         double[] pt = result.getPoint();
         Assert.assertEquals(1.0, pt[0], 1e-3);
         Assert.assertEquals(1.0, pt[1], 1e-3);
         Assert.assertTrue("iterations must be > 0", optimizer.getIterations() > 0);
         Assert.assertTrue("evaluations must be > 0", optimizer.getEvaluations() > 0);
     }

     @Test
     public void testCMAESSphere() {
         CMAESOptimizer optimizer = createCMAES();

         // Sphere: sum of squares, optimum [0,0] value 0
         MultivariateFunction sphere = new MultivariateFunction() {
             public double value(double[] x) { return x[0]*x[0] + x[1]*x[1]; }
         };

         PointValuePair result = optimizer.optimize(
             new ObjectiveFunction(sphere),
             GoalType.MINIMIZE,
             new InitialGuess(new double[] { 5.0, 5.0 }),
             new CMAESOptimizer.Sigma(new double[] { 1.0, 1.0 }),
             new MaxEval(100000),
             new MaxIter(100000));

         Assert.assertTrue("value should be near zero", Math.abs(result.getValue()) < 1e-4);
         double[] pt = result.getPoint();
         Assert.assertEquals(0.0, pt[0], 1e-3);
         Assert.assertEquals(0.0, pt[1], 1e-3);
         Assert.assertTrue("iterations must be > 0", optimizer.getIterations() > 0);
     }

     @Test
     public void testCMAESConstrainedRosen() {
         CMAESOptimizer optimizer = createCMAES();

         MultivariateFunction rosen = new MultivariateFunction() {
             public double value(double[] x) {
                 double a = x[1] - x[0] * x[0];
                 double b = 1 - x[0];
                 return 100 * a * a + b * b;
             }
         };

         // simple bounds: x in [-1,2], y in [-1,2]
         PointValuePair result = optimizer.optimize(
             new ObjectiveFunction(rosen),
             GoalType.MINIMIZE,
             new InitialGuess(new double[] { 0.0, 0.0 }),
             new CMAESOptimizer.Sigma(new double[] { 0.5, 0.5 }),
             new CMAESOptimizer.SimpleBounds(
                 new double[] { -1.0, -1.0 },
                 new double[] {  2.0,  2.0 }),
             new MaxEval(100000),
             new MaxIter(100000));

         Assert.assertTrue("value should be small", Math.abs(result.getValue()) < 1e-2);
         double[] pt = result.getPoint();
         Assert.assertEquals(1.0, pt[0], 0.1);
         Assert.assertEquals(1.0, pt[1], 0.1);
         Assert.assertTrue("iterations must be > 0", optimizer.getIterations() > 0);
     }

     @Test
     public void testCMAESStartAtOptimum() {
         CMAESOptimizer optimizer = createCMAES();

         // Simple quadratic: f(x)=x^2+ (y-1)^2, optimum [0,1] value 0
         MultivariateFunction func = new MultivariateFunction() {
             public double value(double[] x) { return x[0]*x[0] + (x[1]-1)*(x[1]-1); }
         };

         PointValuePair result = optimizer.optimize(
             new ObjectiveFunction(func),
             GoalType.MINIMIZE,
             new InitialGuess(new double[] { 0.0, 1.0 }),   // start at optimum
             new CMAESOptimizer.Sigma(new double[] { 0.1, 0.1 }),
             new MaxEval(10000),
             new MaxIter(10000));

         double[] pt = result.getPoint();
         Assert.assertEquals(0.0, pt[0], 1e-6);
         Assert.assertEquals(1.0, pt[1], 1e-6);
         Assert.assertTrue("value at optimum", Math.abs(result.getValue()) < 1e-6);
     }

     @Test(expected = TooManyEvaluationsException.class)
     public void testCMAESMaxEvaluationsExceeded() {
         CMAESOptimizer optimizer = createCMAES();

         MultivariateFunction rosen = new MultivariateFunction() {
             public double value(double[] x) {
                 double a = x[1] - x[0] * x[0];
                 double b = 1 - x[0];
                 return 100 * a * a + b * b;
             }
         };

         optimizer.optimize(
             new ObjectiveFunction(rosen),
             GoalType.MINIMIZE,
             new InitialGuess(new double[] { 0.0, 0.0 }),
             new CMAESOptimizer.Sigma(new double[] { 1.0, 1.0 }),
             new MaxEval(1),   // force failure
             new MaxIter(10000));
     }

     // ----- Powell --------------------------------------------------------------------
     @Test
     public void testPowellSumSinc() {
         PowellOptimizer optimizer = new PowellOptimizer(
             1e-12, 1e-12, 1e-12, 1e-12,
             new SimpleValueChecker(1e-12, 1e-12));

         // simple quadratic f(x,y)= (x-3)^2 + (y-4)^2
         MultivariateFunction func = new MultivariateFunction() {
             public double value(double[] x) { return (x[0]-3)*(x[0]-3) + (x[1]-4)*(x[1]-4); }
         };

         PointValuePair result = optimizer.optimize(
             new ObjectiveFunction(func),
             GoalType.MINIMIZE,
             new InitialGuess(new double[] { 0.0, 0.0 }),
             new MaxEval(1000),
             new MaxIter(1000));

         Assert.assertTrue("value should be small", Math.abs(result.getValue()) < 1e-6);
         double[] pt = result.getPoint();
         Assert.assertEquals(3.0, pt[0], 1e-4);
         Assert.assertEquals(4.0, pt[1], 1e-4);
         Assert.assertTrue("iterations must be > 0", optimizer.getIterations() > 0);
         Assert.assertTrue("evaluations must be > 0", optimizer.getEvaluations() > 0);
     }

     // ----- Simplex -------------------------------------------------------------------
     @Test
     public void testSimplexMinimize() {
         SimplexOptimizer optimizer = new SimplexOptimizer(new SimpleValueChecker(1e-12, 1e-12));

         MultivariateFunction func = new MultivariateFunction() {
             public double value(double[] x) { return (x[0]-2)*(x[0]-2) + (x[1]+3)*(x[1]+3); }
         };

         PointValuePair result = optimizer.optimize(
             new ObjectiveFunction(func),
             GoalType.MINIMIZE,
             new InitialGuess(new double[] { 0.0, 0.0 }),
             new NelderMeadSimplex(new double[][] { {1,0}, {0,1} }),
             new MaxEval(1000),
             new MaxIter(1000));

         Assert.assertTrue("value should be small", Math.abs(result.getValue()) < 1e-6);
         double[] pt = result.getPoint();
         Assert.assertEquals(2.0, pt[0], 1e-4);
         Assert.assertEquals(-3.0, pt[1], 1e-4);
         Assert.assertTrue("iterations must be > 0", optimizer.getIterations() > 0);
     }

     @Test
     public void testSimplexMaximize() {
         SimplexOptimizer optimizer = new SimplexOptimizer(new SimpleValueChecker(1e-12, 1e-12));

         // Maximize negative quadratic: f(x) = -(x-5)^2, optimum x=5, value 0
         MultivariateFunction func = new MultivariateFunction() {
             public double value(double[] x) { return -(x[0]-5)*(x[0]-5); }
         };

         PointValuePair result = optimizer.optimize(
             new ObjectiveFunction(func),
             GoalType.MAXIMIZE,
             new InitialGuess(new double[] { 0.0 }),
             new NelderMeadSimpl ex(new double[][] { {1.0} }),
             new MaxEval(1000),
             new MaxIter(1000));

         Assert.assertTrue("value should be near max", Math.abs(result.getValue()) < 1e-6);
         double[] pt = result.getPoint();
         Assert.assertEquals(5.0, pt[0], 1e-4);
         Assert.assertTrue("iterations must be > 0", optimizer.getIterations() > 0);
     }

     // ----- Gauss-Newton ----------------------------------------------------------------
     @Test
     public void testGaussNewtonGetIterations() {
         GaussNewtonOptimizer optimizer = new GaussNewtonOptimizer(new SimpleValueChecker(1e-12,
1e-12));

         // Model y = a*x + b,  observations: ( 0,1), (1,2), (2,3) => like to fit into a=1, b=1
         final double[][] xvals = { {0.0}, {1.0}, {2.0} };
         double[] target = new double[]{1.0, 2., 3.};

         MultivariateVectorFunction odel = new MultivariateVectorFunction() {
             public double[] value(double[] p) {
                 double a = p[0]; double b = p[1];
                 double[] y = new double[ xvals.length];
                 for (int i = 0; i < xvals.length; i++) {
                     y[i] = a * xvals[i][0] + b;
                 }
                 return y;
             }
         };

         MultivariateMatrixFunction jacobian = new MultivariateMatrixFunction() {
             public double[][] value(double[] p) {
                 double[][] J = new double[ xvals.length][2];
                 for (int i = 0; i < xvals.length; i++) {
                     J[i][0] = xvals[i][0]; // df/da = x
                     J[i][1] = 1.0; // df/db = 1
                 }
                 return J;
             }
         };

         PointVectorValuePair result = optimizer.optimize(
             new ModelFunction(od el),
             new ModelFunctionJacobian(jacobian),
             new Target(target),
             new Weight(new double[] {1.0, 1.0, 1.0}),
             new InitialGuess(new double[] {0.0, 0.0}),
             new MaxEval(1000),
             new MaxIter(1000));

         double[] pt = result.getPoint();
         Assert.assertEquals(1.0, pt[0], 1e-6);
         Assert.assertEquals(1.0, pt[1], 1e-6);
         Assert.assertTrue("iterations must be > 0", optimizer.getIterations() > 0);
         Assert.assertTrue("evaluations must be > 0", optimizer.getEvaluations() > 0);
     }

     // ----- Levenberg-Marquardt -------------------------------------------------------
     @Test
     public void testLevenbergMarquardtGetIterations() {
         LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer(
             new SimpleValueChecker(1e-12, 1e-12));

         // same problem as Gauss-Newton
         final double[][] xvals = { {0.0}, {1.0}, {2.0} };
         final double[] target = new double[]{1.0, 2.0, 3.0};

         MultivariateVectorFunction model = new MultivariateVectorFunction() {
             public double[] value(double[] p) {
                 double a = p[0]; double b = p[1];
                 double[] y = new double[xvals.length];
                 for (int i = 0; i < xvals.length; i++) {
                     y[i] = a * xvals[i][0] + b;
                 }
                 return y;
             }
         };

         MultivariateMatrixFunction jacobian = new MultivariateMatrixFunction() {
             public double[][] value(double[] p) {
                 double[][] J = new double[xvals.length][2];
                 for (int i = 0; i < xvals.length; i++) {
                     J[i][0] = xvals[i][0];
                     J[i][1] = 1.0;
                 }
                 return J;
             }
         };

         PointVectorValuePair result = optimizer.optimize(
             new ModelFunction(model),
             new ModelFunctionJacobian(jacobian),
             new Target(target),
             new Weight(new double[]{1.0, 1.0, 1.0}),
             new InitialGuess(new double[]{0.0, 0.0}),
             new MaxEval(1000),
             new MaxIter(1000));

         double[] pt = result.getPoint();
         Assert.assertEquals(1.0, pt[0], 1e-6);
         Assert.assertEquals(1.0, pt[1], 1e-6);
         Assert.assertTrue("iterations must be > 0", optimizer.getIterations() > 0);
         Assert.assertTrue("evaluations must be > 0", optimizer.getEvaluations() > 0);
     }

     // ----- helper ---------------------------------------------------------------
     private static CMAESOptimizer createCMAES() {
         return new CMAESOptimizer(
             10000,          // maxIterations
             1e-12,          // stopFitness
             true,           // isActiveCMA
             0,              // diagonalOnly
             0,              // checkFeasableCount
             new MersenneTwister(123456789L),
             false,          // generateStatistics
             new SimpleValueChecker(1e-12, 1e-12));
     }
 }
