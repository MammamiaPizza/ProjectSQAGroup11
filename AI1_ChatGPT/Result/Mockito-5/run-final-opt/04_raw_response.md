@org.junit.Test
public void shouldUseTheConvenienceConstructorForSuccessfulVerification() {
    final int[] calls = new int[1];
    org.mockito.verification.VerificationMode delegate = new org.mockito.verification.VerificationMode() {
        @Override
        public void verify(org.mockito.internal.verification.api.VerificationData data) {
            calls[0]++;
        }
    };

    new org.mockito.internal.verification.VerificationOverTimeImpl(1L, 1000L, delegate, true).verify(null);

    org.junit.Assert.assertEquals(1, calls[0]);
}

@org.junit.Test
public void shouldImmediatelyPropagateFailuresFromAtMostVerification() {
    final org.mockito.exceptions.base.MockitoAssertionError expected =
            new org.mockito.exceptions.base.MockitoAssertionError("at most failure");
    org.mockito.verification.VerificationMode delegate =
            new org.mockito.internal.verification.AtMost(1) {
                @Override
                public void verify(org.mockito.internal.verification.api.VerificationData data) {
                    throw expected;
                }
            };

    try {
        new org.mockito.internal.verification.VerificationOverTimeImpl(
                1L, 1000L, delegate, true, new org.mockito.internal.util.Timer(1000L)).verify(null);
        org.junit.Assert.fail("Expected the delegate failure to be propagated");
    } catch (org.mockito.exceptions.base.MockitoAssertionError actual) {
        org.junit.Assert.assertSame(expected, actual);
    }
}

@org.junit.Test
public void shouldTreatAtMostAndNoMoreInteractionsAsNonRecoverableFailures() {
    RecoverabilityProbe verification = new RecoverabilityProbe();

    org.junit.Assert.assertFalse(
            verification.canRecover(new org.mockito.internal.verification.AtMost(1)));
    org.junit.Assert.assertFalse(
            verification.canRecover(new org.mockito.internal.verification.NoMoreInteractions()));
}

@org.junit.Test
public void shouldRetryAfterAnInterruptedPollingSleep() {
    final int[] calls = new int[1];
    org.mockito.verification.VerificationMode delegate = new org.mockito.verification.VerificationMode() {
        @Override
        public void verify(org.mockito.internal.verification.api.VerificationData data) {
            if (calls[0]++ == 0) {
                throw new org.mockito.exceptions.base.MockitoAssertionError("first attempt fails");
            }
        }
    };

    boolean wasInterrupted = java.lang.Thread.interrupted();
    try {
        java.lang.Thread.currentThread().interrupt();

        new org.mockito.internal.verification.VerificationOverTimeImpl(
                10L, 1000L, delegate, true, new org.mockito.internal.util.Timer(1000L)).verify(null);

        org.junit.Assert.assertEquals(2, calls[0]);
    } finally {
        java.lang.Thread.interrupted();
        if (wasInterrupted) {
            java.lang.Thread.currentThread().interrupt();
        }
    }
}

private static class RecoverabilityProbe
        extends org.mockito.internal.verification.VerificationOverTimeImpl {

    RecoverabilityProbe() {
        super(1L, 1L, null, true, new org.mockito.internal.util.Timer(1L));
    }

    boolean canRecover(org.mockito.verification.VerificationMode verificationMode) {
        return canRecoverFromFailure(verificationMode);
    }
}