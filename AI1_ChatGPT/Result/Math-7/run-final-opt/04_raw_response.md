@org.junit.Test
public void testResetEventCanScheduleSameHandlerAgainLaterInStep() {
    final int[] occurrences = new int[] { 0 };
    final org.apache.commons.math3.ode.events.EventHandler handler =
        new org.apache.commons.math3.ode.events.EventHandler() {
            private boolean reset;

            public void init(final double t0, final double[] y0, final double t) {
            }

            public double g(final double t, final double[] y) {
                return t - (reset ? 0.75 : 0.25);
            }

            public Action eventOccurred(final double t, final double[] y,
                                        final boolean increasing) {
                ++occurrences[0];
                return occurrences[0] == 1 ? Action.RESET_STATE : Action.CONTINUE;
            }

            public void resetState(final double t, final double[] y) {
                reset = true;
            }
        };

    final org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator integrator =
        new org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator(
            1.0e-6, 1.0, 1.0e-10, 1.0e-10);
    integrator.addEventHandler(handler, 0.1, 1.0e-12, 100);
    final double[] state = new double[] { 0.0 };

    integrator.integrate(new org.apache.commons.math3.ode.FirstOrderDifferentialEquations() {
        public int getDimension() {
            return 1;
        }

        public void computeDerivatives(final double t, final double[] y,
                                       final double[] yDot) {
            yDot[0] = 0.0;
        }
    }, 0.0, state, 1.0, state);

    org.junit.Assert.assertEquals(2, occurrences[0]);
    org.junit.Assert.assertEquals(0.0, state[0], 0.0);
}

@org.junit.Test
public void testStepAndEventHandlerCollectionsCanBeCleared() {
    final org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator integrator =
        new org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator(
            1.0e-6, 1.0, 1.0e-10, 1.0e-10);

    final org.apache.commons.math3.ode.sampling.StepHandler stepHandler =
        new org.apache.commons.math3.ode.sampling.StepHandler() {
            public void init(final double t0, final double[] y0, final double t) {
            }

            public void handleStep(
                    final org.apache.commons.math3.ode.sampling.StepInterpolator interpolator,
                    final boolean isLast) {
            }
        };
    final org.apache.commons.math3.ode.events.EventHandler eventHandler =
        new org.apache.commons.math3.ode.events.EventHandler() {
            public void init(final double t0, final double[] y0, final double t) {
            }

            public double g(final double t, final double[] y) {
                return t - 0.5;
            }

            public Action eventOccurred(final double t, final double[] y,
                                        final boolean increasing) {
                return Action.CONTINUE;
            }

            public void resetState(final double t, final double[] y) {
            }
        };

    integrator.addStepHandler(stepHandler);
    integrator.addEventHandler(eventHandler, 0.1, 1.0e-12, 100);

    org.junit.Assert.assertTrue(integrator.getStepHandlers().contains(stepHandler));
    org.junit.Assert.assertTrue(integrator.getEventHandlers().contains(eventHandler));

    integrator.clearStepHandlers();
    integrator.clearEventHandlers();

    org.junit.Assert.assertTrue(integrator.getStepHandlers().isEmpty());
    org.junit.Assert.assertTrue(integrator.getEventHandlers().isEmpty());
}

@org.junit.Test
public void testEvaluationCountAndMaximumAreReported() {
    final org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator integrator =
        new org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator(
            1.0e-6, 1.0, 1.0e-10, 1.0e-10);
    integrator.setMaxEvaluations(1000);
    final double[] state = new double[] { 0.0 };

    integrator.integrate(new org.apache.commons.math3.ode.FirstOrderDifferentialEquations() {
        public int getDimension() {
            return 1;
        }

        public void computeDerivatives(final double t, final double[] y,
                                       final double[] yDot) {
            yDot[0] = 1.0;
        }
    }, 0.0, state, 1.0, state);

    org.junit.Assert.assertEquals(1000, integrator.getMaxEvaluations());
    org.junit.Assert.assertTrue(integrator.getEvaluations() > 0);
    org.junit.Assert.assertEquals(1.0, state[0], 1.0e-12);
}

@org.junit.Test(expected = org.apache.commons.math3.exception.NumberIsTooSmallException.class)
public void testIntegrationIntervalMustBeLargerThanRoundoffThreshold() {
    final org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator integrator =
        new org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator(
            1.0e-6, 1.0, 1.0e-10, 1.0e-10);
    final double[] state = new double[] { 0.0 };

    integrator.integrate(new org.apache.commons.math3.ode.FirstOrderDifferentialEquations() {
        public int getDimension() {
            return 1;
        }

        public void computeDerivatives(final double t, final double[] y,
                                       final double[] yDot) {
            yDot[0] = 0.0;
        }
    }, 1.0, state, 1.0, state);
}