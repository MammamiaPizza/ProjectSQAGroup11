package org.apache.commons.math.estimation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AbstractEstimatorTest {

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testUpdateJacobianUsesOnlyUnboundParametersAndAppliesWeights() {
        ProblemData data = createThreeParameterProblem();
        ProbeEstimator estimator = new ProbeEstimator();

        estimator.prepare(data.problem);
        estimator.updateJacobianForTest();

        double[] jacobian = estimator.getJacobianForTest();
        assertEquals(4, jacobian.length);
        assertEquals(-2.0, jacobian[0], 0.0);
        assertEquals(0.0, jacobian[1], 0.0);
        assertEquals(0.0, jacobian[2], 0.0);
        assertEquals(-6.0, jacobian[3], 0.0);
        assertEquals(1, estimator.getJacobianEvaluations());
    }

    @Test
    public void testCovariancesUseOnlyUnboundParametersWhenAParameterIsBound()
        throws EstimationException {
        ProblemData data = createThreeParameterProblem();
        ProbeEstimator estimator = new ProbeEstimator();

        estimator.prepare(data.problem);
        double[][] covariances = estimator.getCovariances(data.problem);

        assertNotNull(covariances);
        assertEquals(2, covariances.length);
        assertEquals(2, covariances[0].length);
        assertEquals(2.0 / 3.0, covariances[0][0], 1.0e-12);
        assertEquals(-1.0 / 3.0, covariances[0][1], 1.0e-12);
        assertEquals(-1.0 / 3.0, covariances[1][0], 1.0e-12);
        assertEquals(2.0 / 3.0, covariances[1][1], 1.0e-12);
        assertEquals(1, estimator.getJacobianEvaluations());
    }

    @Test
    public void testGuessParameterErrorsExcludesBoundParameters() throws EstimationException {
        EstimatedParameter fixed = new EstimatedParameter("fixed", 0.0);
        fixed.setBound(true);
        EstimatedParameter x = new EstimatedParameter("x", 0.0);
        EstimatedParameter y = new EstimatedParameter("y", 0.0);

        WeightedMeasurement[] measurements = new WeightedMeasurement[] {
            new LinearMeasurement(1.0, 1.0, x, 1.0, y, 0.0),
            new LinearMeasurement(1.0, 2.0, x, 0.0, y, 1.0),
            new LinearMeasurement(1.0, 2.0, x, 1.0, y, 1.0),
            new LinearMeasurement(1.0, 1.0, x, 1.0, y, -1.0)
        };
        SimpleProblem problem = new SimpleProblem(
            new EstimatedParameter[] { fixed, x, y }, measurements);
        ProbeEstimator estimator = new ProbeEstimator();

        estimator.prepare(problem);
        double[] errors = estimator.guessParametersErrors(problem);

        assertEquals(2, errors.length);
        assertEquals(Math.sqrt(5.0 / 3.0), errors[0], 1.0e-12);
        assertEquals(Math.sqrt(5.0 / 3.0), errors[1], 1.0e-12);
    }

    @Test
    public void testResidualEvaluationHonorsMaximumCostEvaluationLimit()
        throws EstimationException {
        EstimatedParameter x = new EstimatedParameter("x", 0.0);
        SimpleProblem problem = new SimpleProblem(
            new EstimatedParameter[] { x },
            new WeightedMeasurement[] {
                new LinearMeasurement(4.0, 3.0, x, 1.0, null, 0.0)
            });
        ProbeEstimator estimator = new ProbeEstimator();

        estimator.prepare(problem);
        estimator.setMaxCostEval(1);
        estimator.updateResidualsForTest();

        assertEquals(1, estimator.getCostEvaluations());
        assertEquals(6.0, estimator.getResidualsForTest()[0], 0.0);
        assertEquals(6.0, estimator.getCostForTest(), 0.0);

        try {
            estimator.updateResidualsForTest();
        } catch (EstimationException expected) {
            assertEquals(2, estimator.getCostEvaluations());
            return;
        }

        throw new AssertionError("Expected an EstimationException after exceeding maxCostEval");
    }

    private static ProblemData createThreeParameterProblem() {
        EstimatedParameter fixed = new EstimatedParameter("fixed", 0.0);
        fixed.setBound(true);
        EstimatedParameter x = new EstimatedParameter("x", 0.0);
        EstimatedParameter y = new EstimatedParameter("y", 0.0);

        WeightedMeasurement[] measurements = new WeightedMeasurement[] {
            new LinearMeasurement(1.0, 0.0, x, 1.0, y, 0.0),
            new LinearMeasurement(1.0, 0.0, x, 0.0, y, 1.0),
            new LinearMeasurement(1.0, 0.0, x, 1.0, y, 1.0)
        };
        return new ProblemData(new SimpleProblem(
            new EstimatedParameter[] { fixed, x, y }, measurements));
    }

    private static final class ProblemData {
        private final SimpleProblem problem;

        private ProblemData(SimpleProblem problem) {
            this.problem = problem;
        }
    }

    private static final class SimpleProblem implements EstimationProblem {
        private final EstimatedParameter[] allParameters;
        private final WeightedMeasurement[] measurements;

        private SimpleProblem(EstimatedParameter[] allParameters,
                              WeightedMeasurement[] measurements) {
            this.allParameters = allParameters;
            this.measurements = measurements;
        }

        public EstimatedParameter[] getAllParameters() {
            return allParameters;
        }

        public EstimatedParameter[] getUnboundParameters() {
            int count = 0;
            for (int i = 0; i < allParameters.length; i++) {
                if (!allParameters[i].isBound()) {
                    count++;
                }
            }

            EstimatedParameter[] unbound = new EstimatedParameter[count];
            int index = 0;
            for (int i = 0; i < allParameters.length; i++) {
                if (!allParameters[i].isBound()) {
                    unbound[index++] = allParameters[i];
                }
            }
            return unbound;
        }

        public WeightedMeasurement[] getMeasurements() {
            return measurements;
        }
    }

    private static final class LinearMeasurement extends WeightedMeasurement {
        private final EstimatedParameter first;
        private final double firstCoefficient;
        private final EstimatedParameter second;
        private final double secondCoefficient;

        private LinearMeasurement(double weight, double measuredValue,
                                  EstimatedParameter first, double firstCoefficient,
                                  EstimatedParameter second, double secondCoefficient) {
            super(weight, measuredValue);
            this.first = first;
            this.firstCoefficient = firstCoefficient;
            this.second = second;
            this.secondCoefficient = secondCoefficient;
        }

        public double getTheoreticalValue() {
            double value = firstCoefficient * first.getEstimate();
            if (second != null) {
                value += secondCoefficient * second.getEstimate();
            }
            return value;
        }

        public double getPartial(EstimatedParameter parameter) {
            if (parameter == first) {
                return firstCoefficient;
            }
            if (parameter == second) {
                return secondCoefficient;
            }
            throw new AssertionError("A bound parameter must not be used as a Jacobian column");
        }
    }

    private static final class ProbeEstimator extends AbstractEstimator {
        public void estimate(EstimationProblem problem) throws EstimationException {
            initializeEstimate(problem);
        }

        private void prepare(EstimationProblem problem) {
            initializeEstimate(problem);
        }

        private void updateJacobianForTest() {
            updateJacobian();
        }

        private void updateResidualsForTest() throws EstimationException {
            updateResidualsAndCost();
        }

        private double[] getJacobianForTest() {
            return jacobian.clone();
        }

        private double[] getResidualsForTest() {
            return residuals.clone();
        }

        private double getCostForTest() {
            return cost;
        }
    }
}