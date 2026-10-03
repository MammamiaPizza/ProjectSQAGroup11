@Test
public void testJacobianDimensionMismatchIsRejected() throws Exception {
    ProbeOptimizer optimizer = new ProbeOptimizer(true);
    try {
        optimizer.optimize(new FixedFunction(new double[] { 0.0 }, new double[0][1]),
                           new double[] { 0.0 },
                           new double[] { 1.0 },
                           new double[] { 0.0 });
        org.junit.Assert.fail("expected FunctionEvaluationException");
    } catch (org.apache.commons.math.FunctionEvaluationException expected) {
        org.junit.Assert.assertEquals(1, optimizer.getJacobianEvaluations());
    }
}

@Test
public void testObjectiveDimensionMismatchIsRejected() throws Exception {
    ProbeOptimizer optimizer = new ProbeOptimizer(false);
    try {
        optimizer.optimize(new FixedFunction(new double[0], new double[][] { { 0.0 } }),
                           new double[] { 0.0 },
                           new double[] { 1.0 },
                           new double[] { 0.0 });
        org.junit.Assert.fail("expected FunctionEvaluationException");
    } catch (org.apache.commons.math.FunctionEvaluationException expected) {
        org.junit.Assert.assertEquals(1, optimizer.getEvaluations());
    }
}

@Test
public void testCovariancesRejectSingularJacobian() throws Exception {
    ProbeOptimizer optimizer = new ProbeOptimizer(false);
    optimizer.setJacobianForCovariance(new double[][] { { 0.0 } });
    try {
        optimizer.getCovariances();
        org.junit.Assert.fail("expected OptimizationException");
    } catch (org.apache.commons.math.optimization.OptimizationException expected) {
        // expected
    }
}

@Test
public void testIterationCounterAllowsIterationAtConfiguredLimit() throws Exception {
    ProbeOptimizer optimizer = new ProbeOptimizer(false);
    optimizer.setMaxIterations(1);

    optimizer.incrementIterationForTest();

    org.junit.Assert.assertEquals(1, optimizer.getIterations());
}

private static final class ProbeOptimizer
    extends org.apache.commons.math.optimization.general.AbstractLeastSquaresOptimizer {

    private final boolean updateJacobian;

    ProbeOptimizer(boolean updateJacobian) {
        this.updateJacobian = updateJacobian;
    }

    void setJacobianForCovariance(double[][] value) {
        jacobian = value;
        rows = value.length;
        cols = value[0].length;
    }

    void incrementIterationForTest()
        throws org.apache.commons.math.optimization.OptimizationException {
        incrementIterationsCounter();
    }

    protected org.apache.commons.math.optimization.VectorialPointValuePair doOptimize()
        throws org.apache.commons.math.FunctionEvaluationException,
               org.apache.commons.math.optimization.OptimizationException {
        if (updateJacobian) {
            updateJacobian();
        } else {
            updateResidualsAndCost();
        }
        return null;
    }
}

private static final class FixedFunction
    implements org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction {

    private final double[] values;
    private final double[][] derivatives;

    FixedFunction(double[] values, double[][] derivatives) {
        this.values = values;
        this.derivatives = derivatives;
    }

    public double[] value(double[] point) {
        return values;
    }

    public org.apache.commons.math.analysis.MultivariateMatrixFunction jacobian() {
        return new org.apache.commons.math.analysis.MultivariateMatrixFunction() {
            public double[][] value(double[] point) {
                return derivatives;
            }
        };
    }
}