@Test
public void testVectorTolerancesResetStepHandlers() throws Exception {
    final int[] resetCalls = new int[] { 0 };
    final org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator integrator =
        new org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator(
            1.0e-8, 1.0, new double[] { 1.0e-10 }, new double[] { 1.0e-10 });

    integrator.addStepHandler(new org.apache.commons.math.ode.sampling.StepHandler() {
        public void reset() {
            ++resetCalls[0];
        }

        public void handleStep(final org.apache.commons.math.ode.sampling.StepInterpolator interpolator,
                               final boolean isLast) {
        }
    });

    final double[] state = new double[] { 0.0 };
    final double stop = integrator.integrate(new org.apache.commons.math.ode.FirstOrderDifferentialEquations() {
        public int getDimension() {
            return 1;
        }

        public void computeDerivatives(final double t, final double[] y, final double[] yDot) {
            yDot[0] = 1.0;
        }
    }, 0.0, state, 1.0, state);

    org.junit.Assert.assertEquals(1, resetCalls[0]);
    org.junit.Assert.assertEquals(1.0, stop, 0.0);
    org.junit.Assert.assertEquals(1.0, state[0], 1.0e-10);
}

@Test
public void testAdaptiveIntegrationOfHighOrderPolynomial() throws Exception {
    final org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator integrator =
        new org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator(
            1.0e-12, 1.0, 1.0e-12, 1.0e-12);
    final double[] state = new double[] { 0.0 };

    final double stop = integrator.integrate(new org.apache.commons.math.ode.FirstOrderDifferentialEquations() {
        public int getDimension() {
            return 1;
        }

        public void computeDerivatives(final double t, final double[] y, final double[] yDot) {
            yDot[0] = t * t * t * t * t;
        }
    }, 0.0, state, 1.0, state);

    org.junit.Assert.assertEquals(1.0, stop, 0.0);
    org.junit.Assert.assertEquals(1.0 / 6.0, state[0], 1.0e-10);
}