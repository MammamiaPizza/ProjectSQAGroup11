@Test
public void recheckingStepTruncatedAtPendingEventAllowsAcceptance() throws Exception {
    org.apache.commons.math.ode.events.EventHandler handler =
        new org.apache.commons.math.ode.events.EventHandler() {
            public double g(final double t, final double[] y) {
                return t - 1.0;
            }

            public int eventOccurred(final double t, final double[] y,
                                     final boolean increasing) {
                return org.apache.commons.math.ode.events.EventHandler.CONTINUE;
            }

            public void resetState(final double t, final double[] y) {
            }
        };

    EventState state = new EventState(handler, 10.0, 1.0e-8, 100);
    state.reinitializeBegin(0.0, new double[] { 0.0 });

    assertTrue(state.evaluateStep(new LinearTimeInterpolator(0.0, 2.0, true)));
    assertEquals(1.0, state.getEventTime(), 1.0e-8);

    assertFalse(state.evaluateStep(new LinearTimeInterpolator(0.0, 1.0, true)));
    assertEquals(1.0, state.getEventTime(), 1.0e-8);
}

@Test
public void acceptedEventAtStepStartIsNotReportedAgain() throws Exception {
    org.apache.commons.math.ode.events.EventHandler handler =
        new org.apache.commons.math.ode.events.EventHandler() {
            public double g(final double t, final double[] y) {
                return t - 1.0;
            }

            public int eventOccurred(final double t, final double[] y,
                                     final boolean increasing) {
                return org.apache.commons.math.ode.events.EventHandler.CONTINUE;
            }

            public void resetState(final double t, final double[] y) {
            }
        };

    EventState state = new EventState(handler, 10.0, 1.0e-8, 100);
    state.reinitializeBegin(0.0, new double[] { 0.0 });

    assertTrue(state.evaluateStep(new LinearTimeInterpolator(0.0, 2.0, true)));
    state.stepAccepted(1.0, new double[] { 1.0 });

    assertFalse(state.evaluateStep(new LinearTimeInterpolator(1.0, 2.0, true)));
}

@Test
public void continueActionDoesNotResetState() throws Exception {
    final int[] resetCalls = new int[] { 0 };
    org.apache.commons.math.ode.events.EventHandler handler =
        new org.apache.commons.math.ode.events.EventHandler() {
            public double g(final double t, final double[] y) {
                return t - 1.0;
            }

            public int eventOccurred(final double t, final double[] y,
                                     final boolean increasing) {
                return org.apache.commons.math.ode.events.EventHandler.CONTINUE;
            }

            public void resetState(final double t, final double[] y) {
                ++resetCalls[0];
            }
        };

    EventState state = new EventState(handler, 10.0, 1.0e-8, 100);
    state.reinitializeBegin(0.0, new double[] { 0.0 });

    assertTrue(state.evaluateStep(new LinearTimeInterpolator(0.0, 2.0, true)));
    state.stepAccepted(1.0, new double[] { 1.0 });

    assertFalse(state.reset(1.0, new double[] { 1.0 }));
    assertEquals(0, resetCalls[0]);
    assertFalse(state.stop());
}