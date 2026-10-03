package org.apache.commons.math.ode.nonstiff;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math.ode.ExpandableStatefulODE;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.junit.Test;

public class EmbeddedRungeKuttaIntegratorTest {

    private static final class UnitRateEquation implements FirstOrderDifferentialEquations {
        public int getDimension() {
            return 1;
        }

        public void computeDerivatives(double t, double[] y, double[] yDot) {
            yDot[0] = 1.0;
        }
    }

    private ExpandableStatefulODE equation(double time, double state) {
        ExpandableStatefulODE equations = new ExpandableStatefulODE(new UnitRateEquation());
        equations.setTime(time);
        equations.setPrimaryState(new double[] { state });
        return equations;
    }

    private DormandPrince853Integrator integratorWithLargeFirstStep() {
        DormandPrince853Integrator integrator =
                new DormandPrince853Integrator(0.1, 0.5, 1.0e-12, 1.0e-12);
        integrator.setInitialStepSize(0.5);
        integrator.setMinReduction(0.001);
        integrator.setMaxGrowth(1.0);
        return integrator;
    }

    @Test
    public void testTooLargeFirstStepIsTrimmedToForwardTarget() {
        ExpandableStatefulODE equations = equation(0.0, 0.0);

        integratorWithLargeFirstStep().integrate(equations, 0.51);

        assertEquals(0.51, equations.getTime(), 1.0e-12);
        assertEquals(0.51, equations.getCompleteState()[0], 1.0e-12);
    }

    @Test
    public void testTooLargeFirstStepIsTrimmedToBackwardTarget() {
        ExpandableStatefulODE equations = equation(1.0, 1.0);

        integratorWithLargeFirstStep().integrate(equations, 0.49);

        assertEquals(0.49, equations.getTime(), 1.0e-12);
        assertEquals(0.49, equations.getCompleteState()[0], 1.0e-12);
    }

    @Test
    public void testTargetAtEndOfInitialStepIsReachedExactly() {
        ExpandableStatefulODE equations = equation(0.0, 0.0);

        integratorWithLargeFirstStep().integrate(equations, 0.5);

        assertEquals(0.5, equations.getTime(), 1.0e-12);
        assertEquals(0.5, equations.getCompleteState()[0], 1.0e-12);
    }

    @Test
    public void testNormalForwardIntegrationReachesRequestedTimeAndState() {
        DormandPrince853Integrator integrator =
                new DormandPrince853Integrator(1.0e-8, 1.0, 1.0e-12, 1.0e-12);
        ExpandableStatefulODE equations = equation(0.0, 0.0);

        integrator.integrate(equations, 0.75);

        assertEquals(0.75, equations.getTime(), 1.0e-12);
        assertEquals(0.75, equations.getCompleteState()[0], 1.0e-12);
    }

@org.junit.Test
public void testStepSizeControlParametersHaveDocumentedDefaultsAndCanBeUpdated() {
    final org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator integrator =
        new org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator(
            1.0e-6, 1.0, 1.0e-10, 1.0e-10);

    org.junit.Assert.assertEquals(0.9, integrator.getSafety(), 0.0);
    org.junit.Assert.assertEquals(0.2, integrator.getMinReduction(), 0.0);
    org.junit.Assert.assertEquals(10.0, integrator.getMaxGrowth(), 0.0);

    integrator.setSafety(0.75);
    integrator.setMinReduction(0.35);
    integrator.setMaxGrowth(4.5);

    org.junit.Assert.assertEquals(0.75, integrator.getSafety(), 0.0);
    org.junit.Assert.assertEquals(0.35, integrator.getMinReduction(), 0.0);
    org.junit.Assert.assertEquals(4.5, integrator.getMaxGrowth(), 0.0);
}
}
