import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.NonSquareMatrixException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.SimpleVectorValueChecker;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunction;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunctionJacobian;
import org.apache.commons.math3.optim.nonlinear.vector.Target;
import org.apache.commons.math3.optim.nonlinear.vector.Weight;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.GaussNewtonOptimizer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Math14WeightTest {

    @Test
    public void testArrayWeightCreatesDiagonalMatrixWithAllEntries() {
        Weight weight = new Weight(new double[] { 2.5, 0.0, 7.25 });

        RealMatrix matrix = weight.getWeight();
        assertEquals(3, matrix.getRowDimension());
        assertEquals(3, matrix.getColumnDimension());
        assertEquals(2.5, matrix.getEntry(0, 0), 0.0);
        assertEquals(0.0, matrix.getEntry(1, 1), 0.0);
        assertEquals(7.25, matrix.getEntry(2, 2), 0.0);
        assertEquals(0.0, matrix.getEntry(0, 2), 0.0);
        assertEquals(0.0, matrix.getEntry(2, 1), 0.0);
    }

    @Test
    public void testMatrixWeightIsCopiedAndGetWeightReturnsCopy() {
        RealMatrix source = MatrixUtils.createRealMatrix(new double[][] {
            { 2.0, 0.5 },
            { 0.5, 3.0 }
        });
        Weight weight = new Weight(source);

        source.setEntry(0, 0, 99.0);
        RealMatrix firstResult = weight.getWeight();
        assertEquals(2.0, firstResult.getEntry(0, 0), 0.0);
        assertEquals(0.5, firstResult.getEntry(0, 1), 0.0);
        assertEquals(3.0, firstResult.getEntry(1, 1), 0.0);

        firstResult.setEntry(1, 1, 77.0);
        assertEquals(3.0, weight.getWeight().getEntry(1, 1), 0.0);
    }

    @Test(expected = NonSquareMatrixException.class)
    public void testMatrixWeightRejectsNonSquareMatrix() {
        new Weight(MatrixUtils.createRealMatrix(2, 3));
    }

    @Test
    public void testOptimizerUsesWeightAsLeastSquaresWeight() {
        GaussNewtonOptimizer optimizer = new GaussNewtonOptimizer(
            false, new SimpleVectorValueChecker(1e-12, 1e-12));

        PointVectorValuePair result = optimizer.optimize(
            new MaxEval(10),
            new MaxIter(10),
            new InitialGuess(new double[] { 0.0 }),
            new Target(new double[] { 2.0, 0.0 }),
            new Weight(new double[] { 4.0, 1.0 }),
            new ModelFunction(new MultivariateVectorFunction() {
                public double[] value(double[] point) {
                    return new double[] { point[0], 2.0 * point[0] };
                }
            }),
            new ModelFunctionJacobian(new MultivariateMatrixFunction() {
                public double[][] value(double[] point) {
                    return new double[][] { { 1.0 }, { 2.0 } };
                }
            })
        );

        assertEquals(1.0, result.getPoint()[0], 1e-12);
    }

    @Test
    public void testLargeDiagonalWeightCanBeUsedByLeastSquaresOptimizer() {
        final int sampleSize = 20000;
        final double[] target = new double[sampleSize];
        final double[] weights = new double[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            target[i] = 2.0;
            weights[i] = 1.0;
        }

        GaussNewtonOptimizer optimizer = new GaussNewtonOptimizer(
            false, new SimpleVectorValueChecker(1e-12, 1e-12));

        PointVectorValuePair result = optimizer.optimize(
            new MaxEval(10),
            new MaxIter(10),
            new InitialGuess(new double[] { 0.0 }),
            new Target(target),
            new Weight(weights),
            new ModelFunction(new MultivariateVectorFunction() {
                public double[] value(double[] point) {
                    double[] values = new double[sampleSize];
                    for (int i = 0; i < sampleSize; i++) {
                        values[i] = point[0];
                    }
                    return values;
                }
            }),
            new ModelFunctionJacobian(new MultivariateMatrixFunction() {
                public double[][] value(double[] point) {
                    double[][] jacobian = new double[sampleSize][1];
                    for (int i = 0; i < sampleSize; i++) {
                        jacobian[i][0] = 1.0;
                    }
                    return jacobian;
                }
            })
        );

        assertEquals(2.0, result.getPoint()[0], 1e-12);
    }
}
